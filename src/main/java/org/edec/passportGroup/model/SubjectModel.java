package org.edec.passportGroup.model;

import lombok.Getter;
import lombok.Setter;
import org.edec.admin.model.EmployeeModel;
import org.edec.utility.constants.FormOfControlConst;

import java.util.List;

/**
 * Created by antonskripacev on 09.04.17.
 */
@Getter
@Setter
public class SubjectModel {
    private Long idLesg;
    private Long idSubject;
    private Long idLgss;
    private String subjectName;
    private FormOfControlConst foc;
    private List<EmployeeModel> employeeModels;
    private Double countHours;
    private Double statistic;
    private String statisticStr;
    private Long idDicSubject;
    private Long idCurriculum;
    private Long idChair;
    private Integer semesterNumber;
    private Double hoursAudCount;
    private Double hoursLabor;
    private Double hoursLection;
    private Double hoursPractic;
    private Boolean isActive;
    private Integer type;
    private Boolean pass, exam, cp, cw, practic;
    private Boolean synchMine;
}
