package org.edec.teacher.model.register;

import lombok.Data;

import java.util.Date;

@Data
public class RegisterRowModel {
    private Long idSSS, idSRH, idSR, idCurrentDicGroup, idDicGroup;
    private Integer mark, currentMark, retakeCount;
    private Date changeDateTime;
    // Тема курсовой работы или курсового проекта
    private String theme;
    private String studentFullName, recordbookNumber;
    private Boolean deducted, academicLeave, notActual;
}
