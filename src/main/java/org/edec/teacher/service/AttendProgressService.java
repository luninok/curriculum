package org.edec.teacher.service;

import org.edec.teacher.model.attendanceProgress.StudentModel;

import java.util.List;

/**
 * @author Max Dimukhametov
 */
public interface AttendProgressService {
    List<StudentModel> getStudentsForGroup (Long idLGSS);
}
