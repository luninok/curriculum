package org.edec.register.ctrl;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.register.model.report.RegisterDateModel;
import org.edec.register.service.dao.RegisterReportServiceImpl;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.model.jasperReport.ReportSrc;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WinGetRegisterReportCtrl extends SelectorComposer<Component> {
    @Wire
    private Datebox dateOfBeginRegisterReport, dateOfEndRegisterReport;
    @Wire
    private Button btnGetRegisterReport;

    private SemesterModel sem = new SemesterModel();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        sem = (SemesterModel) Executions.getCurrent().getArg().get("Semester");
    }

    @Listen("onClick = #btnGetRegisterReport")
    public void getRegisterReport() throws IOException {
        Date dateOfBegin = dateOfBeginRegisterReport.getValue();
        Date dateOfEnd = dateOfEndRegisterReport.getValue();

        JasperReportService service = new JasperReportService();
        JasperReport jasperReport = service.getReportForRegisters(dateOfBegin, dateOfEnd, sem);

        if(jasperReport == null) {
            PopupUtil.showInfo("Нет ведомостей за выбранный период");
        } else {
            jasperReport.showPdf();
        }
    }
}
