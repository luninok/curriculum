package org.edec.newOrder.service;

import org.edec.newOrder.model.enums.DocumentEnum;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.enums.ParamEnum;
import org.edec.newOrder.report.ReportService;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.report.model.jasperReport.JasperReport;

import java.util.Date;
import java.util.HashMap;

public class DocumentService {
    private FileManager manager = new FileManager();
    private ReportService reportService = new ReportService();

    public void generateDocument (DocumentEnum documentEnum, OrderEditModel order, HashMap<ParamEnum, Object> params) {
        // TODO
        JasperReport document = null;

        switch (documentEnum) {
            case SET_ELIMINATION_NOTION:
                document = reportService.getJasperReportForServiceNote(order, (Date) params.get(ParamEnum.DATE_FROM),
                                                                       (String) params.get(ParamEnum.SEMESTER)
                );
                break;
        }

        if (document == null) {
            return;
        }

        manager.createAttachForOrderUrl(order, document.getFile(), (String) params.get(ParamEnum.DOCUMENT_NAME));
    }
}
