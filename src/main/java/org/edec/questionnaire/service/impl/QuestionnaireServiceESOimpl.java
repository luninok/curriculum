package org.edec.questionnaire.service.impl;

import org.edec.questionnaire.manager.QuestionnaireManager;
import org.edec.questionnaire.model.GroupModel;
import org.edec.questionnaire.model.SubjectModel;
import org.edec.questionnaire.model.dao.QuestionnaireEsoModel;
import org.edec.questionnaire.service.QuestionnaireServiceESO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Max Dimukhametov
 */
public class QuestionnaireServiceESOimpl implements QuestionnaireServiceESO {
    private QuestionnaireManager questionnaireManager = new QuestionnaireManager();

    @Override
    public List<GroupModel> getGroupBySem (Long idSem) {
        return getGroupsByModel(questionnaireManager.getQuestionnaireEsoModel(idSem));
    }

    @Override
    public List<String> getRecipients (Long idSem, Long idDG) {
        List<String> recipients = questionnaireManager.getRecipients(idSem, idDG);
        if (recipients == null) {
            return null;
        }
        Set<String> tempList = new HashSet<>(recipients);
        return new ArrayList<>(tempList);
    }

    private List<GroupModel> getGroupsByModel (List<QuestionnaireEsoModel> models) {
        List<GroupModel> groups = new ArrayList<>();

        for (QuestionnaireEsoModel model : models) {
            boolean addGroup = true;
            for (GroupModel group : groups) {
                if (group.getGroupname().equals(model.getGroupname())) {
                    group.getSubjects().add(new SubjectModel(model.getIdSubj(), model.getSubjectname()));
                    addGroup = false;
                    break;
                }
            }
            if (addGroup) {
                GroupModel group = new GroupModel(model.getIdChair(), model.getIdDG(), model.getGroupname());
                group.getSubjects().add(new SubjectModel(model.getIdSubj(), model.getSubjectname()));
                groups.add(group);
            }
        }

        return groups;
    }
}
