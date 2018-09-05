package org.edec.student.journalOrder.model;

import java.util.Date;

public class JournalOrderModel {
    private Date dateSignOrder;

    private String groupname;
    private String orderNumber;
    private String orderType;
    private String semesterStr;

    public JournalOrderModel () {
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public String getSemesterStr () {
        return semesterStr;
    }

    public void setSemesterStr (String semesterStr) {
        this.semesterStr = semesterStr;
    }

    public Date getDateSignOrder () {
        return dateSignOrder;
    }

    public void setDateSignOrder (Date dateSignOrder) {
        this.dateSignOrder = dateSignOrder;
    }

    public String getOrderNumber () {
        return orderNumber;
    }

    public void setOrderNumber (String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderType () {
        return orderType;
    }

    public void setOrderType (String orderType) {
        this.orderType = orderType;
    }
}