package org.edec.schedule.manager;

import org.edec.dao.DAO;
import org.edec.schedule.model.synch.EfficiencyModel;
import org.edec.schedule.model.synch.dao.GroupSubjectEfficiencyEso;
import org.edec.schedule.model.synch.dao.GroupStudentEso;
import org.edec.teacher.model.EsoCourseModel;
import org.edec.utility.converter.DateConverter;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import java.util.Date;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class EntityManagerSynch extends DAO {
    public List<GroupStudentEso> getGroupStudentWithNullEsoID (Long idSem) {
        String query = "SELECT\n" + "\tHF.family||' '||HF.name||' '||HF.patronymic AS fio, SC.id_studentcard AS idStudentCard,\n" +
                       "\tSC.ldap_login AS ldapLogin, SC.other_esoid AS idStudentEok, DG.groupname\n" + "FROM\n" + "\thumanface HF\n" +
                       "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                       "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN semester SEM USING (id_semester)\n" + "\tINNER JOIN dic_group DG USING (id_dic_group)\n" + "WHERE\n" +
                       "\tSEM.id_semester = :idSem AND (SC.other_esoid IS NULL OR SC.other_esoid = 0)\n" +
                       "\tAND SSS.is_deducted = 0 AND SSS.is_academicleave = 0 AND SSS.is_transfer_student = 0\n" + "ORDER BY\n" +
                       "\tDG.groupname, fio\n";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("groupname")
                              .addScalar("ldapLogin")
                              .addScalar("idStudentCard", LongType.INSTANCE)
                              .addScalar("idStudentEok", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(GroupStudentEso.class));
        q.setLong("idSem", idSem);
        return (List<GroupStudentEso>) getList(q);
    }

    public List<GroupStudentEso> getGroupStudent (Long idSem) {
        String query = "SELECT\n" + "\tHF.family||' '||HF.name||' '||HF.patronymic AS fio, SC.id_studentcard AS idStudentCard,\n" +
                       "\tSC.ldap_login AS ldapLogin, SC.other_esoid AS idStudentEok, DG.groupname\n" + "FROM\n" + "\thumanface HF\n" +
                       "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                       "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN semester SEM USING (id_semester)\n" + "\tINNER JOIN dic_group DG USING (id_dic_group)\n" + "WHERE\n" +
                       "\tSSS.is_deducted = 0 AND SSS.is_academicleave = 0\n" +
                       (idSem == null ? "" : "\tAND SEM.id_semester = " + idSem + "\n") + "ORDER BY\n" + "\tDG.groupname, fio\n";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("groupname")
                              .addScalar("ldapLogin")
                              .addScalar("idStudentCard", LongType.INSTANCE)
                              .addScalar("idStudentEok", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(GroupStudentEso.class));
        return (List<GroupStudentEso>) getList(q);
    }

    public List<EsoCourseModel> getEsoCourses () {
        String query = "SELECT \tid_esocourse2 AS idEsoCourse, id_category AS idCategory, fullname, shortname\n" + "FROM\tesocourse2\n" +
                       "ORDER BY id_esocourse2";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idEsoCourse", LongType.INSTANCE)
                              .addScalar("idCategory", IntegerType.INSTANCE)
                              .addScalar("fullname")
                              .addScalar("shortname")
                              .setResultTransformer(Transformers.aliasToBean(EsoCourseModel.class));
        return (List<EsoCourseModel>) getList(q);
    }

    public List<EfficiencyModel> getEfficiencyModel (Long idSem, Integer firstWeek, Date firstDate, Date secondDate) {
        String query = "SELECT\n" +
                       "\tSC.other_esoid AS idEsoStudent, LGSS.id_esocourse2 AS idEsoCourse, SR.id_sessionrating AS idSR, SSS.id_student_semester_status AS idSSS, \n" +
                       "\tCOALESCE (myTab.visitCount, 0) AS visitCount, \n" + "\tCOALESCE(myTab.countLesson, (\n" +
                       "\t\tSELECT COUNT(*) FROM generate_series(TO_DATE('" + DateConverter.convertDateToString(firstDate) +
                       "', 'dd.MM.yyyy'), " + "TO_DATE('" + DateConverter.convertDateToString(secondDate) +
                       "', 'dd.MM.yyyy'), '1 day') AS GS " +
                       "\t\tINNER JOIN link_group_schedule_subject LSCH ON LSCH.id_link_group_semester_subject = LGSS.id_link_group_semester_subject AND CAST(DATE_PART('ISODOW', GS) AS INTEGER) = LSCH.id_dic_day_lesson AND (\n" +
                       "\t\t\t\t\t\t\t(" + firstWeek + " % 2 = 1 AND (\n" +
                       "\t\t\t\t\t\t\t\t(LSCH.week = 1 AND CAST(DATE_PART('WEEK', GS) AS INTEGER) % 2 = 1)\n" +
                       "\t\t\t\t\t\t\t\tOR (LSCH.week = 2 AND CAST(DATE_PART('WEEK', GS) AS INTEGER) % 2 = 0)))\n" + "\t\t\t\t\t\t\tOR (" +
                       firstWeek + " % 2 = 0 AND (\n" +
                       "\t\t\t\t\t\t\t\t(LSCH.week = 1 AND CAST(DATE_PART('WEEK', GS) AS INTEGER) % 2 = 0)\n" +
                       "\t\t\t\t\t\t\t\tOR (LSCH.week = 2 AND CAST(DATE_PART ('WEEK', GS) AS INTEGER) % 2 = 1))))\n" +
                       "\t), 0) AS lessonCount\n" + "FROM\n" + "\tstudentcard SC\n" +
                       "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN semester SEM USING (id_semester)\n" +
                       "\tINNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN sessionrating SR ON SSS.id_student_semester_status  = SR.id_student_semester_status  AND LGSS.id_subject = SR.id_subject\n" +
                       "\tLEFT JOIN (SELECT AT.id_student_semester_status AS idSSS, LGSS.id_link_group_semester_subject AS idLGSS, COUNT(AT.*)+COUNT(LSCH.*) AS countLesson, COUNT(CASE WHEN AT.attend > 0 THEN 1 END) AS visitCount\n" +
                       "\t\tFROM generate_series(TO_DATE('" + DateConverter.convertDateToString(firstDate) + "', 'dd.MM.yyyy'), " +
                       "TO_DATE('" + DateConverter.convertDateToString(secondDate) + "', 'dd.MM.yyyy'), '1 day') AS GS\n" +
                       "\t\t\tLEFT JOIN attendance AT ON AT.visitdate = GS\n" +
                       "\t\t\tLEFT JOIN link_group_semester_subject LGSS ON AT.id_link_group_semester_subject = LGSS.id_link_group_semester_subject\n" +
                       "\t\t\tLEFT JOIN link_group_schedule_subject LSCH ON AT.id_attendance IS NULL AND LSCH.id_link_group_semester_subject = LGSS.id_link_group_semester_subject AND CAST(DATE_PART('ISODOW', GS) AS INTEGER) = LSCH.id_dic_day_lesson AND (\n" +
                       "\t\t\t\t\t\t\t(" + firstWeek + " % 2 = 1 AND (\n" +
                       "\t\t\t\t\t\t\t\t(LSCH.week = 1 AND CAST(DATE_PART('WEEK', GS) AS INTEGER) % 2 = 1)\n" +
                       "\t\t\t\t\t\t\t\tOR (LSCH.week = 2 AND CAST(DATE_PART('WEEK', GS) AS INTEGER) % 2 = 0)))\n" + "\t\t\t\t\t\t\tOR (" +
                       firstWeek + " % 2 = 0 AND (\n" +
                       "\t\t\t\t\t\t\t\t(LSCH.week = 1 AND CAST(DATE_PART('WEEK', GS) AS INTEGER) % 2 = 0)\n" +
                       "\t\t\t\t\t\t\t\tOR (LSCH.week = 2 AND CAST(DATE_PART ('WEEK', GS) AS INTEGER) % 2 = 1))))\n" +
                       "\t\tGROUP BY\tidSSS, idLGSS) myTab ON SSS.id_student_semester_status = myTab.idSSS AND LGSS.id_link_group_semester_subject = myTab.idLGSS\n" +
                       "WHERE\n" + "\tLGS.id_semester = " + idSem + " AND SSS.is_deducted = 0 AND SSS.is_academicleave = 0\n" +
                       "GROUP BY\n" +
                       "\tidEsoStudent,idEsoCourse, idSR, SSS.id_student_semester_status, LGSS.id_link_group_semester_subject, myTab.countLesson, myTab.visitCount";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idEsoStudent", LongType.INSTANCE)
                              .addScalar("idEsoCourse", LongType.INSTANCE)
                              .addScalar("idSR", LongType.INSTANCE)
                              .addScalar("lessonCount", LongType.INSTANCE)
                              .addScalar("visitCount", LongType.INSTANCE)
                              .addScalar("idSSS", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(EfficiencyModel.class));
        return (List<EfficiencyModel>) getList(q);
    }

    public List<GroupSubjectEfficiencyEso> getEfficiencyModelForSynchPerformance () {
        String query =
                "SELECT\tDISTINCT ON (SE.id_sessionrating_efficiency) SC.other_esoid AS idEsoStudent, LGSS.id_esocourse2 AS idEsoCourse, \n" +
                "\tSE.id_sessionrating AS idSR, SE.id_sessionrating_efficiency AS idSRefficiency, DG.groupname, LGSS.id_subject AS idSubject,\n" +
                "\tDS.subjectname, HF.family||' '||HF.name||' '||HF.patronymic AS fio, SR.e_grade_json AS jsonGrades\n" +
                "FROM\tsessionrating_efficiency SE\n" + "\tINNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN studentcard SC USING (id_studentcard)\n" + "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN link_group_semester_subject LGSS ON LGSS.id_link_group_semester = LGS.id_link_group_semester AND LGSS.id_subject = SR.id_subject\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" + "\tINNER JOIN subject S ON LGSS.id_subject = S.id_subject\n" +
                "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" + "WHERE\tSE.performance = -1\n" +
                "ORDER BY SE.id_sessionrating_efficiency";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idEsoStudent", LongType.INSTANCE)
                              .addScalar("idEsoCourse", LongType.INSTANCE)
                              .addScalar("idSR", LongType.INSTANCE)
                              .addScalar("idSRefficiency", LongType.INSTANCE)
                              .addScalar("groupname")
                              .addScalar("idSubject", LongType.INSTANCE)
                              .addScalar("subjectname")
                              .addScalar("fio")
                              .addScalar("jsonGrades")
                              .setResultTransformer(Transformers.aliasToBean(GroupSubjectEfficiencyEso.class));
        return (List<GroupSubjectEfficiencyEso>) getList(q);
    }

    public boolean updateLdapLoginByStudent (String loginLdap, Long idStudentCard) {
        Query q = getSession().createSQLQuery("UPDATE studentcard SET ldap_login = :loginLdap WHERE id_studentcard = :idStudentCard");
        q.setString("loginLdap", loginLdap).setLong("idStudentCard", idStudentCard);
        return executeUpdate(q);
    }

    public boolean updateEokId (Long idStudentEok, Long idStudentCard) {
        Query q = getSession().createSQLQuery("UPDATE studentcard SET other_esoid = :idStudentEok WHERE id_studentcard = :idStudentCard");
        q.setLong("idStudentCard", idStudentCard).setLong("idStudentEok", idStudentEok);
        return executeUpdate(q);
    }

    public boolean updateEsoCourse (Long idCourse, Long idCategory, String fullname, String shortname) {
        Query q = getSession().createSQLQuery("UPDATE esocourse2 SET id_category = :idCategory, fullname = :fullname," +
                                              "shortname = :shortname WHERE id_esocourse2 = :idEsoCourse");
        q.setLong("idCategory", idCategory)
         .setString("fullname", fullname)
         .setString("shortname", shortname)
         .setLong("idEsoCourse", idCourse);
        return executeUpdate(q);
    }

    public boolean updateEokPerformance (EfficiencyModel efficiency) {
        String queryUpdateSE = "UPDATE sessionrating_efficiency SET performance = :performance WHERE id_sessionrating_efficiency = :idSE";
        Query qSE = getSession().createSQLQuery(queryUpdateSE);
        qSE.setInteger("performance", efficiency.getPerformance()).setLong("idSE", efficiency.getIdSRefficiency());
        executeUpdate(qSE);
        String queryUpdateSR = "UPDATE sessionrating SET esogradecurrent = :performance, e_grade_json = :gradesJson WHERE id_sessionrating = :idSR";
        Query qSR = getSession().createSQLQuery(queryUpdateSR);
        qSR.setInteger("performance", efficiency.getPerformance())
           .setLong("idSR", efficiency.getIdSR())
           .setParameter("gradesJson", efficiency.getJsonGrades() == null ? null : efficiency.getJsonGrades(), StringType.INSTANCE);
        executeUpdate(qSR);
        return true;
    }

    public boolean insertEsoCourse (Long idCourse, Long idCatergory, String fullname, String shortname) {
        Query q = getSession().createSQLQuery("INSERT INTO esocourse2 (id_esocourse2, id_category, fullname, shortname) VALUES " +
                                              "(:idCourse,:idCategory, :fullname, :shortname)");
        q.setLong("idCourse", idCourse)
         .setLong("idCategory", idCatergory)
         .setString("fullname", fullname)
         .setString("shortname", shortname);
        return executeUpdate(q);
    }

    public boolean deleteEsoCourse (Long idEsoCourse) {
        Query q = getSession().createSQLQuery("DELETE FROM esocourse2 WHERE id_esocourse2 = :idEsoCourse");
        q.setLong("idEsoCourse", idEsoCourse);
        return executeUpdate(q);
    }

    public boolean synchEfficiencySR (EfficiencyModel efficiency, Date today) {
        String query = "SELECT * FROM synch_sr_efficiency(" + efficiency.getLessonCount() + ", " + efficiency.getVisitCount() + ", " +
                       efficiency.getIdSR() + ", " + (efficiency.getPerformance() == null ? -1 : efficiency.getPerformance()) + ", " +
                       "TO_DATE('" + DateConverter.convertDateToString(today) + "', 'dd.MM.yyyy'))";
        return callFunction(query);
    }
}