package org.edec.studyLoad.service;

import org.edec.main.model.DepartmentModel;
import org.edec.studyLoad.model.AssignmentModel;
import org.edec.studyLoad.model.PositionModel;
import org.edec.studyLoad.model.TeacherModel;

import java.util.List;

public interface StudyLoadService {
    List<TeacherModel> getTeachers (String department);
    List<TeacherModel> searchTeachers (String family, String name, String patronymic);
    List<DepartmentModel> getDepartments();
    List<String> getPosition();
    List<PositionModel> getPositions();
    List<AssignmentModel> getInstructions(Long idSem, Long idDepartment);
    boolean addRate(Long idEmployee, Long idDepartment, Long idPosition);
    boolean addRateBasedOnVacancy(Long idEmployee, Long idDepartment, Long idPosition, Double rate);
    boolean removeRate(Long idEmployee, Long idDepartment);
    //boolean fillRate(Long idEmployee, Long idDepartment, );
}
