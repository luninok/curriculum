package org.edec.teacher.model.dao;

import java.util.Date;

/**
 * @author Max Dimukhametov
 */
public class CompletionCommESOmodel {
    private Boolean curSem;

    private Date dateOfBeginSY;
    private Date dateOfCommission;

    private Integer formOfControl;
    private Integer formofstudy;
    private Integer season;
    private Integer type;

    private Long idRC;
    private Long idReg;
    private Long idSem;

    private String certnumber;
    private String course;
    private String institute;
    private String regNumber;
    private String semesterStr;
    private String signatorytutor;
    private String subjectName;

    public CompletionCommESOmodel () {
    }

    public Boolean getCurSem () {
        return curSem;
    }

    public void setCurSem (Boolean curSem) {
        this.curSem = curSem;
    }

    public Date getDateOfBeginSY () {
        return dateOfBeginSY;
    }

    public void setDateOfBeginSY (Date dateOfBeginSY) {
        this.dateOfBeginSY = dateOfBeginSY;
    }

    public Date getDateOfCommission () {
        return dateOfCommission;
    }

    public void setDateOfCommission (Date dateOfCommission) {
        this.dateOfCommission = dateOfCommission;
    }

    public Integer getFormOfControl () {
        return formOfControl;
    }

    public void setFormOfControl (Integer formOfControl) {
        this.formOfControl = formOfControl;
    }

    public Integer getFormofstudy () {
        return formofstudy;
    }

    public void setFormofstudy (Integer formofstudy) {
        this.formofstudy = formofstudy;
    }

    public Long getIdRC () {
        return idRC;
    }

    public void setIdRC (Long idRC) {
        this.idRC = idRC;
    }

    public Long getIdReg () {
        return idReg;
    }

    public void setIdReg (Long idReg) {
        this.idReg = idReg;
    }

    public Long getIdSem () {
        return idSem;
    }

    public void setIdSem (Long idSem) {
        this.idSem = idSem;
    }

    public String getCertnumber () {
        return certnumber;
    }

    public void setCertnumber (String certnumber) {
        this.certnumber = certnumber;
    }

    public String getCourse () {
        return course;
    }

    public void setCourse (String course) {
        this.course = course;
    }

    public String getInstitute () {
        return institute;
    }

    public void setInstitute (String institute) {
        this.institute = institute;
    }

    public String getRegNumber () {
        return regNumber;
    }

    public void setRegNumber (String regNumber) {
        this.regNumber = regNumber;
    }

    public String getSemesterStr () {
        return semesterStr;
    }

    public void setSemesterStr (String semesterStr) {
        this.semesterStr = semesterStr;
    }

    public String getSignatorytutor () {
        return signatorytutor;
    }

    public void setSignatorytutor (String signatorytutor) {
        this.signatorytutor = signatorytutor;
    }

    public String getSubjectName () {
        return subjectName;
    }

    public void setSubjectName (String subjectName) {
        this.subjectName = subjectName;
    }

    public Integer getSeason () {
        return season;
    }

    public void setSeason (Integer season) {
        this.season = season;
    }

    public Integer getType () {
        return type;
    }

    public void setType (Integer type) {
        this.type = type;
    }
}
