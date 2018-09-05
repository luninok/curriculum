package org.edec.newOrder.service.orderCreator;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.newOrder.manager.CreateOrderManagerESO;
import org.edec.newOrder.manager.EditOrderManagerESO;
import org.edec.newOrder.manager.OrderMainManagerESO;
import org.edec.newOrder.manager.StudentsGetterForCreationManager;
import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.addStudent.LinkOrderSectionEditModel;
import org.edec.newOrder.model.addStudent.SearchStudentModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.enums.GroupingInEditEnum;
import org.edec.newOrder.model.createOrder.OrderCreateDocumentModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.service.DocumentService;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.fileManager.FileModel;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;

import java.util.*;

public abstract class OrderService {
    protected CreateOrderManagerESO managerESO = new CreateOrderManagerESO();
    protected OrderMainManagerESO orderMainManagerESO = new OrderMainManagerESO();
    protected DocumentService documentService = new DocumentService();
    protected EditOrderManagerESO editOrderManagerESO = new EditOrderManagerESO();
    protected StudentsGetterForCreationManager studentsOrderManager = new StudentsGetterForCreationManager();
    protected FileManager fileHelper = new FileManager();

    protected List<OrderCreateParamModel> orderParams = new ArrayList<>();
    protected List<OrderCreateDocumentModel> orderDocuments = new ArrayList<>();
    protected List<OrderVisualParamModel> orderVisualGeneralParams = new ArrayList<>();
    protected Map<Long, List<OrderVisualParamModel>> orderSectionVisualParamsMap = new HashMap<>();

    protected boolean isFilesNeeded;
    // TODO WTF? Исправить
    protected static OrderRuleConst orderRuleConst;
    protected GroupingInEditEnum groupingInEditEnum;
    protected FormOfStudy formOfStudy;
    protected InstituteModel instituteModel;

    public static OrderService getServiceByRule (Long idRule, FormOfStudy formOfStudy, InstituteModel inst) {
        OrderService orderService = null;

        switch (OrderRuleConst.getById(idRule)) {
            case PROLONGATION_ELIMINATION_WINTER:
                orderService = new ProlongationWinterSessionOrderService();
                break;
            case TRANSFER_AFTER_TRANSFER_CONDITIONALLY_NOT_RESPECTFUL:
                orderService = new TransferAfterTransferConditionallyOrderService(false);
                break;
            case TRANSFER_AFTER_TRANSFER_CONDITIONALLY_RESPECTFUL:
                orderService = new TransferAfterTransferConditionallyOrderService(true);
                break;
            case SET_ELIMINATION_RESPECTFUL:
                orderService = new SetEliminationOrderService(true);
                break;
            case SET_ELIMINATION_NOT_RESPECTFUL:
                orderService = new SetEliminationOrderService(false);
                break;
            case TRANSFER_PROLONGATION:
                orderService = new TransferProlongationOrderService();
                break;
            case TRANSFER_CONDITIONALLY:
                orderService = new TransferConditionallyOrderService();
                break;
            case TRANSFER:
                orderService = new TransferOrderService();
                break;
            case SOCIAL_IN_SESSION:
                orderService = new SocialInSessionOrderService();
                break;
            case ACADEMIC_IN_SESSION:
                orderService = new AcademicInSessionOrderService();
                break;
            case DEDUCTION_INITIATIVE:
                orderService = new DeductionInitiativeOrderService();
                break;
            case SOCIAL_NEW_REFERENCE:
                orderService = new SocialNewReferenceOrderService();
                break;
            case ACADEMIC_NOT_IN_SESSION:
                orderService = new AcademicNotInSessionOrderService();
                break;
            case SOCIAL_INCREASED_IN_SESSION:
                orderService = new SocialIncreasedInSessionOrderService();
                break;
            case SOCIAL_INCREASED_NEW_REFERENCE:
                orderService = new SocialIncreasedNewReferenceOrderService();
                break;
            case CANCEL_ACADEMICAL_SCHOLARSHIP_IN_SESSION:
                orderService = new CancelAcademicalScholarshipInSessionOrderService();
                break;
            case TRANSFER_CONDITIONALLY_RESPECTFUL:
                orderService = new TransferConditionallyRespectfulService();
                break;
        }

        orderService.generateParamModel();
        orderService.generateDocumentModel();
        orderService.formOfStudy = formOfStudy;
        orderService.instituteModel = inst;
        orderService.orderRuleConst = OrderRuleConst.getById(idRule);
        orderService.groupingInEditEnum = getGroupingByRule();

        return orderService;
    }

