package org.edec.commission.report.model.schedule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmmax
 */
public class ScheduleChairModel {
    private String fulltitle;

    private List<ScheduleSubjectModel> subjects = new ArrayList<>();

    public ScheduleChairModel () {
    }

    public String getFulltitle () {
        return fulltitle;
    }

    public void setFulltitle (String fulltitle) {
        this.fulltitle = fulltitle;
    }

    public List<ScheduleSubjectModel> getSubjects () {
        return subjects;
    }

    public void setSubjects (List<ScheduleSubjectModel> subjects) {
        this.subjects = subjects;
    }
}
