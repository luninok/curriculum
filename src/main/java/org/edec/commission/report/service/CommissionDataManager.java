package org.edec.commission.report.service;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.commission.model.StudentDebtModel;
import org.edec.commission.model.SubjectDebtModel;
import org.edec.commission.report.dao.CommissionReportDAO;
import org.edec.commission.report.model.*;
import org.edec.commission.report.model.schedule.*;
import org.edec.commission.service.CommissionService;
import org.edec.commission.service.impl.CommissionServiceESOimpl;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by dmmax
 */
public class CommissionDataManager {
    private CommissionReportDAO commissionReportDAO = new CommissionReportDAO();
    private CommissionService commissionServiceESOimpl = new CommissionServiceESOimpl();
    private SimpleDateFormat formatYYYY = new SimpleDateFormat("YYYY");

    public JRBeanCollectionDataSource getSchedule (List<SubjectDebtModel> subjectDebtModels) {
        List<ScheduleChairModel> chairs = new ArrayList<>();
        for (SubjectDebtModel subjectDebt : subjectDebtModels) {
            String strSubjDebt = subjectDebt.getSubjectname() + "(" + subjectDebt.getFocStr() + ")";
            boolean createChair = true;
            for (ScheduleChairModel chair : chairs) {
                if (chair.getFulltitle().equals(subjectDebt.getFulltitle())) {
                    boolean addSubject = true;
                    for (ScheduleSubjectModel subject : chair.getSubjects()) {
                        if (subject.getSubjectname().equals(strSubjDebt) &&
                            ((subject.getDatecommission() == null) || subject.getDatecommission().equals(subjectDebt.getDateComission()))) {
                            createChair = false;
                            addSubject = false;
                            break;
                        }
                    }
                    if (addSubject) {
                        ScheduleSubjectModel subject = new ScheduleSubjectModel();
                        subject.setSubjectname(strSubjDebt);
                        subject.setDatecommission(subjectDebt.getDateComission());
                        subject.setClassroom(subjectDebt.getClassroom());
                        chair.getSubjects().add(subject);
                        createChair = false;
                        break;
                    }
                }
            }
            if (createChair) {
                ScheduleChairModel chair = new ScheduleChairModel();
                chair.setFulltitle(subjectDebt.getFulltitle());
                ScheduleSubjectModel subject = new ScheduleSubjectModel();
                subject.setClassroom(subjectDebt.getClassroom());
                subject.setDatecommission(subjectDebt.getDateComission());
                subject.setSubjectname(strSubjDebt);
                chair.getSubjects().add(subject);
                chairs.add(chair);
            }
        }
        return new JRBeanCollectionDataSource(chairs);
    }

    public JRBeanCollectionDataSource getRegisterBeanData () {
        /*List<EmployeeCommissionModel> listEmployee = (List<EmployeeCommissionModel>) commissionModel.getCommissionStaff();
		EmployeeCommissionModel employeeModel = null;
		for (EmployeeCommissionModel employer : listEmployee) {
			if (employer.isChairman()) {
				employeeModel = employer;
				commissionModel.setChairman(employer.getFIO());
			}
		}
		listEmployee.remove(employeeModel);


		List<CommissionModel> modelList = new ArrayList<>();
		modelList.add(commissionModel);*/
        return new JRBeanCollectionDataSource(null);
    }

    public JRBeanCollectionDataSource getListOfProtocol (Long idCommissionRegister) {
		/*List<CommissionEmployeeModel> employees = commissionReportDAO.getCommissionsEmployee(idCommissionRegister);
		List<CommissionEmployeeModel> comission = new ArrayList<>();
		CommissionEmployeeModel chairman = null;
		for (CommissionEmployeeModel employeeModel : employees) {
			if (employeeModel.getLeader() == 1)
				chairman = employeeModel;
			else
				comission.add(employeeModel);
		}
		List<ProtocolModel> list = commissionReportDAO.getProtocols(idCommissionRegister);

		for (ProtocolModel protocol : list) {
			protocol.setChairman(chairman.getFio());
			protocol.setComission(comission);
			protocol.setFullComission(employees);
		}

		List<ListProtocolModel> listProtocols = new ArrayList<>();
		ListProtocolModel listProtocolModel = new ListProtocolModel();
		listProtocolModel.setProtocols(list);
		listProtocols.add(listProtocolModel);*/
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(null);
        return dataSource;
    }

