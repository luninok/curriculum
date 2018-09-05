package org.edec.schedule.service;

import org.edec.schedule.model.GroupSubjectLesson;
import org.edec.utility.component.model.GroupModel;
import org.edec.utility.component.model.SubjectModel;

import java.util.List;

/**
 * @author Max Dimukhametov
 */
public interface AttendanceService {
    List<GroupSubjectLesson> getLessonsFromTimetable (String groupname, int course, int qualification);
    List<GroupSubjectLesson> getLessonsFromDb (String groupname);
    List<GroupModel> getGroupByInstAndFormOfStudy (Long idInst, Integer formOfStudy);
    List<SubjectModel> getSubjectsByGroupname (String groupname);
    boolean createSchedule (Long idLGSS, Integer week, String room, String teacher, Long idDicDayLesson, Long idDicTimeLesson,
                            Boolean lesson);
    boolean deleteScheduleByGroup (String groupname);
    boolean deleteScheduleById (Long idLSCH);
    void generateForAllGroups (Long idInst, Integer formOfStudy);
}
