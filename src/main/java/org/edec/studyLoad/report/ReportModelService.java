package org.edec.studyLoad.report;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.studyLoad.model.AssignmentModel;
import org.edec.studyLoad.model.report.AssigmentsReportModel;
import org.edec.studyLoad.model.report.AssignmentModelReport;
import org.edec.studyLoad.service.StudyLoadService;
import org.edec.studyLoad.service.impl.StudyLoadServiceImpl;
import org.zkoss.zul.A;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lunin on 05.12.2018.
 */
public class ReportModelService {

    private StudyLoadService studyLoadService = new StudyLoadServiceImpl();

    public JRBeanCollectionDataSource getBeanDataForAssignments (List<AssignmentModel> assignments, String nameDepartment) {

        List<AssignmentModelReport> assignmentModelReports = new ArrayList<>();

        for(AssignmentModel assignment : assignments) {
            AssignmentModelReport assignmentModelReport = new AssignmentModelReport();
            assignmentModelReport.setFio(assignment.getFioWithShortInitials());
            assignmentModelReport.setGroupname(assignment.getGroupName());
            assignmentModelReport.setNameDiscipline(assignment.getNameDiscipline());
            assignmentModelReport.setControl(assignment.getTypeControl());
            assignmentModelReport.setCourse(assignment.getCourse().toString());
            assignmentModelReport.setHours(assignment.getHoursCount().toString());
            assignmentModelReport.setAssignment(assignment.getAssignment() == null ? "" : assignment.getAssignment());
            assignmentModelReports.add(assignmentModelReport);
        }

        List<AssigmentsReportModel> list = new ArrayList<>();
        AssigmentsReportModel reportModel = new AssigmentsReportModel();
        reportModel.setNameDepartment(nameDepartment);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        reportModel.setDateNow(format.format(new Date()));
        reportModel.setAssignmentsList(assignmentModelReports);
        list.add(reportModel);

        return new JRBeanCollectionDataSource(list);
    }
}