    public List<NotionModel> getListForNotionByListComissions (List<SubjectDebtModel> comissions) {
        List<NotionModel> notions = new ArrayList<>();
        List<Long> listIds = new ArrayList<>();
        List<Long> listIdsSc = new ArrayList<>();

        for (SubjectDebtModel comission : comissions) {
            List<StudentDebtModel> students = commissionServiceESOimpl.getStudentByRegisterCommission(comission.getIdRegComission());

            for (StudentDebtModel student : students) {
                boolean isFound = false;

                for (Long id : listIdsSc) {
                    if (id.equals(student.getIdSc())) {
                        isFound = true;
                        break;
                    }
                }

                if (isFound) {
                    continue;
                }

                listIds.add(student.getIdSSS());
                listIdsSc.add(student.getIdSc());
            }
        }

        if (listIds.size() == 0) {
            return new ArrayList<>();
        }

        String ids = "";
        for (int i = 0; i < listIds.size(); i++) {
            ids += listIds.get(i);
            if (i != listIds.size() - 1) {
                ids += ",";
            }
        }

        notions = commissionReportDAO.getNotionModelsByListSSS(ids);

        for (NotionModel notionModel : notions) {
            List<RatingEsoModel> listRatings = commissionReportDAO.getMarksForStudentInOrder(notionModel.getIdSSS());
            notionModel.setDebts(divideEsoModelForRatingModel(listRatings));
            notionModel.setSeason(divideEsoModelBySem(listRatings));
        }

        notions.sort((o1, o2) -> {
            if (!o1.getGroupname().equals(o2.getGroupname())) {
                return o1.getGroupname().compareTo(o2.getGroupname());
            }

            return o1.getFio().compareTo(o2.getFio());
        });

        return notions;
    }

    private String divideEsoModelForRatingModel (List<RatingEsoModel> listEsoModel) {
        String result = "";
        for (RatingEsoModel esoModel : listEsoModel) {
            if (esoModel.getExam() && esoModel.getExamrating() < 3) {
                result += esoModel.getSubjectname() + "(Экз.), ";
            }

            if (esoModel.getPass() && (esoModel.getPassrating() == 2 || esoModel.getPassrating() < 1)) {
                if (esoModel.getType() == null || esoModel.getType() == 0) {
                    result += esoModel.getSubjectname() + "(Зач.), ";
                } else {
                    result += esoModel.getSubjectname() + "(Диф. зач.), ";
                }
            }

            if (esoModel.getCp() && esoModel.getCprating() < 3) {
                result += esoModel.getSubjectname() + "(КП), ";
            }

            if (esoModel.getCw() && esoModel.getCwrating() < 3) {
                result += esoModel.getSubjectname() + "(КР), ";
            }

            if (esoModel.getPractic() && esoModel.getPracticrating() < 3) {
                result += esoModel.getSubjectname() + "(Прак.), ";
            }
        }

        if (result.length() == 0) {
            return result;
        }

        return result.substring(0, result.length() - 2) + ".";
    }

    private String divideEsoModelBySem (List<RatingEsoModel> listEsoModel) {
        String sem = "(итоги ";

        Set<SemesterRatingModel> semesters = new HashSet<>();

        for (RatingEsoModel esoModel : listEsoModel) {
            if ((esoModel.getExam() && esoModel.getExamrating() < 3) ||
                (esoModel.getPass() && (esoModel.getPassrating() == 2 || esoModel.getPassrating() < 1)) ||
                (esoModel.getCp() && esoModel.getCprating() < 3) || (esoModel.getCw() && esoModel.getCwrating() < 3) ||
                (esoModel.getPractic() && esoModel.getPracticrating() < 3)) {
                //Если это долг - вносим в список семестров
                int firstYear = Integer.parseInt(esoModel.getSemester().substring(0, 4));
                int secondYear = Integer.parseInt(esoModel.getSemester().substring(5, 9));

                int season = esoModel.getSemester().contains("осеннего") ? 0 : 1;

                SemesterRatingModel semesterRatingModel = new SemesterRatingModel();
                semesterRatingModel.setStartYear(firstYear);
                semesterRatingModel.setEndYear(secondYear);
                semesterRatingModel.setSeason(season);

                semesters.add(semesterRatingModel);
            }
        }

        List<SemesterRatingModel> semesterList = new ArrayList<>(semesters);
        Collections.sort(semesterList);

        for (int i = 0; i < semesterList.size(); i++) {
            SemesterRatingModel semesterRatingModel = semesterList.get(i);
            //Если семестр весенний - сразу добавляем строчку с годом
            if (semesterRatingModel.getSeason() == 1) {
                sem += "весеннего семестра " + semesterRatingModel.getStartYear() + "-" + semesterRatingModel.getEndYear() + " года, ";
                continue;
            }

            //если семестр осенний, смотрим - есть ли следующий элемент, если нет - добавляем строчку
            if (i == semesterList.size() - 1) {
                sem += "осеннего семестра " + semesterRatingModel.getStartYear() + "-" + semesterRatingModel.getEndYear() + " года, ";
                continue;
            }

            //если следующий элемент это весенний семестр этого же года - формируем строку по 2 элементам
            if (semesterList.get(i + 1).getSeason() == 1 && semesterList.get(i + 1).getStartYear() == semesterRatingModel.getStartYear()) {
                sem += "осеннего и весеннего семестров " + semesterRatingModel.getStartYear() + "-" + semesterRatingModel.getEndYear() +
                       " года, ";
                //пропускаем этот элемент
                i++;
                continue;
            }

            //иначе просто добавляем
            sem += "осеннего семестра " + semesterRatingModel.getStartYear() + "-" + semesterRatingModel.getEndYear() + " года, ";
        }

        sem = sem.substring(0, sem.length() - 2) + ")";

        return sem;
    }

