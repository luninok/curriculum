package org.edec.student.recordBook.model;

import org.edec.efficiency.model.ConfigurationEfficiency;


public class StudentSemesterModel {
    private Long idSSS;

    private String semesterNumber;

    private ConfigurationEfficiency configurationEfficiency;

    public StudentSemesterModel () {
    }

    public Long getIdSSS () {
        return idSSS;
    }

    public void setIdSSS (Long idSSS) {
        this.idSSS = idSSS;
    }

    public String getSemesterNumber () {
        return semesterNumber;
    }

    public void setSemesterNumber (String semesterNumber) {
        this.semesterNumber = semesterNumber;
    }

    public ConfigurationEfficiency getConfigurationEfficiency () {
        return configurationEfficiency;
    }

    public void setConfigurationEfficiency (ConfigurationEfficiency configurationEfficiency) {
        this.configurationEfficiency = configurationEfficiency;
    }
}
