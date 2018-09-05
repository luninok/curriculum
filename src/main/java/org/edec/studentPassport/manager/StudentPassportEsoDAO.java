package org.edec.studentPassport.manager;

import org.edec.dao.DAO;
import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.studentPassport.model.dao.RatingEsoModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.List;


public class StudentPassportEsoDAO extends DAO {

    public StudentStatusModel getStudentByScId (Long studentCardId) {
        String scId = "%%";
        if (studentCardId != null) {
            scId = studentCardId.toString();
        }
        String query = "SELECT\n" + "\tHF.family, HF.name, HF.patronymic, SC.recordBook, max(LGS.course) AS course, \n" +
                "\tDG.groupname, max(SSS.is_academicleave) = 1 AS academicLeave, max(SSS.is_deducted) = 1 AS deducted,\n" +
                "\tMAX(SSS.is_trustagreement)= 1 AS trustAgreement, MAX(SSS.is_government_financed) = 1 AS governmentFinanced,\n" +
                "\tMAX(SSS.id_student_semester_status) AS idSSS, HF.email, HF.birthday, HF.id_humanface AS idHum, " +
                "\tDG.id_dic_group AS idDG, SC.id_studentcard AS idStudentCard,\n" +
                "\tHF.foreigner, SE.id_institute AS idInstitute\n" + "FROM\n" + "\thumanface HF\n" +
                "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                "\tLEFT JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "\tLEFT JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tLEFT JOIN semester SE USING (id_semester)\n" + "\tLEFT JOIN dic_group DG USING (id_dic_group) \n" + "WHERE\n" +
                "\tCAST(SC.id_studentcard AS TEXT) ILIKE :studentcard\n" + "GROUP BY\n" +
                "\tHF.id_humanface, SC.id_studentcard, DG.id_dic_group, SE.id_institute\n" + "ORDER BY\n" + "\tHF.family, HF.name, LGS.id_semester";
        Query q = getSession().createSQLQuery(query)
                .addScalar("family")
                .addScalar("name")
                .addScalar("patronymic")
                .addScalar("recordBook")
                .addScalar("groupname")
                .addScalar("course")
                .addScalar("academicLeave")
                .addScalar("deducted")
                .addScalar("birthday")
                .addScalar("idSSS", LongType.INSTANCE)
                .addScalar("email")
                .addScalar("trustAgreement")
                .addScalar("governmentFinanced")
                .addScalar("idHum", LongType.INSTANCE)
                .addScalar("idDG", LongType.INSTANCE)
                .addScalar("idStudentCard", LongType.INSTANCE)
                .addScalar("foreigner")
                .addScalar("idInstitute", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(StudentStatusModel.class));
        q.setParameter("studentcard", scId);
        List<?> list = getList(q);
        return list.size() == 0 ? null : (StudentStatusModel) list.get(0);
    }

