package org.edec.studyLoad.service.impl;

import org.edec.main.model.DepartmentModel;
import org.edec.studyLoad.manager.EntityManagerStudyLoad;
import org.edec.studyLoad.model.AssignmentModel;
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
    public  List<DepartmentModel> getDepartments() {
        return entityManagerStudyLoad.getDepartments();
    }

    @Override
    public List<String> getPosition() {
        return entityManagerStudyLoad.getPosition();
    }

    @Override
    public List<AssignmentModel> getInstructions(Long idSem, Long idDepartment){
        return entityManagerStudyLoad.getInstructions(idSem, idDepartment);
    }
}
