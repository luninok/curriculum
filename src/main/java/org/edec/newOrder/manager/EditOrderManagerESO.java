package org.edec.newOrder.manager;

import org.edec.dao.DAO;
import org.edec.newOrder.model.dao.LinkOrderSectionEditESO;
import org.edec.newOrder.model.dao.OrderModelESO;
import org.edec.newOrder.model.EmployeeOrderModel;
import org.edec.newOrder.model.dao.SearchGroupModelESO;
import org.edec.newOrder.model.dao.SearchStudentModelESO;
import org.edec.studentPassport.model.dao.RatingEsoModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import java.util.List;

public class EditOrderManagerESO extends DAO {
    public List<OrderModelESO> getListOrderModelESO (long idOrder) {
        String query = "SELECT\n" + "\tHF.family||' '||HF.name||' '||HF.patronymic AS fio,\n" + "\tSC.recordbook AS recordbook,\n" +
                       "\tDG.groupname AS groupname,\n" + "\tOS.name AS sectionname,\n" + "\tOS.id_order_section AS idOS,\n" +
                       "\tLOS.foundation AS foundationLos,\n" + "\tOS.foundation AS foundation,\n" +
                       "\tLOS.first_date AS firstDateSection,\n" + "\tLOS.second_date AS secondDateSection,\n" +
                       "\tLOSS.first_date AS firstDate,\n" + "\tLOSS.second_date AS secondDate,\n" + "\tLOSS.third_date AS thirdDate,\n" +
                       "\tLOS.id_link_order_section AS idSection,\n" + "\tLOSS.id_link_order_student_status AS idStudent,\n" +
                       "\tLOSS.additional AS additionalInfo\n" + "FROM \n" + "\torder_head OH\n" +
                       "\tINNER JOIN link_order_section los USING(id_order_head)\n" +
                       "\tINNER JOIN link_order_student_status loss USING(id_link_order_section)\n" +
                       "\tINNER JOIN order_section os USING(id_order_section)\n" +
                       "\tINNER JOIN student_semester_status sss USING(id_student_semester_status)\n" +
                       "\tINNER JOIN link_group_semester lgs USING(id_link_group_semester)\n" +
                       "\tINNER JOIN dic_group dg USING(id_dic_group)\n" + "\tINNER JOIN studentcard sc USING(id_studentcard)\n" +
                       "\tINNER JOIN humanface hf ON sc.id_humanface = hf.id_humanface \n" + "WHERE OH.id_order_head = :idOrder\n" +
                       "\tORDER BY sectionname, groupname, fio";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("firstDate")
                              .addScalar("secondDate")
                              .addScalar("thirdDate")
                              .addScalar("firstDateSection")
                              .addScalar("secondDateSection")
                              .addScalar("fio")
                              .addScalar("recordbook")
                              .addScalar("groupname")
                              .addScalar("sectionname")
                              .addScalar("foundation")
                              .addScalar("foundationLos")
                              .addScalar("idStudent", LongType.INSTANCE)
                              .addScalar("idSection", LongType.INSTANCE)
                              .addScalar("idOS", LongType.INSTANCE)
                              .addScalar("additionalInfo", StringType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(OrderModelESO.class));
        q.setLong("idOrder", idOrder);
        return (List<OrderModelESO>) getList(q);
    }

    public void saveFoundationStudent (Long id, String foundation) {
        String query = "update link_order_student_status set additional = '" + foundation + "' where id_link_order_student_status = " + id;
        executeUpdate(getSession().createSQLQuery(query));
    }

    public void saveFoundation (Long id, String foundation) {
        String query = "update link_order_section set foundation = '" + foundation + "' where id_link_order_section = " + id;
        executeUpdate(getSession().createSQLQuery(query));
    }

    public void removeStudentFromOrder (Long idLoss) {
        String query = "DELETE FROM link_order_student_status WHERE id_link_order_student_status = :idLoss";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idLoss", idLoss);
        executeUpdate(q);
    }

    public void updateLossParam (Long idLoss, String paramQuery) {
        String query = "update link_order_student_status set " + paramQuery + " where id_link_order_student_status = " + idLoss;
        executeUpdate(getSession().createSQLQuery(query));
    }

