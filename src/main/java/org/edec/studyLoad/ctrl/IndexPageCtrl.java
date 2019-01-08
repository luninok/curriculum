package org.edec.studyLoad.ctrl;


import org.edec.main.model.DepartmentModel;
import org.edec.studyLoad.ctrl.renderer.AssignmentRenderer;
import org.edec.studyLoad.ctrl.renderer.EmploymentRenderer;
import org.edec.studyLoad.ctrl.renderer.VacancyRenderer;
import org.edec.studyLoad.ctrl.renderer.TeachersRenderer;
import org.edec.studyLoad.manager.EntityManagerStudyLoad;
import org.edec.studyLoad.ctrl.renderer.StudyLoadRenderer;
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
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.*;

public class IndexPageCtrl extends CabinetSelector {
    @Wire
    private Listbox lbTeachers, lbVacancy, lbAssignments, lbEmployment, lbStudyLoad;
    @Wire
    private Label labelFIO;
    @Wire
    private Combobox cmbFaculty;
    @Wire
    private Vbox col1;
    @Wire
    private Hbox hbTeacherCards;
    private String selectFIO = "";

    private StudyLoadService studyLoadService = new StudyLoadServiceImpl();
    private Combobox selectedPosition;
    private Spinner selectedRate;
    private List<VacancyModel> vacancyModels = new ArrayList<>();
    private List<TeacherModel> teacherModels = new ArrayList<>();
    private List<AssignmentModel> assignmentModels = new ArrayList<>();
    private List<DepartmentModel> departmentModels = new ArrayList<>();
    private List<PositionModel> positionModels = new ArrayList<>();
    private List<StudyLoadModel> studyLoadModels = new ArrayList<>();
    private DepartmentModel selectedDepartmentModel = new DepartmentModel();
    private TeacherModel selectedTeacher;
    private TeacherModel selectCardTeacher;
    List<EmploymentModel> employmentModels = new ArrayList<>();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        lbVacancy.setItemRenderer(new VacancyRenderer());
        lbTeachers.setItemRenderer(new TeachersRenderer());
        lbEmployment.setItemRenderer(new EmploymentRenderer());
        lbStudyLoad.setItemRenderer(new StudyLoadRenderer());
        lbAssignments.setItemRenderer(new AssignmentRenderer());
       // fillLbVacancy();
    }

    protected void fill() {
        EntityManagerStudyLoad ent = new EntityManagerStudyLoad();
        positionModels = studyLoadService.getPositions();
        fillCmbFaculty();

    }

    public void fillLbAssignment() {
        lbAssignments.getItems().clear();
        assignmentModels = studyLoadService.getAssignments(56L, ((DepartmentModel) cmbFaculty.getSelectedItem().getValue()).getIdDepartment());
        ListModelList<AssignmentModel> assignmentModelListModelList = new ListModelList<>(assignmentModels);
        lbAssignments.setModel(assignmentModelListModelList);
        lbAssignments.renderAll();
    }

    private void teacherCardRenderer(TeacherModel teacherModel) {
        Label fioLabel = new Label();
        List<SumLessonModel> sumLessonModels = studyLoadService.getSumLesson(teacherModel, selectedDepartmentModel.getIdDepartment());
        Label hoursLectionLabel = new Label();
        Label hoursPracticLabel = new Label();
        Label hoursLaborLabel = new Label();
        Label otherHoursLabel = new Label();
        Label timewagerateLabel = new Label();
        Label deviationLabel = new Label();
        Double maxLoad, sumLoad;
        if (teacherModel.getFamily().contains("Вакансия") == false){
            List<EmploymentModel> employmentModels = studyLoadService.getEmployment(teacherModel, selectedDepartmentModel.getFulltitle());
            fioLabel.setValue(employmentModels.get(0).getShorttitleByworker() + teacherModel.toString());
            timewagerateLabel.setValue("Часов: " + String.valueOf(employmentModels.get(0).getTime_wagerate()));
            maxLoad = studyLoadService.getMaxload(teacherModel);
            sumLoad = studyLoadService.getSumLoad(teacherModel);
            if(sumLessonModels.size() != 0) {
                hoursLectionLabel.setValue("Лек.: " + String.valueOf(sumLessonModels.get(0).getHourslection()));
                hoursPracticLabel.setValue("Прак.: " + String.valueOf(sumLessonModels.get(0).getHourspractic()));
                hoursLaborLabel.setValue("Лаб.: " + String.valueOf(sumLessonModels.get(0).getHourslabor()));
                Double otherHours = employmentModels.get(0).getTime_wagerate() - (sumLessonModels.get(0).getHourslection() + sumLessonModels.get(0).getHourspractic() + sumLessonModels.get(0).getHourslabor());
                otherHoursLabel.setValue("Другое: " + String.valueOf(otherHours));
            }
            else {
                hoursLectionLabel.setValue("Лек.: 0");
                hoursPracticLabel.setValue("Прак.: 0");
                hoursLaborLabel.setValue("Лаб.: 0");
                Double otherHours = employmentModels.get(0).getTime_wagerate();
                otherHoursLabel.setValue("Другое: " + String.valueOf(otherHours));
            }
        }
        else {
            fioLabel.setValue(teacherModel.getFamily());
            maxLoad = 0.0;
            sumLoad = 0.0;
            if(sumLessonModels.size() != 0) {
                hoursLectionLabel.setValue("Лек.: " + String.valueOf(sumLessonModels.get(0).getHourslection()));
                hoursPracticLabel.setValue("Прак.: " + String.valueOf(sumLessonModels.get(0).getHourspractic()));
                hoursLaborLabel.setValue("Лаб.: " + String.valueOf(sumLessonModels.get(0).getHourslabor()));
                Double otherHours = sumLessonModels.get(0).getHourslection() + sumLessonModels.get(0).getHourspractic() + sumLessonModels.get(0).getHourslabor();
                otherHoursLabel.setValue("Другое: " + String.valueOf(otherHours));
                timewagerateLabel.setValue("Часов: " + String.valueOf(sumLessonModels.get(0).getHourslection() + sumLessonModels.get(0).getHourspractic() + sumLessonModels.get(0).getHourslabor()));
            }
            else {
                hoursLectionLabel.setValue("Лек.: 0");
                hoursPracticLabel.setValue("Прак.: 0");
                hoursLaborLabel.setValue("Лаб.: 0");
                otherHoursLabel.setValue("Другое: 0");
                timewagerateLabel.setValue("Часов: 0");
            }
        }
        Vbox cardVbox = new Vbox();
        cardVbox.setClass("card");
        cardVbox.setStyle("border: 1px solid #dcdcdc; padding: 5%;");
        cardVbox.setWidth("170px");
        cardVbox.setHeight("170px");
        cardVbox.addEventListener(Events.ON_CLICK, e -> {
            List<Vbox> divCardList = hbTeacherCards.getChildren();
            for(Vbox vbox: divCardList){
                vbox.setStyle("background:white;");
            }
            cardVbox.setStyle("background: linear-gradient(to bottom, #6C7A89 0%, #eeeeee 80%); padding: 5%;");
            selectCardTeacher = (TeacherModel)cardVbox.getAttribute("value");
        });
        Hbox hoursSubjectHbox = new Hbox();
        Image genderImg = new Image();
        if (teacherModel.getSex() == 0) {
            genderImg.setSrc("/imgs/woman.png");
            genderImg.setWidth("75px");
        } else {
            genderImg.setSrc("/imgs/man.png");
            genderImg.setWidth("75px");
        }
        Vbox hoursVbox = new Vbox();
        hoursVbox.appendChild(hoursLectionLabel);
        hoursVbox.appendChild(hoursPracticLabel);
        hoursVbox.appendChild(hoursLaborLabel);
        hoursSubjectHbox.appendChild(genderImg);
        hoursSubjectHbox.appendChild(hoursVbox);
        Vbox hoursOtherVbox = new Vbox();
        Hbox loadHoursHbox = new Hbox();
        double deviation = maxLoad - sumLoad;
        if(deviation < 0){
            deviationLabel.setStyle("background: red; color:white");
        }
        deviationLabel.setValue("Откл: " + String.valueOf(deviation));
        cardVbox.setAttribute("value", teacherModel);
        loadHoursHbox.appendChild(timewagerateLabel);
        loadHoursHbox.appendChild(deviationLabel);
        hoursOtherVbox.appendChild(otherHoursLabel);
        hoursOtherVbox.appendChild(loadHoursHbox);
        cardVbox.appendChild(fioLabel);
        cardVbox.appendChild(hoursSubjectHbox);
        cardVbox.appendChild(hoursOtherVbox);
        hbTeacherCards.appendChild(cardVbox);
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

    private void fillLbEmployment(TeacherModel selectTeacher) {
        List<EmploymentModel> list = studyLoadService.getEmployment(selectTeacher, selectedDepartmentModel.getFulltitle());
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
            ((Doublebox) cellDeviation.getChildren().get(0)).setStyle("background: red; color:white");
        }
        ((Doublebox) cellDeviation.getChildren().get(0)).setValue(deviation);
        Listcell cellMaxLoad = (Listcell) item.getChildren().get(6);
        ((Doublebox) cellMaxLoad.getChildren().get(0)).setValue(maxLoad);
    }

   public void fillLbVacancy() {
        vacancyModels = studyLoadService.getVacancy(selectedDepartmentModel.getIdDepartment());
        ListModelList<VacancyModel> vacancyListModelList = new ListModelList<>(vacancyModels);
        lbVacancy.setModel(vacancyListModelList);
        lbVacancy.renderAll();
    }

    public void fillLbStudyLoad() {
        studyLoadModels = studyLoadService.getStudyLoad(selectedDepartmentModel.getIdDepartment());
        ListModelList<StudyLoadModel> studyLoadListModelList = new ListModelList<>(studyLoadModels);
        lbStudyLoad.setModel(studyLoadListModelList);
        lbStudyLoad.renderAll();
    }

    @Listen("onClick = #btnFixTeacher")
    public void teacherFix() {
        if(cmbFaculty.getValue() == ""){
            PopupUtil.showWarning("Выберите кафедру!");
            return;
        }
        if(selectCardTeacher == null){
            PopupUtil.showWarning("Выберите преподавателя!");
            return;
        }
        if(lbStudyLoad.getSelectedIndex() == -1){
            PopupUtil.showWarning("Выберите дисциплину!");
            return;
        }
        StudyLoadModel model = studyLoadModels.get(lbStudyLoad.getSelectedIndex());
        String j = model.getFamily();
        if (model.getFamily() != null){
            PopupUtil.showWarning("Преподаватель уже закреплён за дисциплиной!");
            return;
        }
        studyLoadService.insertTeacherToTheDiscipline(selectCardTeacher,  model.getIdLGS(), model.getIdSubject());
        fillLbStudyLoad();
        PopupUtil.showInfo("Преподаватель успешно закреплён!");
        List<Vbox> divCardList = hbTeacherCards.getChildren();
        for(Vbox vbox: divCardList){
            vbox.setStyle("background:white;");
        }

    }

    @Listen("onClick = #btnDetacher")
    public  void teacherDetacher() {
        if(cmbFaculty.getValue() == ""){
            PopupUtil.showWarning("Выберите кафедру!");
            return;
        }
        StudyLoadModel model = studyLoadModels.get(lbStudyLoad.getSelectedIndex());
        if(model == null){
            PopupUtil.showWarning("Выберите дисциплину!");
            return;
        }
        studyLoadService.deleteTeacherToTheDiscipline(model.getIdEmployee(), model.getIdLGS(), model.getIdSubject());
        fillLbStudyLoad();
        PopupUtil.showInfo("Преподаватель успешно откреплён!");
    }

    @Listen("onDoubleClick = #lbTeachers")
    public void teacherRowClick() {
        if(cmbFaculty.getValue() == ""){
            PopupUtil.showWarning("Выберите кафедру!");
            return;
        }
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

    @Listen("onClick = #btnSaveEmployment")
    public void saveEmploymentClick() {
        if(cmbFaculty.getValue() == ""){
            PopupUtil.showWarning("Выберите кафедру!");
            return;
        }
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
        if(doubleWagerate > 1.5 || doubleWagerate < 0.1) {
            PopupUtil.showWarning("Такое значение ставки не существует!");
            return;
        }
        Listcell cellWagerateTime = (Listcell) item.getChildren().get(4);
        Double doubleWagerateTime = ((Doublebox) cellWagerateTime.getChildren().get(0)).getValue();
        employmentModels.get(0).setTime_wagerate(doubleWagerateTime);
        studyLoadService.updateEmployment(selectedTeacher.getId_employee(), idByworker, idPosition, doubleWagerate, doubleWagerateTime, selectedDepartmentModel.getIdDepartment());
        hbTeacherCards.getChildren().clear();
        for (TeacherModel teacherModel : teacherModels) {
            teacherCardRenderer(teacherModel);
        }
        PopupUtil.showInfo("Данные успешно обновлены!");
        fillLbEmployment(selectedTeacher);
    }

    @Listen("onClick = #btnRemoveVacancy")
    public void removeVacancyClick() {
        if(cmbFaculty.getValue() == ""){
            PopupUtil.showWarning("Выберите кафедру!");
            return;
        }
        if (lbVacancy.getSelectedItem() != null) {
            VacancyModel vacancyModel = lbVacancy.getSelectedItem().getValue();
            studyLoadService.deleteVacancy(vacancyModel.getId_vacancy());
            fillLbVacancy();
            PopupUtil.showInfo("Вакансия успешно удалена");
        } else {
            PopupUtil.showWarning("Выберите вакансию для удаления!");
        }

    }

   /* @Listen("onClick = #hbTeacherCards")
    public void fixTeacher(Event event) {
        Component target = event.getTarget();
        String tt = target.getParent().getClass().getName();
        if (target.getParent().getClass().getName() == "org.zkoss.zul.Div"){
               ((Hbox)target).setStyle("background: red;");
        }

    } */

    @Listen("onChange = #cmbFaculty")
    public void updateLbTeachers() {
        selectedDepartmentModel = cmbFaculty.getSelectedItem().getValue();
        lbVacancy.clearSelection();
        vacancyModels.clear();
        fillLbVacancy();
        lbTeachers.clearSelection();
        teacherModels.clear();
        teacherModels = studyLoadService.getTeachers((String) cmbFaculty.getValue());
        ListModelList<TeacherModel> teacherListModelList = new ListModelList<>(teacherModels);
        lbTeachers.setModel(teacherListModelList);
        lbTeachers.renderAll();
        fillLbAssignment();
        hbTeacherCards.getChildren().clear();
        for (TeacherModel teacherModel : teacherModels) {
            teacherCardRenderer(teacherModel);
        }
        if (vacancyModels != null) {
            for (VacancyModel vacancyModel : vacancyModels) {
                teacherCardRenderer(vacancyModel.ToTeacherModel());
            }
        }
        fillLbStudyLoad();
    }

    @Listen("onClick = #btnAddRate")
    public void openWinRateStructure() {
        if(cmbFaculty.getValue() == ""){
            PopupUtil.showWarning("Выберите кафедру!");
            return;
        }
        Map arg = new HashMap();
        arg.put("teacherModels", teacherModels);
        arg.put("idDepartment", selectedDepartmentModel.getIdDepartment());
        arg.put("indexPageCtrl", this);
        Window win = (Window) Executions.createComponents("window/winRateDialog.zul", null, arg);
        win.doModal();
    }

    @Listen("onClick = #btnAddVacancy")
    public void openWinVacancyStructure() {
        if(cmbFaculty.getValue() == ""){
            PopupUtil.showWarning("Выберите кафедру!");
            return;
        }
        Runnable updateLbVacancy = this::fillLbVacancy;
        Map<String, Object> arg = new HashMap<>();
        arg.put("fillLbVacancy", updateLbVacancy);
        arg.put("id_department", selectedDepartmentModel.getIdDepartment());
        ComponentHelper.createWindow("window/winVacancyDialog.zul", "winVacancyDialog", arg).doModal();
}

    @Listen("onClick = #btnChangeVacancy")
    public void changeVacancyClick() {
        if(cmbFaculty.getValue() == ""){
            PopupUtil.showWarning("Выберите кафедру!");
            return;
        }
        if (lbVacancy.getSelectedItem() != null) {
            Runnable updateLbVacancy = this::fillLbVacancy;
            Map<String, Object> arg = new HashMap<>();
            arg.put("fillLbVacancy", updateLbVacancy);
            arg.put("vacancy", lbVacancy.getSelectedItem().getValue());
            ComponentHelper.createWindow("window/winVacancyDialog.zul", "winVacancyDialog", arg).doModal();
        } else {
            PopupUtil.showWarning("Выберите вакансию!");
        }
    }

    @Listen("onClick = #btnRemoveRate")
    public void removeRate() {
        if(cmbFaculty.getValue() == ""){
            PopupUtil.showWarning("Выберите кафедру!");
            return;
        }
        if (lbTeachers.getSelectedItems().isEmpty()) {
            PopupUtil.showWarning("Выберите преподавателя, которого хотите удалить!");
            return;
        }
        TeacherModel selectedTeacher = lbTeachers.getSelectedItem().getValue();
        if (studyLoadService.removeRate(selectedTeacher.getId_employee(), selectedDepartmentModel.getIdDepartment())) {
            updateLbTeachers();
            PopupUtil.showInfo("Сотрудник был успешно удалён.");
        } else
            PopupUtil.showError("Ошибка удаления преподавателя");
    }

    @Listen("onClick = #btnShowPdfAssignmentsTabs")
    public void showAssignmentsPDF() {
        if(cmbFaculty.getValue() == ""){
            PopupUtil.showWarning("Выберите кафедру!");
            return;
        }
        JasperReport jasperReport = new ReportService().getJasperForAssignments("/studyLoad/assigmentsPDF.jasper",assignmentModels,((DepartmentModel) cmbFaculty.getSelectedItem().getValue()).getFulltitle());
        jasperReport.showPdf();
    }

    @Listen("onClick = #btnDownloadExcelAssignmentsTabs")
    public void showAssignmentsExcel() {
        if(cmbFaculty.getValue() == ""){
            PopupUtil.showWarning("Выберите кафедру!");
            return;
        }
        AMedia aMedia = new AMedia("Поручения  (" + DateConverter.convertDateToString(new Date()) + ").xls",
                "xls", "application/xls", new ReportService().getXlsxForAssignments(assignmentModels));
        Filedownload.save(aMedia);
    }

    @Listen("onClick = #btnSaveAssignmentsTabs")
    public void saveAssignments() {
        if(cmbFaculty.getValue() == ""){
            PopupUtil.showWarning("Выберите кафедру!");
            return;
        }
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
        PopupUtil.showInfo("Данные успешно сохранены!");
    }
    @Listen("onClick = #btnFillRate")
    public void fillRateClick() {
        if(cmbFaculty.getValue() == ""){
            PopupUtil.showWarning("Выберите кафедру!");
            return;
        }
        if (lbVacancy.getSelectedItems().isEmpty()) {
            PopupUtil.showWarning("Выберите вакансию, которую хотите заполнить!");
            return;
        }
        VacancyModel selectedVacancy = lbVacancy.getSelectedItem().getValue();
        Long idPosition = null;
        for (PositionModel position : positionModels) {
            if (selectedVacancy.getRolename().equals(position.getPositionName())) {
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
}

