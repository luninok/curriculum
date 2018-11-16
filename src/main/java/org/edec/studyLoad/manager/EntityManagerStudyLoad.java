package org.edec.studyLoad.manager;

import org.edec.commission.model.PeriodCommissionModel;
import org.edec.commission.model.SubjectDebtModel;
import org.edec.dao.DAO;
import org.edec.studyLoad.model.TeacherModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
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
                "group by HF.family, HF.name, HF.patronymic";
        Query q = getSession().createSQLQuery(query)
                .addScalar("family")
                .addScalar("name")
                .addScalar("patronymic")
                .setResultTransformer(Transformers.aliasToBean(TeacherModel.class));
        return (List<TeacherModel>) getList(q);
    }

    public List<String> getDepartments() {
        String query = "SELECT D.fulltitle FROM public.department D \n" +
                "inner join institute I using (id_institute) \n" +
                "where I.shorttitle = 'ИКИТ'";
        Query q = getSession().createSQLQuery(query);
        return (List<String>) getList(q);
    }

    public List<String> getPosition() {
        String query = "SELECT  ER.rolename FROM public.employee_role ER";
        Query q = getSession().createSQLQuery(query);
        return (List<String>) getList(q);
    }
}
