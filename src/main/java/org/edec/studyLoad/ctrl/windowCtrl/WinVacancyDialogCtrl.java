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
    private Doublebox dbCountRate;
    @Wire
    private Combobox cmbPosition;
    @Wire
    Window winVacancyDialog;

    private StudyLoadService studyLoadService = new StudyLoadServiceImpl();
    //private IndexPageCtrl indexPageCtrl;
    private Runnable updateLbVacancy;
    private VacancyModel vacancyModel;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        updateLbVacancy = (Runnable) Executions.getCurrent().getArg().get("fillLbVacancy");
        //indexPageCtrl = (IndexPageCtrl) Executions.getCurrent().getArg().get(INDEX_PAGE);
        vacancyModel = (VacancyModel) Executions.getCurrent().getArg().get("vacancy");
        if (vacancyModel != null) {
            cmbPosition.setValue(vacancyModel.getRolename());
            dbCountRate.setValue(Double.valueOf((vacancyModel.getWagerate())));
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
        if (cmbPosition.getSelectedItem() == null || dbCountRate.getValue() == null) {
            PopupUtil.showWarning("Введите указанные значения!");
            return;
        }
        String rolename = cmbPosition.getValue();
        String wagerate = dbCountRate.getValue().toString();
          if (vacancyModel != null) {
              studyLoadService.updateVacancy(vacancyModel.getId_vacancy(), rolename, wagerate);
              updateLbVacancy.run();
           //обновили бд
           //indexPageCtrl.updateLbVacancy(position, rate);
        } else {
              studyLoadService.createVacancy(rolename, wagerate);
              updateLbVacancy.run();
             //сохранили в бд
           // indexPageCtrl.fillLbVacancy(position, rate);
        }
        //winVacancyDialog.detach();
    }

}
