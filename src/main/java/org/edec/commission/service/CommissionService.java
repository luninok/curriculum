package org.edec.commission.service;

import org.edec.commission.model.*;
import org.edec.model.SemesterModel;

import java.util.Date;
import java.util.List;

/**
 * Created by dmmax
 */
public interface CommissionService {
    List<StudentCountDebtModel> getListStudentCountDebt (String qualification, Integer course, Integer debtCount, String typeDebt,
                                                         Integer government, String listIdSem, boolean prolongation, Date dateProlongation,
                                                         Integer formofstudy, Long idInst);

    /**
     * Получение долгов студента
     *
     * @param idSc      - ид студентческой карты
     * @param idDg      - ид группы
     * @param listIdSem - список семестров, по которым ищем
     * @return - список долгов
     */
    List<StudentDebtModel> getDevidedByFocStudentsDebt (Long idSc, Long idDg, String listIdSem, Integer formOfStudy);
    List<StudentDebtModel> getStudentByRegisterCommission (Long idRegComm);
    List<StudentCountDebtModel> getStudentCountDebtByFio (String fio, List<StudentCountDebtModel> tempList);
    List<StudentCountDebtModel> getStudentCountDebtByGroup (String group, List<StudentCountDebtModel> tempList);
    List<SubjectDebtModel> getSubjectCommissionByFilterAndSem (String fioGroupSubject, Long idSem, PeriodCommissionModel period,
                                                               Integer formOfStudy);
    List<SubjectDebtModel> getSubjForCreateCommission (String listIdSSS, Date dateBegin, Date dateEnd);
    List<SubjectDebtModel> getSubjectsByFilter (String filter, List<SubjectDebtModel> allSubjects);
    List<SemesterModel> getAllSemesterWithCommission (Integer formOfStudy);
    List<SemesterModel> getSemesterByInstAndFOS (Long idInst, Integer fos);
    List<CommissionStructureModel> getCommissionStructure (Long idCommission);
    List<PeriodCommissionModel> getPeriodCommission (Integer formOfStudy);

    boolean createIndividualCommission (StudentDebtModel studentDebtModel, Date dateBegin, Date dateEnd, Long idCurrentUser);
    boolean createCommonCommission (SubjectDebtModel subjectDebtModel, List<StudentDebtModel> students, Long idCurrentUser);
    boolean deleteCommission (Long idRegComm);
    boolean deleteSRHfromCommRegister (String listIDsrh);
    boolean updateCommissionRegister (Long idRegComm, Date dateBegin, Date dateEnd);
    boolean setCheckKutsSrh (Long idSrh, boolean status);
    void setStatusSignedForSubjAndStudent (SubjectDebtModel subject);
}
