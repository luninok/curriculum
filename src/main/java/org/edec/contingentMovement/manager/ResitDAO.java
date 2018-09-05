package org.edec.contingentMovement.manager;

import org.edec.contingentMovement.model.ResitRatingModel;
import org.edec.dao.DAO;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class ResitDAO extends DAO {
    public List<ResitRatingModel> getListResitRating (Long idStudentCard, Long idDG, Boolean currentGroup) {
        String query =
                "SELECT DS.subjectname, DG.groupname, LGS.semesternumber, S.hourscount AS hoursCount, SR.id_sessionrating AS idSR,\n" +
                "  SR.examrating, SR.passrating, SR.courseprojectrating AS cprating, SR.courseworkrating AS cwrating, SR.practicrating,\n" +
                "  SR.is_exam = 1 AS exam, SR.is_pass = 1 AS pass, SR.is_courseproject = 1 AS cp, SR.is_coursework = 1 AS cw, SR.is_practic = 1 AS practic,\n" +
                "  SR.status, SR.type, (SEM.is_current_sem = 1) AS curSem\n" + "FROM student_semester_status SSS\n" +
                "  INNER JOIN link_group_semester LGS USING (id_link_group_semester)\n" +
                "  INNER JOIN semester SEM ON LGS.id_semester = SEM.id_semester\n" +
                "  INNER JOIN dic_group DG ON DG.id_dic_group = LGS.id_dic_group\n" +
                "  INNER JOIN sessionrating SR USING (id_student_semester_status)\n" + "  INNER JOIN subject S USING (id_subject)\n" +
                "  INNER JOIN dic_subject DS USING (id_dic_subject)\n" + "WHERE SSS.id_studentcard = " + idStudentCard + "\n" +
                "\tAND DG.id_dic_group " + (currentGroup ? (" = " + idDG) : (" <> " + idDG)) + "\n" +
                "ORDER BY LGS.semesternumber, DS.subjectname, exam, pass, cp, cw, practic, examrating DESC, passrating DESC,\n" +
                "    courseprojectrating DESC, courseworkrating DESC, practicrating DESC";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("subjectname")
                              .addScalar("groupname")
                              .addScalar("semesternumber")
                              .addScalar("hoursCount")
                              .addScalar("idSR", LongType.INSTANCE)
                              .addScalar("examrating")
                              .addScalar("passrating")
                              .addScalar("cprating")
                              .addScalar("cwrating")
                              .addScalar("practicrating")
                              .addScalar("exam")
                              .addScalar("pass")
                              .addScalar("cp")
                              .addScalar("cw")
                              .addScalar("practic")
                              .addScalar("status")
                              .addScalar("type")
                              .addScalar("curSem")
                              .setResultTransformer(Transformers.aliasToBean(ResitRatingModel.class));
        return (List<ResitRatingModel>) getList(q);
    }

    public boolean saveResitRating (ResitRatingModel resit, Long idCurrentUser) {
        String query =
                "SELECT * FROM select_new_rating_after_resit(" + resit.getIdSR() + ", " + resit.getRating() + ", " + resit.getFocInt() +
                ", " + "'1.5.1'," + resit.getResitRating().getIdSR() + ", " + idCurrentUser + ")";
        return callFunction(query);
    }

    public boolean deleteAllResit(Long idStudentcard, Long idDG) {
        String query = "SELECT * FROM resit_remove_by_student_and_group(" + idStudentcard + ", " + idDG + ")";
        return callFunction(query);
    }
}