    public List<StudentStatusModel> getStudentByFilter (String fio, String recordBook, String groupname) {
        String query = "SELECT\n" + "\tHF.family, HF.name, HF.patronymic, SC.id_current_dic_group AS currentGroupId, DG.id_dic_group AS groupId, SC.recordBook, max(LGS.course) AS course, \n" +
                "\tDG.groupname, max(SSS.is_academicleave) = 1 AS academicLeave, max(SSS.is_deducted) = 1 AS deducted,\n" +
                "\tMAX(SSS.is_educationcomplete)=1 AS educationcomplite,\n" +
                "\tMAX(SSS.is_trustagreement)= 1 AS trustAgreement, MAX(SSS.is_government_financed) = 1 AS governmentFinanced,\n" +
                "\tMAX(SSS.id_student_semester_status) AS idSSS, HF.email, HF.birthday, HF.id_humanface AS idHum, " +
                "\tDG.id_dic_group AS idDG, SC.id_studentcard AS idStudentCard,\n" +
                "\tHF.foreigner, SE.id_institute AS idInstitute, MAX(SE.id_semester) AS idSemester\n" +
                "\tFROM\n" + "\thumanface HF\n" +
                "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN semester SE USING (id_semester)\n" + "\tINNER JOIN dic_group DG USING (id_dic_group) \n" + "WHERE\n" +
                "\tHF.family||' '||HF.name||' '||HF.patronymic ILIKE :fio\n" + "\tAND SC.recordBook ILIKE :recordBook\n" +
                "\tAND DG.groupname ILIKE :groupname\n" + "GROUP BY\n" +
                "\tHF.id_humanface, SC.id_studentcard, DG.id_dic_group, SE.id_institute\n" + "ORDER BY\n" + "\tHF.family, HF.name";
        Query q = getSession().createSQLQuery(query)
                .addScalar("family")
                .addScalar("name")
                .addScalar("currentGroupId", LongType.INSTANCE)
                .addScalar("groupId", LongType.INSTANCE)
                .addScalar("patronymic")
                .addScalar("recordBook")
                .addScalar("groupname")
                .addScalar("course")
                .addScalar("academicLeave")
                .addScalar("deducted")
                .addScalar("birthday")
                .addScalar("idSSS", LongType.INSTANCE)
                .addScalar("email")
                .addScalar("educationcomplite")
                .addScalar("trustAgreement")
                .addScalar("governmentFinanced")
                .addScalar("idHum", LongType.INSTANCE)
                .addScalar("idDG", LongType.INSTANCE)
                .addScalar("idStudentCard", LongType.INSTANCE)
                .addScalar("foreigner")
                .addScalar("idInstitute", LongType.INSTANCE)
                .addScalar("idSemester",LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(StudentStatusModel.class));
        q.setParameter("fio", "%" + fio + "%")
                .setParameter("recordBook", "%" + recordBook + "%")
                .setParameter("groupname", "%" + groupname + "%");
        return (List<StudentStatusModel>) getList(q);
    }

    public List<StudentStatusModel> getStudentsByFilterDetail (String fio, String recordBook, String groupname) {
        String query = "SELECT\n" + "\tHF.family, HF.name, HF.patronymic, SC.id_current_dic_group AS currentGroupId, DG.id_dic_group AS groupId, SC.recordBook, " +
                "LGS.course AS course, \n" +
                "\tDG.groupname, SSS.is_academicleave = 1 AS academicLeave, SSS.is_deducted = 1 AS deducted,\n" +
                "\tSSS.is_educationcomplete = 1 AS educationcomplite,\n" +
                "\tSSS.is_trustagreement = 1 AS trustAgreement, SSS.is_government_financed = 1 AS governmentFinanced,\n" +
                "\tSSS.id_student_semester_status AS idSSS, HF.email, HF.birthday, HF.id_humanface AS idHum, " +
                "\tDG.id_dic_group AS idDG, SC.id_studentcard AS idStudentCard,\n" +
                "\tHF.foreigner, SE.id_institute AS idInstitute, SE.id_semester AS idSemester\n" +
                "\tFROM\n" + "\thumanface HF\n" +
                "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN semester SE USING (id_semester)\n" + "\tINNER JOIN dic_group DG USING (id_dic_group) \n" + "WHERE\n" +
                "\tHF.family||' '||HF.name||' '||HF.patronymic ILIKE :fio\n" + "\tAND SC.recordBook ILIKE :recordBook\n" +
                "\tAND DG.groupname ILIKE :groupname\n" + "ORDER BY\n" + "\tHF.family, HF.name, SC.id_studentcard DESC, LGS.id_semester DESC";
        Query q = getSession().createSQLQuery(query)
                .addScalar("family")
                .addScalar("name")
                .addScalar("currentGroupId", LongType.INSTANCE)
                .addScalar("groupId", LongType.INSTANCE)
                .addScalar("patronymic")
                .addScalar("recordBook")
                .addScalar("groupname")
                .addScalar("course")
                .addScalar("academicLeave")
                .addScalar("deducted")
                .addScalar("birthday")
                .addScalar("idSSS", LongType.INSTANCE)
                .addScalar("email")
                .addScalar("educationcomplite")
                .addScalar("trustAgreement")
                .addScalar("governmentFinanced")
                .addScalar("idHum", LongType.INSTANCE)
                .addScalar("idDG", LongType.INSTANCE)
                .addScalar("idStudentCard", LongType.INSTANCE)
                .addScalar("foreigner")
                .addScalar("idInstitute", LongType.INSTANCE)
                .addScalar("idSemester",LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(StudentStatusModel.class));
        q.setParameter("fio", "%" + fio + "%")
                .setParameter("recordBook", "%" + recordBook + "%")
                .setParameter("groupname", "%" + groupname + "%");
        return (List<StudentStatusModel>) getList(q);
    }

