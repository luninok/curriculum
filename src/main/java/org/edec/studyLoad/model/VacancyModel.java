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

    //private List<VacancyModel> teachers = new ArrayList<>();

}
