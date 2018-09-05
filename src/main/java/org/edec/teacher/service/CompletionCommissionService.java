package org.edec.teacher.service;

import org.edec.teacher.model.SemesterModel;
import org.edec.teacher.model.commission.CommissionModel;
import org.edec.teacher.model.commission.EmployeeModel;
import org.edec.teacher.model.commission.StudentModel;

import java.util.List;

/**
 * @author Max Dimukhametov
 */
public interface CompletionCommissionService {
    List<SemesterModel> getSemesterCommByHum (Long idHum, Long idRC, Boolean withSign);
    CommissionModel getCommissionByHumAndRC (Long idRC);
    List<EmployeeModel> getEmployeesByCommission (Long idRC);
    List<StudentModel> getStudentsByCommission (Long idRC);
}
