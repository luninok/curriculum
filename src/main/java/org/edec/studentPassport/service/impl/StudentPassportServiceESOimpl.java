package org.edec.studentPassport.service.impl;

import org.edec.studentPassport.manager.StudentPassportEsoDAO;
import org.edec.utility.component.model.RatingModel;
import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.studentPassport.model.dao.RatingEsoModel;
import org.edec.studentPassport.service.StudentPassportService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class StudentPassportServiceESOimpl implements StudentPassportService {
    private SimpleDateFormat formatYYYY = new SimpleDateFormat("YYYY");
    private StudentPassportEsoDAO studentPassportEsoDAO = new StudentPassportEsoDAO();

    @Override
    public List<StudentStatusModel> getStudentsByFilter (String fio, String recordbook, String groupname) {
        return studentPassportEsoDAO.getStudentByFilter(fio, recordbook, groupname);
    }

    @Override
    public boolean saveStudentInfo (StudentStatusModel studentStatusModel) {
        return studentPassportEsoDAO.saveStudent(studentStatusModel);
    }

    @Override
    public List<RatingModel> getRatingByHumAndDG (Long idHum, Long idDG, boolean debt) {
        List<RatingEsoModel> listEsoModel = studentPassportEsoDAO.getRatingByIdHumAndDigGroup(idHum, idDG);
        List<RatingModel> result = devideEsoModelForRatingModel(listEsoModel, debt);
        return result;
    }

    private List<RatingModel> devideEsoModelForRatingModel (List<RatingEsoModel> listEsoModel, boolean debt) {
        final List<RatingModel> result = new ArrayList<>();
        List<RatingModel> examList = new ArrayList<>();
        List<RatingModel> passList = new ArrayList<>();
        List<RatingModel> cpList = new ArrayList<>();
        List<RatingModel> cwList = new ArrayList<>();
        List<RatingModel> practicList = new ArrayList<>();

        for (RatingEsoModel esoModel : listEsoModel) {
            if (esoModel.getExam() && (!debt || (debt && esoModel.getExamrating() < 3))) {
                RatingModel ratingModel = getRatingModelByEsoModel(esoModel);
                ratingModel.setRating(esoModel.getExamrating());
                ratingModel.setFoc("Экзамен");
                examList.add(ratingModel);
            }
            if (esoModel.getPass() && (!debt || (debt && esoModel.getPassrating() != 1))) {
                RatingModel ratingModel = getRatingModelByEsoModel(esoModel);
                ratingModel.setRating(esoModel.getPassrating());
                ratingModel.setFoc("Зачет");
                passList.add(ratingModel);
            }
            if (esoModel.getCp() && (!debt || (debt && esoModel.getCprating() < 3))) {
                RatingModel ratingModel = getRatingModelByEsoModel(esoModel);
                ratingModel.setRating(esoModel.getCprating());
                ratingModel.setFoc("КП");
                cpList.add(ratingModel);
            }
            if (esoModel.getCw() && (!debt || (debt && esoModel.getCwrating() < 3))) {
                RatingModel ratingModel = getRatingModelByEsoModel(esoModel);
                ratingModel.setRating(esoModel.getCwrating());
                ratingModel.setFoc("КР");
                cwList.add(ratingModel);
            }
            if (esoModel.getPractic() && (!debt || (debt && esoModel.getPracticrating() < 3))) {
                RatingModel ratingModel = getRatingModelByEsoModel(esoModel);
                ratingModel.setRating(esoModel.getPracticrating());
                ratingModel.setFoc("Практика");
                practicList.add(ratingModel);
            }
        }

        result.addAll(examList);
        result.addAll(passList);
        result.addAll(cpList);
        result.addAll(cwList);
        result.addAll(practicList);
        Collections.sort(result, new Comparator<RatingModel>() {
            @Override
            public int compare (RatingModel o1, RatingModel o2) {
                try {
                    if (formatYYYY.parse(o1.getSemester()).compareTo(formatYYYY.parse(o2.getSemester())) == 0) {
                        return o2.getSemester().compareTo(o1.getSemester());
                    }
                    return formatYYYY.parse(o1.getSemester()).compareTo(formatYYYY.parse(o2.getSemester()));
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        return result;
    }

    private RatingModel getRatingModelByEsoModel (RatingEsoModel esoModel) {
        RatingModel ratingModel = new RatingModel();
        ratingModel.setSemester(esoModel.getSemester());
        ratingModel.setSubjectname(esoModel.getSubjectname());
        return ratingModel;
    }
}
