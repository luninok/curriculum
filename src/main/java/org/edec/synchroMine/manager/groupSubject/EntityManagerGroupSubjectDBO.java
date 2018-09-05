package org.edec.synchroMine.manager.groupSubject;

import org.edec.dao.MineDAO;
import org.edec.synchroMine.model.dao.SubjectGroupMineModel;
import org.edec.synchroMine.model.dao.WorkloadModel;
import org.edec.synchroMine.model.eso.GroupMineModel;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.FloatType;
import org.hibernate.type.LongType;
import org.hibernate.type.NumericBooleanType;

import java.util.List;
import java.util.Set;

/**
 * @author Max Dimukhametov
 */
public class EntityManagerGroupSubjectDBO extends MineDAO {

    public List<WorkloadModel> getWorkloadsByGroup (Long idInstMine, Integer course, String groupname, Set<Long> worklaods) {
        String query = "SELECT Нагр.Дисциплина AS subjectname, Преп.Фамилия AS family, Преп.Имя AS name, Преп.Отчество AS patronymic,\n" +
                       "\tГр.Курс AS course, Нагр.Семестр AS semester\n" + "FROM Все_Группы Гр\n" +
                       "\tINNER JOIN Планы Пл ON Гр.КодПлана = Пл.Код\n" +
                       "\tINNER JOIN Нагрузка Нагр ON Пл.ИмяФайла = Нагр.УчебныйПлан AND (Гр.Код = Нагр.КодГруппы OR Гр.Название = Нагр.Группа) AND Нагр.Курс = Гр.Курс\n" +
                       "\t\tAND (Нагр.ПроизведённыеДействия IS NULL OR Нагр.ПроизведённыеДействия NOT LIKE 'Строка исключена из нагрузки%')\n" +
                       "\tINNER JOIN Преподаватели Преп ON Нагр.КодПреподавателя = Преп.Код\n" + "WHERE Гр.Код_Факультета = " + idInstMine +
                       " AND Гр.Курс = " + course + " AND Гр.Название LIKE '" + groupname + "'\n" + "\tAND Нагр.Код NOT IN (" +
                       worklaods.toString().replaceAll("\\[", "").replaceAll("]", "") + ")";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("subjectname")
                              .addScalar("family")
                              .addScalar("name")
                              .addScalar("patronymic")
                              .addScalar("course")
                              .addScalar("semester")
                              .setResultTransformer(Transformers.aliasToBean(WorkloadModel.class));

        return (List<WorkloadModel>) getList(q);
    }

