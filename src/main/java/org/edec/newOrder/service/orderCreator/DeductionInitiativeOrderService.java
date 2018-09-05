package org.edec.newOrder.service.orderCreator;

import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.addStudent.LinkOrderSectionEditModel;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.addStudent.SearchStudentModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.converter.DateConverter;

import java.util.Date;
import java.util.List;

public class DeductionInitiativeOrderService extends OrderService {
    @Override
    protected void generateParamModel () {
        if (formOfStudy != null && formOfStudy.equals(FormOfStudy.EXTRAMURAL)) {
            orderVisualGeneralParams.add(new OrderVisualParamModel("Отчислить с", ComponentEnum.DATEBOX));
        } else if (formOfStudy == null || formOfStudy.equals(FormOfStudy.FULL_TIME)) {
            orderVisualGeneralParams.add(new OrderVisualParamModel("Отчислить с", ComponentEnum.DATEBOX));
            orderVisualGeneralParams.add(new OrderVisualParamModel("Отмена выплат с", ComponentEnum.DATEBOX));
        }
    }

    @Override
    protected void generateDocumentModel () {
        isFilesNeeded = true;
    }

    @Override
    protected Long createOrderInDatabase (List<Object> orderParams, List<OrderCreateStudentModel> students) {
        Long idOrder = createEmptyOrder(getIdSemesterFromParams(orderParams), getDescFromParams(orderParams));

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());

        for (OrderCreateOrderSectionModel section : listSections) {
            Long idLOS = createEmptySection(idOrder, section.getId(), section.getFoundation(), null, null);

            for (OrderCreateStudentModel student : students) {
                createStudentInSection(idLOS, student.getId(), student.getFirstDate(), student.getSecondDate(), student.getGroupname(), "");
            }
        }

        return idOrder;
    }

    @Override
    public boolean createAndAttachOrderDocuments (List<Object> documentParams, OrderEditModel order) {
        return true;
    }

    @Override
    public void setParamForStudent (Integer i, Object value, StudentModel studentModel, Long idOS) {
        assert i <= getVisualParamsByIdSection(idOS).size();

        if (formOfStudy != null && formOfStudy.equals(FormOfStudy.EXTRAMURAL)) {
            switch (i) {
                case 1:
                    updateDateForStudent(studentModel, (Date) value, 1);
                    break;
            }
        } else if (formOfStudy == null || formOfStudy.equals(FormOfStudy.FULL_TIME)) {
            switch (i) {
                case 1:
                    updateDateForStudent(studentModel, (Date) value, 1);
                    break;
                case 2:
                    updateDateForStudent(studentModel, (Date) value, 2);
                    break;
            }
        }
    }

    @Override
    public Object getParamForStudent (Integer i, StudentModel studentModel, Long idOS) {
        assert i <= getVisualParamsByIdSection(idOS).size();

        if (formOfStudy != null && formOfStudy.equals(FormOfStudy.EXTRAMURAL)) {
            switch (i) {
                case 1:
                    return studentModel.getFirstDate();
            }
        } else if (formOfStudy == null || formOfStudy.equals(FormOfStudy.FULL_TIME)) {
            switch (i) {
                case 1:
                    return studentModel.getFirstDate();
                case 2:
                    return studentModel.getSecondDate();
            }
        }

        return null;
    }

    @Override
    public String getStringParamForStudent (Integer i, StudentModel studentModel, Long idOS) {
        assert i <= getVisualParamsByIdSection(idOS).size();

        if (formOfStudy != null && formOfStudy.equals(FormOfStudy.EXTRAMURAL)) {
            switch (i) {
                case 1:
                    return DateConverter.convertDateToString(studentModel.getFirstDate());
            }
        } else if (formOfStudy == null || formOfStudy.equals(FormOfStudy.FULL_TIME)) {
            switch (i) {
                case 1:
                    return DateConverter.convertDateToString(studentModel.getFirstDate());
                case 2:
                    return DateConverter.convertDateToString(studentModel.getSecondDate());
            }
        }

        return null;
    }
}