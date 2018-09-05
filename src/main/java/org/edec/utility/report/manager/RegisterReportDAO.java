package org.edec.utility.report.manager;

import org.edec.dao.DAO;
import org.edec.utility.report.model.register.RegisterJasperModel;
import org.edec.utility.report.model.register.StudentModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;

import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class RegisterReportDAO extends DAO {
    public RegisterJasperModel getRegisterReportModel(Long idRegister, Long idHumanface) {
        String query = "SELECT\n" +
                "\tDISTINCT ON (R.id_register)\n" +
                "\tINST.shorttitle AS institute, CAST(LGS.course AS TEXT), CAST(LGS.semesternumber AS TEXT) AS semester, DG.groupname, \n" +
                "\tDS.subjectname AS subject, CUR.directioncode AS major,\n" +
                "\tTO_CHAR(COALESCE(R.signdate, now()), 'dd.MM.yyyy') AS signdate,\n" +
                "\tCASE\n" +
                "\t\tWHEN abs(SRH.retake_count) <> 1 THEN TO_CHAR(COALESCE(R.signdate, now()), 'dd.MM.yyyy')\n" +
                "\t\tWHEN SRH.is_exam = 1 THEN TO_CHAR(LGSS.examdate, 'dd.MM.yyyy')\n" +
                "\t\tWHEN SRH.is_pass = 1 THEN TO_CHAR(LGSS.passdate, 'dd.MM.yyyy')\n" +
                "\t\tWHEN SRH.is_courseproject = 1 THEN TO_CHAR(LGSS.tmpcourseprojectdate, 'dd.MM.yyyy')\n" +
                "\t\tWHEN SRH.is_coursework = 1 THEN TO_CHAR(LGSS.tmpcourseworkdate, 'dd.MM.yyyy')\n" +
                "\t\tWHEN SRH.is_practic = 1 THEN TO_CHAR(LGSS.practicdate, 'dd.MM.yyyy')\n" +
                "\tEND AS dateOfExamination,\n" +
                "\tCASE\n" +
                "\t\tWHEN SRH.is_exam = 1 OR SRH.is_pass = 1 THEN S.hourscount||'('||trunc((CAST(S.hourscount/CAST(36 AS FLOAT) AS NUMERIC)), 1)||' з.е.)'\n" +
                "\tELSE '' END AS totalHours,\n" +
                "\tHF.family||' '||HF.name||' '||HF.patronymic AS teacher,\n" +
                "\tCASE\n" +
                "\t\tWHEN SRH.is_exam = 1 THEN 1\n" +
                "\t\tWHEN SRH.is_pass = 1 THEN 2\n" +
                "\t\tWHEN SRH.is_courseproject = 1 THEN 3\n" +
                "\t\tWHEN SRH.is_coursework = 1 THEN 4\n" +
                "\t\tWHEN SRH.is_practic = 1 THEN 5\n" +
                "\tEND AS formOfControl, SRH.retake_count AS retakeCount, \n" +
                "\tR.register_number AS registerNumber, R.certnumber, R.signatorytutor, SRH.type\n" +
                "FROM\n" +
                "\tlink_group_semester LGS\n" +
                "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                "\tINNER JOIN institute INST USING (id_institute)\n" +
                "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                "\tINNER JOIN curriculum CUR USING (id_curriculum)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_link_group_semester)\n" +
                "\tINNER JOIN sessionrating SR USING (id_student_semester_status)\n" +
                "\tINNER JOIN sessionratinghistory SRH USING (id_sessionrating)\n" +
                "\tINNER JOIN register R USING (id_register)\n" +
                "\tINNER JOIN subject S USING (id_subject)\n" +
                "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                "\tINNER JOIN link_group_semester_subject LGSS USING (id_subject)\n" +
                "\tINNER JOIN link_employee_subject_group LESG USING (id_link_group_semester_subject)\n" +
                "\tINNER JOIN employee EMP USING (id_employee)\n" +
                "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                "WHERE\n" +
                "\tR.id_register = :idRegister AND CAST(HF.id_humanface AS TEXT) ILIKE :idHum";
        Query q = getSession().createSQLQuery(query)
                .addScalar("institute").addScalar("course").addScalar("semester").addScalar("groupname").addScalar("subject")
                .addScalar("major").addScalar("signdate").addScalar("dateOfExamination").addScalar("totalHours")
                .addScalar("formOfControl").addScalar("retakeCount").addScalar("registerNumber")
                .addScalar("certnumber").addScalar("signatorytutor").addScalar("type")
                .addScalar("teacher")
                .setResultTransformer(Transformers.aliasToBean(RegisterJasperModel.class));
        q.setLong("idRegister", idRegister).setParameter("idHum", idHumanface == null ? "%%" : String.valueOf(idHumanface), StringType.INSTANCE);
        List<?> list = getList(q);
        return list.size() == 0 ? null : (RegisterJasperModel) list.get(0);
    }

    public List<StudentModel> getStudentByRegister(Long idRegister) {
        String query = "SELECT\n" +
                "\tHF.family||' '||SUBSTRING(HF.name FROM 1 FOR 1)||'. '||SUBSTRING(HF.patronymic FROM 1 FOR 1)||'.' AS fio,\n" +
                "\tSC.recordbook AS recordBook, SRH.newrating AS rating,\n" +
                "\tCASE\n" +
                "\t\tWHEN SRH.is_courseproject = 1 THEN COALESCE(SR.esocourseprojecttheme, '')\n" +
                "\t\tWHEN SRH.is_coursework = 1 THEN COALESCE(SR.esocourseworktheme, '')\n" +
                "\tELSE '' END AS themeOfWork\n" +
                "FROM\n" +
                "\tsessionratinghistory SRH\n" +
                "\tINNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "\tINNER JOIN studentcard SC USING (id_studentcard)\n" +
                "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                "WHERE\n" +
                "\tSRH.id_register = :idRegister\n" +
                "ORDER BY\n" +
                "\tfio";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio").addScalar("recordBook").addScalar("rating").addScalar("themeOfWork")
                .setResultTransformer(Transformers.aliasToBean(StudentModel.class));
        q.setLong("idRegister", idRegister);
        return (List<StudentModel>) getList(q);
    }
}
