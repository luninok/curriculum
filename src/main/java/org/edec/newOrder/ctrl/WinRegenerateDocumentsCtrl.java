package org.edec.newOrder.ctrl;

import org.edec.newOrder.model.createOrder.OrderCreateDocumentModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.service.ComponentProvider;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;
import org.zkoss.zul.impl.XulElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WinRegenerateDocumentsCtrl extends CabinetSelector {
    public static final int ORDER_MODEL = 1;

    @Wire
    private Listbox lbDocuments;

    @Wire
    private Window winRegenerateDocuments;

    private List<XulElement> documentParamElements = new ArrayList<>();
    private OrderEditModel orderEditModel;

    private ComponentProvider componentProvider = new ComponentProvider();
    private OrderService orderService;

    @Override
    protected void fill () {
        orderEditModel = (OrderEditModel) Executions.getCurrent().getArg().get(ORDER_MODEL);

        orderService = OrderService.getServiceByRule(orderEditModel.getIdOrderRule(), null, null);

        for (OrderCreateDocumentModel model : orderService.getOrderDocuments()) {
            Listitem li = new Listitem();
            li.appendChild(new Listcell(model.getNameDocument()));

            for (OrderCreateParamModel param : model.getListDocumentParam()) {
                Listcell lcElementParam = new Listcell();
                XulElement element = componentProvider.provideComponent(param.getUiElement());
                documentParamElements.add(element);
                lcElementParam.appendChild(element);
                li.appendChild(lcElementParam);
            }

            lbDocuments.appendChild(li);
        }
    }

    @Listen("onClick = #btnRegenerateDocuments")
    public void onClickBtnRegenerateDocuments () {
        List<Object> valueDocumentParams = documentParamElements.stream()
                                                                .map(element -> componentProvider.getValueComponent(element))
                                                                .collect(Collectors.toList());

        orderService.createAndAttachOrderDocuments(valueDocumentParams, orderEditModel);
        getSelf().detach();
    }
}
