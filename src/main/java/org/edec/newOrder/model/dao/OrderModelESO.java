package org.edec.newOrder.model.dao;

import java.util.Date;

/**
 * Created by apple on 29.11.16.
 */
public class OrderModelESO {
    private String fio;
    private String foundation;
    private String foundationLos;
    private String groupname;
    private String recordbook;
    private String sectionname;
    private String additionalInfo;

    private Date firstDate;
    private Date secondDate;
    private Date thirdDate;

    private Date firstDateSection;
    private Date secondDateSection;

    private long idSection;
    private long idStudent;
    private long idOS;

    public OrderModelESO () {
    }

    public String getFoundationLos () {
        return foundationLos;
    }

    public void setFoundationLos (String foundationLos) {
        this.foundationLos = foundationLos;
    }

    public Date getFirstDateSection () {
        return firstDateSection;
    }

    public void setFirstDateSection (Date firstDateSection) {
        this.firstDateSection = firstDateSection;
    }

    public Date getSecondDateSection () {
        return secondDateSection;
    }

    public void setSecondDateSection (Date secondDateSection) {
        this.secondDateSection = secondDateSection;
    }

    public long getIdStudent () {
        return idStudent;
    }

    public void setIdStudent (long idStudent) {
        this.idStudent = idStudent;
    }

    public long getIdSection () {
        return idSection;
    }

    public void setIdSection (long idSection) {
        this.idSection = idSection;
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

    public String getSectionname () {
        return sectionname;
    }

    public void setSectionname (String sectionname) {
        this.sectionname = sectionname;
    }

    public Date getFirstDate () {
        return firstDate;
    }

    public void setFirstDate (Date firstDate) {
        this.firstDate = firstDate;
    }

    public Date getSecondDate () {
        return secondDate;
    }

    public void setSecondDate (Date secondDate) {
        this.secondDate = secondDate;
    }

    public Date getThirdDate () {
        return thirdDate;
    }

    public void setThirdDate (Date thirdDate) {
        this.thirdDate = thirdDate;
    }

    public String getAdditionalInfo () {
        return additionalInfo;
    }

    public void setAdditionalInfo (String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public long getIdOS () {
        return idOS;
    }

    public void setIdOS (long idOS) {
        this.idOS = idOS;
    }
}
