package org.edec.utility.pdfViewer.ctrl;

import org.edec.utility.zk.PopupUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by dmmax
 */
public class PdfViewerCtrl extends SelectorComposer<Component> {
    public static final String FILE = "file";

    @Wire
    private Hbox hbBtnPdfViewer;

    @Wire
    private Iframe iframePdfViwer;

    @Wire
    private Window winPdfViewer;

    private File file;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        file = (File) Executions.getCurrent().getArg().get(FILE);
        showFile();
    }

    private void showFile () {
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
            PopupUtil.showError("Проблемы с отображением документа, обратитесь к администраторам!");
            winPdfViewer.detach();
            return;
        }
        iframePdfViwer.setContent(amedia);
        iframePdfViwer.setVflex("1");
        iframePdfViwer.setHflex("1");
    }
}
