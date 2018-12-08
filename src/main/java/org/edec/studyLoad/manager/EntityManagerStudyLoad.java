package org.edec.studyLoad.manager;

import org.edec.main.model.DepartmentModel;
import org.edec.dao.DAO;
import org.edec.studyLoad.model.*;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class EntityManagerStudyLoad extends DAO {
    public List<TeacherModel> getTeachers(String department) {
        String query = "SELECT HF.family, HF.name, HF.patronymic, E.id_employee\n" +
                "from employee E \n" +
                "inner join link_employee_department LED using (id_employee) \n" +
                "inner join department D using (id_department) \n" +
                "inner join humanface HF using (id_humanface) \n" +
                "inner join employee_role ER using (id_employee_role)" +
                "where D.fulltitle = '" + department + "'\n" + "and ER.group='" + 1 + "'" +
                "group by HF.family, HF.name, HF.patronymic, E.id_employee";
        Query q = getSession().createSQLQuery(query)
                .addScalar("family")
                .addScalar("name")
                .addScalar("patronymic")
                .addScalar("id_employee", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(TeacherModel.class));
        return (List<TeacherModel>) getList(q);
    }

    public List<TeacherModel> searchTeachers(String family, String name, String patronymic) {
        String query = "SELECT HF.family, HF.name, HF.patronymic, E.id_employee\n" +
                "from employee E\n" +
                "inner join link_employee_department LED using (id_employee)\n" +
                "inner join department D using (id_department)\n" +
                "inner join humanface HF using (id_humanface)\n" +
                "where lower(HF.Name) Like lower('%" + name + "%')" +
                " AND lower(HF.family) Like lower('%" + family + "%')" +
                " AND lower(HF.patronymic) Like lower('%" + patronymic + "%') \n" +
                "                group by HF.family, HF.name, HF.patronymic, E.id_employee";
        Query q = getSession().createSQLQuery(query)
                .addScalar("family")
                .addScalar("name")
                .addScalar("patronymic")
                .addScalar("id_employee", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(TeacherModel.class));
        return (List<TeacherModel>) getList(q);
    }

    public Double getMaxload(TeacherModel selectTeacher) {
        String query = "SELECT ER.maximum_load\n" +
                "from link_employee_department LED\n" +
                "inner join employee_role ER using (id_employee_role)\n" +
                "inner join employee E using (id_employee)\n" +
                "inner join humanface HF using (id_humanface)\n" +
                "where HF.family = '" + selectTeacher.getFamily() + "' and HF.name ='" + selectTeacher.getName() + "' and HF.patronymic='" + selectTeacher.getPatronymic() + "' and ER.group=1";
        Query q = getSession().createSQLQuery(query)
                .addScalar("maximum_load", DoubleType.INSTANCE);
        return (Double) getList(q).get(0);
    }

    public List<LoadTeacherModel> getLoad(TeacherModel selectTeacher) {
        String query = "SELECT LED.time_wagerate, id_department\n" +
                "from link_employee_department LED\n" +
                "inner join employee_role ER using (id_employee_role)\n" +
                "inner join employee E using (id_employee)\n" +
                "inner join humanface HF using (id_humanface)\n" +
                "inner join department D using (id_department)\n" +
                "inner join employee_byworker EB using (id_employee_byworker)\n" +
                "where HF.family = '" + selectTeacher.getFamily() + "' and HF.name ='" + selectTeacher.getName() + "' and HF.patronymic='" + selectTeacher.getPatronymic() + "' and ER.group=1";
        Query q = getSession().createSQLQuery(query)
                .addScalar("time_wagerate", DoubleType.INSTANCE)
                .addScalar("id_department", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(LoadTeacherModel.class));
        return (List<LoadTeacherModel>) getList(q);
    }

    public List<EmploymentModel> getEmployment(TeacherModel selectTeacher, String department) {
        String query = "SELECT  D.shorttitle, EB.byworker, ER.rolename, LED.wagerate, LED.time_wagerate\n" +
                "from link_employee_department LED \n" +
                "inner join employee_role ER using (id_employee_role) \n" +
                "inner join employee E using (id_employee) \n" +
                "inner join humanface HF using (id_humanface)\n" +
                "inner join department D using (id_department) \n" +
                "inner join employee_byworker EB using (id_employee_byworker) \n" +
                "where D.fulltitle = '" + department + "' and HF.family = '" + selectTeacher.getFamily() +
                "' and HF.name ='" + selectTeacher.getName() + "' and HF.patronymic='" + selectTeacher.getPatronymic() +
                "' and ER.group='" + 1 + "'";
        Query q = getSession().createSQLQuery(query)
                .addScalar("shorttitle")
                .addScalar("byworker")
                .addScalar("rolename")
                .addScalar("wagerate")
                .addScalar("time_wagerate")
                .setResultTransformer(Transformers.aliasToBean(EmploymentModel.class));

        // List<EmploymentModel> employmentModels = q.list();
        //  employmentModels.get(0).setMaximum_load(maxWagerate);
        //return employmentModels;
        return (List<EmploymentModel>) getList(q);
    }

    public List<DepartmentModel> getDepartments() {
        String query = "SELECT D.id_department AS idDepartment, D.fulltitle AS fulltitle,\n" +
                "D.shorttitle AS shorttitle, D.id_chair AS idChair,\n" +
                "D.id_institute FROM public.department D \n" +
                "inner join institute I using (id_institute)\n" +
                "where I.shorttitle = 'ИКИТ' and D.fulltitle Like '%Кафедра%'";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idDepartment", LongType.INSTANCE)
                .addScalar("fulltitle")
                .addScalar("shorttitle")
                .addScalar("idChair", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(DepartmentModel.class));
        return (List<DepartmentModel>) getList(q);
    }

    public List<PositionModel> getPositions() {
        String query = "SELECT ER.id_employee_role AS idPosition, ER.rolename AS positionName FROM public.employee_role ER where ER.group='" + 1 + "'";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idPosition", LongType.INSTANCE)
                .addScalar("positionName")
                .setResultTransformer(Transformers.aliasToBean(PositionModel.class));
        return (List<PositionModel>) getList(q);
    }

    public List<ByworkerModel> getByworker() {
        String query = "SELECT EB.id_employee_byworker AS idByworker, EB.byworker AS byworker FROM public.employee_byworker EB";
        Query q = getSession().createSQLQuery(query)
                .addScalar("idByworker", LongType.INSTANCE)
                .addScalar("byworker")
                .setResultTransformer(Transformers.aliasToBean(ByworkerModel.class));
        return (List<ByworkerModel>) getList(q);
    }

    public List<AssignmentModel> getInstructions(Long idSem, Long idDepartment) {
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
                "ORDER BY fio\n";
        Query q = getSession().createSQLQuery(query)
                .addScalar("fio")
                .addScalar("nameDiscipline")
                .addScalar("groupName")
                .addScalar("course", IntegerType.INSTANCE)
                .addScalar("hoursCount", IntegerType.INSTANCE)
                .addScalar("hourSaudCount", IntegerType.INSTANCE)
                .addScalar("is_exam", IntegerType.INSTANCE)
                .addScalar("is_pass", IntegerType.INSTANCE)
                .addScalar("is_courseproject", IntegerType.INSTANCE)
                .addScalar("is_coursework", IntegerType.INSTANCE)
                .addScalar("is_practic", IntegerType.INSTANCE)
                .addScalar("typeInstructionInt", IntegerType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(AssignmentModel.class));
        return (List<AssignmentModel>) getList(q);
    }

    public List<VacancyModel> getVacancy() {
        String query = "SELECT id_vacancy, ER.rolename, wagerate FROM public.vacancies  \n" +
                "inner join employee_role ER using (id_employee_role)";
        Query q = getSession().createSQLQuery(query)
                .addScalar("id_vacancy", LongType.INSTANCE)
                .addScalar("rolename")
                .addScalar("wagerate", DoubleType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(VacancyModel.class));
        return (List<VacancyModel>) getList(q);
    }

    public void updateVacancy(Long id_vacancy, Long id_employee_role, String wagerate) {
        String query = "update vacancies set id_employee_role = " + id_employee_role + ", wagerate = " + wagerate + " where id_vacancy=" + id_vacancy + "";
        executeUpdate(getSession().createSQLQuery(query));
    }

    public void createVacancy(Long id_employee_role, String wagerate) {
        String query = "insert into vacancies (id_employee_role, wagerate) values (" + id_employee_role + ", " + wagerate + ")";

        executeUpdate(getSession().createSQLQuery(query));
    }

    public void deleteVacancy(Long id_vacancy) {
        String query = "delete from vacancies where id_vacancy= " + id_vacancy;
        executeUpdate(getSession().createSQLQuery(query));
    }

    public void updateEmployment(Long id_employee, Long idByworker, Long idRole, Double wagerate, Double time_wagerate, Long id_department) {
        String queryVacancy = "update link_employee_department set id_employee_byworker = " + idByworker +
                ", id_employee_role = " + idRole + ", wagerate = " + wagerate + ", time_wagerate = " +
                time_wagerate + " where id_employee=" + id_employee + " and id_department=" + id_department + "";
        executeUpdate(getSession().createSQLQuery(queryVacancy));
    }

    public boolean addRate(Long id_employee, Long id_department, Long id_position) {
        try {
            begin();
            String query = "INSERT INTO link_employee_department(id_employee, id_department, id_employee_role, wagerate, " +
                    "is_permanency, employee_position, is_hide)\n" +
                    "VALUES (" + id_employee + ", " + id_department + ", " + id_position + ", '0', NULL, NULL, false)";

            Query q = getSession().createSQLQuery(query);
            q.executeUpdate();
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

    public boolean addRateBasedOnVacancy(Long id_employee, Long id_department, Long id_position, Double rate) {
        try {
            begin();
            String query = "INSERT INTO link_employee_department(id_employee, id_department, id_employee_role, wagerate, " +
                    "is_permanency, employee_position, is_hide)\n" +
                    "VALUES (" + id_employee + ", " + id_department + ", " + id_position + ", '" + rate + "', NULL, NULL, false)";

            Query q = getSession().createSQLQuery(query);
            q.executeUpdate();
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

    public boolean removeRate(Long id_employee, Long id_department) {
        try {
            begin();
            String query = "DELETE FROM link_employee_department LED " +
                    "WHERE LED.id_employee = " + id_employee + " AND LED.id_department = " + id_department;

            Query q = getSession().createSQLQuery(query);
            q.executeUpdate();
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
}
