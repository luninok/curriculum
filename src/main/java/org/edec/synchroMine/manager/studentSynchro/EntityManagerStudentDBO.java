package org.edec.synchroMine.manager.studentSynchro;

import org.edec.dao.MineDAO;
import org.edec.synchroMine.model.mine.Group;
import org.edec.synchroMine.model.mine.Student;
import org.edec.utility.component.model.StudentModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class EntityManagerStudentDBO extends MineDAO {
    public List<Student> getStudentsByInst (Long idInst) {
        Query q = getSession().createQuery("from Student where idInst = :idInst and idStatus = 1");
        q.setLong("idInst", idInst);
        return (List<Student>) getList(q);
    }

    public List<Student> getStudentsByGroup (Long idGroup) {
        Query q = getSession().createQuery("from Student where idGroup = :idGroup");
        q.setLong("idGroup", idGroup);
        return (List<Student>) getList(q);
    }

    public List<Group> getGroupByInst (Long idInst) {
        Query q = getSession().createQuery("from Group where idInst = :idInst");
        q.setLong("idInst", idInst);
        return (List<Group>) getList(q);
    }

    public List<String> getGroupNameByInstFor6years (Long idInst) {
        String years = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        years += "'" + (calendar.get(Calendar.YEAR) - 7) + "-" + (calendar.get(Calendar.YEAR) - 6) + "',";
        years += "'" + (calendar.get(Calendar.YEAR) - 6) + "-" + (calendar.get(Calendar.YEAR) - 5) + "',";
        years += "'" + (calendar.get(Calendar.YEAR) - 5) + "-" + (calendar.get(Calendar.YEAR) - 4) + "',";
        years += "'" + (calendar.get(Calendar.YEAR) - 4) + "-" + (calendar.get(Calendar.YEAR) - 3) + "',";
        years += "'" + (calendar.get(Calendar.YEAR) - 3) + "-" + (calendar.get(Calendar.YEAR) - 2) + "',";
        years += "'" + (calendar.get(Calendar.YEAR) - 2) + "-" + (calendar.get(Calendar.YEAR) - 1) + "',";
        years += "'" + (calendar.get(Calendar.YEAR) - 1) + "-" + (calendar.get(Calendar.YEAR)) + "',";
        years += "'" + (calendar.get(Calendar.YEAR)) + "-" + (calendar.get(Calendar.YEAR) + 1) + "'";
        String query =
                "SELECT DISTINCT (Название) AS groupname\n" + "FROM Все_Группы\n" + "WHERE Код_Факультета = :idInst AND УчебныйГод IN (" +
                years + ")";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idInst", idInst);
        return (List<String>) getList(q);
    }

    public List<StudentModel> getStudentsByGroupName (String groupName) {
        String query = "SELECT\n" +
                       "\tСт.Код AS idStudCardMine, Ст.Имя AS name, Ст.Фамилия AS family, Ст.Отчество AS patronymic,Ст.Дата_Рождения AS birthday,\n" +
                       "\tСт.Статус AS status, Ст.Номер_Зачетной_книжки AS recordbook, Ст.УслОбучения AS condOfEducation,\n" + "\tCASE\n" +
                       "\t\tWHEN Ст.Пол LIKE 'Муж' OR Ст.Пол LIKE 'мужской' THEN 1\n" + "\tELSE 0 END AS sex\n" + "FROM\n" +
                       "\tВсе_Студенты Ст\n" + "\tINNER JOIN Все_Группы Гр ON Ст.Код_Группы = Гр.Код\n" + "WHERE\n" +
                       "\tГр.Название LIKE :groupname\n" + "ORDER BY\n" + "\tfamily, name, patronymic";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idStudCardMine", LongType.INSTANCE)
                              .addScalar("name")
                              .addScalar("family")
                              .addScalar("patronymic")
                              .addScalar("status")
                              .addScalar("recordbook")
                              .addScalar("condOfEducation")
                              .addScalar("sex")
                              .addScalar("birthday")
                              .setResultTransformer(Transformers.aliasToBean(StudentModel.class));
        q.setString("groupname", groupName);
        return (List<StudentModel>) getList(q);
    }
}