    public List<EmployeeOrderModel> getEmployeeForEnsemble (Long idOrder) {
        String query = "SELECT\n" + "\tHF.family||' '||HF.name||' '||HF.patronymic AS fio, LRE.subquery,\n" +
                       "\tHF.id_humanface AS idHum, LRE.sign, LRE.actionrule, HF.email\n" + "FROM\n" + "\thumanface HF\n" +
                       "\tINNER JOIN employee EMP USING (id_humanface)\n" + "\tINNER JOIN link_rule_employee LRE USING (id_employee)\n" +
                       "\tINNER JOIN order_rule ORR ON LRE.id_rule = ORR.id_order_rule\n" +
                       "\tINNER JOIN order_head OH USING (id_order_rule)\n" + "WHERE\n" +
                       "\tOH.id_order_head = :idOrder AND LRE.actionrule IN (1,2)\n" + "ORDER BY\n" + "\tactionrule DESC, pos";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("idHum", LongType.INSTANCE)
                              .addScalar("sign")
                              .addScalar("actionrule")
                              .addScalar("email")
                              .addScalar("subquery")
                              .setResultTransformer(Transformers.aliasToBean(EmployeeOrderModel.class));
        q.setParameter("idOrder", idOrder);
        return (List<EmployeeOrderModel>) getList(q);
    }

    public List<RatingEsoModel> getMarksForStudentInOrder (Long idLoss) {
        String query = "SELECT \n" +
                       "\tSR.is_exam = 1 AS exam, SR.is_pass = 1 AS pass, SR.is_courseproject = 1 AS cp, SR.is_coursework = 1 AS cw, SR.is_practic = 1 AS practic,\n" +
                       "\tSR.examrating, SR.passrating, SR.courseprojectrating AS cprating, SR.courseworkrating AS cwrating, SR.practicrating,\n" +
                       "\tDS.subjectname,\n" + "\tCASE\n" +
                       "\t\tWHEN SEM.season = 0 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'-'||EXTRACT(YEAR FROM SY.dateofend)||' (осень)'\n" +
                       "\t\tWHEN SEM.season = 1 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'-'||EXTRACT(YEAR FROM SY.dateofend)||' (весна)'\n" +
                       "\tEND AS semester\n" + "FROM\n" + "\tstudentcard SC\n" +
                       "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN semester SEM USING (id_semester)\n" + "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                       "\tINNER JOIN sessionrating SR USING (id_student_semester_status)\n" + "\tINNER JOIN subject USING (id_subject)\n" +
                       "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" + "WHERE\n" + "\tid_dic_group = \n" +
                       "\t\t(SELECT id_dic_group FROM link_group_semester \n" +
                       "\t\t INNER JOIN student_semester_status sss USING(id_link_group_semester)\n" +
                       "\t\t INNER JOIN link_order_student_status loss USING(id_student_semester_status)\n" +
                       "\t\t WHERE id_link_order_student_status = :idLoss)\n" + "\tAND id_studentcard = \n" +
                       "\t\t(SELECT id_studentcard FROM student_semester_status sss\n" +
                       "\t\t INNER JOIN link_order_student_status loss USING(id_student_semester_status)\n" +
                       "\t\t WHERE id_link_order_student_status = :idLoss) ORDER BY id_semester, subjectname DESC\n";

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
                              .addScalar("subjectname")
                              .addScalar("semester")
                              .setResultTransformer(Transformers.aliasToBean(RatingEsoModel.class));
        q.setParameter("idLoss", idLoss);
        return (List<RatingEsoModel>) getList(q);
    }

    public List<LinkOrderSectionEditESO> getLinkOrderSections (Long idOrder) {
        String query =
                "SELECT \n" + "    OS.name, \n" + "    LOS.id_link_order_section AS idLOS,\n" + "    LOS.id_order_section AS idOS,\n" +
                "    LOS.first_date AS firstDate,\n" + "    LOS.second_date AS secondDate\n" + "FROM link_order_section los\n" +
                "INNER JOIN order_section OS USING(id_order_section)\n" + "WHERE \n" + "\tid_order_head = :idOrder";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("name", StringType.INSTANCE)
                              .addScalar("idLOS", LongType.INSTANCE)
                              .addScalar("idOS", LongType.INSTANCE)
                              .setParameter("idOrder", idOrder)
                              .setResultTransformer(Transformers.aliasToBean(LinkOrderSectionEditESO.class));
        return (List<LinkOrderSectionEditESO>) getList(q);
    }

