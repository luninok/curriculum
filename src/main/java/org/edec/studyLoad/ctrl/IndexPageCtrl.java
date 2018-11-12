package org.edec.studyLoad.ctrl;


import javafx.scene.control.ComboBox;
import org.edec.commission.model.SubjectDebtModel;
import org.edec.model.SemesterModel;
import org.edec.studyLoad.model.TeacherModel;
import org.edec.studyLoad.service.impl.StudyLoadServiceImpl;
import org.edec.studyLoad.service.StudyLoadService;
import org.edec.utility.zk.CabinetSelector;
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

    @Wire
    private Listbox lbTeachers, lbVacancy;
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

    protected void fill() {

        fillCmbFaculty();
        /*Clients.showBusy("Загрузка данных");
        Events.echoEvent("onLater", vbCalendar, null);
        Events.echoEvent("onFirstRun", vbCalendar, null);
        Events.echoEvent("onLater", cmbSemesterForTabDay, null);
        Events.echoEvent("onLater", cmbGroupForTabDay, null);*/

    }

    private void fillCmbFaculty() {

        List<String> list = studyLoadService.getDepartments();
        for (String department : list) {
            Comboitem comboitem = new Comboitem();
            comboitem.setLabel(department);

            cmbFaculty.getItems().add(comboitem);
        }
        /*List<TeacherModel> teacherModelList = new ArrayList<>();
        teacherModelList.add(new TeacherModel("d", "b", "c"));
        teacherModelList.add(new TeacherModel("f", "d", "s"));
        teacherModelList.add(new TeacherModel("f", "d", "s"));
        teacherModelList.add(new TeacherModel("f", "d", "s"));
        teacherModelList.add(new TeacherModel("f", "d", "s"));
        teacherModelList.add(new TeacherModel("f", "d", "s"));
        teacherModelList.add(new TeacherModel("f", "d", "s"));

        for (TeacherModel teacherModel : teacherModelList) {
            Row row = new Row();
            new Label(teacherModel.getLastName()).setParent(row);
            new Label(teacherModel.getFirstName()).setParent(row);
            new Label(teacherModel.getMiddleName()).setParent(row);

            lbTeachers.getRows().appendChild(row);
        }*/
        //Clients.showBusy(lbTeachers, "Загрузка данных из БД");
        //Events.echoEvent("onLater", lbTeachers, null);
    }

    @Listen("onSelect = #lbTeachers")
    public void teacherRowClick() {
        labelFIO.setValue("");
        col1.setVisible(true);
        col2.setVisible(false);
        TeacherModel selectTeacher = lbTeachers.getSelectedItem().getValue();
        labelFIO.setValue(selectTeacher.getFamily() + " " + selectTeacher.getName() + " " + selectTeacher.getPatronymic());
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
        lbTeachers.clearSelection();
        List<TeacherModel> list = studyLoadService.getTeachers((String) cmbFaculty.getValue());
        for (TeacherModel teacher : list) {
            Listitem listitem = new Listitem();
            listitem.setValue(teacher); // Вот она новая строчка!
            new Listcell(teacher.getFamily()).setParent(listitem);
            new Listcell(teacher.getName()).setParent(listitem);
            new Listcell(teacher.getPatronymic()).setParent(listitem);
            Listcell cell = new Listcell();
            cell.setParent(listitem);
            lbTeachers.getItems().add(listitem);
        }

        /*lbShowCommission.setModel(new ListModelList<>(list));
        lbShowCommission.renderAll();
        Clients.clearBusy(lbShowCommission);*/
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
        Window win = (Window) Executions.createComponents("window/winVacancyDialog.zul", null, arg);
        win.doModal();
    }

    @Listen("onClick = #btnFillRate")
    public void openWinFillRateStructure() {
        Map arg = new HashMap();
        Window win = (Window) Executions.createComponents("window/winRateDialog.zul", null, arg);
        win.doModal();
    }
}
