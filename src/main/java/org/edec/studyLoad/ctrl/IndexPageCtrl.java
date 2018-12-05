package org.edec.studyLoad.ctrl;


import javafx.scene.control.ListCell;
import org.edec.main.model.DepartmentModel;
import org.edec.studyLoad.ctrl.renderer.EmploymentRenderer;
import org.edec.studyLoad.ctrl.renderer.VacancyRenderer;
import org.edec.studyLoad.ctrl.renderer.TeachersRenderer;
import org.edec.studyLoad.ctrl.windowCtrl.WinVacancyDialogCtrl;
import org.edec.studyLoad.model.*;
import org.edec.studyLoad.service.impl.StudyLoadServiceImpl;
import org.edec.studyLoad.service.StudyLoadService;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.*;

public class IndexPageCtrl extends CabinetSelector {
    @Wire
    private Listbox lbTeachers, lbVacancy, lbAssignments, lbEmployment;
    @Wire
    private Combobox cmbFaculty;
    @Wire
    private Listheader lhByWorker, lhPositionTeacher, lhRateTeacher, lhRateTime;
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
    private List<EmploymentModel> employmentModels = new ArrayList<>();
    private List<AssignmentModel> assignmentModels = new ArrayList<>();
    private List<DepartmentModel> departmentModels = new ArrayList<>();
    private List<PositionModel> positionModels = new ArrayList<>();
    private DepartmentModel selectedDepartmentModel = new DepartmentModel();
    private TeacherModel selectedTeacher;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        lbVacancy.setItemRenderer(new VacancyRenderer());
        lbTeachers.setItemRenderer(new TeachersRenderer());
        lbEmployment.setItemRenderer(new EmploymentRenderer());
        fillLbVacancy();
    }

    protected void fill() {
        positionModels = studyLoadService.getPositions();
        fillCmbFaculty();
        fillLbAssignment();
        updateLbTeachers();
    }

    private void fillCmbFaculty() {
        departmentModels = studyLoadService.getDepartments();
        for (DepartmentModel department : departmentModels) {
            Comboitem comboitem = new Comboitem();
            comboitem.setLabel(department.getFulltitle());
            comboitem.setValue(department);
            cmbFaculty.getItems().add(comboitem);
        }
        if (cmbFaculty.getItems().size() != 0) {
            cmbFaculty.setSelectedIndex(0);
        }
    }

    @Listen("onDoubleClick = #lbTeachers")
    public void teacherRowClick() {
        labelFIO.setValue("");
        col1.setVisible(true);
        col2.setVisible(false);
        selectedTeacher = lbTeachers.getSelectedItem().getValue();
        fillLbEmployment(selectedTeacher);
        lbTeachers.getSelectedItem().setSelected(false);
        labelFIO.setValue(selectedTeacher.toString());
    }

    private void fillLbEmployment(TeacherModel selectTeacher) {
        List<EmploymentModel> list = studyLoadService.getEmployment(selectTeacher, (String) cmbFaculty.getValue());
        employmentModels = new ArrayList<>();
        employmentModels.add(list.get(0));
        ListModelList<EmploymentModel> employmentListModelList = new ListModelList<>(employmentModels);
        lbEmployment.setModel(employmentListModelList);
        lbEmployment.renderAll();
    }

    public void fillLbVacancy() {
        vacancyModels = studyLoadService.getVacancy();
        ListModelList<VacancyModel> vacancyListModelList = new ListModelList<>(vacancyModels);
        lbVacancy.setModel(vacancyListModelList);
        lbVacancy.renderAll();
    }

    @Listen("onClick = #btnSaveEmployment")
    public void saveEmploymentClick() {
        //List<ByworkerModel> listByworker = studyLoadService.getByworker();
        Listitem item = lbEmployment.getItems().get(0);
        Listcell cellByworker = (Listcell)item.getChildren().get(1);
        Combobox comboboxByworker = (Combobox) cellByworker.getChildren().get(0);
        ByworkerModel byworkerModel = comboboxByworker.getSelectedItem().getValue();
        Long idByworker = byworkerModel.getIdByworker();
        Listcell cellPosition = (Listcell)item.getChildren().get(2);
        Combobox comboboxPosition = (Combobox) cellPosition.getChildren().get(0);
        PositionModel position = comboboxPosition.getSelectedItem().getValue();
        Long idPosition = position.getIdPosition();
        Listcell cellWagerate = (Listcell)item.getChildren().get(3);
        Double doubleWagerate = ((Doublebox)cellWagerate.getChildren().get(0)).getValue();
        Listcell cellWagerateTime = (Listcell)item.getChildren().get(4);
        Double doubleWagerateTime = ((Doublebox)cellWagerateTime.getChildren().get(0)).getValue();

        studyLoadService.updateEmployment(selectedTeacher.getId_employee(), idByworker, idPosition, doubleWagerate, doubleWagerateTime);
        fillLbEmployment(selectedTeacher);
    }

    @Listen("onClick = #btnRemoveVacancy")
    public void removeVacancyClick() {
        if (lbVacancy.getSelectedItem() != null) {
            VacancyModel vacancyModel = lbVacancy.getSelectedItem().getValue();
            studyLoadService.deleteVacancy(vacancyModel.getId_vacancy());
            fillLbVacancy();
            PopupUtil.showInfo("Вакансия успешно удалена");
        } else {
            PopupUtil.showError("Выберите вакансию для удаления!");
        }

    }

    @Listen("onClick = #btnBackward")
    public void labelFIOClick() {
        col2.setVisible(true);
        col1.setVisible(false);
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

    @Listen("onClick = #btnFillRate")
    public void fillRate() {

        Long idPosition = null;
        for (PositionModel position : positionModels) {
            if (position.getPositionName().equals(vacancyModels.get(lbVacancy.getSelectedIndex()).getRolename())) {
                idPosition = position.getIdPosition();
                break;
            }
        }

        Map arg = new HashMap();
        arg.put("teacherModels", teacherModels);
        arg.put("idDepartment", selectedDepartmentModel.getIdDepartment());
        arg.put("idPosition", idPosition);
        arg.put("rate", vacancyModels.get(lbVacancy.getSelectedIndex()).getWagerate());
        arg.put("indexPageCtrl", this);
        Window win = (Window) Executions.createComponents("window/winFillVacancyDialog.zul", null, arg);
        win.doModal();
    }

    @Listen("onClick = #btnAddVacancy")
    public void openWinVacancyStructure() {
        Runnable updateLbVacancy = this::fillLbVacancy;
        Map<String, Object> arg = new HashMap<>();
        arg.put("fillLbVacancy", updateLbVacancy);
        ComponentHelper.createWindow("window/winVacancyDialog.zul", "winVacancyDialog", arg).doModal();
    }

    @Listen("onClick = #btnChangeVacancy")
    public void changeVacancyClick() {
        if (lbVacancy.getSelectedItem() != null) {
            Runnable updateLbVacancy = this::fillLbVacancy;
            Map<String, Object> arg = new HashMap<>();
            arg.put("fillLbVacancy", updateLbVacancy);
            arg.put("vacancy", lbVacancy.getSelectedItem().getValue());
            ComponentHelper.createWindow("window/winVacancyDialog.zul", "winVacancyDialog", arg).doModal();
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
