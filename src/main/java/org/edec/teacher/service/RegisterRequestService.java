package org.edec.teacher.service;

import org.edec.teacher.model.registerRequest.RegisterRequestModel;
import org.edec.teacher.model.registerRequest.StudentModel;

import java.util.List;

public interface RegisterRequestService {

    List<StudentModel> getRequestAvailableStudents (long idLgss, int foc);

    List<RegisterRequestModel> getRegisterRequestHistory (long idHum, long idLgss, int foc);

    boolean sendRequest (RegisterRequestModel registerRequestModel);

    List<StudentModel> filterStudentsByRequests (List<StudentModel> students, List<RegisterRequestModel> registerHistory);

    List<RegisterRequestModel> filterRequestHistoryByStatus (List<RegisterRequestModel> registerHistory);
}
