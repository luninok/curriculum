package org.edec.contingentMovement.report.model;

import org.edec.contingentMovement.model.ResitRatingModel;

import java.util.Date;
import java.util.List;

public class ProtocolCommissionModel {

    private Date dateCommission;

    private Integer numberProtocol;

    private String agenda;
    private String chairman;
    private String fioStudent;

    private List<ResitRatingModel> resitSubjects;
    private List<String> сommissionMembers;

    public Date getDateCommission () {
        return dateCommission;
    }

    public void setDateCommission (Date dateCommission) {
        this.dateCommission = dateCommission;
    }

    public String getAgenda () {
        return agenda;
    }

    public void setAgenda (String agenda) {
        this.agenda = agenda;
    }

    public String getChairman () {
        return chairman;
    }

    public void setChairman (String chairman) {
        this.chairman = chairman;
    }

    public String getFioStudent () {
        return fioStudent;
    }

    public void setFioStudent (String fioStudent) {
        this.fioStudent = fioStudent;
    }

    public Integer getNumberProtocol () {
        return numberProtocol;
    }

    public void setNumberProtocol (Integer numberProtocol) {
        this.numberProtocol = numberProtocol;
    }

    public List<ResitRatingModel> getResitSubjects () {
        return resitSubjects;
    }

    public void setResitSubjects (List<ResitRatingModel> resitSubjects) {
        this.resitSubjects = resitSubjects;
    }

    public List<String> getСommissionMembers () {
        return сommissionMembers;
    }

    public void setСommissionMembers (List<String> сommissionMembers) {
        this.сommissionMembers = сommissionMembers;
    }
}
