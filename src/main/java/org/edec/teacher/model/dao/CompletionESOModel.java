package org.edec.teacher.model.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * @author Max Dimukhametov
 */
@Getter
@Setter
@NoArgsConstructor
public class CompletionESOModel {
    private Boolean curSem;

    private Boolean exam, pass, cp, cw, practic;

    private Date dateofbegin;
    private Date dateofend;

    private Integer formofcontrol;
    private Integer formofstudy;
    private Integer season;
    private Integer course;
    private Integer semesterNumber;
    private Integer type;

    private Double hoursCount;

    private Long idESOcourse;
    private Long idLGSS;
    private Long idInstitute;
    private Long idSemester;

    private String groupname;
    private String institute;
    private String semesterStr;
    private String subjectname;
}
