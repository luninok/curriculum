package org.edec.successful.service;

import org.edec.successful.model.CourseModel;
import org.edec.successful.model.DepartmentModel;
import org.edec.successful.model.RatingModel;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.GovFinancedConst;
import org.edec.utility.constants.LevelConst;

import java.util.Date;
import java.util.List;


public interface SuccessfulService {
    //TODO: level to string (1,2,3)
    List<RatingModel> getRatingByFilter (Long idInst, Long idSemester, Long idDepart, FormOfStudy fos, GovFinancedConst govFin,
                                         String levels, String groupName, Date lastDate, String courses, Long idChair);

    List<CourseModel> fullRebuildRating (List<RatingModel> ratings);
    List<DepartmentModel> fullRebuildRatingChair (List<RatingModel> ratings);
}
