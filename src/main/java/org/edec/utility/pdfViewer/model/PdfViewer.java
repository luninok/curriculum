package org.edec.utility.pdfViewer.model;

import org.edec.utility.fileManager.FileManager;
import org.edec.utility.pdfViewer.ctrl.PdfViewerCtrl;
import org.edec.utility.pdfViewer.ctrl.PdfViewerDocumentsCtrl;
import org.edec.utility.zk.ComponentHelper;
import org.edec.workflow.model.WorkflowModel;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by dmmax
 */
public class PdfViewer {
    private FileManager fileManager = new FileManager();

    private String relativePath;
    private WorkflowModel workflowModel;
    private String[] relativePaths;

    public PdfViewer (String relativePath) {
        this.relativePath = relativePath;
    }

    public PdfViewer (WorkflowModel workflowModel) {
        this.workflowModel = workflowModel;
    }

    //для паспорта групп
    public PdfViewer (String[] relativePaths) {
        this.relativePaths = relativePaths;
    }

    public void showPdf () {
        if (Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("#winPdfViewer") != null) {
            Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("#winPdfViewer").detach();
        }

        Map arg = new HashMap();
        arg.put(PdfViewerCtrl.FILE, fileManager.getFileByRelativePath(relativePath));
        ComponentHelper.createWindow("/utility/pdfViewer/index.zul", "winPdfViewer", arg).doModal();
    }

    public void showRegisters () {
        if (Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("#winPdfViewer") != null) {
            Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("#winPdfViewer").detach();
        }

        Map arg = new HashMap();
        File[] files = new File[relativePaths.length];
        for (int i = 0; i < files.length; ++i) {
            files[i] = fileManager.getFileByRelativePath(relativePaths[i]);
        }

        arg.put(PdfViewerDocumentsCtrl.DOCUMENTS, files);
        ComponentHelper.createWindow("/utility/pdfViewer/documents.zul", "winPdfViewer", arg).doModal();
    }

    public void showDirectory () {
        if (Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("#winPdfViewer") != null) {
            Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("#winPdfViewer").detach();
        }

        Map arg = new HashMap();
        if (relativePath != null) {
            arg.put(PdfViewerDocumentsCtrl.DOCUMENTS, fileManager.getFilesByRelativePath(relativePath));
        } else if (workflowModel != null) {
            arg.put(PdfViewerDocumentsCtrl.WORKFLOW_MODEL, workflowModel);
            String regular = "((\\\\)|(/))(order)*(\\d)*(\\u002E)(pdf)";
            workflowModel.setPathFile(workflowModel.getPathFile().toLowerCase());
            if (workflowModel.getPathFile().endsWith(".pdf")) {
                workflowModel.setPathFile(workflowModel.getPathFile().replaceAll(regular, ""));
            }
            arg.put(PdfViewerDocumentsCtrl.DOCUMENTS, fileManager.getFilesByFullPath(workflowModel.getPathFile()));
        }

        ComponentHelper.createWindow("/utility/pdfViewer/documents.zul", "winPdfViewer", arg).doModal();
    }
}
