package org.edec.student.journalOrder.ctrl;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.ModuleModel;
import org.edec.student.journalOrder.model.JournalOrderModel;
import org.edec.student.journalOrder.service.JournalOrderService;
import org.edec.utility.converter.DateConverter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import static org.zkoss.zk.ui.Executions.getCurrent;


public class IndexPageCtrl extends SelectorComposer<Component> {
    @Wire
    private Listbox lbJournal;

    private JournalOrderService journalOrderService = new JournalOrderService();
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
        lbJournal.setItemRenderer(new ListitemRenderer<JournalOrderModel>() {
            @Override
            public void render (Listitem li, JournalOrderModel data, int index) throws Exception {
                li.setValue(data);
                new Listcell(String.valueOf(index + 1)).setParent(li);
                new Listcell(data.getSemesterStr()).setParent(li);
                new Listcell(data.getGroupname()).setParent(li);
                new Listcell(data.getOrderNumber()).setParent(li);
                new Listcell(DateConverter.convertDateToString(data.getDateSignOrder())).setParent(li);
                new Listcell(data.getOrderType()).setParent(li);
            }
        });
        lbJournal.setModel(new ListModelList<>(journalOrderService.getJournalByHum(template.getCurrentUser().getIdHum())));
        lbJournal.renderAll();
    }
}
