package org.edec.contingentMovement.report.sevice.impl;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.contingentMovement.report.manager.ResitMarkManager;
import org.edec.contingentMovement.report.model.ResitMarkModel;
import org.edec.contingentMovement.report.model.ResitModel;
import org.edec.contingentMovement.report.sevice.ResitMarkService;
import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.utility.constants.RatingConst;

import java.util.Arrays;
import java.util.Collections;

public class ResitMarkImpl implements ResitMarkService {

    private ResitMarkManager manager = new ResitMarkManager();

    @Override
    public JRBeanCollectionDataSource getResitReport(StudentStatusModel student) {
        ResitModel model = new ResitModel();
        model.setFio(student.getFio());
        model.setDirectioncode("1");
        model.setGroupname(student.getGroupname());
        model.setPrevgroupname("1");
        model.setMarks(manager.getResitMark(student.getIdStudentCard(), student.getIdDG()));
        model.setShortfio(student.getShortFio());
        for (ResitMarkModel mark : model.getMarks()) {
            String rating = "";
            if (mark.getFoc().equals("Экзамен")) {
                rating = RatingConst.getNameByRating(mark.getExamRating());
            } else if (mark.getFoc().equals("Зачет")) {
                if (mark.getPassRating() != 0) {
                    rating = RatingConst.getNameByRating(mark.getPassRating());
                } else {
                    rating = RatingConst.getNameByRating(mark.getPracticRating());
                }
            } else if (mark.getFoc().equals("Курсовой проект")) {
                if (mark.getCourseProjectRating() != 0) {
                    rating = RatingConst.getNameByRating(mark.getCourseProjectRating());
                } else {
                    rating = RatingConst.getNameByRating(mark.getCourseWorkRating());
                }
            } else if (mark.getFoc().equals("Курсовая работа")) {
                if (mark.getCourseWorkRating()!= 0) {
                    rating = RatingConst.getNameByRating(mark.getCourseWorkRating());
                } else {
                    rating = RatingConst.getNameByRating(mark.getCourseProjectRating());
                }
            } else if (mark.getFoc().equals("Практика")) {
                rating = RatingConst.getNameByRating(mark.getPracticRating());
            }
            mark.setRating(RatingConst.getNameByRating(mark.getNewRating()));
            mark.setInfoFrom(mark.getInfoFrom() + rating);
            mark.setInfoTo(mark.getInfoTo() + " " + mark.getFoc());

        }
        return new JRBeanCollectionDataSource(Collections.singletonList(model));
    }
}
