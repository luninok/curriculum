package org.edec.studyLoad.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PositionModel {
    private String positionName;
    private Long idPosition;

    public PositionModel(String positionName, Long idPosition) {
        this.positionName = positionName;
        this.idPosition = idPosition;
    }
}