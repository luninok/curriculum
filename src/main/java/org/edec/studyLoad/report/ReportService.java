package org.edec.studyLoad.report;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.utility.fileManager.FileModel;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.zkoss.zk.ui.Executions;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lunin on 05.12.2018.
 */
public class ReportService {

    private ServletContext servletContext;
    private ReportModelService reportModelService = new ReportModelService();

    public JasperReport getJasperForAssignments(String localPath, Long idSem, Long idDepartment, String nameDepartment) {
        JRBeanCollectionDataSource data = reportModelService.getBeanDataForAssignments(idSem, idDepartment, nameDepartment);
        Map<String, Object> arg = new HashMap<>();
        FileModel fileModel = null;
        return new JasperReport("Поручения", getRealPath("") + localPath, arg, data, fileModel, null,
                servletContext
        );
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