    public List<SubjectGroupMineModel> getSubjectFromCurriculum (Long idInstMine, Integer course, Integer semester, String groupname,
                                                                 Set<Long> listSubject) {
        String query = "SELECT Пстр.Дисциплина AS subjectname, Пстр.КодДисциплины AS idSubjMine,\n" +
                       "\tCASE WHEN COALESCE(ПлПр.Экзамен, 0) = 1 THEN 1\n" + "\t\tWHEN ПЧасыС.Экзамен = 1 OR ПЧасы.Экзамен = 1 THEN 1 \n" +
                       "\tELSE 0 END exam,\n" + "\tCASE WHEN ПлПр.Код IS NOT NULL AND (ПлПр.Зачёт = 1 OR ПлПр.ЗачётСОценкой = 1) THEN 1\n" +
                       "\t     WHEN ПЧасыС.Зачет = 1 OR ПЧасыС.ЗачетСОценкой = 1 OR ПЧасы.Зачет = 1 OR ПЧасы.ЗачетСОценкой = 1 THEN 1 \n" +
                       "\tELSE 0 END pass, \n" + "\tCASE WHEN COALESCE(ПлПр.КурсовойПроект, 0) = 1 THEN 1\n" +
                       "\t     WHEN ПЧасыС.КурсовойПроект = 1 OR ПЧасы.КурсовойПроект = 1 THEN 1 \n" + "\tELSE 0 END cp,   \n" +
                       "\tCASE WHEN COALESCE(ПлПр.КурсоваяРабота, 0) =1 THEN 1\n" +
                       "\t     WHEN ПЧасыС.КурсоваяРабота = 1 OR ПЧасы.КурсоваяРабота = 1 THEN 1 \n" + "\tELSE 0 END cw,\n" +
                       "\tCASE WHEN COALESCE(ПлПр.ЗачётСОценкой, 0) = 1 THEN 1\n" +
                       "\t     WHEN ПЧасы.ЗачетСОценкой = 1 OR ПЧасыС.ЗачетСОценкой = 1 THEN 1 \n" + "\tELSE 0 END type, \n" +
                       "\tCOALESCE (ПЧасыС.Лекции, ПЧасы.Лекций, 0) AS hoursLecture, COALESCE (ПЧасыС.Практики, ПЧасы.Практик, 0) AS hoursPractice,\n" +
                       "\tCOALESCE (ПЧасыС.Лабораторные, ПЧасы.Лабораторных, 0) AS hoursLabaratory, COALESCE (ПЧасыС.СамРабота, ПЧасы.СамРабота, 0) AS hoursIndependent,\n" +
                       "\tCOALESCE (ПЧасыС.ЧасовНаЭкзамены, ПЧасы.ЧасовНаЭкзамены, 0) AS hoursExam,\n" +
                       "\tCASE WHEN ПлПр.Код IS NOT NULL THEN 1 ELSE 0 END practicType, Пстр.КодКафедры AS idChairMine, Пстр.Факультатив AS facultative\n" +
                       "FROM Все_Группы Гр\n" + "\tINNER JOIN Планы Пл ON Гр.КодПлана = Пл.Код\n" +
                       "\tINNER JOIN ПланыСтроки Пстр ON Пл.Код = Пстр.КодПлана\n" +
                       "\tLEFT JOIN ПланыЧасы ПЧасы ON ПСтр.Код = ПЧасы.КодСтроки AND ПЧасы.Курс = Гр.Курс\n" +
                       "\tLEFT JOIN ПланыЧасыСессии ПЧасыС ON ПЧасы.Код = ПЧасыС.КодСтрокиЧасы\n" +
                       "\tLEFT JOIN ПланыПрактики ПлПр ON Пстр.Дисциплина = ПлПр.ВидПрактики AND ПлПр.КодПлана = Пл.Код\n" +
                       "\t\tAND (ПлПр.Семестр = " + semester + " OR ПлПр.Семестр = " + (semester - 1) + ")\n" +
                       "WHERE Гр.Код_Факультета = " + idInstMine + " AND Гр.Курс = " + course + " AND Гр.Название LIKE '" + groupname +
                       "'\n" + "\tAND ((ПЧасыС.ВидСессии IS NOT NULL AND ПЧасыС.ВидСессии = " + ((semester % 2 == 1) ? 2 : 3) + ")\n" +
                       "\t\t      OR (ПЧасыС.ВидСессии IS NULL AND ПЧасы.Семестр = " + semester + "))\n" +
                       "AND ПСтр.КодДисциплины NOT IN(" + listSubject.toString().replaceAll("\\[", "").replaceAll("]", "") + ")";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("subjectname")
                              .addScalar("idSubjMine", LongType.INSTANCE)
                              .addScalar("exam", NumericBooleanType.INSTANCE)
                              .addScalar("pass", NumericBooleanType.INSTANCE)
                              .addScalar("cp", NumericBooleanType.INSTANCE)
                              .addScalar("cw", NumericBooleanType.INSTANCE)
                              .addScalar("idChairMine", LongType.INSTANCE)
                              .addScalar("type")
                              .addScalar("hoursExam", DoubleType.INSTANCE)
                              .addScalar("hoursLecture", DoubleType.INSTANCE)
                              .addScalar("hoursPractice", DoubleType.INSTANCE)
                              .addScalar("hoursLabaratory", DoubleType.INSTANCE)
                              .addScalar("hoursIndependent", DoubleType.INSTANCE)
                              .addScalar("practicType", NumericBooleanType.INSTANCE)
                              .addScalar("facultative")
                              .setResultTransformer(Transformers.aliasToBean(SubjectGroupMineModel.class));

        return (List<SubjectGroupMineModel>) getList(q);
    }

