package org.edec.passportGroup.service.impl;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.edec.admin.model.EmployeeModel;
import org.edec.passportGroup.manager.PassportGroupManager;
import org.edec.passportGroup.model.*;
import org.edec.passportGroup.model.eso.*;
import org.edec.passportGroup.service.PassportGroupService;
import org.edec.register.manager.RegisterManager;
import org.edec.register.model.RetakeModel;
import org.edec.register.model.SessionRatingHistoryModel;
import org.edec.register.model.dao.RetakeModelEso;
import org.edec.utility.constants.*;
import org.edec.utility.converter.DateConverter;

import java.util.*;

import static org.edec.register.service.RegisterService.INDIVIDUAL_RETAKE;
import static org.edec.utility.constants.FormOfControlConst.*;

/**
 * Created by antonskripacev on 09.04.17.
 */
@Log4j
public class PassportGroupServiceESO implements PassportGroupService {

    private PassportGroupManager manager = new PassportGroupManager();
    private RegisterManager registerManager = new RegisterManager();

    @Override
    public List<SemesterModel> getSemestersByParams(long idInstitute, int typeOfSemester, int formOfStudy) {
        return manager.getSemesterByParams(idInstitute, typeOfSemester, formOfStudy);
    }

    @Override
    public List<GroupModel> getGroupsByFilter(long idSemester, int course, String group, boolean isBachelor, boolean isMaster,
                                              boolean isEngineer) {
        return manager.getGroupsByFilter(idSemester, course, group, isBachelor, isMaster, isEngineer);
    }

    @Override
    public List<StudentModel> getStudentsByGroup(long idLgs) {
        List<StudentModelESO> studentsESO = manager.getStudentsByGroup(idLgs);
        List<StudentModel> students = new ArrayList<>();

        StudentModel student = null;
        for (StudentModelESO aStudentsESO : studentsESO) {
            if (student == null || !student.getIdSSS().equals(aStudentsESO.getIdSSS())) {
                student = constructStudent(aStudentsESO);
                students.add(student);
            }

            constructRating(aStudentsESO, student);
        }

        students.sort(Comparator.comparing(StudentModel::getFullName));

        return students;
    }

    private StudentModel constructStudent(StudentModelESO studentModelESO) {
        StudentModel student = new StudentModel();
        student.setFullName(studentModelESO.getFamily() + " " + studentModelESO.getName() + " " + studentModelESO.getPatronymic());
        student.setIdSSS(studentModelESO.getIdSSS());
        student.setRatings(new ArrayList<>());
        student.setDeducted(studentModelESO.getDeducted());
        student.setAcademicLeave(studentModelESO.getAcademicLeave());
        student.setListener(studentModelESO.getListener());
        student.setGovernmentFinanced(studentModelESO.getGovernmentFinanced());
        student.setIdDicGroup(studentModelESO.getIdDicGroup());
        student.setIdCurrentDicGroup(studentModelESO.getIdCurrentDicGroup());

        return student;
    }

    private void constructRating(StudentModelESO studentESO, StudentModel student) {
        if (studentESO.getExam()) {
            student.getRatings().add(createRating(studentESO, EXAM, studentESO.getExamRating(),studentESO.getEsoExamRating(), student));
        }

        if (studentESO.getPass()) {
            student.getRatings().add(createRating(studentESO, FormOfControlConst.PASS, studentESO.getPassRating(),studentESO.getEsoPassRating(), student));
        }

        if (studentESO.getCp()) {
            student.getRatings().add(createRating(studentESO, FormOfControlConst.CP, studentESO.getCPRating(), studentESO.getEsoCPRating(), student));
        }

        if (studentESO.getCw()) {
            student.getRatings().add(createRating(studentESO, FormOfControlConst.CW, studentESO.getCWRating(), studentESO.getEsoCWRating(), student));
        }

        if (studentESO.getPractic()) {
            student.getRatings().add(createRating(studentESO, FormOfControlConst.PRACTIC, studentESO.getPracticRating(), 0, student));
        }
    }

    private RatingModel createRating(StudentModelESO studentESO, FormOfControlConst foc, Integer rat, Integer esoRat,
                                     StudentModel student) {
        RatingModel rating = new RatingModel();
        rating.setIdSR(studentESO.getIdSR());
        rating.setIdSubject(studentESO.getIdSubject());
        rating.setFoc(foc);
        rating.setRating((rat != -1 && rat != 1 && rat != 3 && rat != 4 && rat != 5) ? esoRat : rat);
        rating.setStatus(studentESO.getStatus());
        rating.setStudent(student);
        rating.setType(studentESO.getType());

        return rating;
    }

