package org.edec.teacher.manager;

import org.edec.dao.DAO;
import org.edec.teacher.model.register.RatingModel;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RegisterType;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by antonskripacev on 24.02.17.
 */
public class EntityManagerRegister extends DAO {
    public List<BigInteger> getListRegisterIdsBySubject(Long idLGSS, FormOfControlConst foc, RegisterType registerType) {
        String query = "select \n" + "\treg.id_register \n" + "from \n" + "\tregister reg\n" +
                       "\tinner join sessionratinghistory srh using(id_register)\n" +
                       "\tinner join sessionrating sr using(id_sessionrating)\n" +
                       "\tinner join student_semester_status sss using(id_student_semester_status)\n" +
                       "\tinner join link_group_semester lgs using(id_link_group_semester)\n" +
                       "\tinner join dic_group dg using(id_dic_group)\n" +
                       "\tinner join link_group_semester_subject lgss ON lgs.id_link_group_semester = lgss.id_link_group_semester and sr.id_subject = lgss.id_subject\n" +
                       "where \n" + "\tid_link_group_semester_subject = :idLGSS\n" +
                       "\tand" + getPartFocCondition(foc) + "\n" +
                       "\tand" + getPartTypeRegisterCondition(registerType) + "\n" +
                       "group by id_register";

        return getSession().createSQLQuery(query).setParameter("idLGSS", idLGSS).list();
    }

