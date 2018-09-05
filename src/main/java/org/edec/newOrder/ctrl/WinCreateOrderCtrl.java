package org.edec.newOrder.ctrl;

import org.edec.newOrder.model.createOrder.OrderCreateDocumentModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateRuleModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.service.ComponentProvider;
import org.edec.newOrder.service.CreateOrderService;
import org.edec.newOrder.service.esoImpl.CreateOrderServiceESO;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;
import org.zkoss.zul.impl.XulElement;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.edec.newOrder.ctrl.WinEditOrderCtrl.ORDER_MODEL;

// TODO проработать отображения
public class WinCreateOrderCtrl extends CabinetSelector{
    public static final String UPDATE_UI = "updateUi";

    @Wire
    private Combobox cmbInst, cmbFormOfStudy, cmbListOrderTypes;

    @Wire
    private Hbox hbInst, hbFormOfStudy;

    @Wire
    private Groupbox gbOrderParams, gbOrderStudents, gbOrderDocuments;

    @Wire
    private Listbox lbParams, lbStudents, lbAttachedFiles;

    @Wire
    private Window winCreateOrder;

    @Wire
    private Textbox tbDescription;

    private List<OrderCreateRuleModel> listRules;
    private List<XulElement> orderParamElements = new ArrayList<>();
    private List<XulElement> documentParamElements = new ArrayList<>();
    private List<Media> attachedDocuments = new ArrayList<>();
    private HashSet<OrderCreateStudentModel> studentsToAdd = new HashSet<>();

    private FormOfStudy currentFOS;
    private InstituteModel currentInstitute;
    private Long currentSem;
    private Runnable updateUI;

    private CreateOrderService createOrderService = new CreateOrderServiceESO();
    private ComponentService componentService = new ComponentServiceESOimpl();
    private ComponentProvider componentProvider = new ComponentProvider();
    private OrderService orderService;

    @Override
    protected void fill() {
        updateUI = (Runnable) Executions.getCurrent().getArg().get(UPDATE_UI);
        currentInstitute = componentService.fillCmbInst(cmbInst, hbInst, currentModule.getDepartments(), false);
        currentFOS = componentService.fillCmbFormOfStudy(cmbFormOfStudy, hbFormOfStudy, currentModule.getFormofstudy(), false);
        currentSem = createOrderService.getCurrentSemester(currentInstitute.getIdInst(), currentFOS.getType());

        // TODO заглушка по получению всех правил
        // TODO необходимо ввести понятие: правило для института и формы контроля(ограничения на уровне бд)
        listRules = createOrderService.getOrderRulesByInstitute(currentInstitute.getIdInst());
        cmbListOrderTypes.setModel(new ListModelList<>(listRules));
        cmbListOrderTypes.setItemRenderer((ComboitemRenderer<OrderCreateRuleModel>) (comboitem, orderCreateRuleModel, i) -> {
            comboitem.setValue(orderCreateRuleModel);
            comboitem.setLabel(orderCreateRuleModel.getName());
        });
    }

    @Listen("onChange = #cmbInst")
    public void onChangeCmbInst() {
        currentInstitute = cmbInst.getSelectedItem().getValue();
        clearWorkspace();
        updateSemesterAndRules();
    }

    @Listen("onChange = #cmbFormOfStudy")
    public void onChangeCmbFOS() {
        currentFOS = cmbFormOfStudy.getSelectedItem().getValue();
        clearWorkspace();
        updateSemesterAndRules();
    }

    @Listen("onChange = #cmbListOrderTypes")
    public void onChangeCmbListOrderTypes() {
        clearWorkspace();
        if(cmbListOrderTypes.getSelectedIndex() != -1) {
            setWorkspace(cmbListOrderTypes.getSelectedItem().getValue());
        }
    }

