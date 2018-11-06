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
   public List<TeacherModel> getTeachers (String department) {
        String query = "SELECT HF.family, HF.name, HF.patronymic\n" +
                "from employee E \n" +
                "inner join link_employee_department LED using (id_employee) \n" +
                "inner join department D using (id_department) \n" +
                "inner join humanface HF using (id_humanface) \n" +
                "where D.shorttitle = '" + department + "_ИКИТ'\n" +
                "group by HF.family, HF.name, HF.patronymic";
        Query q = getSession().createSQLQuery(query)
                .addScalar("family")
                .addScalar("name")
                .addScalar("patronymic")
                .setResultTransformer(Transformers.aliasToBean(TeacherModel.class));
        return (List<TeacherModel>) getList(q);
    }
}
