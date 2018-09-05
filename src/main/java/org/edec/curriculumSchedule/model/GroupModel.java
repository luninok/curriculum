package org.edec.curriculumSchedule.model;

import java.util.Date;

public class GroupModel {

    private Long idDg;
    private Long idLgs;
    private String groupName;
    private Date dateOfBeginStudy;
    private Date dateOfEndStudy;
    private Date dateOfBeginSemester;
    private Date dateOfEndSemester;
    private Date dateOfBeginPassWeek;
    private Date dateOfEndPassWeek;
    private Date dateOfBeginSession;
    private Date dateOfEndSession;
    private Date dateOfBeginVacation;
    private Date dateOfEndVacation;

    public Date getDateOfBeginSemester () {
        return dateOfBeginSemester;
    }

    public void setDateOfBeginSemester (Date dateOfBeginSemester) {
        this.dateOfBeginSemester = dateOfBeginSemester;
    }

    public Date getDateOfEndSemester () {
        return dateOfEndSemester;
    }

    public void setDateOfEndSemester (Date dateOfEndSemester) {
        this.dateOfEndSemester = dateOfEndSemester;
    }

    public Date getDateOfBeginPassWeek () {
        return dateOfBeginPassWeek;
    }

    public void setDateOfBeginPassWeek (Date dateOfBeginPassWeek) {
        this.dateOfBeginPassWeek = dateOfBeginPassWeek;
    }

    public Date getDateOfEndPassWeek () {
        return dateOfEndPassWeek;
    }

    public void setDateOfEndPassWeek (Date dateOfEndPassWeek) {
        this.dateOfEndPassWeek = dateOfEndPassWeek;
    }

    public Date getDateOfBeginSession () {
        return dateOfBeginSession;
    }

    public void setDateOfBeginSession (Date dateOfBeginSession) {
        this.dateOfBeginSession = dateOfBeginSession;
    }

    public Date getDateOfEndSession () {
        return dateOfEndSession;
    }

    public void setDateOfEndSession (Date dateOfEndSession) {
        this.dateOfEndSession = dateOfEndSession;
    }

    public Date getDateOfBeginVacation () {
        return dateOfBeginVacation;
    }

    public void setDateOfBeginVacation (Date dateOfBeginVacation) {
        this.dateOfBeginVacation = dateOfBeginVacation;
    }

    public Date getDateOfEndVacation () {
        return dateOfEndVacation;
    }

    public void setDateOfEndVacation (Date dateOfEndVacation) {
        this.dateOfEndVacation = dateOfEndVacation;
    }

    public Long getIdDg () {
        return idDg;
    }

    public void setIdDg (Long idDg) {
        this.idDg = idDg;
    }

    public String getGroupName () {
        return groupName;
    }

    public void setGroupName (String groupName) {
        this.groupName = groupName;
    }

    public Long getIdLgs () {
        return idLgs;
    }

    public void setIdLgs (Long idLgs) {
        this.idLgs = idLgs;
    }

    public Date getDateOfBeginStudy () {
        return dateOfBeginStudy;
    }

    public void setDateOfBeginStudy (Date dateOfBeginStudy) {
        this.dateOfBeginStudy = dateOfBeginStudy;
    }

    public Date getDateOfEndStudy () {
        return dateOfEndStudy;
    }

    public void setDateOfEndStudy (Date dateOfEndStudy) {
        this.dateOfEndStudy = dateOfEndStudy;
    }
}
