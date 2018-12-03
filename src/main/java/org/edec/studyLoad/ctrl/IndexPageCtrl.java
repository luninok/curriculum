package org.edec.studyLoad.ctrl;


import org.edec.main.model.DepartmentModel;
import org.edec.studyLoad.ctrl.renderer.EmploymentRenderer;
import org.edec.studyLoad.ctrl.renderer.VacancyRenderer;
import org.edec.studyLoad.ctrl.renderer.TeachersRenderer;
import org.edec.studyLoad.model.AssignmentModel;
import org.edec.studyLoad.model.EmploymentModel;
import org.edec.studyLoad.model.TeacherModel;
import org.edec.studyLoad.model.VacancyModel;
import org.edec.studyLoad.service.impl.StudyLoadServiceImpl;
import org.edec.studyLoad.service.StudyLoadService;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.*;

public class IndexPageCtrl extends CabinetSelector {
    // public static final String SELECTED_POSITION = "selected_position";
    // public static final String SELECTED_RATE = "selected_rate";
    @Wire
    private Listbox lbTeachers, lbVacancy, lbAssignments, lbEmployment;
    @Wire
    private Combobox cmbFaculty;
    @Wire
    private Combobox cmbPosition;
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
    private TeacherModel selectEmployee;
    private List<TeacherModel> teacherModels = new ArrayList<>();
    private List<EmploymentModel> employmentModels = new ArrayList<>();
    private List<AssignmentModel> assignmentModels = new ArrayList<>();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        lbVacancy.setItemRenderer(new VacancyRenderer());
        lbTeachers.setItemRenderer(new TeachersRenderer());
        lbEmployment.setItemRenderer(new EmploymentRenderer());
        fillLbVacancy();
    }

    protected void fill() {
        fillCmbFaculty();
        fillLbAssignment();
        laterForTeachers();
    }

    private void fillCmbFaculty() {

        List<DepartmentModel> list = studyLoadService.getDepartments();
        for (DepartmentModel department : list) {
            Comboitem comboitem = new Comboitem();
            comboitem.setLabel(department.getFulltitle());
            comboitem.setValue(department);
            cmbFaculty.getItems().add(comboitem);
        }
        if (cmbFaculty.getItems().size() != 0) {
            cmbFaculty.setSelectedIndex(0);
        }
    }

    @Listen("onSelect = #lbTeachers")
    public void teacherRowClick() {
        labelFIO.setValue("");
        col1.setVisible(true);
        col2.setVisible(false);
        TeacherModel selectTeacher = lbTeachers.getSelectedItem().getValue();
        selectEmployee = selectTeacher;
        lbTeachers.getSelectedItem().setSelected(false);
        labelFIO.setValue(selectTeacher.toString());
        fillLbEmployment(selectTeacher);
        //fillCmbPosition();
    }

    private void fillCmbPosition() {
        List<String> list = studyLoadService.getPosition();
        for (String position : list) {
            Comboitem comboitem = new Comboitem();
            comboitem.setValue(position);
            cmbPosition.getItems().add(comboitem);
        }
    }

    private void fillLbEmployment(TeacherModel selectTeacher) {
        //посылаем запрос в бд и если у выбранного преподавателя есть должность на этой кафедре и ставка с нормой времени на ставку не равны 0, то записываем их в таблицу lbEmployment
        List<EmploymentModel> list = studyLoadService.getEmployment(selectTeacher, (String) cmbFaculty.getValue());
        employmentModels = list;
        ListModelList<EmploymentModel> employmentListModelList = new ListModelList<>(employmentModels);
        lbEmployment.setModel(employmentListModelList);
        lbEmployment.renderAll();
    }

    public void fillLbVacancy(/*String position, String rate*/) {
        /*VacancyModel selectVacancy = new VacancyModel(position, rate, lbVacancy.getItemCount());
        vacancyModels.add(selectVacancy);
        ListModelList<VacancyModel> vacancyListModelList = new ListModelList<>(vacancyModels);
        lbVacancy.setModel(vacancyListModelList);
        lbVacancy.renderAll();*/
        List<VacancyModel> vacancyModels = studyLoadService.getVacancy();
        ListModelList<VacancyModel> listModelOrderRule = new ListModelList<>(vacancyModels);
        lbVacancy.setModel(listModelOrderRule);
        lbVacancy.renderAll();
    }

    @Listen("onClick = #btnSaveEmployment")
    public void saveEmploymentClick() {
        /*studyLoadService.updateEmployment(selectEmployee.getId_employee(), shorttitle, byworker, rolename, wagerate, time_wagerate);
        fillLbEmployment(selectEmployee);*/
    }

    @Listen("onClick = #btnRemoveVacancy")
    public void removeVacancyClick() {
        if (lbVacancy.getSelectedItem() != null) {
            VacancyModel vacancyModel = lbVacancy.getSelectedItem().getValue();
            studyLoadService.deleteVacancy(vacancyModel.getId_vacancy());
            fillLbVacancy();
            /*
            vacancyModels.remove(lbVacancy.getSelectedIndex());
            lbVacancy.removeItemAt(lbVacancy.getSelectedIndex());
            */
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

   /* @Listen("onClick = #vacancyRows row")
    public void vacancyRowClick(Event e) {
        Row r = (Row) e.getTarget();
        String t = ((Label) r.getChildren().get(0)).getValue();
    } */

    @Listen("onChange = #cmbFaculty")
    public void laterForTeachers() {
        lbTeachers.clearSelection();
        teacherModels.clear();
        List<TeacherModel> list = studyLoadService.getTeachers((String) cmbFaculty.getValue());
        teacherModels = list;
        ListModelList<TeacherModel> teacherListModelList = new ListModelList<>(teacherModels);
        lbTeachers.setModel(teacherListModelList);
        lbTeachers.renderAll();
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
        Runnable updateLbVacancy = this::fillLbVacancy;
        Map<String, Object> arg = new HashMap<>();
        arg.put("fillLbVacancy", updateLbVacancy);
        ComponentHelper.createWindow("window/winVacancyDialog.zul", "winVacancyDialog", arg).doModal();

        /*Map arg = new HashMap();
        arg.put(WinVacancyDialogCtrl.INDEX_PAGE, this);
        Window win = (Window) Executions.createComponents("window/winVacancyDialog.zul", null, arg);
        win.doModal();*/
    }

    @Listen("onClick = #btnChangeVacancy")
    public void changeVacancyClick() {
        if (lbVacancy.getSelectedItem() != null) {
            Runnable updateLbVacancy = this::fillLbVacancy;
            Map<String, Object> arg = new HashMap<>();
            arg.put("fillLbVacancy", updateLbVacancy);
            arg.put("vacancy", lbVacancy.getSelectedItem().getValue());

            ComponentHelper.createWindow("window/winVacancyDialog.zul", "winVacancyDialog", arg).doModal();
           /* Map arg = new HashMap();
            arg.put(WinVacancyDialogCtrl.INDEX_PAGE, this);
            arg.put(WinVacancyDialogCtrl.SELECT_VACANCY, lbVacancy.getSelectedItem().getValue());
            Window win = (Window) Executions.createComponents("window/winVacancyDialog.zul", null, arg);
            win.doModal(); */
        } else {
            PopupUtil.showInfo("Выберите вакансию!");
        }
    }


    public void fillLbAssignment() {
        // TODO Создать отдельный renderer, добавить семестр как текущий
        lbAssignments.getItems().clear();
        List<AssignmentModel> assignmentModels = studyLoadService.getInstructions(56L, ((DepartmentModel) cmbFaculty.getSelectedItem().getValue()).getIdDepartment());
        for (int i = 0; i < assignmentModels.size(); i++) {
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

   /* public void updateLbVacancy(String position, String rate) {
        VacancyModel changeVacancy = new VacancyModel(position, rate, lbVacancy.getSelectedIndex());
        for (VacancyModel vacancyModal : vacancyModels) {
            if (changeVacancy.getVacancy().equals(vacancyModal.getVacancy())) {
                vacancyModal.setVacancy(changeVacancy.getVacancy());
                vacancyModal.setRate(changeVacancy.getRate());
                vacancyModal.setPosition(changeVacancy.getPosition());
            }
        }
        lbVacancy.clearSelection();
        ListModelList<VacancyModel> vacancyListModelList = new ListModelList<>(vacancyModels);
        lbVacancy.setModel(vacancyListModelList);
        lbVacancy.renderAll();
    }*/

}
