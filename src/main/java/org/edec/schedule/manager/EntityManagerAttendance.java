package org.edec.schedule.manager;

import org.edec.dao.DAO;
import org.edec.schedule.model.DicDayLesson;
import org.edec.schedule.model.DicTimeLesson;
import org.edec.schedule.model.dao.GroupSubjectLessonEso;
import org.edec.utility.component.model.GroupModel;
import org.edec.utility.component.model.SubjectModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class EntityManagerAttendance extends DAO {
    public List<GroupModel> getGroupsByInstAndFormOfStudy (Long idInst, Integer formOfStudy) {
        String query = "SELECT\n" + "\tDG.groupname, LGS.course, CUR.qualification\n" + "FROM\n" + "\tdic_group DG\n" +
                       "\tINNER JOIN curriculum CUR USING (id_curriculum)\n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_dic_group)\n" + "\tINNER JOIN semester SEM USING (id_semester)\n" +
                       "WHERE\n" + "\tSEM.id_institute = :idInst " + (formOfStudy == 3 ? "" : "AND SEM.formofstudy = " + formOfStudy) +
                       "AND SEM.is_current_sem = 1\n" + "ORDER BY\n" + "\tcourse, groupname";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("groupname")
                              .addScalar("course")
                              .addScalar("qualification")
                              .setResultTransformer(Transformers.aliasToBean(GroupModel.class));
        q.setLong("idInst", idInst);
        return (List<GroupModel>) getList(q);
    }

    public List<SubjectModel> getSubjectsByGroupname (String groupname) {
        String query = "SELECT\n" + "\tLGSS.id_link_group_semester_subject AS idLGSS, DS.subjectname\n" + "FROM\n" + "\tdic_group DG\n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_dic_group)\n" + "\tINNER JOIN semester SEM USING (id_semester)\n" +
                       "\tINNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN subject S USING (id_subject)\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" + "WHERE\n" +
                       "\tDG.groupname LIKE :groupname AND SEM.is_current_sem = 1\n" + "ORDER BY\n" + "\tDS.subjectname";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idLGSS", LongType.INSTANCE)
                              .addScalar("subjectname")
                              .setResultTransformer(Transformers.aliasToBean(SubjectModel.class));
        q.setString("groupname", groupname);
        return (List<SubjectModel>) getList(q);
    }

    public List<DicDayLesson> getDays () {
        String query = "SELECT\tid_dic_day_lesson AS idDicDayLesson, day_name AS name\n" + "FROM\tdic_day_lesson";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idDicDayLesson", LongType.INSTANCE)
                              .addScalar("name")
                              .setResultTransformer(Transformers.aliasToBean(DicDayLesson.class));
        return (List<DicDayLesson>) getList(q);
    }

    public List<DicTimeLesson> getTimes () {
        String query = "SELECT\tid_dic_time_lesson AS idDicTimeLesson,\n" + "\tname_time AS timeName\n" + "FROM\tdic_time_lesson";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idDicTimeLesson", LongType.INSTANCE)
                              .addScalar("timeName")
                              .setResultTransformer(Transformers.aliasToBean(DicTimeLesson.class));
        return (List<DicTimeLesson>) getList(q);
    }

    public List<GroupSubjectLessonEso> getModelLesson (String groupname) {
        String query = "SELECT\n" +
                       "\tLSCH.id_link_group_semester_subject AS idLGSS, LSCH.id_link_group_schedule_subject AS idLSCH, LSCH.week, LSCH.room, LSCH.teacher,\n" +
                       "\tDD.day_name AS dayName, DT.name_time AS timeName,  DS.subjectName, LSCH.lesson\n" + "FROM\n" +
                       "\tdic_group DG\n" + "\tINNER JOIN link_group_semester LGS USING (id_dic_group)\n" +
                       "\tINNER JOIN semester SEM USING (id_semester)\n" +
                       "\tINNER JOIN link_group_semester_subject LGSS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN subject S USING (id_subject)\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                       "\tINNER JOIN link_group_schedule_subject LSCH USING (id_link_group_semester_subject)\n" +
                       "\tINNER JOIN dic_day_lesson DD USING (id_dic_day_lesson)\n" +
                       "\tINNER JOIN dic_time_lesson DT USING (id_dic_time_lesson)\n" + "WHERE\n" +
                       "\tDG.groupname LIKE :groupname AND SEM.is_current_sem = 1\n" + "ORDER BY\n" +
                       "\tDD.id_dic_day_lesson, DT.id_dic_time_lesson, week";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idLGSS", LongType.INSTANCE)
                              .addScalar("idLSCH", LongType.INSTANCE)
                              .addScalar("week")
                              .addScalar("room")
                              .addScalar("teacher")
                              .addScalar("dayName")
                              .addScalar("timeName")
                              .addScalar("subjectName")
                              .addScalar("lesson")
                              .setResultTransformer(Transformers.aliasToBean(GroupSubjectLessonEso.class));
        q.setString("groupname", groupname);
        return (List<GroupSubjectLessonEso>) getList(q);
    }

    public boolean addAttend (Long idLGSS, Integer week, String room, String teacher, Long idDicDayLesson, Long idDicTimeLesson,
                              Boolean lesson) {
        String query = "INSERT INTO link_group_schedule_subject\n" +
                       "\t(id_link_group_semester_subject, week, room, teacher, lesson, id_dic_day_lesson, id_dic_time_lesson)\n" +
                       "\tSELECT :idLGSS, :week, :room, :teacher, :lesson, :day, :time\n" + "\tWHERE NOT EXISTS (\n" + "\tSELECT *\n" +
                       "\tFROM\n" + "\t\tlink_group_semester_subject LGSS\n" +
                       "\t\tLEFT JOIN link_group_schedule_subject LGSCH USING (id_link_group_semester_subject)\n" + "\tWHERE\n" +
                       "\t\tLGSS.id_link_group_semester = (SELECT id_link_group_semester FROM link_group_semester_subject WHERE id_link_group_semester_subject = :idLGSS2)\n" +
                       "\t\tAND week = :week2 AND id_dic_day_lesson = :day2 AND id_dic_time_lesson = :time2)";
        Query q = getSession().createSQLQuery(query)
                              .setLong("idLGSS", idLGSS)
                              .setLong("idLGSS2", idLGSS)
                              .setBoolean("lesson", lesson)
                              .setInteger("week", week)
                              .setInteger("week2", week)
                              .setString("room", room)
                              .setString("teacher", teacher)
                              .setLong("day", idDicDayLesson)
                              .setLong("day2", idDicDayLesson)
                              .setLong("time", idDicTimeLesson)
                              .setLong("time2", idDicTimeLesson);
        return executeUpdate(q);
    }

    public boolean removeAllScheduleByGroupname (String groupname) {
        String query = "DELETE FROM link_group_schedule_subject WHERE id_link_group_semester_subject IN \n" +
                       "(SELECT LGSS.id_link_group_semester_subject \n" + "FROM \tlink_group_semester_subject LGSS\n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN dic_group DG USING (id_dic_group)\n" + "\tINNER JOIN semester SEM USING (id_semester)\n" +
                       "WHERE\tDG.groupname LIKE :groupname AND SEM.is_current_sem = 1)";
        Query q = getSession().createSQLQuery(query).setString("groupname", groupname);
        return executeUpdate(q);
    }

    public boolean removeScheduleById (Long idLSCH) {
        String query = "DELETE FROM link_group_schedule_subject WHERE id_link_group_semester_subject = :idLSCH";
        Query q = getSession().createSQLQuery(query).setLong("idLSCH", idLSCH);
        return executeUpdate(q);
    }
}
