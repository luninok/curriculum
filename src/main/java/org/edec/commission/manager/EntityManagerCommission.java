package org.edec.commission.manager;

import org.edec.commission.model.*;
import org.edec.dao.DAO;
import org.edec.model.SemesterModel;
import org.edec.synchroMine.model.eso.entity.Register;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.Collections;
import java.util.Date;
import java.util.List;


public class EntityManagerCommission extends DAO {
    public List<StudentCountDebtModel> getStudentCountDebt (String qualification, Integer course, Integer debtCount, String typeDebt,
                                                            Integer government, String listIdSem, boolean prolongation,
                                                            Date dateEndProlongation, Integer formofstudy, Long idInst) {
        String governmentFinanced = "";
        if (government == 1) {
            governmentFinanced = "1";
        } else if (government == 2) {
            governmentFinanced = "0";
        }
        //TODO: ид института сделать динамическим
        String query = "WITH search AS (SELECT SSS.id_studentcard\n" + "FROM student_semester_status SSS \n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                       "WHERE SEM.id_institute = :idInst AND SEM.is_current_sem = 1 AND SSS.is_deducted = 0 AND SSS.is_academicleave = 0)" +
                       "SELECT *\n" +
                       "FROM (SELECT HF.family||' '||HF.name||' '||HF.patronymic AS fio, SC.id_studentcard AS idSC, DG.id_dic_group AS idDG,DG.groupname,\n" +
                       "\t\tTRIM(both '{}' FROM CAST(array_agg(SSS.id_student_semester_status) AS TEXT)) AS listSSS,\n" +
                       "\t\tCAST((SUM(CASE WHEN (SR.is_exam = 1 AND SR.examrating < 3) THEN 1 ELSE 0 END)\n" +
                       "\t\t+SUM(CASE WHEN (SR.is_pass = 1 AND SR.passrating <> 1 AND SR.passrating < 3) THEN 1 ELSE 0 END)\n" +
                       "\t\t+SUM(CASE WHEN (SR.is_courseproject = 1 AND SR.courseprojectrating < 3) THEN 1 ELSE 0 END)\n" +
                       "\t\t+SUM(CASE WHEN (SR.is_coursework = 1 AND SR.courseworkrating < 3) THEN 1 ELSE 0 END)\n" +
                       "\t\t+SUM(CASE WHEN (SR.is_practic = 1 AND SR.practicrating < 3) THEN 1 ELSE 0 END)) AS INTEGER) AS debt\n" +
                       "\tFROM humanface HF \n" + "\t\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                       "\t\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                       "\t\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN semester SEM ON SEM.id_semester = LGS.id_semester\n" +
                       "\t\tINNER JOIN dic_group DG USING (id_dic_group)\n" + "\t\tINNER JOIN curriculum CUR USING (id_curriculum)\n" +
                       "\t\tINNER JOIN sessionrating SR USING (id_student_semester_status)\n" +
                       "\t\tINNER JOIN search ON SSS.id_studentcard = search.id_studentcard\n" +
                       "\tWHERE SC.id_current_dic_group = DG.id_dic_group " + (listIdSem == null
                                                                               ?
                                                                               "AND LGS.id_semester < (SELECT id_semester FROM semester WHERE id_institute = :idInst AND is_current_sem = 1 AND formofstudy = " +
                                                                               formofstudy + ")"
                                                                               : "AND LGS.id_semester IN (" + listIdSem + ")") + "\n" +
                       "\t\tAND CUR.qualification IN (" + qualification + ")\n" +
                       (prolongation ? "\t\tAND ((SSS.is_sessionprolongation = 0) OR (SSS.is_sessionprolongation = 1 AND '" +
                                       dateEndProlongation + "' > SSS.prolongationenddate))\n" : "") +
                       "\t\tAND CAST(LGS.course AS TEXT) LIKE '%" + (course == 0 ? "" : course) + "%'\n" +
                       "\t\tAND CAST(SSS.is_government_financed AS TEXT) LIKE '%" + governmentFinanced + "%'\n" +
                       "\t\tAND SEM.formofstudy = " + formofstudy + " \n" + "\t\tAND SSS.is_listener = 0\n" +
                       "\tGROUP BY fio, idSC, idDG\n" + "\tHAVING SUM(SSS.is_deducted) = 0 AND SUM(SSS.is_academicleave) = 0\n" +
                       "\tORDER BY fio) myTab\n" + "WHERE debt " + typeDebt + debtCount;

        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("groupname")
                              .addScalar("idSC", LongType.INSTANCE)
                              .addScalar("debt")
                              .addScalar("idDG", LongType.INSTANCE)
                              .addScalar("listSSS")
                              .setResultTransformer(Transformers.aliasToBean(StudentCountDebtModel.class))
                              .setParameter("idInst", idInst);
        return (List<StudentCountDebtModel>) getList(q);
    }

    public List<StudentDebtModel> getStudentDebt (Long idSc, Long idDg, String listIdSem, Integer formOfStudy) {
        //TODO: ид института сделать динамическим
        String query =
                "SELECT SR.is_exam = 1 AS exam, SR.is_pass = 1 AS pass, SR.is_courseproject = 1 AS cp, SR.is_coursework = 1AS cw, SR.is_practic = 1 AS practic,\n" +
                "\tSR.examrating, SR.passrating, SR.courseprojectrating AS cprating, SR.courseworkrating AS cwrating, SR.practicrating,\n" +
                "\tDS.subjectname, LGS.semesternumber, SR.id_sessionrating AS idSr, SR.id_subject AS idSubj, LGS.id_semester AS idSemester, \n" +
                "\t(SELECT fulltitle FROM department WHERE id_chair = S.id_chair AND is_main = TRUE),\n" +
                "\tEXTRACT(YEAR FROM SY.dateofbegin)||'-'||EXTRACT(YEAR FROM SY.dateofend)||' '||CASE WHEN SEM.season = 0 THEN 'осень' ELSE 'весна' END AS semesterStr,\n" +
                "\tEXISTS(SELECT id_sessionratinghistory FROM sessionratinghistory WHERE is_exam = 1 AND retake_count = -3 AND id_sessionrating = SR.id_sessionrating) AS examComm,\n" +
                "\tEXISTS(SELECT id_sessionratinghistory FROM sessionratinghistory WHERE is_pass = 1 AND retake_count = -3 AND id_sessionrating = SR.id_sessionrating) AS passComm,\n" +
                "\tEXISTS(SELECT id_sessionratinghistory FROM sessionratinghistory WHERE is_courseproject = 1 AND retake_count = -3 AND id_sessionrating = SR.id_sessionrating) AS cpComm,\n" +
                "\tEXISTS(SELECT id_sessionratinghistory FROM sessionratinghistory WHERE is_coursework = 1 AND retake_count = -3 AND id_sessionrating = SR.id_sessionrating) AS cwComm,\n" +
                "\tEXISTS(SELECT id_sessionratinghistory FROM sessionratinghistory WHERE is_practic = 1 AND retake_count = -3 AND id_sessionrating = SR.id_sessionrating) AS practicComm\n" +
                "FROM student_semester_status SSS\n" + "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN sessionrating SR USING (id_student_semester_status)\n" + "\tINNER JOIN subject S USING (id_subject)\n" +
                "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" + "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                "WHERE ((SR.is_exam = 1 AND SR.examrating < 3) OR (SR.is_pass = 1 AND SR.passrating <> 1 AND SR.passrating < 3) OR (SR.is_courseproject = 1 AND SR.courseprojectrating < 3)\n" +
                "\t\tOR (SR.is_coursework = 1 AND SR.courseworkrating < 3) OR (SR.is_practic = 1 AND SR.practicrating < 3))\n" +
                "\tAND LGS.id_dic_group = " + idDg + " AND SSS.id_studentcard = " + idSc + "\n" + (listIdSem == null
                                                                                                   ?
                                                                                                   "\tAND LGS.id_semester < (SELECT id_semester FROM semester WHERE is_current_sem = 1 AND id_institute = 1 AND formofstudy = " +
                                                                                                   formOfStudy + ")"
                                                                                                   : "\tAND LGS.id_semester IN (" +
                                                                                                     listIdSem + ")");
        Query q = getSession().createSQLQuery(query)
                              .addScalar("exam")
                              .addScalar("pass")
                              .addScalar("cp")
                              .addScalar("cw")
                              .addScalar("practic")
                              .addScalar("examrating")
                              .addScalar("passrating")
                              .addScalar("cprating")
                              .addScalar("cwrating")
                              .addScalar("practicrating")
                              .addScalar("examComm")
                              .addScalar("passComm")
                              .addScalar("cpComm")
                              .addScalar("cwComm")
                              .addScalar("practicComm")
                              .addScalar("subjectname")
                              .addScalar("fulltitle")
                              .addScalar("semesternumber")
                              .addScalar("semesterStr")
                              .addScalar("idSr", LongType.INSTANCE)
                              .addScalar("idSubj", LongType.INSTANCE)
                              .addScalar("idSemester", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(StudentDebtModel.class));
        return (List<StudentDebtModel>) getList(q);
    }

    public List<SubjectDebtModel> getSubjectCommissionByFilterAndSem (String fioGroupSubject, Long idSem, PeriodCommissionModel period,
                                                                      Integer formOfStudy) {
        String query = "SELECT DISTINCT ON (id_register_comission, semesterStr, fulltitle, subjectname)\n" +
                       "\tDS.subjectname, (R.certnumber IS NOT NULL) AS signed,\n" + "\tCASE WHEN SRH.is_exam = 1 THEN 'Экзамен' \n" +
                       "\t\tWHEN SRH.is_pass = 1 THEN 'Зачет' \n" + "\t\tWHEN SRH.is_courseproject = 1 THEN 'КП'\n" +
                       "\t\tWHEN SRH.is_coursework = 1 THEN 'КР'\n" + "\t\tWHEN SRH.is_practic = 1 THEN 'Практика'\n" +
                       "\tELSE '???' END AS focStr, \n" +
                       "\tRC.id_register_comission AS idRegComission, RC.comission_date AS dateComission, RC.classroom,\n" +
                       "\tRC.dateofbegincomission, RC.dateofendcomission, RC.id_subject AS idSubj, LGS.id_semester AS idSemester,\n" +
                       "\tCAST(COUNT(SRH.*) AS INTEGER) AS countstudent, CAST(SUM(CASE WHEN SRH.check_commission = TRUE THEN 1 ELSE 0 END) AS INTEGER) AS checkedcount, \n" +
                       "\t(SELECT fulltitle FROM department WHERE id_chair = S.id_chair AND is_main IS true),\n" +
                       "\tEXTRACT(YEAR FROM SY.dateofbegin)||'-'||EXTRACT(YEAR FROM SY.dateofend)||' '||CASE WHEN SEM.season = 0 THEN 'осень' ELSE 'весна' END AS semesterStr\n" +
                       "FROM register_comission RC\n" + "\tINNER JOIN subject S USING (id_subject)\n" +
                       "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" + "\tINNER JOIN register R USING (id_register)\n" +
                       "\tINNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                       "\tINNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                       "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                       "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" + "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                       "\tINNER JOIN studentcard SC USING (id_studentcard)\n" + "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                       "WHERE CAST(LGS.id_semester AS TEXT) LIKE  '%" + (idSem == null ? "" : idSem) + "%'\n" +
                       "\tAND HF.family||' '||HF.name||' '||HF.patronymic||' '||DG.groupname||' '||DS.subjectname ILIKE '%" +
                       fioGroupSubject + "%'\n" + "\tAND SEM.formofstudy = " + formOfStudy + "\n" + (period == null
                                                                                                     ? ""
                                                                                                     : "\tAND RC.dateofbegincomission = '" +
                                                                                                       period.getDateOfBegin() +
                                                                                                       "' AND RC.dateofendcomission = '" +
                                                                                                       period.getDateOfEnd() + "'\n") +
                       "GROUP BY idRegComission, idSubj, signed, focStr, semesterStr, subjectname, id_chair, LGS.id_semester\n" +
                       "ORDER BY semesterStr, fulltitle, subjectname";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("subjectname")
                              .addScalar("focStr")
                              .addScalar("signed")
                              .addScalar("idSubj", LongType.INSTANCE)
                              .addScalar("semesterStr")
                              .addScalar("dateofbegincomission")
                              .addScalar("dateofendcomission")
                              .addScalar("dateComission")
                              .addScalar("classroom")
                              .addScalar("fulltitle")
                              .addScalar("idRegComission", LongType.INSTANCE)
                              .addScalar("idSemester", LongType.INSTANCE)
                              .addScalar("countstudent")
                              .addScalar("checkedcount")
                              .setResultTransformer(Transformers.aliasToBean(SubjectDebtModel.class));
        return (List<SubjectDebtModel>) getList(q);
    }

    public List<StudentDebtModel> getStudentByIdRegCommission (Long idRegCommission) {
        String query =
                "SELECT HF.family||' '||HF.name||' '||HF.patronymic AS fio, DG.groupname,\n" + "\tSRH.id_sessionratinghistory AS idSrh,\n" +
                "\tSRH.newrating AS rating,\n" + "\tSC.id_studentcard AS idSc,\n" + "\tSSS.id_student_semester_status AS idSSS,\n" +
                "\tSRH.check_commission = TRUE AS checkKuts\n" + "FROM register_comission RC\n" +
                "\tINNER JOIN register R USING (id_register)\n" + "\tINNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                "\tINNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" + "\tINNER JOIN studentcard SC USING (id_studentcard)\n" +
                "\tINNER JOIN humanface HF USING (id_humanface)\n" + "WHERE RC.id_register_comission = " + idRegCommission + "\n" +
                "ORDER BY fio, DG.groupname";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("groupname")
                              .addScalar("idSrh", LongType.INSTANCE)
                              .addScalar("idSc", LongType.INSTANCE)
                              .addScalar("idSSS", LongType.INSTANCE)
                              .addScalar("rating")
                              .addScalar("checkKuts")
                              .setResultTransformer(Transformers.aliasToBean(StudentDebtModel.class));
        return (List<StudentDebtModel>) getList(q);
    }

    public List<SemesterModel> getSemesterCommission (int formOfStudy) {
        //TODO: ид института сделать динамическим
        String query =
                "SELECT  DISTINCT R.id_semester AS idSem, SY.dateofbegin, SY.dateofend, SEM.season\n" + "FROM    register_comission RC\n" +
                "\tINNER JOIN subject S USING (id_subject)\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "\tINNER JOIN register R USING (id_register)\n" + "\tINNER JOIN semester SEM ON R.id_semester = SEM.id_semester\n" +
                "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" + "WHERE   SEM.formofstudy = :formOfStudy\n" + "ORDER BY idsem";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idSem", LongType.INSTANCE)
                              .addScalar("dateOfBegin")
                              .addScalar("dateOfEnd")
                              .addScalar("season")
                              .setResultTransformer(Transformers.aliasToBean(SemesterModel.class));
        q.setInteger("formOfStudy", formOfStudy);
        return (List<SemesterModel>) getList(q);
    }

    public List<CommissionStructureModel> getCommissionStructure (Long idRegComm) {
        String query = "SELECT HF.family||' '||HF.name||' '||HF.patronymic AS fio,     \n" + "\tER.rolename, (LD.leader=1) AS leader\n" +
                       "FROM department DEP     \n" + "\tINNER JOIN link_employee_department LED USING(id_department)     \n" +
                       "\tINNER JOIN employee_role ER USING(id_employee_role)     \n" +
                       "\tINNER JOIN employee EMP USING(id_employee)     \n" + "\tINNER JOIN humanface HF USING(id_humanface)     \n" +
                       "\tINNER JOIN led_comission LD USING(id_link_employee_department)\n" + "WHERE LD.id_register_comission = " +
                       idRegComm;
        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("rolename")
                              .addScalar("leader")
                              .setResultTransformer(Transformers.aliasToBean(CommissionStructureModel.class));
        return (List<CommissionStructureModel>) getList(q);
    }

    public List<StudentDebtModel> getStudentDebtByListSSSandDateCommission (String listIdSSS, Date dateEnd) {
        String query = "SELECT HF.family||' '||HF.name||' '||HF.patronymic AS fio, DG.groupname, COALESCE (S.id_chair,0) AS idChair,\n" +
                       "\tSR.is_exam = 1 AS exam, SR.is_pass = 1 AS pass, SR.is_courseproject = 1 AS cp, SR.is_coursework = 1 AS cw, SR.is_practic = 1 AS practic,\n" +
                       "\tSR.examrating, SR.passrating, SR.courseprojectrating AS cprating, SR.courseworkrating AS cwrating, SR.practicrating,\n" +
                       "\tDS.subjectname, LGS.semesternumber, SR.id_sessionrating AS idSr, SR.id_subject AS idSubj, LGS.id_semester AS idSemester,\n" +
                       "\t(SELECT fulltitle FROM department WHERE id_chair = S.id_chair AND is_main = TRUE),\n" +
                       "\tEXTRACT(YEAR FROM SY.dateofbegin)||'-'||EXTRACT(YEAR FROM SY.dateofend)||' '||CASE WHEN SEM.season = 0 THEN 'осень' ELSE 'весна' END AS semesterStr,\n" +
                       "\tEXISTS(SELECT id_sessionratinghistory FROM sessionratinghistory \n" +
                       "\t\tWHERE is_exam = SR.is_exam AND is_pass = 0 AND is_courseproject = 0 AND is_coursework = 0 AND is_practic = 0 AND retake_count = -3 AND id_sessionrating = SR.id_sessionrating) AS examComm,\n" +
                       "\tEXISTS(SELECT id_sessionratinghistory FROM sessionratinghistory \n" +
                       "\t\tWHERE is_exam = 0 AND is_pass = SR.is_pass AND is_courseproject = 0 AND is_coursework = 0 AND is_practic = 0 AND retake_count = -3 AND id_sessionrating = SR.id_sessionrating) AS passComm,\n" +
                       "\tEXISTS(SELECT id_sessionratinghistory FROM sessionratinghistory \n" +
                       "\t\tWHERE is_exam = 0 AND is_pass = 0 AND is_courseproject = SR.is_courseproject AND is_coursework = 0 AND is_practic = 0 AND retake_count = -3 AND id_sessionrating = SR.id_sessionrating) AS cpComm,\n" +
                       "\tEXISTS(SELECT id_sessionratinghistory FROM sessionratinghistory \n" +
                       "\t\tWHERE is_exam = 0 AND is_pass = 0 AND is_courseproject = 0 AND is_coursework = SR.is_coursework AND is_practic = 0 AND retake_count = -3 AND id_sessionrating = SR.id_sessionrating) AS cwComm,\n" +
                       "\tEXISTS(SELECT id_sessionratinghistory FROM sessionratinghistory \n" +
                       "\t\tWHERE is_exam = 0 AND is_pass = 0 AND is_courseproject = 0 AND is_coursework = 0 AND is_practic = SR.is_practic AND retake_count = -3 AND id_sessionrating = SR.id_sessionrating) AS practicComm\n" +
                       "FROM student_semester_status SSS\n" + "\tINNER JOIN studentcard SC USING (id_studentcard)\n" +
                       "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                       "\tINNER JOIN sessionrating SR USING (id_student_semester_status)\n" +
                       "\tINNER JOIN subject S USING (id_subject)\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                       "\tINNER JOIN semester SEM USING (id_semester)\n" + "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                       "WHERE SSS.id_student_semester_status IN (" + listIdSSS + ")\n" +
                       "\tAND ((SSS.is_sessionprolongation = 0) OR (SSS.is_sessionprolongation = 1 AND '" + dateEnd +
                       "' > SSS.prolongationenddate))\n" +
                       "\tAND ((SR.is_exam = 1 AND SR.examrating < 3) OR (SR.is_pass = 1 AND SR.passrating <> 1 AND SR.passrating < 3) OR (SR.is_courseproject = 1 AND SR.courseprojectrating < 3)\n" +
                       "\t\tOR (SR.is_coursework = 1 AND SR.courseworkrating < 3) OR (SR.is_practic = 1 AND SR.practicrating < 3))\n" +
                       "ORDER BY fio";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("exam")
                              .addScalar("pass")
                              .addScalar("cp")
                              .addScalar("cw")
                              .addScalar("practic")
                              .addScalar("examrating")
                              .addScalar("passrating")
                              .addScalar("cprating")
                              .addScalar("cwrating")
                              .addScalar("practicrating")
                              .addScalar("examComm")
                              .addScalar("passComm")
                              .addScalar("cpComm")
                              .addScalar("cwComm")
                              .addScalar("practicComm")
                              .addScalar("subjectname")
                              .addScalar("fulltitle")
                              .addScalar("semesternumber")
                              .addScalar("semesterStr")
                              .addScalar("idSr", LongType.INSTANCE)
                              .addScalar("idSubj", LongType.INSTANCE)
                              .addScalar("idChair", LongType.INSTANCE)
                              .addScalar("fio")
                              .addScalar("groupname")
                              .addScalar("idSemester", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(StudentDebtModel.class));
        return (List<StudentDebtModel>) getList(q);
    }

    public List<PeriodCommissionModel> getPeriodCommission (Integer formOfStudy) {
        //TODO: ид института сделать динамическим
        String query = "SELECT\tdateofbegincomission AS dateOfBegin, dateofendcomission AS dateOfEnd\n" + "FROM \tregister_comission \n" +
                       "\tINNER JOIN register R USING (id_register)\n" + "\tINNER JOIN semester SEM USING (id_semester)\n" +
                       "WHERE \tSEM.formofstudy = " + formOfStudy + "\n" + "GROUP BY dateofbegincomission, dateofendcomission\n" +
                       "ORDER BY dateofbegincomission, dateofendcomission";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("dateOfBegin")
                              .addScalar("dateOfEnd")
                              .setResultTransformer(Transformers.aliasToBean(PeriodCommissionModel.class));
        return (List<PeriodCommissionModel>) getList(q);
    }

    public boolean createIndividualCommission (StudentDebtModel studentDebtModel, Date dateBeginComm, Date dateEndComm,
                                               Long idCurrentUser) {
        int exam = 0, pass = 0, cp = 0, cw = 0, practic = 0;
        switch (studentDebtModel.getFocStr()) {
            case "Экзамен":
                exam = 1;
                break;
            case "Зачет":
                pass = 1;
                break;
            case "КП":
                cp = 1;
                break;
            case "КР":
                cw = 1;
                break;
            case "Практика":
                practic = 1;
                break;
        }

        try {
            begin();
            //Создание ведомости
            Register newReg = new Register();
            newReg.setIdSemester(studentDebtModel.getIdSemester());
            newReg.setIsCanceled(false);
            long idReg = (Long) getSession().save(newReg);
            //Создание SRH для комиссии
            String queryCreateSrh = "INSERT INTO sessionratinghistory \n" +
                                    "\t\t(id_sessionrating, is_exam, is_pass, is_courseproject, is_coursework, is_practic, retake_count, id_systemuser, id_humanface_last_action, id_register)\n" +
                                    "\t\tVALUES (" + studentDebtModel.getIdSr() + ", " + exam + ", " + pass + ", " + cp + ", " + cw + ", " +
                                    practic + ", -3,  17, " + idCurrentUser + " ," + newReg.getId() + ");";
            Query qSrh = getSession().createSQLQuery(queryCreateSrh);
            qSrh.executeUpdate();
            String queryComission =
                    "INSERT INTO register_comission " + "(id_register, dateofbegincomission, dateofendcomission, id_subject) " +
                    "VALUES (" + newReg.getId() + ", '" + dateBeginComm + "', '" + dateEndComm + "', " + studentDebtModel.getIdSubj() + ")";
            Query qComission = getSession().createSQLQuery(queryComission);
            qComission.executeUpdate();
            commit();
            return true;
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
            return false;
        } finally {
            close();
        }
    }

    public boolean createCommonCommission (SubjectDebtModel subjectDebtModel, List<StudentDebtModel> students, Long idCurrentUser) {
        int exam = 0, pass = 0, cp = 0, cw = 0, practic = 0;
        switch (subjectDebtModel.getFocStr()) {
            case "Экзамен":
                exam = 1;
                break;
            case "Зачет":
                pass = 1;
                break;
            case "КП":
                cp = 1;
                break;
            case "КР":
                cw = 1;
                break;
            case "Практика":
                practic = 1;
                break;
        }
        try {
            begin();
            Register newReg = new Register();
            newReg.setIsCanceled(false);
            newReg.setIdSemester(subjectDebtModel.getIdSemester());
            long idReg = (Long) getSession().save(newReg);
            for (StudentDebtModel student : students) {
                if (student.isOpenComm()) {
                    continue;
                }
                String query = "INSERT INTO sessionratinghistory \n" +
                               "\t\t(id_sessionrating, is_exam, is_pass, is_courseproject, is_coursework, is_practic, retake_count, id_systemuser, id_humanface_last_action, id_register)\n" +
                               "\t\tVALUES (" + student.getIdSr() + ", " + exam + ", " + pass + ", " + cp + ", " + cw + ", " + practic +
                               ", -3,  17, " + idCurrentUser + "," + newReg.getId() + ");";
                Query q = getSession().createSQLQuery(query);
                q.executeUpdate();
            }
            String queryComission =
                    "INSERT INTO register_comission " + "(id_register, dateofbegincomission, dateofendcomission, id_subject) " +
                    "VALUES (" + idReg + ", '" + subjectDebtModel.getDateofbegincomission() + "', '" +
                    subjectDebtModel.getDateofendcomission() + "', " + subjectDebtModel.getIdSubj() + ")";
            Query qComission = getSession().createSQLQuery(queryComission);
            qComission.executeUpdate();
            commit();
            return true;
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
            return false;
        } finally {
            close();
        }
    }

    public boolean updateRegisterComission (Long idRegComission, Date datebegincomission, Date dateendcomission) {
        String query = "UPDATE register_comission SET dateofbegincomission = '" + datebegincomission + "'," + " dateofendcomission = '" +
                       dateendcomission + "' WHERE id_register_comission = " + idRegComission;
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean setCheckKutsForSRH (Long idSRH, Boolean status) {
        String query = "UPDATE sessionratinghistory SET check_commission = " + status + " WHERE id_sessionratinghistory = " + idSRH;
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean deleteSRHfromComissionRegister (String listSRHid) {
        String query = "DELETE FROM sessionratinghistory WHERE id_sessionratinghistory IN (" + listSRHid + ")";
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean deleteComission (Long idRegisterComission) {
        try {
            begin();
            String queryLED = "DELETE FROM led_comission WHERE id_register_comission = " + idRegisterComission;
            Query qLED = getSession().createSQLQuery(queryLED);
            qLED.executeUpdate();
            String query = "DELETE FROM register_comission WHERE id_register_comission = " + idRegisterComission + " RETURNING id_register";
            Query q = getSession().createSQLQuery(query);
            List<Long> list = q.list();
            String deleteRegister = "DELETE FROM register WHERE id_register = " + list.get(0);
            Query queryDeleteRegister = getSession().createSQLQuery(deleteRegister);
            queryDeleteRegister.executeUpdate();
            commit();
            return true;
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
            return false;
        } finally {
            close();
        }
    }
}