    public boolean saveStudent (StudentStatusModel studentStatusModel) {
        try {
            begin();
            String query = "UPDATE humanface SET family = :family, name = :name, patronymic = :patronymic, " +
                    "email = :email, birthday = :birthday, foreigner = :foreigner \n" + "WHERE id_humanface = :idHum";
            Query q = getSession().createSQLQuery(query);
            q.setParameter("family", studentStatusModel.getFamily())
                    .setParameter("name", studentStatusModel.getName())
                    .setParameter("patronymic", studentStatusModel.getPatronymic())
                    .setParameter("email", studentStatusModel.getEmail())
                    .setDate("birthday", studentStatusModel.getBirthday())
                    .setParameter("idHum", studentStatusModel.getIdHum())
                    .setBoolean("foreigner", studentStatusModel.getForeigner());
            q.executeUpdate();
            String queryStudentcard = "UPDATE studentcard SET recordBook = :recordBook WHERE id_studentcard = :idStudentcard";
            q = getSession().createSQLQuery(queryStudentcard);
            q.setParameter("recordBook", studentStatusModel.getRecordBook())
                    .setParameter("idStudentcard", studentStatusModel.getIdStudentCard());
            q.executeUpdate();
            commit();
            return true;
        } catch (HibernateException e) {
            rollback();
            e.printStackTrace();
            return false;
        } finally {
            close();
        }
    }

    public List<RatingEsoModel> getRatingByIdHumAndDigGroup (Long idHum, Long idDG) {
        String query = "SELECT \n" +
                "\tSR.is_exam = 1 AS exam, SR.is_pass = 1 AS pass, SR.is_courseproject = 1 AS cp, SR.is_coursework = 1 AS cw, SR.is_practic = 1 AS practic,\n" +
                "\tSR.examrating, SR.passrating, SR.courseprojectrating AS cprating, SR.courseworkrating AS cwrating, SR.practicrating,\n" +
                "\tDS.subjectname,\n" + "\tCASE\n" +
                "\t\tWHEN SEM.season = 0 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'-'||EXTRACT(YEAR FROM SY.dateofend)||' (осень)'\n" +
                "\t\tWHEN SEM.season = 1 THEN EXTRACT(YEAR FROM SY.dateofbegin)||'-'||EXTRACT(YEAR FROM SY.dateofend)||' (весна)'\n" +
                "\tEND AS semester\n" + "FROM\n" + "\tstudentcard SC\n" +
                "\tINNER JOIN student_semester_status USING (id_studentcard)\n" +
                "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "\tINNER JOIN semester SEM USING (id_semester)\n" + "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                "\tINNER JOIN sessionrating SR USING (id_student_semester_status)\n" + "\tINNER JOIN subject USING (id_subject)\n" +
                "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" + "WHERE\n" + "\tSC.id_humanface = " + idHum +
                " AND LGS.id_dic_group = " + idDG;
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
        return (List<RatingEsoModel>) getList(q);
    }
}
