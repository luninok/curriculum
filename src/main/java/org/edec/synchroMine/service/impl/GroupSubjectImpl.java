package org.edec.synchroMine.service.impl;

import org.edec.chairEmployee.manager.EntityManagerChairEmployee;
import org.edec.main.model.DepartmentModel;
import org.edec.manager.EntityManagerSemesterESO;
import org.edec.model.SemesterModel;
import org.edec.passportGroup.manager.PassportGroupManager;
import org.edec.synchroMine.manager.groupSubject.EntityManagerGroupSubjectDBO;
import org.edec.synchroMine.manager.groupSubject.EntityManagerGroupSubjectESO;
import org.edec.synchroMine.model.dao.SubjectGroupMineModel;
import org.edec.synchroMine.model.dao.SubjectGroupModel;
import org.edec.synchroMine.model.dao.WorkloadModel;
import org.edec.synchroMine.model.eso.GroupMineModel;
import org.edec.synchroMine.service.GroupSubjectService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Max Dimukhametov
 */
public class GroupSubjectImpl implements GroupSubjectService {
    private EntityManagerChairEmployee emChairEmp = new EntityManagerChairEmployee();
    private EntityManagerSemesterESO emSemesterESO = new EntityManagerSemesterESO();
    private EntityManagerGroupSubjectESO emGroupSubjectESO = new EntityManagerGroupSubjectESO();
    private EntityManagerGroupSubjectDBO emGroupSubjectDBO = new EntityManagerGroupSubjectDBO();
    private PassportGroupManager passportGroupManager = new PassportGroupManager();

    private final Pattern pattern = Pattern.compile("((\\d{2,}\\.)+(\\d{2,})?){1}(.)*");

    @Override
    public List<DepartmentModel> getDepartments () {
        return emChairEmp.getAllDepartments();
    }

    @Override
    public List<GroupMineModel> getGroupsBySem (Long idSem) {
        return emGroupSubjectESO.getGroupBySem(idSem);
    }

    @Override
    public List<GroupMineModel> getGroupsByInstMineAndYear (Long idInstMine, String year, String formOfStudy) {
        return emGroupSubjectDBO.getGroupByInstAndYear(idInstMine, year, formOfStudy);
    }

    @Override
    public List<SemesterModel> getSemesters (Long idInst, Integer formOfStudy) {
        return emSemesterESO.getSemesters(idInst, formOfStudy, null);
    }

    @Override
    public List<SubjectGroupMineModel> getSubjectMineModel (Long idInstMine, Integer semester, Integer course, String groupname,
                                                            Set<Long> listSubject) {
        return emGroupSubjectDBO.getSubjectFromCurriculum(idInstMine, course, semester, groupname, listSubject);
    }

    @Override
    public List<SubjectGroupModel> getSubjectGroupESO (Long idLGS) {
        return emGroupSubjectESO.getSubjectsByLGS(idLGS);
    }

    @Override
    public List<SubjectGroupModel> getSubjectGroupMine (String groupname, Long idInstMine, Integer semester, Integer course) {
        List<SubjectGroupModel> result = new ArrayList<>();
        for (SubjectGroupMineModel model : emGroupSubjectDBO.getSubjectGroup(groupname, idInstMine, semester, course)) {

            SubjectGroupModel subject = result.stream()
                                              .filter(subjectGroupModel -> subjectGroupModel.getIdSubjMine().equals(model.getIdSubjMine()))
                                              .findFirst()
                                              .orElse(null);

            if (subject == null) {
                subject = getSubjectGroupByMineModel(model);
                result.add(subject);
            }

            Long idEmp = searchIdEmpByFio(model.getFamily(), model.getName(), model.getPatronymic());

            if (idEmp != null) {
                subject.getEmployees().add(idEmp);
            }

            subject.getWorkLoads().add(model.getIdWorkload());
        }
        return result;
    }