    public List<SubjectGroupMineModel> getSubjectGroup (String groupname, Long idInstMine, Integer semester, Integer course) {
        String query = "WITH searchWorkload AS (SELECT Гр.Курс, Нагр.КодДисциплины, Нагр.Дисциплина, Пл.Код AS КодПлана, \n" +
                       "\tПреп.Фамилия AS family, Преп.Имя AS name, Преп.Отчество AS patronymic, Нагр.Код AS idWorkload\n" +
                       "\tFROM Все_Группы Гр\n" + "\t    INNER JOIN Планы Пл ON Гр.КодПлана = Пл.Код\n" +
                       "\t    INNER JOIN Нагрузка Нагр ON Пл.ИмяФайла = Нагр.УчебныйПлан AND (Гр.Код = Нагр.КодГруппы OR Гр.Название = Нагр.Группа) AND Нагр.Курс = Гр.Курс\n" +
                       "\t        AND (Нагр.ПроизведённыеДействия IS NULL OR Нагр.ПроизведённыеДействия NOT LIKE 'Строка исключена из нагрузки%')\n" +
                       "\t    INNER JOIN Преподаватели Преп ON Нагр.КодПреподавателя = Преп.Код\n" + "\tWHERE Гр.Код_Факультета = " +
                       idInstMine + " AND Гр.Курс = " + course + " AND Гр.Название LIKE '" + groupname + "')\n" +
                       "SELECT  Пстр.Дисциплина AS subjectname, Пстр.КодДисциплины AS idSubjMine, Нагр.family, Нагр.name, Нагр.patronymic,\n" +
                       "\tПлПр.Экзамен AS exam, CASE WHEN (ПлПр.Зачёт = 1 OR ПлПр.ЗачётСОценкой = 1) THEN 1 ELSE 0 END pass,\n" +
                       "\tПлПр.КурсовойПроект AS cp, ПлПр.КурсоваяРабота AS cw, CASE WHEN ПлПр.ЗачётСОценкой = 1 THEN 1 ELSE 0 END type,\n" +
                       "\tCOALESCE (ПЧасыС.Лекции, ПЧасы.Лекций, 0) AS hoursLecture, COALESCE (ПЧасыС.Практики, ПЧасы.Практик, 0) AS hoursPractice,\n" +
                       "\tCOALESCE (ПЧасыС.Лабораторные, ПЧасы.Лабораторных, 0) AS hoursLabaratory, COALESCE (ПЧасыС.СамРабота, ПЧасы.СамРабота, 0) AS hoursIndependent,\n" +
                       "\tCOALESCE (ПЧасыС.ЧасовНаЭкзамены, ПЧасы.ЧасовНаЭкзамены, 0) AS hoursExam,\n" +
                       "\t1 AS practicType, Пстр.КодКафедры AS idChairMine, idWorkload, Пстр.Факультатив AS facultative\n" +
                       "FROM    searchWorkload AS Нагр\n" + "\tINNER JOIN ПланыСтроки Пстр ON Нагр.КодПлана = Пстр.КодПлана\n" +
                       "\tINNER JOIN ПланыПрактики ПлПр ON Пстр.Дисциплина = ПлПр.ВидПрактики AND ПлПр.КодПлана = Нагр.КодПлана \n" +
                       "\tLEFT JOIN ПланыЧасы ПЧасы ON ПСтр.Код = ПЧасы.КодСтроки AND ПЧасы.Курс = Нагр.Курс\n" +
                       "\tLEFT JOIN ПланыЧасыСессии ПЧасыС ON ПЧасы.Код = ПЧасыС.КодСтрокиЧасы\n" +
                       "WHERE   (Нагр.КодДисциплины = Пстр.КодДисциплины\n" + "\t        OR Нагр.Дисциплина = Пстр.Дисциплина\n" +
                       "\t        OR CONCAT(ПлПр.КатегорияПрактики,' практика/', Пстр.Дисциплина) = Нагр.Дисциплина\n" +
                       "\t        OR Нагр.Дисциплина LIKE CONCAT(ПлПр.КатегорияПрактики,' практика/', Пстр.Дисциплина, '%')\n" +
                       "\t        OR Нагр.Дисциплина LIKE CONCAT('%/', Пстр.Дисциплина)\n" +
                       "\t        OR REPLACE(REPLACE(Нагр.Дисциплина, '   ', ' '), '  ', ' ') = REPLACE(REPLACE(CONCAT(ПлПр.КатегорияПрактики,' практика/', Пстр.Дисциплина), '   ', ' '), '  ', ' ')\n" +
                       "\t        OR Нагр.КодДисциплины = (SELECT Код FROM ПланыДисциплины WHERE Дисциплина = Пстр.Дисциплина))\n" +
                       "\t AND ((ПЧасыС.ВидСессии IS NOT NULL AND ПЧасыС.ВидСессии = " + ((semester % 2 == 1) ? 2 : 3) + ")\n" +
                            "\t\tOR (ПЧасыС.ВидСессии IS NULL AND ПЧасы.Семестр = " + semester + "))\n" +
                       "UNION\n" +
                       "SELECT  Пстр.Дисциплина AS subjectname, Пстр.КодДисциплины AS idSubjMine, Нагр.family, Нагр.name, Нагр.patronymic,\n" +
                       "\tCASE WHEN (ПЧасыС.Экзамен = 1 OR (ПЧасыС.Код IS NULL AND ПЧасы.Экзамен = 1)) THEN 1 ELSE 0 END exam,\n" +
                       "\tCASE WHEN (ПЧасыС.Зачет = 1 OR ПЧасыС.ЗачетСОценкой = 1\n" +
                       "\t\tOR (ПЧасыС.Код IS NULL AND (ПЧасы.Зачет = 1 OR ПЧасы.ЗачетСОценкой = 1))) THEN 1 ELSE 0 END pass,\n" +
                       "\tCASE WHEN (ПЧасыС.КурсовойПроект = 1 OR (ПЧасыС.Код IS NULL AND ПЧасы.КурсовойПроект = 1)) THEN 1 ELSE 0 END cp,\n" +
                       "\tCASE WHEN (ПЧасыС.КурсоваяРабота = 1 OR (ПЧасыС.Код IS NULL AND ПЧасы.КурсоваяРабота = 1)) THEN 1 ELSE 0 END cw, \n" +
                       "\tCASE WHEN (ПЧасы.ЗачетСОценкой = 1 OR (ПЧасыС.Код IS NULL AND ПЧасыС.ЗачетСОценкой = 1)) THEN 1 ELSE 0 END type, \n" +
                       "\tCOALESCE (ПЧасыС.Лекции, ПЧасы.Лекций, 0) AS hoursLecture, COALESCE (ПЧасыС.Практики, ПЧасы.Практик, 0) AS hoursPractice,\n" +
                       "\tCOALESCE (ПЧасыС.Лабораторные, ПЧасы.Лабораторных, 0) AS hoursLabaratory, COALESCE (ПЧасыС.СамРабота, ПЧасы.СамРабота, 0) AS hoursIndependent,\n" +
                       "\tCOALESCE (ПЧасыС.ЧасовНаЭкзамены, ПЧасы.ЧасовНаЭкзамены, 0) AS hoursExam,\n" +
                       "\t0 AS practicType, Пстр.КодКафедры AS idChairMine, idWorkload, Пстр.Факультатив AS facultative\n" +
                       "FROM    searchWorkload AS Нагр\n" + "\tINNER JOIN ПланыСтроки Пстр ON Нагр.КодПлана = Пстр.КодПлана\n" +
                       "\tLEFT JOIN ПланыЧасы ПЧасы ON ПСтр.Код = ПЧасы.КодСтроки AND ПЧасы.Курс = Нагр.Курс\n" +
                       "\tLEFT JOIN ПланыЧасыСессии ПЧасыС ON ПЧасы.Код = ПЧасыС.КодСтрокиЧасы\n" +
                       "WHERE   ((ПЧасыС.ВидСессии IS NOT NULL AND ПЧасыС.ВидСессии = " + ((semester % 2 == 1) ? 2 : 3) + ")\n" +
                       "\t\t      OR (ПЧасыС.ВидСессии IS NULL AND ПЧасы.Семестр = " + semester + "))\n" +
                       "\tAND (Нагр.КодДисциплины = Пстр.КодДисциплины\n" + "\t        OR Нагр.Дисциплина = Пстр.Дисциплина\n" +
                       "\t        OR Нагр.КодДисциплины = (SELECT Код FROM ПланыДисциплины WHERE Дисциплина = Пстр.Дисциплина))\n" +
                       "ORDER BY subjectname";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("subjectname")
                              .addScalar("idSubjMine", LongType.INSTANCE)
                              .addScalar("exam", NumericBooleanType.INSTANCE)
                              .addScalar("pass", NumericBooleanType.INSTANCE)
                              .addScalar("cp", NumericBooleanType.INSTANCE)
                              .addScalar("cw", NumericBooleanType.INSTANCE)
                              .addScalar("idChairMine", LongType.INSTANCE)
                              .addScalar("type")
                              .addScalar("hoursExam", DoubleType.INSTANCE)
                              .addScalar("hoursLecture", DoubleType.INSTANCE)
                              .addScalar("hoursPractice", DoubleType.INSTANCE)
                              .addScalar("hoursLabaratory", DoubleType.INSTANCE)
                              .addScalar("hoursIndependent", DoubleType.INSTANCE)
                              .addScalar("family")
                              .addScalar("name")
                              .addScalar("patronymic")
                              .addScalar("practicType", NumericBooleanType.INSTANCE)
                              .addScalar("idWorkload", LongType.INSTANCE)
                              .addScalar("facultative")
                              .setResultTransformer(Transformers.aliasToBean(SubjectGroupMineModel.class));

        return (List<SubjectGroupMineModel>) getList(q);
    }

