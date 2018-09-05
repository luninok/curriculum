package org.edec.signEditor.ctrl;

import org.edec.signEditor.model.OrderRuleModel;
import org.edec.signEditor.service.OrderRuleService;
import org.edec.signEditor.service.impl.OrderRuleServiceImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

public class WinCreateOrderRuleCtrl extends SelectorComposer<Component> {
    @Wire
    private Textbox tbNameOrderRule, tbDescription, tbHeadDescription, tbInst, tbTypeRule;
    @Wire
    private Listbox lbFK, lbIsAuto;

    private Runnable updateTableOrderRule;
    private OrderRuleModel orderRuleModel;
    private OrderRuleService service = new OrderRuleServiceImpl();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        updateTableOrderRule = (Runnable) Executions.getCurrent().getArg().get("fillListOrderRule");
        orderRuleModel = (OrderRuleModel) Executions.getCurrent().getArg().get("rule");

        if (orderRuleModel != null) {
            tbNameOrderRule.setValue(orderRuleModel.getName());
            tbDescription.setValue(orderRuleModel.getDescription());
            tbHeadDescription.setValue(orderRuleModel.getHeadDescription());
            tbInst.setValue(orderRuleModel.getIdInstitute().toString());
            tbTypeRule.setValue(orderRuleModel.getIdOrderType().toString());

            if (orderRuleModel.getFormOfControl() == 1) {
                lbFK.setSelectedIndex(0);
            } else {
                lbFK.setSelectedIndex(1);
            }

            if (orderRuleModel.isAutomatic()) {
                lbIsAuto.setSelectedIndex(0);
            } else {
                lbIsAuto.setSelectedIndex(1);
            }
        }
    }

    @Listen("onClick = #btnCreate")
    public void createOrderRule() {
        String name = tbNameOrderRule.getValue();
        String description = tbDescription.getValue();
        String headDesc = tbHeadDescription.getValue();
        Long institute = Long.parseLong(tbInst.getValue());
        Long type = Long.parseLong(tbTypeRule.getValue());
        Long formCtrl = Long.parseLong(lbFK.getSelectedItem().getValue());
        Boolean isAuto = lbIsAuto.getSelectedIndex() == 0;

        if (orderRuleModel != null) {
            service.updateOrderRule(orderRuleModel.getIdOrderRule(), name, description, headDesc, type,
                    institute, formCtrl, isAuto);
            updateTableOrderRule.run();
        } else {
            service.createRuleModel(name, description, headDesc, institute, type, formCtrl, isAuto);
            updateTableOrderRule.run();
        }
    }
}