    @Listen("onClick = #btnCreateOrder")
    public void onClickBtnCreateOrder() {
        if(cmbListOrderTypes.getSelectedIndex() == -1) {
            PopupUtil.showWarning("Выберите тип приказа");
            return;
        }

        List<Object> valueOrderParams = orderParamElements.stream()
                .filter(e -> componentProvider.getValueComponent(e) != null)
                .map(element -> componentProvider.getValueComponent(element))
                .collect(Collectors.toList());

        if(valueOrderParams.size() != orderParamElements.size()) {
            PopupUtil.showWarning("Заполните все параметры приказа");
            return;
        }

        valueOrderParams.add(currentInstitute.getIdInst());
        valueOrderParams.add(currentFOS.getType());
        valueOrderParams.add(currentSem);
        valueOrderParams.add(tbDescription.getValue());

        List<Object> valueDocumentParams = documentParamElements.stream()
                .map(element -> componentProvider.getValueComponent(element))
                .collect(Collectors.toList());

        // TODO собрать всю информацию о студентах
        attachedDocuments = lbAttachedFiles.getItems()
                .stream()
                .map(item -> (Media)item.getValue())
                .filter(item -> item != null)
                .collect(Collectors.toList());
        OrderEditModel order = orderService.createOrder(valueOrderParams, valueDocumentParams, attachedDocuments, new ArrayList<>(studentsToAdd), null);
        attachedDocuments.clear();

        Map arg = new HashMap();
        arg.put(ORDER_MODEL, order);

        ComponentHelper.createWindow("winEditOrder.zul", "winEditOrder", arg).doModal();

        updateUI.run();
    }

    private void updateSemesterAndRules() {
        currentSem = createOrderService.getCurrentSemester(currentInstitute.getIdInst(), currentFOS.getType());
        listRules = createOrderService.getOrderRulesByInstitute(currentInstitute.getIdInst());
        cmbListOrderTypes.setModel(new ListModelList<>(listRules));
        cmbListOrderTypes.setValue("");
        cmbListOrderTypes.setSelectedIndex(-1);
    }

    private void clearWorkspace() {
        gbOrderParams.setVisible(false);
        while(lbParams.getItems().size() > 0) lbParams.getItems().remove(0);

        gbOrderStudents.setVisible(false);
        while(lbStudents.getItems().size() > 0) lbStudents.getItems().remove(0);

        gbOrderDocuments.setVisible(false);
        while(lbAttachedFiles.getItems().size() > 0) lbAttachedFiles.getItems().remove(0);

        orderParamElements = new ArrayList<>();
    }

    private void setWorkspace(OrderCreateRuleModel ruleModel) {
        orderService = OrderService.getServiceByRule(ruleModel.getId(), currentFOS, currentInstitute);

        if(orderService == null) {
            return;
        }

        if(ruleModel.getAutomatic()) {
            fillParamsTable();
        } else {
            fillStudentsTable();
        }

        if(orderService.isFilesNeeded() || orderService.getOrderDocuments().size() > 0) {
            fillDocumentsTable();
        }

        winCreateOrder.setPosition("center, center");
    }

    private void fillParamsTable() {
        gbOrderParams.setVisible(true);

        for(OrderCreateParamModel orderCreateParamModel : orderService.getOrderParams()) {
            Listitem li = new Listitem();

            Listcell lcLabelParam = new Listcell(orderCreateParamModel.getLabelParam());
            Listcell lcElementParam = new Listcell();
            XulElement element = componentProvider.provideComponent(orderCreateParamModel.getUiElement());
            orderParamElements.add(element);
            lcElementParam.appendChild(element);

            li.appendChild(lcLabelParam);
            li.appendChild(lcElementParam);
            lbParams.appendChild(li);
        }
    }

    private void fillDocumentsTable() {
        gbOrderDocuments.setVisible(true);

        for(OrderCreateDocumentModel model : orderService.getOrderDocuments()) {
            Listitem li = new Listitem();
            li.appendChild(new Listcell(model.getNameDocument()));

            for(OrderCreateParamModel param : model.getListDocumentParam()) {
                Listcell lcElementParam = new Listcell();
                XulElement element = componentProvider.provideComponent(param.getUiElement());
                documentParamElements.add(element);
                lcElementParam.appendChild(element);
                li.appendChild(lcElementParam);
            }

            lbAttachedFiles.appendChild(li);
        }

        // TODO перенести это поле в OrderRule
        if(orderService.isFilesNeeded()) {
            lbAttachedFiles.appendChild(getLiWithUploadButton());
        }
    }

