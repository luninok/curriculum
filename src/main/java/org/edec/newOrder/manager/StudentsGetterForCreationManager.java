package org.edec.newOrder.manager;

import org.edec.dao.DAO;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.utility.converter.DateConverter;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class StudentsGetterForCreationManager extends DAO {
    public List<OrderCreateStudentModel> getStudentForProlongationAfterSetElimination (Long idSemester, boolean governmentFinanced) {
        String query = "select\n" + "\tsss.id_student_semester_status as id,\n" + "\tdg.groupname as groupname,\n" +
                       "\tsss.is_government_financed as governmentFinanced\n" + "from \n" + "\tstudent_semester_status sss\n" +
                       "\tinner join studentcard sc using(id_studentcard)\n" +
                       "\tinner join link_group_semester lgs using(id_link_group_semester)\n" +
                       "\tinner join dic_group dg using(id_dic_group)\n" + "\tinner join curriculum cr using(id_curriculum)\n" + "\t\n" +
                       "where\n" + "\tid_semester = " + idSemester + "\n" + "\tand sessionresult2 < 0\n" + "\tand is_deducted = 0\n" +
                       "\tand is_academicleave = 0\n" + "\tand is_transfered = 0\n" + "\tand \n" +
                       "\t\t(select is_deducted from student_semester_status\n" +
                       "\t\t inner join link_group_semester using(id_link_group_semester)\t\t \n" +
                       "\t\t inner join semester using(id_semester)\t\t \n" + "\t\t where id_studentcard = sss.id_studentcard \n" +
                       "\t\t and id_dic_group = lgs.id_dic_group and is_current_sem = 1\n" + "\t\t ) = 0\n" + "\tand \n" +
                       "\t\t(select is_academicleave from student_semester_status\n" +
                       "\t\t inner join link_group_semester using(id_link_group_semester)\t\t \n" +
                       "\t\t inner join semester using(id_semester)\t\t \n" +
                       "\t\t where id_studentcard = sss.id_studentcard and id_dic_group = lgs.id_dic_group \n" +
                       "\t\t and is_current_sem = 1\n" + "\t\t ) = 0\n" + "\tand \n" +
                       "\t\t(select loss2.first_date from link_order_student_status loss2 \n" +
                       "\t\t inner join link_order_section los using(id_link_order_section)\n" +
                       "\t\t inner join order_head oh using(id_order_head)\n" + "\t\t where id_order_rule in (32,33) and attr1 = '" +
                       idSemester + "' \n" + "\t\t and loss2.id_student_semester_status = sss.id_student_semester_status\n" +
                       "\t\t) < now()\n" + "\tand \n" + "\t\t(select loss2.first_date from link_order_student_status loss2 \n" +
                       "\t\t inner join link_order_section los using(id_link_order_section)\n" +
                       "\t\t inner join order_head oh using(id_order_head)\n" + "\t\t where id_order_rule = 34 and attr1 = '" + idSemester +
                       "'\n" + "\t\t and loss2.id_student_semester_status = sss.id_student_semester_status\n" + "\t\t) is null\n" +
                       "\tand sc.id_current_dic_group = lgs.id_dic_group\n" + "\tand sss.is_government_financed = " +
                       (governmentFinanced ? 1 : 0) + "\n";
        //"\tand groupname in (" + idsGroup + ")";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("groupname", StringType.INSTANCE)
                              .addScalar("governmentFinanced", BooleanType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));
        return (List<OrderCreateStudentModel>) getList(q);
    }

    public List<OrderCreateStudentModel> getStudentForCancelAcademicalScholarshipInSession (Long idSemester, Long prevSem,
                                                                                            Date dateMarksFrom, Date dateMarksTo) {
        String firstDateStr = DateConverter.convertDateToStringByFormat(dateMarksFrom, "yyyy-MM-dd");
        String secondDateStr = DateConverter.convertDateToStringByFormat(dateMarksTo, "yyyy-MM-dd");

        String query = "select \n" + "\tsss.id_student_semester_status as id,\n" + "\tfamily,\n" + "\tname,\n" + "\tgroupname,\n" +
                       "\tget_date_first_debt_for_cancel_scholarship(sss.id_student_semester_status, '" + firstDateStr + "', '" +
                       secondDateStr + "') as firstDate,\n" + "\t(\n" +
                       "\t\tselect id_dic_action from order_action oa2 where oa2.id_studentcard = sc.id_studentcard \n" +
                       "\t\tand oa2.id_semester = " + prevSem + " and oa2.id_dic_action in (1,2) order by oa2.date_action desc limit 1\n" +
                       "\t) as idLastDicAction,\n" + "\t(\n" +
                       "\t\tselect date_finish from order_action oa2 where oa2.id_studentcard = sc.id_studentcard \n" +
                       "\t\tand oa2.id_semester = " + prevSem + " and oa2.id_dic_action = 1 order by oa2.date_action desc limit 1\n" +
                       "\t) as secondDate\n" + // дата окончания последней стипендии в этом семестре
                       "from \n" + "\tstudent_semester_status sss\n" + "        inner join studentcard sc using(id_studentcard)\n" +
                       "        inner join humanface hf using(id_humanface)\n" +
                       "        inner join link_group_semester lgs using(id_link_group_semester)\n" +
                       "        inner join dic_group dg using(id_dic_group)\n" + "where\n" + "\tlgs.id_semester = " + idSemester + "\n" +
                       "        and sss.is_deducted = 0\n" + "        and sss.is_academicleave = 0\n" +
                       "        and sss.is_sessionprolongation = 0\n" + "        and sc.id_current_dic_group = dg.id_dic_group\n" +
                       "        and get_date_first_debt_for_cancel_scholarship(sss.id_student_semester_status, '" + firstDateStr + "', '" +
                       secondDateStr + "') is not null\n" + "        order by groupname, family, name;";
        //"\tand groupname in (" + idsGroup + ")";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("family", StringType.INSTANCE)
                              .addScalar("name", StringType.INSTANCE)
                              .addScalar("groupname", StringType.INSTANCE)
                              .addScalar("firstDate", DateType.INSTANCE)
                              .addScalar("secondDate", DateType.INSTANCE)
                              .addScalar("idLastDicAction", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(OrderCreateStudentModel.class));
        return (List<OrderCreateStudentModel>) getList(q);
    }
}
