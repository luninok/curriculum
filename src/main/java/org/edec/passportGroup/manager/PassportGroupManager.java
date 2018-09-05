package org.edec.passportGroup.manager;

import org.edec.admin.model.EmployeeModel;
import org.edec.dao.DAO;
import org.edec.passportGroup.model.*;
import org.edec.passportGroup.model.eso.StudentModelESO;
import org.edec.passportGroup.model.eso.SubjectModelESO;
import org.edec.passportGroup.model.eso.SubjectReportModelESO;
import org.edec.passportGroup.model.eso.TeacherModelESO;
import org.edec.register.model.RetakeModel;
import org.edec.register.service.RegisterService;
import org.edec.passportGroup.model.GroupModel;
import org.edec.passportGroup.model.ScholarshipInfo;
import org.edec.passportGroup.model.SemesterModel;
import org.edec.utility.constants.FormOfControlConst;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;

import java.math.BigInteger;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by antonskripacev on 09.04.17.
 */
public class PassportGroupManager extends DAO {

    public List<SemesterModel> getSemesterByParams(long idInstitute, int typeOfSemester, int formOfStudy) {
        String query = "SELECT " + "\tid_semester as idSemester,\n" + "\tformofstudy as formOfStudy,\n" + "\tseason as season,\n" +
                       "\tdateofbegin as dateOfBegin,\n" + "\tdateofend as dateOfEnd\n" + "FROM semester \n" +
                       "INNER JOIN schoolyear using(id_schoolyear)\n" +
                       "WHERE id_semester IN (select id_semester from semester WHERE id_institute = " + idInstitute +
                       (formOfStudy != 3 ? " and formofstudy = " + formOfStudy : "") +
                       (typeOfSemester > 0 ? " and season = " + (typeOfSemester - 1) : "") + ") ORDER BY id_semester DESC";
        Query q = getSession().createSQLQuery(query).addScalar("idSemester", LongType.INSTANCE).addScalar("formOfStudy").addScalar("season")
                              .addScalar("dateOfBegin").addScalar("dateOfEnd")
                              .setResultTransformer(Transformers.aliasToBean(SemesterModel.class));
        return (List<SemesterModel>) getList(q);
    }

    public List<EmployeeModel> getEmployeesBySubject(long idLgss) {
        String query = "select \n" + "hf.family || ' ' || hf.name || ' ' || hf.patronymic as fio,\n" + "emp.id_employee as idEmp\n" +
                       "from link_employee_subject_group lesg\n" + "inner join employee emp using(id_employee)\n" +
                       "inner join humanface hf using(id_humanface)\n" + "where id_link_group_semester_subject = " + idLgss;
        Query q = getSession().createSQLQuery(query).addScalar("idEmp", LongType.INSTANCE).addScalar("fio")
                              .setResultTransformer(Transformers.aliasToBean(EmployeeModel.class));
        return (List<EmployeeModel>) getList(q);
    }

    public List<GroupModel> getGroupsByFilter(long idSemester, int course, String group, boolean isBachelor, boolean isMaster,
                                              boolean isEngineer) {
        String query = "SELECT \n" + "    LGS.id_link_group_semester as idLgs, " + "    LGS.course as course, " +
                       "    LGS.semesternumber as semesterNumber, " + "    DG.groupname as groupName, " +
                       "    DG.id_curriculum as idCurriculum, " + "    CUR.qualification as qualification, " +
                       "    SEM.season as season, " +
                       "    CAST(SUBSTRING(TO_CHAR(SY.dateofbegin, 'YYYY-MM-DD'), 1, 4) AS integer) as dateBegin, " +
                       "    CAST(SUBSTRING(TO_CHAR(SY.dateofend, 'YYYY-MM-DD'), 1, 4) AS integer) as dateEnd, " +
                       "    SEM.season as season " + "FROM" + "    link_group_semester LGS" +
                       "    INNER JOIN dic_group DG ON LGS.id_dic_group = DG.id_dic_group" +
                       "    INNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester" +
                       "    INNER JOIN schoolyear SY ON SY.id_schoolyear = SEM.id_schoolyear" +
                       "    INNER JOIN curriculum CUR ON DG.id_curriculum = CUR.id_curriculum" + " WHERE" + "    LGS.id_semester = " +
                       idSemester + "    AND CUR.qualification IN (" + (isBachelor ? "2" : "null") + ", " + (isMaster ? "3" : "null") +
                       ", " + (isEngineer ? "1" : "null") + ")" + "    AND DG.groupname ILIKE '%" + group + "%' " +
                       (course > 0 ? " AND course = " + String.valueOf(course) : "") + "ORDER BY course, DG.groupname";
        Query q = getSession().createSQLQuery(query).addScalar("idLgs", LongType.INSTANCE).addScalar("idCurriculum", LongType.INSTANCE)
                              .addScalar("course").addScalar("groupName").addScalar("qualification").addScalar("dateBegin")
                              .addScalar("dateEnd").addScalar("season").addScalar("semesterNumber")
                              .setResultTransformer(Transformers.aliasToBean(GroupModel.class));
        return (List<GroupModel>) getList(q);
    }

