package org.edec.contingentMovement.report.manager;

import org.edec.contingentMovement.report.model.ResitMarkModel;
import org.edec.dao.DAO;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;

import java.util.List;

public class ResitMarkManager extends DAO {
    public List<ResitMarkModel> getResitMark(Long idStudentCard, Long idDicGroup){
         String query = "select ds.subjectname || ' ' ||  lgs.semesternumber || ' Семестр ' || cast  (sb.hourscount as integer) || ' ' as infoTo, \n" +
                 "ds2.subjectname || ' ' || lgs2.semesternumber || ' Семестр ' || cast (sb2.hourscount as integer) || ' ' as infoFrom,\n" +
                 "sr2.passrating as passRating, sr2.examrating as examRating, \n" +
                 "sr2.courseprojectrating as courseProjectRating,  sr2.courseworkrating as courseWorkRating, sr2.practicrating as practicRating,\n" +
                 "case when srh.is_exam = 1 then 'Экзамен'\n" +
                 "\twhen srh.is_pass = 1 then 'Зачет'\n" +
                 "\twhen srh.is_courseproject = 1 then 'Курсовой проект'\n" +
                 "\twhen srh.is_coursework = 1 then 'Курсовая работа'\n" +
                 "\twhen srh.is_practic = 1 then 'Практика'\n" +
                 "end as foc, srh.newrating as newRating\t\n" +
                 "from\n" +
                 "studentcard sc\n" +
                 "inner join student_semester_status sss using (id_studentcard)\n" +
                 "inner join link_group_semester lgs using (id_link_group_semester)\n" +
                 "inner join dic_group dg using (id_dic_group)\n" +
                 "inner join sessionrating sr using (id_student_semester_status)\n" +
                 "inner join sessionratinghistory srh using (id_sessionrating)\n" +
                 "inner join subject sb using (id_subject)\n" +
                 "inner join dic_subject ds using (id_dic_subject)\n" +
                 "inner join sessionrating sr2 on sr.id_sr_resit = sr2.id_sessionrating\n" +
                 "inner join subject sb2 on sb2.id_subject = sr2.id_subject\n" +
                 "inner join dic_subject ds2 on ds2.id_dic_subject = sb2.id_dic_subject\n" +
                 "inner join link_group_semester_subject lgss on lgss.id_subject = sb2.id_subject\n" +
                 "inner join link_group_semester lgs2 on lgs2.id_link_group_semester = lgss.id_link_group_semester\n" +
                 "where \n" +
                 "sc.id_studentcard = "+idStudentCard+"\n" +
                 "and dg.id_dic_group = "+idDicGroup+"\n" +
                 "and (srh.status = '1.5.0' or srh.status = '1.5.1')\n" +
                 "and (srh.newrating = 1 or srh.newrating = 3 or srh.newrating = 4 or srh.newrating = 5)";

         Query q = getSession().createSQLQuery(query)
                 .addScalar("infoTo")
                 .addScalar("infoFrom")
                 .addScalar("passRating", IntegerType.INSTANCE)
                 .addScalar("foc")
                 .addScalar("examRating", IntegerType.INSTANCE)
                 .addScalar("courseProjectRating", IntegerType.INSTANCE)
                 .addScalar("courseWorkRating", IntegerType.INSTANCE)
                 .addScalar("practicRating", IntegerType.INSTANCE)
                 .addScalar("newRating", IntegerType.INSTANCE)
                 .setResultTransformer(Transformers.aliasToBean(ResitMarkModel.class));

         return (List<ResitMarkModel>) getList(q);

    }
}
