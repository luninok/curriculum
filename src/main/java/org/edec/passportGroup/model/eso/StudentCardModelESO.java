package org.edec.passportGroup.model.eso;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by ilyabaikalow on 09.02.18.
 */
@Getter
@Setter
public class StudentCardModelESO {
    private Long idLgs, idSSS, idSC;
    private String family, name, patronymic;
    private String idRecordBook;

    private Long idSubject;
    private String subjectName;
    private Boolean pass, exam, cp, cw, practic;
    private Integer passRating, examRating, CPRating, CWRating, practicRating;

    private Long idHf, idSemester;
    private Integer formOfStudy;
    private Integer season;
    private Date dateOfBegin, dateOfEnd;
    private String groupName;

    private String status;
}
