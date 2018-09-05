package org.edec.student.calendarOfEvents.service;

import org.edec.schedule.model.GroupSubjectLesson;
import org.edec.student.calendarOfEvents.model.MonthAttendStudentModel;

import java.util.Date;
import java.util.List;


public interface CalOfEventService {
    List<MonthAttendStudentModel> getAttendancesByMonth (Long idSSS, Date dateOfBegin, Date dateOfEnd);
    Long getIdSSSbyHum (Long idHum);

    void fillAtendanceByLessons (List<MonthAttendStudentModel> monthAttendances, Integer firstWeekSem, List<GroupSubjectLesson> lessons);

    void fillAttendanceBySelectedDay (MonthAttendStudentModel selectedDay, Long idSSS);
}
