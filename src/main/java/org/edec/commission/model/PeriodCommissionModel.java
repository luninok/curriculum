package org.edec.commission.model;

import java.util.Date;


public class PeriodCommissionModel {
    private Date dateOfBegin;
    private Date dateOfEnd;

    public PeriodCommissionModel () {
    }

    public Date getDateOfBegin () {
        return dateOfBegin;
    }

    public void setDateOfBegin (Date dateOfBegin) {
        this.dateOfBegin = dateOfBegin;
    }

    public Date getDateOfEnd () {
        return dateOfEnd;
    }

    public void setDateOfEnd (Date dateOfEnd) {
        this.dateOfEnd = dateOfEnd;
    }
}
