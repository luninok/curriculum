package org.edec.order.ctrl;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.ModuleModel;
import org.edec.order.ctrl.delegate.IndexPageCtrlDelegate;
import org.edec.order.ctrl.renderer.OrderRenderer;
import org.edec.order.model.OrderStatusModel;
import org.edec.order.model.OrderTypeModel;
import org.edec.order.service.OrderService;
import org.edec.order.service.impl.OrderSerivceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.zkoss.zk.ui.Executions.getCurrent;


public class IndexPageCtrl extends SelectorComposer<Component> {
    @Wire
    private Combobox cmbInst, cmbType, cmbStatus, cmbFormOfStudy;

    @Wire
    private Groupbox gbCreateOrder;

    @Wire
    private Listbox lbOrder;

    @Wire
    private Listheader lhrEdit, lhrDateCreate, lhrDateSign, lhrDateFinish, lhrDelete;

    @Wire
    private Textbox tbFio;

    @Wire
    private Vbox vbInst, vbFormOfStudy;

    private TemplatePageCtrl template = new TemplatePageCtrl();
    private ComponentService componentService = new ComponentServiceESOimpl();
    private OrderService orderService = new OrderSerivceESOimpl();

    private FormOfStudy currentFOS;
    private ModuleModel currentModule;
    private InstituteModel currentInstitute;

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
        currentInstitute = componentService.fillCmbInst(cmbInst, vbInst, currentModule.getDepartments());
        currentFOS = componentService.fillCmbFormOfStudy(cmbFormOfStudy, vbFormOfStudy, currentModule.getFormofstudy());
        fillCmbStatus();
        fillCmbType();
        if (currentModule.isReadonly()) {
            gbCreateOrder.setVisible(false);
            lhrEdit.setVisible(false);
            lhrDateCreate.setVisible(false);
            lhrDateSign.setVisible(false);
            lhrDelete.setVisible(false);
        } else {
            lhrDateFinish.setVisible(false);
        }
        lbOrder.setItemRenderer(new OrderRenderer(currentModule.isReadonly()));
        callLater();
    }

    private void fillCmbStatus () {
        List<OrderStatusModel> statuses = new ArrayList<>();
        statuses.add(new OrderStatusModel(null, "Все"));
        statuses.addAll(orderService.getAllStatus());
        cmbStatus.setModel(new ListModelList<>(statuses));
        cmbStatus.setItemRenderer((ComboitemRenderer<OrderStatusModel>) (ci, data, index) -> {
            ci.setValue(data);
            ci.setLabel(data.getName());
        });
        cmbStatus.addEventListener("onAfterRender", event -> cmbStatus.setSelectedIndex(0));
    }

    private void fillCmbType () {
        List<OrderTypeModel> types = new ArrayList<>();
        types.add(new OrderTypeModel(null, "Все"));
        types.addAll(orderService.getDistinctType());
        cmbType.setModel(new ListModelList<>(types));
        cmbType.setItemRenderer((ComboitemRenderer<OrderTypeModel>) (ci, data, index) -> {
            ci.setValue(data);
            ci.setLabel(data.getName());
        });
        cmbType.addEventListener("onAfterRender", event -> cmbType.setSelectedIndex(0));
    }

    @Listen("onClick = #btnCreateNewOrder")
    public void createOrder () {
        FormOfStudy selectedFos = cmbFormOfStudy.getSelectedItem().getValue();
        InstituteModel selectedInst = cmbInst.getSelectedItem().getValue();
        if (selectedFos.getType() == 3 || selectedInst.getIdInst() == null) {
            PopupUtil.showWarning("Выберите форму контроля и институт!");
            return;
        }

        Map<String, Object> arg = new HashMap<>();
        arg.put(WinCreateOrderCtrl.DELEGATE, new IndexPageCtrlDelegate(this));
        arg.put(WinCreateOrderCtrl.INSTITUTE_MODEL, selectedInst);
        arg.put(WinCreateOrderCtrl.FORM_OF_STUDY, selectedFos);
        ComponentHelper.createWindow("/order/winCreateOrder.zul", "winCreateOrder", arg).doModal();
    }

    @Listen("onChange = #cmbInst; onChange = #cmbStatus; onChange = #cmbFormOfStudy; " +
            "onChange = #cmbType; onClick = #tbFio; onOK=#tbFio;")
    public void search () {
        callLater();
    }

    @Listen("onLater = #lbOrder")
    public void laterOnLbOrder () {
        refreshData();
        Clients.clearBusy(lbOrder);
    }

    public void refreshData () {
        lbOrder.setModel(new ListModelList<>(orderService.getOrderArchiveByFilter(
                ((InstituteModel) cmbInst.getSelectedItem().getValue()).getIdInst(),
                ((FormOfStudy) cmbFormOfStudy.getSelectedItem().getValue()).getType(),
                ((OrderStatusModel) cmbStatus.getSelectedItem().getValue()).getIdStatus(),
                ((OrderTypeModel) cmbType.getSelectedItem().getValue()).getIdType(), tbFio.getValue()
        )));
        lbOrder.renderAll();
    }

    private void callLater () {
        Clients.showBusy(lbOrder, "Загрузка данных");
        Events.echoEvent("onLater", lbOrder, null);
    }
}
