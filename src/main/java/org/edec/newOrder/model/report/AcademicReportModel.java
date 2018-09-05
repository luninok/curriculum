package org.edec.newOrder.model.report;

/**
 * Created by dmmax
 */
public class AcademicReportModel {
    private Boolean prolongation;

    private String description;
    private String fio;
    private String foundation;
    private String groupname;
    private String recordbook;
    private String subDescription;

    public AcademicReportModel () {
    }

    public Boolean getProlongation () {
        return prolongation;
    }

    public void setProlongation (Boolean prolongation) {
        this.prolongation = prolongation;
    }

    public String getDescription () {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
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

    public String getRecordbook () {
        return recordbook;
    }

    public void setRecordbook (String recordbook) {
        this.recordbook = recordbook;
    }

    public String getSubDescription () {
        return subDescription;
    }

    public void setSubDescription (String subDescription) {
        this.subDescription = subDescription;
    }
}
