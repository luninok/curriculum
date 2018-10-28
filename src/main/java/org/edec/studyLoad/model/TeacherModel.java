package org.edec.studyLoad.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TeacherModel {

    private String FirstName;
    private String LastName;
    private String MiddleName;

    public TeacherModel(String firstName, String lastName, String middleName)
    {
        FirstName = firstName;
        LastName = lastName;
        MiddleName = middleName;
    }

}
