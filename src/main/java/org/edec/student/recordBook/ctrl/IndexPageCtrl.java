package org.edec.student.recordBook.ctrl;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.ModuleModel;
import org.edec.student.recordBook.ctrl.renderer.RecordBookRenderer;
import org.edec.student.recordBook.model.GroupModel;
import org.edec.student.recordBook.model.StudentSemesterModel;
import org.edec.student.recordBook.service.RecordBookService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.List;

import static org.zkoss.zk.ui.Executions.getCurrent;


public class IndexPageCtrl extends SelectorComposer<Component> {
    @Wire
    private Combobox cmbGroup, cmbSemester;

    @Wire
    private Listbox lbRecordBook;

    private RecordBookService recordBookService = new RecordBookService();
    private TemplatePageCtrl template = new TemplatePageCtrl();

    private ModuleModel currentModule;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        template.checkModuleByRole(getCurrent().getDesktop().getRequestPath(), getPage());
        currentModule = template.getCurrentModule();
        if (currentModule != null) {
            fill();
        }
    }

    private void fill () {
        template.setVisitedModuleByHum(template.getCurrentUser().getIdHum(), currentModule.getIdModule());
        lbRecordBook.setItemRenderer(new RecordBookRenderer());
        List<GroupModel> groups = recordBookService.getGroupByHum(template.getCurrentUser().getIdHum());
        cmbGroup.setItemRenderer((comboitem, groupModel, i) -> {
            comboitem.setValue(groupModel);
            comboitem.setLabel(((GroupModel) groupModel).getGroupname());
        });
        cmbSemester.setItemRenderer((comboitem, studentSemesterModel, i) -> {
            comboitem.setValue(studentSemesterModel);
            comboitem.setLabel(((StudentSemesterModel) studentSemesterModel).getSemesterNumber());
        });
        cmbGroup.setVisible(groups.size() > 1 ? true : false);
        cmbGroup.setModel(new ListModelList<>(groups));
    }

    @Listen("onAfterRender = #cmbGroup")
    public void onAfterRenderCmbGroup () {
        cmbGroup.setSelectedIndex(0);
        Events.echoEvent(Events.ON_CHANGE, cmbGroup, null);
    }

    @Listen("onChange = #cmbGroup")
    public void onChangeCmbGroup () {
        GroupModel selectedGroup = cmbGroup.getSelectedItem().getValue();
        cmbSemester.setModel(new ListModelList<>(selectedGroup.getSemesters()));
        Events.echoEvent(Events.ON_CHANGE, cmbSemester, null);
    }

    @Listen("onAfterRender = #cmbSemester")
    public void onAfterRenderCmbSemester () {
        cmbSemester.setSelectedIndex(0);
    }

    @Listen("onChange = #cmbSemester")
    public void onChangeSemester () {
        Clients.showBusy(lbRecordBook, "Загрузка данных");
        Events.echoEvent("onLater", lbRecordBook, null);
    }

    @Listen("onLater = #lbRecordBook")
    public void onLaterLbRecordBook () {
        lbRecordBook.setModel(new ListModelList<>(recordBookService.getGradeBookBySSS(cmbSemester.getSelectedItem().getValue())));
        lbRecordBook.renderAll();
        Clients.clearBusy(lbRecordBook);
    }
}