    @Override
    public List<SubjectModel> getSubjectsByGroup(long idLgs) {
        List<SubjectModelESO> listSubjectESO = manager.getSubjectsByGroup(idLgs);
        List<SubjectModel> subjects = new ArrayList<>();

        for (SubjectModelESO aListSubjectESO : listSubjectESO) {
            if (aListSubjectESO.getExam()) {
                subjects.add(
                        constructSubject(aListSubjectESO.getHoursCount(), aListSubjectESO.getIdLgss(), aListSubjectESO.getIdSubject(), EXAM,
                                         aListSubjectESO.getSubjectName()
                        ));
            }

            if (aListSubjectESO.getPass()) {
                subjects.add(constructSubject(aListSubjectESO.getHoursCount(), aListSubjectESO.getIdLgss(), aListSubjectESO.getIdSubject(),
                                              FormOfControlConst.PASS, aListSubjectESO.getSubjectName()
                ));
            }

            if (aListSubjectESO.getCp()) {
                subjects.add(constructSubject(aListSubjectESO.getHoursCount(), aListSubjectESO.getIdLgss(), aListSubjectESO.getIdSubject(),
                                              FormOfControlConst.CP, aListSubjectESO.getSubjectName()
                ));
            }

            if (aListSubjectESO.getCw()) {
                subjects.add(constructSubject(aListSubjectESO.getHoursCount(), aListSubjectESO.getIdLgss(), aListSubjectESO.getIdSubject(),
                                              FormOfControlConst.CW, aListSubjectESO.getSubjectName()
                ));
            }

            if (aListSubjectESO.getPractic()) {
                subjects.add(constructSubject(aListSubjectESO.getHoursCount(), aListSubjectESO.getIdLgss(), aListSubjectESO.getIdSubject(),
                                              FormOfControlConst.PRACTIC, aListSubjectESO.getSubjectName()
                ));
            }
        }

        Collections.sort(subjects, Comparator.comparing(o -> o.getFoc().getValue()));

        return subjects;
    }

    public List<EmployeeModel> getEmployeesBySubject(long idLgss) {
        return manager.getEmployeesBySubject(idLgss);
    }

    private SubjectModel constructSubject(Double hours, Long idLgss, Long idSubject, FormOfControlConst foc, String subjectName) {
        SubjectModel subject = new SubjectModel();
        subject.setFoc(foc);
        subject.setIdSubject(idSubject);
        subject.setIdLgss(idLgss);
        subject.setSubjectName(subjectName);
        subject.setCountHours(hours);

        return subject;
    }

    @Override
    public String[] getRegistersBySr(Long idSr, FormOfControlConst foc) {
        List<String> registers = manager.getRegistersBySr(idSr, foc);
        return registers.toArray(new String[registers.size()]);
    }

    // Список предметов
    public List<SubjectReportModel> getSubjectsReport(List<GroupModel> groupList, int formCtrl, int status, String searchStr) {

        List<SubjectReportModelESO> subjectsESO = manager.getSubjectsReport(groupList);
        List<SubjectReportModel> listSubjects = new ArrayList<>();

        SubjectReportModel prevModel = null;

        for (SubjectReportModelESO aSubjectsESO : subjectsESO) {

            SubjectReportModel currModel = createSubject(aSubjectsESO);
            boolean isAddDep = true;
            if (prevModel != null && currModel.getIdSubject().equals(prevModel.getIdSubject())) {
                for (int e = 0; e < prevModel.getListEmployees().size(); e++) {
                    for (int j = 0; j < currModel.getListEmployees().size(); j++) {
                        if (prevModel.getListEmployees().get(e).getIdTeacher().equals(currModel.getListEmployees().get(j).getIdTeacher())) {
                            prevModel.getListEmployees().get(e).getListDepTitles()
                                     .add(currModel.getListEmployees().get(j).getListDepTitles().get(0));
                            isAddDep = false;
                        }
                    }
                }
                if (currModel.getIdSubject().equals(prevModel.getIdSubject()) && isAddDep) {
                    prevModel.getListEmployees().add(currModel.getListEmployees().get(0));
                }
            } else {
                prevModel = currModel;
                listSubjects.add(prevModel);
            }
        }

        return listSubjects;
    }

