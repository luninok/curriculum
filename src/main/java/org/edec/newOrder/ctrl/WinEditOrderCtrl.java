package org.edec.newOrder.ctrl;

import org.edec.newOrder.ctrl.renderer.OrderEditStudentRenderer;
import org.edec.newOrder.model.editOrder.GroupModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.SectionModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.report.ReportService;
import org.edec.newOrder.service.OrderEnsembleService;
import org.edec.newOrder.service.esoImpl.EditOrderService;
import org.edec.newOrder.service.esoImpl.OrderEnsembleServiceImpl;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.constants.OrderStatusConst;
import org.edec.utility.pdfViewer.model.PdfViewer;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WinEditOrderCtrl extends CabinetSelector {
    public static final String ORDER_MODEL = "order_model";

    @Wire
    private Button btnSendToServer, btnSaveDesc, btnAddStudents, btnAttachNewFiles, btnRegenerateDocuments, btnDeleteSelectedStudents;

    @Wire
    private Groupbox gbContent;

    @Wire
    private Textbox tbDescOrder;

    private OrderService orderService;
    private OrderEnsembleService orderEnsembleService = new OrderEnsembleServiceImpl();
    private EditOrderService editOrderService = new EditOrderService();

    private OrderEditModel order;
    private boolean readOnly = true;

    @Override
    protected void fill() {
        order = (OrderEditModel) Executions.getCurrent().getArg().get(ORDER_MODEL);

        // TODO WEAK PLACE
        if(orderService == null) {
            orderService = OrderService.getServiceByRule(order.getIdOrderRule(), null, null);
        }

        if (orderService.getOrderDocuments().size() > 0) {
            btnRegenerateDocuments.setVisible(true);
        }

        switch (OrderStatusConst.getOrderStatusConstByName(order.getStatus())) {
            case CREATED:
            case REVISION:
                readOnly = false;
                break;
            default:
                tbDescOrder.setDisabled(true);
                btnAttachNewFiles.setDisabled(true);
                btnSendToServer.setDisabled(true);
                btnSaveDesc.setDisabled(true);
                btnAddStudents.setDisabled(true);
                btnRegenerateDocuments.setDisabled(true);
                btnDeleteSelectedStudents.setDisabled(true);
                readOnly = true;
                break;
        }

        tbDescOrder.setText(order.getDescription());

        fillContent();
    }

    private void fillContent() {
        while (gbContent.getFirstChild() != null) gbContent.removeChild(gbContent.getFirstChild());
        editOrderService.fillOrderModel(order);

        switch (orderService.getGroupingInEditEnum()) {
            case BY_GROUP:
                fillAndGroupByGroup();
                break;
            case BY_SECTION:
                fillAndGroupBySection();
                break;
            case BY_SECTION_WITH_ORDER_FOUNDATION:
                fillAndGroupBySectionWithFoundationForOrder();
                break;
            case BY_SECTION_WITH_STUDENT_FOUNDATION:
                fillAndGroupBySectionWithFoundationForStudent();
                break;
        }
    }

    private void fillAndGroupBySection() {
        for (SectionModel section : order.getSections()) {
            Groupbox gbSection = new Groupbox();
            gbSection.setMold("3d");
            gbSection.setParent(gbContent);

            Caption captionGroup = new Caption(section.getName());
            captionGroup.setParent(gbSection);

            for (GroupModel group : section.getGroups()) {
                Label lGroup = new Label(group.getName());
                lGroup.setParent(gbSection);
                Listbox lbStudent = new Listbox();
                lbStudent.setParent(gbSection);
                lbStudent.setModel(new ListModelList<>(group.getStudentModels()));
                lbStudent.setItemRenderer(new OrderEditStudentRenderer(order, this::fillContent, readOnly, section));
                lbStudent.renderAll();
            }
        }
    }

    private void fillAndGroupByGroup() {
        for (GroupModel group : order.getGroups()) {
            Groupbox gbGroup = new Groupbox();
            gbGroup.setMold("3d");
            gbGroup.setParent(gbContent);

            Caption captionGroup = new Caption(group.getName());
            captionGroup.setParent(gbGroup);

            for (SectionModel section : group.getSections()) {
                Label lSection = new Label(section.getName());
                lSection.setParent(gbGroup);
                Listbox lbStudent = new Listbox();
                lbStudent.setParent(gbGroup);
                lbStudent.setModel(new ListModelList<>(section.getStudentModels()));
                lbStudent.setItemRenderer(new OrderEditStudentRenderer(order, this::fillContent, readOnly, section));
                lbStudent.renderAll();
            }
        }
    }

    private void fillAndGroupBySectionWithFoundationForOrder() {
        for (SectionModel section : order.getSections()) {
            Groupbox gbSection = new Groupbox();
            gbSection.setMold("3d");
            gbSection.setParent(gbContent);

            Caption captionSection = new Caption(section.getName());
            captionSection.setParent(gbSection);

            for (GroupModel group : section.getGroups()) {
                Label lSection = new Label(group.getName());
                lSection.setParent(gbSection);
                Listbox lbStudent = new Listbox();
                lbStudent.setParent(gbSection);
                lbStudent.setModel(new ListModelList<>(group.getStudentModels()));
                lbStudent.setItemRenderer(new OrderEditStudentRenderer(order, this::fillContent, readOnly, section));
                lbStudent.renderAll();
            }

            if (section.getName().endsWith("(Бюджет)")) {
                Hbox hbox = new Hbox();
                Label label = new Label("Основание");
                Textbox tb = new Textbox();
                Button btn = new Button("Сохранить");

                if (readOnly) {
                    tb.setDisabled(true);
                    btn.setDisabled(true);
                }

                hbox.appendChild(label);
                hbox.appendChild(tb);
                hbox.appendChild(btn);

                hbox.setStyle("margin-top: 10px");

                tb.setText((section.getFoundationLos() == null || section.getFoundationLos().equals("") ? section.getFoundation() : section.getFoundationLos()));
                tb.setWidth("500px");

                btn.addEventListener(Events.ON_CLICK, event -> {
                    editOrderService.saveFoundation(section.getId(), tb.getText());

                    PopupUtil.showInfo("Изменения сохранены");
                });

                gbSection.appendChild(hbox);
            }
        }
    }

    private void fillAndGroupBySectionWithFoundationForStudent() {
        for (SectionModel section : order.getSections()) {
            Groupbox gbSection = new Groupbox();
            gbSection.setMold("3d");
            gbSection.setParent(gbContent);

            Caption captionSection = new Caption(section.getName());
            captionSection.setParent(gbSection);

            for (GroupModel group : section.getGroups()) {
                Label lGroup = new Label(group.getName());
                lGroup.setParent(gbSection);

                for (StudentModel studentModel : group.getStudentModels()) {
                    Listbox lbStudent = new Listbox();
                    lbStudent.setParent(gbSection);
                    ArrayList<StudentModel> student = new ArrayList<>();
                    student.add(studentModel);
                    lbStudent.setModel(new ListModelList<>(student));
                    lbStudent.setItemRenderer(new OrderEditStudentRenderer(order, this::fillContent, readOnly, section));
                    lbStudent.renderAll();

                    Hbox hbox = new Hbox();
                    Label label = new Label("Основание");
                    Textbox tb = new Textbox();
                    Button btn = new Button("Сохранить");

                    if (readOnly) {
                        tb.setDisabled(true);
                        btn.setDisabled(true);
                    }

                    hbox.appendChild(label);
                    hbox.appendChild(tb);
                    hbox.appendChild(btn);

                    hbox.setStyle("margin-top: 10px");

                    if (studentModel.getFoundation() != null && !studentModel.getFoundation().equals("")) {
                        tb.setText(studentModel.getFoundation());
                    } else {
                        tb.setText((section.getFoundationLos() == null || section.getFoundationLos().equals("") ? section.getFoundation() : section.getFoundationLos()));
                    }

                    tb.setWidth("500px");

                    btn.addEventListener(Events.ON_CLICK, event -> {
                        editOrderService.saveFoundationStudent(studentModel, tb.getText());

                        PopupUtil.showInfo("Изменения сохранены");
                    });

                    gbSection.appendChild(hbox);
                }
            }
        }
    }

    @Listen("onClick = #btnDeleteSelectedStudents")
    public void deleteSelectedStudents() {
        if (order.getGroups() == null || order.getGroups().size() == 0) {
            for (SectionModel sectionModel : order.getSections()) {
                for(GroupModel groupModel : sectionModel.getGroups()) {
                    for (StudentModel studentModel : groupModel.getStudentModels()) {
                        if (studentModel.isSelected()) {
                            this.editOrderService.deleteStudents(studentModel);
                        }
                    }
                }
            }
        } else {
            for (GroupModel groupModel : order.getGroups()) {
                for(SectionModel sectionModel : groupModel.getSections()) {
                    for (StudentModel studentModel : sectionModel.getStudentModels()) {
                        if (studentModel.isSelected()) {
                            this.editOrderService.deleteStudents(studentModel);
                        }
                    }
                }
            }
        }

        fillContent();
    }

    @Listen("onClick = #btnAttachedFiles")
    public void showAttachedFile() {
        PdfViewer pdfViewer = new PdfViewer(order.getUrl());
        pdfViewer.showDirectory();
    }


    public void showWinEditCtrl(OrderEditModel tempOrder) {
        Map arg = new HashMap();
        arg.put(ORDER_MODEL, tempOrder);
        ComponentHelper.createWindow("/newOrder/winEditOrder.zul", "winEditOrder", arg).doModal();
    }

    @Listen("onClick = #btnAttachNewFiles")
    public void attachNewFiles() {
        Map arg = new HashMap();
        arg.put(ORDER_MODEL, order);
        ComponentHelper.createWindow("/newOrder/winAttachNewFiles.zul", "winAttachNewFiles", arg).doModal();
    }

    @Listen("onClick = #btnShowPreviewPdf")
    public void showOrder() {
        JasperReport jasperReport = new ReportService().getJasperForOrder(order.getIdOrderRule(), order.getIdOrder());
        jasperReport.showPdf();
    }

    @Listen("onClick = #btnSendToServer")
    public void startEnsembleProcess() {
        if (order.getDescription().equals("")) {
            PopupUtil.showWarning("Заполните описание!");
            return;
        }
        Clients.showBusy("Ожидайте запуска бизнес-процесса...");
        Events.echoEvent("onLater", btnSendToServer, null);
    }

    @Listen("onLater = #btnSendToServer")
    public void onLaterBtnSendToServer() {
        if (orderEnsembleService.sendOrderToEnsemble(order)) {
            PopupUtil.showInfo("Бизнес-процесс успешно запущен!");
            tbDescOrder.setDisabled(true);
            btnSendToServer.setDisabled(true);
            btnSaveDesc.setDisabled(true);
            btnAddStudents.setDisabled(true);
            btnRegenerateDocuments.setDisabled(true);
            readOnly = true;
        } else {
            PopupUtil.showError("Не удалось отправить на сервер, обратитесь к администратору!");
        }
        Clients.clearBusy();
    }

    @Listen("onClick = #btnSaveDesc")
    public void onClickSaveDescOrder() {
        orderService.updateOrderDesc(tbDescOrder.getText(), order.getIdOrder().longValue());
        order.setDescription(tbDescOrder.getText());
        PopupUtil.showInfo("Описание сохранено");
    }

    @Listen("onClick = #btnRegenerateDocuments")
    public void onClickBtnRegenerateDocuments() {
        Map arg = new HashMap();
        arg.put(WinRegenerateDocumentsCtrl.ORDER_MODEL, order);

        ComponentHelper.createWindow("/newOrder/winRegenerateDocuments.zul", "winRegenerateDocuments", arg).doModal();
    }

    @Listen("onClick = #btnAddStudents")
    public void onClickBtnAddStudents() {
        Map arg = new HashMap();
        arg.put(WinAddStudentCtrl.ORDER, order);
        arg.put(WinAddStudentCtrl.UPDATE_UI, (Runnable) this::fillContent);
        arg.put(WinAddStudentCtrl.ORDER_SERVICE, orderService);
        ComponentHelper.createWindow("/newOrder/winAddStudent.zul","winAddStudents", arg).doModal();
    }
}
