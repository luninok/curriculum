package org.edec.studyLoad.ctrl.windowCtrl;

import org.edec.studyLoad.ctrl.IndexPageCtrl;
import org.edec.studyLoad.model.PositionModel;
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

import java.util.ArrayList;
import java.util.List;

public class WinVacancyDialogCtrl extends CabinetSelector {
    @Wire
    private Doublebox dbCountRate;
    @Wire
    private Combobox cmbPosition;
    @Wire
    Window winVacancyDialog;

    private StudyLoadService studyLoadService = new StudyLoadServiceImpl();
    private Runnable updateLbVacancy;
    private VacancyModel vacancyModel;
    private List<PositionModel> positionModels = new ArrayList<>();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        updateLbVacancy = (Runnable) Executions.getCurrent().getArg().get("fillLbVacancy");
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
        positionModels = studyLoadService.getPositions();
        for (PositionModel position : positionModels) {
            Comboitem comboitem = new Comboitem();
            comboitem.setLabel(position.getPositionName());
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
        Long idEmployeeRole = null;
        for (PositionModel position : positionModels) {
            if (position.getPositionName().equals(rolename)) {
                idEmployeeRole = position.getIdPosition();
            }
        }
        if (vacancyModel != null) {
            studyLoadService.updateVacancy(vacancyModel.getId_vacancy(), idEmployeeRole, wagerate);
            updateLbVacancy.run();
            PopupUtil.showInfo("Вакансия успешно обновлена!");
        } else {
            studyLoadService.createVacancy(idEmployeeRole, wagerate);
            updateLbVacancy.run();
            PopupUtil.showInfo("Вакансия успешно добавлена!");
        }
        PopupUtil.showInfo("Вакансия была успешно добавлена.");
    }

}
