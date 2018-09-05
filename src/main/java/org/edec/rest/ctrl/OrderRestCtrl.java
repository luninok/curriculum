package org.edec.rest.ctrl;

import org.apache.log4j.Logger;
import org.edec.order.manager.EntityManagerOrderESO;
import org.edec.order.model.OrderModel;
import org.edec.rest.manager.OrderRestDAO;
import org.edec.synchroMine.service.MineOrderService;
import org.edec.synchroMine.service.impl.MineOrderServiceESOimpl;
import org.edec.utility.constants.OrderStatusConst;
import org.edec.utility.constants.OrderTypeConst;
import org.edec.utility.email.Sender;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.swing.text.BadLocationException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.Date;


@Path("/order")
public class OrderRestCtrl {
    private static final Logger log = Logger.getLogger(OrderRestCtrl.class.getName());

    @Context
    private ServletContext servletContext;

    private OrderRestDAO orderRestDAO = new OrderRestDAO();
    private EntityManagerOrderESO emOrderESO = new EntityManagerOrderESO();
    private MineOrderService mineOrderService = new MineOrderServiceESOimpl();

    @PUT
    @Path("/update/document")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=utf-8")
    public String getPath (@FormParam("idOrder") Long id_order, @FormParam("ordernumber") String ordernumber,
                           @FormParam("lotusid") String lotusid) {
        log.info("Обновление приказа(" + id_order + ") с номером(" + ordernumber + ") и лотусид(" + lotusid + ")");
        if (!orderRestDAO.updateOrderAfterLouts(id_order, ordernumber, lotusid)) {
            return new JSONObject().put("path", "").toString();
        }

        OrderModel order = emOrderESO.getOrderById(id_order);
        JasperReportService jasperReportService = new JasperReportService(servletContext);
        JasperReport jasperReport = jasperReportService.getJasperForOrder(order);
        byte[] bytes = jasperReport.getFile();
        if (bytes == null) {
            return new JSONObject().put("path", "").toString();
        }

        FileManager fileManager = new FileManager(servletContext);

        fileManager.signedFile(order.getUrl());
        String path = fileManager.createFileByRelativePath(
                order.getUrl(), "Приказ " + order.getIdOrder() + " " + order.getDescription() + ".pdf", bytes);

        /*String replacedFile = fileManager.replaceFileByRelativePathForFile(order.getUrl()
                + File.separator + order.getIdOrder() + ".pdf", bytes);*/
        if (path != null && !path.equals("")) {
            log.info("Обновление приказа(" + id_order + ") с номером(" + ordernumber + ") и лотусид(" + lotusid + ") прошло успешно");
            return new JSONObject().put("path", path).toString();
        }

        return new JSONObject().put("path", "").toString();
    }

    @PUT
    @Path("/update/status")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=utf-8")
    public String getStatus (@FormParam("idOrder") Long id_order, @FormParam("status") String status, @FormParam("idHum") Long idHum,
                             @FormParam("signFio") String fio, @FormParam("certNumber") String certNumber,
                             @FormParam("operation") String operation) {
        log.info("Обновление статуса приказа(" + id_order + ") со статусом(" + status + ")");
        Long statusId = OrderStatusConst.getOrderStatusConstByName(status).getId();
        if (orderRestDAO.updateOrderStatus(id_order, statusId, idHum, fio, certNumber, operation)) {
            //Если приказ подписан, то ставим статусы
            if (OrderStatusConst.AGREED == OrderStatusConst.getOrderStatusConstByName(status)) {
                OrderModel order = emOrderESO.getOrderById(id_order);
                switch (OrderTypeConst.getByType(order.getOrderType())) {
                    case DEDUCTION:
                        orderRestDAO.updateSSSdeduction(id_order);
                        break;
                    case ACADEMIC:
                        orderRestDAO.updateSSSacademic(id_order);
                        break;
                    case SOCIAL:
                        orderRestDAO.updateSSSsocial(id_order);
                        break;
                    case SOCIAL_INCREASED:
                        orderRestDAO.updateSSSsocialIncreased(id_order);
                        break;
                    case TRANSFER:
                        orderRestDAO.updateSSSTransfer(id_order);
                        break;
                    default:
                        return null;
                }
            }
        }
        return null;
    }

    @POST
    @Path("/mine")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=utf-8")
    public String createOrderMine (@FormParam("idorder") Long idorder) throws IOException, BadLocationException {
        log.info("Проведение приказа(" + idorder + ") в шахтах начато");
        OrderModel order = emOrderESO.getOrderById(idorder);
        if (order == null) {
            return new JSONObject().put("STATUS", "ERROR").toString();
        }
        if (!mineOrderService.createOrderInMine(order)) {
            return new JSONObject().put("STATUS", "ERROR").toString();
        }
        log.info("Проведение приказа(" + idorder + ") в шахтах успешно закончено");
        return new JSONObject().put("STATUS", "END_ERROR").toString();
    }


    @POST
    @Path("/send/email")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=utf-8")
    public String sendEmail (@FormParam("destination") String destination, @FormParam("subject") String subject,
                             @FormParam("message") String message) {
        try {
            Sender sender = new Sender(servletContext);
            sender.sendSimpleMessage(destination, subject, message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JSONObject().put("STATUS", "SUCCESS").toString();
    }

    @GET
    @Path("/status")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=utf-8")
    public String checkStatsu (@QueryParam("id_order") Long id_order) {
        log.info("Проверка статуса приказа(" + id_order + ") с отмененного:");
        OrderModel order = emOrderESO.getOrderById(id_order);
        if (order == null) {
            return new JSONObject().put("Status", 4).toString();
        }
        Long statusId = OrderStatusConst.getOrderStatusConstByName(order.getStatus()).getId();
        return new JSONObject().put("Status", statusId).toString();
    }

    @GET
    @Path("/ordersign")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=utf-8")
    public String createOrderFile(@QueryParam("id_order") Long idOrder) {
        try {
            OrderModel order = emOrderESO.getOrderById(idOrder);
            JasperReportService jasperReportService = new JasperReportService(servletContext);

            FileManager fm = new FileManager(servletContext);

            if(!fm.signedFile(order.getUrl())) {
                return new JSONObject().put("result", 0).toString();
            }

            String path = fm.createFileByRelativePath(order.getUrl(), "Приказ " + order.getIdOrder() + " " + order.getDescription() + ".pdf", jasperReportService.getJasperForOrder(order).getFile());

            if(path == null) {
                return new JSONObject().put("result", 0).toString();
            }

            return new JSONObject().put("result", 1).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject().put("result", 0).toString();
        }
    }
}