    public List<StudentModelESO> getStudentsByGroup(long idLgs) {
        String query = "SELECT \n" + "\thf.family as family,\n" + "\thf.name as name,\n" + "\thf.patronymic as patronymic,\n" +
                       "\tdg.id_dic_group as idDicGroup,\n" + "\tsc.id_current_dic_group as idCurrentDicGroup,\n" +
                       "\tsss.is_government_financed as governmentFinanced,\n" + "\tsss.id_student_semester_status as idSSS,\n" +
                       "\tsss.is_deducted as deducted,\n" + "\tsss.is_academicleave as academicLeave,\n" +
                       "\tsss.is_listener as listener,\n" + "\tsr.is_exam as exam,\n" + "\tsr.is_pass as pass,\n" +
                       "\tsr.is_courseproject as cp,\n" + "\tsr.is_coursework as cw,\n" + "\tsr.is_practic as practic,\n" +
                       "\tsr.examrating as examRating,\n" + "\tsr.passrating as passRating,\n" + "\tsr.courseprojectrating as CPRating,\n" +
                       "\tsr.courseworkrating as CWRating,\n" + "\tsr.practicrating as practicRating,\n" +

                       "\tsr.esoexamrating as esoExamRating,\n" + "\tsr.esopassrating as esoPassRating,\n" +
                       "\tsr.esocourseprojectrating as esoCPRating,\n" + "\tsr.esocourseworkrating as esoCWRating,\n" +
                       "\tsr.id_sessionrating as idSR,\n" + "\tsr.id_subject as idSubject,\n" + "\tsr.status as status,\n" +
                       "\tsr.type as type\n" + "FROM \n" + "\tsessionrating sr\n" +
                       "\tINNER JOIN student_semester_status sss USING(id_student_semester_status)\n" +
                       "\tINNER JOIN studentcard sc USING(id_studentcard)\n" + "\tINNER JOIN humanface hf USING(id_humanface)\n" +
                       "\tINNER JOIN link_group_semester lgs USING(id_link_group_semester)\n" +
                       "\tINNER JOIN dic_group dg USING(id_dic_group)\n" + "WHERE \n" + "\tsss.id_link_group_semester = " + idLgs +
                       " order by hf.family, sc.id_studentcard";
        Query q = getSession().createSQLQuery(query).addScalar("idDicGroup", LongType.INSTANCE)
                              .addScalar("idCurrentDicGroup", LongType.INSTANCE).addScalar("idSSS", LongType.INSTANCE)
                              .addScalar("idSR", LongType.INSTANCE).addScalar("idSubject", LongType.INSTANCE).addScalar("family")
                              .addScalar("patronymic").addScalar("name").addScalar("status").addScalar("type")
                              .addScalar("governmentFinanced", BooleanType.INSTANCE).addScalar("exam", BooleanType.INSTANCE)
                              .addScalar("pass", BooleanType.INSTANCE).addScalar("cp", BooleanType.INSTANCE)
                              .addScalar("cw", BooleanType.INSTANCE).addScalar("practic", BooleanType.INSTANCE)
                              .addScalar("academicLeave", BooleanType.INSTANCE).addScalar("listener", BooleanType.INSTANCE)
                              .addScalar("deducted", BooleanType.INSTANCE).addScalar("examRating").addScalar("passRating")
                              .addScalar("CPRating").addScalar("CWRating").addScalar("practicRating").addScalar("esoExamRating")
                              .addScalar("esoPassRating").addScalar("esoCPRating").addScalar("esoCWRating")
                              .setResultTransformer(Transformers.aliasToBean(StudentModelESO.class));
        return (List<StudentModelESO>) getList(q);
    }

    public List<SubjectModelESO> getSubjectsByGroup(long idLgs) {
        String query = "SELECT \n" + "\tsbj.hourscount as hoursCount,\n" + "\tsbj.is_exam as exam,\n" + "\tsbj.is_pass as pass,\n" +
                       "\tsbj.is_courseproject as cp,\n" + "\tsbj.is_coursework as cw,\n" + "\tsbj.is_practic as practic,\n" +
                       "\tlgss.id_subject as idSubject,\n" + "\tlgss.id_link_group_semester_subject as idLgss,\n" +
                       "\tdsbj.subjectname as subjectName\n" + "FROM \n" + "\tlink_group_semester_subject lgss\n" +
                       "\tINNER JOIN subject sbj using(id_subject)\n" + "\tINNER JOIN dic_subject dsbj using(id_dic_subject)\n" +
                       "WHERE \n" + "\tlgss.id_link_group_semester = " + idLgs;
        Query q = getSession().createSQLQuery(query).addScalar("idSubject", LongType.INSTANCE).addScalar("idLgss", LongType.INSTANCE)
                              .addScalar("subjectName").addScalar("hoursCount", DoubleType.INSTANCE).addScalar("exam", BooleanType.INSTANCE)
                              .addScalar("pass", BooleanType.INSTANCE).addScalar("cp", BooleanType.INSTANCE)
                              .addScalar("cw", BooleanType.INSTANCE).addScalar("practic", BooleanType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(SubjectModelESO.class));
        return (List<SubjectModelESO>) getList(q);
    }

    public List<String> getRegistersBySr(Long idSr, FormOfControlConst foc) {
        String subquery = "";

        switch (foc) {
            case EXAM:
                subquery = " and srh.is_exam = 1 ";
                break;
            case PASS:
                subquery = " and srh.is_pass = 1 ";
                break;
            case CP:
                subquery = " and srh.is_courseproject = 1 ";
                break;
            case CW:
                subquery = " and srh.is_coursework = 1 ";
                break;
            case PRACTIC:
                subquery = " and srh.is_practic = 1 ";
                break;
        }
        String query = "SELECT\n" + "\tregister_url\n" + "FROM \n" + "\tsessionratinghistory srh\n" +
                       "\tINNER JOIN register rg using(id_register)\n" + "WHERE\n" +
                       "\tregister_url is not null and register_url <> '' and id_sessionrating = " + idSr + subquery;
        return (List<String>) getList(getSession().createSQLQuery(query));
    }

