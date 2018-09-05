package org.edec.schedule.model.synch.dao;

import lombok.Data;

@Data
public class GroupSubjectEfficiencyEso {
    private Integer performance;

    private Long lessonCount;
    private Long visitCount;

    private Long idEsoCourse;
    private Long idEsoStudent;
    private Long idSR;
    private Long idSRefficiency;
    private Long idSSS;
    private Long idSubject;

    private String fio;
    private String groupname;
    private String jsonGrades;
    private String subjectname;
}
