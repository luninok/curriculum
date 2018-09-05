package org.edec.order.ctrl;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.ModuleModel;
import org.edec.order.model.OrderModel;
import org.edec.order.service.impl.ImportOrderService;
import org.edec.order.service.impl.OrderSerivceESOimpl;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.ListModelList;

import java.util.List;


public class OrderImportCtrl extends SelectorComposer {
    @Wire
    private Combobox cmbOrder;

    private InstituteModel currentInstitute;
    private ComponentService componentService;

    private ImportOrderService importOrderService = new ImportOrderService();
    private OrderSerivceESOimpl orderSerivceESOimpl = new OrderSerivceESOimpl();

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        fill();
    }

    protected void fill () {
        //TODO заглушка для института
        List<OrderModel> listOrders = orderSerivceESOimpl.getOrderArchiveByFilter(1L, 3, null, null, "");
        cmbOrder.setItemRenderer((Comboitem comboitem, OrderModel orderModel, int i) -> {
            if (orderModel.getNumber() == null) {
                comboitem.setLabel("Приказ не проведен");
            } else {
                comboitem.setLabel(orderModel.getNumber() + " " + orderModel.getDescription());
                comboitem.setValue(orderModel);
            }
        });
        cmbOrder.setModel(new ListModelList<>(listOrders));
    }

    @Listen("onClick = #btnImport")
    public void onClickBtnImport () {
        if (cmbOrder.getSelectedIndex() == -1) {
            return;
        }

        if (cmbOrder.getSelectedItem().getValue() == null) {
            return;
        }

        OrderModel model = cmbOrder.getSelectedItem().getValue();
        importOrderService.startTransfer(model);
    }
}
