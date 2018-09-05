package org.edec.utility.report.model.commission.dao;

import java.util.Date;

public class CommissionScheduleEso {
    private Date datecommission;

    private String classroom;
    private String fulltitle;
    private String subjectname;

    public CommissionScheduleEso () {
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

    public String getFulltitle () {
        return fulltitle;
    }

    public void setFulltitle (String fulltitle) {
        this.fulltitle = fulltitle;
    }

    public String getSubjectname () {
        return subjectname;
    }

    public void setSubjectname (String subjectname) {
        this.subjectname = subjectname;
    }
}