    private void fillStudentsTable() {
        gbOrderStudents.setVisible(true);
        lbStudents.appendChild(getLiWithAddStudentsButton());
    }

    private Listitem getLiWithAddStudentsButton() {
        Listitem li = new Listitem();

        Listcell lcButtonAdd = new Listcell();
        lcButtonAdd.appendChild(getAddStudentsButton());

        li.appendChild(lcButtonAdd);
        li.appendChild(new Listcell());
        li.appendChild(new Listcell());
        return li;
    }

    private Button getAddStudentsButton() {
        Button btnAddStudent = new Button("Добавить");

        btnAddStudent.addEventListener(Events.ON_CLICK, event -> {
            HashMap<Integer, Object> args = new HashMap<>();

            Consumer<List<OrderCreateStudentModel>> updateListStudentsFunc = list -> {
                lbStudents.removeChild(lbStudents.getLastChild());

                studentsToAdd.addAll(list);

                addRowsToStudentsList(list);

                lbStudents.appendChild(getLiWithAddStudentsButton());
            };

            args.put(WinSelectStudentsForCreateCtrl.INSTITUTE, currentInstitute);
            args.put(WinSelectStudentsForCreateCtrl.FOS, currentFOS);
            args.put(WinSelectStudentsForCreateCtrl.ADD_ACTION, updateListStudentsFunc);

            ComponentHelper.createWindow("/newOrder/winSelectStudentsForCreate.zul", "winSelectStudentsForCreate", args).doModal();
        });

        return btnAddStudent;
    }

    private void addRowsToStudentsList(List<OrderCreateStudentModel> studentsToAdd) {
        studentsToAdd.forEach(student -> {
            Listitem li = new Listitem();

            Listcell lcFamily = new Listcell(student.getFio());
            li.appendChild(lcFamily);

            Listcell lcGroup = new Listcell(student.getGroupname());
            li.appendChild(lcGroup);

            Button btnDelete = new Button("Удалить");
            btnDelete.addEventListener(Events.ON_CLICK, event -> {
                lbStudents.removeChild(li);
            });
            Listcell lcDelete = new Listcell();
            lcDelete.appendChild(btnDelete);
            li.appendChild(lcDelete);

            lbStudents.appendChild(li);
        });
    }

    public Listitem getLiWithUploadButton() {
        Listitem li = new Listitem();

        Listcell lcButtonAdd = new Listcell();
        lcButtonAdd.appendChild(getUploadButton());

        li.appendChild(lcButtonAdd);
        li.appendChild(new Listcell());
        return li;
    }

    public Button getUploadButton() {
        Button btnAddDocument = new Button("Добавить");
        btnAddDocument.setUpload("true,maxsize=3000000,multiple=true");

        btnAddDocument.addEventListener(Events.ON_UPLOAD, (EventListener<UploadEvent>) event -> {
            lbAttachedFiles.removeChild(lbAttachedFiles.getLastChild());

            Arrays.stream(event.getMedias()).forEach(m -> lbAttachedFiles.appendChild(getLiWithUploadedFile(m)));

            lbAttachedFiles.appendChild(getLiWithUploadButton());
        });

        return btnAddDocument;
    }

    private Listitem getLiWithUploadedFile(Media m) {
        Listitem li = new Listitem();
        li.setValue(m);

        Listcell lcName = new Listcell(m.getName());
        Listcell lcButtonDel = new Listcell();

        Button btnDelete = new Button("", "/imgs/del.png");
        btnDelete.addEventListener(Events.ON_CLICK, event -> lbAttachedFiles.removeChild(li));

        lcButtonDel.appendChild(btnDelete);

        li.appendChild(lcName);
        li.appendChild(lcButtonDel);
        return li;
    }
}
