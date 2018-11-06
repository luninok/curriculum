package org.edec.studyLoad.ctrl;


import javafx.scene.control.ComboBox;
import org.edec.commission.model.SubjectDebtModel;
import org.edec.model.SemesterModel;
import org.edec.studyLoad.model.TeacherModel;
import org.edec.studyLoad.service.impl.StudyLoadServiceImpl;
import org.edec.studyLoad.service.StudyLoadService;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.Executions;
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
    private Grid gridTeachers;
    @Wire
    private Combobox cmbFaculty;

    private StudyLoadService studyLoadService = new StudyLoadServiceImpl();

    protected void fill() {

        fillTable();
        /*Clients.showBusy("Загрузка данных");
        Events.echoEvent("onLater", vbCalendar, null);
        Events.echoEvent("onFirstRun", vbCalendar, null);
        Events.echoEvent("onLater", cmbSemesterForTabDay, null);
        Events.echoEvent("onLater", cmbGroupForTabDay, null);*/

    }

    private void fillTable() {

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

            gridTeachers.getRows().appendChild(row);
        }*/
        //Clients.showBusy(gridTeachers, "Загрузка данных из БД");
        //Events.echoEvent("onLater", gridTeachers, null);
    }

    @Listen("onChange = #cmbFaculty")
    public void laterForTeachers() {
        gridTeachers.getRows().getChildren().clear();
        List<TeacherModel> list = studyLoadService.getTeachers((String) cmbFaculty.getValue());

        for (TeacherModel teacher : list) {
            Row row = new Row();
            new Label(teacher.getFamily()).setParent(row);
            new Label(teacher.getName()).setParent(row);
            new Label(teacher.getPatronymic()).setParent(row);

            gridTeachers.getRows().appendChild(row);
        }

        /*lbShowCommission.setModel(new ListModelList<>(list));
        lbShowCommission.renderAll();
        Clients.clearBusy(lbShowCommission);*/
    }

    @Listen("onClick = #addRateBtn")
    public void openWinRateStructure() {
        Map arg = new HashMap();
        Window win = (Window) Executions.createComponents("window/winRateDialog.zul", null, arg);
        win.doModal();
    }

    @Listen("onClick = #addVacancyBtn")
    public void openWinVacancyStructure() {
        Map arg = new HashMap();
        Window win = (Window) Executions.createComponents("window/winVacancyDialog.zul", null, arg);
        win.doModal();
    }

    @Listen("onClick = #fillRateBtn")
    public void openWinFillRateStructure() {
        Map arg = new HashMap();
        Window win = (Window) Executions.createComponents("window/winRateDialog.zul", null, arg);
        win.doModal();
    }
}
