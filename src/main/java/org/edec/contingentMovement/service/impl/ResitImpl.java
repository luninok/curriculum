package org.edec.contingentMovement.service.impl;

import org.edec.contingentMovement.manager.ResitDAO;
import org.edec.contingentMovement.model.ResitRatingModel;
import org.edec.contingentMovement.service.ResitService;
import org.edec.studentPassport.model.StudentStatusModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class ResitImpl implements ResitService {
    private ResitDAO resitDAO = new ResitDAO();

    @Override
    public List<ResitRatingModel> getListResitRatingModel (Long idStudent, Long idGroup, Boolean currentGroup) {
        List<ResitRatingModel> result = new ArrayList<>();
        for (ResitRatingModel rating : resitDAO.getListResitRating(idStudent, idGroup, currentGroup)) {
            ResitRatingModel.getRatingByDoubleFoc(rating, result);
        }
        return result;
    }

    @Override
    public void autoResit (List<ResitRatingModel> listRatingOldGroup, List<ResitRatingModel> listRatingCurrentGroup) {
        for (ResitRatingModel curRating : listRatingCurrentGroup) {
            ResitRatingModel foundOldRating = listRatingOldGroup.stream()
                                                                .filter(rating -> rating.getSubjectname()
                                                                                        .equals(curRating.getSubjectname()) &&
                                                                                  rating.getSemesternumber()
                                                                                        .equals(curRating.getSemesternumber()) &&
                                                                                  (curRating.getHoursCount() / rating.getHoursCount()) >=
                                                                                  0.7)
                                                                .findFirst()
                                                                .orElse(null);
            if (foundOldRating != null) {
                manualResit(foundOldRating, curRating);
            }
        }
    }

    @Override
    public boolean deleteAllResit (StudentStatusModel student) {
        return resitDAO.deleteAllResit(student.getIdStudentCard(), student.getIdDG());
    }

    @Override
    public boolean manualResit (ResitRatingModel oldRating, ResitRatingModel currentRating) {
        if ((oldRating.getRating() < 3 && oldRating.getRating() != 1) ||
            (currentRating.getRating() != null && (currentRating.getRating() == 1 || currentRating.getRating() >= 3)) ||
            currentRating.getResitRating() != null ||
            oldRating.getResitRating() != null) {
            return false;
        }

        if (oldRating.getFoc().equals(currentRating.getFoc()) ||
            (oldRating.getFoc().equals("Экзамен") && currentRating.getFocInt().equals(2)) ||
            (oldRating.getFoc().equals("КП") && currentRating.getFoc().equals("КР")) ||
            (oldRating.getFoc().equals("КР") && currentRating.getFoc().equals("КП")) ||
            (oldRating.getFoc().equals("Диф. зачет") && currentRating.getFoc().equals("Практика")) ||
            (oldRating.getFoc().equals("Практика") && currentRating.getFocInt().equals(2))) {

            currentRating.setRating(oldRating.getRating());
            if (currentRating.getFoc().equals("Зачет") && !oldRating.getFoc().equals("Зачет")) {
                currentRating.setRating(1);
            }
            currentRating.setResitRating(oldRating);
            oldRating.setResitRating(currentRating);
            return true;
        }
        return false;
    }

    @Override
    public boolean saveResit (List<ResitRatingModel> listOfResitRating, Long idCurrentUser) {
        boolean successfullySaveResit = true;
        for (ResitRatingModel resit : listOfResitRating) {
            if (!resitDAO.saveResitRating(resit, idCurrentUser)) {
                successfullySaveResit = false;
            }
        }
        return successfullySaveResit;
    }
}
