package org.edec.reference.manager;

import org.edec.dao.DAO;
import org.edec.reference.model.ExcelReportModel;
import org.edec.reference.model.ReferenceModel;
import org.edec.reference.model.StudentModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ReferenceManager extends DAO {

    public List<StudentModel> getStudents (String tbValue, Long idInst) {
        String query = "SELECT\n" + "\tSC.id_studentcard as idStudentcard,\n" + "\tHF.id_humanface as idHumanface,\n" +
                       "\tHF.family || ' ' || HF.name || ' ' || HF.patronymic AS fio,\n" + "\tHF.birthday as dateOfBirth,\n" +
                       "\tDG.groupname as groupName,\n" + "\tSC.is_invalid as isInvalid,\n" + "\tSC.is_sirota as isOrphan,\n" +
                       "\tSC.is_indigent as isIndigent,\n" + "\tSC.type_invalid as invalidType,\n" + "\tSEM.id_semester as idSemester\n" +
                       "\tFROM\n" + "\tstudentcard SC\n" + "\tINNER JOIN humanface HF ON SC.id_humanface = HF.id_humanface\n" +
                       "\tINNER JOIN student_semester_status SSS ON SC.id_studentcard = SSS.id_studentcard\n" +
                       "\tINNER JOIN link_group_semester LGS ON SSS.id_link_group_semester = LGS.id_link_group_semester\n" +
                       "\tINNER JOIN dic_group DG ON LGS.id_dic_group = DG.id_dic_group\n" +
                       "\tINNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" + "\tWHERE SEM.id_institute = :idInstitute\n" +
                       "\tAND (SSS.is_deducted = 0 and SSS.formofstudy = 1 and SSS.is_government_financed = 1) AND SC.id_humanface IN (select id_humanface from humanface where" +
                       " family ilike '%" + tbValue + "%' ) \n" + "\tAND SEM.is_current_sem = 1 \n" + "\tAND SEM.id_institute = " + idInst +
                       "\n";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("idStudentcard", LongType.INSTANCE)
                              .addScalar("idHumanface", LongType.INSTANCE)
                              .addScalar("fio")
                              .addScalar("dateOfBirth")
                              .addScalar("groupName")
                              .addScalar("isInvalid", BooleanType.INSTANCE)
                              .addScalar("isOrphan", BooleanType.INSTANCE)
                              .addScalar("isIndigent", BooleanType.INSTANCE)
                              .addScalar("invalidType")
                              .addScalar("idSemester", LongType.INSTANCE)
                              .setParameter("idInstitute", idInst)
                              .setResultTransformer(Transformers.aliasToBean(StudentModel.class));
        return (List<StudentModel>) getList(q);
    }

    public List<ReferenceModel> getReferences (long idStudentcard) {
        String query = "SELECT\n" + "\tRF.id_reference AS idRef,\n" + "\tRF.id_reference_subtype AS refType,\n" +
                       "\tRF.book_number AS booknumber,\n" + "\tRF.date_start AS dateStart,\n" + "\tRF.date_finish AS dateFinish,\n" +
                       "\tRF.date_get AS dateGet,\n" + "\tRF.url AS url\n" + "\tFROM\n" + "\treference RF\n" +
                       "\tWHERE RF.id_studentcard = :idStudentcard\n";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("idRef", LongType.INSTANCE)
                              .addScalar("refType", LongType.INSTANCE)
                              .addScalar("booknumber")
                              .addScalar("url")
                              .addScalar("dateStart")
                              .addScalar("dateFinish")
                              .addScalar("dateGet")
                              .setParameter("idStudentcard", idStudentcard)
                              .setResultTransformer(Transformers.aliasToBean(ReferenceModel.class));
        return (List<ReferenceModel>) getList(q);
    }

    public boolean updateStudentStatus (long idStudentcard, boolean isOrphan, boolean isInvalid, int typeInvalid, boolean isIndigent) {
        String query =
                "UPDATE studentcard \n" + "SET is_sirota = :isOrphan,\n" + "is_invalid = :isInvalid,\n" + "is_indigent = :isIndigent,\n" +
                "type_invalid = :typeInvalid\n" + "WHERE id_studentcard = :idStudentcard\n" + "\n";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("isOrphan", isOrphan ? 1 : 0)
         .setParameter("isInvalid", isInvalid ? 1 : 0)
         .setParameter("isIndigent", isIndigent ? 1 : 0)
         .setParameter("typeInvalid", typeInvalid)
         .setParameter("idStudentcard", idStudentcard);
        return executeUpdate(q);
    }

    public boolean updateStudentDateOfBirth (long idHumanface, Date dateOfBirth) {
        String query = "UPDATE humanface \n" + "SET birthday = :dateOfBirth\n" + "WHERE id_humanface = :idHumanface\n" + "\n";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idHumanface", idHumanface).setParameter("dateOfBirth", dateOfBirth);
        return executeUpdate(q);
    }

    public Long createReference (ReferenceModel reference) {
        String query = "INSERT INTO reference (id_reference_subtype, id_studentcard, book_number, date_start, date_finish, date_get, url)" +
                       "VALUES (:idRefType, :idSc, :bookNumber, :dateOfStart, :dateOfFinish, :dateOfGet, :url)\n" +
                       "RETURNING id_reference";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idRefType", reference.getRefType())
         .setParameter("idSc", reference.getIdStudentcard())
         .setParameter("bookNumber", reference.getBooknumber())
         .setParameter("bookNumber", reference.getBooknumber())
         .setDate("dateOfStart", reference.getDateStart())
         .setDate("dateOfFinish", reference.getDateFinish())
         .setDate("dateOfGet", reference.getDateGet())
         .setParameter("url", reference.getUrl());
        List<BigInteger> list = (List<BigInteger>) getList(q);
        return list.size() == 0 ? null : list.get(0).longValue();
    }

    public boolean updateReference (ReferenceModel reference) {
        String query =
                "UPDATE reference \n" + "SET book_number = :bookNumber," + " date_start = :dateStart," + " date_finish = :dateFinish," +
                " url = :url\n" + "WHERE id_reference = :idRef\n" + "\n";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idRef", reference.getIdRef())
         .setParameter("bookNumber", reference.getBooknumber())
         .setDate("dateStart", reference.getDateStart())
         .setDate("dateFinish", reference.getDateFinish())
         .setParameter("url", reference.getUrl());
        return executeUpdate(q);
    }

    public boolean deleteReference (long idRef) {
        String query = "DELETE FROM reference WHERE id_reference = :idRef";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idRef", idRef);
        return executeUpdate(q);
    }

    public List<ExcelReportModel> getExcelReport () {
        String query = "SELECT\n" + " DISTINCT hf.family||' '||hf.name||' '||hf.patronymic AS fio,\n" + "  rf.book_number AS regNumber,\n" +
                       "  dg.groupname AS groupname,\n" + "  rf.date_start AS dateStart,\n" + "  rf.date_finish AS dateFinish,\n" +
                       "  rs.name AS typeOfReference,\n" + "  loss.first_date AS firstDate,\n" + "  loss.second_date AS secondDate,\n" +
                       "  ot.id_order_type AS orderType,\n" + "  oh.id_order_head AS idOrder\n" + "FROM\n" + "  studentcard sc\n" +
                       "  LEFT JOIN reference rf ON rf.id_studentcard = sc.id_studentcard\n" +
                       "  INNER JOIN humanface hf ON sc.id_humanface = hf.id_humanface\n" +
                       "  INNER JOIN dic_group dg ON sc.id_current_dic_group = dg.id_dic_group\n" +
                       "  INNER JOIN curriculum cur USING (id_curriculum)\n" +
                       "  LEFT JOIN reference_subtype rs ON rf.id_reference_subtype=rs.id_reference_subtype\n" +
                       "  INNER JOIN student_semester_status sss ON sss.id_studentcard = sc.id_studentcard\n" +
                       "  LEFT JOIN link_order_student_status loss ON loss.id_student_semester_status = sss.id_student_semester_status\n" +
                       "  LEFT JOIN link_order_section los ON los.id_link_order_section = loss.id_link_order_section\n" +
                       "  LEFT JOIN order_section os ON los.id_order_section = os.id_order_section\n" +
                       "  LEFT JOIN order_head oh ON oh.id_order_head = los.id_order_head\n" +
                       "  LEFT JOIN order_rule ordr ON ordr.id_order_rule = oh.id_order_rule\n" +
                       "  LEFT JOIN order_type ot ON ot.id_order_type = ordr.id_order_type\n" + "WHERE\n" +
                       "  (sc.is_sirota = 1 OR rf.date_finish >= now() OR (sc.is_invalid = 1 AND ((date_finish IS NOT NULL AND date_finish > now())OR(date_finish IS NULL))))" +
                       "  AND (ot.id_order_type = 5 OR ot.id_order_type = 6 OR ot.id_order_type IS NULL)\n" +
                       "  AND dg.dateofend > now()\n" + "  AND cur.formofstudy = 1\n" +
                       "  AND (os.id_order_section NOT IN (60,62,67,68,69,70) OR os.id_order_section IS NULL) " +
                       "  AND (rf.book_number IS NULL OR rf.book_number != '0')";

        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("regNumber")
                              .addScalar("groupname")
                              .addScalar("dateStart")
                              .addScalar("dateFinish")
                              .addScalar("typeOfReference")
                              .addScalar("firstDate")
                              .addScalar("secondDate")
                              .addScalar("orderType", LongType.INSTANCE)
                              .addScalar("idOrder", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(ExcelReportModel.class));
        return (List<ExcelReportModel>) getList(q);
    }
}

