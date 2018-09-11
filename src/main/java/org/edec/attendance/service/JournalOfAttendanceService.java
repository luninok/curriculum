package org.edec.attendance.service;

import org.edec.attendance.model.MonthAttendSubjectsModel;
import org.edec.attendance.model.StudentAttendanceModel;
import org.edec.model.AttendanceModel;
import org.edec.model.GroupSemesterModel;
import org.edec.schedule.model.GroupSubjectLesson;

import java.util.Date;
import java.util.List;


public interface JournalOfAttendanceService {
    GroupSemesterModel getGroupSemesterByHum(Long idHum);
    List<GroupSemesterModel> getGroupModelBySem(Long idSem, String qualification, String courses);
    List<AttendanceModel> getSubjectAttendanceByLGSandDate(Long idLGS, Date visitDate);
    List<MonthAttendSubjectsModel> getMonthAttendModel(Long idLGSS, Date dateBegin, Date dateEnd);
    List<StudentAttendanceModel> getStudentByGroup(Long idLGS);
    void fillStudentByAttendance(List<StudentAttendanceModel> students, List<AttendanceModel> subjectsAttendance, Long idLGS, Date visitDate);
    void fillMonthAttendance(List<MonthAttendSubjectsModel> monthAttendance, Integer firstWeekSem, List<GroupSubjectLesson> lessons);

    void createAttendance(AttendanceModel attendance);
    void updateAttendance(AttendanceModel attendance);

    void deleteAttendanceByDate(MonthAttendSubjectsModel selectedDay, Long idLGS);
}
