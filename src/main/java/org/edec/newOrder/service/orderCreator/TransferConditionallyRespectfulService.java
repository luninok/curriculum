package org.edec.newOrder.service.orderCreator;

import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateDocumentModel;
import org.edec.newOrder.model.createOrder.OrderCreateOrderSectionModel;
import org.edec.newOrder.model.createOrder.OrderCreateParamModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.enums.ComponentEnum;
import org.edec.newOrder.model.enums.DocumentEnum;
import org.edec.newOrder.model.enums.ParamEnum;
import org.edec.utility.converter.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TransferConditionallyRespectfulService extends OrderService {

    @Override
    protected void generateParamModel () {
        orderParams = new ArrayList<>();

        orderVisualGeneralParams = new ArrayList<>();
        orderVisualGeneralParams.add(new OrderVisualParamModel("Перевести с", ComponentEnum.DATEBOX));
        orderVisualGeneralParams.add(new OrderVisualParamModel("Сроки ликвидации по", ComponentEnum.DATEBOX));

        isFilesNeeded = true;
    }

    @Override
    protected void generateDocumentModel () {
        orderDocuments = new ArrayList<>();
    }

    @Override
    protected Long createOrderInDatabase (List<Object> orderParams, List<OrderCreateStudentModel> students) {
        Long idOrder = createEmptyOrder(getIdSemesterFromParams(orderParams), getDescFromParams(orderParams));

        List<OrderCreateOrderSectionModel> listSections = managerESO.getListOrderSection(orderRuleConst.getId());

        for (OrderCreateOrderSectionModel section : listSections) {
            Long idLOS = createEmptySection(idOrder, section.getId(), section.getFoundation(), null, null);

            for (OrderCreateStudentModel student : students) {
                if (student.getGovernmentFinanced() && section.getId().equals(96L) ||
                    !student.getGovernmentFinanced() && section.getId().equals(97L)) {
                    // TODO продумать механизм изменения парметров студентов во время формирования приказа(ручной режим)
                    createStudentInSection(idLOS, student.getId(), new Date(), new Date(), student.getGroupname(), "");
                }
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
            case 2:
                updateDateForStudent(studentModel, (Date) value, 2);
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
            case 2:
                return studentModel.getSecondDate();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public String getStringParamForStudent (Integer i, StudentModel studentModel, Long idOS) {
        switch (i) {
            case 1:
                return DateConverter.convertDateToString(studentModel.getFirstDate());
            case 2:
                return DateConverter.convertDateToString(studentModel.getSecondDate());
            default:
                throw new IllegalArgumentException();
        }
    }
}
