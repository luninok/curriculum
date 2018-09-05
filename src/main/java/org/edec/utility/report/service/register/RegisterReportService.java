package org.edec.utility.report.service.register;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.teacher.model.GroupModel;
import org.edec.teacher.model.register.RegisterModel;
import org.edec.teacher.model.register.RegisterRowModel;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.report.manager.RegisterReportDAO;
import org.edec.utility.report.model.register.RegisterJasperModel;
import org.edec.utility.report.model.register.StudentModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class RegisterReportService {
    private RegisterReportDAO registerReportDAO = new RegisterReportDAO();

    public JRBeanCollectionDataSource getBeanData(Long idRegister, Long idHum) {
        RegisterJasperModel register = registerReportDAO.getRegisterReportModel(idRegister, idHum);
        FormOfControlConst foc = FormOfControlConst.getName(register.getFormOfControl());

        String registerNumber = register.getRegisterNumber() == null ? "" : register.getRegisterNumber();
        if (register.getRetakeCount() > 1) {
            register.setTypeOfRegister("ВЕДОМОСТЬ ПЕРЕСДАЧИ № " + registerNumber);
        } else {
            if (foc == FormOfControlConst.EXAM) {
                register.setTypeOfRegister("ЭКЗАМЕНАЦИОННАЯ ВЕДОМОСТЬ № " + registerNumber);
            } else {
                register.setTypeOfRegister("ЗАЧЕТНАЯ ВЕДОМОСТЬ № " + registerNumber);
            }
        }

        if (foc == FormOfControlConst.CP) {
            register.setCoWorkOrProj("КУРСОВОЙ ПРОЕКТ");
        } else if (foc == FormOfControlConst.CW) {
            register.setCoWorkOrProj("КУРСОВАЯ РАБОТА");
        } else {
            register.setCoWorkOrProj("");
        }

        if (foc == FormOfControlConst.EXAM || foc == FormOfControlConst.PRACTIC) {
            register.setDateOfExaminationTitle("ДАТА СДАЧИ ЭКЗАМЕНА");
        } else {
            register.setDateOfExaminationTitle("ДАТА СДАЧИ ЗАЧЕТА");
        }

        List<StudentModel> students = registerReportDAO.getStudentByRegister(idRegister);
        register.setStudents(students);

        //studentA - отличник, B - ударник, C - УДОВЛ. , D - неУДОВЛ., E - не явка
        int studentA = 0, studentB = 0, studentC = 0, studentD = 0, studentE = 0;
        for (StudentModel student : students) {
            int rating = student.getRating();
            if (rating == 1 || rating == 5) {
                studentA++;
            } else if (rating == 4) {
                studentB++;
            } else if (rating == 3) {
                studentC++;
            } else if (rating == 2 || rating == -2) {
                studentD++;
            } else if (rating == -3) {
                studentE++;
            }
        }

        String marksCount = "";
        if (foc == FormOfControlConst.PASS && register.getType() == 0) {
            if (studentA + studentD + studentE == students.size()) {
                marksCount = "ЗАЧТЕНО: " + String.valueOf(studentA) + " НЕ АТТЕСТОВАНО: " + String.valueOf(studentE + studentD);
            } else {
                marksCount = "ЗАЧТЕНО:\tНЕ АТТЕСТОВАНО:  ";
            }
        } else {
            if (studentA + studentB + studentC + studentD + studentE == students.size()) {
                marksCount = "ОТЛИЧНО: " + studentA + " ХОРОШО: " + studentB + " УДОВЛ.: " + studentC + " НЕУД.: " + studentD +
                             " НЕ АТТЕСТОВАНО: " + studentE;
            } else {
                marksCount = "ОТЛИЧНО:    ХОРОШО:    УДОВЛ.:    НЕУД.:    НЕ АТТЕСТОВАНО: ";
            }
        }
        register.setMarksCount(marksCount);

        List<RegisterJasperModel> data = new ArrayList<>();
        data.add(register);
        return new JRBeanCollectionDataSource(data);
    }

    public JRBeanCollectionDataSource getBeanDataWithoutMarks(int formOfControl, GroupModel curGroup, RegisterModel mainRegister) {
        RegisterJasperModel register = new RegisterJasperModel();
        register.setFormOfControl(formOfControl);
        register.setSubject(curGroup.getSubject().getSubjectname());
        register.setGroupname(curGroup.getGroupname());
        register.setCourse(curGroup.getCourse().toString());
        register.setTotalHours(curGroup.getHoursCount() + "(" + curGroup.getHoursCount() / 36 + "зач.ед.)");

        register.setSemester(curGroup.getSemesterNumber().toString());

        for (RegisterRowModel ratingModel : mainRegister.getListRegisterRow()) {
            StudentModel studentModel = new StudentModel();

            studentModel.setFio(ratingModel.getStudentFullName());
            studentModel.setRecordBook(ratingModel.getRecordbookNumber());

            register.getStudents().add(studentModel);
        }

        ArrayList<RegisterJasperModel> list = new ArrayList<>();
        list.add(register);

        return new JRBeanCollectionDataSource(list);
    }
}
