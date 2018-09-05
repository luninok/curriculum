package org.edec.schedule.model.synch;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Max Dimkhametov
 */
@Data
public class GroupModel {
    private Boolean completed = false;

    private String groupname;

    private List<StudentModel> students = new ArrayList<>();
}
