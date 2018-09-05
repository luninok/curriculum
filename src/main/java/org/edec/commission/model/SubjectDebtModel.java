package org.edec.commission.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dmmax
 */
public class SubjectDebtModel implements Comparable<SubjectDebtModel> {
    private boolean signed;

    private Date dateComission;
    private Date dateofbegincomission;
    private Date dateofendcomission;

    private Integer checkedcount;
    private Integer countstudent;
    private Integer semesternumber;

    private Long idChair;
    private Long idSemester;
    private Long idSubj;
    private Long idRegComission;

    private String classroom;
    private String focStr;
    private String fulltitle;
    private String semesterStr;
    private String subjectname;

    private List<StudentDebtModel> students = new ArrayList<>();

    public SubjectDebtModel () {
    }

    public Date getDateComission () {
        return dateComission;
    }

    public void setDateComission (Date dateComission) {
        this.dateComission = dateComission;
    }

    public Date getDateofbegincomission () {
        return dateofbegincomission;
    }

    public void setDateofbegincomission (Date dateofbegincomission) {
        this.dateofbegincomission = dateofbegincomission;
    }

    public Date getDateofendcomission () {
        return dateofendcomission;
    }

    public void setDateofendcomission (Date dateofendcomission) {
        this.dateofendcomission = dateofendcomission;
    }

    public Integer getSemesternumber () {
        return semesternumber;
    }

    public void setSemesternumber (Integer semesternumber) {
        this.semesternumber = semesternumber;
    }

    public Long getIdChair () {
        return idChair;
    }

    public void setIdChair (Long idChair) {
        this.idChair = idChair;
    }

    public Long getIdSubj () {
        return idSubj;
    }

    public void setIdSubj (Long idSubj) {
        this.idSubj = idSubj;
    }

    public Long getIdRegComission () {
        return idRegComission;
    }

    public void setIdRegComission (Long idRegComission) {
        this.idRegComission = idRegComission;
    }

    public String getClassroom () {
        return classroom;
    }

    public void setClassroom (String classroom) {
        this.classroom = classroom;
    }

    public String getFocStr () {
        return focStr;
    }

    public void setFocStr (String focStr) {
        this.focStr = focStr;
    }

    public String getFulltitle () {
        return fulltitle == null ? "" : fulltitle;
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

    public List<StudentDebtModel> getStudents () {
        return students;
    }

    public void setStudents (List<StudentDebtModel> students) {
        this.students = students;
    }

    public String getSemesterStr () {
        return semesterStr;
    }

    public void setSemesterStr (String semesterStr) {
        this.semesterStr = semesterStr;
    }

    public boolean isSigned () {
        return signed;
    }

    public void setSigned (boolean signed) {
        this.signed = signed;
    }

    public Integer getCheckedcount () {
        return checkedcount;
    }

    public void setCheckedcount (Integer checkedcount) {
        this.checkedcount = checkedcount;
    }

    public Integer getCountstudent () {
        return countstudent;
    }

    public void setCountstudent (Integer countstudent) {
        this.countstudent = countstudent;
    }

    @Override
    public int compareTo (SubjectDebtModel o) {
        if (this.getIdChair().compareTo(o.getIdChair()) == 0) {
            return o.getFocStr().compareTo(this.getFocStr());
        } else {
            return o.getIdChair().compareTo(this.getIdChair());
        }
    }

    public Long getIdSemester () {
        return idSemester;
    }

    public void setIdSemester (Long idSemester) {
        this.idSemester = idSemester;
    }
}
