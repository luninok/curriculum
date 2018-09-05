package org.edec.newOrder.model.addStudent;

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
public class SearchStudentModel {
    private String surname;
    private String name;
    private String patronymic;
    private String groupname;

    private Boolean governmentFinanced;
    private List<Object> studentParams = new ArrayList<>();

    private long id;

    public SearchStudentModel (String surname, String name, String patronymic, String groupname, long id) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.groupname = groupname;
        this.id = id;
    }
}
