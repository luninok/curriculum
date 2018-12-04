package org.edec.studyLoad.service.impl;

import org.edec.main.model.DepartmentModel;
import org.edec.studyLoad.manager.EntityManagerStudyLoad;
import org.edec.studyLoad.model.AssignmentModel;
import org.edec.studyLoad.model.PositionModel;
import org.edec.studyLoad.model.TeacherModel;
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
    public List<String> getPosition() {
        return entityManagerStudyLoad.getPosition();
    }

    @Override
    public List<PositionModel> getPositions() {
        return entityManagerStudyLoad.getPositions();
    }

    @Override
    public List<AssignmentModel> getInstructions(Long idSem, Long idDepartment){
        return entityManagerStudyLoad.getInstructions(idSem, idDepartment);
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
