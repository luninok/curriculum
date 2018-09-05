package org.edec.register.model.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Anton Skripachev
 */
@Getter
@Setter
@NoArgsConstructor
public class RetakeModelEso {
    private String fio;
    private Long idSSS, idSR, idSRH, idCurDicGroup, idSemester;
    private Boolean deducted, deductedCurSem, academicLeave, academicLeaveCurSem, transferedStudent;
    private Boolean listenerCurSem, notActual, transferedStudentCurSem;
    private Integer passRating, examRating, cpRating, cwRating, practicRating, retakeCount, type;
    private int qualification;
}
