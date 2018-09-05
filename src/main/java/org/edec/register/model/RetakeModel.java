package org.edec.register.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anton Skripachev
 */
@Getter
@Setter
@NoArgsConstructor
public class RetakeModel {
    private String fio;
    private Long idSSS, idSR, idCurDicGroup, idSemester;
    private Boolean deductedCurSem, academicLeaveCurSem, transferedStudent;
    private Boolean listenerCurSem, transferedStudentCurSem;
    private Integer passRating, examRating, cpRating, cwRating, practicRating, type;

    private List<SessionRatingHistoryModel> listSRH = new ArrayList<>();
}