    public List<RatingModel> getListRatingsByIdRegister(Long idRegister) {
        String query = "SELECT " +
                       "    LGS.id_semester AS idSemester, " +
                       "    SR.id_sessionrating AS idSessionRating, " +
                       "    HF.family || ' ' || HF.name || ' ' || HF.patronymic AS studentFIO, " +
                       "    DG.groupname AS groupName, " +
                       "    SSS.is_deducted AS deductedStatus, " +
                       "    SC.recordbook AS recordbookNumber, " +
                       "    SSS.is_academicleave AS academicLeaveStatus," +
                       "    reg.certnumber AS certNumber," +
                       "    sbj.hourscount AS hoursCount," +
                       "    reg.otherdbid AS idRegisterMine," +
                       "    reg.is_canceled AS isCanceled," +
                       "    reg.signatorytutor AS signatoryTutor," +
                       "    reg.synchstatus AS synchStatus," +
                       "    reg.thumbprint AS thumbPrint," +
                       "    DSBJ.subjectname AS subjectName," +
                       "    case " +
                       "            when SRH.is_exam = 1 then sr.examrating\n" +
                       "            when SRH.is_pass = 1 then sr.passrating\n" +
                       "            when SRH.is_coursework = 1 then sr.courseworkrating\n" +
                       "            when SRH.is_courseproject = 1 then sr.courseprojectrating\n" +
                       "            when SRH.is_practic = 1 then sr.practicrating\n" +
                       "    end as currentRating, " +
                       "    case " +
                       "            when SRH.is_exam = 1 then " + FormOfControlConst.EXAM.getValue() + "\n" +
                       "            when SRH.is_pass = 1 then " + FormOfControlConst.PASS.getValue() + "\n" +
                       "            when SRH.is_coursework = 1 then " + FormOfControlConst.CW.getValue() + "\n" +
                       "            when SRH.is_courseproject = 1 then " + FormOfControlConst.CP.getValue() + "\n" +
                       "            when SRH.is_practic = 1 then " + FormOfControlConst.PRACTIC.getValue() + "\n" +
                       "    end as foc, " +
                       "    SR.status, " +
                       "    SR.type, " +
                       "    SR.is_notactual as notActual, " +
                       "    SR.statusbegindate AS statusBeginDate," +
                       "    SR.statusfinishdate AS statusFinishDate," +
                       "    case " +
                       "            when SRH.is_exam = 1 then LGSS.examdate\n" +
                       "            when SRH.is_pass = 1 then LGSS.passdate\n" +
                       "            when SRH.is_coursework = 1 then LGSS.tmpcourseworkdate\n" +
                       "            when SRH.is_courseproject = 1 then LGSS.tmpcourseprojectdate\n" +
                       "            when SRH.is_practic = 1 then LGSS.practicdate\n" +
                       "    end as completionDate, " +
                       "    SR.esocourseprojecttheme AS courseProjectTheme,"
                       + "    SR.esocourseworktheme AS courseWorkTheme, "
                       + "    LGS.course AS course,"
                       + "    DG.id_dic_group AS idDicGroup,"
                       + "    SC.id_current_dic_group AS idCurrentDicGroup,"
                       + "    SRH.newrating AS newRating,"
                       + "    SRH.retake_count AS retakeCount,"
                       + "    REG.id_register AS idRegister,"
                       + "    REG.register_url AS registerUrl,"
                       + "    REG.register_number AS registerNumber,"
                       + "    SRH.changedatetime AS changeDateTime,"
                       + "    SRH.id_sessionratinghistory AS idSessionRatingHistory,"
                       + "    SRH.signDate AS signDate,"
                       + "    SSS.id_student_semester_status as idStudentSemesterStatus,"
                       + "    SRH.is_signed_backdate as signedBackDate "
                       + " FROM "
                       + "    sessionrating SR "
                       + "    INNER JOIN subject SBJ ON SR.id_subject = SBJ.id_subject "
                       + "    INNER JOIN dic_subject DSBJ ON DSBJ.id_dic_subject = SBJ.id_dic_subject "
                       + "    INNER JOIN student_semester_status SSS ON SSS.id_student_semester_status = SR.id_student_semester_status "
                       + "    INNER JOIN link_group_semester_subject LGSS ON LGSS.id_link_group_semester = SSS.id_link_group_semester AND LGSS.id_subject = SR.id_subject "
                       + "    INNER JOIN studentcard SC ON SC.id_studentcard = SSS.id_studentcard "
                       + "    INNER JOIN humanface HF ON HF.id_humanface = SC.id_humanface "
                       + "    INNER JOIN link_group_semester LGS ON LGS.id_link_group_semester = SSS.id_link_group_semester "
                       + "    INNER JOIN dic_group DG ON DG.id_dic_group = LGS.id_dic_group "
                       + "    INNER JOIN sessionratinghistory srh ON srh.id_sessionrating = sr.id_sessionrating"
                       + "    INNER JOIN register reg using(id_register) "
                       + "WHERE "
                       + " reg.id_register = :idRegister"
                       + " ORDER BY HF.family , SC.id_studentcard, SSS.id_student_semester_status, SRH.id_sessionratinghistory";

        Query q = getSession()
                .createSQLQuery(query)
                .addScalar("idSemester", LongType.INSTANCE).addScalar("idSessionRating", LongType.INSTANCE)
                .addScalar("studentFIO").addScalar("groupName").addScalar("deductedStatus", BooleanType.INSTANCE)
                .addScalar("recordbookNumber").addScalar("academicLeaveStatus", BooleanType.INSTANCE)
                .addScalar("currentRating").addScalar("status").addScalar("type").addScalar("notActual", BooleanType.INSTANCE)
                .addScalar("statusBeginDate").addScalar("statusFinishDate").addScalar("completionDate").addScalar("courseProjectTheme")
                .addScalar("courseWorkTheme").addScalar("course").addScalar("idDicGroup", LongType.INSTANCE)
                .addScalar("idCurrentDicGroup", LongType.INSTANCE).addScalar("newRating").addScalar("retakeCount")
                .addScalar("idRegister", LongType.INSTANCE).addScalar("registerUrl").addScalar("registerNumber")
                .addScalar("changeDateTime").addScalar("idSessionRatingHistory", LongType.INSTANCE).addScalar("signDate")
                .addScalar("idStudentSemesterStatus", LongType.INSTANCE).addScalar("signedBackDate", BooleanType.INSTANCE)
                .addScalar("certNumber").addScalar("hoursCount").addScalar("idRegisterMine", LongType.INSTANCE)
                .addScalar("isCanceled", BooleanType.INSTANCE).addScalar("signatoryTutor").addScalar("foc")
                .addScalar("subjectName").addScalar("synchStatus", IntegerType.INSTANCE).addScalar("thumbPrint")
                .setParameter("idRegister", idRegister)
                .setResultTransformer(Transformers.aliasToBean(RatingModel.class));
        return (List<RatingModel>) getList(q);
    }

