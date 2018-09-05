package org.edec.schedule.model.synch.dao;

import lombok.Data;

/**
 * @author Max Dimukhametov
 */
@Data
public class GroupStudentEso {
    private Boolean completed = false;

    private Long idStudentCard;
    private Long idStudentEok;

    private String fio;
    private String groupname;
    private String ldapLogin;
}
