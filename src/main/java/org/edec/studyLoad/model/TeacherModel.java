package org.edec.studyLoad.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.commission.model.StudentDebtModel;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TeacherModel {

    private String name;
    private String family;
    private String patronymic;
    private Long id_employee;

    public TeacherModel(String name, String family, String patronymic, Long id_employee) {
        this.name = name;
        this.family = family;
        this.patronymic = patronymic;
        this.id_employee = id_employee;
    }

    // private List<TeacherModel> teachers = new ArrayList<>();


    @Override
    public String toString() {
        return family + " " + name + " " + patronymic;
    }
}
