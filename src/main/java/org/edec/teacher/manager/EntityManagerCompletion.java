package org.edec.teacher.manager;

import org.edec.dao.DAO;
import org.edec.teacher.model.EsoCourseModel;
import org.edec.teacher.model.commission.EmployeeModel;
import org.edec.teacher.model.commission.StudentModel;
import org.edec.teacher.model.dao.CompletionCommESOmodel;
import org.edec.teacher.model.dao.CompletionESOModel;
import org.edec.teacher.model.dao.CourseHistoryESOModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.NumericBooleanType;
import org.hibernate.type.StringType;

import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class EntityManagerCompletion extends DAO {
    public List<CompletionESOModel> getCompletionESOModel (Long idHum, boolean unSignedRegister) {
        String query =
                "SELECT\n \tCOALESCE(INST.shorttitle, INST.fulltitle) AS institute," +
                " SEM.formofstudy," +
                " SEM.season," +
                " LGS.semesternumber," +
                " SEM.id_semester AS idSemester, \n" +
                "\tCASE WHEN SEM.season = 0 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'/'||EXTRACT(YEAR FROM SY.dateofend)||' осенний'\n" +
                "\t\tWHEN SEM.season = 1 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'/'||EXTRACT(YEAR FROM SY.dateofend)||' весенний' END AS semesterStr,\n" +
                "\t(SEM.is_current_sem=1) AS curSem,INST.id_institute AS idInstitute, LGSS.id_link_group_semester_subject AS idLGSS, DG.groupname, DS.subjectname, \n" +
                "\tS.is_exam=1 AS exam, S.is_pass=1 AS pass, S.is_courseproject=1 AS cp, S.is_coursework=1 AS cw, S.is_Practic=1 AS practic,\n" +
                "\tSY.dateofbegin, LGSS.id_esocourse2 AS idESOcourse, SY.dateofend, S.hourscount AS hoursCount, S.type, LGS.course\n" +
                "FROM employee EMP\n" + "\tINNER JOIN link_employee_subject_group LESG USING (id_employee)\n" +
                "\tINNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester_subject)\n" +
                "\tINNER JOIN subject S USING (id_subject)\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" + "\tINNER JOIN semester SEM USING (id_semester)\n" +
                "\tINNER JOIN institute INST ON SEM.id_institute = INST.id_institute\n" +
                "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" + "\tLEFT JOIN sessionrating SR USING (id_subject)\n" +
                "\tLEFT JOIN student_semester_status SSS ON SR.id_student_semester_status = SSS.id_student_semester_status\n" +
                "\tLEFT JOIN sessionratinghistory SRH ON SR.id_sessionrating = SRH.id_sessionrating" +
                " AND SRH.status NOT IN ('1.4.0', '1.4.1', '1.5.0', '1.5.1')\n" +
                "\t\tAND SRH.retake_count IN (" + (unSignedRegister ? "1,2,4" : "") + "-1, -2, -4)" +
                " AND SSS.is_academicleave = 0 AND SSS.is_deducted = 0\n" +
                (unSignedRegister ? "\tLEFT JOIN register R ON SRH.id_register = R.id_register\n" : "") +
                "WHERE EMP.id_humanface = :idHumanface\n" +
                "GROUP BY institute, SEM.formofstudy, SEM.season, LGS.semesternumber, idSemester, semesterStr,\n" +
                "\tcurSem, idInstitute, idLGSS, DG.groupname, DS.subjectname,\n" +
                "\texam, pass, cp, cw, practic, SY.dateofbegin, idESOcourse, SY.dateofend, hoursCount, S.type, LGS.course\n" +
                (unSignedRegister ? "\tHAVING MIN(R.signdate) is null" : "");
        Query q = getSession().createSQLQuery(query)
                              .addScalar("institute")
                              .addScalar("formofstudy")
                              .addScalar("semesterStr")
                              .addScalar("season")
                              .addScalar("curSem")
                              .addScalar("idLGSS", LongType.INSTANCE)
                              .addScalar("groupname")
                              .addScalar("subjectname")
                              .addScalar("exam")
                              .addScalar("pass")
                              .addScalar("cp")
                              .addScalar("cw")
                              .addScalar("practic")
                              .addScalar("dateofbegin")
                              .addScalar("idESOcourse", LongType.INSTANCE)
                              .addScalar("dateofend")
                              .addScalar("idSemester", LongType.INSTANCE)
                              .addScalar("hoursCount")
                              .addScalar("semesterNumber")
                              .addScalar("type")
                              .addScalar("course")
                              .addScalar("idInstitute", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(CompletionESOModel.class));

        q.setLong("idHumanface", idHum);
        return (List<CompletionESOModel>) getList(q);
    }

    public List<CompletionCommESOmodel> getCompletionCommESOmodel (Long idHum, Long idRC, boolean withSign) {
        String query = "SELECT\n" + "\tDISTINCT ON (R.id_register)\n" +
                       "\tCOALESCE(INST.shorttitle, INST.fulltitle) AS institute, SEM.formofstudy, SEM.id_semester AS idSem,\n" +
                       "\tCASE\n" +
                       "\t\tWHEN SEM.season = 0 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'/'||EXTRACT(YEAR FROM SY.dateofend)||' осенний'\n" +
                       "\t\tWHEN SEM.season = 1 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'/'||EXTRACT(YEAR FROM SY.dateofend)||' весенний'\n" +
                       "\tEND AS semesterStr,(SEM.is_current_sem=1) AS curSem, SY.dateofbegin AS dateOfBeginSY, SEM.season, \n" +
                       "\tDS.subjectname AS subjectName, RC.comission_date AS dateOfCommission, RC.id_register_comission AS idRC,\n" +
                       "\tR.id_register AS idReg, R.signatorytutor,  R.certnumber, R.register_number AS regNumber,\n" +
                       "\tCAST(LGS.course AS TEXT), SR.type, \n" + "\tCASE\n" + "\t\tWHEN SRH.is_exam = 1 THEN 1 \n" +
                       "\t\tWHEN SRH.is_pass = 1 THEN 2\n" + "\t\tWHEN SRH.is_courseproject = 1 THEN 3\n" +
                       "\t\tWHEN SRH.is_coursework = 1 THEN 4\n" + "\t\tWHEN SRH.is_practic = 1 THEN 5\n" +
                       "\tELSE 0 END AS formOfControl\n" + "FROM\n" + "\tled_comission LD\n" +
                       "\tINNER JOIN link_employee_department LED USING (id_link_employee_department)\n" +
                       "\tINNER JOIN employee EMP USING (id_employee)\n" +
                       "\tINNER JOIN register_comission RC USING (id_register_comission)\n" +
                       "\tINNER JOIN subject S USING (id_subject)\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                       "\tINNER JOIN register R USING (id_register)\n" + "\tINNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                       "\tINNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                       "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status )\n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                       "\tINNER JOIN institute INST USING (id_institute)\n" + "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                       "WHERE\n" + "\tCAST(EMP.id_humanface AS TEXT) ILIKE :idHum \n" +
                       "\tAND CAST(RC.id_register_comission AS TEXT) ILIKE :idRC\n" + "\t" +
                       (idRC == null && withSign ? " AND R.certnumber IS NOT NULL" : "");

        Query q = getSession().createSQLQuery(query)
                              .addScalar("institute")
                              .addScalar("formofstudy")
                              .addScalar("idSem", LongType.INSTANCE)
                              .addScalar("season")
                              .addScalar("semesterStr")
                              .addScalar("curSem")
                              .addScalar("subjectName")
                              .addScalar("dateOfBeginSY")
                              .addScalar("dateOfCommission")
                              .addScalar("idRC", LongType.INSTANCE)
                              .addScalar("idReg", LongType.INSTANCE)
                              .addScalar("signatorytutor")
                              .addScalar("certnumber")
                              .addScalar("regNumber")
                              .addScalar("type")
                              .addScalar("course")
                              .addScalar("formOfControl")
                              .setResultTransformer(Transformers.aliasToBean(CompletionCommESOmodel.class));
        q.setParameter("idHum", idHum == null ? "%%" : String.valueOf(idHum), StringType.INSTANCE)
         .setParameter("idRC", idRC == null ? "%%" : String.valueOf(idRC), StringType.INSTANCE);
        return (List<CompletionCommESOmodel>) getList(q);
    }

    public List<EmployeeModel> getEmployeeByCommission (Long idRC) {
        String query = "SELECT\n" + "\tHFteacher.family||' '||HFteacher.name||' '||HFteacher.patronymic AS fio, LD.leader AS chairman\n" +
                       "FROM\n" + "\tregister_comission RC\n" + "\tINNER JOIN led_comission LD USING (id_register_comission)\n" +
                       "\tINNER JOIN link_employee_department LED USING (id_link_employee_department)\n" +
                       "\tINNER JOIN employee EMP USING (id_employee)\n" + "\tINNER JOIN humanface HFteacher USING (id_humanface)\n" +
                       "WHERE\n" + "\tid_register_comission = :idRC\n" + "ORDER BY\n" + "\tLD.leader DESC, fio";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("chairman", NumericBooleanType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(EmployeeModel.class));
        q.setLong("idRC", idRC);
        return (List<EmployeeModel>) getList(q);
    }

    public List<StudentModel> getStudentsByCommission (Long idRC) {
        String query = "SELECT\n" +
                       "\tHF.family||' '||SUBSTRING(HF.name, 1, 1)||'. '||SUBSTRING(HF.patronymic, 1, 1)||'.' AS fio,DG.groupname AS groupName,\n" +
                       "\tCASE\n" + "\t\tWHEN newrating = 1 THEN 'Зачет'\n" + "\t\tWHEN newrating = 2 THEN 'Неудовл.'\n" +
                       "\t\tWHEN newrating = 3 THEN 'Удовл.'\n" + "\t\tWHEN newrating = 4 THEN 'Хорошо'\n" +
                       "\t\tWHEN newrating = 5 THEN 'Отлично'\n" + "\t\tWHEN newrating = -2 THEN 'Незачет'\n" +
                       "\t\tWHEN newrating = -3 THEN 'Н.Я.'\n" +
                       "\tELSE '' END AS ratingStr, SRH.newrating AS rating, SRH.id_sessionratinghistory AS idSRH\n" + "FROM\n" +
                       "\tregister_comission RC\n" + "\tINNER JOIN subject S USING (id_subject)\n" +
                       "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" + "\tINNER JOIN register R USING (id_register)\n" +
                       "\tINNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                       "\tINNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                       "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                       "\tINNER JOIN institute INST USING (id_institute)\n" + "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                       "\tINNER JOIN studentcard SC USING (id_studentcard)\n" + "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                       "WHERE\n" + "\tid_register_comission = :idRC\n" + "ORDER BY\n" + "\tfio";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("groupname")
                              .addScalar("rating")
                              .addScalar("ratingStr")
                              .addScalar("idSRH", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(StudentModel.class));
        q.setLong("idRC", idRC);
        return (List<StudentModel>) getList(q);
    }

    public List<EsoCourseModel> getEsoCourses () {
        String query = "SELECT id_esocourse2 AS idEsoCourse, id_category AS idCategory, fullname, shortname \n" + "FROM esocourse2 \n" +
                       "ORDER BY fullname";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idCategory")
                              .addScalar("idEsoCourse", LongType.INSTANCE)
                              .addScalar("fullname")
                              .addScalar("shortname")
                              .setResultTransformer(Transformers.aliasToBean(EsoCourseModel.class));

        return (List<EsoCourseModel>) getList(q);
    }

    public boolean updateRating (Integer rating, Long idSRH) {
        String query = "UPDATE sessionratinghistory SET newrating = " + rating + " WHERE id_sessionratinghistory =" + idSRH;
        Query q = getSession().createSQLQuery(query);
        return executeUpdate(q);
    }

    public boolean updateIdESOcourse (Long idLGSS, Long idESOcourse) {
        String query = "UPDATE link_group_semester_subject SET id_esocourse2 = :idESOcourse WHERE id_link_group_semester_subject = :idLGSS";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idLGSS", idLGSS).setParameter("idESOcourse", idESOcourse, LongType.INSTANCE);
        return executeUpdate(q);
    }

    public List<CourseHistoryESOModel> getAvailableCoursesForBinding (long idHumanface, long idCurSem) {
        String query =
                "select emp.id_employee as idEmp,\n" + "ds1.subjectname as subjectname,\n" + "lgss2.id_esocourse2 as idEsoCourse2,\n" +
                "ec2.fullname as fullname,\n" + "lgss1.id_link_group_semester_subject as idLGSS,\n" + "dg.groupname as groupName\n" +
                "from employee emp\n" + "inner join humanface hf using (id_humanface)\n" +
                "inner join link_employee_subject_group lesg1 on emp.id_employee = lesg1.id_employee\n" +
                "inner join link_group_semester_subject lgss1 on lesg1.id_link_group_semester_subject = lgss1.id_link_group_semester_subject\n" +
                "inner join subject subj1 on lgss1.id_subject = subj1.id_subject\n" +
                "inner join dic_subject ds1 on subj1.id_dic_subject = ds1.id_dic_subject\n" +
                "inner join link_group_semester lgs1 on lgss1.id_link_group_semester = lgs1.id_link_group_semester\n" +
                "inner join dic_group dg on lgs1.id_dic_group = dg.id_dic_group\n" +
                "inner join link_employee_subject_group lesg2 on emp.id_employee = lesg2.id_employee\n" +
                "inner join link_group_semester_subject lgss2 on lesg2.id_link_group_semester_subject = lgss2.id_link_group_semester_subject\n" +
                "inner join subject subj2 on lgss2.id_subject = subj2.id_subject\n" +
                "inner join dic_subject ds2 on subj2.id_dic_subject = ds2.id_dic_subject\n" +
                "inner join esocourse2 ec2 on lgss2.id_esocourse2 = ec2.id_esocourse2\n" +
                "inner join link_group_semester lgs2 on lgss2.id_link_group_semester = lgs2.id_link_group_semester\n" + "where \n" +
                "id_humanface = " + idHumanface + "\n" + "AND lgss1.id_esocourse2 IS NULL\n" + "AND lgs1.id_semester = " + idCurSem + "\n" +
                "AND lgss2.id_esocourse2 IS NOT NULL\n" + "AND lgs2.id_semester != " + idCurSem + "\n" +
                "AND ds1.subjectname = ds2.subjectname\n" +
                "GROUP BY lgss1.id_link_group_semester_subject, emp.id_employee, ds1.subjectname, lgss2.id_esocourse2, ec2.fullname,dg.groupname\n" +
                "ORDER BY ds1.subjectname,dg.groupname";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("idLGSS", LongType.INSTANCE)
                              .addScalar("idEmp", LongType.INSTANCE)
                              .addScalar("subjectname")
                              .addScalar("idEsoCourse2", LongType.INSTANCE)
                              .addScalar("fullname")
                              .addScalar("groupName")
                              .setResultTransformer(Transformers.aliasToBean(CourseHistoryESOModel.class));

        return (List<CourseHistoryESOModel>) getList(q);
    }

    public boolean updateRegisterNumber (Long idReg, Long idSem, String suffixForNumber) {
        String query = "SELECT * FROM updateRegisterNumber(" + idReg + ", " + idSem + ", '" + suffixForNumber + "')";
        return callFunction(query);
    }

    public boolean updateRegisterSrSrhAfterSign (Long idReg, String regUrl, String serialNumber, String thumbPrint, String FIO,
                                                 String statusSR, String statusSRH) {
        String query = "SELECT register_sign(" + idReg + ",'" + regUrl + "','" + serialNumber + "','" + thumbPrint + "', '" + FIO + "', " +
                       (statusSR == null ? "null" : ("'" + statusSR + "'")) + ", " +
                       (statusSRH == null ? "null" : ("'" + statusSRH + "'")) + ");";

        return callFunction(query);
    }
}
