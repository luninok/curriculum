package org.edec.newOrder.model.editOrder;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by apple on 29.11.16.
 */
@Getter
@Setter
public class StudentModel {
    private String fio;
    private String recordnumber;
    private String numberPrevOrder;
    private String foundation;
    private String additional;

    private Date firstDate;
    private Date secondDate;
    private Date thirdDate;
    private Date datePrevOrder;

    private long id;

    private boolean selected;

    public StudentModel (long id) {
        this.id = id;
    }

    public StudentModel () {
        this.id = id;
    }

    /**
     * используется при создании переводного
     **/
    private boolean isProlongatedManualy;
    private Date dateProlongation;
    private String groupname;
}
