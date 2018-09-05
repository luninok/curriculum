package org.edec.studentPassport.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.model.StudentModel;

import java.util.Date;

/**
 * @author Max Dimukhametov
 */
@Getter
@Setter
@NoArgsConstructor
public class StudentStatusModel extends StudentModel {
    private Boolean academicLeave, deducted, foreigner;
    private Boolean governmentFinanced, trustAgreement;
    private Boolean educationcomplite;

    private Date birthday;

    private Integer course, sex, semesterNumber;

    private Long idDG, idInstitute;
    private Long idSemester;
    private Long currentGroupId;
    private Long groupId;
    private Long mineId;

    private String groupname;
}
