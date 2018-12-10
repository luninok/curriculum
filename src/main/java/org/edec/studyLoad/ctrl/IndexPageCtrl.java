package org.edec.studyLoad.ctrl;


import javafx.scene.control.ListCell;
import org.edec.main.model.DepartmentModel;
import org.edec.studyLoad.ctrl.renderer.AssignmentRenderer;
import org.edec.studyLoad.ctrl.renderer.EmploymentRenderer;
import org.edec.studyLoad.ctrl.renderer.VacancyRenderer;
import org.edec.studyLoad.ctrl.renderer.TeachersRenderer;
import org.edec.studyLoad.manager.EntityManagerStudyLoad;
import org.edec.studyLoad.ctrl.windowCtrl.WinVacancyDialogCtrl;
import org.edec.studyLoad.model.*;
import org.edec.studyLoad.report.ReportService;
import org.edec.studyLoad.service.impl.StudyLoadServiceImpl;
import org.edec.studyLoad.service.StudyLoadService;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.util.media.AMedia;
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
    private Label labelFIO;
    @Wire
    private Combobox cmbFaculty;

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
        lbAssignments.setItemRenderer(new AssignmentRenderer());
        fillLbVacancy();
    }

    protected void fill() {
        EntityManagerStudyLoad ent = new EntityManagerStudyLoad();
        positionModels = studyLoadService.getPositions();
        fillCmbFaculty();
    }

    private void fillCmbFaculty() {
        departmentModels = studyLoadService.getDepartments();
        for (DepartmentModel department : departmentModels) {
            Comboitem comboitem = new Comboitem();
            comboitem.setLabel(department.getFulltitle());
            comboitem.setValue(department);
            cmbFaculty.getItems().add(comboitem);
        }
    }

    @Listen("onDoubleClick = #lbTeachers")
    public void teacherRowClick() {
        labelFIO.setValue("");
        if (lbTeachers.getSelectedItem() == null) {
            PopupUtil.showWarning("Выберите преподавателя!");
            return;
        }
        selectedTeacher = lbTeachers.getSelectedItem().getValue();
        labelFIO.setValue(selectedTeacher.toString());
        fillLbEmployment(selectedTeacher);
        lbTeachers.getSelectedItem().setSelected(false);
    }

    private void fillLbEmployment(TeacherModel selectTeacher) {
        List<EmploymentModel> list = studyLoadService.getEmployment(selectTeacher, (String) cmbFaculty.getValue());
        Double maxLoad = studyLoadService.getMaxload(selectTeacher);
        Double sumLoad = studyLoadService.getSumLoad(selectTeacher);
        double deviation = maxLoad - sumLoad;
        employmentModels = new ArrayList<>();
        employmentModels.add(list.get(0));
        ListModelList<EmploymentModel> employmentListModelList = new ListModelList<>(employmentModels);
        lbEmployment.setModel(employmentListModelList);
        lbEmployment.renderAll();
        Listitem item = lbEmployment.getItems().get(0);
        Listcell cellDeviation = (Listcell) item.getChildren().get(5);
        if (deviation < 0) {
            ((Doublebox)cellDeviation.getChildren().get(0)).setStyle("background: red; color:white");
        }
        ((Doublebox)cellDeviation.getChildren().get(0)).setValue(deviation);
        Listcell cellMaxLoad = (Listcell) item.getChildren().get(6);
        ((Doublebox)cellMaxLoad.getChildren().get(0)).setValue(maxLoad);
    }

    public void fillLbVacancy() {
        vacancyModels = studyLoadService.getVacancy();
        ListModelList<VacancyModel> vacancyListModelList = new ListModelList<>(vacancyModels);
        lbVacancy.setModel(vacancyListModelList);
        lbVacancy.renderAll();
    }

    @Listen("onClick = #btnSaveEmployment")
    public void saveEmploymentClick() {
        Listitem item = lbEmployment.getItems().get(0);
        Listcell cellByworker = (Listcell) item.getChildren().get(1);
        Combobox comboboxByworker = (Combobox) cellByworker.getChildren().get(0);
        ByworkerModel byworkerModel = comboboxByworker.getSelectedItem().getValue();
        Long idByworker = byworkerModel.getIdByworker();
        Listcell cellPosition = (Listcell) item.getChildren().get(2);
        Combobox comboboxPosition = (Combobox) cellPosition.getChildren().get(0);
        PositionModel position = comboboxPosition.getSelectedItem().getValue();
        Long idPosition = position.getIdPosition();
        Listcell cellWagerate = (Listcell) item.getChildren().get(3);
        Double doubleWagerate = ((Doublebox) cellWagerate.getChildren().get(0)).getValue();
        Listcell cellWagerateTime = (Listcell) item.getChildren().get(4);
        Double doubleWagerateTime = ((Doublebox) cellWagerateTime.getChildren().get(0)).getValue();
        studyLoadService.updateEmployment(selectedTeacher.getId_employee(), idByworker, idPosition, doubleWagerate, doubleWagerateTime, selectedDepartmentModel.getIdDepartment());
        PopupUtil.showInfo("Данные успешно обновлены!");
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
            PopupUtil.showWarning("Выберите вакансию для удаления!");
        }

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
            PopupUtil.showWarning("Выберите преподавателя, которого хотите удалить!");
            return;
        }
        TeacherModel selectedTeacher = lbTeachers.getSelectedItem().getValue();
        if (studyLoadService.removeRate(selectedTeacher.getId_employee(), selectedDepartmentModel.getIdDepartment())) {
            updateLbTeachers();
            PopupUtil.showInfo("Сотрудник был успешно удалён.");
        }
        else
            PopupUtil.showError("Ошибка удаления преподавателя");
    }

    @Listen("onClick = #btnShowPdfAssignmentsTabs")
    public void showAssignmentsPDF() {
        JasperReport jasperReport = new ReportService().getJasperForAssignments("/studyLoad/assigmentsPDF.jasper",assignmentModels,((DepartmentModel) cmbFaculty.getSelectedItem().getValue()).getFulltitle());
        jasperReport.showPdf();
    }

    @Listen("onClick = #btnDownloadExcelAssignmentsTabs")
    public void showAssignmentsExcel() {
        AMedia aMedia = new AMedia("Поручения  (" + DateConverter.convertDateToString(new Date()) + ").xls",
                "xls", "application/xls", new ReportService().getXlsxForAssignments(assignmentModels));
        Filedownload.save(aMedia);
    }

    @Listen("onClick = #btnSaveAssignmentsTabs")
    public void saveAssignments() {
         List<Listitem> listitems = lbAssignments.getItems();
         for(int i = 0; i < listitems.size(); i++){
            Listitem listitem = listitems.get(i);
             AssignmentModel valueListItem = listitem.getValue();
             String assignment = ((Textbox)listitem.getChildren().get(9).getFirstChild()).getValue();
             if (!assignment.equals("") && !assignment.equals(valueListItem.getAssignment())) {
                 if(studyLoadService.upsertRequests(valueListItem.getId_link_group_semester().longValue(),valueListItem.getId_link_employee_subject_group().longValue(), assignment)){
                     assignmentModels.get(i).setAssignment(assignment);
                 }
             }
         }
    }
    @Listen("onClick = #btnFillRate")
    public void fillRateClick()
    {
        if (lbVacancy.getSelectedItems().isEmpty()) {
            PopupUtil.showWarning("Выберите вакансию, которую хотите заполнить!");
            return;
        }
        VacancyModel selectedVacancy = lbVacancy.getSelectedItem().getValue();
        Long idPosition = null;
        for (PositionModel position : positionModels) {
            if(selectedVacancy.getRolename().equals(position.getPositionName())) {
                idPosition = position.getIdPosition();
                break;
            }
        }
        Map arg = new HashMap();
        arg.put("idVacancy", vacancyModels.get(lbVacancy.getSelectedIndex()).getId_vacancy());
        arg.put("idPosition", idPosition);
        arg.put("idDepartment", selectedDepartmentModel.getIdDepartment());
        arg.put("rate", selectedVacancy.getWagerate());
        arg.put("teacherModels", teacherModels);
        arg.put("indexPageCtrl", this);
        Window win = (Window) Executions.createComponents("window/winFillVacancyDialog.zul", null, arg);
        win.doModal();
    }

    public void fillLbAssignment() {
        lbAssignments.getItems().clear();
        assignmentModels = studyLoadService.getAssignments(56L, ((DepartmentModel) cmbFaculty.getSelectedItem().getValue()).getIdDepartment());
        ListModelList<AssignmentModel> assignmentModelListModelList = new ListModelList<>(assignmentModels);
        lbAssignments.setModel(assignmentModelListModelList);
        lbAssignments.renderAll();
    }
}

