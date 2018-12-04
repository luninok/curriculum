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
    private String vacancy;
    private String position;
    private Double rate;

    //private List<VacancyModel> teachers = new ArrayList<>();

    public VacancyModel(String position, Double rate, int number)
    {
        vacancy = "Вакансия " + number;
        this.position = position;
        this.rate = rate;
    }
}
