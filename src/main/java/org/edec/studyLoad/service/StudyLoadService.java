package org.edec.studyLoad.service;

import org.edec.main.model.DepartmentModel;
import org.edec.studyLoad.model.*;

import java.util.List;

public interface StudyLoadService {
    List<TeacherModel> getTeachers (String department);

    List<TeacherModel> searchTeachers (String family, String name, String patronymic);

    List<DepartmentModel> getDepartments();

    List<EmploymentModel> getEmployment(TeacherModel selectTeacher, String department);

    List<String> getPosition();

    List<PositionModel> getPositions();

    List<String> getByworker();

    List<AssignmentModel> getInstructions(Long idSem, Long idDepartment);

    List<VacancyModel> getVacancy();

    void updateVacancy(Long id_vacancy, String rolename, String wagerate);

    void createVacancy(String rolename, String wagerate);

    void updateEmployment(Long id_vacancy, String shorttitle, String byworker, String rolename, Double wagerate, Double time_wagerate);

    void deleteVacancy(Long id_vacancy);

    boolean addRate(Long idEmployee, Long idDepartment, Long idPosition);

    boolean addRateBasedOnVacancy(Long idEmployee, Long idDepartment, Long idPosition, Double rate);

    boolean removeRate(Long idEmployee, Long idDepartment);
}
