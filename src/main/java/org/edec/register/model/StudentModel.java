package org.edec.register.model;

import java.util.Date;

/**
 * Created by antonskripacev on 10.06.17.
 */
public class StudentModel extends org.edec.model.StudentModel {
    private int rating, type;
    private Date changeDateTime;
    private Long idSRH;

    public Long getIdSRH () {
        return idSRH;
    }

    public int getType () {
        return type;
    }

    public void setType (int type) {
        this.type = type;
    }

    public void setIdSRH (Long idSRH) {
        this.idSRH = idSRH;
    }

    public int getRating () {
        return rating;
    }

    public void setRating (int rating) {
        this.rating = rating;
    }

    public Date getChangeDateTime () {
        return changeDateTime;
    }

    public void setChangeDateTime (Date changeDateTime) {
        this.changeDateTime = changeDateTime;
    }
}
