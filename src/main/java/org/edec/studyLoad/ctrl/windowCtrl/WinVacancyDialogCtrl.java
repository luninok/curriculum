package org.edec.studyLoad.ctrl.windowCtrl;

import org.edec.studyLoad.ctrl.IndexPageCtrl;
import org.edec.studyLoad.model.VacancyModel;
import org.edec.studyLoad.service.StudyLoadService;
import org.edec.studyLoad.service.impl.StudyLoadServiceImpl;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.List;

public class WinVacancyDialogCtrl extends CabinetSelector {
    public static final String INDEX_PAGE = "index_page";
    public static final String SELECT_VACANCY = "select_vacancy";
    @Wire
    private Spinner spinnerCountRate;
    @Wire
    private Combobox cmbPosition;
    @Wire
    Window winVacancyDialog;

    private StudyLoadService studyLoadService = new StudyLoadServiceImpl();
    private IndexPageCtrl indexPageCtrl;
    private VacancyModel vacancyModel;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        indexPageCtrl = (IndexPageCtrl) Executions.getCurrent().getArg().get(INDEX_PAGE);
        vacancyModel = (VacancyModel) Executions.getCurrent().getArg().get(SELECT_VACANCY);
        if (vacancyModel != null) {
            cmbPosition.setValue(vacancyModel.getPosition());
            spinnerCountRate.setValue(Integer.valueOf(vacancyModel.getRate()));
        }
    }

    protected void fill() {
        fillCmbPosition();
    }

    private void fillCmbPosition() {
        List<String> list = studyLoadService.getPosition();
        for (String position : list) {
            Comboitem comboitem = new Comboitem();
            comboitem.setLabel(position);
            cmbPosition.getItems().add(comboitem);
        }
    }

    @Listen("onClick = #btnTakeVacancy")
    public void onTakeVacancy() {
        if (cmbPosition.getSelectedItem() == null || spinnerCountRate.getValue() == null) {
            PopupUtil.showWarning("Введите указанные значения!");
            return;
        }
        String position = cmbPosition.getValue();
        String rate = spinnerCountRate.getValue().toString();
          if (vacancyModel != null) {
           indexPageCtrl.updateLbVacancy(position, rate);
        } else {
            indexPageCtrl.fillLbVacancy(position, rate);
        }
        winVacancyDialog.detach();
    }

}
