package org.edec.student.journalOfAttendance.service;

import org.edec.model.AttendanceModel;
import org.edec.model.GroupSemesterModel;
import org.edec.model.MonthAttendModel;
import org.edec.model.StudentModel;
import org.edec.schedule.model.GroupSubjectLesson;
import org.edec.student.journalOfAttendance.model.MonthAttendSubjectsModel;
import org.edec.student.journalOfAttendance.model.StudentAttendanceModel;

import java.util.Date;
import java.util.List;


public interface JournalOfAttendanceService {
    GroupSemesterModel getGroupSemesterByHum (Long idHum);
    List<AttendanceModel> getSubjectAttendanceByLGSandDate (Long idLGS, Date visitDate);
    List<MonthAttendSubjectsModel> getMonthAttendModel (Long idLGSS, Date dateBegin, Date dateEnd);
    List<StudentAttendanceModel> getStudentByGroup (Long idLGS);
    void fillStudentByAttendance (List<StudentAttendanceModel> students, List<AttendanceModel> subjectsAttendance, Long idLGS,
                                  Date visitDate);
    void fillMonthAttendance (List<MonthAttendSubjectsModel> monthAttendance, Integer firstWeekSem, List<GroupSubjectLesson> lessons);

    void createAttendance (AttendanceModel attendance);
    void updateAttendance (AttendanceModel attendance);

    void deleteAttendanceByDate (MonthAttendSubjectsModel selectedDay, Long idLGS);
}
