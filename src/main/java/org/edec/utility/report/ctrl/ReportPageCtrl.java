package org.edec.utility.report.ctrl;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.apache.commons.codec.binary.Base64;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.sign.service.SignService;
import org.zkoss.json.JSONObject;
import org.zkoss.util.media.AMedia;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;

import java.io.ByteArrayInputStream;
import java.util.Map;

/**
 * Created by dmmax
 */
public class ReportPageCtrl extends SelectorComposer<Component> {
    public static final String ARG = "arg";
    public static final String BEAN_DATA = "bean_data";
    public static final String FILE_NAME = "file_name";
    public static final String JASPER_FILE = "jasper_file";
    public static final String SIGN_SERVICE = "sign_service";

    @Wire
    private Hbox hbBtn;

    @Wire
    private Iframe iframeReport;

    @Wire
    private Window winReport;

    private byte[] buffer = null;
    private String jasperFile;
    private String fileName;
    private Map arg;
    private Object beanData;
    private SignService signService;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        jasperFile = (String) Executions.getCurrent().getArg().get(JASPER_FILE);
        fileName = (String) Executions.getCurrent().getArg().get(FILE_NAME);
        arg = (Map) Executions.getCurrent().getArg().get(ARG);
        beanData = Executions.getCurrent().getArg().get(BEAN_DATA);
        signService = (SignService) Executions.getCurrent().getArg().get(SIGN_SERVICE);

        //Для возможности добавить кнопку подписать
        if (signService != null) {
            Button btnSign = new Button("Подписать");
            btnSign.setStyle("font-size: 12pt;");
            btnSign.setTooltiptext("Подписать документ, используя LSS");
            btnSign.setId("btnSignFile");
            btnSign.setParent(hbBtn);
        }
        generateJasper(jasperFile, fileName, arg, beanData, "pdf");
    }

    private void generateJasper(String jasperFile, String fileName, Map arg, Object beanData, String type) {
        try {
            if (beanData != null) {
                if (beanData instanceof JRBeanCollectionDataSource) {
                    buffer = JasperRunManager.runReportToPdf(jasperFile, arg, (JRBeanCollectionDataSource) beanData);
                } else if (beanData instanceof JRMapCollectionDataSource) {
                    buffer = JasperRunManager.runReportToPdf(jasperFile, arg, (JRMapCollectionDataSource) beanData);
                }
            } else {
                buffer = JasperRunManager.runReportToPdf(jasperFile, arg, new JREmptyDataSource());
            }

            ByteArrayInputStream is = new ByteArrayInputStream(buffer);
            AMedia amedia = new AMedia(fileName + "." + type, type, "application/" + type, is);
            iframeReport.setContent(amedia);
            iframeReport.setHflex("1");
            iframeReport.setVflex("1");
        } catch (JRException e) {
            e.printStackTrace();
            PopupUtil.showError("Проблемы с отображением документа, обратитесь к администратору!");
            winReport.detach();
        }
    }

    @Listen("onClick = #btnSignFile")
    public void signFile() {
        Clients.showBusy("Процесс подписания...");
        //Считываем PDF из фрэйма
        if (buffer == null) {
            buffer = iframeReport.getContent().getByteData();
        }

        String res = new String(new Base64().encode(buffer));
        Clients.evalJavaScript("sign('" + res + "','" + fileName + "')");
    }

    /**
     * Заглушка для получение обратной связи от JS из окно winReport
     */
    @Listen("onFinish = #winReport")
    public void finishSign(Event ev) {
        JSONObject evData = (JSONObject) ev.getData();
        String baseSign = (String) evData.get("Data");
        String serialNumber = (String) evData.get("SerialNumber");
        String thumbPrint = (String) evData.get("Thumbprint");
        byte[] deArr = Base64.decodeBase64(baseSign);

        if (!signService.createFileAndUpdateUI(deArr, serialNumber, thumbPrint)) {
            PopupUtil.showError("Не удалось подписать файл! Обратитесь к администратору");
        } else {
            PopupUtil.showInfo("Файл успешно подписан!");
            hbBtn.setVisible(false);
        }
        Clients.clearBusy();
    }

    /*public void finishSignTest() {
        String serialNumber = "88005553535";
        String thumbPrint = "12345";
        byte[] deArr = iframeReport.getContent().getByteData();

        if (!signService.createFileAndUpdateUI(deArr, serialNumber, thumbPrint))
            Messagebox.show("Не удалось подписать файл! Обратитесь к администратору");
        else
        {
            Messagebox.show("Файл успешно подписан!");
            hbBtn.setVisible(false);
        }
        Clients.clearBusy();
    }*/

    /**
     * Функция ожидающая провал подписи
     */
    @Listen("onErrorSign = #winReport")
    public void errorSign(Event ev) {
        Clients.clearBusy();
        DialogUtil.error(ev.getData().toString());
    }
}
