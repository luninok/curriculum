package org.edec.synchroMine.model.dao;

import lombok.Getter;
import lombok.Setter;
import org.edec.model.SubjectModel;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
public class SubjectGroupModel extends SubjectModel {
    private Boolean practicType = false, facultative = false;

    private Double hoursLecture;
    private Double hoursPractice;
    private Double hoursLabaratory;
    private Double hoursIndependent;
    private Double hoursExam;

    private Long idChair;
    private Long idChairMine;
    private Long idLGSS;
    private Long idSubjMine;

    private SubjectGroupModel otherSubject;

    private Set<Long> workLoads = new HashSet<>();
    private Set<Long> employees = new HashSet<>();
}
