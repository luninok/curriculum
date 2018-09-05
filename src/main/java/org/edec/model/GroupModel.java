package org.edec.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class GroupModel {
    private Integer course;
    private Integer semester;

    private Long idDG;
    private Long idLGS;
    private Long idInstitute;

    private String groupname;

    @Override
    public String toString () {
        return getGroupname();
    }
}
