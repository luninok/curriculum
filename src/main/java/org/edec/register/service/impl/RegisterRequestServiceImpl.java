package org.edec.register.service.impl;

import lombok.extern.log4j.Log4j;
import org.edec.register.manager.RegisterManager;
import org.edec.register.manager.RegisterRequestManager;
import org.edec.register.model.RegisterRequestModel;
import org.edec.register.model.RetakeModel;
import org.edec.register.model.SessionRatingHistoryModel;
import org.edec.register.model.dao.RetakeModelEso;
import org.edec.register.service.RegisterRequestService;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RegisterConst;
import org.edec.utility.constants.RegisterRequestStatusConst;
import org.edec.utility.email.Sender;
import org.zkoss.zk.ui.Executions;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.edec.register.service.RegisterService.INDIVIDUAL_RETAKE;

@Log4j
public class RegisterRequestServiceImpl implements RegisterRequestService {

    private Sender sender;
    {
        try {
            sender = new Sender();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    RegisterRequestManager manager = new RegisterRequestManager();
    private RegisterManager registerManager = new RegisterManager();

    @Override
    public List<RegisterRequestModel> getRegisterRequests (long idInstitute, int fos) {
        return manager.getAllRegisterRequests(idInstitute, fos);
    }

    @Override
    public boolean acceptRequest (long idRegisterRequest, int status) {
        return manager.updateRequestStatus(idRegisterRequest, status, new Date(), "");
    }

    @Override
    public boolean denyRequest (long idRegisterRequest, String additionalInformation) {
        return manager.updateRequestStatus(idRegisterRequest, RegisterRequestStatusConst.DENIED, new Date(), additionalInformation);
    }

    @Override
    public List<RegisterRequestModel> filterRequestHistory (List<RegisterRequestModel> registerHistory, boolean isApproved,
                                                            boolean isDenied, boolean isOnlyUnderConsideration, String fioTeacher,
                                                            String fioStudent, String groupName, String subjectName) {

        List<RegisterRequestModel> filteredRegisterHistory = new ArrayList<>(registerHistory);

        for (int i = 0; i < filteredRegisterHistory.size(); i++) {

            if (!isApproved && !isDenied && !isOnlyUnderConsideration) {
            } else {
                if (!isOnlyUnderConsideration &&
                    (filteredRegisterHistory.get(i).getStatus() == RegisterRequestStatusConst.UNDER_CONSIDERATION)) {
                    filteredRegisterHistory.remove(i);
                    i--;
                    continue;
                }

                if (!isApproved && (filteredRegisterHistory.get(i).getStatus() == RegisterRequestStatusConst.APPROVED)) {
                    filteredRegisterHistory.remove(i);
                    i--;
                    continue;
                }

                if (!isDenied && (filteredRegisterHistory.get(i).getStatus() == RegisterRequestStatusConst.DENIED)) {
                    filteredRegisterHistory.remove(i);
                    i--;
                    continue;
                }
            }

            if (!fioTeacher.equals("")) {
                if (!filteredRegisterHistory.get(i).getTeacherFullName().toLowerCase().contains(fioTeacher.toLowerCase())) {
                    filteredRegisterHistory.remove(i);
                    i--;
                    continue;
                }
            }

            if (!fioStudent.equals("")) {
                if (!filteredRegisterHistory.get(i).getStudentFullName().toLowerCase().contains(fioStudent.toLowerCase())) {
                    filteredRegisterHistory.remove(i);
                    i--;
                    continue;
                }
            }

            if (!groupName.equals("")) {
                if (!filteredRegisterHistory.get(i).getGroupName().toLowerCase().contains(groupName.toLowerCase())) {
                    filteredRegisterHistory.remove(i);
                    i--;
                    continue;
                }
            }

            if (!subjectName.equals("")) {
                if (!filteredRegisterHistory.get(i).getSubjectName().toLowerCase().contains(subjectName.toLowerCase())) {
                    filteredRegisterHistory.remove(i);
                    i--;
                    continue;
                }
            }
        }

        return filteredRegisterHistory;
    }

    @Override
    public boolean openRetake (List<RegisterRequestModel> requests, Date dateOfBegin, Date dateOfEnd, String userFio) {

        List<RegisterRequestModel> acceptedRequests = new ArrayList<>();

        requestCycle:
        for (RegisterRequestModel requestModel : requests) {

            if (requestModel.getStatus() == RegisterRequestStatusConst.APPROVED) {
                continue requestCycle;
            }

            List<RetakeModel> listRetakes = separateListRetakesByIdSRH(
                    registerManager.getListRatingByListGroupSubjects(Long.toString(requestModel.getIdLgss()),
                                                                     getFokQueryForSubject(requestModel.getFoc()),
                                                                     getFocQueryForLeftJoin(requestModel.getFoc())
                    ));

            retakeCycle:
            for (RetakeModel retakeModel : listRetakes) {

                if (!retakeModel.getIdSSS().equals(requestModel.getIdSss())) {
                    continue retakeCycle;
                }

                for (SessionRatingHistoryModel historyModel : retakeModel.getListSRH()) {
                    if (historyModel.getIdSRH() != null && historyModel.getRetakeCount() == RegisterConst.TYPE_RETAKE_MAIN_NOT_SIGNED) {
                        continue retakeCycle;
                    }
                }

                if (retakeModel.getDeductedCurSem() == null) {
                    continue;
                }

                if (retakeModel.getDeductedCurSem() == true) {
                    continue;
                }

                switch (FormOfControlConst.getName(requestModel.getFoc())) {
                    case EXAM:
                        if (!isMarkNegative(retakeModel.getExamRating())) {
                            continue;
                        }
                        break;
                    case PASS:
                        if (!isMarkNegative(retakeModel.getPassRating())) {
                            continue;
                        }
                        break;
                    case CP:
                        if (!isMarkNegative(retakeModel.getCpRating())) {
                            continue;
                        }
                        break;
                    case CW:
                        if (!isMarkNegative(retakeModel.getCwRating())) {
                            continue;
                        }
                        break;
                    case PRACTIC:
                        if (!isMarkNegative(retakeModel.getPracticRating())) {
                            continue;
                        }
                        break;
                    default:
                        continue;
                }

                for (SessionRatingHistoryModel historyModel : retakeModel.getListSRH()) {
                    if (historyModel.getIdSRH() != null &&
                        historyModel.getRetakeCount().intValue() == RegisterConst.TYPE_RETAKE_INDIV_NOT_SIGNED) {
                        continue retakeCycle;
                    }
                }

                if (!retakeModel.getIdCurDicGroup().equals(requestModel.getIdGroup())) {
                    continue;
                }

                if (retakeModel.getAcademicLeaveCurSem() == true) {
                    continue;
                }

                if (!registerManager.createRetakeForModel(
                        FormOfControlConst.getName(requestModel.getFoc()), retakeModel, INDIVIDUAL_RETAKE, dateOfBegin, dateOfEnd)) {
                    log.error("Не удалось открыть индивидуальную пересдачу" + "; студенту " + retakeModel.getFio() + "; группа " +
                              requestModel.getGroupName() + "; по предмету " + requestModel.getSubjectName() + "; пользователь " + userFio);
                    return false;
                } else {
                    log.info("Пользователь " + userFio + " открыл индивидуальную пересдачу" + "; студенту " + retakeModel.getFio() +
                             "; группа " + requestModel.getGroupName() + "; по предмету " + requestModel.getSubjectName());

                    acceptRequest(requestModel.getIdRegisterRequest(), RegisterRequestStatusConst.APPROVED);

                    acceptedRequests.add(requestModel);
                }
            }
        }

        createNotificationForTeacher(acceptedRequests);

        return true;
    }

    private void createNotificationForTeacher (List<RegisterRequestModel> acceptedRequests) {

        while (acceptedRequests.size() != 0) {
            StringBuilder msg = new StringBuilder(
                    "Ваша заявка на открытие ведомостей одобрена!\nВедомость открыта у следующих студентов:\n");
            String fio = acceptedRequests.get(0).getTeacherFullName();
            String email = acceptedRequests.get(0).getEmail();
            Boolean getNotification = acceptedRequests.get(0).isGetNotification();
            int studentIndex = 1;

            for (int i = 0; i < acceptedRequests.size(); i++) {
                if (acceptedRequests.get(i).getTeacherFullName().equals(fio)) {
                    msg.append("\t")
                       .append(studentIndex)
                       .append(". ")
                       .append(acceptedRequests.get(i).getStudentFullName())
                       .append(" - \"")
                       .append(acceptedRequests.get(i).getSubjectName())
                       .append("\"\n");

                    acceptedRequests.remove(i);
                    i--;

                    studentIndex++;
                }
            }

            sendTeacherNotification(email, msg.toString(), getNotification);
        }
    }

    @Override
    public void sendTeacherNotification (String email, String msg, boolean getNotification) {
        if (sender != null && getNotification && email != null && !email.equals("") && !Executions.getCurrent().getServerName().equals("localhost")) {
            try {
                sender.sendSimpleMessage(email, "Заявка на открытие пересдачи", msg);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    private String getFokQueryForSubject (int foc) {
        switch (FormOfControlConst.getName(foc)) {
            case EXAM:
                return "is_exam = 1";
            case PASS:
                return "is_pass = 1";
            case CP:
                return "is_courseproject = 1";
            case CW:
                return "is_coursework = 1";
            case PRACTIC:
                return "is_practic = 1";
            default:
                return null;
        }
    }

    private List<RetakeModel> separateListRetakesByIdSRH (List<RetakeModelEso> listESO) {
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

    private SessionRatingHistoryModel createSessionRatingHistoryModel (RetakeModelEso retakeModelEso) {
        SessionRatingHistoryModel srhModel = new SessionRatingHistoryModel();
        srhModel.setIdSRH(retakeModelEso.getIdSRH());
        srhModel.setRetakeCount(retakeModelEso.getRetakeCount());
        return srhModel;
    }

    private RetakeModel createRetakeModel (RetakeModelEso retakeModelEso) {
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

    private boolean isMarkNegative (Integer mark) {
        if (mark == null) {
            return true;
        }

        if (mark.intValue() < 3 && mark.intValue() != 1) {
            return true;
        }

        return false;
    }

    private String getFocQueryForLeftJoin (int foc) {
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
}
