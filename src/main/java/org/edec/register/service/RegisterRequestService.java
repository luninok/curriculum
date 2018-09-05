package org.edec.register.service;

import org.edec.register.model.RegisterRequestModel;
import org.edec.register.model.SubjectModel;

import java.util.Date;
import java.util.List;

public interface RegisterRequestService {

    List<RegisterRequestModel> getRegisterRequests (long idInstitute, int fos);

    boolean acceptRequest (long idRegisterRequest, int status);

    boolean denyRequest (long idRegisterRequest, String additionalInformation);

    List<RegisterRequestModel> filterRequestHistory (List<RegisterRequestModel> registerHistory, boolean isApproved, boolean isDenied,
                                                     boolean isOnlyUnderConsideration, String fioTeacher, String fioStudent,
                                                     String groupName, String subjectName);

    boolean openRetake (List<RegisterRequestModel> requests, Date dateOfBegin, Date dateOfEnd, String userFio);

    void sendTeacherNotification (String email, String msg, boolean getNotification);
}
