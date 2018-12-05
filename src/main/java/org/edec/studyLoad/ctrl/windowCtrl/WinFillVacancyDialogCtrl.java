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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

public class WinFillVacancyDialogCtrl extends CabinetSelector {

    @Wire
    private Listbox lbRate;

    @Wire
    Textbox tbFamily, tbName, tbPatronymic;

    private List<TeacherModel> searchTeacherModels = new ArrayList<>();
    private List<TeacherModel> departmentTeacherModels = new ArrayList<>();
    private StudyLoadService studyLoadService = new StudyLoadServiceImpl();

    private IndexPageCtrl indexPageCtrl;
    private Long idDepartment, idPosition;
    private Double rate;


    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        idDepartment = (Long) Executions.getCurrent().getArg().get("idDepartment");
        idPosition = (Long) Executions.getCurrent().getArg().get("idPosition");
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
            Messagebox.show("Заполните хотя бы одно поле!");
            return;
        }

        lbRate.clearSelection();
        searchTeacherModels = studyLoadService.searchTeachers(tbFamily.getValue(), tbName.getValue(), tbPatronymic.getValue());
        lbRate.setModel(new ListModelList<>(searchTeacherModels));
        lbRate.renderAll();
    }

    @Listen("onClick = #btnFillVacancy")
    public void addRateBasedOnVacancy() {
        TeacherModel selectedTeacher = searchTeacherModels.get(lbRate.getSelectedIndex());
        for(TeacherModel teacher : departmentTeacherModels) {
            if (teacher.getId_employee().equals(selectedTeacher.getId_employee())) {
                Messagebox.show("Выбранный преподаватель уже работает на этой кафедре!");
                return;
            }
        }

        if (!studyLoadService.addRateBasedOnVacancy(selectedTeacher.getId_employee(), idDepartment, idPosition, rate)) {
            Messagebox.show("Ошибка заполнения вакансии");
        }
    }

    @Listen("onClose = #winFillVacancyDialog")
    public void updateLbTeachers()
    {
        indexPageCtrl.updateLbTeachers();
    }
}
