package org.edec.studyLoad.ctrl.windowCtrl;

import org.edec.studyLoad.ctrl.IndexPageCtrl;
import org.edec.studyLoad.ctrl.renderer.TeachersRenderer;
import org.edec.studyLoad.model.PositionModel;
import org.edec.studyLoad.model.TeacherModel;
import org.edec.studyLoad.model.VacancyModal;
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

public class WinRateDialogCtrl extends CabinetSelector {

    @Wire
    private Listbox lbRate;

    @Wire
    Textbox tbFamily, tbName, tbPatronymic;

    @Wire
    Combobox cmbPosition;

    private List<TeacherModel> searchTeacherModels = new ArrayList<>();
    private List<TeacherModel> departmentTeacherModels = new ArrayList<>();
    private List<PositionModel> positionModels = new ArrayList<>();
    private StudyLoadService studyLoadService = new StudyLoadServiceImpl();

    private IndexPageCtrl indexPageCtrl;
    private Long idDepartment;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        idDepartment = (Long) Executions.getCurrent().getArg().get("idDepartment");
        departmentTeacherModels = (List<TeacherModel>) Executions.getCurrent().getArg().get("teacherModels");
        indexPageCtrl = (IndexPageCtrl) Executions.getCurrent().getArg().get("indexPageCtrl");
    }

    protected void fill() {
        fillTable();
        fillCmbPosition();
        lbRate.setItemRenderer(new TeachersRenderer());

    }

    private void fillTable() {


    }

    private void fillCmbPosition() {

        positionModels = studyLoadService.getPositions();
        for (PositionModel position : positionModels) {
            Comboitem comboitem = new Comboitem();
            comboitem.setLabel(position.getPositionName());
            comboitem.setValue(position);
            cmbPosition.getItems().add(comboitem);
        }
        if(cmbPosition.getItems().size() != 0) { cmbPosition.setSelectedIndex(0); }

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

    @Listen("onClick = #btnTakeRate")
    public void addRate() {
        TeacherModel selectedTeacher = searchTeacherModels.get(lbRate.getSelectedIndex());
        Long selectedIdPosition = positionModels.get(cmbPosition.getSelectedIndex()).getIdPosition();
        for(TeacherModel teacher : departmentTeacherModels)
            if (teacher.getId_employee().equals(selectedTeacher.getId_employee())) {
                Messagebox.show("Выбранный преподаватель уже работает на этой кафедре!");
                return;
            }

        if (!studyLoadService.addRate(selectedTeacher.getId_employee(), idDepartment, selectedIdPosition))
            Messagebox.show("Ошибка добавления преподавателя");
    }

    @Listen("onClose = #winRateDialog")
    public void updateLbTeachers()
    {
        indexPageCtrl.updateLbTeachers();
    }
}
