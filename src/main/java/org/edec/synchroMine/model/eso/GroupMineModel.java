package org.edec.synchroMine.model.eso;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.model.GroupModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class GroupMineModel extends GroupModel {
    private Boolean military = false;

    private Date createdSchoolYear, enterSchoolYear;

    private Float periodOfStudy;

    //заочная форма обучения: 1 - заочно-очная, 2 - заочная
    private Integer distanceType;
    //форма обучения: 1 - очная, 2 - заочная
    private Integer formOfStudy;
    private Integer generation;
    //Квалификация: 1 - инженер, 2 - бакалавр, 3 - магистр
    private Integer qualification;

    private Long idChair;
    private Long idChairMine;
    private Long idCurriculumMine;
    private Long idDirection;
    private Long idDirectionMine;
    private Long idGroupMine;

    private String chairName;
    private String directionCode;
    private String directionTitle;
    private String planfileName;
    private String specialityTitle;
    private String qualificationCode;

    private GroupMineModel otherGroup;

    private List<StudentModel> students = new ArrayList<>();
}