    private static GroupingInEditEnum getGroupingByRule () {
        switch (orderRuleConst) {
            case ACADEMIC_IN_SESSION:
            case ACADEMIC_NOT_IN_SESSION:
                return GroupingInEditEnum.BY_GROUP;
            case DEDUCTION_INITIATIVE:
                return GroupingInEditEnum.BY_SECTION_WITH_STUDENT_FOUNDATION;
            case SOCIAL_IN_SESSION:
            case SOCIAL_NEW_REFERENCE:
            case SOCIAL_INCREASED_IN_SESSION:
            case SOCIAL_INCREASED_NEW_REFERENCE:
            case CANCEL_ACADEMICAL_SCHOLARSHIP_IN_SESSION:
                return GroupingInEditEnum.BY_SECTION;
            case TRANSFER:
            case TRANSFER_CONDITIONALLY:
            case PROLONGATION_ELIMINATION_WINTER:
            case SET_ELIMINATION_NOT_RESPECTFUL:
            case TRANSFER_PROLONGATION:
            case SET_ELIMINATION_RESPECTFUL:
            case TRANSFER_AFTER_TRANSFER_CONDITIONALLY_RESPECTFUL:
            case TRANSFER_AFTER_TRANSFER_CONDITIONALLY_NOT_RESPECTFUL:
            case TRANSFER_CONDITIONALLY_RESPECTFUL:
                return GroupingInEditEnum.BY_SECTION_WITH_ORDER_FOUNDATION;
        }

        return null;
    }

    public OrderEditModel createOrder (List<Object> orderParams, List<Object> documentParams, List<Media> attachedDocuments,
                                       List<OrderCreateStudentModel> students, List<String> groups) {
        // TODO транзакция
        Long idOrder = createOrderInDatabase(orderParams, students);
        createFolderForOrder(getIdInstFromParams(orderParams), getIdSemesterFromParams(orderParams), idOrder, attachedDocuments);

        OrderEditModel order = orderMainManagerESO.getOrderById(idOrder);

        createAndAttachOrderDocuments(documentParams, order);

        return order;
    }

    protected Long createEmptyOrder (Long idSemester, String description) {
        Long idHumanface = ((UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER)).getIdHum();
        return managerESO.createEmptyOrder(orderRuleConst.getId(), new Date(), idSemester, idHumanface, description);
    }

    Long createEmptySection (Long idOrder, Long idOS, String foundation, Date firstDate, Date secondDate) {
        return managerESO.createLinkOrderSection(idOrder, idOS, foundation, firstDate, secondDate);
    }

    void createStudentInSection (Long idLOS, Long idSSS, Date firstDate, Date secondDate, String groupname, String additional) {
        managerESO.createLinkOrderStudentStatus(idLOS, idSSS, firstDate, secondDate, groupname, additional);
    }

    private void createFolderForOrder (Long idInstitute, Long idSemester, Long idOrder, List<Media> attached) {
        FileModel fileModel = new FileModel(FileModel.Inst.getInstById(idInstitute), FileModel.TypeDocument.ORDER,
                                            getTypeDocumentByRule(orderRuleConst), idSemester, idOrder.toString()
        );

        fileHelper.createFolder(fileModel, true, true, attached);
        setUrlForOrder(idOrder, FileManager.getRelativePath(fileModel));
    }

