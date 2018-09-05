package org.edec.utility.component.model;

import lombok.Data;

/**
 * @author Max Dimukhametov
 */
@Data
public class EmployeeModel {
    private Long idEmp;
    private Long idHum;
    private Long idLED;

    private Integer sex;

    private String family;
    private String loginLdap;
    private String name;
    private String patronymic;
    private String email;
    /**
     * Должность
     **/
    private String post;
    private Boolean isHide;

    public String getFIO () {
        return family + " " + name + " " + patronymic;
    }
}