    public JRBeanCollectionDataSource getScheduleByFormOfStudy (Date dateOfBegin, Date dateOfEnd, Integer fos) {
        List<ListOfStudentByFormOfStudy> models = commissionReportDAO.getSceduliesByFormOfStudy(dateOfBegin, dateOfEnd, fos);

        List<FormOfStudyModel> formOfStudies = new ArrayList<>();
        for (ListOfStudentByFormOfStudy model : models) {
            boolean addFormOfStudy = true;
            for (FormOfStudyModel formOfStudy : formOfStudies) {
                if (formOfStudy.getFormOfStudy().equals(model.getFormOfStudy())) {
                    setCourseForFOS(formOfStudy, model);
                    addFormOfStudy = false;
                    break;
                }
            }
            if (addFormOfStudy) {
                FormOfStudyModel formOfStudy = new FormOfStudyModel();
                formOfStudy.setFormOfStudy(model.getFormOfStudy());
                setCourseForFOS(formOfStudy, model);
                formOfStudies.add(formOfStudy);
            }
        }

        return new JRBeanCollectionDataSource(formOfStudies);
    }

    private void setCourseForFOS (FormOfStudyModel formOfStudy, ListOfStudentByFormOfStudy model) {
        boolean addCourse = true;
        for (CourseModel course : formOfStudy.getCourses()) {
            if (course.getCourse().equals(model.getCourse())) {
                setGroupForCourse(course, model);
                addCourse = false;
                break;
            }
        }
        if (addCourse) {
            CourseModel course = new CourseModel();
            course.setCourse(model.getCourse());
            setGroupForCourse(course, model);
            formOfStudy.getCourses().add(course);
        }
    }

    private void setGroupForCourse (CourseModel course, ListOfStudentByFormOfStudy model) {
        boolean addGroup = true;
        for (GroupModel group : course.getGroups()) {
            if (group.getGroupname().equals(model.getGroupname())) {
                setStudentForGroup(group, model);
                addGroup = false;
                break;
            }
        }
        if (addGroup) {
            GroupModel group = new GroupModel();
            group.setGroupname(model.getGroupname());
            setStudentForGroup(group, model);
            course.getGroups().add(group);
        }
    }

    private void setStudentForGroup (GroupModel group, ListOfStudentByFormOfStudy model) {
        StudentModel student = new StudentModel();
        student.setFio(model.getFio());
        student.setRecordbook(model.getRecordbook());
        group.getStudents().add(student);
    }

    public JRBeanCollectionDataSource getListOfStudentByChair (Date dateOfBegin, Date dateOfEnd, Integer formOfStudy) {
        List<ListOfStudentByChair> models = commissionReportDAO.getListOfModuleByChair(dateOfBegin, dateOfEnd, formOfStudy);
        List<ScheduleChairModel> chairs = new ArrayList<>();
        for (ListOfStudentByChair model : models) {
            boolean addChair = true;
            for (ScheduleChairModel chair : chairs) {
                if (chair.getFulltitle().equals(model.getFulltitle())) {
                    setSubjectForChair(chair, model);
                    addChair = false;
                    break;
                }
            }
            if (addChair) {
                ScheduleChairModel chair = new ScheduleChairModel();
                chair.setFulltitle(model.getFulltitle());
                setSubjectForChair(chair, model);
                chairs.add(chair);
            }
        }
        return new JRBeanCollectionDataSource(chairs);
    }

    private void setSubjectForChair (ScheduleChairModel chair, ListOfStudentByChair model) {
        boolean addSubject = true;
        for (ScheduleSubjectModel subject : chair.getSubjects()) {
            if (subject.getSubjectname().equals(model.getSubjectname())) {
                setStudentForSubject(subject, model);
                addSubject = false;
                break;
            }
        }
        if (addSubject) {
            ScheduleSubjectModel subject = new ScheduleSubjectModel();
            subject.setSubjectname(model.getSubjectname());
            setStudentForSubject(subject, model);
            chair.getSubjects().add(subject);
        }
    }

    private void setStudentForSubject (ScheduleSubjectModel subject, ListOfStudentByChair model) {
        StudentModel student = new StudentModel();
        student.setFio(model.getFio());
        student.setGroupname(model.getGroupname());
        subject.getStudents().add(student);
    }
}
