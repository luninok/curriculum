package org.edec.studyLoad.service;

import org.edec.commission.model.PeriodCommissionModel;
import org.edec.commission.model.SubjectDebtModel;
import org.edec.studyLoad.model.TeacherModel;

import java.util.List;

public interface StudyLoadService {
    List<TeacherModel> getTeachers (String department);
}
