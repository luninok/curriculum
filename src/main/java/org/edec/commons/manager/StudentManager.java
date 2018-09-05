package org.edec.commons.manager;

import org.edec.commons.model.StudentGroupModel;
import org.edec.dao.DAO;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;

import java.util.List;


public class StudentManager extends DAO {
    /**
     * @param filter либо фио, либо зачетка, либо группа
     * @param idInst - институт (если null, то институт не учитывается)
     * @param fos    - форма обучения (если null, то форма обучения не учитывается)
     * @return список студентов
     */
    public List<StudentGroupModel> getStudentGroupByFilter (String filter, Long idInst, Integer fos) {
        String query = "SELECT HF.family, HF.name, HF.patronymic, SC.recordBook, DG.groupname,\n" +
                       "\tSC.id_studentcard AS idStudentCard, DG.id_dic_group AS idDG\n" + "FROM humanface HF\n" +
                       "    INNER JOIN studentcard SC USING (id_humanface)\n" +
                       "    INNER JOIN student_semester_status SSS USING (id_studentcard)\n" +
                       "    INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                       "    INNER JOIN semester SEM USING (id_semester)\n" + "    INNER JOIN dic_group DG USING (id_dic_group)\n" +
                       "WHERE HF.family||' '||HF.name||' '||HF.patronymic||' '||SC.recordbook||' '||DG.groupname ILIKE '%" + filter +
                       "%'\n" + (idInst == null ? "" : "AND SEM.id_institute = " + idInst + "\n") +
                       (fos == null ? "" : "AND SEM.formofstudy = " + fos + "\n") +
                       "GROUP BY HF.id_humanface, SC.id_studentcard, DG.id_dic_group\n" + "ORDER BY HF.family, HF.name, DG.groupname";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("family")
                              .addScalar("name")
                              .addScalar("patronymic")
                              .addScalar("recordBook")
                              .addScalar("groupname")
                              .addScalar("idDG", StandardBasicTypes.LONG)
                              .addScalar("idStudentCard", StandardBasicTypes.LONG)
                              .setResultTransformer(Transformers.aliasToBean(StudentGroupModel.class));
        return (List<StudentGroupModel>) getList(q);
    }
}