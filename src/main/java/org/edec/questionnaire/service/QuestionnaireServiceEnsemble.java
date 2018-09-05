package org.edec.questionnaire.service;

import org.edec.questionnaire.model.QuestionnaireModel;
import org.edec.questionnaire.model.TopQuestModel;

/**
 * @author Max Dimukhametov
 */
public interface QuestionnaireServiceEnsemble {
    boolean sendQuestionnaire (QuestionnaireModel questionnaire);
    Long sendTopQuest (TopQuestModel topQuest);
}
