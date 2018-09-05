package org.edec.secretaryChair.manager;

import org.edec.dao.DAO;
import org.edec.secretaryChair.model.CommissionModel;
import org.edec.secretaryChair.model.EmployeeModel;
import org.edec.secretaryChair.model.StudentCommissionModel;
import org.edec.teacher.model.commission.StudentModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.converter.DateConverter;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StandardBasicTypes;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class EntityManagerSecretaryChair extends DAO {
    public List<SemesterModel> getSemestersByIdChair (Long idChair, Integer formOfStudy) {
        String query = "SELECT DISTINCT LGS.id_semester AS idSem, SY.dateofbegin AS dateOfBegin, SY.dateofend AS dateOfEnd, \n" +
                       "\tSEM.season, SEM.formofstudy\n" + "FROM register_comission RC\n" + "\tINNER JOIN subject S USING (id_subject)\n" +
                       "\tINNER JOIN chair CH USING (id_chair)\n" + "\tINNER JOIN department DEP USING (id_chair)" +
                       "\tINNER JOIN register R USING (id_register)\n" + "\tINNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                       "\tINNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                       "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                       "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" + "WHERE DEP.id_chair = " + idChair + "\n" + "" +
                       (formOfStudy == 3 ? "" : "\tAND SEM.formofstudy = " + formOfStudy) + "\n" + "ORDER BY idSem";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idSem", LongType.INSTANCE)
                              .addScalar("dateOfBegin")
                              .addScalar("dateOfEnd")
                              .addScalar("season")
                              .addScalar("formofstudy")
                              .setResultTransformer(Transformers.aliasToBean(SemesterModel.class));
        return (List<SemesterModel>) getList(q);
    }

    public List<CommissionModel> getCommissionByChair (Long idSem, Long idChair, Integer formOfStudy, boolean singed) {
        String query = "SELECT\n" + "\tRC.id_register_comission AS id, RC.classroom, S.id_chair AS idChair, \n" +
                       "\tRC.comission_date AS commissionDate, DS.subjectname AS subjectName,\n" +
                       "\tRC.dateofbegincomission AS dateBegin, RC.dateofendcomission AS dateEnd,\n" + "\tCASE\n" +
                       "\t\tWHEN SRH.is_exam = 1 THEN 1\n" + "\t\tWHEN SRH.is_pass = 1 THEN 2\n" +
                       "\t\tWHEN SRH.is_courseproject = 1 THEN 3\n" + "\t\tWHEN SRH.is_coursework = 1 THEN 4\n" +
                       "\t\tWHEN SRH.is_practic = 1 THEN 5\n" + "\tEND AS formOfControl,\n" +
                       "\tEXTRACT(YEAR FROM SY.dateOfBegin)||'/'||EXTRACT(YEAR FROM SY.dateOfEnd)||' '||\n" + "\t\tCASE\n" +
                       "\t\t\tWHEN SEM.season = 0 THEN 'осень'\n" + "\t\t\tWHEN SEM.season = 1 THEN 'весна'\n" +
                       "\t\tEND AS semesterStr,\n" + "\tCASE\n" + "\t\tWHEN (RC.classroom IS NULL AND RC.comission_date IS NULL) THEN 0\n" +
                       "\t\tWHEN (RC.classroom IS NOT NULL AND R.certnumber IS NULL) THEN 1\n" +
                       "\t\tWHEN (R.certnumber IS NOT NULL) THEN 2\n" + "\tEND AS status\n" + "FROM student_semester_status SSS\n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                       "\tINNER JOIN schoolyear SY USING (id_schoolyear)\n" +
                       "\tINNER JOIN sessionrating SR USING (id_student_semester_status)\n" +
                       "\tINNER JOIN sessionratinghistory SRH USING (id_sessionrating)\n" +
                       "\tINNER JOIN register R USING (id_register)\n" + "\tINNER JOIN register_comission RC USING (id_register)\n" +
                       "\tINNER JOIN subject S ON RC.id_subject = S.id_subject\n" + "\tINNER JOIN chair CH USING (id_chair)\n" +
                       "\tINNER JOIN department DEP USING (id_chair)" + "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                       "WHERE DEP.id_chair = :idChair\n" + (idSem == null ? "" : "\t" + "AND SEM.id_semester = " + idSem + "\n") +
                       (singed ? "" : "\t" + "AND R.certnumber IS NULL\n") +
                       (formOfStudy == 3 ? "" : "\t" + "AND SEM.formofstudy = " + formOfStudy + "\n") +
                       "GROUP BY id, subjectname, formOfControl, semesterStr, idChair, R.id_register\n" + "ORDER BY semesterStr, status";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("id", LongType.INSTANCE)
                              .addScalar("classroom")
                              .addScalar("commissionDate")
                              .addScalar("subjectName")
                              .addScalar("formOfControl")
                              .addScalar("semesterStr")
                              .addScalar("dateBegin")
                              .addScalar("dateEnd")
                              .addScalar("idChair", LongType.INSTANCE)
                              .addScalar("status")
                              .setResultTransformer(Transformers.aliasToBean(CommissionModel.class));
        q.setLong("idChair", idChair);
        return (List<CommissionModel>) getList(q);
    }

    public List<CommissionModel> getCommissionByDate (Long idCommission) {
        String query =
                "WITH selectedCommission AS (SELECT RC.id_register_comission AS idComm, SSS.id_studentcard, SR.id_student_semester_status,\n" +
                "          SR.id_subject, RC.dateofbegincomission, RC.dateofendcomission\n" + "       FROM register_comission RC\n" +
                "           INNER JOIN register R USING (id_register)\n" +
                "           INNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                "           INNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                "           INNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "       WHERE RC.id_register_comission = " + idCommission + ")\n" +
                "  SELECT RC.comission_date AS commissionDate, RC.id_subject AS idSubject\n" + "  FROM register_comission RC\n" +
                "    INNER JOIN register R USING (id_register)\n" + "    INNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                "    INNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                "    INNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                "    INNER JOIN selectedCommission COMM ON RC.id_register_comission <> COMM.idComm AND SSS.id_studentcard = COMM.id_studentcard\n" +
                "                                      AND (SR.id_student_semester_status, SR.id_subject) <> (COMM.id_student_semester_status, COMM.id_subject)\n" +
                "  WHERE R.certnumber IS NULL AND RC.comission_date IS NOT NULL\n" + "  GROUP BY RC.id_register_comission\n" +
                "  ORDER BY commissionDate;";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("commissionDate")
                              .addScalar("idSubject", StandardBasicTypes.LONG)
                              .setResultTransformer(Transformers.aliasToBean(CommissionModel.class));
        return (List<CommissionModel>) getList(q);
    }

    public List<EmployeeModel> getEmployeesByDepartment (Long idChair) {
        String query = "SELECT HF.family||' '||HF.name||' '||HF.patronymic AS fio, LED.id_link_employee_department AS idLED,\n" +
                       "\tEMP.id_employee AS idEmployee, ER.rolename AS role\n" + "FROM link_employee_department LED \n" +
                       "\tINNER JOIN department DEP USING (id_department)\n" + "\tINNER JOIN employee_role ER USING (id_employee_role)\n" +
                       "\tINNER JOIN employee EMP USING (id_employee)\n" + "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                       "WHERE LED.is_hide = FALSE AND DEP.id_chair = :idChair\n" + "ORDER BY fio";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("idLED", LongType.INSTANCE)
                              .addScalar("idEmployee", LongType.INSTANCE)
                              .addScalar("role")
                              .setResultTransformer(Transformers.aliasToBean(EmployeeModel.class));
        q.setLong("idChair", idChair);
        return (List<EmployeeModel>) getList(q);
    }

    public List<EmployeeModel> getCommissionEmployee (Long idCommissionRegister) {
        String query = "SELECT HF.family||' '||HF.name||' '||HF.patronymic AS fio, LED.id_link_employee_department AS idLED,\n" +
                       "\tEMP.id_employee AS idEmployee, ER.rolename AS role, LEDC.leader, LEDC.pos, LEDC.sign\n" +
                       "FROM link_employee_department LED \n" + "\tINNER JOIN employee_role ER USING (id_employee_role)\n" +
                       "\tINNER JOIN employee EMP USING (id_employee)\n" + "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                       "\tINNER JOIN led_comission LEDC USING (id_link_employee_department)\n" +
                       "WHERE LEDC.id_register_comission = :idRC\n" + "ORDER BY fio";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("idLED", LongType.INSTANCE)
                              .addScalar("idEmployee", LongType.INSTANCE)
                              .addScalar("role")
                              .addScalar("leader")
                              .addScalar("pos")
                              .setResultTransformer(Transformers.aliasToBean(EmployeeModel.class));
        q.setLong("idRC", idCommissionRegister);
        return (List<EmployeeModel>) getList(q);
    }

    public List<StudentModel> getStudentsByCommission (Long idCommission) {
        String query = "SELECT HF.family||' '||HF.name||' '||HF.patronymic AS fio,\n" + "\tDG.groupname\n" + "FROM humanface HF\n" +
                       "\tINNER JOIN studentcard SC USING (id_humanface)\n" +
                       "\tINNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN dic_group DG USING (id_dic_group)\n" +
                       "\tINNER JOIN sessionrating SR USING (id_student_semester_status)\n" +
                       "\tINNER JOIN sessionratinghistory SRH USING (id_sessionrating)\n" +
                       "\tINNER JOIN register R USING (id_register)\n" + "\tINNER JOIN register_comission RC USING (id_register)\n" +
                       "WHERE RC.id_register_comission = :idCommission \n" + "ORDER BY fio";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("groupname")
                              .setResultTransformer(Transformers.aliasToBean(StudentModel.class));
        q.setLong("idCommission", idCommission);
        return (List<StudentModel>) getList(q);
    }

    public List<StudentCommissionModel> getStudentsForCheckFreeDate (Long idComm, Date dateComm, boolean day) {

        String dateCondition;

        if (day) {
            dateCondition = "\tAND CAST(RC.comission_date AS DATE) = date '" + DateConverter.convertDateToSQLStringFormat(dateComm) + "'";
        } else {
            String date = DateConverter.convertTimestampToSQLStringFormate(dateComm);
            dateCondition = "\tAND RC.comission_date BETWEEN (timestamp '" + date + "' - time '01:59') AND (timestamp '" + date +
                            "' + time '01:59')\n";
        }

        String query = "SELECT HF.family, HF.name, HF.patronymic, DG.groupname, RC.comission_date AS dateCommission,\n" +
                       "\tDS.subjectname, CASE WHEN SRH.is_exam = 1 THEN 1\n" + "  WHEN SRH.is_pass = 1 THEN 2\n" +
                       "  WHEN SRH.is_courseproject = 1 THEN 3\n" + "  WHEN SRH.is_coursework = 1 THEN 4\n" +
                       "  WHEN SRH.is_practic = 1 THEN 5 END AS foc,\n" + "  SC.id_studentcard AS idStudentCard " +
                       "FROM register_comission RC\n" + "  INNER JOIN subject S USING (id_subject)\n" +
                       "  INNER JOIN dic_subject DS USING (id_dic_subject)\n" + "  INNER JOIN register R USING (id_register)\n" +
                       "  INNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                       "  INNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                       "  INNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                       "  INNER JOIN studentcard SC USING (id_studentcard)\n" + "  INNER JOIN humanface HF USING (id_humanface)\n" +
                       "  INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "  INNER JOIN dic_group DG USING (id_dic_group)\n" +
                       "  INNER JOIN (SELECT SSS.id_studentcard, RC.id_register_comission, SSS.id_student_semester_status, SR.id_subject\n" +
                       "      FROM register_comission RC\n" + "      INNER JOIN register R USING (id_register)\n" +
                       "      INNER JOIN semester SEM USING (id_semester)\n" +
                       "      INNER JOIN sessionratinghistory SRH USING (id_register)\n" +
                       "      INNER JOIN sessionrating SR USING (id_sessionrating)\n" +
                       "      INNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                       "      WHERE RC.id_register_comission = " + idComm +
                       ") AS searchRegister ON SSS.id_studentcard = searchRegister.id_studentcard\n" +
                       "          AND RC.id_register_comission <> searchRegister.id_register_comission\n" +
                       "          AND (SR.id_student_semester_status, SR.id_subject) <> (searchRegister.id_student_semester_status, searchRegister.id_subject)\n" +
                       "WHERE R.certnumber IS NULL\n" + dateCondition;
        Query q = getSession().createSQLQuery(query)
                              .addScalar("family")
                              .addScalar("name")
                              .addScalar("patronymic")
                              .addScalar("subjectname")
                              .addScalar("groupname")
                              .addScalar("dateCommission")
                              .addScalar("foc")
                              .addScalar("idStudentCard", StandardBasicTypes.LONG)
                              .setResultTransformer(Transformers.aliasToBean(StudentCommissionModel.class));
        return (List<StudentCommissionModel>) getList(q);
    }

    public boolean updateCommissionInfo (Date dateCommission, String classroom, Long idCommission) {
        Query q = getSession().createSQLQuery("UPDATE register_comission SET comission_date = :dateCommission, classroom = :classroom\n" +
                                              "WHERE id_register_comission = :idCommission");
        q.setTimestamp("dateCommission", dateCommission).setString("classroom", classroom).setLong("idCommission", idCommission);
        return executeUpdate(q);
    }

    public boolean deleteCommissionStaff (Long idCommission) {
        Query q = getSession().createSQLQuery("DELETE FROM led_comission WHERE id_register_comission = :idCommission");
        q.setLong("idCommission", idCommission);
        return executeUpdate(q);
    }

    public boolean addCommissionStaff (EmployeeModel employee, Long idCommission) {
        Query q = getSession().createSQLQuery("INSERT INTO led_comission(id_register_comission, id_link_employee_department, leader) " +
                                              " VALUES (:idCommission, :idLED, :leader)");
        q.setLong("idCommission", idCommission).setLong("idLED", employee.getIdLED()).setInteger("leader", employee.getLeader());
        return executeUpdate(q);
    }
}
