package org.edec.student.journalOrder.manager;

import org.edec.dao.DAO;
import org.edec.student.journalOrder.model.JournalOrderModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import java.util.List;


public class EntityManagerJournalOrder extends DAO {
    public List<JournalOrderModel> getJournalOrder (Long idHum) {
        String query =
                "SELECT \n" + "\tOH.ordernumber AS orderNumber, DG.groupname, OT.name AS orderType, OH.dateoffinish AS dateSignOrder,\n" +
                "\tEXTRACT(YEAR FROM SY.dateofbegin)||'-'||EXTRACT(YEAR FROM SY.dateofend)||' ('||CASE WHEN SEM.season = 0 THEN 'осень' ELSE 'весна' END||')' AS semesterStr\n" +
                "FROM \n" + "\tstudentcard SC \n" + "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN semester SEM USING (id_semester)\n" + "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN link_order_student_status LOSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN link_order_section LOS USING (id_link_order_section)\n" +
                "\tINNER JOIN order_head OH USING (id_order_head)\n" + "\tINNER JOIN order_rule ORR USING (id_order_rule) \n" +
                "\tINNER JOIN order_type OT USING (id_order_type)\n" + "WHERE \n" +
                "\tSC.id_humanface = :idHum AND OH.id_order_status_type = 3\n" + "ORDER BY \n" + "\tOH.dateoffinish";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("orderNumber")
                              .addScalar("groupname")
                              .addScalar("orderType")
                              .addScalar("dateSignOrder")
                              .addScalar("semesterStr")
                              .setResultTransformer(Transformers.aliasToBean(JournalOrderModel.class));
        q.setLong("idHum", idHum);
        return (List<JournalOrderModel>) getList(q);
    }
}
