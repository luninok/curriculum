package org.edec.secretaryChair.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class CommissionDayModel {
    /**
     * День комиссии
     */
    private Date day;
    /**
     * Содержит время, когда проводится уже комиссия.
     * Если размер = 0, значит комисси на этот день не назначены
     */
    private List<Date> busyTimes;

    private int countSubject = 0;

    public CommissionDayModel () {
    }

    public CommissionDayModel (Date day) {
        this.day = day;
    }

    public Date getDay () {
        return day;
    }

    public void setDay (Date day) {
        this.day = day;
    }

    public List<Date> getBusyTimes () {
        if (busyTimes == null) {
            busyTimes = new ArrayList<>();
        }
        return busyTimes;
    }

    public void setBusyTimes (List<Date> busyTimes) {
        this.busyTimes = busyTimes;
    }

    public int getCountSubject () {
        return countSubject;
    }

    public void setCountSubject (int countSubject) {
        this.countSubject = countSubject;
    }
}