package org.edec.utility.pdfViewer.ctrl;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.edec.workflow.ctrl.IndexPageCtrl;
import org.edec.workflow.model.WorkflowModel;
import org.edec.workflow.service.WorkflowService;
import org.edec.workflow.service.impl.WorkflowServiceEnsembleImpl;
import org.zkoss.json.JSONObject;
import org.zkoss.util.media.AMedia;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by dmmax
 */
public class PdfViewerDocumentsCtrl extends SelectorComposer<Component> {
    public static final Logger log = Logger.getLogger(PdfViewerDocumentsCtrl.class.getName());

    public static final String DOCUMENTS = "documents";
    public static final String WORKFLOW_MODEL = "workflow_model";

    @Wire
    private Iframe iframePdfViwer;

    @Wire
    private Listbox lbDocSign, lbAttach, lbCanceled;

    @Wire
    private Vbox vbDocs, vbContent;

    @Wire
    private Window winPdfViewer;

    private File currentFile;
    private File[] documents;
    private UserModel currentUser;
    private WorkflowModel workflowModel;

    private WorkflowService workflowService = new WorkflowServiceEnsembleImpl();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        documents = (File[]) Executions.getCurrent().getArg().get(DOCUMENTS);
        workflowModel = (WorkflowModel) Executions.getCurrent().getArg().get(WORKFLOW_MODEL);
        currentUser = new TemplatePageCtrl().getCurrentUser();
        fillListboxes();
        if (workflowModel != null) {
            fillFowWorkflow();
        }
        if (lbDocSign.getItems().size() > 0) {
            Events.echoEvent(Events.ON_CLICK, lbDocSign.getItems().get(0), null);
        }
    }

    private void fillContent() {
        Hbox hbContent = new Hbox();
        hbContent.setParent(vbContent);
        hbContent.setHflex("1");
        hbContent.setAlign("center");
        hbContent.setPack("right");

        Button btnSign = new Button("Подтвердить");
        btnSign.setParent(hbContent);
        btnSign.setIconSclass("z-icon-check");
        btnSign.setStyle("color: #000; border: solid 1px #ff7e00; font-size: 18px; font-weight: 600;");
        btnSign.addEventListener(Events.ON_CLICK, event -> {
            if (Objects.equals(workflowModel.getSignReq(), WorkflowModel.ACTION_PREVIEW_AND_UNSIGN_AND_SIGN) ||
                Objects.equals(workflowModel.getSignReq(), WorkflowModel.ACTION_PREVIEW_AND_UNSIGN_AND_SIGN_FINAL)) {
                Messagebox.show("Вы действительно желаете подписать документ?", "Внимание!", Messagebox.YES | Messagebox.NO,
                                Messagebox.QUESTION, event1 -> {
                            if (event1.getName().equals(Messagebox.ON_YES)) {
                                signPdf();
                            }
                        }
                );
            } else {
                Messagebox.show("Вы действительно желаете подтвердить?", "Внимание!", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
                                event12 -> {
                                    if (event12.getName().equals(Messagebox.ON_YES)) {
                                        confirm();
                                    }
                                }
                );
            }
        });

        Button btnCancel = new Button("Отправить на доработку");
        btnCancel.setParent(hbContent);
        btnCancel.setIconSclass("z-icon-ban");
        btnCancel.setStyle("color: #000; border: solid 1px #000; font-size: 18px;");
        btnCancel.addEventListener(Events.ON_CLICK, event -> {
            Map arg = new HashMap();
            arg.put(ViewCancelDialogPageCtrl.WORKFLOW_MODEL, workflowModel);
            arg.put(ViewCancelDialogPageCtrl.WIN_PDFVIEWER, winPdfViewer);
            ComponentHelper.createWindow("/utility/pdfViewer/winCancelDialog.zul", "winCancelDialog", arg).doModal();
        });
    }

    /**
     * Метод подписания.
     */
    public void signPdf() {
        String filePath = currentFile.getAbsolutePath();
        Clients.showBusy("Процесс подписания...");
        File pdf = new File(filePath);
        try {
            FileInputStream fis = new FileInputStream(pdf);
            byte[] buffer = IOUtils.toByteArray(fis);
            Base64 base64 = new Base64();
            String res = new String(base64.encode(buffer));
            String pdfname = workflowModel.getSubject();
            Clients.evalJavaScript("sign('" + res + "','" + pdfname + "')");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(currentUser.getFio() + ", ID task: " + workflowModel.getTaskId(), e);
        }
    }

    /**
     * Метод подтверждения.
     */
    private void confirm() {
        try {
            workflowModel.setAction(WorkflowModel.STATUS_SIGNED_EN);
            workflowModel.setStatus("Подтверждена");
            if (workflowService.updateOrderTask(workflowModel) != null) {
                IndexPageCtrl pageCtrl = (IndexPageCtrl) Executions.getCurrent().getSession().getAttribute("workflow_index_page");
                pageCtrl.refreshTaskList();
                Messagebox.show("Подтверждено.", "Готово!", Messagebox.OK, Messagebox.INFORMATION, event -> {
                    if (event.getName().equals(Messagebox.ON_OK)) {
                        winPdfViewer.detach();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(currentUser.getFio() + ", ID task: " + workflowModel.getTaskId(), e);
        }
    }

    @Listen("onFinish = #winPdfViewer;")
    public void signPdfFinish(Event ev) {
        Clients.clearBusy();
        FileInputStream fis = null;
        try {
            JSONObject evData = (JSONObject) ev.getData();
            String baseSign = (String) evData.get("Data");
            String serialNumber = (String) evData.get("SerialNumber");
            byte deArr[] = Base64.decodeBase64(baseSign);
            if (deArr.length == 0) {
                System.out.println("JavaScript error. Byte array not load.");
                Messagebox.show("Не получилось подписать. Обратитесь к администратору.", "Ошибка!", Messagebox.OK, Messagebox.ERROR);
            } else {
                String pathFile = currentFile.getAbsolutePath();
                FileOutputStream fos = new FileOutputStream(new File(pathFile));
                fos.write(deArr);
                fos.close();
                File file = new File(pathFile);
                if (!file.exists()) {
                    Messagebox.show("Не получилось подписать документ. Обратитесь к администратору.", "Ошибка!", Messagebox.OK,
                                    Messagebox.ERROR
                    );
                } else {
                    workflowModel.setAction(WorkflowModel.STATUS_SIGNED_EN);
                    workflowModel.setStatus("Подписана");
                    workflowModel.setCertNumber(serialNumber);
                    String status = workflowService.updateOrderTask(workflowModel);
                    if (status == null || status.equals("Error")) {
                        System.out.println("Error update data of task ID" + workflowModel.getTaskId() + ". Data respons not received.");
                        log.error("Error update data of task ID" + workflowModel.getTaskId() + ". Data respons not received.");
                        Messagebox.show("Ошибка обновления статуса задачи. Обратитесь к адмиинистратору.", "Ошибка!", Messagebox.OK,
                                        Messagebox.ERROR
                        );
                        return;
                    } else {
                        PopupUtil.showInfo("Документ успешно подписан.");
                    }
                    IndexPageCtrl pageCtrl = (IndexPageCtrl) Executions.getCurrent().getSession().getAttribute("workflow_index_page");
                    pageCtrl.refreshTaskList();
                    winPdfViewer.detach();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error(currentUser.getFio() + ", ID task: " + workflowModel.getTaskId(), e);
            Messagebox.show("Не удалось сохранить подписанный файл. Обратитесь к администратору.", "Ошибка!", Messagebox.OK,
                            Messagebox.ERROR
            );
        } catch (Exception e) {
            e.printStackTrace();
            log.error(currentUser.getFio() + ", ID task: " + workflowModel.getTaskId(), e);
            Messagebox.show("Неизвестная ошибка. Обратитесь к администратору.", "Ошибка!", Messagebox.OK, Messagebox.ERROR);
        }
    }

    @Listen("onErrorSign = #winPdfViewer")
    public void errorSign(Event ev) {
        Clients.clearBusy();
        DialogUtil.error(ev.getData().toString());
    }

    private void showFile(File file, Boolean sign) {
        while (vbContent.getChildren().size() > 1) {
            vbContent.getChildren().remove(1);
        }
        if (sign && workflowModel != null && !new TemplatePageCtrl().getCurrentModule().isReadonly()) {
            fillContent();
        }
        AMedia amedia = null;
        try {
            byte[] buffer = new byte[(int) file.length()];
            FileInputStream fs = new FileInputStream(file);
            fs.read(buffer);
            fs.close();
            ByteArrayInputStream is = new ByteArrayInputStream(buffer);
            amedia = new AMedia("file", "pdf", "application/pdf", is);
        } catch (IOException e) {
            e.printStackTrace();
            org.zkoss.zhtml.Messagebox.show("Проблемы с отображением документа, обратитесь к администраторам!");
        }
        iframePdfViwer.setContent(amedia);
        iframePdfViwer.setVflex("1");
        iframePdfViwer.setHflex("1");
    }

    private void fillListboxes() {
        for (File file : documents) {
            if (file.isFile()) {
                addListcell(lbDocSign, file.getName(), file, true);
            } else {
                if (file.getName().equals("attach")) {
                    for (File fileAttach : file.listFiles()) {
                        addListcell(lbAttach, fileAttach.getName(), fileAttach, false);
                    }
                } else if (file.getName().equals("canceled")) {
                    for (File fileCanceled : file.listFiles()) {
                        addListcell(lbCanceled, fileCanceled.getName(), fileCanceled, false);
                    }
                }
            }
        }
    }

    private void fillFowWorkflow() {
        Hbox hboxTitle = new Hbox();
        hboxTitle.setParent(vbDocs);
        hboxTitle.setHflex("1");
        hboxTitle.setAlign("center");
        hboxTitle.setStyle("background: #5f8a96; border-bottom: none; height: 30px;");
        Span spanTitle = new Span();
        spanTitle.setParent(hboxTitle);
        spanTitle.setClass("z-icon-info-circle");
        spanTitle.setStyle("margin-left: 5px; font-size: 20px; color: #fff;");
        Label lTitle = new Label("Информация");
        lTitle.setParent(hboxTitle);
        lTitle.setStyle(
                "color: #fff; font-family: opensans,arial,freesans,sans-serif; font-weight: 700; font-size: 15px; font-style: normal;");

        Vbox vboxName = new Vbox();
        vboxName.setParent(vbDocs);
        Label lName = new Label("Название:");
        lName.setParent(vboxName);
        lName.setStyle("font-family: opensans,arial,freesans,sans-serif; font-weight: 700; font-size: 15px; font-style: normal;");
        Label lNameInfo = new Label(workflowModel.getSubject());
        lNameInfo.setParent(vboxName);
        lNameInfo.setStyle("font-family: opensans,arial,freesans,sans-serif; font-size: 15px; font-style: normal;");

        Vbox vboxType = new Vbox();
        vboxType.setParent(vbDocs);
        Label lType = new Label("Тип:");
        lType.setParent(vboxType);
        lType.setStyle("font-family: opensans,arial,freesans,sans-serif; font-weight: 700; font-size: 15px; font-style: normal;");
        Label lTypeInfo = new Label(workflowModel.getOrderType());
        lTypeInfo.setParent(vboxType);
        lTypeInfo.setStyle("font-family: opensans,arial,freesans,sans-serif; font-size: 15px; font-style: normal;");
    }

    private void addListcell(Listbox listbox, String name, File file, boolean sign) {
        Listitem li = new Listitem(name);
        li.setParent(listbox);
        li.addEventListener(Events.ON_CLICK, getEvent(file, sign));
    }

    private EventListener<Event> getEvent(final File file, final Boolean sign) {
        return event -> {
            currentFile = file;
            showFile(file, sign);
        };
    }
}
