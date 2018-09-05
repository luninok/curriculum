package org.edec.contingentMovement.service;

import org.edec.commons.model.GroupDirectionModel;
import org.edec.contingentMovement.model.ResitRatingModel;

import java.util.List;

public interface IndividualCurrService {
    List<GroupDirectionModel> getGroupDirectionBySemester (Long idSem);
    List<ResitRatingModel> getRatingByStudentAndGroup (Long idSC, Long idDG);
    List<ResitRatingModel> getSubjectByGroup (Long idDG);
    void setRatingBySubject (ResitRatingModel subject, List<ResitRatingModel> ratings);
}