    public List<SearchGroupModelESO> getGroupsForOrderSearch (long idOrder) {
        // if in order there are no students - it will be broken

        String query = "SELECT dg.groupname AS name, dg.id_dic_group AS id FROM link_group_semester lgs\n" +
                       "INNER JOIN dic_group dg USING(id_dic_group)\n" +
                       "WHERE lgs.id_semester = (SELECT DISTINCT(id_semester) FROM link_order_student_status loss\n" +
                       "INNER JOIN link_order_section los USING(id_link_order_section)\n" +
                       "INNER JOIN student_semester_status sss USING(id_student_semester_status)\n" +
                       "INNER JOIN link_group_semester lgs USING(id_link_group_semester)\n" + "WHERE id_order_head = :idOrder\n" +
                       "ORDER BY id_semester DESC LIMIT 1)";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("name", StringType.INSTANCE)
                              .addScalar("id", LongType.INSTANCE)
                              .setParameter("idOrder", idOrder)
                              .setResultTransformer(Transformers.aliasToBean(SearchGroupModelESO.class));
        return (List<SearchGroupModelESO>) getList(q);
    }

    public List<SearchStudentModelESO> getStudentsByFilterNotInOrder (String family, String name, String patronymic, String groupname,
                                                                      long idOrder) {
        String query = "SELECT \n" + "\thf.name, \n" + "    hf.family AS surname,\n" + "    hf.patronymic,\n" + "    dg.groupname,\n" +
                       "    sss.id_student_semester_status AS idSSS\n" + "FROM student_semester_status sss\n" +
                       "INNER JOIN studentcard sc USING(id_studentcard)\n" + "INNER JOIN humanface hf USING(id_humanface)\n" +
                       "INNER JOIN link_group_semester lgs USING(id_link_group_semester)\n" +
                       "INNER JOIN dic_group dg USING(id_dic_group)\n" + "WHERE \n" +
                       "\tlgs.id_semester = (SELECT DISTINCT(id_semester) FROM link_order_student_status loss\n" +
                       "INNER JOIN link_order_section los USING(id_link_order_section)\n" +
                       "INNER JOIN student_semester_status sss USING(id_student_semester_status)\n" +
                       "INNER JOIN link_group_semester lgs USING(id_link_group_semester)\n" + "WHERE id_order_head = :idOrder\n" +
                       "ORDER BY id_semester DESC LIMIT 1)\n" + "    AND hf.name ILIKE :name\n" + "    AND hf.family ILIKE :family \n" +
                       "    AND hf.patronymic ILIKE :patronymic\n" + "    AND dg.groupname ILIKE :groupname\n" +
                       "    AND sss.id_student_semester_status NOT IN (\n" +
                       "    SELECT id_student_semester_status FROM link_order_student_status\n" +
                       "    INNER JOIN link_order_section los USING(id_link_order_section)\n" + "    WHERE id_order_head = :idOrder)";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("name", StringType.INSTANCE)
                              .addScalar("surname", StringType.INSTANCE)
                              .addScalar("patronymic", StringType.INSTANCE)
                              .addScalar("groupname", StringType.INSTANCE)
                              .addScalar("idSSS", LongType.INSTANCE)
                              .setParameter("family", "%" + family + "%", StringType.INSTANCE)
                              .setParameter("name", "%" + name + "%", StringType.INSTANCE)
                              .setParameter("patronymic", "%" + patronymic + "%", StringType.INSTANCE)
                              .setParameter("groupname", "%" + groupname + "%", StringType.INSTANCE)
                              .setParameter("idOrder", idOrder, LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(SearchStudentModelESO.class));
        return (List<SearchStudentModelESO>) getList(q);
    }
}
