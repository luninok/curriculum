package org.edec.schedule.model.synch;

import lombok.Data;

/**
 * @author Max Dimukhametov
 */
@Data
public class StudentModel {
    private Boolean completed = false;

    private Long idStudentCard;

    private String fio;
    private String loginLdap;

    public StudentModel () {
    }

    public StudentModel (String fio, String loginLdap, Long idStudentCard) {
        this.fio = fio;
        this.loginLdap = loginLdap;
        this.idStudentCard = idStudentCard;
    }

    public StudentModel (String fio, String ldapLogin) {
        this.fio = fio;
        this.loginLdap = ldapLogin;
    }
}
