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

    private List<TeacherModel> teachers = new ArrayList<>();

    public TeacherModel(String firstName, String lastName, String middleName)
    {
        name = firstName;
        family = lastName;
        patronymic = middleName;
    }

}
