package org.edec.commons.manager;

import org.edec.commons.model.GroupDirectionModel;
import org.edec.dao.DAO;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StandardBasicTypes;

import java.util.List;


public class GroupManager extends DAO {

    public List<GroupDirectionModel> getGroupDirectionBySemester (Long idSem) {
        String query = "SELECT\tdg.id_dic_group AS idDG, dg.groupname AS groupname,\n" +
                       "\tlgs.id_link_group_semester AS idLGS, se.id_institute AS idInstitute,\n" +
                       "\tCUR.directioncode, CUR.specialitytitle AS directiontitle\n" + "FROM\tdic_group dg\n" +
                       "\tINNER JOIN curriculum CUR USING (id_curriculum)\n" +
                       "\tINNER JOIN link_group_semester lgs USING (id_dic_group)\n" + "\tINNER JOIN semester se USING (id_semester)\n" +
                       "WHERE\tlgs.id_semester = :idSemester\n" + "ORDER BY LGS.course, dg.groupname";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idDG", StandardBasicTypes.LONG)
                              .addScalar("idLGS", StandardBasicTypes.LONG)
                              .addScalar("groupname")
                              .addScalar("idInstitute", StandardBasicTypes.LONG)
                              .addScalar("directioncode")
                              .addScalar("directiontitle")
                              .setResultTransformer(Transformers.aliasToBean(GroupDirectionModel.class));
        q.setLong("idSemester", idSem);
        return (List<GroupDirectionModel>) getList(q);
    }
}
