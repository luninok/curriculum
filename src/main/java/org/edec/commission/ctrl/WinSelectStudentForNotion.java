package org.edec.commission.ctrl;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.commission.report.model.NotionMainModel;
import org.edec.commission.report.model.NotionModel;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.model.jasperReport.ReportSrc;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by apple on 19.10.17.
 */
public class WinSelectStudentForNotion extends SelectorComposer<Component> {
    public static final String STUDENTS = "students";
    public static final String IS_DOCX = "idDocx";

    @Wire
    Listbox lbStudents;

    @Wire
    Datebox dbDateNotion;

    private List<NotionModel> notions;
    private Boolean isDocx;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);

        dbDateNotion.setValue(new Date());

        notions = (List<NotionModel>) Executions.getCurrent().getArg().get(STUDENTS);
        isDocx = (Boolean) Executions.getCurrent().getArg().get(IS_DOCX);
        lbStudents.setItemRenderer((Listitem listitem, NotionModel notionModel, int i) -> {
            new Listcell(notionModel.getFio()).setParent(listitem);
            new Listcell(notionModel.getGroupname()).setParent(listitem);

            Checkbox checkbox = new Checkbox();
            checkbox.setChecked(true);
            checkbox.addEventListener(Events.ON_CHECK, event -> notionModel.setChecked(checkbox.isChecked()));
            Listcell cell = new Listcell();
            cell.appendChild(checkbox);
            cell.setParent(listitem);
        });

        lbStudents.setModel(new ListModelList<>(notions));
    }

    @Listen("onClick = #btnPrint")
    public void onClickPrint () {
        List<NotionModel> listToPrint = new ArrayList<>();
        notions.forEach(notionModel -> {
            notionModel.setDateNotion(new SimpleDateFormat("dd.MM.yyyy").format(dbDateNotion.getValue()));
            if (notionModel.getChecked()) {
                listToPrint.add(notionModel);
            }
        });

        printReport(listToPrint);
    }

    private void printReport (List<NotionModel> listToPrint) {
        List<NotionMainModel> mainModels = new ArrayList<>();
        NotionMainModel model = new NotionMainModel();
        model.setNotions(listToPrint);
        mainModels.add(model);

        JasperReportService jasperReportService = new JasperReportService();

        JasperReport jasperReport = jasperReportService.printReport(mainModels);

        if (isDocx) {
            jasperReport.downloadDocx();
        } else {
            jasperReport.showPdf();
        }
    }
}
