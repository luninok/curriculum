package org.edec.studyLoad.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class VacancyModel {
    private Long id_vacancy;
    private String rolename;
    private double wagerate;
    private Long id_department;
    private int number;

    public VacancyModel(Long id_vacancy, String rolename, double wagerate, int number, Long id_department) {
        this.id_vacancy = id_vacancy;
        this.rolename = rolename;
        this.wagerate = wagerate;
        this.id_department = id_department;
        this.number = number;
    }

    public TeacherModel ToTeacherModel(){
        TeacherModel teacherModel = new TeacherModel();
        teacherModel.setPatronymic("");
        teacherModel.setFamily("Вакансия" + number);
        teacherModel.setName("");
        teacherModel.setSex(1);
        teacherModel.setId_employee(id_vacancy);
        return teacherModel;
    }
}