    // Получение всех предметов у нескольких групп
    public List<SubjectReportModelESO> getSubjectsReport(List<GroupModel> groupList) {
        List<SubjectReportModelESO> allGroups = new ArrayList<>();
        for (GroupModel aGroupList : groupList) {

            String query = "SELECT DISTINCT \n" + "\tdsbj.subjectname as subjectName,\n" + "\tsbj.is_exam as exam,\n" +
                           "\tsbj.is_pass as pass,\n" + "\tsbj.is_courseproject as cp,\n" + "\tsbj.is_coursework as cw,\n" +
                           "\tsbj.is_practic as practic,\n" + "\tsbj.hourscount as hoursCount,\n" +
                           "\tsbj.hoursaudcount as hoursAudCount,\n" + "\tsbj.hourslection as hoursLection,\n" +
                           "\tsbj.hourslabor as hoursLabor,\n" + "\tsbj.hourspractic as hoursPractic,\n" +
                           "\tsbj.id_dic_subject as idDicSubject, \n" + "\tsbj.id_chair as idChair, \n" + "\tsbj.type as type,\n" +
                           "\tsbj.synch_mine as synchMine,\n" + "\tdg.groupname as groupName,\n" + "\tlgss.examdate as examDate,\n" +
                           "\tlgss.passdate as passDate,\n" + "\tlgss.practicdate as practicDate,\n" +
                           "\tlgss.tmpcourseprojectdate as courseProjectDate,\n" + "\tlgss.tmpcourseworkdate as courseWorkDate,\n" +
                           "\tlgss.consultationdate as consultDate,\n" + "\tlesg.id_link_employee_subject_group as idLesg,\n" +
                           "\tlesg.id_employee as idEmployee,\n" + "\thf.family || ' ' || hf.name || ' ' || hf.patronymic as fullName,\n" +
                           "\tinst.fulltitle as instTitle,\n" + "\tdep.fulltitle as depTitle,\n" + "\tlgss.id_subject as idSubject,\n" +
                           "\tlgss.id_link_group_semester_subject as idLgss,\n" + "\tdep2.fulltitle as subjDepTitle,\n" +
                           "\tdep2.otherdbid as otherDbId\n" + "FROM \n" + "\tlink_group_semester lgs\n" +
                           "\tLEFT JOIN link_group_semester_subject lgss using(id_link_group_semester)\n" +
                           "\tLEFT JOIN subject sbj using(id_subject)\n" +
                           "\tLEFT JOIN department dep2 on sbj.id_chair=dep2.id_department\n" +
                           "\tLEFT JOIN dic_subject dsbj using(id_dic_subject)\n" +
                           "\tLEFT JOIN link_employee_subject_group lesg using(id_link_group_semester_subject)\n" +
                           "\tLEFT JOIN dic_group dg using(id_dic_group)\n" + "\tLEFT JOIN employee emp using(id_employee)\n" +
                           "\tLEFT JOIN humanface hf using(id_humanface)\n" +
                           "\tLEFT JOIN link_employee_department led using(id_employee)\n" +
                           "\tLEFT JOIN institute inst on dg.id_institute=inst.id_institute\n" +
                           "\tLEFT JOIN department dep on led.id_department=dep.id_department\n" + "WHERE \n" +
                           "\tlgss.id_link_group_semester = " + aGroupList.getIdLgs() + " \n" +
                           "\tAND ((led.id_link_employee_department IS NOT NULL AND dep.fulltitle IS NOT NULL) OR ((led.id_link_employee_department IS NULL AND dep.fulltitle IS NULL))) \n" +
                           "ORDER BY dsbj.subjectname";

            Query q = getSession().createSQLQuery(query).addScalar("idLesg", LongType.INSTANCE).addScalar("idSubject", LongType.INSTANCE)
                                  .addScalar("idLgss", LongType.INSTANCE).addScalar("idEmployee", LongType.INSTANCE)
                                  .addScalar("subjectName").addScalar("idDicSubject", LongType.INSTANCE).addScalar("fullName")
                                  .addScalar("instTitle").addScalar("depTitle").addScalar("subjDepTitle")
                                  .addScalar("otherDbId", LongType.INSTANCE).addScalar("exam", BooleanType.INSTANCE)
                                  .addScalar("pass", BooleanType.INSTANCE).addScalar("cp", BooleanType.INSTANCE)
                                  .addScalar("cw", BooleanType.INSTANCE).addScalar("practic", BooleanType.INSTANCE).addScalar("hoursCount")
                                  .addScalar("hoursAudCount").addScalar("hoursLection").addScalar("hoursLabor").addScalar("hoursPractic")
                                  .addScalar("groupName").addScalar("type").addScalar("synchMine", BooleanType.INSTANCE)
                                  .addScalar("examDate", DateType.INSTANCE).addScalar("passDate", DateType.INSTANCE)
                                  .addScalar("practicDate", DateType.INSTANCE).addScalar("courseProjectDate", DateType.INSTANCE)
                                  .addScalar("courseWorkDate", DateType.INSTANCE).addScalar("consultDate", DateType.INSTANCE)
                                  .addScalar("idChair", LongType.INSTANCE)
                                  .setResultTransformer(Transformers.aliasToBean(SubjectReportModelESO.class));
            List<SubjectReportModelESO> list = (List<SubjectReportModelESO>) getList(q);
            allGroups.addAll(list);
        }
        return allGroups;
    }

    // Получение всех преподавателей
    public List<TeacherModelESO> getTeachers() {
        String query = "SELECT \n" + "\tlesg.id_link_employee_subject_group AS idLesg,\n" + "\temp.id_employee AS idTeacher,\n" +
                       "\thf.family || ' ' || hf.name || ' ' || hf.patronymic AS fullName,\n" + "\tinst.fulltitle AS instTitle,\n" +
                       "\tdep.fulltitle AS depTitle\n" + "FROM \n" + "\tlink_group_semester lgs\n" +
                       "\tINNER JOIN link_group_semester_subject lgss USING(id_link_group_semester)\n" +
                       "\tINNER JOIN subject sbj USING(id_subject)\n" + "\tINNER JOIN dic_subject dsbj USING(id_dic_subject)\n" +
                       "\tINNER JOIN link_employee_subject_group lesg USING(id_link_group_semester_subject)\n" +
                       "\tINNER JOIN dic_group dg USING(id_dic_group)\n" + "\tINNER JOIN employee emp USING(id_employee)\n" +
                       "\tINNER JOIN humanface hf USING(id_humanface)\n" +
                       "\tINNER JOIN link_employee_department led USING(id_employee)\n" +
                       "\tINNER JOIN institute inst USING(id_institute)\n" + "\tINNER JOIN department dep USING(id_department)\n" +
                       "ORDER BY emp.id_employee";
        Query q = getSession().createSQLQuery(query).addScalar("idLesg", LongType.INSTANCE).addScalar("idTeacher", LongType.INSTANCE)
                              .addScalar("fullName").addScalar("instTitle").addScalar("depTitle")
                              .setResultTransformer(Transformers.aliasToBean(TeacherModelESO.class));
        return (List<TeacherModelESO>) getList(q);
    }