    public List<GroupMineModel> getGroupByInstAndYear (Long idInst, String year, String formOfStudy) {
        String query = "SELECT Гр.Код AS idGroupMine, Гр.Название AS groupname, Гр.Курс AS course,\n" +
                       "Спец.Название_спец AS specialityTitle, Спец.Специальность AS directionCode,\n" +
                       "RTRIM(LTRIM(CASE    WHEN Пл.Титул LIKE '%профиль: %' THEN SUBSTRING(Пл.Титул, CHARINDEX('профиль:', Пл.Титул) + LEN('профлиь:') + 1, LEN(Пл.Титул))\n" +
                       "        WHEN Пл.Титул LIKE '%профиль подготовки %' THEN SUBSTRING(Пл.Титул, CHARINDEX('профиль подготовки', Пл.Титул) + LEN('профиль подготовки') + 1, LEN(Пл.Титул))\n" +
                       "        WHEN Пл.Титул LIKE '%профиль %' THEN SUBSTRING(Пл.Титул, CHARINDEX('профиль', Пл.Титул) + LEN('профлиь') + 1, LEN(Пл.Титул))\n" +
                       "        WHEN Пл.Титул LIKE '% программа подготовки %' THEN SUBSTRING(Пл.Титул, CHARINDEX('программа подготовки', Пл.Титул) + LEN('программа подготовки') + 1, LEN(Пл.Титул))\n" +
                       "        WHEN Пл.Титул LIKE '% программа  подготовки %' THEN SUBSTRING(Пл.Титул, CHARINDEX('программа  подготовки', Пл.Титул) + LEN('программа  подготовки') + 1, LEN(Пл.Титул))\n" +
                       "        WHEN Пл.Титул LIKE '% программа %' THEN SUBSTRING(Пл.Титул, CHARINDEX('программа', Пл.Титул) + LEN('программа') + 1, LEN(Пл.Титул)) \n" +
                       "END)) AS directionTitle, \n" + "Пл.Код AS idCurriculumMine, Каф.Название AS chairName, Каф.Код AS idChairMine,\n" +
                       "CASE WHEN Гр.Форма_Обучения = 1 THEN 1 ELSE 2 END AS formOfStudy,\n" +
                       "CASE WHEN Гр.Форма_Обучения = 2 THEN 1 WHEN Гр.Форма_Обучения = 3 THEN 2 END AS distanceType,\n" +
                       "CASE WHEN RIGHT(Гр.Название, 1) = 'Б' THEN 2 WHEN RIGHT(Гр.Название,1) = 'М' THEN 3 ELSE 1 END AS qualification, \n" +
                       "CONVERT(FLOAT, REPLACE(Гр.СрокОбучения, ',', '.')) AS periodOfStudy, Пл.ИмяФайла AS planfileName,\n" +
                       "CAST(CONCAT(SUBSTRING(Пл.УчебныйГод, 0, CHARINDEX('-', Пл.УчебныйГод)), '-09-01') AS DATETIME) AS createdSchoolYear, \n" +
                       "CAST(CONCAT(Пл.ГодНачалаПодготовки, '-09-01') AS DATETIME) AS enterSchoolYear\n" + "FROM Все_Группы Гр\n" +
                       "INNER JOIN Планы Пл ON Гр.КодПлана = Пл.Код\n" +
                       "INNER JOIN Специальности Спец ON Гр.Код_специальности = Спец.код\n" +
                       "LEFT JOIN Кафедры Каф ON Спец.Код_кафедры = Каф.Код\n" +
                       "WHERE Гр.код_Факультета = :idInst AND Гр.учебныйГод = :year AND Гр.Форма_Обучения IN (" + formOfStudy + ")\n" +
                       "ORDER BY course, groupname";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("idGroupMine", LongType.INSTANCE)
                              .addScalar("groupname")
                              .addScalar("course")
                              .addScalar("specialityTitle")
                              .addScalar("directionCode")
                              .addScalar("directionTitle")
                              .addScalar("idCurriculumMine", LongType.INSTANCE)
                              .addScalar("chairName")
                              .addScalar("idChairMine", LongType.INSTANCE)
                              .addScalar("formOfStudy")
                              .addScalar("distanceType")
                              .addScalar("qualification")
                              .addScalar("periodOfStudy", FloatType.INSTANCE)
                              .addScalar("planfileName")
                              .addScalar("createdSchoolYear")
                              .addScalar("enterSchoolYear")
                              .setResultTransformer(Transformers.aliasToBean(GroupMineModel.class));
        q.setLong("idInst", idInst).setString("year", year);
        return (List<GroupMineModel>) getList(q);
    }
}
