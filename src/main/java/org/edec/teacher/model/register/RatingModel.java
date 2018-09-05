package org.edec.teacher.model.register;

import lombok.Data;
import org.edec.utility.constants.FormOfControlConst;

import java.util.Date;

/**
 * @author Anton Skripachev
 */
@Data
public class RatingModel {
    private Integer course;
    private Integer newRating;
    private Integer retakeCount;
    private Integer type;
    private Integer currentRating;
    private Integer foc;
    private Integer synchStatus;

    private Double hoursCount;

    private Long idSemester;
    private Long idCurrentDicGroup;
    private Long idDicGroup;
    private Long idSessionRating;
    private Long idSessionRatingHistory;
    private Long idRegister;
    private Long idStudentSemesterStatus;
    private Long idRegisterMine;

    private String studentFIO;
    private String groupName;
    private String status;
    private String courseProjectTheme;
    private String courseWorkTheme;
    private String registerUrl;
    private String registerNumber;
    private String recordbookNumber;
    private String certNumber;
    private String signatoryTutor;
    private String subjectName;
    private String thumbPrint;

    private Date statusBeginDate;
    private Date statusFinishDate;
    private Date completionDate;
    private Date changeDateTime;
    private Date signDate;

    private Boolean signedBackDate;
    private Boolean deductedStatus, academicLeaveStatus;
    private Boolean notActual, isCanceled;
}