    private SubjectReportModel createSubject(SubjectReportModelESO subjectsESO) {
        SubjectReportModel subject = new SubjectReportModel();

        subject.setIdLgss(subjectsESO.getIdLgss());
        subject.setIdSubject(subjectsESO.getIdSubject());
        subject.setIdDicSubject(subjectsESO.getIdDicSubject());
        subject.setIdChair(subjectsESO.getIdChair());
        subject.setSubjectName(subjectsESO.getSubjectName());
        subject.setDepTitle(subjectsESO.getSubjDepTitle());
        subject.setOtherDbId(subjectsESO.getOtherDbId());
        subject.setHoursCount(subjectsESO.getHoursCount());
        subject.setHoursAudCount(subjectsESO.getHoursAudCount());
        subject.setHoursLabor(subjectsESO.getHoursLabor());
        subject.setHoursLection(subjectsESO.getHoursLection());
        subject.setHoursPractic(subjectsESO.getHoursPractic());
        subject.setExam(subjectsESO.getExam());
        subject.setPass(subjectsESO.getPass());
        subject.setCp(subjectsESO.getCp());
        subject.setCw(subjectsESO.getCw());
        subject.setPractic(subjectsESO.getPractic());
        subject.setCheckExamDate(subjectsESO.getExamDate());
        subject.setCheckPassDate(subjectsESO.getPassDate());
        subject.setCheckCPDate(subjectsESO.getCourseProjectDate());
        subject.setCheckCWDate(subjectsESO.getCourseWorkDate());
        subject.setCheckPracticDate(subjectsESO.getPracticDate());
        subject.setConsultDate(subjectsESO.getConsultDate());
        subject.setSynchMine(subjectsESO.getSynchMine());
        subject.setType(subjectsESO.getType());

        if (subjectsESO.getExam()) {
            subject.setFoc(FormOfControlConst.EXAM);
            subject.setCheckDate(subjectsESO.getExamDate());
        } else if (subjectsESO.getPass()) {
            subject.setFoc(FormOfControlConst.PASS);
            subject.setCheckDate(subjectsESO.getPassDate());
        } else if (subjectsESO.getCp()) {
            subject.setFoc(FormOfControlConst.CP);
            subject.setCheckDate(subjectsESO.getCourseProjectDate());
        } else if (subjectsESO.getCw()) {
            subject.setFoc(FormOfControlConst.CW);
            subject.setCheckDate(subjectsESO.getCourseWorkDate());
        } else if (subjectsESO.getPractic()) {
            subject.setFoc(FormOfControlConst.PRACTIC);
            subject.setCheckDate(subjectsESO.getPracticDate());
        }

        subject.setGroupName(subjectsESO.getGroupName());
        subject.setListEmployees(new ArrayList<>());

        if (subjectsESO.getIdEmployee() != null) {
            TeacherModel teacher = new TeacherModel();
            teacher.setIdTeacher(subjectsESO.getIdEmployee());
            teacher.setIdLesg(subjectsESO.getIdLesg());
            teacher.setFullName(subjectsESO.getFullName());
            teacher.setListDepTitles(new ArrayList<>());
            teacher.setInstTitle(subjectsESO.getInstTitle());
            teacher.getListDepTitles().add(subjectsESO.getDepTitle());

            subject.setStatus("Прикреплен");

            subject.getListEmployees().add(teacher);
        } else {
            subject.setStatus("Не прикреплен");
        }

        subject.setConsultDate(subjectsESO.getConsultDate());

        return subject;
    }