    private void setUrlForOrder (Long idOrder, String url) {
        managerESO.setUrlForOrder(idOrder, url);
    }

    private FileModel.SubTypeDocument getTypeDocumentByRule (OrderRuleConst orderRuleConst) {
        // TODO заполнить
        switch (orderRuleConst) {
            case PROLONGATION_ELIMINATION_WINTER:
                return FileModel.SubTypeDocument.TRANSFER;
            case DEDUCTION_INITIATIVE:
                return FileModel.SubTypeDocument.DEDUCTION;
            case CANCEL_ACADEMICAL_SCHOLARSHIP_IN_SESSION:
                return FileModel.SubTypeDocument.ACADEMIC;
            case TRANSFER_CONDITIONALLY_RESPECTFUL:
                return FileModel.SubTypeDocument.TRANSFER;
        }

        return null;
    }

    String getDescFromParams (List<Object> params) {
        return (String) params.get(params.size() - 1);
    }

    Long getIdSemesterFromParams (List<Object> params) {
        return (Long) params.get(params.size() - 2);
    }

    protected Integer getFOSFromParams (List<Object> params) {
        return (Integer) params.get(params.size() - 3);
    }

    private Long getIdInstFromParams (List<Object> params) {
        return (Long) params.get(params.size() - 4);
    }

    public void updateOrderDesc (String desc, long id) {
        managerESO.updateOrderDesc(desc, id);
    }

    public List<OrderVisualParamModel> getVisualParamsByIdSection (Long idOrderSection) {
        return orderSectionVisualParamsMap.get(idOrderSection) == null
               ? orderVisualGeneralParams
               : orderSectionVisualParamsMap.get(idOrderSection);
    }

    public void addStudentToOrder (SearchStudentModel studentModel, OrderEditModel order, LinkOrderSectionEditModel orderSection) {
        Long id = managerESO.createLinkOrderStudentStatus(orderSection.getIdLOS(), studentModel.getId(), null, null,
                                                          studentModel.getGroupname(), ""
        );

        List<OrderVisualParamModel> listParams = getVisualParamsByIdSection(orderSection.getIdOS());

        for (int i = 0; i < listParams.size(); i++) {
            setParamForStudent(i + 1, studentModel.getStudentParams().get(i), new StudentModel(id), orderSection.getIdOS());
        }
    }

    public void removeStudentFromOrder (Long idLoss, OrderEditModel order) {
        editOrderManagerESO.removeStudentFromOrder(idLoss);
    }

    void updateDateForStudent (StudentModel studentModel, Date date, int fieldNum) {
        String field = "";

        switch (fieldNum) {
            case 1:
                studentModel.setFirstDate(date);
                field = "first_date";
                break;
            case 2:
                studentModel.setSecondDate(date);
                field = "second_date";
                break;
        }

        editOrderManagerESO.updateLossParam(studentModel.getId(),
                                            field + " = '" + DateConverter.convertDateToStringByFormat(date, "yyyy-MM-dd") + "'"
        );
    }

    protected abstract void generateParamModel ();
    protected abstract void generateDocumentModel ();
    protected abstract Long createOrderInDatabase (List<Object> orderParams, List<OrderCreateStudentModel> students);

    public abstract boolean createAndAttachOrderDocuments (List<Object> documentParams, OrderEditModel order);
    public abstract void setParamForStudent (Integer i, Object value, StudentModel studentModel, Long idOS);
    public abstract Object getParamForStudent (Integer i, StudentModel studentModel, Long idOS);
    public abstract String getStringParamForStudent (Integer i, StudentModel studentModel, Long idOS);

    public List<OrderCreateParamModel> getOrderParams () {
        return orderParams;
    }

    public List<OrderCreateDocumentModel> getOrderDocuments () {
        return orderDocuments;
    }

    public boolean isFilesNeeded () {
        return isFilesNeeded;
    }

    public GroupingInEditEnum getGroupingInEditEnum () {
        return groupingInEditEnum;
    }
}