    // Прикрепить преподавателя к предмету
    public boolean addTeacher(Long idTeacher, Long idLgss) {
        String query = "INSERT INTO link_employee_subject_group \n" + "\t(id_employee, id_link_group_semester_subject) " + "VALUES \n" +
                       "\t(:idTeacher, :idLgss)";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idTeacher", idTeacher).setLong("idLgss", idLgss);
        return executeUpdate(q);
    }

    // Отвязать преподавателя от предмета
    public boolean removeTeacherFromSubject(Long idLesg) {
        String query = "DELETE FROM\n" + "\tlink_employee_subject_group\n" + "WHERE\n" + "\tid_link_employee_subject_group = :idLesg";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idLesg", idLesg);
        return executeUpdate(q);
    }

    public List<ScholarshipInfo> getScholarshipInfoByStudentIds(String ids) {
        String query = "select \n" + "\toa.date_start as dateScholarshipBegin,\n" + "\toa.date_finish as dateScholarshipEnd,\n" +
                       "\toa.id_dic_action as idDicAction,\n" + "\tos.name as sectionName,\n" +
                       "\tsc.id_current_dic_group as idCurDicGroup,\n" + "\tdg.id_dic_group as idDicGroup,\n" +
                       "\tdg.id_institute as idInstitute,\n" + "\tsss.id_studentcard as idStudentcard,\n" +
                       "\tlgs.id_semester as idSemester,\n" + "\tsss.sessionresult as sessionResult,\n" + "\t(\n" +
                       "\t\tselect is_deducted from student_semester_status sss2\n" +
                       "\t\tinner join link_group_semester lgs2 using(id_link_group_semester)\n" +
                       "\t\tinner join semester sem2 using(id_semester)\n" +
                       "                where lgs2.id_dic_group = sc.id_current_dic_group and sss2.id_studentcard = sss.id_studentcard and sem2.is_current_sem = 1\n" +
                       "        ) as deductedCurSem,\n" + "        sss.id_student_semester_status as idSSS,\n" + "\t(\n" +
                       "\t\tselect is_academicleave from student_semester_status sss2\n" +
                       "\t\tinner join link_group_semester lgs2 using(id_link_group_semester)\n" +
                       "\t\tinner join semester sem2 using(id_semester)\n" +
                       "                where lgs2.id_dic_group = sc.id_current_dic_group and sss2.id_studentcard = sss.id_studentcard and sem2.is_current_sem = 1\n" +
                       "        ) as academicLeaveCurSem,\n" + "        sss.is_government_financed as governmentFinanced,\n" +
                       "        sss.is_deducted as deducted,\n" + "        sss.is_academicleave as academicLeave,\n" + "        (\n" +
                       "\t\tselect is_government_financed from student_semester_status sss2\n" +
                       "\t\tinner join link_group_semester lgs2 using(id_link_group_semester)\n" +
                       "\t\tinner join semester sem2 using(id_semester)\n" +
                       "                where lgs2.id_dic_group = sc.id_current_dic_group and sss2.id_studentcard = sss.id_studentcard and sem2.is_current_sem = 1\n" +
                       "        ) as nextGovernmentFinanced,\n" + "        sss.date_complete_session as dateCompleteSession\n" + "from \n" +
                       "\tstudent_semester_status sss\n" + "\tinner join link_group_semester lgs using(id_link_group_semester)\n" +
                       "\tleft join order_action oa on sss.id_studentcard = oa.id_studentcard and (oa.id_order_action is null or (oa.id_semester = lgs.id_semester and oa.id_dic_action in (1,2)))\n" +
                       "\tleft join link_order_student_status loss using(id_link_order_student_status)\n" +
                       "\tleft join link_order_section los using(id_link_order_section)\n" +
                       "\tleft join order_section os using(id_order_section)\n" + "\tinner join dic_group dg using(id_dic_group)\n" +
                       "\tinner join studentcard sc on sc.id_studentcard = sss.id_studentcard\n" + "where\n" +
                       "\tsss.id_student_semester_status in (" + ids + ")\n" + "\torder by date_action";

        Query q = getSession().createSQLQuery(query).addScalar("idCurDicGroup", LongType.INSTANCE) //
                              .addScalar("idDicGroup", LongType.INSTANCE) //
                              .addScalar("idDicAction", LongType.INSTANCE) //
                              .addScalar("idSSS", LongType.INSTANCE).addScalar("idInstitute", LongType.INSTANCE)//
                              .addScalar("idStudentcard", LongType.INSTANCE).addScalar("deductedCurSem", BooleanType.INSTANCE)
                              .addScalar("deducted", BooleanType.INSTANCE).addScalar("academicLeaveCurSem", BooleanType.INSTANCE)
                              .addScalar("academicLeave", BooleanType.INSTANCE).addScalar("nextGovernmentFinanced", BooleanType.INSTANCE)
                              .addScalar("governmentFinanced", BooleanType.INSTANCE).addScalar("idSemester", LongType.INSTANCE)
                              .addScalar("dateScholarshipBegin") //
                              .addScalar("dateScholarshipEnd") //
                              .addScalar("dateCompleteSession").addScalar("sessionResult").addScalar("sectionName") //
                              .setResultTransformer(Transformers.aliasToBean(ScholarshipInfo.class));
        return (List<ScholarshipInfo>) getList(q);
    }

    public boolean cancelScholarship(ScholarshipInfo scholarshipInfo, Date dateCancel, String orderNumber, Long idHumanface) {
        try {
            begin();

            String query = "insert into order_action (id_studentcard, id_user, id_dic_action, \n" +
                           "\t\t\t  id_dic_group_from, id_institute_from, id_semester, date_action,\n" +
                           "\t\t\t  date_start, order_number) \n" + "values " + "( " + scholarshipInfo.getIdStudentcard() + ", " +
                           idHumanface + ", " + " 2," + scholarshipInfo.getIdDicGroup() + "," + scholarshipInfo.getIdInstitute() + "," +
                           scholarshipInfo.getIdSemester() + "," + "'" + dateCancel + "'," + "'" + dateCancel + "', " + "'" + orderNumber +
                           "'" + ")";

            getSession().createSQLQuery(query).executeUpdate();

            getSession().createSQLQuery("update student_semester_status set is_get_academic = 0 where id_student_semester_status = " +
                                        scholarshipInfo.getIdSSS()).executeUpdate();

            commit();

            return true;
        } catch (HibernateException e) {
            e.printStackTrace();
            rollback();
            return false;
        } finally {
            close();
        }
    }

