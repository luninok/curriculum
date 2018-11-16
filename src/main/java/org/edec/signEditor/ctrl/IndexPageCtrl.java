package org.edec.signEditor.ctrl;

import org.edec.signEditor.model.OrderRuleModel;
import org.edec.signEditor.model.OrderSectionModel;
import org.edec.signEditor.renderer.OrderRuleRenderer;
import org.edec.signEditor.renderer.OrderSectionRenderer;
import org.edec.signEditor.service.OrderRuleService;
import org.edec.signEditor.service.impl.OrderRuleServiceImpl;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexPageCtrl extends SelectorComposer<Component> {
    @Wire
    private Listbox lbOrderRule, lbOrderSection;
    @Wire
    private Button btnCreateOrderRule, btnUpdateOrderRule, btnDeleterOrdeRule;
    @Wire
    private Button btnCreateOrderSection, btnUpdateOrderSection, btnDeleteOrderSection;


    private OrderRuleService service = new OrderRuleServiceImpl();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        lbOrderRule.setItemRenderer(new OrderRuleRenderer());
        fillListOrderRule();
    }

    public void fillListOrderRule() {
        List<OrderRuleModel> orderRules = service.getListOrderRule();
        ListModelList<OrderRuleModel> listModelOrderRule = new ListModelList<>(orderRules);
        lbOrderRule.setModel(listModelOrderRule);
        lbOrderRule.renderAll();
    }

    @Listen("onSelect = #lbOrderRule")
    public void fillListOrderSection() {
        OrderRuleModel model = lbOrderRule.getSelectedItem().getValue();
        List<OrderSectionModel> orderSections = service.getOrderSection(model.getIdOrderRule());
        ListModelList<OrderSectionModel> listModelOrderSection = new ListModelList<>(orderSections);
        lbOrderSection.setModel(listModelOrderSection);
        lbOrderSection.setItemRenderer(new OrderSectionRenderer());
        lbOrderSection.renderAll();
    }

    @Listen("onClick = #btnCreateOrderRule")
    public void openCreateOrderRuleWin() {
        Runnable updateListOrderRule = this::fillListOrderRule;
        Map<String, Object> arg = new HashMap<>();
        arg.put("fillListOrderRule", updateListOrderRule);
        ComponentHelper.createWindow("/signEditor/winCreateOrderRule.zul", "winCreateOrderRule", arg).doModal();
    }

    @Listen("onClick = #btnUpdateOrderRule")
    public void openUpdateOrderRuleWin() {
        if (lbOrderRule.getSelectedItem() != null) {
            Runnable updateListOrderRule = this::fillListOrderRule;
            Map<String, Object> arg = new HashMap<>();
            arg.put("fillListOrderRule", updateListOrderRule);
            arg.put("rule", lbOrderRule.getSelectedItem().getValue());

            ComponentHelper.createWindow("/signEditor/winCreateOrderRule.zul", "winCreateOrderRule", arg).doModal();
        } else {
            PopupUtil.showInfo("Выберите правило");
        }
    }

    @Listen("onClick = #btnDeleteOrderRule")
    public void deleteRule() {
        if (lbOrderRule.getSelectedItem() != null) {
            OrderRuleModel model = lbOrderRule.getSelectedItem().getValue();
            service.deleteOrderRule(model.getIdOrderRule());
            lbOrderSection.setModel(new ListModelList<>());
            fillListOrderRule();
        } else {
            PopupUtil.showInfo("Выберите правило");
        }
    }


    @Listen("onClick = #btnCreateOrderSection")
    public void openCreateSectionWin() {
        if (lbOrderRule.getSelectedItem() != null) {
            Runnable updateListOrderSection = this::fillListOrderSection;
            OrderRuleModel model = lbOrderRule.getSelectedItem().getValue();

            Map<String, Object> arg = new HashMap<>();
            arg.put("fillListOrderSection", updateListOrderSection);
            arg.put("id", model.getIdOrderRule());

            ComponentHelper.createWindow("/signEditor/winCreateSection.zul", "winCreateSection", arg).doModal();
        } else {
            PopupUtil.showInfo("Выберите правило");
        }

    }

    @Listen("onClick = #btnUpdateOrderSection")
    public void openUpdateSectionWin() {
        if (lbOrderSection.getSelectedItem() != null) {
            Runnable fillListOrderSection = this::fillListOrderSection;
            OrderRuleModel model = lbOrderRule.getSelectedItem().getValue();

            Map<String, Object> arg = new HashMap<>();

            arg.put("fillListOrderSection", fillListOrderSection);
            arg.put("section", lbOrderSection.getSelectedItem().getValue());
            arg.put("id", model.getIdOrderRule());

            ComponentHelper.createWindow("/signEditor/winCreateSection.zul", "winCreateSection", arg).doModal();
        } else {
            PopupUtil.showInfo("Выберите секцию");
        }
    }

    @Listen("onClick = #btnDeleteOrderSection")
    public void deleteSection() {
        if (lbOrderSection.getSelectedItem() != null) {
            OrderSectionModel model = lbOrderSection.getSelectedItem().getValue();
            service.deleteOrderSection(model.getIdOrderSection());
            fillListOrderSection();
        } else {
            PopupUtil.showInfo("Выберите секцию");
        }
    }
}