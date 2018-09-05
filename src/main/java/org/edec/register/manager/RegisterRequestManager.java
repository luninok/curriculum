package org.edec.register.manager;

import org.edec.dao.DAO;
import org.edec.register.model.RegisterRequestModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RegisterRequestManager extends DAO {

    public List<RegisterRequestModel> getAllRegisterRequests (long idInstitute, int fos) {
        String query = "SELECT RR.id_register_request AS idRegisterRequest,\n" +
                       "\tTHF.family || ' ' || THF.name || ' ' || THF.patronymic AS teacherFullName,\n" +
                       "\tSHF.family || ' ' || SHF.name || ' ' || SHF.patronymic AS studentFullName,\n" + "\tTHF.email as email,\n" +
                       "\tTHF.get_notification as getNotification,\n" + "\tDG.groupname as groupName,\n" +
                       "\tDG.id_dic_group as idGroup,\n" + "\tDS.subjectname as subjectName,\n" + "\tSEM.season as seasonSemester,\n" +
                       "\tSY.dateofbegin as dateOfBeginSemester,\n" + "\tSY.dateofend as dateOfEndSemester,\n" +
                       "\tRR.dateofapplying as dateOfApplying,\n" + "\tRR.dateofanswering as dateOfAnswering,\n" +
                       "\tRR.additional_information as additionalInformation,\n" + "\tRR.request_status as status,\n" +
                       "\tRR.formofcontrol as foc,\n" + "\tRR.id_link_group_semester_subject as idLgss,\n" +
                       "\tRR.id_student_semester_status as idSss\n" + "\tFROM register_request RR\n" +
                       "\tINNER JOIN humanface THF ON RR.id_humanface = THF.id_humanface\n" +
                       "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                       "\tINNER JOIN studentcard SC USING (id_studentcard)\n" +
                       "\tINNER JOIN humanface SHF ON SC.id_humanface = SHF.id_humanface\n" +
                       "\tINNER JOIN link_group_semester_subject LGSS ON RR.id_link_group_semester_subject = LGSS.id_link_group_semester_subject\n" +
                       "\tINNER JOIN subject SUB USING (id_subject)\n" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                       "\tINNER JOIN link_group_semester LGS ON LGSS.id_link_group_semester = LGS.id_link_group_semester\n" +
                       "\tINNER JOIN dic_group DG USING (id_dic_group)\n" + "\tINNER JOIN semester SEM USING (id_semester)\n" +
                       "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" + "\tWHERE SEM.id_institute = :idInstitute\n" +
                       "\tAND SEM.formofstudy " + (fos != 3 ? "= " + fos : " in (1,2)") +
                       "\tORDER BY RR.dateofapplying DESC, RR.request_status ASC\n";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("idRegisterRequest", LongType.INSTANCE)
                              .addScalar("idLgss", LongType.INSTANCE)
                              .addScalar("idGroup", LongType.INSTANCE)
                              .addScalar("idSss", LongType.INSTANCE)
                              .addScalar("teacherFullName")
                              .addScalar("studentFullName")
                              .addScalar("groupName")
                              .addScalar("subjectName")
                              .addScalar("dateOfApplying")
                              .addScalar("dateOfAnswering")
                              .addScalar("additionalInformation")
                              .addScalar("status")
                              .addScalar("foc")
                              .addScalar("dateOfBeginSemester")
                              .addScalar("dateOfEndSemester")
                              .addScalar("seasonSemester")
                              .addScalar("email")
                              .addScalar("getNotification", BooleanType.INSTANCE)
                              .setParameter("idInstitute", idInstitute)
                              .setResultTransformer(Transformers.aliasToBean(RegisterRequestModel.class));
        return (List<RegisterRequestModel>) getList(q);
    }

    public boolean updateRequestStatus (long idRegisterRequest, int requestStatus, Date dateOfAnswering, String additionalInformation) {
        String query = "UPDATE register_request \n" + "SET request_status = :requestStatus,\n" + "dateofanswering = :dateOfAnswering,\n" +
                       "additional_information = :additionalInformation\n" + "WHERE id_register_request = :idRegisterRequest\n" + "\n";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idRegisterRequest", idRegisterRequest)
         .setParameter("requestStatus", requestStatus)
         .setParameter("dateOfAnswering", dateOfAnswering)
         .setParameter("additionalInformation", additionalInformation);
        return executeUpdate(q);
    }
}