    public boolean setScholarship(ScholarshipInfo scholarshipInfo, Date dateSet, Date dateStart, Date dateFinish, String orderNumber,
                                  Long idHumanface) {
        try {
            begin();

            String query = "insert into order_action (id_studentcard, id_user, id_dic_action, \n" +
                           "\t\t\t  id_dic_group_from, id_institute_from, id_semester, date_action,\n" +
                           "\t\t\t  date_start, date_finish, order_number) \n" + "values " + "( " + scholarshipInfo.getIdStudentcard() +
                           ", " + idHumanface + ", " + " 1," + scholarshipInfo.getIdDicGroup() + "," + scholarshipInfo.getIdInstitute() +
                           "," + scholarshipInfo.getIdSemester() + "," + "'" + dateSet + "'," + "'" + dateStart + "', " + "'" + dateFinish +
                           "', " + "'" + orderNumber + "'" + ")";

            getSession().createSQLQuery(query).executeUpdate();

            getSession().createSQLQuery("update student_semester_status set is_get_academic = 1 where id_student_semester_status = " +
                                        scholarshipInfo.getIdSSS()).executeUpdate();

            commit();

            return true;
        } catch (HibernateException e) {
            e.printStackTrace();
            rollback();
            return false;
        } finally {
            close();
        }
    }