    public List<RatingModel> getListRatingsBySubjectAndType(Long idLGSS, FormOfControlConst foc, RegisterType type) {
        String query = "SELECT " +
                       "    LGS.id_semester AS idSemester, " +
                       "    SR.id_sessionrating AS idSessionRating, " +
                       "    HF.family || ' ' || HF.name || ' ' || HF.patronymic AS studentFIO, " +
                       "    DG.groupname AS groupName, " +
                       "    SSS.is_deducted AS deductedStatus, " +
                       "    SC.recordbook AS recordbookNumber, " +
                       "    SSS.is_academicleave AS academicLeaveStatus," +
                       "    reg.certnumber AS certNumber," +
                       "    sbj.hourscount AS hoursCount," +
                       "    reg.otherdbid AS idRegisterMine," +
                       "    reg.is_canceled AS isCanceled," +
                       "    reg.signatorytutor AS signatoryTutor," +
                       "    reg.synchstatus AS synchStatus," +
                       "    reg.thumbprint AS thumbPrint," +
                       "    DSBJ.subjectname AS subjectName," +
                       "    case " +
                       "            when SRH.is_exam = 1 then sr.examrating\n" +
                       "            when SRH.is_pass = 1 then sr.passrating\n" +
                       "            when SRH.is_coursework = 1 then sr.courseworkrating\n" +
                       "            when SRH.is_courseproject = 1 then sr.courseprojectrating\n" +
                       "            when SRH.is_practic = 1 then sr.practicrating\n" +
                       "    end as currentRating, " +
                       "    case " +
                       "            when SRH.is_exam = 1 then " + FormOfControlConst.EXAM.getValue() + "\n" +
                       "            when SRH.is_pass = 1 then " + FormOfControlConst.PASS.getValue() + "\n" +
                       "            when SRH.is_coursework = 1 then " + FormOfControlConst.CW.getValue() + "\n" +
                       "            when SRH.is_courseproject = 1 then " + FormOfControlConst.CP.getValue() + "\n" +
                       "            when SRH.is_practic = 1 then " + FormOfControlConst.PRACTIC.getValue() + "\n" +
                       "    end as foc, " +
                       "    SR.status, " +
                       "    SR.type, " +
                       "    SR.is_notactual as notActual, " +
                       "    SR.statusbegindate AS statusBeginDate," +
                       "    SR.statusfinishdate AS statusFinishDate," +
                       "    case " +
                       "            when SRH.is_exam = 1 then LGSS.examdate\n" +
                       "            when SRH.is_pass = 1 then LGSS.passdate\n" +
                       "            when SRH.is_coursework = 1 then LGSS.tmpcourseworkdate\n" +
                       "            when SRH.is_courseproject = 1 then LGSS.tmpcourseprojectdate\n" +
                       "            when SRH.is_practic = 1 then LGSS.practicdate\n" +
                       "    end as completionDate, " +
                       "    SR.esocourseprojecttheme AS courseProjectTheme,"
                       + "    SR.esocourseworktheme AS courseWorkTheme, "
                       + "    LGS.course AS course,"
                       + "    DG.id_dic_group AS idDicGroup,"
                       + "    SC.id_current_dic_group AS idCurrentDicGroup,"
                       + "    SRH.newrating AS newRating,"
                       + "    SRH.retake_count AS retakeCount,"
                       + "    REG.id_register AS idRegister,"
                       + "    REG.register_url AS registerUrl,"
                       + "    REG.register_number AS registerNumber,"
                       + "    SRH.changedatetime AS changeDateTime,"
                       + "    SRH.id_sessionratinghistory AS idSessionRatingHistory,"
                       + "    SRH.signDate AS signDate,"
                       + "    SSS.id_student_semester_status as idStudentSemesterStatus,"
                       + "    SRH.is_signed_backdate as signedBackDate "
                       + " FROM "
                       + "    sessionrating SR "
                       + "    INNER JOIN subject SBJ ON SR.id_subject = SBJ.id_subject "
                       + "    INNER JOIN dic_subject DSBJ ON DSBJ.id_dic_subject = SBJ.id_dic_subject "
                       + "    INNER JOIN student_semester_status SSS ON SSS.id_student_semester_status = SR.id_student_semester_status "
                       + "    INNER JOIN link_group_semester_subject LGSS ON LGSS.id_link_group_semester = SSS.id_link_group_semester AND LGSS.id_subject = SR.id_subject "
                       + "    INNER JOIN studentcard SC ON SC.id_studentcard = SSS.id_studentcard "
                       + "    INNER JOIN humanface HF ON HF.id_humanface = SC.id_humanface "
                       + "    INNER JOIN link_group_semester LGS ON LGS.id_link_group_semester = SSS.id_link_group_semester "
                       + "    INNER JOIN dic_group DG ON DG.id_dic_group = LGS.id_dic_group "
                       + "    LEFT JOIN sessionratinghistory srh ON srh.id_sessionrating = sr.id_sessionrating and " + getPartFocCondition(foc)
                       + "    LEFT JOIN register reg using(id_register) "
                       + "WHERE "
                       + " lgss.id_link_group_semester_subject = :idLgss "
                       + " and " + (type == RegisterType.MAIN
                                    ? "(" + getPartTypeRegisterCondition(type) + " or SRH.retake_count is null )"
                                    : getPartTypeRegisterCondition(type))
                       + " ORDER BY HF.family, SC.id_studentcard, SSS.id_student_semester_status, SRH.id_sessionratinghistory";

        Query q = getSession()
                .createSQLQuery(query)
                .addScalar("idSemester", LongType.INSTANCE).addScalar("idSessionRating", LongType.INSTANCE)
                .addScalar("studentFIO").addScalar("groupName").addScalar("deductedStatus", BooleanType.INSTANCE)
                .addScalar("recordbookNumber").addScalar("academicLeaveStatus", BooleanType.INSTANCE)
                .addScalar("currentRating").addScalar("status").addScalar("type").addScalar("notActual", BooleanType.INSTANCE)
                .addScalar("statusBeginDate").addScalar("statusFinishDate").addScalar("completionDate").addScalar("courseProjectTheme")
                .addScalar("courseWorkTheme").addScalar("course").addScalar("idDicGroup", LongType.INSTANCE)
                .addScalar("idCurrentDicGroup", LongType.INSTANCE).addScalar("newRating").addScalar("retakeCount")
                .addScalar("idRegister", LongType.INSTANCE).addScalar("registerUrl").addScalar("registerNumber")
                .addScalar("changeDateTime").addScalar("idSessionRatingHistory", LongType.INSTANCE).addScalar("signDate")
                .addScalar("idStudentSemesterStatus", LongType.INSTANCE).addScalar("signedBackDate", BooleanType.INSTANCE)
                .addScalar("certNumber").addScalar("hoursCount").addScalar("idRegisterMine", LongType.INSTANCE)
                .addScalar("isCanceled", BooleanType.INSTANCE).addScalar("signatoryTutor").addScalar("foc")
                .addScalar("subjectName").addScalar("synchStatus", IntegerType.INSTANCE).addScalar("thumbPrint")
                .setParameter("idLgss", idLGSS)
                .setResultTransformer(Transformers.aliasToBean(RatingModel.class));
        return (List<RatingModel>) getList(q);
    }

