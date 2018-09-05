package org.edec.newOrder.report;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.newOrder.manager.OrderMainManagerESO;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.report.OrderReportMainModel;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.fileManager.FileModel;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.model.jasperReport.ReportSrc;
import org.zkoss.zk.ui.Executions;

import javax.servlet.ServletContext;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService {
    private ServletContext servletContext;
    private OrderMainManagerESO mainManagerESO = new OrderMainManagerESO();

    public ReportService () { }

    public JasperReport getJasperForOrder(Long idOrder) {
        OrderEditModel order = mainManagerESO.getOrderById(idOrder);
        return getJasperForOrder(order.getIdOrderRule(), order.getIdOrder());
    }

    public JasperReport getJasperForOrder (Long idOrderRule, Long idOrder) {
        ReportModelService orderReportService = new ReportModelService();
        ReportSrc reportSrc = null;
        JRBeanCollectionDataSource data = null;
        Map<String, Object> arg = new HashMap<>();
        FileModel fileModel = null;

        switch (OrderRuleConst.getById(idOrderRule)) {
            case ACADEMIC_IN_SESSION:
            case ACADEMIC_NOT_IN_SESSION:
                reportSrc = ReportSrc.ORDER_ACADEMIC;
                data = orderReportService.getBeanDataForAcademicOrder(idOrder);
                String path = getRealPath("orders/") + File.separator;
                arg.put("realPath", path);
                break;
            case DEDUCTION_INITIATIVE:
                reportSrc = ReportSrc.ORDER_INDIVIDUAL_DEDUCTION;
                data = orderReportService.getBeanDataForDeductionOrder(idOrder);
                break;
            case SOCIAL_IN_SESSION:
            case SOCIAL_NEW_REFERENCE:
                reportSrc = ReportSrc.ORDER_SOCIAL;
                data = orderReportService.getBeanDataForSocialOrder(idOrder);
                break;
            case SOCIAL_INCREASED_IN_SESSION:
            case SOCIAL_INCREASED_NEW_REFERENCE:
                reportSrc = ReportSrc.ORDER_SOCIAL_INCREASE;
                data = orderReportService.getBeanDataForSocialIncreasedOrder(idOrder);
                break;
            case TRANSFER_AFTER_TRANSFER_CONDITIONALLY_RESPECTFUL:
            case TRANSFER_AFTER_TRANSFER_CONDITIONALLY_NOT_RESPECTFUL:
            case TRANSFER_PROLONGATION:
                reportSrc = ReportSrc.ORDER_TRANSFER_AFTER_TRANSFER;
                data = orderReportService.getBeanDataForTransferOrder(idOrder);
                break;
            case CANCEL_ACADEMICAL_SCHOLARSHIP_IN_SESSION:
                reportSrc = ReportSrc.ORDER_TRANSFER_AFTER_TRANSFER;
                data = orderReportService.getBeanDataForSocialOrder(idOrder);
                break;
            case SET_ELIMINATION_RESPECTFUL:
                reportSrc = ReportSrc.ORDER_SET_ELIMINATION_RESPECTFUL;
                data = orderReportService.getBeanDataForSetElimination(idOrder, OrderRuleConst.getById(idOrderRule));
                break;
            case SET_ELIMINATION_NOT_RESPECTFUL:
            case PROLONGATION_ELIMINATION_WINTER:
                reportSrc = ReportSrc.ORDER_SET_ELIMINATION;
                data = orderReportService.getBeanDataForSetElimination(idOrder, OrderRuleConst.getById(idOrderRule));
                break;
            case TRANSFER:
            case TRANSFER_CONDITIONALLY:
            case TRANSFER_CONDITIONALLY_RESPECTFUL:
                reportSrc = ReportSrc.ORDER_TRANSFER;
                data = orderReportService.getBeanDataForTransferOrder(idOrder);
                break;
        }

        arg.put("type", reportSrc.getType());

        return new JasperReport("Приказ", getRealPath("") + reportSrc.getLocalPath(), arg, data, fileModel, null,
                                servletContext
        );
    }

    public JasperReport getJasperReportForServiceNote (OrderEditModel order, Date dateNotion, String sem) {
        ReportModelService orderReportService = new ReportModelService();
        ReportSrc reportSrc = ReportSrc.SERVICE_NOTE;
        Map arg = new HashMap();
        FileModel fileModel = null;
        JRBeanCollectionDataSource data = orderReportService.getBeanDataForServiceNote(
                order.getIdOrder(), getDescriptionForServiceNote(order.getIdOrderRule()));

        List<OrderReportMainModel> listDate = (List<OrderReportMainModel>) data.getData();
        listDate.get(0).setDateNote(new SimpleDateFormat("dd.MM.yyyy").format(dateNotion));
        listDate.get(0).setSemesters("по итогам промежуточной аттестации (осеннего-весеннего семестров " + sem + " уч. г.)");

        data = new JRBeanCollectionDataSource(listDate);
        JasperReport jasperReport = new JasperReport("", getRealPath("") + reportSrc.getLocalPath(), arg, data, fileModel, null,
                                                     servletContext
        );
        return jasperReport;
    }

    private String getDescriptionForServiceNote (Long idRule) {
        if (OrderRuleConst.getById(idRule).equals(OrderRuleConst.TRANSFER_PROLONGATION)) {
            return "Прошу продлить срок ликвидации академических задолженностей следующим студентам," + " обучающимся " +
                   ReportModelService.FORM_OF_STUDY + ", условно переведенным на следующий курс и не прошедшим" +
                   " промежуточную аттестацию, до " + ReportModelService.FIRST_DATE + ":";
        }

        if (OrderRuleConst.getById(idRule).equals(OrderRuleConst.PROLONGATION_ELIMINATION_WINTER)) {
            return "Прошу продлить срок ликвидации академических задолженностей следующим студентам," + " обучающимся " +
                   ReportModelService.FORM_OF_STUDY + ", не прошедшим" + " промежуточную аттестацию, до " + ReportModelService.FIRST_DATE +
                   ":";
        }

        return "";
    }

    private String getRealPath (String relativePath) {
        if (Executions.getCurrent() != null) {
            return Executions.getCurrent().getDesktop().getWebApp().getRealPath("WEB-INF/reports/" + relativePath);
        } else if (servletContext != null) {
            return servletContext.getRealPath("WEB-INF/reports/" + relativePath);
        }

        return "";
    }
}
