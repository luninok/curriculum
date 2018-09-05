package org.edec.commission.service.impl;

import org.edec.commission.manager.EntityManagerCommission;
import org.edec.commission.model.*;
import org.edec.commission.service.CommissionService;
import org.edec.manager.EntityManagerSemesterESO;
import org.edec.model.SemesterModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class CommissionServiceESOimpl implements CommissionService {
    private EntityManagerCommission emCommission = new EntityManagerCommission();
    private EntityManagerSemesterESO emSemesterESO = new EntityManagerSemesterESO();

    @Override
    public List<StudentCountDebtModel> getListStudentCountDebt (String qualification, Integer course, Integer debtCount, String typeDebt,
                                                                Integer government, String listIdSem, boolean prolongation,
                                                                Date dateProlongation, Integer formofstudy, Long idInst) {
        List<StudentCountDebtModel> listStudentCountDebt = emCommission.getStudentCountDebt(
                qualification, course, debtCount, typeDebt, government, listIdSem, prolongation, dateProlongation, formofstudy, idInst);
        return listStudentCountDebt;
    }

    @Override
    public List<StudentDebtModel> getDevidedByFocStudentsDebt (Long idSc, Long idDg, String listIdSem, Integer formOfStudy) {
        List<StudentDebtModel> listStudentDebt = emCommission.getStudentDebt(idSc, idDg, listIdSem, formOfStudy);
        listStudentDebt = getDevidedByFoc(listStudentDebt, false);
        return listStudentDebt;
    }

    @Override
    public List<StudentDebtModel> getStudentByRegisterCommission (Long idRegComm) {
        return emCommission.getStudentByIdRegCommission(idRegComm);
    }

    @Override
    public List<StudentCountDebtModel> getStudentCountDebtByFio (String fio, List<StudentCountDebtModel> tempList) {
        List<StudentCountDebtModel> result = new ArrayList<>();
        for (StudentCountDebtModel tempModel : tempList) {
            if (tempModel.getFio().toLowerCase().contains(fio.toLowerCase())) {
                result.add(tempModel);
            }
        }
        return result;
    }

    @Override
    public List<StudentCountDebtModel> getStudentCountDebtByGroup (String group, List<StudentCountDebtModel> tempList) {
        List<StudentCountDebtModel> result = new ArrayList<>();
        for (StudentCountDebtModel tempModel : tempList) {
            if (tempModel.getGroupname().toLowerCase().contains(group.toLowerCase())) {
                result.add(tempModel);
            }
        }
        return result;
    }

    @Override
    public List<SubjectDebtModel> getSubjectCommissionByFilterAndSem (String fioGroupSubject, Long idSem, PeriodCommissionModel period,
                                                                      Integer formOfStudy) {
        return emCommission.getSubjectCommissionByFilterAndSem(fioGroupSubject, idSem, period, formOfStudy);
    }

    @Override
    public List<SubjectDebtModel> getSubjForCreateCommission (String listIdSSS, Date dateBegin, Date dateEnd) {
        List<SubjectDebtModel> result = new ArrayList<>();

        List<StudentDebtModel> listStudent = emCommission.getStudentDebtByListSSSandDateCommission(listIdSSS, dateEnd);
        listStudent = getDevidedByFoc(listStudent, true);

        for (StudentDebtModel student : listStudent) {

            SubjectDebtModel subject = result.stream()
                                             .filter(subjectDebtModel -> subjectDebtModel.getIdChair().equals(student.getIdChair()) &&
                                                                         subjectDebtModel.getSemesternumber()
                                                                                         .equals(student.getSemesternumber()) &&
                                                                         subjectDebtModel.getFocStr().equals(student.getFocStr()) &&
                                                                         subjectDebtModel.getSemesterStr()
                                                                                         .equals(student.getSemesterStr()) &&
                                                                         subjectDebtModel.getSubjectname().equals(student.getSubjectname()))
                                             .findFirst()
                                             .orElse(null);

            if (subject == null) {
                subject = new SubjectDebtModel();
                subject.setIdSemester(student.getIdSemester());
                subject.setSemesterStr(student.getSemesterStr());
                subject.setSemesternumber(student.getSemesternumber());
                subject.setSubjectname(student.getSubjectname());
                subject.setDateofbegincomission(dateBegin);
                subject.setDateofendcomission(dateEnd);
                subject.setFocStr(student.getFocStr());
                subject.setFulltitle(student.getFulltitle());
                subject.setIdChair(student.getIdChair());
                subject.setIdSubj(student.getIdSubj());
                result.add(subject);
            }

            subject.getStudents().add(student);
        }

        Collections.sort(result);
        return result;
    }

    @Override
    public List<SubjectDebtModel> getSubjectsByFilter (String filter, List<SubjectDebtModel> allSubjects) {
        List<SubjectDebtModel> result = new ArrayList<>();
        for (SubjectDebtModel subj : allSubjects) {
            if (subj.getSubjectname().toLowerCase().contains(filter.toLowerCase())) {
                result.add(subj);
                continue;
            }
            StudentDebtModel student = subj.getStudents()
                                           .stream()
                                           .filter(studentModel -> studentModel.getFio().toLowerCase().contains(filter.toLowerCase()) ||
                                                                   studentModel.getGroupname().toLowerCase().contains(filter.toLowerCase()))
                                           .findAny()
                                           .orElse(null);
            if (student != null) {
                result.add(subj);
            }
        }
        return result;
    }

    @Override
    public List<SemesterModel> getAllSemesterWithCommission (Integer formOfStudy) {
        return emCommission.getSemesterCommission(formOfStudy);
    }

    @Override
    public List<SemesterModel> getSemesterByInstAndFOS (Long idInst, Integer fos) {
        return emSemesterESO.getSemesters(idInst, fos, 0);
    }

    @Override
    public List<CommissionStructureModel> getCommissionStructure (Long idCommission) {
        return emCommission.getCommissionStructure(idCommission);
    }

    @Override
    public List<PeriodCommissionModel> getPeriodCommission (Integer formOfStudy) {
        return emCommission.getPeriodCommission(formOfStudy);
    }

    @Override
    public boolean createIndividualCommission (StudentDebtModel studentDebtModel, Date dateBegin, Date dateEnd, Long idCurrentUser) {

        return emCommission.createIndividualCommission(studentDebtModel, dateBegin, dateEnd, idCurrentUser);
    }

    @Override
    public boolean createCommonCommission (SubjectDebtModel subjectDebtModel, List<StudentDebtModel> students, Long idCurrentUser) {
        return emCommission.createCommonCommission(subjectDebtModel, students, idCurrentUser);
    }

    @Override
    public boolean deleteCommission (Long idRegComm) {
        return emCommission.deleteComission(idRegComm);
    }

    @Override
    public boolean deleteSRHfromCommRegister (String listIDsrh) {
        return emCommission.deleteSRHfromComissionRegister(listIDsrh);
    }

    @Override
    public boolean updateCommissionRegister (Long idRegComm, Date dateBegin, Date dateEnd) {
        return emCommission.updateRegisterComission(idRegComm, dateBegin, dateEnd);
    }

    @Override
    public boolean setCheckKutsSrh (Long idSrh, boolean status) {
        return emCommission.setCheckKutsForSRH(idSrh, status);
    }

    @Override
    public void setStatusSignedForSubjAndStudent (SubjectDebtModel subject) {
        subject.setSigned(true);
        for (StudentDebtModel student : subject.getStudents()) {
            student.setOpenComm(true);
        }
    }

    /**
     * Делит модель на формы контроля
     *
     * @param tempListStudentDebt - модель
     * @param openComm            - если true, то нужно чтобы не было комиссий у студента по любому из предметов, иначе показываем все долги
     * @return
     */
    public List<StudentDebtModel> getDevidedByFoc (List<StudentDebtModel> tempListStudentDebt, boolean openComm) {
        List<StudentDebtModel> result = new ArrayList<>();

        List<StudentDebtModel> examList = new ArrayList<>();
        List<StudentDebtModel> passList = new ArrayList<>();
        List<StudentDebtModel> cpList = new ArrayList<>();
        List<StudentDebtModel> cwList = new ArrayList<>();
        List<StudentDebtModel> practicList = new ArrayList<>();

        for (StudentDebtModel model : tempListStudentDebt) {
            if (model.getExam() && model.getExamrating() < 3 && (!openComm || !model.getExamComm())) {
                StudentDebtModel newModel = createFOCmodel(model);
                newModel.setFocStr("Экзамен");
                newModel.setOpenComm(model.getExam());
                examList.add(newModel);
            }
            if (model.getPass() && model.getPassrating() != 1 && (!openComm || !model.getPassComm())) {
                StudentDebtModel newModel = createFOCmodel(model);
                newModel.setFocStr("Зачет");
                newModel.setOpenComm(model.getPass());
                passList.add(newModel);
            }
            if (model.getCp() && model.getCprating() < 3 && (!openComm || !model.getCpComm())) {
                StudentDebtModel newModel = createFOCmodel(model);
                newModel.setFocStr("КП");
                newModel.setOpenComm(model.getCp());
                cpList.add(newModel);
            }
            if (model.getCw() && model.getCwrating() < 3 && (!openComm || !model.getCwComm())) {
                StudentDebtModel newModel = createFOCmodel(model);
                newModel.setFocStr("КР");
                newModel.setOpenComm(model.getCw());
                cwList.add(newModel);
            }
            if (model.getPractic() && model.getPracticrating() < 3 && (!openComm || !model.getPracticComm())) {
                StudentDebtModel newModel = createFOCmodel(model);
                newModel.setFocStr("Практика");
                newModel.setOpenComm(model.getPractic());
                practicList.add(newModel);
            }
        }

        result.addAll(examList);
        result.addAll(passList);
        result.addAll(cpList);
        result.addAll(cwList);
        result.addAll(practicList);
        Collections.sort(result);

        return result;
    }

    private StudentDebtModel createFOCmodel (StudentDebtModel tempModel) {
        StudentDebtModel newModel = new StudentDebtModel();
        newModel.setFio(tempModel.getFio());
        newModel.setFulltitle(tempModel.getFulltitle());
        newModel.setIdChair(tempModel.getIdChair());
        newModel.setIdSr(tempModel.getIdSr());
        newModel.setIdSemester(tempModel.getIdSemester());
        newModel.setIdSubj(tempModel.getIdSubj());
        newModel.setGroupname(tempModel.getGroupname());
        newModel.setSemesterStr(tempModel.getSemesterStr());
        newModel.setSemesternumber(tempModel.getSemesternumber());
        newModel.setSubjectname(tempModel.getSubjectname());
        return newModel;
    }
}
