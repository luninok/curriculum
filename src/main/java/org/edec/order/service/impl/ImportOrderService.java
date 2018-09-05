package org.edec.order.service.impl;

import org.edec.order.manager.CreateOrderManagerESO;
import org.edec.order.manager.ImportOrderManager;
import org.edec.order.model.OrderModel;
import org.edec.order.model.dao.OrderImportModel;
import org.edec.utility.constants.OrderRuleConst;

import java.util.List;


public class ImportOrderService {
    private ImportOrderManager importOrderManager = new ImportOrderManager();
    private CreateOrderManagerESO createOrderManagerESO = new CreateOrderManagerESO();

    public void startTransfer (OrderModel model) {
        if (model.getIdOrderRule().equals(OrderRuleConst.ACADEMIC_IN_SESSION.getId())) {
            transferAcademicOrderToOrderActionTable(model.getIdOrder());
        }

        if (model.getIdOrderRule().equals(OrderRuleConst.ACADEMIC_NOT_IN_SESSION.getId())) {
            transferAcademicOrderToOrderActionTable(model.getIdOrder());
        }

        if (model.getIdOrderRule().equals(OrderRuleConst.SET_ELIMINATION_RESPECTFUL.getId()) ||
            model.getIdOrderRule().equals(OrderRuleConst.SET_ELIMINATION_NOT_RESPECTFUL.getId())) {
            transferSetEliminationOrderToOrderActionTable(model.getIdOrder());
        }
    }

    private void transferAcademicOrderToOrderActionTable (Long idOrder) {
        List<OrderImportModel> listStudents = importOrderManager.getOrderDataById(idOrder);

        //TODO проверка, что приказ уже импортирован и приказ импортирован некорректно(недостает человек)

        for (OrderImportModel student : listStudents) {
            importOrderManager.importStudentFromAcademOrder(student);
        }
    }

    private void transferSetEliminationOrderToOrderActionTable (Long idOrder) {
        List<OrderImportModel> listStudents = importOrderManager.getOrderDataById(idOrder);

        //TODO проверка, что приказ уже импортирован и приказ импортирован некорректно(недостает человек)

        Long idSem = createOrderManagerESO.getNextSemester(listStudents.get(0).getIdSemester());

        for (OrderImportModel student : listStudents) {
            student.setIdSemester(idSem);
            importOrderManager.importStudentFromSetEliminationOrder(student);
        }
    }
}
