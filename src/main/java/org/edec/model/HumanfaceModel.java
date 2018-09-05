package org.edec.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class HumanfaceModel {
    private Long idHum;
    private String family, name, patronymic;
    private String email;

    public String getFio () {
        return family + " " + name + " " + patronymic;
    }

    public String getShortFio () {
        return family + " " + name.substring(0, 1) + ". " + patronymic.substring(0, 1) + ".";
    }

    public String getShortFioInverse() {
        return name.substring(0, 1) + ". " + patronymic.substring(0, 1) + "." + " " + family;
    }
}