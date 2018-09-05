package org.edec.register.service.impl;

import org.edec.model.GroupModel;
import org.edec.register.manager.RetakeManager;
import org.edec.register.model.RetakeSubjectModel;
import org.edec.register.service.RetakeService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class RetakeImpl implements RetakeService {
    private RetakeManager retakeManager = new RetakeManager();

    @Override
    public List<GroupModel> getGroupsByFilter (String qualification, Integer course, Long idSem, String groupname) {
        return retakeManager.getGroupsByFilter(qualification, course, idSem, groupname);
    }

    @Override
    public List<RetakeSubjectModel> getRetakeSubjects (Long idSem, String listOfIdGroup, String filter) {
        return devideSubjectsByFOC(retakeManager.getSubjectsByFitler(idSem, listOfIdGroup, filter));
    }

    @Override
    public boolean openRetake (Long idLGSS, Integer foc, Date dateOfBegin, Date dateOfEnd, Integer vedomType) {
        return retakeManager.openRetake(idLGSS, foc, dateOfBegin, dateOfEnd, vedomType);
    }

    private List<RetakeSubjectModel> devideSubjectsByFOC(List<RetakeSubjectModel> subjects) {
        List<RetakeSubjectModel> result = new ArrayList<>();

        for (RetakeSubjectModel subject : subjects) {
            if (subject.getExam()) {
                RetakeSubjectModel tmpSubject = createTmpSubject(subject);
                tmpSubject.setFoc("Экзамен");
                tmpSubject.setFocInt(1);
                result.add(tmpSubject);
            }
            if (subject.getPass()) {
                RetakeSubjectModel tmpSubject = createTmpSubject(subject);
                tmpSubject.setFoc("Зачет");
                tmpSubject.setFocInt(2);
                result.add(tmpSubject);
            }
            if (subject.getCp()) {
                RetakeSubjectModel tmpSubject = createTmpSubject(subject);
                tmpSubject.setFoc("КП");
                tmpSubject.setFocInt(3);
                result.add(tmpSubject);
            }
            if (subject.getCw()) {
                RetakeSubjectModel tmpSubject = createTmpSubject(subject);
                tmpSubject.setFoc("КР");
                tmpSubject.setFocInt(4);
                result.add(tmpSubject);
            }
            if (subject.getPractic()) {
                RetakeSubjectModel tmpSubject = createTmpSubject(subject);
                tmpSubject.setFoc("Практика");
                tmpSubject.setFocInt(5);
                result.add(tmpSubject);
            }
        }

        return result;
    }

    private RetakeSubjectModel createTmpSubject(RetakeSubjectModel subject) {
        RetakeSubjectModel tmpSubject = new RetakeSubjectModel();
        tmpSubject.setGroupname(subject.getGroupname());
        tmpSubject.setSubjectname(subject.getSubjectname());
        tmpSubject.setIdSubj(subject.getIdSubj());
        tmpSubject.setIdLGSS(subject.getIdLGSS());
        tmpSubject.setTeachers(subject.getTeachers());
        return tmpSubject;
    }
}
