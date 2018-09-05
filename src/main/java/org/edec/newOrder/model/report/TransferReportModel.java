package org.edec.newOrder.model.report;

import java.util.Date;

/**
 * @author Max Dimukhametov
 */
public class TransferReportModel {
    private Integer course;

    private String description;
    private String descriptionOneDates;
    private String descriptionTwoDates;
    private String fio;
    private String foundation;
    private String groupname;
    private String recordbook;
    private String additional;
    private String prevOrderNum;
    private String prevOrderDate;

    private Date firstDateStudent;

    public TransferReportModel () {
    }

    public Integer getCourse () {
        return course;
    }

    public void setCourse (Integer course) {
        this.course = course;
    }

    public String getDescription () {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public String getDescriptionOneDates () {
        return descriptionOneDates;
    }

    public void setDescriptionOneDates (String descriptionOneDates) {
        this.descriptionOneDates = descriptionOneDates;
    }

    public String getDescriptionTwoDates () {
        return descriptionTwoDates;
    }

    public void setDescriptionTwoDates (String descriptionTwoDates) {
        this.descriptionTwoDates = descriptionTwoDates;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getFoundation () {
        return foundation;
    }

    public void setFoundation (String foundation) {
        this.foundation = foundation;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public String getRecordbook () {
        return recordbook;
    }

    public void setRecordbook (String recordbook) {
        this.recordbook = recordbook;
    }

    public String getAdditional () {
        return additional;
    }

    public void setAdditional (String additional) {
        this.additional = additional;
    }

    public String getPrevOrderNum () {
        return prevOrderNum;
    }

    public void setPrevOrderNum (String prevOrderNum) {
        this.prevOrderNum = prevOrderNum;
    }

    public String getPrevOrderDate () {
        return prevOrderDate;
    }

    public void setPrevOrderDate (String prevOrderDate) {
        this.prevOrderDate = prevOrderDate;
    }

    public Date getFirstDateStudent () {
        return firstDateStudent;
    }

    public void setFirstDateStudent (Date firstDateStudent) {
        this.firstDateStudent = firstDateStudent;
    }
}