    // Список преподавателей
    public List<TeacherModel> getTeachers(String teacher, String inst, String dep) {
        List<TeacherModelESO> teachersESO = manager.getTeachers();
        List<TeacherModel> listTeachers = new ArrayList<>();

        TeacherModel prevModel = null;
        for (TeacherModelESO aTeachersESO : teachersESO) {
            TeacherModel currModel = createTeacher(aTeachersESO);
            if (prevModel != null && currModel.getIdTeacher().equals(prevModel.getIdTeacher())) {
                prevModel.getListDepTitles().add(currModel.getListDepTitles().get(0));
            } else {
                prevModel = currModel;
                listTeachers.add(prevModel);
            }
        }

        for (TeacherModel listTeacher : listTeachers) {
            listTeacher.getListDepTitles().sort(Comparator.comparing(String::toString));

            for (int l = 0; l < listTeacher.getListDepTitles().size(); l++) {
                if (l != 0) {
                    if ((listTeacher.getListDepTitles().size() > 1) &&
                        (listTeacher.getListDepTitles().get(l).equals(listTeacher.getListDepTitles().get(l - 1)))) {
                        listTeacher.getListDepTitles().remove(l);
                        if (listTeacher.getListDepTitles().size() > 1) {
                            l = 0;
                        }
                    }
                }
            }
        }

        listTeachers.sort(Comparator.comparing(TeacherModel::getFullName));

        if (!teacher.isEmpty()) {
            for (int i = 0; i < listTeachers.size(); i++) {
                if (!listTeachers.get(i).getFullName().toLowerCase().contains(teacher.toLowerCase())) {
                    listTeachers.remove(i);
                    i = 0;
                }
            }
            if (listTeachers.size() > 0) {
                if (!listTeachers.get(0).getFullName().toLowerCase().contains(teacher.toLowerCase())) {
                    listTeachers.remove(0);
                }
            }
        }
        if (!inst.isEmpty()) {
            for (int i = 0; i < listTeachers.size(); i++) {
                if (!listTeachers.get(i).getInstTitle().toLowerCase().contains(inst.toLowerCase())) {
                    listTeachers.remove(i);
                    i = 0;
                }
            }
            if (listTeachers.size() > 0) {
                if (!listTeachers.get(0).getInstTitle().toLowerCase().contains(inst.toLowerCase())) {
                    listTeachers.remove(0);
                }
            }
        }
        if (!dep.isEmpty()) {
            String depStr;
            for (int i = 0; i < listTeachers.size(); i++) {
                depStr = "";
                for (int j = 0; j < listTeachers.get(i).getListDepTitles().size(); j++) {
                    depStr += listTeachers.get(i).getListDepTitles().get(j) + " ";
                }
                if (!depStr.toLowerCase().contains(dep.toLowerCase())) {
                    listTeachers.remove(i);
                    i = 0;
                }
            }
            depStr = "";
            if (listTeachers.size() > 0) {
                for (int j = 0; j < listTeachers.get(0).getListDepTitles().size(); j++) {
                    depStr += listTeachers.get(0).getListDepTitles().get(j) + " ";
                }
                if (!listTeachers.get(0).getListDepTitles().get(0).toLowerCase().contains(dep.toLowerCase())) {
                    listTeachers.remove(0);
                }
            }
        }
        return listTeachers;
    }

    private TeacherModel createTeacher(TeacherModelESO teacherESO) {
        TeacherModel teacher = new TeacherModel();

        teacher.setIdLesg(teacherESO.getIdLesg());
        teacher.setIdTeacher(teacherESO.getIdTeacher());
        teacher.setFullName(teacherESO.getFullName());
        teacher.setInstTitle(teacherESO.getInstTitle());
        teacher.setListDepTitles(new ArrayList<>());
        teacher.getListDepTitles().add(teacherESO.getDepTitle());

        return teacher;
    }

    // Прикрепление преподавателя
    public boolean addTeacherToSubject(Long idTeacher, Long idLgss) {
        return manager.addTeacher(idTeacher, idLgss);
    }

    // Открепление преподавателя
    public boolean removeTeacherFromSubject(Long idLesg) {
        return manager.removeTeacherFromSubject(idLesg);
    }

    //Стипендии
    @Override
    public void setScholarshipInfoForListStudents(List<StudentModel> listStudents) {
        if (listStudents == null || listStudents.size() == 0) {
            return;
        }

        StringBuilder ids = new StringBuilder();
        for (StudentModel studentModel : listStudents) {
            ids.append(studentModel.getIdSSS()).append(",");
        }

        ids = new StringBuilder(ids.substring(0, ids.lastIndexOf(",")));

        List<ScholarshipInfo> listScholarship = manager.getScholarshipInfoByStudentIds(ids.toString());

        for (StudentModel student : listStudents) {
            student.setListScholarshipInfo(new ArrayList<>());

            for (ScholarshipInfo scholarshipInfo : listScholarship) {
                if (student.getIdSSS().equals(scholarshipInfo.getIdSSS())) {
                    student.getListScholarshipInfo().add(scholarshipInfo);
                }
            }
        }
    }

