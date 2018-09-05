package org.edec.schedule.model;

import lombok.Data;


@Data
public class GroupSubjectLesson {
    private Boolean lesson;

    private Integer week;

    private Long idLGSS;
    private Long idLSCH;

    private String room;
    private String subjectName;
    private String teacher;

    private DicDayLesson dicDayLesson;
    private DicTimeLesson dicTimeLesson;
}
