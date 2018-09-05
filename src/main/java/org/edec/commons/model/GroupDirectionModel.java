package org.edec.commons.model;

import lombok.Getter;
import lombok.Setter;
import org.edec.model.GroupModel;


@Getter
@Setter
public class GroupDirectionModel extends GroupModel {
    private String directioncode;
    private String directiontitle;
}
