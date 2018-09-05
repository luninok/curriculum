package org.edec.register.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * @author Anton Skripachev
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class RegisterModel {
    private Long idRegister, idRegisterMine, idSubject, idSemester;
    private String groupName, subjectName, semester, registerNumber, certNumber, registerUrl;
    private HashSet<String> teachers;
    private Integer foc, retakeCount, fos, qualification, synchStatus;
    private Date signDate, dateOfEnd, dateOfBegin;
    private List<StudentModel> students = new ArrayList<>();
}
