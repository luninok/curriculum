package org.edec.teacher.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GroupModel {

    private Integer course;
    private Integer semesterNumber;
    private Double hoursCount;

    private Long idESOcourse;
    private Long idLGSS;

    private String groupname;

    private SubjectModel subject;

    private Boolean selected;
}
