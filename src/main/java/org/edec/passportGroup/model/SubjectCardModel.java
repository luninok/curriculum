package org.edec.passportGroup.model;

import org.edec.utility.constants.FinalGrade;

public class SubjectCardModel extends SubjectModel {
    private FinalGrade grade;
    private String status;

    public FinalGrade getGrade () {
        return grade;
    }

    public void setGrade (FinalGrade grade) {
        this.grade = grade;
    }

    public String getStatus () {
        return status;
    }

    public void setStatus (String status) {
        this.status = status;
    }
}
