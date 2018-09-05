package org.edec.newOrder.service.orderCreator;

import org.edec.newOrder.model.*;
import org.edec.newOrder.model.addStudent.LinkOrderSectionEditModel;
import org.edec.newOrder.model.createOrder.OrderCreateDocumentModel;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.newOrder.model.enums.DocumentEnum;
import org.edec.newOrder.model.enums.ParamEnum;
import org.edec.newOrder.model.addStudent.SearchStudentModel;
import org.edec.utility.converter.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CancelAcademicalScholarshipInSessionOrderService extends OrderService {

    @Override
    protected void generateParamModel () {
        orderParams = new ArrayList<>();
        orderParams.add(new OrderCreateParamModel("Дата учета оценок с", ComponentEnum.DATEBOX, true));
        orderParams.add(new OrderCreateParamModel("Дата учета оценок по", ComponentEnum.DATEBOX, true));

        orderVisualGeneralParams = new ArrayList<>();
        orderVisualGeneralParams.add(new OrderVisualParamModel("Отменить с", ComponentEnum.DATEBOX));

        isFilesNeeded = false;
    }

    @Override
    protected void generateDocumentModel () {

    }

    @Override
    protected Long createOrderInDatabase (List<Object> orderParams, List<OrderCreateStudentModel> students) {
        Long idOrder = createEmptyOrder(getIdSemesterFromParams(orderParams), getDescFromParams(orderParams));

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());

        for (OrderCreateOrderSectionModel section : listSections) {
            Long idLOS = createEmptySection(idOrder, section.getId(), section.getFoundation(), null, null);

            List<OrderCreateStudentModel> listStudents = studentsOrderManager.getStudentForCancelAcademicalScholarshipInSession(
                    getIdSemesterFromParams(orderParams), managerESO.getPrevSemester(getIdSemesterFromParams(orderParams)),
                    (Date) orderParams.get(0), (Date) orderParams.get(1)
            );

            for (OrderCreateStudentModel student : listStudents) {
                // TODO исправление хардкора "2", заведение констант DicAction
                // Если студенту уже отменена стипендия:
                if (student.getIdLastDicAction() == null || student.getIdLastDicAction().equals(2)) {
                    continue;
                }

                // Дата отмены - первое число следующего месяца от первого долга(тройки) в этом семестре
                Date dateCancel = DateConverter.getFirstDayOfNextMonth((Date) orderParams.get(0));

                if (student.getSecondDate() == null || student.getSecondDate().before(dateCancel)) {
                    continue;
                }

                createStudentInSection(idLOS, student.getId(), dateCancel, null, student.getGroupname(), "");
            }
        }

        return idOrder;
    }

    @Override
    public boolean createAndAttachOrderDocuments (List<Object> documentParams, OrderEditModel order) {
        return false;
    }

    @Override
    public void setParamForStudent (Integer i, Object value, StudentModel studentModel, Long idOS) {
        switch (i) {
            case 1:
                updateDateForStudent(studentModel, (Date) value, 1);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public Object getParamForStudent (Integer i, StudentModel studentModel, Long idOS) {
        switch (i) {
            case 1:
                return studentModel.getFirstDate();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public String getStringParamForStudent (Integer i, StudentModel studentModel, Long idOS) {
        switch (i) {
            case 1:
                return DateConverter.convertDateToString(studentModel.getFirstDate());
            default:
                throw new IllegalArgumentException();
        }
    }
}
