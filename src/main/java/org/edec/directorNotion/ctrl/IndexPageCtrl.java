package org.edec.directorNotion.ctrl;

import org.edec.directorNotion.ctrl.renderer.StudentRenderer;
import org.edec.directorNotion.model.StudentModel;
import org.edec.directorNotion.service.DirectorNotionService;
import org.edec.directorNotion.service.impl.DirectorNotionServiceImpl;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.ModuleModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.zkoss.zk.ui.Executions.getCurrent;

public class IndexPageCtrl extends SelectorComposer<Component> {

    @Wire
    private Combobox cmbFormOfStudy;

    @Wire
    private Listbox lbStudent;

    @Wire
    private Textbox tbFio, tbRecordbook, tbGroup;

    @Wire
    private Vbox vbFormOfStudy;

    @Wire
    private Button btnShowPdf,btnDownloadDocx;

    @Wire
    private Datebox dbNotionDate;

    private DirectorNotionService service = new DirectorNotionServiceImpl();
    private ComponentService componentService = new ComponentServiceESOimpl();
    private TemplatePageCtrl template = new TemplatePageCtrl();

    private FormOfStudy currentFOS;
    private ModuleModel currentModule;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        template.checkModuleByRole(getCurrent().getDesktop().getRequestPath(), getPage());
        currentModule = template.getCurrentModule();
        dbNotionDate.setValue(new Date());
        if (currentModule != null)
            fill();
    }

    private void fill() {
        lbStudent.setItemRenderer(new StudentRenderer());
        currentFOS = componentService.fillCmbFormOfStudy(cmbFormOfStudy, vbFormOfStudy, currentModule.getFormofstudy());
    }

    @Listen("onOK=#tbFio; onOK=#tbRecordbook; onOK=#tbGroup;")
    public void searchStudents() {
        Clients.showBusy(lbStudent, "Загрузка данных");
        Events.echoEvent("onLater", lbStudent, null);
    }

    @Listen("onLater = #lbStudent")
    public void laterLbStudent(Event event) {
        ListModelList<StudentModel> list = new ListModelList<>(service.getStudentsByFilter(tbFio.getValue(), tbRecordbook.getValue(),
                tbGroup.getValue()));
        list.setMultiple(true);
        lbStudent.setModel(list);
        lbStudent.renderAll();
        if (event.getData() != null) {
            int index = (int) event.getData();
            lbStudent.renderItem(lbStudent.getItemAtIndex(index));
            lbStudent.setSelectedIndex(index);
        }
        Clients.clearBusy(lbStudent);
    }

    @Listen("onClick = #btnGetReportPdf")
    public void getPdfReport(){
        List<StudentModel> selectedStudents = lbStudent.getSelectedItems().stream().map((s)->(StudentModel)s.getValue()).collect(Collectors.toList());

        if(selectedStudents.isEmpty()){
            PopupUtil.showWarning("Выберите хотя бы одного студента");
        } else{
            service.getDirectorNotion(true, selectedStudents,DateConverter.convertDateToString(dbNotionDate.getValue()));
        }
    }

    @Listen("onClick = #btnGetReportDocx")
    public void getDocxReport(){
        List<StudentModel> selectedStudents = lbStudent.getSelectedItems().stream().map((s)->(StudentModel)s.getValue()).collect(Collectors.toList());

        if(selectedStudents.isEmpty()){
            PopupUtil.showWarning("Выберите хотя бы одного студента");
        } else{
            service.getDirectorNotion(false, selectedStudents,DateConverter.convertDateToString(dbNotionDate.getValue()));
        }
    }

    public void updateLb(int index) {
        Clients.showBusy(lbStudent, "Загрузка данных");
        Events.echoEvent("onLater", lbStudent, index);
    }


}
