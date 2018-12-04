package org.edec.studyLoad.ctrl;


import org.edec.main.model.DepartmentModel;
import org.edec.studyLoad.ctrl.renderer.VacancyRenderer;
import org.edec.studyLoad.ctrl.renderer.TeachersRenderer;
import org.edec.studyLoad.ctrl.windowCtrl.WinVacancyDialogCtrl;
import org.edec.studyLoad.model.AssignmentModel;
import org.edec.studyLoad.model.PositionModel;
import org.edec.studyLoad.model.TeacherModel;
import org.edec.studyLoad.model.VacancyModel;
import org.edec.studyLoad.service.impl.StudyLoadServiceImpl;
import org.edec.studyLoad.service.StudyLoadService;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
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
    private List<VacancyModel> vacancyModels = new ArrayList<>();
    private List<TeacherModel> teacherModels = new ArrayList<>();
    private List<PositionModel> positionModels = new ArrayList<>();
    private List<DepartmentModel> departmentModels = new ArrayList<>();
    private DepartmentModel selectedDepartmentModel = new DepartmentModel();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        lbVacancy.setItemRenderer(new VacancyRenderer());
        lbTeachers.setItemRenderer(new TeachersRenderer());
    }

    protected void fill() {
        positionModels = studyLoadService.getPositions();
        fillCmbFaculty();
        fillLbAssignment();
    }

    private void fillCmbFaculty() {

        departmentModels = studyLoadService.getDepartments();
        for (DepartmentModel department : departmentModels) {
            Comboitem comboitem = new Comboitem();
            comboitem.setLabel(department.getFulltitle());
            comboitem.setValue(department);
            cmbFaculty.getItems().add(comboitem);
        }
        if(cmbFaculty.getItems().size() != 0) { cmbFaculty.setSelectedIndex(0); }

    }

    @Listen("onDoubleClick = #lbTeachers")
    public void teacherRowClick() {
        labelFIO.setValue("");
        col1.setVisible(true);
        col2.setVisible(false);
        TeacherModel selectedTeacher = lbTeachers.getSelectedItem().getValue();
        lbTeachers.getSelectedItem().setSelected(false);
        labelFIO.setValue(selectedTeacher.toString());
    }

    @Listen("onClick = #btnRemoveVacancy")
    public void removeVacancyClick() {
        if (lbVacancy.getSelectedItem() != null) {
            vacancyModels.remove(lbVacancy.getSelectedIndex());
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
    public void updateLbTeachers() {
        selectedDepartmentModel = cmbFaculty.getSelectedItem().getValue();
        lbTeachers.clearSelection();
        teacherModels.clear();
        teacherModels = studyLoadService.getTeachers((String) cmbFaculty.getValue());
        ListModelList<TeacherModel> teacherListModelList = new ListModelList<>(teacherModels);
        lbTeachers.setModel(teacherListModelList);
        lbTeachers.renderAll();
        fillLbAssignment();
    }

    @Listen("onClick = #btnAddRate")
    public void openWinRateStructure() {
        Map arg = new HashMap();
        arg.put("teacherModels", teacherModels);
        arg.put("idDepartment", selectedDepartmentModel.getIdDepartment());
        arg.put("indexPageCtrl", this);
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

    @Listen("onClick = #btnRemoveRate")
    public void removeRate()
    {
        if (lbTeachers.getSelectedItems().isEmpty()) {
            Messagebox.show("Выберите преподавателя, которого хотите удалить!");
            return;
        }
        TeacherModel selectedTeacher = lbTeachers.getSelectedItem().getValue();
        if (studyLoadService.removeRate(selectedTeacher.getId_employee(), selectedDepartmentModel.getIdDepartment()))
            updateLbTeachers();
        else
            Messagebox.show("Ошибка удаления преподавателя");
    }

    @Listen("onClick = #btnFillRate")
    public void fillRateClick()
    {
        if (lbVacancy.getSelectedItems().isEmpty()) {
            Messagebox.show("Выберите вакансию, которую хотите заполнить!");
            return;
        }
        VacancyModel selectedVacancy = lbVacancy.getSelectedItem().getValue();
        Long idPosition = null;
        for (PositionModel position : positionModels) {
            if(selectedVacancy.getPosition().equals(position.getPositionName())) {
                idPosition = position.getIdPosition();
                break;
            }
        }
        Map arg = new HashMap();
        arg.put("idPosition", idPosition);
        arg.put("idDepartment", selectedDepartmentModel.getIdDepartment());
        arg.put("rate", selectedVacancy.getRate());
        arg.put("teacherModels", teacherModels);
        arg.put("indexPageCtrl", this);
        Window win = (Window) Executions.createComponents("window/winFillVacancyDialog.zul", null, arg);
        win.doModal();
    }


    public void fillLbVacancy(String position, Double rate) {
        VacancyModel selectVacancy = new VacancyModel(position, rate, lbVacancy.getItemCount());
        vacancyModels.add(selectVacancy);
        ListModelList<VacancyModel> vacancyListModelList = new ListModelList<>(vacancyModels);
        lbVacancy.setModel(vacancyListModelList);
        lbVacancy.renderAll();
    }

    public void fillLbAssignment() {
        // TODO Создать отдельный renderer, добавить семестр как текущий
        lbAssignments.getItems().clear();
        List<AssignmentModel> assignmentModels = studyLoadService.getInstructions(56L, ((DepartmentModel)cmbFaculty.getSelectedItem().getValue()).getIdDepartment());
        for (int i = 0; i < assignmentModels.size();i++) {
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
        }
    }

    public void updateLbVacancy(String position, Double rate) {
        VacancyModel changeVacancy = new VacancyModel(position, rate, lbVacancy.getSelectedIndex());
        for (VacancyModel vacancyModel : vacancyModels) {
            if (changeVacancy.getVacancy().equals(vacancyModel.getVacancy())) {
                vacancyModel.setVacancy(changeVacancy.getVacancy());
                vacancyModel.setRate(changeVacancy.getRate());
                vacancyModel.setPosition(changeVacancy.getPosition());
            }
        }
        lbVacancy.clearSelection();
        ListModelList<VacancyModel> vacancyListModelList = new ListModelList<>(vacancyModels);
        lbVacancy.setModel(vacancyListModelList);
        lbVacancy.renderAll();
    }



}
