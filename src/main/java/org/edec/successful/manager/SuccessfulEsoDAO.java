package org.edec.successful.manager;

import org.edec.dao.DAO;
import org.edec.successful.model.RatingModel;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.GovFinancedConst;
import org.edec.utility.converter.DateConverter;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;

import java.util.Collections;
import java.util.Date;
import java.util.List;


public class SuccessfulEsoDAO extends DAO {

    public List<RatingModel> getRatingByFilter (Long idInst, Long idSemester, Long idDepart, FormOfStudy fos, GovFinancedConst govFin,
                                                String levels, String groupName, Date lastDate, String courses, Long idChair) {

        String lastDateCheck = " (RE.signdate < '" + DateConverter.convertDateToString(lastDate != null ? lastDate : new Date()) +
                               "' OR RE.signdate IS NULL)";
        String instCheck = idInst != null ? " AND SE.id_institute = " + idInst : "";
        String semestrCheck = idSemester != null ? " AND SE.id_semester = " + idSemester : "";
        String departCheck = idDepart != null ? " AND DE.id_department = " + idDepart : "";
        String fosCheck = fos != null ? " AND CU.formofstudy = " + fos.getType() : "";
        String govFinCheck = (govFin != null && govFin.getType() != null) ? " AND SSS.is_government_financed = " + govFin.getType() : "";
        String levelCheck = (levels != null && levels != "") ? " AND CU.qualification IN (" + levels + ")" : "";
        String groupNameCheck = (groupName != null && (!groupName.equals("-") && groupName != ""))
                                ? " AND DG.groupname like '" + groupName + "'"
                                : " AND DG.groupname like '%%'";
        String coursesCheck = (courses != null && courses != "") ? " AND LGS.course IN (" + courses + ")" : "";
        String chairCheck = (idChair != null) ? " AND SU.id_chair = " + idChair : "";
        String currentGroupCheck = " AND SC.id_current_dic_group = DG.id_dic_group";
        String retakeCheck = " AND SRH.retake_count>=0";
        String deductCheck = " AND SSS.is_academicleave=0 AND SSS.is_deducted=0";

        String query = "SELECT " + "\tDG.id_dic_group AS idGroup,\n" + "\tSSS.id_student_semester_status AS idStudent,\n" +
                       "\tHF.id_humanface AS idHumanface,\n" + "\tSC.id_studentcard AS idStudentcard,\n" +
                       "\tSE.id_semester AS idSemester,\n" +
                       //"\tDE.id_department AS idDepartment,\n" +
                       "\tSRH.id_sessionratinghistory AS idSRH,\n" + "\tSU.id_subject AS idSubject,\n" + "\tDG.groupname AS groupname,\n" +
                       "\tHF.family || ' ' || HF.name || ' ' || HF.patronymic AS fio,\n" + "\tDS.subjectname AS subjectName,\n" +
                       "\tRE.register_number AS regNumber,\n" +
                       //"\tDE.fulltitle AS departmentName,\n" +
                       "\tLGS.course AS course,\n" + "\tCU.formofstudy AS formOfStudy,\n" + "\tCU.qualification AS lvl,\n" +
                       "\tSSS.is_government_financed AS govFinanced,\n" + "\tSRH.is_pass AS pass,\n" + "\tSRH.is_exam AS exam,\n" +
                       "\tSRH.is_courseproject AS cp,\n" + "\tSRH.is_coursework AS cw,\n" + "\tSRH.is_practic AS practic,\n" +
                       "\tSRH.newrating AS rating, \n" + "\tRE.signdate AS signdate, \n" +
                       "\tCASE WHEN DE2.shorttitle is not null THEN '(' || DE2.shorttitle || ') ' || DE2.fulltitle \n" +
                       "\tELSE DE2.fulltitle \n" + "\tEND AS tChairFulltitle, \n" + "\tCH2.id_chair AS tChairId, \n" +
                       "\tDE.fulltitle AS eChairFulltitle, \n" + "\tCH.id_chair AS eChairId \n" +
                       //"\tAS type,\n" +
                       //"\tAS status\n" +
                       "\tFROM\n" + "\tsessionratinghistory SRH\n" + "\tLEFT JOIN register RE USING (id_register)\n" +
                       "\tINNER JOIN sessionrating SR USING (id_sessionrating)\n" + "\tINNER JOIN subject SU USING (id_subject)\n" +
                       "\tLEFT JOIN chair CH2 ON SU.id_chair=CH2.id_chair\n" + "\tLEFT JOIN department DE2 ON CH2.id_chair=DE2.id_chair\n" +
                       "\tINNER JOIN dic_subject DS USING (id_dic_subject)\n" +
                       "\tINNER JOIN student_semester_status SSS USING (id_student_semester_status)\n" +
                       "\tINNER JOIN studentcard SC USING (id_studentcard)\n" + "\tINNER JOIN humanface HF USING (id_humanface)\n" +
                       "\tINNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "\tINNER JOIN semester SE ON LGS.id_semester = SE.id_semester\n" +
                       "\tINNER JOIN dic_group DG USING (id_dic_group) \n" +
                       "\tINNER JOIN curriculum CU ON DG.id_curriculum=CU.id_curriculum \n" +
                       "\tINNER JOIN chair CH ON CU.id_chair=CH.id_chair \n" + "\tLEFT JOIN department DE ON CH.id_chair=DE.id_chair\n" +
                       //"\tINNER JOIN department DE ON DE.id_chair=CH.id_chair \n" +
                       "WHERE \n" + lastDateCheck + instCheck + semestrCheck + departCheck + fosCheck + govFinCheck + levelCheck +
                       groupNameCheck + coursesCheck + chairCheck + currentGroupCheck + retakeCheck + deductCheck +
                       //"\t GROUP BY \n" +
                       //"\tSSS.id_student_semester_status, SRH.id_sessionratinghistory \n" +
                       "\tORDER BY\n" +
                       "\tLGS.course, fio, SSS.id_student_semester_status,DS.subjectname, SRH.retake_count DESC, SRH.newrating DESC, RE.register_number ";
        //System.out.println(query);

        Query q = getSession().createSQLQuery(query)
                              .addScalar("idGroup", LongType.INSTANCE)
                              .addScalar("idStudent", LongType.INSTANCE)
                              .addScalar("idHumanface", LongType.INSTANCE)
                              .addScalar("idStudentcard", LongType.INSTANCE)
                              .addScalar("idSemester", LongType.INSTANCE)
                              //.addScalar("idDepartment", LongType.INSTANCE)
                              .addScalar("idSRH", LongType.INSTANCE)
                              .addScalar("idSubject", LongType.INSTANCE)
                              .addScalar("groupname")
                              .addScalar("fio")
                              .addScalar("subjectName")
                              .addScalar("regNumber")
                              //.addScalar("departmentName")
                              .addScalar("course")
                              .addScalar("formOfStudy")
                              .addScalar("lvl")
                              .addScalar("govFinanced", BooleanType.INSTANCE)
                              .addScalar("pass", BooleanType.INSTANCE)
                              .addScalar("exam", BooleanType.INSTANCE)
                              .addScalar("cp", BooleanType.INSTANCE)
                              .addScalar("cw", BooleanType.INSTANCE)
                              .addScalar("practic", BooleanType.INSTANCE)
                              .addScalar("rating")
                              .addScalar("signdate", DateType.INSTANCE)
                              .addScalar("tChairFulltitle")
                              .addScalar("tChairId", LongType.INSTANCE)
                              .addScalar("eChairFulltitle")
                              .addScalar("eChairId", LongType.INSTANCE)
                              //.addScalar("type")
                              //.addScalar("status")
                              .setResultTransformer(Transformers.aliasToBean(RatingModel.class));
        return (List<RatingModel>) getList(q);
    }
}
