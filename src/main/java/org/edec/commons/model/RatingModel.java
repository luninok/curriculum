package org.edec.commons.model;

import lombok.Getter;
import lombok.Setter;
import org.edec.model.SubjectModel;
import org.edec.utility.constants.RatingConst;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class RatingModel extends SubjectModel {
    private Integer examrating, passrating, cprating, cwrating, practicrating;
    private Integer rating;
    private Long idSR;

    private String strRatingShort;

    public static List<RatingModel> getRatingByDoubleFOC (RatingModel rating) {
        List<RatingModel> result = new ArrayList<>();

        if (rating.getExam()) {
            RatingModel tmpModel = getTmpRatingByModel(rating);
            tmpModel.setFoc("Экзамен");
            tmpModel.setRating(rating.getExamrating());
            result.add(tmpModel);
        }
        if (rating.getPass()) {
            RatingModel tmpModel = getTmpRatingByModel(rating);
            tmpModel.setFoc("Зачет");
            tmpModel.setRating(rating.getPassrating());
            result.add(tmpModel);
        }
        if (rating.getCp()) {
            RatingModel tmpModel = getTmpRatingByModel(rating);
            tmpModel.setFoc("КП");
            tmpModel.setRating(rating.getCprating());
            result.add(tmpModel);
        }
        if (rating.getCw()) {
            RatingModel tmpModel = getTmpRatingByModel(rating);
            tmpModel.setFoc("КР");
            tmpModel.setRating(rating.getCwrating());
            result.add(tmpModel);
        }
        if (rating.getPractic()) {
            RatingModel tmpModel = getTmpRatingByModel(rating);
            tmpModel.setFoc("Практика");
            tmpModel.setRating(rating.getPracticrating());
            result.add(tmpModel);
        }

        return result;
    }

    private static RatingModel getTmpRatingByModel (RatingModel rating) {
        RatingModel tmpModel = new RatingModel();
        tmpModel.setIdSR(rating.getIdSR());
        tmpModel.setSubjectname(rating.getSubjectname());
        tmpModel.setSemesternumber(rating.getSemesternumber());
        tmpModel.setType(rating.getType());
        tmpModel.setHoursCount(rating.getHoursCount());

        return tmpModel;
    }

    public String getStrRatingShort () {
        return RatingConst.getNameByRating(rating);
    }
}
