package org.edec.contingentMovement.ctrl;

import org.edec.commons.component.LbSearchStudent;
import org.edec.commons.model.GroupDirectionModel;
import org.edec.commons.renderer.GroupRenderer;
import org.edec.contingentMovement.ctrl.renderer.StudentRanderer;
import org.edec.commons.model.StudentGroupModel;
import org.edec.contingentMovement.ctrl.renderer.RatingRenderer;
import org.edec.contingentMovement.model.ResitRatingModel;
import org.edec.contingentMovement.service.ContingentMovementService;
import org.edec.contingentMovement.service.IndividualCurrService;
import org.edec.contingentMovement.service.ResitService;
import org.edec.contingentMovement.service.impl.ContingentMovementImpl;
import org.edec.contingentMovement.service.impl.IndividualCurrImpl;
import org.edec.contingentMovement.service.impl.ResitImpl;
import org.edec.model.GroupModel;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.DialogUtil;
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

    @Wire
    private Combobox cmbInst, cmbFos;
    @Wire
    private Hbox hbFilters;
    @Wire
    private Listbox lbTab1Student, lbIndCurGroup, lbIndCurStudent;
    @Wire
    private Vbox vbInst, vbFos;
    @Wire
    private Tab tabSearchStudent, tabTransferStudent, tabRestoreStudent;
    @Wire
    private Textbox tbTab1Fio, tbTab1Group, tbTab1Recordbook;
    @Wire
    private Radio rbIKIT, rbSFU;
    @Wire
    private Button rbExternal;
    @Wire
    private Checkbox chDetailSemester;

    /**
     * Вкладка индивидуальный УП
     **/
    @Wire
    private Checkbox chIndCurrExternalStudent;
    @Wire
    private Combobox cmbStudentFrom, cmbIndCurrSelectGroup;
    @Wire
    private Datebox dbIndCurrDatePass;
    @Wire
    private Hbox hbIndCurrChooseStudent;
    @Wire
    private LbSearchStudent lbIndCurrSearchStudent;
    @Wire
    private Listbox lbIndCurrStudentSubject, lbIndCurrGroupSubject;
    @Wire
    private Textbox tbIndCurrFioStudent;

    //Сервисы
    private ComponentService componentService = new ComponentServiceESOimpl();
    private ContingentMovementService contingentMovementService = new ContingentMovementImpl();
    private IndividualCurrService individualCurrService = new IndividualCurrImpl();
    private JasperReportService jasperReportService = new JasperReportService();
    private ResitService resitService = new ResitImpl();

    //Переменные
    private Integer fos;
    private Long idSem, idInst;

    private Boolean innerSearch = true;

    private List<ResitRatingModel> resitSubjects;
    private List<ResitRatingModel> studentRatings;

    @Override
    protected void fill () {
        lbTab1Student.setItemRenderer(new StudentRanderer(this, currentModule, innerSearch));
        componentService.fillCmbInst(cmbInst, vbInst, currentModule.getDepartments());
        componentService.fillCmbFormOfStudy(cmbFos, vbFos, currentModule.getFormofstudy());
        if (!vbInst.isVisible() && !vbFos.isVisible()) {
            hbFilters.setVisible(false);
        }
        Events.echoEvent("onSelect", cmbInst, null);
        lbIndCurrSearchStudent.setCheckmark(true);
        cmbIndCurrSelectGroup.setItemRenderer(new GroupRenderer());
        lbIndCurrStudentSubject.setItemRenderer(new RatingRenderer(false));
    }

    @Listen("onSelect = #cmbInst; onSelect = #cmbFos")
    public void selectInstAndFos () {
        if (cmbInst.getSelectedItem() == null || cmbFos.getSelectedItem() == null) {
            PopupUtil.showWarning("Пожалуйста выберите институт и семестр");
            return;
        }
        InstituteModel inst = cmbInst.getSelectedItem().getValue();
        FormOfStudy fos = cmbFos.getSelectedItem().getValue();
        idInst = inst.getIdInst();
        this.fos = fos.getType();
        lbIndCurrSearchStudent.setIdInst(idInst);
        lbIndCurrSearchStudent.setFos(this.fos);
        idSem = contingentMovementService.getCurrentSem(idInst, this.fos);
    }

    @Listen("onOK=#tbTab1Fio; onOK=#tbTab1Recordbook; onOK=#tbTab1Group; onCheck = #chDetailSemester")
    public void searchStudents () {
        if (rbIKIT.isSelected() || rbSFU.isSelected()) {
            innerSearch = true;
            Clients.showBusy(lbTab1Student, "Загрузка данных");
            Events.echoEvent("onLater", lbTab1Student, null);
        }
        /*
        else if (rbExternal.isSelected()){
            innerSearch=false;
            Clients.showNotification("Пытаюсь создать нового");
        }
        */
    }

    @Listen("onClick=#rbExternal")
    public void createNewExternalStudent () {
        innerSearch = false;
        Map<String, Object> arg = new HashMap<>();
        arg.put(WinRecoveryCtrl.SELECTED_STUDENT, null);
        arg.put(WinRecoveryCtrl.MAIN_PAGE, this);
        arg.put(WinRecoveryCtrl.ACTION_PAGE, WinRecoveryCtrl.Actions.NEW);

        ComponentHelper.createWindow("winRecovery.zul", "WinRecovery", arg).doModal();
    }

    @Listen("onLater = #lbTab1Student")
    public void laterLbTab1Student (Event event) {
        if (rbIKIT.isSelected()) { //Пытаемся найти в ИКИТ
            innerSearch = true;
            if (chDetailSemester.isChecked())
            {
                lbTab1Student.setModel(new ListModelList<>(
                        contingentMovementService.getStudentsByFilterDetail(tbTab1Fio.getValue(), tbTab1Recordbook.getValue(),
                                tbTab1Group.getValue()
                        )));
            }else{
                lbTab1Student.setModel(new ListModelList<>(
                        contingentMovementService.getStudentsByFilter(tbTab1Fio.getValue(), tbTab1Recordbook.getValue(),
                                tbTab1Group.getValue()
                        )));
            }

            lbTab1Student.setItemRenderer(new StudentRanderer(this, currentModule, innerSearch));
            lbTab1Student.renderAll();
            if (event.getData() != null) {
                int index = (int) event.getData();
                lbTab1Student.renderItem(lbTab1Student.getItemAtIndex(index));
                lbTab1Student.setSelectedIndex(index);
            }
            Clients.clearBusy(lbTab1Student);
        } else if (rbSFU.isSelected()) { //Если пытаемся найти в Шахтах
            innerSearch = false;
            lbTab1Student.setModel(new ListModelList<>(
                    contingentMovementService.getStudentsByFilterInDBO(tbTab1Fio.getValue(), tbTab1Recordbook.getValue(),
                                                                       tbTab1Group.getValue()
                    )));
            lbTab1Student.setItemRenderer(new StudentRanderer(this, currentModule, innerSearch));
            lbTab1Student.renderAll();
            Clients.clearBusy(lbTab1Student);
        }
    }

    //////////////////////////////
    ///~~ Индивидуальный план ////
    @Listen("onSelect = #tabIndividualCurriculum")
    public void selectTabIndividCurr () {
        if (!checkOnChooseInstAndFos()) {
            return;
        }
        cmbIndCurrSelectGroup.setModel(new ListModelList<>(individualCurrService.getGroupDirectionBySemester(idSem)));
    }

    @Listen("onClick = #btnIndCurrPrintReport")
    public void printIndCurrReport () {
        if (cmbIndCurrSelectGroup.getSelectedItem() == null || dbIndCurrDatePass.getValue() == null) {
            DialogUtil.exclamation("Выберите группу и заполнените дату сдачи");
            return;
        }
        GroupDirectionModel selectedGroup = cmbIndCurrSelectGroup.getSelectedItem().getValue();
        String fio, shortFio;
        List<ResitRatingModel> subjects;
        if (chIndCurrExternalStudent.isChecked()) {
            String[] splitFio = tbIndCurrFioStudent.getValue().split(" ");
            if (splitFio.length < 3) {
                DialogUtil.exclamation("Указано неверное ФИО! Введите ФИО через пробелы.");
                return;
            }
            fio = tbIndCurrFioStudent.getValue();
            shortFio = splitFio[0] + " " + splitFio[1].substring(0, 1) + ". " + splitFio[2].substring(0, 1) + ".";
            subjects = resitSubjects;
        } else {
            if (lbIndCurrSearchStudent.getSelectedItem() == null) {
                DialogUtil.exclamation("Выберите студента");
                return;
            }
            StudentGroupModel selectedStudent = lbIndCurrSearchStudent.getSelectedItem().getValue();
            fio = selectedStudent.getFio();
            shortFio = selectedStudent.getShortFioInverse();
            subjects = new ArrayList<>();
            for (Listitem li : lbIndCurrGroupSubject.getItems()) {
                ResitRatingModel subject = li.getValue();
                if (subject.getRating() == null || (subject.getRating() != 1 && subject.getRating() < 3)) {
                    subjects.add(subject);
                }
            }
        }
        jasperReportService.getContingentIndCurr(fio, shortFio, "Направление " + selectedGroup.getDirectioncode(),
                                                 selectedGroup.getGroupname(), dbIndCurrDatePass.getValue(), subjects
        ).showPdf();
    }

    @Listen("onSelect = #cmbIndCurrSelectGroup; onCheck = #chIndCurrExternalStudent;")
    public void selectGroupInIndCurr () {
        if (!checkOnChooseInstAndFos()) {
            return;
        }
        if (cmbIndCurrSelectGroup.getSelectedItem() == null) {
            PopupUtil.showWarning("Выберите группу!");
            return;
        }
        Clients.showBusy(lbIndCurrGroupSubject, "Загрузка данных");
        Events.echoEvent("onLater", lbIndCurrGroupSubject, null);
    }

    @Listen("onLater = #lbIndCurrGroupSubject")
    public void laterOnSearchGroupSubject () {
        GroupModel selectedGroup = cmbIndCurrSelectGroup.getSelectedItem().getValue();
        /*if (!chIndCurrExternalStudent.isChecked() && cmbIndCurrSelectGroup.getSelectedItem() != null &&
            lbIndCurrSearchStudent.getSelectedItem() != null) {

            lbIndCurrGroupSubject.setItemRenderer(new RatingRenderer(studentRatings));
        } else {
            lbIndCurrGroupSubject.setItemRenderer(new RatingRenderer());
        }*/
        lbIndCurrGroupSubject.setItemRenderer(new RatingRenderer(true));

        resitSubjects = individualCurrService.getSubjectByGroup(selectedGroup.getIdDG());
        //Если не был выбран студент из института, то перезачет делать не нужно
        if (studentRatings != null) {
            resitService.autoResit(studentRatings, resitSubjects);
        }
        lbIndCurrGroupSubject.setModel(new ListModelList<>(resitSubjects));
        lbIndCurrGroupSubject.renderAll();
        Clients.clearBusy(lbIndCurrGroupSubject);
    }

    @Listen("onClick = #btnIndCurrOpenProtocol")
    public void openIndCurrProtocol () {
        Map<String, Object> arg = new HashMap<>();
        GroupDirectionModel selectedGroup = cmbIndCurrSelectGroup.getSelectedItem().getValue();
        arg.put(WinIndCurrProtocol.ARG_SELECTED_DIRECTION,
                selectedGroup.getDirectioncode() + " \"" + selectedGroup.getDirectiontitle() + "\""
        );
        List<ResitRatingModel> resitSubjects = new ArrayList<>();
        for (Listitem li : lbIndCurrGroupSubject.getItems()) {
            ResitRatingModel rating = li.getValue();
            if (rating.getRating() != null && (rating.getRating() == 1 || rating.getRating() > 2)) {
                resitSubjects.add(rating);
            }
        }
        if (resitSubjects.size() == 0) {
            PopupUtil.showWarning("Нет перезачтеных дисциплин. Отчет невозможно построить");
            return;
        }

        arg.put(WinIndCurrProtocol.ARG_SELECTED_RESIST_SUBJECTS, resitSubjects);
        if (chIndCurrExternalStudent.isChecked()) {
            arg.put(WinIndCurrProtocol.ARG_SELECTED_FIO_STUDENT, tbIndCurrFioStudent.getValue());
        } else {
            if (lbIndCurrSearchStudent.getSelectedItem() == null) {
                PopupUtil.showWarning("Необходимо выбрать студента");
                return;
            }
            StudentGroupModel selectedStudent = lbIndCurrSearchStudent.getSelectedItem().getValue();
            arg.put(WinIndCurrProtocol.ARG_SELECTED_FIO_STUDENT, selectedStudent.getFio());
            arg.put(WinIndCurrProtocol.ARG_SELECTED_RECORDBOOK, selectedStudent.getRecordBook());
        }

        ComponentHelper.createWindow("/contingentMovement/winIndCurrProtocol.zul", "winIndCurrProtocol", arg).doModal();
    }

    @Listen("onClick = #lbIndCurrSearchStudent")
    public void selectedIndCurrSearchStudent () {
        if (lbIndCurrSearchStudent.getSelectedItem() == null) {
            return;
        }
        Clients.showBusy(lbIndCurrStudentSubject, "Загрузка данных");
        Events.echoEvent("onLater", lbIndCurrStudentSubject, null);
    }

    @Listen("onLater = #lbIndCurrStudentSubject")
    public void laterOnSearchStudentSubject () {
        StudentGroupModel selectedStudent = lbIndCurrSearchStudent.getSelectedItem().getValue();
        studentRatings = individualCurrService.getRatingByStudentAndGroup(selectedStudent.getIdStudentCard(), selectedStudent.getIdDG());
        lbIndCurrStudentSubject.setModel(new ListModelList<>(studentRatings));
        lbIndCurrStudentSubject.renderAll();
        Clients.clearBusy(lbIndCurrStudentSubject);
        if (cmbIndCurrSelectGroup.getSelectedItem() != null) {
            Clients.showBusy(lbIndCurrGroupSubject, "Загрузка данных");
            Events.echoEvent("onLater", lbIndCurrGroupSubject, null);
        }
    }

    private boolean checkOnChooseInstAndFos () {
        if (idSem == null) {
            DialogUtil.exclamation("Выберите институт и форму обучения!");
            return false;
        }
        return true;
    }

    public Long getIdSem () {
        return idSem;
    }
}
