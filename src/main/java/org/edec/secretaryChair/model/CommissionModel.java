package org.edec.secretaryChair.model;

import java.util.Date;

/**
 * @author Max Dimukhametov
 */
public class CommissionModel {
    private Long id;
    private Long idRegister;
    private Long idChair;
    private Long idSubject;

    private Date dateBegin;
    private Date dateEnd;
    private Date commissionDate;

    private Integer formOfControl;
    private Integer status;

    private String subjectName;
    private String semesterStr;
    private String classroom;

    public CommissionModel () {

    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public Long getIdRegister () {
        return idRegister;
    }

    public void setIdRegister (Long idRegister) {
        this.idRegister = idRegister;
    }

    public Long getIdChair () {
        return idChair;
    }

    public void setIdChair (Long idChair) {
        this.idChair = idChair;
    }

    public Date getDateBegin () {
        return dateBegin;
    }

    public void setDateBegin (Date dateBegin) {
        this.dateBegin = dateBegin;
    }

    public Date getDateEnd () {
        return dateEnd;
    }

    public void setDateEnd (Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Long getIdSubject () {
        return idSubject;
    }

    public void setIdSubject (Long idSubject) {
        this.idSubject = idSubject;
    }

    public String getSubjectName () {
        return subjectName;
    }

    public void setSubjectName (String subjectName) {
        this.subjectName = subjectName;
    }

    public Integer getFormOfControl () {
        return formOfControl;
    }

    public void setFormOfControl (Integer formOfControl) {
        this.formOfControl = formOfControl;
    }

    public String getSemesterStr () {
        return semesterStr;
    }

    public void setSemesterStr (String semesterStr) {
        this.semesterStr = semesterStr;
    }

    public Date getCommissionDate () {
        return commissionDate;
    }

    public void setCommissionDate (Date commissionDate) {
        this.commissionDate = commissionDate;
    }

    public String getClassroom () {
        return classroom;
    }

    public void setClassroom (String classroom) {
        this.classroom = classroom;
    }

    public Integer getStatus () {
        return status;
    }

    public void setStatus (Integer status) {
        this.status = status;
    }
}
