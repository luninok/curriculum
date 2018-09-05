package org.edec.factSheet.model;

import java.util.Date;

public class FactSheetModel {

    private Date dateCreate;
    private Long idHum;
    private Long idFactSheet;
    private int idTypeFactSheet;
    private int idFactSheetStatus;
    private int registerNumber;
    private String email;

    public String getEmail () {
        return email;
    }

    public void setEmail (String email) {
        this.email = email;
    }

    public Long getIdHum () {
        return idHum;
    }

    public void setIdHum (Long idHum) {
        this.idHum = idHum;
    }

    public int getIdFactSheetStatus () {
        return idFactSheetStatus;
    }

    public void setIdFactSheetStatus (int idFactSheetStatus) {
        this.idFactSheetStatus = idFactSheetStatus;
    }

    public Date getDateCreate () {
        return dateCreate;
    }

    public void setDateCreate (Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    public Long getIdFactSheet () {
        return idFactSheet;
    }

    public void setIdFactSheet (Long idFactSheet) {
        this.idFactSheet = idFactSheet;
    }

    public int getIdTypeFactSheet () {
        return idTypeFactSheet;
    }

    public void setIdTypeFactSheet (int idTypeFactSheet) {
        this.idTypeFactSheet = idTypeFactSheet;
    }

    public int getRegisterNumber () {
        return registerNumber;
    }

    public void setRegisterNumber (int registerNumber) { this.registerNumber = registerNumber; }
}

