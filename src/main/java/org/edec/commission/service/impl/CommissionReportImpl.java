package org.edec.commission.service.impl;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.edec.commission.manager.EntityManagerReportCommission;
import org.edec.commission.model.CommissionStudentReportModel;
import org.edec.commission.service.CommissionReportService;
import org.edec.utility.converter.DateConverter;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

public class CommissionReportImpl implements CommissionReportService {
    private EntityManagerReportCommission emReportCommission = new EntityManagerReportCommission();

    @Override
    public byte[] getXlsxForStudentCommission (int formofstudy, Date dateOfBegin, Date dateOfEnd) {
        List<CommissionStudentReportModel> students = emReportCommission.getStudentForReport(formofstudy, dateOfBegin, dateOfEnd);
        Workbook book = new HSSFWorkbook();

        Sheet sheet = book.createSheet("Студенты в комиссиях");
        setHeadersForSheet(sheet);

        for (CommissionStudentReportModel student : students) {
            setContent(sheet, student);
        }

        for (int i = 0; i < 9; i++) {
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

    private void setHeadersForSheet (Sheet sheet) {
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("ФИО");
        row.createCell(1).setCellValue("Платник");
        row.createCell(2).setCellValue("Группа");
        row.createCell(3).setCellValue("Дисциплина");
        row.createCell(4).setCellValue("ФК");
        row.createCell(5).setCellValue("Оценка");
        row.createCell(6).setCellValue("Семестр");
        row.createCell(7).setCellValue("Кафедра");
        row.createCell(8).setCellValue("Дата комиссии");
        row.createCell(9).setCellValue("Аудитория");
    }

    private void setContent (Sheet sheet, CommissionStudentReportModel commissionStudent) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        row.createCell(0).setCellValue(commissionStudent.getFio());
        row.createCell(1).setCellValue(!commissionStudent.getGovernmentFinanced() ? "Да" : "Нет");
        row.createCell(2).setCellValue(commissionStudent.getGroupname());
        row.createCell(3).setCellValue(commissionStudent.getSubjectname());
        row.createCell(4).setCellValue(commissionStudent.getFocStr());
        row.createCell(5).setCellValue(commissionStudent.getRating());
        row.createCell(6).setCellValue(commissionStudent.getSemesterStr());
        row.createCell(7).setCellValue(commissionStudent.getChair());
        row.createCell(8)
           .setCellValue(commissionStudent.getCommissionDate() != null ? DateConverter.convertTimestampToString(
                   commissionStudent.getCommissionDate()) : "");
        row.createCell(9).setCellValue(commissionStudent.getClassroom());
    }
}
