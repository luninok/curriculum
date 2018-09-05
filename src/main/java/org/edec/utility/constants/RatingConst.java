package org.edec.utility.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
@AllArgsConstructor
public enum RatingConst {
    EXCELLENT("Отлично", "5", 5, 5, 1),
    GOOD("Хорошо", "4", 4, 4, 1),
    SATISFACTORILY("Удовл.", "3", 3, 3, 1),
    UNSATISFACTORILY("Неудовл.", "2", 2, 2, 1),
    PASS("Зачтено", "Зачтено", 1, 7, 2),
    NOT_PASS("Не зачтено", "Не зачтено", -2, -1, 2),
    FAILED_TO_APPEAR("Не явился", "н/я", -3, 1, 3),
    ZERO("-", "-", 0, -101, 0),
    NOT_LEARNED("Не изучал", "н/и", -1, -100, 4);

    private String name;
    private String shortname;
    private int rating;
    private int mineRating;
    private int type;

    /**
     * @param type 1 - экзамен, 2 - зачет
     * @return список возможных оценок
     */
    public static List<RatingConst> getRatingsByType(int type) {
        List<RatingConst> result = new ArrayList<>();
        for (RatingConst ratingConst : RatingConst.values()) {
            if (ratingConst.getType() == type || ratingConst.getType() == 3)
                result.add(ratingConst);
        }
        return result;
    }

    public static List<RatingConst> getRatingCommissionByType(int type) {
        List<RatingConst> result = new ArrayList<>();
        for (RatingConst ratingConst : RatingConst.values()) {
            if (ratingConst.getType() == type || ratingConst.getType() == 3) {
                result.add(ratingConst);
            }
        }
        return result;
    }

    public static RatingConst getDataByRating(int rating) {
        for (RatingConst ratingConst : RatingConst.values()) {
            if (ratingConst.getRating() - rating == 0) {
                return ratingConst;
            }
        }
        return null;
    }

    public static String getNameByRating(int rating) {
        for (RatingConst ratingConst : RatingConst.values()) {
            if (ratingConst.getRating() - rating == 0) {
                return ratingConst.getName();
            }
        }
        return "";
    }

    public static int getRatingByShortname(String shortname) {
        for (RatingConst ratingConst : RatingConst.values()) {
            if (ratingConst.getShortname().equals(shortname)) {
                return ratingConst.getRating();
            }
        }
        return -1;
    }

    public static RatingConst getByMineRating(Integer rating) {
        if(rating == null) {
            return null;
        }

        for (RatingConst ratingConst : RatingConst.values()) {
            if (ratingConst.getMineRating() == rating) {
                return ratingConst;
            }
        }
        return null;
    }
}
