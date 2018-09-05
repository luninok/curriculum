package org.edec.utility.report.model.commission;

import java.util.Date;

public class ScheduleSubjectModel {
    private Date datecommission;

    private String classroom;
    private String subjectname;

    public ScheduleSubjectModel () {
    }

    public Date getDatecommission () {
        return datecommission;
    }

    public void setDatecommission (Date datecommission) {
        this.datecommission = datecommission;
    }

    public String getClassroom () {
        return classroom;
    }

    public void setClassroom (String classroom) {
        this.classroom = classroom;
    }

    public String getSubjectname () {
        return subjectname;
    }

    public void setSubjectname (String subjectname) {
        this.subjectname = subjectname;
    }
}