    @Override
    public List<WorkloadModel> getWorkloadByGroup (Long idInstMine, Integer course, String groupname, Set<Long> worklaods) {
        return emGroupSubjectDBO.getWorkloadsByGroup(idInstMine, course, groupname, worklaods);
    }

    @Override
    public List<String> getRegisterNumberByLGSandSubj (Long idLGS, Long idSubj) {
        return emGroupSubjectESO.getRegisterNumberByLGSandSubj(idLGS, idSubj);
    }

    @Override
    public SubjectGroupModel getSubjectGroupByMineModel (SubjectGroupMineModel model) {
        SubjectGroupModel subject = new SubjectGroupModel();
        double hoursExam = model.getHoursExam();
        double hoursIndependent = model.getHoursIndependent();
        double hoursLabaratory = model.getHoursLabaratory();
        double hoursLecture = model.getHoursLecture();
        double hoursPractice = model.getHoursPractice();
        double hoursCount = hoursExam + hoursIndependent + hoursLabaratory + hoursLecture + hoursPractice;
        subject.setHoursExam(hoursExam);
        subject.setHoursIndependent(hoursIndependent);
        subject.setHoursLabaratory(hoursLabaratory);
        subject.setHoursLecture(hoursLecture);
        subject.setHoursPractice(hoursPractice);
        subject.setHoursCount(hoursCount);
        subject.setIdChairMine(model.getIdChairMine());
        subject.setIdSubjMine(model.getIdSubjMine());
        subject.setExam(model.getExam());
        subject.setPass(model.getPass());
        subject.setCp(model.getCp());
        subject.setCw(model.getCw());
        subject.setPractic(model.getPractic());
        subject.setFacultative(model.getFacultative());
        subject.setPracticType(model.getPracticType());
        subject.setType(model.getType());
        subject.setSubjectname(model.getSubjectname());
        return subject;
    }

    @Override
    public Long searchIdEmpByFio (String family, String name, String patronymic) {
        return emGroupSubjectESO.getEmpByFIO(family, name, patronymic);
    }

    @Override
    public void fillGroupsSubjects (List<SubjectGroupModel> subjectsESO, List<SubjectGroupModel> subjectsMine,
                                    List<DepartmentModel> departments) {
        for (SubjectGroupModel subjectMine : subjectsMine) {
            //Ищем подразделение по идентификатору шахт
            DepartmentModel foundDepartment = departments.stream()
                                                         .filter(departmentModel -> departmentModel.getIdDepartmentMine() != null &&
                                                                                    departmentModel.getIdDepartmentMine()
                                                                                                   .equals(subjectMine.getIdChairMine()))
                                                         .findFirst()
                                                         .orElse(null);
            //Если нашли, то записываем  idChair в модель
            if (foundDepartment != null) {
                subjectMine.setIdChair(foundDepartment.getIdChair());
            }
            //Ищем предмет, у которого еще нет привязки к другому subject и у которых совпадают названия дисциплин
            SubjectGroupModel subjectGroupESO = subjectsESO.stream()
                                                           .filter(subjectESO -> subjectESO.getOtherSubject() == null &&
                                                                                 (subjectESO.getSubjectname()
                                                                                            .equals(subjectMine.getSubjectname()) ||
                                                                                  Objects.deepEquals(subjectESO.getIdSubjMine(),
                                                                                                     subjectMine.getIdSubjMine()
                                                                                  )))
                                                           .findFirst()
                                                           .orElse(null);
            if (subjectGroupESO != null) {
                subjectGroupESO.setOtherSubject(subjectMine);
                subjectMine.setOtherSubject(subjectGroupESO);
            }
        }
    }

    @Override
    public void fillGroups (List<GroupMineModel> groupsESO, List<GroupMineModel> groupsMine) {
        for (GroupMineModel groupESO : groupsESO) {
            for (GroupMineModel groupMine : groupsMine) {
                if (groupMine.getOtherGroup() != null) {
                    continue;
                }
                if (groupESO.getGroupname().equals(groupMine.getGroupname())) {
                    groupESO.setOtherGroup(groupMine);
                    groupMine.setOtherGroup(groupESO);
                    break;
                }
            }
        }
    }

