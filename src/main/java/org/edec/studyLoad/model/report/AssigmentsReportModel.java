package org.edec.studyLoad.model.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.studyLoad.model.AssignmentModel;;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AssigmentsReportModel {

    private String nameDepartment;

    private String dateNow;

    private List<AssignmentModelReport> assignmentsList = new ArrayList<>();
}