    public boolean createRetakeForModel(FormOfControlConst foc, RetakeModel retakeModel, int typeRetake, Date dateOfBegin, Date dateOfEnd) {
        try {
            begin();

            String strDateOfBegin = new SimpleDateFormat("yyyy-MM-dd").format(dateOfBegin);
            String strDateOfEnd = new SimpleDateFormat("yyyy-MM-dd").format(dateOfEnd);

            String strRetake = typeRetake == RegisterService.MAIN_RETAKE ? "-2" : "-4";

            String query = "insert into sessionratinghistory " + "(retake_count, " + "changedatetime, " + "is_courseproject, " +
                           "is_coursework, " + "is_exam, " + "is_pass, " + "is_practic, " + "newrating, " + "oldrating, " + "status, " +
                           "type, " + "id_sessionrating, " +
                           //TODO по идее, можно отказываться от этого поля
                           "id_systemuser) values (" + strRetake + ", " + "now(), " + (foc == FormOfControlConst.CP ? "1, " : "0, ") +
                           (foc == FormOfControlConst.CW ? "1, " : "0, ") + (foc == FormOfControlConst.EXAM ? "1, " : "0, ") +
                           (foc == FormOfControlConst.PASS ? "1, " : "0, ") + (foc == FormOfControlConst.PRACTIC ? "1, " : "0, ") + "0," +
                           "0," + "'1.5.1'," + (retakeModel.getType() == null ? retakeModel.getType().toString() : 0) + ", " +
                           retakeModel.getIdSR() + ", " +
                           //TODO по идее, можно отказываться от этого поля
                           "17" + ")";

            getSession().createSQLQuery(query).executeUpdate();

            query = "update sessionrating set " + "status = '1.5.1', " + "statusbegindate = '" + strDateOfBegin + "', " +
                    "statusfinishdate = '" + strDateOfEnd + "' " + "where id_sessionrating = " + retakeModel.getIdSR();

            getSession().createSQLQuery(query).executeUpdate();

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

    public boolean createSRH(RetakeModel retakeModel, FormOfControlConst foc, int newRating, int oldRating, Long userHumanfaceId) {
        String query =
                "INSERT INTO sessionratinghistory \n" + "(retake_count, " + "changedatetime, " + "is_courseproject, " + "is_coursework, " +
                "is_exam, " + "is_pass, " + "is_practic, " + "newrating, " + "oldrating, " + "status, " + "type, " + "id_sessionrating, " +
                "id_systemuser," + "id_humanface_last_action) values (" + "5, " + "now(), " +
                (foc == FormOfControlConst.CP ? "1, " : "0, ") + (foc == FormOfControlConst.CW ? "1, " : "0, ") +
                (foc == FormOfControlConst.EXAM ? "1, " : "0, ") + (foc == FormOfControlConst.PASS ? "1, " : "0, ") +
                (foc == FormOfControlConst.PRACTIC ? "1, " : "0, ") + newRating + ", " + oldRating + ", " + "'1.5.1'," +
                (retakeModel.getType() == null ? retakeModel.getType().toString() : 0) + ", " + retakeModel.getIdSR() + ", " + "17" + "," +
                userHumanfaceId + ")";
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean deleteSRH(long idSR) {
        String query = "DELETE FROM sessionratinghistory WHERE id_sessionratinghistory " +
                       "IN (SELECT id_sessionratinghistory FROM sessionratinghistory WHERE id_sessionrating = :idSR ORDER BY changedatetime DESC LIMIT 1)";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idSR", idSR);
        return executeUpdate(q);
    }

    public boolean updateSR(RetakeModel retakeModel, FormOfControlConst foc, Integer newRating) {
        String query = "UPDATE sessionrating SET \n" + "retake_count = 5," +
                       ((newRating < 0 || newRating == 2) ? "eso" : "") +
                       (foc == FormOfControlConst.EXAM ? "examrating = " + newRating + "," : "") +
                       (foc == FormOfControlConst.PASS ? "passrating = " + newRating + "," : "") +
                       (foc == FormOfControlConst.CP ? "courseprojectrating = " + newRating + "," : "") +
                       (foc == FormOfControlConst.CW ? "courseworkrating = " + newRating + "," : "") +
                       (foc == FormOfControlConst.PRACTIC ? "practicrating = " + newRating + "," : "") + "status = '1.5.1' \n" +
                       "WHERE id_sessionrating = " + retakeModel.getIdSR();

        return executeUpdate(getSession().createSQLQuery(query));
    }

    public long getGroupByIdLgss(long idLgss) {
        String query = "SELECT \n" + "\tLGS.id_dic_group AS idDg, \n" + "\tDG.id_institute AS idInst \n" + "FROM \n" +
                       "\tlink_group_semester_subject LGSS\n" + "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN dic_group DG USING (id_dic_group)\n" + "WHERE \n" + "\tid_link_group_semester_subject = :idLgss\n";
        Query q = getSession().createSQLQuery(query).addScalar("idDg", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(org.edec.teacher.model.registerRequest.GroupModel.class));
        q.setLong("idLgss", idLgss);
        List<org.edec.teacher.model.registerRequest.GroupModel> list = (List<org.edec.teacher.model.registerRequest.GroupModel>) getList(q);
        return list.size() == 0 ? -1 : list.get(0).getIdDg();
    }

    public Integer getRetakeCountBySR(long idSR) {
        String query = "SELECT \n" + "\tSRH.retake_count AS retakeCount\n" + "FROM \n" + "\tsessionratinghistory SRH\n" +
                       "\tWHERE id_sessionrating = " + idSR + " ORDER BY changedatetime DESC LIMIT 1\n";
        Query q = getSession().createSQLQuery(query);
        List<Integer> list = (List<Integer>) getList(q);
        return list.size() == 0 ? -1 : list.get(0);
    }

    public Integer countSR(long idLGSS) {
        String query = "SELECT COUNT(srh.*) from link_group_semester_subject lgss " +
                       "INNER JOIN link_group_semester lgs ON lgss.id_link_group_semester = lgs.id_link_group_semester " +
                       "INNER JOIN student_semester_status sss ON sss.id_link_group_semester = lgs.id_link_group_semester " +
                       "INNER JOIN sessionrating sr ON sr.id_student_semester_status = sss.id_student_semester_status and sr.id_subject = lgss.id_subject " +
                       "INNER JOIN sessionratinghistory srh ON sr.id_sessionrating = srh.id_sessionrating " +
                       "where lgss.id_link_group_semester_subject = " + idLGSS;

        Query q = getSession().createSQLQuery(query);
        List<BigInteger> list = (List<BigInteger>) getList(q);
        return list.size() == 0 ? -1 : list.get(0).intValue();
    }

    public boolean updateSRH(long idLGSS, String focUpdate) {
        String query = "update sessionratinghistory set " + focUpdate +
                       " where id_sessionrating in (select sr.id_sessionrating from link_group_semester_subject lgss " +
                       "INNER JOIN link_group_semester lgs ON lgss.id_link_group_semester = lgs.id_link_group_semester " +
                       "INNER JOIN student_semester_status sss ON sss.id_link_group_semester = lgs.id_link_group_semester " +
                       "INNER JOIN sessionrating sr ON sr.id_student_semester_status = sss.id_student_semester_status and sr.id_subject = lgss.id_subject " +
                       "where lgss.id_link_group_semester_subject = " + idLGSS + ")";
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean updateSR(long idLGSS, String focUpdate, String markUpdate) {
        String query = "update sessionrating set " + focUpdate + ", " + markUpdate +
                       " where id_sessionrating in (select sr.id_sessionrating from link_group_semester_subject lgss " +
                       "INNER JOIN link_group_semester lgs ON lgss.id_link_group_semester = lgs.id_link_group_semester " +
                       "INNER JOIN student_semester_status sss ON sss.id_link_group_semester = lgs.id_link_group_semester " +
                       "INNER JOIN sessionrating sr ON sr.id_student_semester_status = sss.id_student_semester_status and sr.id_subject = lgss.id_subject " +
                       "where lgss.id_link_group_semester_subject = " + idLGSS + ")";
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean updateSubjectStatus(long idLGSS, String focUpdate) {
        String query = "update subject set " + focUpdate + " where id_subject = " +
                       "(select id_subject from link_group_semester_subject where id_link_group_semester_subject = " + idLGSS + " limit 1)";
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean deleteLESG(long idLGSS) {
        String query = "delete from link_employee_subject_group " + "where id_link_group_semester_subject = " + idLGSS;
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean deleteAttendance(long idLGSS) {
        String query = "delete from attendance " + "where id_link_group_semester_subject = " + idLGSS;
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean deleteSubject(long idLGSS) {
        String query = "delete from subject where id_subject = " +
                       "(select id_subject from link_group_semester_subject where id_link_group_semester_subject = " + idLGSS + " limit 1)";
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean deleteLGSS(long idLGSS) {
        String query = "delete from link_group_semester_subject " + "where id_link_group_semester_subject = " + idLGSS;
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean deleteSrhByLgss(long idLGSS) {
        String query =
                "delete from sessionratinghistory where id_sessionrating in (select sr.id_sessionrating from link_group_semester_subject lgss " +
                "INNER JOIN link_group_semester lgs ON lgss.id_link_group_semester = lgs.id_link_group_semester " +
                "INNER JOIN student_semester_status sss ON sss.id_link_group_semester = lgs.id_link_group_semester " +
                "INNER JOIN sessionrating sr ON sr.id_student_semester_status = sss.id_student_semester_status and sr.id_subject = lgss.id_subject " +
                "where lgss.id_link_group_semester_subject = " + idLGSS + ")";
        return executeUpdate(getSession().createSQLQuery(query));
    }

    //I`m so sorry :(
    public boolean deleteLGScheduleS(long idLGSS) {
        String query = "delete from link_group_schedule_subject where id_link_group_semester_subject = " + idLGSS;
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean deleteRegisterRequest(long idLGSS) {
        String query = "delete from register_request where id_link_group_semester_subject = " + idLGSS;
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean deleteSR(long idLGSS) {
        String query =
                "delete from sessionrating where id_sessionrating in (select sr.id_sessionrating from link_group_semester_subject lgss " +
                "INNER JOIN link_group_semester lgs ON lgss.id_link_group_semester = lgs.id_link_group_semester " +
                "INNER JOIN student_semester_status sss ON sss.id_link_group_semester = lgs.id_link_group_semester " +
                "INNER JOIN sessionrating sr ON sr.id_student_semester_status = sss.id_student_semester_status and sr.id_subject = lgss.id_subject " +
                "where lgss.id_link_group_semester_subject = " + idLGSS + ")";
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public List<SubjectModel> getSubjectList() {
        String query = "SELECT dsbj.subjectname AS subjectName," + "dsbj.id_dic_subject AS idDicSubject FROM dic_subject dsbj";
        Query q = getSession().createSQLQuery(query).addScalar("subjectName").addScalar("idDicSubject", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(SubjectModel.class));
        return (List<SubjectModel>) getList(q);
    }

    public List<DepartmentModel> getDepartmentList() {
        String query = "SELECT dep.otherdbid AS otherDbID,\n" + "dep.id_chair AS idChair,\n" + "dep.fulltitle AS fullTitle \n" +
                       "FROM department dep \n" + "WHERE id_chair IS NOT NULL AND otherdbid IS NOT NULL";
        Query q = getSession().createSQLQuery(query).addScalar("idChair", LongType.INSTANCE).addScalar("otherDbID", LongType.INSTANCE)
                              .addScalar("fullTitle").setResultTransformer(Transformers.aliasToBean(DepartmentModel.class));
        return (List<DepartmentModel>) getList(q);
    }

    public Long createDicSubject(String subjectName) {
        String query = "INSERT INTO dic_subject (subjectname)\n" + " values ('" + subjectName + "')\n" + "RETURNING id_dic_subject";
        Query q = getSession().createSQLQuery(query);
        List<BigInteger> list = (List<BigInteger>) getList(q);
        return list.size() == 0 ? null : list.get(0).longValue();
    }

    public Long createSubject(SubjectModel subjectModel) {
        String query =
                "INSERT INTO subject \n" + "(id_curriculum, " + "id_dic_subject, " + "id_chair," + "hourscount, " + "hoursaudcount, " +
                "hourslection, " + "hourslabor, " + "hourspractic, " + "semesternumber, " + "is_courseproject, " + "is_coursework, " +
                "is_exam, " + "is_pass, " + "is_practic, " + "type, " + "synch_mine," + "is_active) " + "values (" +
                subjectModel.getIdCurriculum() + "," + subjectModel.getIdDicSubject() + "," + subjectModel.getIdChair() + "," +
                subjectModel.getCountHours() + "," + subjectModel.getHoursAudCount() + "," + subjectModel.getHoursLection() + "," +
                subjectModel.getHoursLabor() + "," + subjectModel.getHoursPractic() + "," + subjectModel.getSemesterNumber() + "," +
                (subjectModel.getCp() ? "1" : "0") + "," + (subjectModel.getCw() ? "1" : "0") + "," + (subjectModel.getExam() ? "1" : "0") +
                "," + (subjectModel.getPass() ? "1" : "0") + "," + (subjectModel.getPractic() ? "1" : "0") + "," + subjectModel.getType() +
                "," + (subjectModel.getSynchMine() ? "1" : "0") + "," + (subjectModel.getIsActive() ? "1" : "0") + ")\n" +
                "RETURNING id_subject";
        Query q = getSession().createSQLQuery(query);
        List<BigInteger> list = (List<BigInteger>) getList(q);
        return list.size() == 0 ? null : list.get(0).longValue();
    }

    public boolean createLinkGroupSemesterSubject(LinkGroupSemesterSubjectModel lgss) {
        String query = "INSERT INTO link_group_semester_subject \n" + "(id_link_group_semester, " + "id_subject, " + "printstatus," +
                       "hourscourseproject, " + "hourscoursework, " + "hourscontroldistance, " + "hoursconsult, " +
                       "hourscontrolselfstudy, " + "formexam, " + "lessonscount) " + "values (" + lgss.getIdLGS() + "," +
                       lgss.getIdSubject() + "," + lgss.getPrintStatus() + "," + lgss.getHoursCP() + "," + lgss.getHoursCW() + "," +
                       lgss.getHoursConrolDistance() + "," + lgss.getHoursConsult() + "," + lgss.getHoursControlSelfStudy() + "," +
                       (lgss.getFormExam() ? "1" : "0") + "," + lgss.getLessonCount() + ")";
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public List<Long> getStudentSemesterStatusList(long idLGS) {
        String query = "SELECT\n" + "\tid_student_semester_status\n" + "FROM \n" + "\tstudent_semester_status sss\n" + "WHERE\n" +
                       "\tid_link_group_semester = " + idLGS;
        List<BigInteger> list = (List<BigInteger>) getList(getSession().createSQLQuery(query));
        List<Long> listLong = new ArrayList<>();
        for (BigInteger sss : list) {
            listLong.add(sss.longValue());
        }
        return listLong;
    }

    public boolean createSessionRating(SessionRatingModel sr) {
        String query =
                "INSERT INTO sessionrating \n" + "(type, " + "id_subject, " + "is_courseproject, " + "is_coursework, " + "is_exam, " +
                "is_pass, " + "is_practic, " + "id_student_semester_status," + "esogradecurrent, " + "examrating, " + "passrating, " +
                "courseprojectrating, " + "courseworkrating, " + "practicrating, " + "esogrademax," + "skipcount," + "visitcount," +
                "esoexamrating, " + "esopassrating, " + "esocourseprojectrating, " + "esocourseworkrating, " + "retake_count," +
                "id_dop_eso2," + "attendancecount," + "status," + "is_esostudy) " + "values (" + sr.getType() + "," + sr.getIdSubject() +
                "," + (sr.getCp() ? "1" : "0") + "," + (sr.getCw() ? "1" : "0") + "," + (sr.getExam() ? "1" : "0") + "," +
                (sr.getPass() ? "1" : "0") + "," + (sr.getPractic() ? "1" : "0") + "," + sr.getIdSSS() + "," + sr.getEsoGradeCurrent() +
                "," + sr.getExamRating() + "," + sr.getPassRating() + "," + sr.getCpRating() + "," + sr.getCwRating() + "," +
                sr.getPracticRating() + "," + sr.getEsoGradeMax() + "," + sr.getSkipCount() + "," + sr.getVisitCount() + "," +
                sr.getEsoExamRating() + "," + sr.getEsoPassRating() + "," + sr.getEsoCPRating() + "," + sr.getEsoCWRating() + "," +
                sr.getRetakeCount() + "," + sr.getIdDopEso2() + "," + sr.getAttendenceCount() + "," + "'" + sr.getStatus() + "'," +
                (sr.getEsoStudy() ? "1" : "0") + ")";
        return executeUpdate(getSession().createSQLQuery(query));
    }

    public boolean updateDicSubject(long idDicSubject, String subjectName) {
        String query = "UPDATE dic_subject \n" + "SET subjectname = :subjectName\n" + "WHERE id_dic_subject = :idDicSubject\n" + "\n";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("subjectName", subjectName).setParameter("idDicSubject", idDicSubject);
        return executeUpdate(q);
    }

    public boolean updateSubject(SubjectModel subject) {
        String query =
                "UPDATE subject \n" + "SET id_chair = :idChair,\n" + "id_dic_subject = :idDicSubject,\n" + "hourscount = :hoursCount, \n" +
                "hoursaudcount = :hoursAudCount, \n" + "hourslection = :hoursLection, \n" + "hourslabor = :hoursLabor, \n" +
                "hourspractic = :hoursPractic, \n" + "semesternumber = :semesterNumber, \n" + "is_courseproject = :cp, \n" +
                "is_coursework = :cw, \n" + "is_exam = :exam, \n" + "is_pass = :pass, \n" + "is_practic = :practic, \n" +
                "synch_mine = :synchMine, \n" + "type = :type \n" + "WHERE id_subject = :idSubject";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idChair", subject.getIdChair()).setParameter("idDicSubject", subject.getIdDicSubject())
         .setParameter("hoursCount", subject.getCountHours()).setParameter("hoursAudCount", subject.getHoursAudCount())
         .setParameter("hoursLection", subject.getHoursLection()).setParameter("hoursLabor", subject.getHoursLabor())
         .setParameter("hoursPractic", subject.getHoursPractic()).setParameter("semesterNumber", subject.getSemesterNumber())
         .setParameter("cp", subject.getCp() ? 1 : 0).setParameter("cw", subject.getCw() ? 1 : 0)
         .setParameter("exam", subject.getExam() ? 1 : 0).setParameter("pass", subject.getPass() ? 1 : 0)
         .setParameter("practic", subject.getPractic() ? 1 : 0).setParameter("synchMine", subject.getSynchMine() ? 1 : 0)
         .setParameter("type", subject.getType()).setParameter("idSubject", subject.getIdSubject());
        return executeUpdate(q);
    }

    public List<SessionRatingModel> getSRbySSS(long idSSS) {
        String query = "SELECT sr.id_sessionrating AS idSR," + "sr.id_subject AS idSubject FROM sessionrating sr \n" +
                       "WHERE id_student_semester_status = " + idSSS;

        Query q = getSession().createSQLQuery(query).addScalar("idSR", LongType.INSTANCE).addScalar("idSubject", LongType.INSTANCE)
                              .setResultTransformer(Transformers.aliasToBean(SessionRatingModel.class));
        return (List<SessionRatingModel>) getList(q);
    }

    public boolean updateSessionRating(SessionRatingModel sr) {
        String query = "UPDATE sessionrating \n" + "SET is_courseproject = :cp, \n" + "is_coursework = :cw, \n" + "is_exam = :exam, \n" +
                       "is_pass = :pass, \n" + "is_practic = :practic, \n" + "type = :type \n" + "WHERE id_sessionrating = :idSR\n" + "\n";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("cp", sr.getCp() ? 1 : 0).setParameter("cw", sr.getCw() ? 1 : 0).setParameter("exam", sr.getExam() ? 1 : 0)
         .setParameter("pass", sr.getPass() ? 1 : 0).setParameter("practic", sr.getPractic() ? 1 : 0).setParameter("type", sr.getType())
         .setParameter("idSR", sr.getIdSR());
        return executeUpdate(q);
    }

    public List<Long> getIdSubjectList(long idSSS) {
        String query = "SELECT id_subject\n" + "FROM  sessionrating sr\n" + "WHERE id_student_semester_status = " + idSSS;
        List<BigInteger> idSubjectList = (List<BigInteger>) getList(getSession().createSQLQuery(query));
        List<Long> list = new ArrayList<>();
        for (BigInteger subject : idSubjectList) {
            list.add(subject.longValue());
        }

        return list;
    }

    public boolean updateSubjectDate(long idLGSS, Date date, String dateField) {
        String query = "UPDATE link_group_semester_subject \n" + "SET " + dateField + " = :date \n" +
                       "WHERE id_link_group_semester_subject = :idLGSS\n" + "\n";
        Query q = getSession().createSQLQuery(query);
        q.setDate("date", date).setParameter("idLGSS", idLGSS);
        return executeUpdate(q);
    }

    public boolean updateGroup(long idLGSS, long idLGS) {
        String query = "UPDATE link_group_semester_subject \n" + "SET id_link_group_semester = :idLGS \n" +
                       "WHERE id_link_group_semester_subject = :idLGSS\n" + "\n";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idLGS", idLGS).setParameter("idLGSS", idLGSS);
        return executeUpdate(q);
    }

    public Integer checkIfOpenRetakeExist(long idSR, String focQuery) {
        String query = "select count(*) from sessionrating\n" +
                       "                    inner join sessionratinghistory srh using (id_sessionrating)\n" +
                       "                    where id_sessionrating = " + idSR + "\n" + "                    and srh.retake_count < 0" +
                       "                    and " + focQuery;
        List<BigInteger> list = (List<BigInteger>) getList(getSession().createSQLQuery(query));
        return list.size() == 0 ? -1 : list.get(0).intValue();
    }
}