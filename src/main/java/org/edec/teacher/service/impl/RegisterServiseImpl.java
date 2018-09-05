package org.edec.teacher.service.impl;

import org.apache.log4j.Logger;
import org.edec.dao.DAO;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.synchroMine.model.eso.entity.Register;
import org.edec.teacher.manager.EntityManagerRegister;
import org.edec.teacher.model.register.RatingModel;
import org.edec.teacher.model.register.RegisterModel;
import org.edec.teacher.model.register.RegisterRowModel;
import org.edec.teacher.service.RegisterService;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.constants.RegisterConst;
import org.edec.utility.constants.RegisterType;
import org.hibernate.type.BigIntegerType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Messagebox;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * Created by antonskripacev on 24.02.17.
 */
public class RegisterServiseImpl implements RegisterService {
    private EntityManagerRegister manager = new EntityManagerRegister();

    @Override
    public RegisterModel getMainRegister(Long idLGSS, FormOfControlConst foc) {
        List<BigInteger> listRegId = manager.getListRegisterIdsBySubject(idLGSS, foc, RegisterType.MAIN);
        assert listRegId.size() <= 1;

        RegisterModel registerModel;

        if (listRegId.size() == 1) {
            registerModel = getRegisterByIdRegister(listRegId.get(0).longValue());
        } else {
            registerModel = getRegisterBySubjectAndFoc(idLGSS, foc, RegisterType.MAIN);
        }

        if (!registerModel.isRegisterSigned()) {
            filterStudentsInRegister(registerModel);
        } else {
            // Если преподаватель поставил оценки студентам, которые в следствии были отчислены/переведены/ушли в академ, srh на них остаются, убираем их
            registerModel.setListRegisterRow(
                    registerModel.getListRegisterRow().stream().filter(el -> el.getRetakeCount() != null && el.getRetakeCount() > 0)
                                 .collect(Collectors.toList())
            );
        }

        return registerModel;
    }

    private void filterStudentsInRegister(RegisterModel registerModel) {
        if (registerModel == null) {
            return;
        }

        registerModel.setListRegisterRow(
                registerModel.getListRegisterRow().stream().filter(e -> !e.getDeducted() && !e.getAcademicLeave())
                             .filter(e -> e.getCurrentMark() == null || !(e.getCurrentMark() >= 3 || e.getCurrentMark() == 1))
                             .filter(e -> e.getIdDicGroup().equals(e.getIdCurrentDicGroup())).collect(Collectors.toList()));
    }

    private RegisterModel getRegisterBySubjectAndFoc(Long idLGSS, FormOfControlConst foc, RegisterType type) {
        List<RatingModel> ratings = manager.getListRatingsBySubjectAndType(idLGSS, foc, type);
        return transformRatingModelsToRegisterModel(ratings, foc);
    }

    @Override
    public RegisterModel getRegisterByIdRegister(Long idRegister) {
        List<RatingModel> ratings = manager.getListRatingsByIdRegister(idRegister);
        return transformRatingModelsToRegisterModel(ratings, null);
    }

