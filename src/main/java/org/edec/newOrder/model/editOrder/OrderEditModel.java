package org.edec.newOrder.model.editOrder;

import java.util.Date;
import java.util.List;

public class OrderEditModel {
    private Date datecreated;
    private Date datefinish;
    private Date datesign;

    private Long idOrder;
    private Long idOrderRule;
    private Long orderType;
    private Long idSemester;

    private Long countStudents;

    private String currenthumanface;
    private String description;
    private String idLotus;
    private String number;
    private String semesterSeason;
    private String status;
    private String type;
    private String url;

    private List<SectionModel> sections;
    private List<GroupModel> groups;

    public Date getDatecreated () {
        return datecreated;
    }

    public void setDatecreated (Date datecreated) {
        this.datecreated = datecreated;
    }

    public Date getDatefinish () {
        return datefinish;
    }

    public void setDatefinish (Date datefinish) {
        this.datefinish = datefinish;
    }

    public Date getDatesign () {
        return datesign;
    }

    public void setDatesign (Date datesign) {
        this.datesign = datesign;
    }

    public Long getIdOrder () {
        return idOrder;
    }

    public void setIdOrder (Long idOrder) {
        this.idOrder = idOrder;
    }

    public Long getIdOrderRule () {
        return idOrderRule;
    }

    public void setIdOrderRule (Long idOrderRule) {
        this.idOrderRule = idOrderRule;
    }

    public Long getOrderType () {
        return orderType;
    }

    public void setOrderType (Long orderType) {
        this.orderType = orderType;
    }

    public Long getIdSemester () {
        return idSemester;
    }

    public void setIdSemester (Long idSemester) {
        this.idSemester = idSemester;
    }

    public Long getCountStudents () {
        return countStudents;
    }

    public void setCountStudents (Long countStudents) {
        this.countStudents = countStudents;
    }

    public String getCurrenthumanface () {
        return currenthumanface;
    }

    public void setCurrenthumanface (String currenthumanface) {
        this.currenthumanface = currenthumanface;
    }

    public String getDescription () {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public String getIdLotus () {
        return idLotus;
    }

    public void setIdLotus (String idLotus) {
        this.idLotus = idLotus;
    }

    public String getNumber () {
        return number;
    }

    public void setNumber (String number) {
        this.number = number;
    }

    public String getSemesterSeason () {
        return semesterSeason;
    }

    public void setSemesterSeason (String semesterSeason) {
        this.semesterSeason = semesterSeason;
    }

    public String getStatus () {
        return status;
    }

    public void setStatus (String status) {
        this.status = status;
    }

    public String getType () {
        return type;
    }

    public void setType (String type) {
        this.type = type;
    }

    public String getUrl () {
        return url;
    }

    public void setUrl (String url) {
        this.url = url;
    }

    public List<SectionModel> getSections () {
        return sections;
    }

    public void setSections (List<SectionModel> sections) {
        this.sections = sections;
    }

    public List<GroupModel> getGroups () {
        return groups;
    }

    public void setGroups (List<GroupModel> groups) {
        this.groups = groups;
    }
}
