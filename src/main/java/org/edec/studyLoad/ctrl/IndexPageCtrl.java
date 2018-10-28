package org.edec.studyLoad.ctrl;


import org.edec.studyLoad.model.TeacherModel;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;

public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Grid gridTeachers;

       protected void fill() {

        fillTable();
        /*Clients.showBusy("Загрузка данных");
        Events.echoEvent("onLater", vbCalendar, null);
        Events.echoEvent("onFirstRun", vbCalendar, null);
        Events.echoEvent("onLater", cmbSemesterForTabDay, null);
        Events.echoEvent("onLater", cmbGroupForTabDay, null);*/

    }

    private void fillTable() {

        List<TeacherModel> teacherModelList = new ArrayList<>();
        teacherModelList.add(new TeacherModel("d", "b", "c"));
        teacherModelList.add(new TeacherModel("f", "d", "s"));
        teacherModelList.add(new TeacherModel("f", "d", "s"));
        teacherModelList.add(new TeacherModel("f", "d", "s"));
        teacherModelList.add(new TeacherModel("f", "d", "s"));
        teacherModelList.add(new TeacherModel("f", "d", "s"));
        teacherModelList.add(new TeacherModel("f", "d", "s"));

        for (TeacherModel teacherModel: teacherModelList) {
            Row row = new Row();
            new Label(teacherModel.getLastName()).setParent(row);
            new Label(teacherModel.getFirstName()).setParent(row);
            new Label(teacherModel.getMiddleName()).setParent(row);

            gridTeachers.getRows().appendChild(row);
        }
    }

}
