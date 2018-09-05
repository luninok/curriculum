package org.edec.synchroMine.service;

import org.edec.main.model.DepartmentModel;
import org.edec.model.SemesterModel;
import org.edec.synchroMine.model.dao.SubjectGroupMineModel;
import org.edec.synchroMine.model.dao.SubjectGroupModel;
import org.edec.synchroMine.model.dao.WorkloadModel;
import org.edec.synchroMine.model.eso.GroupMineModel;

import java.util.List;
import java.util.Set;

/**
 * @author Max Dimukhametov
 */
public interface GroupSubjectService {
    List<DepartmentModel> getDepartments ();

    List<GroupMineModel> getGroupsBySem (Long idSem);

    /**
     * @param idInstMine
     * @param year        - это год обучения, который имеет формат 'yyyy-yyyy', где первая 'yyyy' - дата начала учебного года, а вторая - окончание
     * @param formOfStudy - должна быть в формате "1" - если очное отделение, "1,2" - если заончное
     * @return
     */
    List<GroupMineModel> getGroupsByInstMineAndYear (Long idInstMine, String year, String formOfStudy);

    List<SemesterModel> getSemesters (Long idInst, Integer formOfStudy);

    List<SubjectGroupMineModel> getSubjectMineModel (Long idInstMine, Integer semester, Integer course, String groupname,
                                                     Set<Long> listSubject);

    List<SubjectGroupModel> getSubjectGroupESO (Long idLGS);

    List<SubjectGroupModel> getSubjectGroupMine (String groupname, Long idInstMine, Integer semester, Integer course);

    List<WorkloadModel> getWorkloadByGroup (Long idInstMine, Integer course, String groupname, Set<Long> workloads);

    List<String> getRegisterNumberByLGSandSubj (Long idLGS, Long idSubj);

    SubjectGroupModel getSubjectGroupByMineModel (SubjectGroupMineModel mineModel);

    Long searchIdEmpByFio (String family, String name, String patronymic);

    void fillGroupsSubjects (List<SubjectGroupModel> subjectsESO, List<SubjectGroupModel> subjectsMine, List<DepartmentModel> departments);

    void fillGroups (List<GroupMineModel> groupsESO, List<GroupMineModel> groupMine);

    boolean updateGroup (Long idLGS, Long idGroupMine);

    boolean updateSubject (SubjectGroupModel subjectGroupDec, SubjectGroupModel subjectGroupMine);

    boolean updateSubjectFacultative (Long idSubject, Boolean facultative);

    boolean createSubjectSRforLGS (Long idLGS, SubjectGroupModel subjectModelMine);

    void createSubjects (List<SubjectGroupModel> subjectsMine, Long idLGS);

    void createGroup (GroupMineModel data, Long idInst, SemesterModel semester);

    boolean deleteSubjectByLGSS(Long idLGSS);
}
