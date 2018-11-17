package org.edec.studyLoad.service;

import org.edec.main.model.DepartmentModel;
import org.edec.studyLoad.model.AssignmentModel;
import org.edec.studyLoad.model.TeacherModel;

import java.util.List;

public interface StudyLoadService {
    List<TeacherModel> getTeachers (String department);
    List<DepartmentModel> getDepartments();
    List<String> getPosition();
    List<AssignmentModel> getInstructions(Long idSem, Long idDepartment);
}