    @Override
    public boolean cancelScholarship(StudentModel studentModel, ScholarshipInfo scholarshipInfo, Date dateCancel, String orderNumber,
                                     String userName, Long idHumanface) {
        if (manager.cancelScholarship(scholarshipInfo, dateCancel, orderNumber, idHumanface)) {
            log.info("Пользователь " + userName + " отменил стипендию студенту " + studentModel.getFullName());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean setScholarship(StudentModel studentModel, ScholarshipInfo scholarshipInfo, Date dateSet, Date dateStart, Date dateFinish,
                                  String orderNumber, String userName, Long idHumanface) {
        if (manager.setScholarship(scholarshipInfo, dateSet, dateStart, dateFinish, orderNumber, idHumanface)) {
            log.info("Пользователь " + userName + " назначил стипендию студенту " + studentModel.getFullName() + "; сроки: " +
                     DateConverter.convertDateToString(dateStart) + " - " + DateConverter.convertDateToString(dateFinish));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean openRetake(SubjectModel subject, List<StudentModel> studentsList, GroupModel group, Date dateOfBegin, Date dateOfEnd,
                              String userFio) {

        boolean isAllSuccessful = false;

        for (StudentModel student : studentsList) {

            List<RetakeModel> listRetakes = separateListRetakesByIdSRH(registerManager.getListRatingByListGroupSubjects(
                    Long.toString(subject.getIdLgss()), getFokQueryForSubject(subject.getFoc().getValue()),
                    getFocQueryForLeftJoin(subject.getFoc().getValue())
            ));

            retakeCycle:
            for (RetakeModel retakeModel : listRetakes) {

                if (!retakeModel.getIdSSS().equals(student.getIdSSS())) {
                    continue retakeCycle;
                }

                isAllSuccessful = true;

                for (SessionRatingHistoryModel historyModel : retakeModel.getListSRH()) {
                    if (historyModel.getIdSRH() != null && historyModel.getRetakeCount() == RegisterConst.TYPE_RETAKE_MAIN_NOT_SIGNED) {
                        isAllSuccessful = false;
                        continue retakeCycle;
                    }
                }

                if (retakeModel.getDeductedCurSem() == null) {
                    isAllSuccessful = false;
                    continue;
                }

                if (retakeModel.getDeductedCurSem()) {
                    isAllSuccessful = false;
                    continue;
                }

                switch (FormOfControlConst.getName(subject.getFoc().getValue())) {
                    case EXAM:
                        if (!isMarkNegative(retakeModel.getExamRating())) {
                            isAllSuccessful = false;
                            continue;
                        }
                        break;
                    case PASS:
                        if (!isMarkNegative(retakeModel.getPassRating())) {
                            isAllSuccessful = false;
                            continue;
                        }
                        break;
                    case CP:
                        if (!isMarkNegative(retakeModel.getCpRating())) {
                            isAllSuccessful = false;
                            continue;
                        }
                        break;
                    case CW:
                        if (!isMarkNegative(retakeModel.getCwRating())) {
                            isAllSuccessful = false;
                            continue;
                        }
                        break;
                    case PRACTIC:
                        if (!isMarkNegative(retakeModel.getPracticRating())) {
                            isAllSuccessful = false;
                            continue;
                        }
                        break;
                    default:
                        continue;
                }

                for (SessionRatingHistoryModel historyModel : retakeModel.getListSRH()) {
                    if (historyModel.getIdSRH() != null && historyModel.getRetakeCount() == RegisterConst.TYPE_RETAKE_INDIV_NOT_SIGNED) {
                        isAllSuccessful = false;
                        continue retakeCycle;
                    }
                }

                long idGroup = manager.getGroupByIdLgss(subject.getIdLgss());

                if (!retakeModel.getIdCurDicGroup().equals(idGroup)) {
                    isAllSuccessful = false;
                    continue retakeCycle;
                }

                if (retakeModel.getAcademicLeaveCurSem()) {
                    isAllSuccessful = false;
                    continue retakeCycle;
                }

                if (!registerManager
                        .createRetakeForModel(FormOfControlConst.getName(subject.getFoc().getValue()), retakeModel, INDIVIDUAL_RETAKE,
                                              dateOfBegin, dateOfEnd
                        )) {
                    log.error("Не удалось открыть индивидуальную пересдачу" + "; студенту " + retakeModel.getFio() + "; группа " +
                              group.getGroupName() + "; по предмету " + subject.getSubjectName() + "; пользователь " + userFio);
                    return false;
                } else {
                    log.info("Пользователь " + userFio + " открыл индивидуальную пересдачу" + "; студенту " + retakeModel.getFio() +
                             "; группа " + group.getGroupName() + "; по предмету " + subject.getSubjectName());
                }
            }
        }

        return isAllSuccessful;
    }

    @Override
    public boolean changeRating(RetakeModel retakeModel, FormOfControlConst foc, int newRating, int oldRating, String userFio,
                                Long userHumanfaceId, String groupName, String subjectName, String studentName) {
        boolean srhWasChanged;

        if (newRating == RatingConst.ZERO.getRating()) {
            srhWasChanged = manager.deleteSRH(retakeModel.getIdSR());
        } else {
            srhWasChanged = manager.createSRH(retakeModel, foc, newRating, oldRating, userHumanfaceId);
        }

        if (srhWasChanged && manager.updateSR(retakeModel, foc, newRating)) {
            log.info("Пользователь " + userFio + " изменил оценку " + "; студенту " + studentName + "; группа " + groupName +
                     "; по предмету " + subjectName + "; форма контроля " + foc.getName() + "; старая оценка " + oldRating +
                     "; новая оценка " + newRating);
            return true;
        } else {
            log.error(
                    "Не удалось изменить оценку " + "; студенту " + studentName + "; группа " + groupName + "; по предмету " + subjectName +
                    "; форма контроля " + foc.getName() + "; старая оценка " + oldRating + "; новая оценка " + newRating +
                    "; пользователь " + userFio);
            return false;
        }
    }

    private String getFokQueryForSubject(int foc) {
        switch (FormOfControlConst.getName(foc)) {
            case EXAM:
                return "is_exam = 1 or srh.is_exam is null";
            case PASS:
                return "is_pass = 1 or srh.is_pass is null";
            case CP:
                return "is_courseproject = 1 or srh.is_courseproject is null";
            case CW:
                return "is_coursework = 1 or srh.is_coursework is null";
            case PRACTIC:
                return "is_practic = 1 or srh.is_practic is null";
            default:
                return null;
        }
    }

    private String getFocQueryForLeftJoin(int foc) {
        switch (FormOfControlConst.getName(foc)) {
            case EXAM:
                return "srh.is_exam = 1";
            case PASS:
                return "srh.is_pass = 1";
            case CP:
                return "srh.is_courseproject = 1";
            case CW:
                return "srh.is_coursework = 1";
            case PRACTIC:
                return "srh.is_practic = 1";
            default:
                return null;
        }
    }

    private List<RetakeModel> separateListRetakesByIdSRH(List<RetakeModelEso> listESO) {
        List<RetakeModel> retakeModels = new ArrayList<>();
        RetakeModel prevModel = null;
        for (RetakeModelEso retakeModelEso : listESO) {
            if (prevModel != null && prevModel.getIdSR().equals(retakeModelEso.getIdSR())) {
                prevModel.getListSRH().add(createSessionRatingHistoryModel(retakeModelEso));
            } else {
                prevModel = createRetakeModel(retakeModelEso);
                prevModel.getListSRH().add(createSessionRatingHistoryModel(retakeModelEso));
                retakeModels.add(prevModel);
            }
        }

        return retakeModels;
    }

    private SessionRatingHistoryModel createSessionRatingHistoryModel(RetakeModelEso retakeModelEso) {
        SessionRatingHistoryModel srhModel = new SessionRatingHistoryModel();
        srhModel.setIdSRH(retakeModelEso.getIdSRH());
        srhModel.setRetakeCount(retakeModelEso.getRetakeCount());
        return srhModel;
    }

    private RetakeModel createRetakeModel(RetakeModelEso retakeModelEso) {
        RetakeModel retakeModel = new RetakeModel();
        retakeModel.setAcademicLeaveCurSem(retakeModelEso.getAcademicLeave());
        retakeModel.setIdSemester(retakeModelEso.getIdSemester());
        retakeModel.setCpRating(retakeModelEso.getCpRating());
        retakeModel.setCwRating(retakeModelEso.getCwRating());
        retakeModel.setDeductedCurSem(retakeModelEso.getDeductedCurSem());
        retakeModel.setAcademicLeaveCurSem(retakeModelEso.getAcademicLeaveCurSem());
        retakeModel.setExamRating(retakeModelEso.getExamRating());
        retakeModel.setFio(retakeModelEso.getFio());
        retakeModel.setIdCurDicGroup(retakeModelEso.getIdCurDicGroup());
        retakeModel.setIdSR(retakeModelEso.getIdSR());
        retakeModel.setIdSSS(retakeModelEso.getIdSSS());
        retakeModel.setListenerCurSem(retakeModelEso.getListenerCurSem());
        retakeModel.setTransferedStudent(retakeModelEso.getTransferedStudent());
        retakeModel.setTransferedStudentCurSem(retakeModelEso.getTransferedStudentCurSem());
        retakeModel.setPracticRating(retakeModelEso.getPracticRating());
        retakeModel.setPassRating(retakeModelEso.getPassRating());
        retakeModel.setType(retakeModelEso.getType());
        return retakeModel;
    }

    private boolean isMarkNegative(Integer mark) {
        return mark == null || mark < 3 && mark != 1;
    }

    @Override
    public void setStatisticForSubjects(List<StudentModel> students, List<SubjectModel> subjects) {
        for (SubjectModel subjectModel : subjects) {
            int sumStatistic = 0;
            int countEmpty = 0;

            student:
            for (StudentModel studentModel : students) {
                if (studentModel.getRatings() != null) {
                    for (RatingModel ratingModel : studentModel.getRatings()) {
                        if (ratingModel.getIdSubject().equals(subjectModel.getIdSubject())) {
                            if (ratingModel.getRating() > 0 && ratingModel.getRating() != 2) {
                                sumStatistic++;
                            }
                            continue student;
                        }
                    }
                } else {
                    countEmpty++;
                }
            }

            subjectModel.setStatistic((sumStatistic * 100.0 / (students.size() - countEmpty)));
            subjectModel.setStatisticStr(subjectModel.getStatistic().intValue() + "%(" + sumStatistic + ")");
        }
    }

    @Override
    public int getRetakeCount(long idSR) {
        return manager.getRetakeCountBySR(idSR);
    }

    @Override
    public int countSR(long idLGSS) {
        return manager.countSR(idLGSS);
    }

    @Override
    public boolean deleteSubject(long idLGSS, boolean hasMarks, boolean hasMultipleFoc, FormOfControlConst foc) {
        String focUpdate = queryForStatus(foc);
        String markUpdate = queryForMark(foc);

        if (hasMarks) {
            if (hasMultipleFoc) {
                if (!manager.updateSRH(idLGSS, focUpdate) || !manager.updateSR(idLGSS, focUpdate, markUpdate)) {
                    return false;
                }
            } else {
                if (!manager.deleteSrhByLgss(idLGSS) || !manager.deleteSR(idLGSS)) {
                    return false;
                }
            }
        }

        if (hasMultipleFoc) {
            if (!manager.updateSubjectStatus(idLGSS, focUpdate)) {
                return false;
            }
        } else {
            if (!manager.deleteLESG(idLGSS) || !manager.deleteAttendance(idLGSS) || !manager.deleteLGScheduleS(idLGSS) ||
                !manager.deleteRegisterRequest(idLGSS) || !manager.deleteLGSS(idLGSS) || !manager.deleteSubject(idLGSS)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public List<SubjectModel> getSubjectList() {
        return manager.getSubjectList();
    }

    @Override
    public List<DepartmentModel> getDepartmentList() {
        return manager.getDepartmentList();
    }

    @Override
    public long createDicSubject(String subjectName) {
        return manager.createDicSubject(subjectName);
    }

    @Override
    public long createSubject(SubjectModel subject) {
        return manager.createSubject(subject);
    }

    @Override
    public boolean createLinkGroupSemesterSubject(LinkGroupSemesterSubjectModel lgss) {
        return manager.createLinkGroupSemesterSubject(lgss);
    }

    @Override
    public boolean createSessionRating(SessionRatingModel sr) {
        return manager.createSessionRating(sr);
    }

    @Override
    public List<Long> getStudentSemesterStatusList(long idLGS) {
        return manager.getStudentSemesterStatusList(idLGS);
    }

    @Override
    public boolean updateDicSubject(long idDicSubject, String subjectName) {
        return manager.updateDicSubject(idDicSubject, subjectName);
    }

    @Override
    public boolean updateSubject(SubjectModel subject) {
        return manager.updateSubject(subject);
    }

    @Override
    public boolean updateSessionRating(SessionRatingModel sr) {
        return manager.updateSessionRating(sr);
    }

    @Override
    public List<SubjectReportModel> getSubjectListModel(List<SubjectReportModel> list) {
        List<SubjectReportModel> subjectReportModelList = new ArrayList<>();
        for (SubjectReportModel subjectReportModel : list) {
            if (subjectReportModel.getExam()) {
                SubjectReportModel subject = subjectReportModel.clone();
                subject.setFoc(FormOfControlConst.EXAM);
                subject.setCheckDate(subject.getCheckExamDate());
                subjectReportModelList.add(subject);
            }
            if (subjectReportModel.getPass()) {
                SubjectReportModel subject = subjectReportModel.clone();
                subject.setFoc(FormOfControlConst.PASS);
                subject.setCheckDate(subject.getCheckPassDate());
                subjectReportModelList.add(subject);
            }
            if (subjectReportModel.getCp()) {
                SubjectReportModel subject = subjectReportModel.clone();
                subject.setFoc(FormOfControlConst.CP);
                subject.setCheckDate(subject.getCheckCPDate());
                subjectReportModelList.add(subject);
            }
            if (subjectReportModel.getCw()) {
                SubjectReportModel subject = subjectReportModel.clone();
                subject.setFoc(FormOfControlConst.CW);
                subject.setCheckDate(subject.getCheckCWDate());
                subjectReportModelList.add(subject);
            }
            if (subjectReportModel.getPractic()) {
                SubjectReportModel subject = subjectReportModel.clone();
                subject.setFoc(FormOfControlConst.PRACTIC);
                subject.setCheckDate(subject.getCheckPracticDate());
                subjectReportModelList.add(subject);
            }
        }
        return subjectReportModelList;
    }

    @Override
    public List<SubjectReportModel> filterSubjectReportList(List<SubjectReportModel> listSubjects, String subjectName, int foc,
                                                            int attachmentStatus) {

        if (foc != 0) {
            for (int i = 0; i < listSubjects.size(); i++) {
                if (foc != listSubjects.get(i).getFoc().getValue()) {
                    listSubjects.remove(i);
                    i--;
                }
            }
        }

        if (attachmentStatus != AttachmentConst.ALL.getValue()) {
            for (int i = 0; i < listSubjects.size(); i++) {
                if (!listSubjects.get(i).getStatus().equals(AttachmentConst.getNameByValue(attachmentStatus))) {
                    listSubjects.remove(i);
                    i--;
                }
            }
        }

        if (!subjectName.isEmpty()) {
            for (int i = 0; i < listSubjects.size(); i++) {
                if (!listSubjects.get(i).getSubjectName().toLowerCase().contains(subjectName.toLowerCase())) {
                    listSubjects.remove(i);
                    i--;
                }
            }
        }

        return listSubjects;
    }

    @Override
    public boolean updateConsultDate(long idLGSS, Date consultDate) {
        return manager.updateSubjectDate(idLGSS, consultDate, "consultationdate");
    }

    @Override
    public boolean updateCheckDate(long idLGSS, Date checkDate, FormOfControlConst foc) {
        String dateField = "";

        if (foc.equals(FormOfControlConst.EXAM)) {
            dateField = "examdate";
        } else if (foc.equals(FormOfControlConst.PASS)) {
            dateField = "passdate";
        } else if (foc.equals(FormOfControlConst.CP)) {
            dateField = "tmpcourseprojectdate";
        } else if (foc.equals(FormOfControlConst.CW)) {
            dateField = "tmpcourseworkdate";
        } else if (foc.equals(FormOfControlConst.PRACTIC)) {
            dateField = "practicdate";
        }

        return manager.updateSubjectDate(idLGSS, checkDate, dateField);
    }

    @Override
    public boolean updateGroup(long idLGSS, long idLGS) {
        return manager.updateGroup(idLGSS, idLGS);
    }

    @Override
    public boolean checkIfOpenRetakeExist(long idSR, FormOfControlConst foc) {
        return manager.checkIfOpenRetakeExist(idSR, getFocQueryForLeftJoin(foc.getValue())) > 0;
    }

    @Override
    public List<SessionRatingModel> getSRbySSS(long idSSS) {
        return manager.getSRbySSS(idSSS);
    }

    private String queryForStatus(FormOfControlConst foc) {
        String queryWhatFlagToUpdate = "";

        if (foc.equals(FormOfControlConst.CP)) {
            queryWhatFlagToUpdate = " is_courseproject = 0";
        } else if (foc.equals(FormOfControlConst.CW)) {
            queryWhatFlagToUpdate = " is_coursework = 0 ";
        } else if (foc.equals(FormOfControlConst.EXAM)) {
            queryWhatFlagToUpdate = " is_exam = 0 ";
        } else if (foc.equals(FormOfControlConst.PASS)) {
            queryWhatFlagToUpdate = " is_pass = 0 ";
        } else if (foc.equals(FormOfControlConst.PRACTIC)) {
            queryWhatFlagToUpdate = " is_practic = 0 ";
        }

        return queryWhatFlagToUpdate;
    }

    public String queryForMark(FormOfControlConst foc) {
        String queryWhatMarkToUpdate = "";

        if (foc.equals(FormOfControlConst.CP)) {
            queryWhatMarkToUpdate = " courseprojectrating = 0";
        } else if (foc.equals(FormOfControlConst.CW)) {
            queryWhatMarkToUpdate = " courseworkrating = 0 ";
        } else if (foc.equals(FormOfControlConst.EXAM)) {
            queryWhatMarkToUpdate = " examrating = 0 ";
        } else if (foc.equals(FormOfControlConst.PASS)) {
            queryWhatMarkToUpdate = " passrating = 0 ";
        } else if (foc.equals(FormOfControlConst.PRACTIC)) {
            queryWhatMarkToUpdate = " practicrating = 0 ";
        }

        return queryWhatMarkToUpdate;
    }
}
