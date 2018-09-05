package org.edec.utility.report.manager;

import org.edec.dao.DAO;
import org.edec.utility.report.model.commission.ProtocolModel;
import org.edec.utility.report.model.commission.dao.CommissionScheduleEso;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class CommissionOrderDAO extends DAO {
    public List<ProtocolModel> getProtocols (Long idCommissionRegister) {
        String query = "SELECT\n" + "\tHF.family||' '||SUBSTRING(HF.name, 1, 1)||'. '||SUBSTRING(HF.patronymic, 1, 1)||'.' AS fio,\n" +
                       "\tDS.subjectname AS subject, DG.groupname,\n" + "\tRC.comission_date AS commissionDate," +
                       "\tCASE WHEN SRH.is_exam = 1 THEN 'экзамена'\n" + "\t\tWHEN SRH.is_pass = 1 AND S.type = 1 THEN 'зачета (диф.)'\n" +
                       "\t\tWHEN SRH.is_pass = 1 AND S.TYPE = 0 THEN 'зачета'" +
                       "\t\tWHEN SRH.is_courseproject = 1 THEN 'курсового проекта'\n" +
                       "\t\tWHEN SRH.is_coursework = 1 THEN 'курсовой работы'\n" + "\t\tWHEN SRH.is_practic = 1 THEN 'практики'\n" +
                       "\tELSE '' END AS formOfControl,\n" + "\tCASE WHEN SRH.newrating = 1 THEN 'зачтено'\n" +
                       "\t\tWHEN SRH.newrating = 2 THEN 'неудовлетворительно'\n" + "\t\tWHEN SRH.newrating = 3 THEN 'удовлетворительно'\n" +
                       "\t\tWHEN SRH.newrating = 4 THEN 'хорошо'\n" + "\t\tWHEN SRH.newrating = 5 THEN 'отлично'\n" +
                       "\t\tWHEN SRH.newrating = -2 THEN 'не зачтено'\n" + "\t\tWHEN SRH.newrating = -3 THEN 'не явился'\n" +
                       "\tELSE '' END AS ratingStr\n" + "FROM\n" + "\tregister_comission RC\n" +
                       "\tINNER JOIN subject S USING (id_subject)\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                       "\tINNER JOIN register R USING (id_register)\n" + "\tINNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                       "\tINNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                       "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                       "\tINNER JOIN institute INST USING (id_institute)\n" + "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                       "\tINNER JOIN studentcard SC USING (id_studentcard)\n" + "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                       "WHERE\n" + "\tid_register_comission = " + idCommissionRegister + "\n" + "ORDER BY\n" + "\tfio";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("subject")
                              .addScalar("ratingStr")
                              .addScalar("groupname")
                              .addScalar("formOfControl")
                              .addScalar("commissionDate")
                              .setResultTransformer(Transformers.aliasToBean(ProtocolModel.class));
        return (List<ProtocolModel>) getList(q);
    }

    public List<CommissionScheduleEso> getScheduleModel (Integer formOfStudy, Long idChair) {
        String query = "SELECT DISTINCT ON (subjectname, datecommission, classroom, fulltitle)\n" + "DS.subjectname||'('||\n" + "CASE\n" +
                       "\tWHEN SRH.is_exam = 1 THEN 'экзамен'\n" + "\tWHEN SRH.is_pass = 1 THEN 'зачет'\n" +
                       "\tWHEN SRH.is_courseproject = 1 THEN 'КП'\n" + "\tWHEN SRH.is_coursework = 1 THEN 'КР'\n" +
                       "\tWHEN SRH.is_practic = 1 THEN 'Практика'\n" +
                       "END||')' AS subjectname, comission_date AS datecommission, classroom,\n" + "DEP.fulltitle\n" +
                       "FROM register_comission RC\n" + "\tINNER JOIN register R USING (id_register)\n" +
                       "\tINNER JOIN semester SEM USING (id_semester)\n" + "\tINNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                       "\tINNER JOIN subject S ON RC.id_subject = S.id_subject\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                       "\tINNER JOIN chair C USING (id_chair)\n" +
                       "\tINNER JOIN department DEP ON C.id_chair = DEP.id_chair AND DEP.is_main = TRUE\n" + "WHERE\n" +
                       "\tR.certnumber IS NULL\n" + "\t" + (formOfStudy == 3 ? "" : "AND SEM.formofstudy = " + formOfStudy) + "\n" + "\t" +
                       (idChair == null ? "" : " AND S.id_chair = " + idChair) + "\n" + "ORDER BY\n" +
                       "\tDEP.fulltitle, comission_date, subjectname";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("subjectname")
                              .addScalar("datecommission")
                              .addScalar("classroom")
                              .addScalar("fulltitle")
                              .setResultTransformer(Transformers.aliasToBean(CommissionScheduleEso.class));
        return (List<CommissionScheduleEso>) getList(q);
    }
}