    @Override
    public boolean updateGroup (Long idLGS, Long idGroupMine) {
        return emGroupSubjectESO.updateGroup(idLGS, idGroupMine);
    }

    @Override
    public boolean updateSubject (SubjectGroupModel subjectGroupDec, SubjectGroupModel subjectGroupMine) {
        Long idDicSubj = subjectGroupDec.getSubjectname().equals(subjectGroupMine.getSubjectname()) ? subjectGroupDec.getIdDicSubj() : null;
        //Если ИД словаря предметов отсутствует, то нужно по @subjectName получить ИД или создать новый и получить у новог
        if (idDicSubj == null) {
            idDicSubj = emGroupSubjectESO.getDicSubjetBySubjectname(subjectGroupMine.getSubjectname());
            subjectGroupDec.setIdDicSubj(idDicSubj);
        }
        return emGroupSubjectESO.updateSubject(subjectGroupDec, subjectGroupMine);
    }

    @Override
    public boolean updateSubjectFacultative (Long idSubject, Boolean facultative) {
        return emGroupSubjectESO.updateSubjectFacultative(idSubject, facultative);
    }

    @Override
    public boolean createSubjectSRforLGS (Long idLGS, SubjectGroupModel subjectModelMine) {
        return emGroupSubjectESO.createSubject(idLGS, subjectModelMine);
    }

    @Override
    public void createSubjects (List<SubjectGroupModel> subjectsMine, Long idLGS) {
        for (SubjectGroupModel subject : subjectsMine) {
            if (subject.getOtherSubject() != null) {
                continue;
            }
            createSubjectSRforLGS(idLGS, subject);
        }
    }

    @Override
    public void createGroup (GroupMineModel data, Long idInst, SemesterModel semester) {
        Long idCreatedSchoolYear = emGroupSubjectESO.getIdSchoolYearByBeginDate(data.getCreatedSchoolYear());
        Long idEnterSchoolYear = emGroupSubjectESO.getIdSchoolYearByBeginDate(data.getEnterSchoolYear());
        if (data.getDirectionTitle() != null) {
            Matcher m = pattern.matcher(data.getDirectionTitle());
            if (m.find()) {
                data.setDirectionCode(data.getDirectionTitle().substring(0, m.end(1)));
                data.setDirectionTitle(data.getDirectionTitle().substring(m.end(1)).trim());
            }
        }
        Long idChair = emGroupSubjectESO.getIdChairByNameAndODI(data.getChairName(), data.getIdChairMine());
        data.setIdChair(idChair);
        Long idDirection = emGroupSubjectESO.getIdDirectionByTitleAndCode(data.getSpecialityTitle(), data.getDirectionCode());
        data.setIdDirection(idDirection);

        Long idCurriculum = emGroupSubjectESO.createOrGetCurriculum(data, idCreatedSchoolYear, idEnterSchoolYear);
        Long idDicGroup = emGroupSubjectESO.createOrGetDicGroup(idCurriculum, idInst, data.getMilitary(), data.getGroupname());
        Integer semesterNumber = (data.getCourse() - 1) * 2 + (semester.getSeason() == 0 ? 1 : 2);
        Long idLGS = emGroupSubjectESO.createOrGetLGS(
                data.getCourse(), semesterNumber, idDicGroup, semester.getIdSem(), data.getIdGroupMine());
    }

    @Override
    public boolean deleteSubjectByLGSS (Long idLGSS) {
        return passportGroupManager.deleteLESG(idLGSS) && passportGroupManager.deleteLGSS(idLGSS)
               && passportGroupManager.deleteSubject(idLGSS) && passportGroupManager.deleteSR(idLGSS);
    }
}