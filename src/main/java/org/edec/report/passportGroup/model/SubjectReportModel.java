package org.edec.report.passportGroup.model;

import lombok.Getter;
import lombok.Setter;
import org.edec.model.SubjectModel;

@Getter
@Setter
public class SubjectReportModel extends SubjectModel {
    private double hourscount;

    public SubjectReportModel (String subjectname, String foc, double hourscount) {
        setSubjectname(subjectname);
        setFoc(foc);
        this.hourscount = hourscount;
    }
}