    private RegisterModel transformRatingModelsToRegisterModel(List<RatingModel> ratings, FormOfControlConst foc) {
        RegisterModel registerModel = new RegisterModel();

        if (ratings.size() != 0) {
            RatingModel ratingModel = ratings.get(0);
            registerModel.setCertNumber(registerModel.getCertNumber());
            registerModel.setCompletionDate(ratingModel.getCompletionDate());
            registerModel.setCourse(ratingModel.getCourse());
            registerModel.setFinishDate(ratingModel.getStatusFinishDate());
            registerModel.setFoc(FormOfControlConst.getName(ratingModel.getFoc()));
            registerModel.setHoursCount(ratingModel.getHoursCount());
            registerModel.setIdRegisterESO(ratingModel.getIdRegister());
            registerModel.setIdRegisterMine(ratingModel.getIdRegisterMine());
            registerModel.setIdSemester(ratingModel.getIdSemester());
            registerModel.setIsCanceled(ratingModel.getIsCanceled());
            registerModel.setRegisterNumber(ratingModel.getRegisterNumber());
            registerModel.setRegisterURL(ratingModel.getRegisterUrl());
            registerModel.setSignatoryTutor(ratingModel.getSignatoryTutor());
            registerModel.setSignDate(ratingModel.getSignDate());
            registerModel.setStartDate(ratingModel.getStatusBeginDate());
            registerModel.setSubjectName(ratingModel.getSubjectName());
            registerModel.setSynchStatus(ratingModel.getSynchStatus());
            registerModel.setThumbPrint(ratingModel.getThumbPrint());
            registerModel.setType(ratingModel.getType());
            registerModel.setGroupName(ratingModel.getGroupName());
            registerModel.setRegisterType(
                    ratingModel.getRetakeCount() != null ? RegisterType.getRegisterTypeByRetakeCount(ratingModel.getRetakeCount()) : null);

            for (RatingModel rating : ratings) {
                if (foc == null) {
                    foc = registerModel.getFoc();
                }

                RegisterRowModel registerRowModel = new RegisterRowModel();

                registerRowModel.setIdSR(rating.getIdSessionRating());
                registerRowModel.setRetakeCount(rating.getRetakeCount());
                registerRowModel.setIdSRH(rating.getIdSessionRatingHistory());
                registerRowModel.setMark(rating.getNewRating());
                registerRowModel.setStudentFullName(rating.getStudentFIO());
                registerRowModel.setTheme(foc == FormOfControlConst.CP
                                          ? rating.getCourseProjectTheme()
                                          : (foc == FormOfControlConst.CW ? rating.getCourseWorkTheme() : null));
                registerRowModel.setAcademicLeave(ratingModel.getAcademicLeaveStatus());
                registerRowModel.setChangeDateTime(ratingModel.getChangeDateTime());
                registerRowModel.setCurrentMark(rating.getCurrentRating());
                registerRowModel.setDeducted(rating.getDeductedStatus());
                registerRowModel.setIdCurrentDicGroup(rating.getIdCurrentDicGroup());
                registerRowModel.setIdDicGroup(rating.getIdDicGroup());
                registerRowModel.setIdSSS(rating.getIdStudentSemesterStatus());
                registerRowModel.setNotActual(rating.getNotActual());
                registerRowModel.setRecordbookNumber(rating.getRecordbookNumber());

                registerModel.getListRegisterRow().add(registerRowModel);
            }
        }

        return registerModel;
    }

    @Override
    public RegisterModel getMainRetakeRegister(Long idLGSS, FormOfControlConst foc) {
        List<BigInteger> listRegId = manager.getListRegisterIdsBySubject(idLGSS, foc, RegisterType.MAIN_RETAKE);
        assert listRegId.size() <= 1;

        RegisterModel registerModel;

        if (listRegId.size() == 1) {
            registerModel = getRegisterByIdRegister(listRegId.get(0).longValue());
        } else {
            registerModel = getRegisterBySubjectAndFoc(idLGSS, foc, RegisterType.MAIN_RETAKE);
        }

        if (!registerModel.isRegisterSigned()) {
            filterStudentsInRegister(registerModel);
        } else {
            // Если преподаватель поставил оценки студентам, которые в следствии были отчислены/переведены/ушли в академ, srh на них остаются, убираем их
            registerModel.setListRegisterRow(
                    registerModel.getListRegisterRow().stream().filter(el -> el.getRetakeCount() != null && el.getRetakeCount() > 0)
                                 .collect(Collectors.toList())
            );
        }

        return registerModel;
    }

    @Override
    public List<RegisterModel> getListIndividualRegisters(Long idLGSS, FormOfControlConst foc) {
        List<BigInteger> listRegId = manager.getListRegisterIdsBySubject(idLGSS, foc, RegisterType.INDIVIDUAL_RETAKE);
        List<RegisterModel> registers = new ArrayList<>();

        for (BigInteger id : listRegId) {
            registers.add(getRegisterByIdRegister(id.longValue()));
        }

        return registers;
    }

    @Override
    public void setCourseProjectTheme(String theme, long isSessionRating) {
        manager.updateCPTheme(theme, isSessionRating);
    }

    @Override
    public void setCourseWorkTheme(String theme, long isSessionRating) {
        manager.updateCWTheme(theme, isSessionRating);
    }

    @Override
    public void updateSRHDateAndRating(long idSessionRatingHistory, int rating) {
        manager.updateSRHWithDateAndRating(idSessionRatingHistory, rating);
    }

    @Override
    public long createSRH(boolean exam, boolean pass, boolean cp, boolean cw, boolean practic, int type, String status, int newRating,
                          long idSessionRating, long idSystemUser, int retakeCount) {
                return manager.createSRH(exam, pass, cp, cw, practic, type, status, newRating, idSessionRating, idSystemUser, retakeCount);
    }

    public boolean setRegisterNumber(RegisterModel registerModel, String tutor, RegisterType registerType) {
        String listSRH = registerModel.getListRegisterRow().stream().map(RegisterRowModel::getIdSRH).collect(Collectors.toList()).toString()
                                      .replace("[", "{").replace("]", "}");

        return manager.setRegisterNumber(registerModel.getIdRegisterESO(), tutor, listSRH, registerModel.getIdSemester(),
                                         registerType.getSuffix()
        );
    }

    public EntityManagerRegister getManager() {
        return manager;
    }
}
