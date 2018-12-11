package org.edec.studyLoad.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class StudyLoadModel {

    private String planFileName;
    private String instituteShortTitle;
    private String subjectCode;
    private String subjectName;
    private String departmentShortTitle;
    private Integer course;
    private Integer semester;
    private String groupName;
    private Boolean isExam;
    private Boolean isPass;
    private Integer tutoringType;
    private Integer hoursCount;
    private String family;
    private String name;
    private String patronymic;
    private String roleName;

    public StudyLoadModel(String planFileName, String instituteShortTitle, String subjectCode, String subjectName,
                          String departmentShortTitle, Integer course, Integer semester, String groupName,
                          boolean isExam, boolean isPass, Integer tutoringType, Integer hoursCount, String family,
                          String name, String patronymic, String roleName) {
        this.planFileName = planFileName;
        this.instituteShortTitle = instituteShortTitle;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.departmentShortTitle = departmentShortTitle;
        this.course = course;
        this.semester = semester;
        this.groupName = groupName;
        this.isExam = isExam;
        this.isPass = isPass;
        this.tutoringType = tutoringType;
        this.hoursCount = hoursCount;
        this.family = family;
        this.name = name;
        this.patronymic = patronymic;
        this.roleName = roleName;
    }
}
