package org.edec.commission.report.model.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dmmax
 */
public class ScheduleSubjectModel {
    private Date datecommission;

    private String classroom;
    private String subjectname;

    private List<StudentModel> students = new ArrayList<>();

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

    public List<StudentModel> getStudents () {
        return students;
    }

    public void setStudents (List<StudentModel> students) {
        this.students = students;
    }
}
