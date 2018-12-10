package org.edec.studyLoad.report;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.edec.studyLoad.model.AssignmentModel;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.fileManager.FileModel;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.zkoss.zk.ui.Executions;

import javax.servlet.ServletContext;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lunin on 05.12.2018.
 */
public class ReportService {

    private ServletContext servletContext;
    private ReportModelService reportModelService = new ReportModelService();

    public JasperReport getJasperForAssignments(String localPath, List<AssignmentModel> assignments, String nameDepartment) {
        JRBeanCollectionDataSource data = reportModelService.getBeanDataForAssignments(assignments, nameDepartment);
        Map<String, Object> arg = new HashMap<>();
        FileModel fileModel = null;
        return new JasperReport("Поручения", getRealPath("") + localPath, arg, data, fileModel, null,
                servletContext
        );
    }

    public byte[] getXlsxForAssignments(List<AssignmentModel> assignments) {
        Workbook book = new HSSFWorkbook();

        Sheet sheet = book.createSheet("Поручения");
        setHeadersForSheet(sheet);

        for (int i = 0; i < assignments.size(); i++) {
            setContent(sheet, assignments.get(i), i + 1);
        }

        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            book.write(bos);
            bos.close();
            return bos.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private void setHeadersForSheet(Sheet sheet) {
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("№");
        row.createCell(1).setCellValue("ФИО");
        row.createCell(2).setCellValue("Дисциплина");
        row.createCell(3).setCellValue("Группа(ы)");
        row.createCell(4).setCellValue("Контроль");
        row.createCell(5).setCellValue("Курс");
        row.createCell(6).setCellValue("Часов");
        row.createCell(7).setCellValue("Пожелания");
    }

    private void setContent(Sheet sheet, AssignmentModel assignment, int i) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        row.createCell(0).setCellValue(Integer.toString(i));
        row.createCell(1).setCellValue(assignment.getFioWithShortInitials());
        row.createCell(2).setCellValue(assignment.getNameDiscipline());
        row.createCell(3).setCellValue(assignment.getGroupName());
        row.createCell(4).setCellValue(assignment.getTypeInstructionString());
        row.createCell(5).setCellValue(Integer.toString(assignment.getCourse()));
        row.createCell(6).setCellValue(Integer.toString(assignment.getHoursCount()));
        row.createCell(7).setCellValue(assignment.getAssignment());
    }

    private String getRealPath(String relativePath) {
        if (Executions.getCurrent() != null) {
            return Executions.getCurrent().getDesktop().getWebApp().getRealPath("WEB-INF/reports/" + relativePath);
        } else if (servletContext != null) {
            return servletContext.getRealPath("WEB-INF/reports/" + relativePath);
        }

        return "";
    }
}