    private String getPartFocCondition(FormOfControlConst foc) {
        switch (foc) {
            case EXAM:
                return " srh.is_exam = 1 ";
            case PASS:
                return " srh.is_pass = 1 ";
            case CP:
                return " srh.is_courseproject = 1 ";
            case CW:
                return " srh.is_coursework = 1 ";
            case PRACTIC:
                return " srh.is_practic = 1 ";
        }

        return null;
    }

    private String getPartTypeRegisterCondition(RegisterType type) {
        switch (type) {
            case MAIN:
                return " srh.retake_count in (-1,1) ";
            case MAIN_RETAKE:
                return " srh.retake_count in (-2,2) ";
            case INDIVIDUAL_RETAKE:
                return " srh.retake_count in (-4,4) ";
        }

        return null;
    }

    public void updateCPTheme(String theme, long idSessionRating) {
        String query = "update sessionrating set esocourseprojecttheme = '" + theme + "' where id_sessionrating = " + idSessionRating;
        executeUpdate(getSession().createSQLQuery(query));
    }

    public void updateCWTheme(String theme, long idSessionRating) {
        String query = "update sessionrating set esocourseworktheme = '" + theme + "' where id_sessionrating = " + idSessionRating;
        executeUpdate(getSession().createSQLQuery(query));
    }

    public void updateSRHWithDateAndRating(long idSessionRatingHistory, int rating) {
        String query = "update sessionratinghistory set changedatetime = now(), newRating = " + rating + " where id_sessionratinghistory = " + idSessionRatingHistory;
        executeUpdate(getSession().createSQLQuery(query));
    }

    public Long createSRH(boolean exam, boolean pass, boolean cp, boolean cw, boolean practic, int type, String status, int newRating, long idSessionRating, long idSystemUser, int retakeCount) {
        String query = "insert into sessionratinghistory (is_exam, is_pass, is_courseproject, is_coursework, is_practic, " +
                " type, status, newrating, changedatetime, id_sessionrating, id_systemuser, retake_count) values (" +
                (exam ? 1 : 0) + "," +
                (pass ? 1 : 0) + "," +
                (cp ? 1 : 0) + "," +
                (cw ? 1 : 0) + "," +
                (practic ? 1 : 0) + "," +
                type + "," +
                "'" + status + "'," +
                newRating + ","
                + "now()," +
                idSessionRating + "," +
                idSystemUser + "," +
                retakeCount +
                ") RETURNING id_sessionratinghistory";
        List<Long> list = (List<Long>) getList(
                getSession().createSQLQuery(query)
                        .addScalar("id_sessionratinghistory", LongType.INSTANCE));
        return list.size() == 0 ? null : list.get(0);
    }

    public void updateSRH(String status, long idSRH) {
        String query = "update sessionratinghistory set status = '" + status + "' where id_sessionratinghistory = " + idSRH;
        getSession().createSQLQuery(query).executeUpdate();
    }

    public boolean setRegisterNumber(Long idRegister, String tutor, String listSRH, Long idSemester, String suffix) {
        return callFunction("select register_create_or_update(" +
                idRegister + ", " +
                "'" + tutor + "', " +
                "'" + listSRH + "', " +
                idSemester + ", " +
                "'" + suffix + "'" +
                ")");
    }
}