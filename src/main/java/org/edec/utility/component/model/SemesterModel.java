package org.edec.utility.component.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * @author Max Dimukhametov
 */
@Getter
@Setter
@NoArgsConstructor
public class SemesterModel {
    private boolean curSem;

    private Integer season;
    private Integer formofstudy;

    private Long idSem;

    private Date dateOfBegin;
    private Date dateOfEnd;
}
