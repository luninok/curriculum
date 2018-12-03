package org.edec.studyLoad.service.impl;

import org.edec.main.model.DepartmentModel;
import org.edec.studyLoad.manager.EntityManagerStudyLoad;
import org.edec.studyLoad.model.AssignmentModel;
import org.edec.studyLoad.model.EmploymentModel;
import org.edec.studyLoad.model.TeacherModel;
import org.edec.studyLoad.model.VacancyModel;
import org.edec.studyLoad.service.StudyLoadService;

import java.util.List;

public class StudyLoadServiceImpl implements StudyLoadService {
    private EntityManagerStudyLoad entityManagerStudyLoad = new EntityManagerStudyLoad();

    @Override
    public List<TeacherModel> getTeachers (String department) {
        return entityManagerStudyLoad.getTeachers(department);
    }

    @Override
    public  List<DepartmentModel> getDepartments() {
        return entityManagerStudyLoad.getDepartments();
    }

    @Override
    public List<EmploymentModel> getEmployment(TeacherModel selectTeacher, String department) {
        return entityManagerStudyLoad.getEmployment(selectTeacher, department); }


    @Override
    public List<String> getPosition() {
        return entityManagerStudyLoad.getPosition();
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
    public void updateVacancy(Long id_vacancy, String rolename, String wagerate) {
        entityManagerStudyLoad.updateVacancy(id_vacancy, rolename, wagerate);
    }

    @Override
    public void createVacancy(String rolename, String wagerate) {
        entityManagerStudyLoad.createVacancy(rolename, wagerate);
    }

    @Override
    public void updateEmployment(Long id_vacancy, String shorttitle, String byworker, String rolename, Double wagerate, Double time_wagerate) {
        entityManagerStudyLoad.updateEmployment(id_vacancy, shorttitle, byworker, rolename, wagerate, time_wagerate);
    }

    @Override
    public void deleteVacancy(Long id_vacancy) {
        entityManagerStudyLoad.deleteVacancy(id_vacancy);
    }
}
