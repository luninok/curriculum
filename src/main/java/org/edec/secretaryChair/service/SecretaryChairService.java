package org.edec.secretaryChair.service;

import org.edec.secretaryChair.model.CommissionDayModel;
import org.edec.secretaryChair.model.CommissionModel;
import org.edec.secretaryChair.model.EmployeeModel;
import org.edec.secretaryChair.model.StudentCommissionModel;
import org.edec.teacher.model.commission.StudentModel;
import org.edec.utility.component.model.SemesterModel;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Max Dimukhametov
 */
public interface SecretaryChairService {
    List<CommissionModel> getCommission (Long idSem, Long idChair, Integer formOfStudy, boolean signed);

    List<CommissionDayModel> getInfoCommissionDays (CommissionModel commission);

    List<SemesterModel> getSemesterByChair (Long idChair, Integer formOfStudy);

    List<StudentModel> getStudentByCommission (Long idCommission);

    /**
     * Проверка даты и времени, чтобы не было других комиссий на заданную дату
     *
     * @return
     */
    List<StudentCommissionModel> getStudentsForCheckFreeDate (Long idComm, Date dateComm);

    List<EmployeeModel> getEmployeeByChair (Long idChair);

    List<EmployeeModel> getEmployeeByCommission (Long idCommission);

    Map<Integer, List<Date>> getFreeIntervalByDate (List<Date> dates);

    boolean updateCommissionInfo (Date dateCommission, String classroom, Long idCommission);

    boolean deleteCommissionStaff (Long idCommission);

    boolean addCommissionStaff (EmployeeModel employee, Long idCommission);
}
