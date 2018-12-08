package org.edec.studyLoad.ctrl.windowCtrl;

import org.edec.studyLoad.ctrl.IndexPageCtrl;
import org.edec.studyLoad.ctrl.renderer.TeachersRenderer;
import org.edec.studyLoad.model.PositionModel;
import org.edec.studyLoad.model.TeacherModel;
import org.edec.studyLoad.model.VacancyModel;
import org.edec.studyLoad.service.StudyLoadService;
import org.edec.studyLoad.service.impl.StudyLoadServiceImpl;
import org.edec.utility.zk.CabinetSelector;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;

public class WinFillVacancyDialogCtrl extends CabinetSelector {

    @Wire
    private Listbox lbRate;

    @Wire
    Textbox tbFamily, tbName, tbPatronymic;

    @Wire
    Window winFillVacancyDialog;

    private List<TeacherModel> searchTeacherModels = new ArrayList<>();
    private List<TeacherModel> departmentTeacherModels = new ArrayList<>();
    private List<PositionModel> positionModels = new ArrayList<>();
    private StudyLoadService studyLoadService = new StudyLoadServiceImpl();

    private IndexPageCtrl indexPageCtrl;
    private Long idDepartment, idPosition, idVacancy;
    private Double rate;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        idDepartment = (Long) Executions.getCurrent().getArg().get("idDepartment");
        idPosition = (Long) Executions.getCurrent().getArg().get("idPosition");
        idVacancy = (Long) Executions.getCurrent().getArg().get("idVacancy");
        rate = (Double) Executions.getCurrent().getArg().get("rate");
        departmentTeacherModels = (List<TeacherModel>) Executions.getCurrent().getArg().get("teacherModels");
        indexPageCtrl = (IndexPageCtrl) Executions.getCurrent().getArg().get("indexPageCtrl");
    }

    protected void fill() {
        fillTable();
        lbRate.setItemRenderer(new TeachersRenderer());

    }

    private void fillTable() {


    }

    @Listen("onClick = #btnSearch")
    public void searchTeacher() {
        if (tbFamily.getText().equals("") && tbName.getText().equals("") && tbPatronymic.getText().equals("")) {
            PopupUtil.showWarning("Заполните хотя бы одно поле!");
            return;
        }

        lbRate.clearSelection();
        searchTeacherModels = studyLoadService.searchTeachers(tbFamily.getValue(), tbName.getValue(), tbPatronymic.getValue());
        lbRate.setModel(new ListModelList<>(searchTeacherModels));
        lbRate.renderAll();
    }

    @Listen("onClick = #btnFillVacancy")
    public void addRateBasedOnVacancy() {
        if (lbRate.getSelectedItems().isEmpty()){
            PopupUtil.showInfo("Выберите преподавателя, которого хотите добавить.");
            return;
        }

        TeacherModel selectedTeacher = searchTeacherModels.get(lbRate.getSelectedIndex());
        for(TeacherModel teacher : departmentTeacherModels) {
            if (teacher.getId_employee().equals(selectedTeacher.getId_employee())) {
                PopupUtil.showWarning("Выбранный преподаватель уже работает на этой кафедре!");
                return;
            }
        }

        if (!studyLoadService.addRateBasedOnVacancy(selectedTeacher.getId_employee(), idDepartment, idPosition, rate)) {
            PopupUtil.showError("Ошибка заполнения вакансии");
        }
        else {
            studyLoadService.deleteVacancy(idVacancy);
            indexPageCtrl.fillLbVacancy();
            indexPageCtrl.updateLbTeachers();
            winFillVacancyDialog.onClose();
            PopupUtil.showInfo("Сотрудник был добавлен успешно!");
        }
    }
}
