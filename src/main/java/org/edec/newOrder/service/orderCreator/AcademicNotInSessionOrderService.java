package org.edec.newOrder.service.orderCreator;

import org.edec.newOrder.model.addStudent.LinkOrderSectionEditModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.addStudent.SearchStudentModel;
import org.hibernate.cfg.NotYetImplementedException;

import java.util.List;

public class AcademicNotInSessionOrderService extends OrderService {
    @Override
    protected void generateParamModel () {
        // TODO
    }

    @Override
    protected void generateDocumentModel () {
        // TODO
    }

    @Override
    protected Long createOrderInDatabase (List<Object> orderParams, List<OrderCreateStudentModel> students) {
        throw new NotYetImplementedException();
    }

    @Override
    public boolean createAndAttachOrderDocuments (List<Object> documentParams, OrderEditModel order) {
        // TODO

        return true;
    }

    @Override
    public void removeStudentFromOrder (Long idLoss, OrderEditModel order) {

    }

    @Override
    public void setParamForStudent (Integer i, Object value, StudentModel studentModel, Long idOS) {

    }

    @Override
    public Object getParamForStudent (Integer i, StudentModel studentModel, Long idOS) {
        return null;
    }

    @Override
    public String getStringParamForStudent (Integer i, StudentModel studentModel, Long idOS) {
        return null;
    }

    public void addStudentToOrder (SearchStudentModel studentModel, OrderEditModel order, LinkOrderSectionEditModel orderSection) {

    }
}