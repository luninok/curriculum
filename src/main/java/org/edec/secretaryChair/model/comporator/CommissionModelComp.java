package org.edec.secretaryChair.model.comporator;

import org.edec.secretaryChair.model.CommissionModel;

import java.util.Comparator;

/**
 * Created by dmmax
 */
public class CommissionModelComp implements Comparator<CommissionModel> {
    public enum CompareMethods {
        BY_DATE, BY_DATE_REV, BY_SEMESTER, BY_SEMESTER_REV, BY_SUBJECT, BY_SUBJECT_REV
    }

    private CompareMethods compareMethods;

    public CommissionModelComp (CompareMethods compareMethods) {
        this.compareMethods = compareMethods;
    }

    @Override
    public int compare (CommissionModel o1, CommissionModel o2) {
        if (o1 != null && o2 != null) {
            switch (compareMethods) {
                case BY_DATE:
                    if (o1.getCommissionDate() != null && o2.getCommissionDate() != null) {
                        return o1.getCommissionDate().compareTo(o2.getCommissionDate());
                    } else {
                        return 0;
                    }
                case BY_DATE_REV:
                    if (o1.getCommissionDate() != null && o2.getCommissionDate() != null) {
                        return o2.getCommissionDate().compareTo(o1.getCommissionDate());
                    } else {
                        return 0;
                    }
                case BY_SEMESTER:
                    if (o1.getSemesterStr() != null && o2.getSemesterStr() != null) {
                        return o1.getSemesterStr().compareTo(o2.getSemesterStr());
                    } else {
                        return 0;
                    }
                case BY_SEMESTER_REV:
                    if (o1.getSemesterStr() != null && o2.getSemesterStr() != null) {
                        return o2.getSemesterStr().compareTo(o1.getSemesterStr());
                    } else {
                        return 0;
                    }
                case BY_SUBJECT:
                    if (o1.getSubjectName() != null && o2.getSubjectName() != null) {
                        return o1.getSemesterStr().compareTo(o2.getSemesterStr());
                    } else {
                        return 0;
                    }
                case BY_SUBJECT_REV:
                    if (o1.getSubjectName() != null && o2.getSubjectName() != null) {
                        return o2.getSemesterStr().compareTo(o1.getSemesterStr());
                    } else {
                        return 0;
                    }
                default:
                    return 0;
            }
        }
        return 0;
    }
}
