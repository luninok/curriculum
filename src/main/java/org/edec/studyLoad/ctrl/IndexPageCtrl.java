package org.edec.studyLoad.ctrl;


import javafx.scene.control.ComboBox;
import org.edec.commission.model.SubjectDebtModel;
import org.edec.model.SemesterModel;
import org.edec.main.model.DepartmentModel;
import org.edec.studyLoad.ctrl.renderer.AssignmentRenderer;
import org.edec.studyLoad.ctrl.renderer.VacancyRenderer;
import org.edec.studyLoad.ctrl.renderer.TeachersRenderer;
import org.edec.studyLoad.ctrl.windowCtrl.WinVacancyDialogCtrl;
import org.edec.studyLoad.model.AssignmentModel;
import org.edec.studyLoad.model.TeacherModel;
import org.edec.studyLoad.model.VacancyModal;
import org.edec.studyLoad.service.impl.StudyLoadServiceImpl;
import org.edec.studyLoad.service.StudyLoadService;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexPageCtrl extends CabinetSelector {
    // public static final String SELECTED_POSITION = "selected_position";
    // public static final String SELECTED_RATE = "selected_rate";
    @Wire
    private Listbox lbTeachers, lbVacancy, lbAssignments;
    @Wire
    private Combobox cmbFaculty;
    @Wire
    private Vbox col2;
    @Wire
    private Vbox col1;
    @Wire
    private Label labelFIO;

    private String selectFIO = "";

    private StudyLoadService studyLoadService = new StudyLoadServiceImpl();
    private Combobox selectedPosition;
    private Spinner selectedRate;
    private List<VacancyModal> vacancyModals = new ArrayList<>();
    private List<TeacherModel> teacherModels = new ArrayList<>();
    private List<AssignmentModel> assignmentModels = new ArrayList<>();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        lbVacancy.setItemRenderer(new VacancyRenderer());
        lbTeachers.setItemRenderer(new TeachersRenderer());
        lbAssignments.setItemRenderer(new AssignmentRenderer());
    }

    protected void fill() {
        fillCmbFaculty();
        fillLbTeacher();
        fillLbAssignment();
    }

    private void fillCmbFaculty() {

        List<DepartmentModel> list = studyLoadService.getDepartments();
        for (DepartmentModel department : list) {
            Comboitem comboitem = new Comboitem();
            comboitem.setLabel(department.getFulltitle());
            comboitem.setValue(department);
            cmbFaculty.getItems().add(comboitem);
        }
        if(cmbFaculty.getItems().size() != 0) { cmbFaculty.setSelectedIndex(0); }
    }

    @Listen("onSelect = #lbTeachers")
    public void teacherRowClick() {
        labelFIO.setValue("");
        col1.setVisible(true);
        col2.setVisible(false);
        TeacherModel selectTeacher = lbTeachers.getSelectedItem().getValue();
        lbTeachers.getSelectedItem().setSelected(false);
        labelFIO.setValue(selectTeacher.toString());
    }

    @Listen("onClick = #btnRemoveVacancy")
    public void removeVacancyClick() {
        if (lbVacancy.getSelectedItem() != null) {
            vacancyModals.remove(lbVacancy.getSelectedIndex());
            lbVacancy.removeItemAt(lbVacancy.getSelectedIndex());
            PopupUtil.showInfo("Вакансия успешно удалена");
        } else {
            PopupUtil.showError("Выберити вакансию для удаления!");
        }

    }

    @Listen("onClick = #btnBackward")
    public void labelFIOClick() {
        col2.setVisible(true);
        col1.setVisible(false);
    }

    @Listen("onClick = #vacancyRows row")
    public void vacancyRowClick(Event e) {
        Row r = (Row) e.getTarget();
        String t = ((Label) r.getChildren().get(0)).getValue();
    }

    @Listen("onChange = #cmbFaculty")
    public void laterForTeachers() {
        fillLbTeacher();
        fillLbAssignment();
    }

    @Listen("onClick = #btnAddRate")
    public void openWinRateStructure() {
        Map arg = new HashMap();
        Window win = (Window) Executions.createComponents("window/winRateDialog.zul", null, arg);
        win.doModal();
    }

    @Listen("onClick = #btnAddVacancy")
    public void openWinVacancyStructure() {
        Map arg = new HashMap();
        arg.put(WinVacancyDialogCtrl.INDEX_PAGE, this);
        Window win = (Window) Executions.createComponents("window/winVacancyDialog.zul", null, arg);
        win.doModal();
    }

    @Listen("onClick = #btnChangeVacancy")
    public void changeVacancyClick() {
        if (lbVacancy.getSelectedItem() != null) {

            Map arg = new HashMap();
            arg.put(WinVacancyDialogCtrl.INDEX_PAGE, this);
            arg.put(WinVacancyDialogCtrl.SELECT_VACANCY, lbVacancy.getSelectedItem().getValue());
            Window win = (Window) Executions.createComponents("window/winVacancyDialog.zul", null, arg);
            win.doModal();
        } else {
            PopupUtil.showInfo("Выберите вакансию!");
        }
    }

    private void fillLbTeacher() {
        lbTeachers.clearSelection();
        teacherModels.clear();
        List<TeacherModel> list = studyLoadService.getTeachers((String) cmbFaculty.getValue());
        teacherModels = list;
        ListModelList<TeacherModel> teacherListModelList = new ListModelList<>(teacherModels);
        lbTeachers.setModel(teacherListModelList);
        lbTeachers.renderAll();
    }

    public void fillLbVacancy(String position, String rate) {
        VacancyModal selectVacancy = new VacancyModal(position, rate, lbVacancy.getItemCount());
        vacancyModals.add(selectVacancy);
        ListModelList<VacancyModal> vacancyListModelList = new ListModelList<>(vacancyModals);
        lbVacancy.setModel(vacancyListModelList);
        lbVacancy.renderAll();
    }

    private void fillLbAssignment() {
        // TODO Создать отдельный renderer, добавить семестр как текущий
        lbAssignments.getItems().clear();
        List<AssignmentModel> assignmentModels = studyLoadService.getInstructions(56L, ((DepartmentModel)cmbFaculty.getSelectedItem().getValue()).getIdDepartment());
        ListModelList<AssignmentModel> assignmentModelListModelList = new ListModelList<>(assignmentModels);
        lbAssignments.setModel(assignmentModelListModelList);
        lbAssignments.renderAll();
        /*for (int i = 0; i < assignmentModels.size();i++) {
            AssignmentModel assignmentModel = assignmentModels.get(i);
            Listitem listitem = new Listitem();
            listitem.setValue(assignmentModel);
            new Listcell(String.valueOf(i)).setParent(listitem);
            new Listcell(assignmentModel.getFio()).setParent(listitem);
            new Listcell(assignmentModel.nameDiscipline).setParent(listitem);
            new Listcell(assignmentModel.getTypeInstructionString()).setParent(listitem);
            new Listcell(assignmentModel.getGroupName()).setParent(listitem);
            new Listcell(assignmentModel.getTypeControl()).setParent(listitem);
            new Listcell(String.valueOf(assignmentModel.getCourse())).setParent(listitem);
            new Listcell(String.valueOf(assignmentModel.getHourSaudCount())).setParent(listitem);
            new Listcell(String.valueOf(assignmentModel.getHoursCount())).setParent(listitem);
            lbAssignments.getItems().add(listitem);
        }*/
    }

    public void updateLbVacancy(String position, String rate) {
        VacancyModal changeVacancy = new VacancyModal(position, rate, lbVacancy.getSelectedIndex());
        for (VacancyModal vacancyModal : vacancyModals) {
            if (changeVacancy.getVacancy().equals(vacancyModal.getVacancy())) {
                vacancyModal.setVacancy(changeVacancy.getVacancy());
                vacancyModal.setRate(changeVacancy.getRate());
                vacancyModal.setPosition(changeVacancy.getPosition());
            }
        }
        lbVacancy.clearSelection();
        ListModelList<VacancyModal> vacancyListModelList = new ListModelList<>(vacancyModals);
        lbVacancy.setModel(vacancyListModelList);
        lbVacancy.renderAll();
    }

}
