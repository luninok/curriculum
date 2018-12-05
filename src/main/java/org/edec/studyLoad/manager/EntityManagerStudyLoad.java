package org.edec.studyLoad.manager;

import org.edec.main.model.DepartmentModel;
import org.edec.dao.DAO;
import org.edec.studyLoad.model.AssignmentModel;
import org.edec.studyLoad.model.TeacherModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;

import java.util.List;

public class EntityManagerStudyLoad extends DAO {
    public List<TeacherModel> getTeachers(String department) {
        String query = "SELECT HF.family, HF.name, HF.patronymic\n" +
                "from employee E \n" +
                "inner join link_employee_department LED using (id_employee) \n" +
                "inner join department D using (id_department) \n" +
                "inner join humanface HF using (id_humanface) \n" +
                "where D.fulltitle = '" + department + "'\n" +
                "group by HF.family, HF.name, HF.patronymic\n" +
                "order by HF.family";
        Query q = getSession().createSQLQuery(query)
                .addScalar("family")
                .addScalar("name")
                .addScalar("patronymic")
                .setResultTransformer(Transformers.aliasToBean(TeacherModel.class));
        return (List<TeacherModel>) getList(q);
    }

    public List<DepartmentModel> getDepartments() {
        String query = "SELECT D.id_department AS idDepartment, D.fulltitle AS fulltitle,\n"+
                "D.shorttitle AS shorttitle, D.id_chair AS idChair,\n" +
                "D.id_institute FROM public.department D \n" +
                "inner join institute I using (id_institute)\n" +
                "where I.shorttitle = 'ИКИТ'";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idDepartment", LongType.INSTANCE)
                .addScalar("fulltitle")
                .addScalar("shorttitle")
                .addScalar("idChair", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(DepartmentModel.class));
        return (List<DepartmentModel>) getList(q);
    }

    public List<String> getPosition() {
        String query = "SELECT  ER.rolename FROM public.employee_role ER";
        Query q = getSession().createSQLQuery(query);
        return (List<String>) getList(q);
    }

    public List<AssignmentModel> getInstructions(Long idSem, Long idDepartment)
    {
        String query = "SELECT HF.family || ' ' || HF.name || ' ' || HF.patronymic AS fio, DS.subjectname AS nameDiscipline,\n" +
                "LESG.tutoringtype AS typeInstructionInt, DG.groupname AS groupname, LGS.course AS course,\n" +
                "SUB.is_exam, SUB.is_pass, SUB.is_courseproject, SUB.is_coursework, SUB.is_practic, SUB.hoursaudcount as hourSaudCount, SUB.hourscount as hoursCount\n" +
                "FROM link_employee_department\n" +
                "INNER JOIN employee E using (id_employee) \n" +
                "inner join humanface HF using (id_humanface)\n" +
                "inner join link_employee_subject_group LESG using (id_employee)\n" +
                "inner join link_group_semester_subject LGSS using (id_link_group_semester_subject)\n" +
                "inner join link_group_semester LGS using (id_link_group_semester)\n" +
                "inner join semester SEM using (id_semester)\n" +
                "inner join dic_group DG using (id_dic_group)\n" +
                "inner join subject SUB using (id_subject)\n" +
                "inner join dic_subject DS using (id_dic_subject)\n" +
                "WHERE id_department =" + idDepartment + "  AND SEM.id_semester = " + idSem + " AND SUB.is_active = 1\n" +
                "GROUP BY FIO, groupname, nameDiscipline, typeInstructionInt, course, SUB.is_exam, SUB.is_pass, SUB.is_courseproject, SUB.is_coursework, SUB.is_practic, hourSaudCount, hoursCount\n" +
                "ORDER BY FIO";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("nameDiscipline")
                .addScalar("groupName")
                .addScalar("course", IntegerType.INSTANCE)
                .addScalar("hoursCount",IntegerType.INSTANCE)
                .addScalar("hourSaudCount",IntegerType.INSTANCE)
                .addScalar("is_exam", IntegerType.INSTANCE)
                .addScalar("is_pass",IntegerType.INSTANCE)
                .addScalar("is_courseproject",IntegerType.INSTANCE)
                .addScalar("is_coursework",IntegerType.INSTANCE)
                .addScalar("is_practic",IntegerType.INSTANCE)
                .addScalar("typeInstructionInt",IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(AssignmentModel.class));
        return (List<AssignmentModel>) getList(q);
    }
}
