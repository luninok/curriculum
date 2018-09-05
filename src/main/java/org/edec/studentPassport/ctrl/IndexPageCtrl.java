package org.edec.studentPassport.ctrl;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.ModuleModel;
import org.edec.passportGroup.service.impl.ReportsServiceESO;
import org.edec.studentPassport.ctrl.renderer.StudentRanderer;
import org.edec.studentPassport.service.StudentPassportService;
import org.edec.studentPassport.service.impl.StudentPassportServiceESOimpl;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.zkoss.zk.ui.Executions.getCurrent;

/**
 * @author Max Dimukhametov
 */
public class IndexPageCtrl extends CabinetSelector {
    @Wire
    private Combobox cmbInst, cmbFormOfStudy, cmbSemester;

    @Wire
    private Groupbox gbFilter;

    @Wire
    private Listbox lbStudent;

    @Wire
    private Textbox tbFio, tbRecordbook, tbGroup;

    @Wire
    private Vbox vbInst, vbFormOfStudy;

    private StudentPassportService stPassportService = new StudentPassportServiceESOimpl();
    private ComponentService componentService = new ComponentServiceESOimpl();

    private ReportsServiceESO reports = new ReportsServiceESO();

    protected void fill () {
        lbStudent.setItemRenderer(new StudentRanderer(this, currentModule));
        componentService.fillCmbInst(cmbInst, vbInst, currentModule.getDepartments());
        componentService.fillCmbFormOfStudy(cmbFormOfStudy, vbFormOfStudy, currentModule.getFormofstudy());
        componentService.fillCmbSem(cmbSemester, ((Integer) (1)).longValue(), 1, null);
    }

    @Listen("onOK=#tbFio; onOK=#tbRecordbook; onOK=#tbGroup;")
    public void searchStudents () {
        Clients.showBusy(lbStudent, "Загрузка данных");
        Events.echoEvent("onLater", lbStudent, null);
    }

    @Listen("onLater = #lbStudent")
    public void laterLbStudent (Event event) {
        lbStudent.setModel(
                new ListModelList<>(stPassportService.getStudentsByFilter(tbFio.getValue(), tbRecordbook.getValue(), tbGroup.getValue())));
        lbStudent.renderAll();
        if (event.getData() != null) {
            int index = (int) event.getData();
            lbStudent.renderItem(lbStudent.getItemAtIndex(index));
            lbStudent.setSelectedIndex(index);
        }
        Clients.clearBusy(lbStudent);
    }

    @Listen("onClick = #btnGetReport")
    public void GetReport (Event event) throws IOException, JSONException {
        if (cmbSemester.getSelectedIndex() == -1) {
            return;
        }

        String rep = reports.getGoncharicReport(cmbInst.getSelectedIndex(), cmbInst.getText(),
                                                ((SemesterModel) cmbSemester.getSelectedItem().getValue()).getIdSem(),
                                                cmbSemester.getText(), cmbFormOfStudy.getSelectedIndex()
        );

        Filedownload.save(
                rep, "text/xml", "Отчет [" + cmbSemester.getText() + "](" + DateConverter.convertTimestampToString(new Date()) + ").xml");
    }

    public void updateLb (int index) {
        Clients.showBusy(lbStudent, "Загрузка данных");
        Events.echoEvent("onLater", lbStudent, index);
    }
}
