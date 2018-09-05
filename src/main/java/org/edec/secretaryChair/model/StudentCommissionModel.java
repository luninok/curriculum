package org.edec.secretaryChair.model;

import org.edec.commons.model.StudentGroupModel;
import org.edec.utility.constants.FormOfControlConst;

import java.util.Date;

/**
 * Модель предназначена для показа студентов и комиссии, в которых они есть
 *
 * @author Max Dimukhametov
 */
public class StudentCommissionModel extends StudentGroupModel {
    private Date dateCommission;

    private Integer foc;

    private String subjectname;

    public StudentCommissionModel () {
    }

    public Date getDateCommission () {
        return dateCommission;
    }

    public void setDateCommission (Date dateCommission) {
        this.dateCommission = dateCommission;
    }

    public Integer getFoc () {
        return foc;
    }

    public void setFoc (Integer foc) {
        this.foc = foc;
    }

    public String getFocStr () {
        return FormOfControlConst.getName(this.foc).getName();
    }

    public String getSubjectname () {
        return subjectname;
    }

    public void setSubjectname (String subjectname) {
        this.subjectname = subjectname;
    }
}
