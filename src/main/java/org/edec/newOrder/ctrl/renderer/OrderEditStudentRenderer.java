package org.edec.newOrder.ctrl.renderer;

import org.edec.newOrder.ctrl.WinInfoStudentCtrl;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.editOrder.SectionModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.newOrder.service.ComponentProvider;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;
import org.zkoss.zul.impl.XulElement;

import java.util.HashMap;
import java.util.Map;

public class OrderEditStudentRenderer implements ListitemRenderer<StudentModel> {
    private OrderEditModel order;
    private OrderService orderService;
    private ComponentProvider componentProvider;
    private SectionModel section;
    private Runnable updateUI;
    private boolean readOnly;

    public OrderEditStudentRenderer(OrderEditModel order, Runnable updateUI, boolean readOnly, SectionModel section) {
        this.order = order;
        this.updateUI = updateUI;
        this.section = section;
        this.readOnly = readOnly;
        this.componentProvider = new ComponentProvider();
        // TODO придумать откуда брать formOfStudy
        this.orderService = OrderService.getServiceByRule(order.getIdOrderRule(), null, null);
    }

    @Override
    public void render(final Listitem li, final StudentModel data, int index) throws Exception {
        li.setValue(data);

        Listcell lc = new Listcell();
        Checkbox chDelete = new Checkbox();

        chDelete.addEventListener(Events.ON_CHECK, event -> {
            data.setSelected(chDelete.isChecked());
        });

        chDelete.setParent(lc);
        lc.setParent(li);
        lc.setStyle("width: 30px;");

        componentProvider.createListcell(li, "(#" + data.getRecordnumber() + ") " + data.getFio(), "width: 100%");

        fill(li, index, data);

        Button btnDel = new Button("", "/imgs/del.png");

        if (!readOnly) {
            btnDel.addEventListener(Events.ON_CLICK, event -> Messagebox.show("Вы действительно хотите удалить студента из приказа?", "Удаление студента", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new EventListener() {
                        public void onEvent(Event evt) {
                            if (evt.getName().equals("onOK")) {
                                orderService.removeStudentFromOrder(data.getId(), order);

                                // TODO перенести в Social и SocialIncreaser creators
                                /*new EditOrderManagerESO().removeStudentFromOrder(data.getId());

                                FileModel pathOrder = new FileModel(
                                        //FIXME убрать id inst
                                        FileModel.Inst.getInstById(1L),
                                        FileModel.TypeDocument.ORDER,
                                        order.getOrderType().equals(OrderTypeConst.SOCIAL.getType()) ? FileModel.SubTypeDocument.SOCIAL : FileModel.SubTypeDocument.SOCIAL_INCREASE,
                                        order.getIdSemester(), Long.toString(order.getIdOrder()));

                                if (order.getOrderType().equals(OrderTypeConst.SOCIAL.getType())
                                        || order.getOrderType().equals(OrderTypeConst.SOCIAL_INCREASED.getType())) {
                                    new FileManager().removeReferenceFromOrder(pathOrder, data.getFio());
                                }*/
                                updateUI.run();
                                PopupUtil.showInfo("Удаление студента прошло успешно");
                            }
                        }
                    })
            );
        } else {
            btnDel.setDisabled(true);
        }

        Button btnInfo = new Button("", "/imgs/edit.png");
        btnInfo.addEventListener(Events.ON_CLICK, event -> {
            Map arg = new HashMap();
            arg.put(WinInfoStudentCtrl.STUDENT, data);
            arg.put(WinInfoStudentCtrl.ORDER, order);
            ComponentHelper.createWindow("/newOrder/winInfoStudent.zul", "winInfoStudent", arg).doModal();
        });

        btnInfo.setParent(componentProvider.createListcell(li, "", "width: 90px; align:center"));
        btnDel.setParent(componentProvider.createListcell(li, "", "width: 90px; align:center"));
    }

    private void fill(final Listitem li, int index, StudentModel data) {
        if (index == 0) {
            Listhead lh = new Listhead();
            lh.setParent(li.getListbox());

            componentProvider.createListheader(lh, "", "cwf-listheader-label", "width: 30px");

            componentProvider.createListheader(lh, "Студент", "cwf-listheader-label", "width: 100%;");

            for(OrderVisualParamModel paramModel : orderService.getVisualParamsByIdSection(section.getIdOS())) {
                componentProvider.createListheader(lh, paramModel.getName(), "cwf-listheader-label", "width: 200px;");
            }

            componentProvider.createListheader(lh, "Инфо", "cwf-listheader-label", "width: 90px;");
            componentProvider.createListheader(lh, "Удаление", "cwf-listheader-label", "width: 90px;");
        }

        int i = 1;
        for(OrderVisualParamModel paramModel : orderService.getVisualParamsByIdSection(section.getIdOS())) {
            createListcell(li, i, "width: 200px;", paramModel.getEditComponent());
            i++;
        }
    }

    private void createListcell(Listitem li, int index, String style, ComponentEnum componentEnum) {
        final Listcell lc = componentProvider.createListcell(
                li,
                orderService.getStringParamForStudent(index, li.getValue(), section.getIdOS()),
                style
        );

        lc.addEventListener(Events.ON_CLICK, event -> {
            if (lc.getFirstChild() != null) {
                return;
            }

            final StudentModel model = li.getValue();

            final XulElement editElement = componentProvider.provideComponent(componentEnum);
            final Button btnOK = new Button("Сохранить");
            btnOK.addEventListener("onClick", event1 -> {
                orderService.setParamForStudent(index, componentProvider.getValueComponent(editElement), model, section.getIdOS());

                while (lc.getFirstChild() != null) lc.removeChild(lc.getFirstChild());
                lc.setLabel(orderService.getStringParamForStudent(index, model, section.getIdOS()));
            });

            final Button btnCancel = new Button("Отмена");
            btnCancel.addEventListener("onClick", event12 -> {
                while (lc.getFirstChild() != null) lc.removeChild(lc.getFirstChild());
                lc.setLabel(orderService.getStringParamForStudent(index, model, section.getIdOS()));
            });

            lc.setLabel("");
            lc.appendChild(editElement);
            lc.appendChild(btnOK);
            lc.appendChild(btnCancel);
        });

        li.appendChild(lc);
    }
}
