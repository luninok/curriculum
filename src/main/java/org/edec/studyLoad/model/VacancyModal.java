package org.edec.studyLoad.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class VacancyModal {
    private String vacancy;
    private String position;
    private String rate;

    //private List<VacancyModal> teachers = new ArrayList<>();

    public VacancyModal(String position, String rate, int number)
    {
        vacancy = "Вакансия " + number;
        this.position = position;
        this.rate = rate;
    }
}
