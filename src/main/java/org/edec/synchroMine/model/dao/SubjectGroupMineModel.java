package org.edec.synchroMine.model.dao;

import lombok.Getter;
import lombok.Setter;
import org.edec.model.SubjectModel;


@Getter
@Setter
public class SubjectGroupMineModel extends SubjectModel {
    private Boolean practicType, facultative;

    private Double hoursLecture;
    private Double hoursPractice;
    private Double hoursLabaratory;
    private Double hoursIndependent;
    private Double hoursExam;

    private Long idChairMine;
    private Long idSubjMine;
    private Long idWorkload;

    private String family;
    private String name;
    private String patronymic;
}
