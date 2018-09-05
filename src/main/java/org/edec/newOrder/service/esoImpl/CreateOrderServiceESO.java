package org.edec.newOrder.service.esoImpl;

import org.edec.newOrder.manager.CreateOrderManagerESO;
import org.edec.newOrder.model.createOrder.OrderCreateRuleModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;
import org.edec.newOrder.service.CreateOrderService;

import java.util.List;

/**
 * Created by antonskripacev on 08.01.17.
 */
public class CreateOrderServiceESO implements CreateOrderService {

    private CreateOrderManagerESO managerCreate = new CreateOrderManagerESO();

    public List<OrderCreateRuleModel> getOrderRulesByInstitute (long idInstitute) {
        return managerCreate.getListOrderRule(idInstitute);
    }

    @Override
    public Long getCurrentSemester (long institute, int formOfControl) {
        return managerCreate.getCurrentSemester(institute, formOfControl);
    }

    @Override
    public List<OrderCreateStudentModel> searchStudentsForOrderCreation (long semester, String fio) {
        return managerCreate.getStudentsForSearch(semester, fio);
    }
}
