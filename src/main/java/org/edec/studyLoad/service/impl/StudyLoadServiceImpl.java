package org.edec.studyLoad.service.impl;

import org.edec.commission.manager.EntityManagerCommission;
import org.edec.commission.model.PeriodCommissionModel;
import org.edec.commission.model.SubjectDebtModel;
import org.edec.studyLoad.manager.EntityManagerStudyLoad;
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
    public  List<String> getDepartments() {
        return entityManagerStudyLoad.getDepartments();
    }
}
