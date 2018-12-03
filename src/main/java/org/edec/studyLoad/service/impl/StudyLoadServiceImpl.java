package org.edec.studyLoad.service.impl;

import org.edec.main.model.DepartmentModel;
import org.edec.studyLoad.manager.EntityManagerStudyLoad;
import org.edec.studyLoad.model.*;
import org.edec.studyLoad.service.StudyLoadService;

import java.util.List;

public class StudyLoadServiceImpl implements StudyLoadService {
    private EntityManagerStudyLoad entityManagerStudyLoad = new EntityManagerStudyLoad();

    @Override
    public List<TeacherModel> getTeachers (String department) {
        return entityManagerStudyLoad.getTeachers(department);
    }

    @Override
    public List<TeacherModel> searchTeachers (String family, String name, String patronymic) {
        return entityManagerStudyLoad.searchTeachers(family, name, patronymic);
    }

    @Override
    public  List<DepartmentModel> getDepartments() {
        return entityManagerStudyLoad.getDepartments();
    }

    @Override
    public List<EmploymentModel> getEmployment(TeacherModel selectTeacher, String department) {
        return entityManagerStudyLoad.getEmployment(selectTeacher, department); }

    @Override
    public List<PositionModel> getPositions() {
        return entityManagerStudyLoad.getPositions();
    }

    @Override
    public List<String> getByworker() {
        return entityManagerStudyLoad.getByworker();
    }

    @Override
    public List<AssignmentModel> getInstructions(Long idSem, Long idDepartment){
        return entityManagerStudyLoad.getInstructions(idSem, idDepartment);
    }

    @Override
    public List<VacancyModel> getVacancy() {
        return entityManagerStudyLoad.getVacancy();
    }

    @Override
    public void updateVacancy(Long id_vacancy, Long id_employee_role, String wagerate) {
        entityManagerStudyLoad.updateVacancy(id_vacancy, id_employee_role, wagerate);
    }

    @Override
    public void createVacancy(Long id_employee_role, String wagerate) {
        entityManagerStudyLoad.createVacancy(id_employee_role, wagerate);
    }

    @Override
    public void updateEmployment(Long id_vacancy, String shorttitle, String byworker, String rolename, Double wagerate, Double time_wagerate) {
        entityManagerStudyLoad.updateEmployment(id_vacancy, shorttitle, byworker, rolename, wagerate, time_wagerate);
    }

    @Override
    public void deleteVacancy(Long id_vacancy) {
        entityManagerStudyLoad.deleteVacancy(id_vacancy);
    }

    @Override
    public boolean addRate(Long idEmployee, Long idDepartment, Long idPosition) {
        return entityManagerStudyLoad.addRate(idEmployee, idDepartment, idPosition);
    }

    @Override
    public boolean addRateBasedOnVacancy(Long idEmployee, Long idDepartment, Long idPosition, Double rate) {
        return entityManagerStudyLoad.addRateBasedOnVacancy(idEmployee, idDepartment, idPosition, rate);
    }

    @Override
    public boolean removeRate(Long idEmployee, Long idDepartment){
        return entityManagerStudyLoad.removeRate(idEmployee, idDepartment);
    }
}
