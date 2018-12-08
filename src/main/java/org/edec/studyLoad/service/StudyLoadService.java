package org.edec.studyLoad.service;

import org.edec.main.model.DepartmentModel;
import org.edec.studyLoad.model.*;

import java.util.List;

public interface StudyLoadService {
    List<TeacherModel> getTeachers (String department);

    List<TeacherModel> searchTeachers (String family, String name, String patronymic);

    List<DepartmentModel> getDepartments();

    List<EmploymentModel> getEmployment(TeacherModel selectTeacher, String department);

    Double getSumLoad (TeacherModel selectTeacher);

    Double getMaxload (TeacherModel selectTeacher);

    List<PositionModel> getPositions();

    List<ByworkerModel> getByworker();

    List<AssignmentModel> getInstructions(Long idSem, Long idDepartment);

    List<VacancyModel> getVacancy();

    void updateVacancy(Long id_vacancy, Long id_employee_role, String wagerate);

    void createVacancy(Long id_employee_role, String wagerate);

    void updateEmployment(Long id_employee, Long idByworker, Long idRole, Double wagerate, Double time_wagerate, Long id_department);

    void deleteVacancy(Long id_vacancy);

    boolean addRate(Long idEmployee, Long idDepartment, Long idPosition);

    boolean addRateBasedOnVacancy(Long idEmployee, Long idDepartment, Long idPosition, Double rate);

    boolean removeRate(Long idEmployee, Long idDepartment);
}
