package org.edec.teacher.service;

import org.edec.teacher.model.CourseHistoryModel;
import org.edec.teacher.model.EsoCourseModel;
import org.edec.teacher.model.SemesterModel;

import java.util.List;
import java.util.Set;

/**
 * @author Max Dimukhametov
 */
public interface CompletionService {
    List<SemesterModel> getSemesterByHumanface (Long idHum, boolean unSignedRegister);

    Set<String> getInstitutesByModelSemester (List<SemesterModel> semesters);

    Set<String> getFormOfStudyByInst (String inst, List<SemesterModel> semesters);

    List<SemesterModel> getSemesterByFOSandInst (String inst, String formOfStudy, List<SemesterModel> semesters);

    List<EsoCourseModel> getEsoCourses ();

    List<EsoCourseModel> getFilteredEsoCourses (String fullname, Long idEsoCourse, List<EsoCourseModel> esoCourses);

    List<CourseHistoryModel> getAvailableCourses (long idHumanface, long idCurSem);

    boolean updateESOcourse (Long idLGSS, Long idESOcourse);

    boolean updateRating (Integer rating, Long idSRH);

    boolean updateRegisterNumber (Long idReg, Long idSem, String suffix);

    boolean updateRegisterAfterSign (Long idReg, String url, String serialNumber, String thumbPrint, String fio, String statusSR,
                                     String statusSRH);
}
