package org.edec.newOrder.manager;

import org.edec.dao.DAO;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateRuleModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by antonskripacev on 08.01.17.
 */
public class CreateOrderManagerESO extends DAO {
    public List<OrderCreateRuleModel> getListOrderRule (Long idInst) {
        String query = "SELECT\n" + "\t id_order_rule AS id,\n" + "\t name AS name,\n" + "\t is_automatic AS isAutomatic,\n" +
                       "\t id_order_type AS idOrderType\n" + "FROM order_rule WHERE is_displayed = TRUE AND id_institute = :idInst";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("idOrderType", LongType.INSTANCE)
                              .addScalar("name", StringType.INSTANCE)
                              .addScalar("isAutomatic", BooleanType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(OrderCreateRuleModel.class));

        q.setLong("idInst", idInst);
        return (List<OrderCreateRuleModel>) getList(q);
    }

    public Long createEmptyOrder (Long idOrderRule, Date dateOfBegin, Long idSemester, Long idHumanface, String description) {
        String strDate = new SimpleDateFormat("yyyy.MM.dd").format(dateOfBegin);
        String query =
                "insert into order_head (id_order_rule, id_order_status_type, dateofbegin, semester, id_humanface, descriptionspec )\n" +
                "values  \t        (" + idOrderRule + ", 1, '" + strDate + "', " + idSemester + ", " + idHumanface + ", '" + description +
                "')\n" + "returning id_order_head";

        Query q = getSession().createSQLQuery(query);
        List<BigInteger> list = (List<BigInteger>) getList(q);
        return list.size() == 0 ? null : list.get(0).longValue();
    }

    public Long createLinkOrderSection (Long idOrderHead, Long idOrderSection, String foundation, Date firstDate, Date secondDate) {
        String strFirstDate = (firstDate == null ? "" : new SimpleDateFormat("yyyy.MM.dd").format(firstDate));
        String strSecondDate = (secondDate == null ? "" : new SimpleDateFormat("yyyy.MM.dd").format(secondDate));
        String query = "insert into link_order_section (id_order_head, id_order_section, first_date, second_date, foundation)\n" +
                       "values  \t        (" + idOrderHead + ", " + idOrderSection + ", " + "" +
                       (strFirstDate.equals("") ? "null" : ("'" + strFirstDate + "'")) + ", " + "" +
                       (strSecondDate.equals("") ? "null" : ("'" + strSecondDate + "'")) + ", " + "'" + foundation +
                       "') returning id_link_order_section";
        Query q = getSession().createSQLQuery(query);
        List<BigInteger> list = (List<BigInteger>) getList(q);
        return list.size() == 0 ? null : list.get(0).longValue();
    }

    public Long createLinkOrderStudentStatus (Long idLinkOrderSection, Long idSSS, Date firstDate, Date secondDate, String groupname,
                                              String additional) {
        String strFirstDate = (firstDate == null ? "" : new SimpleDateFormat("yyyy.MM.dd").format(firstDate));
        String strSecondDate = (secondDate == null ? "" : new SimpleDateFormat("yyyy.MM.dd").format(secondDate));
        String query =
                "insert into link_order_student_status (id_link_order_section, id_student_semester_status, first_date, second_date, groupname,  additional)\n" +
                "values  \t        " + "(" + idLinkOrderSection + ", " + "" + idSSS + ", " + "" +
                (strFirstDate.equals("") ? "null" : ("'" + strFirstDate + "'")) + ", " + "" +
                (strSecondDate.equals("") ? "null" : ("'" + strSecondDate + "'")) + ", " + "'" + groupname + "', " + "'" + additional +
                "') returning id_link_order_student_status";
        Query q = getSession().createSQLQuery(query);

        List<BigInteger> list = (List<BigInteger>) getList(q);
        return list.size() == 0 ? null : list.get(0).longValue();
    }

    public List<OrderCreateOrderSectionModel> getListOrderSection (Long idRule) {
        String query = "select\n" + "\t id_order_section as id,\n" + "\t name as name,\n" + "\t foundation as foundation\n" +
                       "from order_section where id_order_rule = " + idRule;

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("name", StringType.INSTANCE)
                              .addScalar("foundation", StringType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(OrderCreateOrderSectionModel.class));
        return (List<OrderCreateOrderSectionModel>) getList(q);
    }

    public boolean setUrlForOrder (Long idOrder, String url) {
        String query = "UPDATE order_head SET order_url = :orderUrl WHERE id_order_head = :idOrder";
        Query q = getSession().createSQLQuery(query);
        q.setString("orderUrl", url).setLong("idOrder", idOrder);
        return executeUpdate(q);
    }

    public Long getCurrentSemester (long idInstitute, int formOfStudy) {
        String query =
                "Select id_semester as idSemester from semester where id_institute = " + idInstitute + " and formofstudy = " + formOfStudy +
                " and is_current_sem = 1 limit 1";
        Query q = getSession().createSQLQuery(query);
        List<BigInteger> list = (List<BigInteger>) getList(q);
        return list.size() == 0 ? null : list.get(0).longValue();
    }

    public Long getPrevSemester (long idSemester) {
        String query =
                "select id_semester from semester where " + " id_institute = (select id_institute from semester where id_semester = " +
                idSemester + ")" + " and formofstudy = (select formofstudy from semester where id_semester = " + idSemester + ")" +
                " and id_semester < " + idSemester + " order by id_semester desc limit 1";

        Query q = getSession().createSQLQuery(query);
        List<BigInteger> list = (List<BigInteger>) getList(q);
        return list.size() == 0 ? null : list.get(0).longValue();
    }

    public Long getNextSemester (long idSemester) {
        String query =
                "select id_semester from semester where " + " id_institute = (select id_institute from semester where id_semester = " +
                idSemester + ")" + " and formofstudy = (select formofstudy from semester where id_semester = " + idSemester + ")" +
                " and id_semester > " + idSemester + " order by id_semester limit 1";

        Query q = getSession().createSQLQuery(query);
        List<BigInteger> list = (List<BigInteger>) getList(q);
        return list.size() == 0 ? null : list.get(0).longValue();
    }

    public void updateOrderDesc (String desc, long idOrder) {
        String query = "update order_head set descriptionspec = '" + desc + "' where id_order_head = " + idOrder;
        Query q = getSession().createSQLQuery(query);
        executeUpdate(q);
    }

    public List<OrderCreateStudentModel> getStudentsForSearch (long idSemester, String fio) {
        String query = "SELECT \n" + "\tid_student_semester_status AS id, groupname,\n" + "\tfamily||' '||name||' '||patronymic AS fio,\n" +
                       "\tSSS.is_government_financed AS governmentFinanced\n" + "FROM\n" + "\tstudent_semester_status SSS\n" +
                       "\tINNER JOIN studentcard SC using(id_studentcard)\n" + "\tINNER JOIN humanface HF using(id_humanface)\n" +
                       "\tINNER JOIN link_group_semester LGS USING(id_link_group_semester)\n" +
                       "\tINNER JOIN dic_group DG using(id_dic_group)\n" + "\tWHERE id_semester = " + idSemester +
                       " AND family||' '||name||' '||patronymic ILIKE '%" + fio + "%'";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("groupname")
                              .addScalar("governmentFinanced", BooleanType.INSTANCE)
                              .addScalar("fio")
                              .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));

        return (List<OrderCreateStudentModel>) getList(q);
    }
}
