package org.edec.student.questionnaire.ctrl;

import org.edec.student.questionnaire.ctrl.renderer.QuestionnaireRenderer;
import org.edec.student.questionnaire.model.QuestionnaireModel;
import org.edec.student.questionnaire.service.QuestionnaireEnsembleService;
import org.edec.student.questionnaire.service.impl.QuestionnaireEnsembleImpl;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import java.util.List;


public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Listbox lbQuestionnaire;

    private QuestionnaireEnsembleService questionnaireEnsembleService = new QuestionnaireEnsembleImpl();

    protected void fill () {
        lbQuestionnaire.setItemRenderer(new QuestionnaireRenderer());
        Executions.getCurrent().getSession().setAttribute(QuestionnaireCtrl.INDEX_PAGE_QUESTIONNAIRE, this);

        rendererListbox();
    }

    void rendererListbox () {
        List<QuestionnaireModel> questionnaireModels = questionnaireEnsembleService.getQuestionnairesByIdHum(
                template.getCurrentUser().getIdHum());
        lbQuestionnaire.setModel(new ListModelList<Object>(questionnaireModels));
        lbQuestionnaire.renderAll();
    }
}