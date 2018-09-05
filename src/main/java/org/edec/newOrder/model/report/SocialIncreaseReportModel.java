package org.edec.newOrder.model.report;

/**
 * @author Max Dimukhametov
 */
public class SocialIncreaseReportModel {
    private Integer course;

    private String description;
    private String descriptionDate;
    private String fio;
    private String foundation;
    private String groupname;
    private String recordbook;

    public SocialIncreaseReportModel () {
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

    public String getDescriptionDate () {
        return descriptionDate;
    }

    public void setDescriptionDate (String descriptionDate) {
        this.descriptionDate = descriptionDate;
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
}
