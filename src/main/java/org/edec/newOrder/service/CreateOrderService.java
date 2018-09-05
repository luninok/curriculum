package org.edec.newOrder.service;

import org.edec.newOrder.model.createOrder.OrderCreateRuleModel;
import org.edec.newOrder.model.createOrder.OrderCreateStudentModel;

import java.util.List;

/**
 * Created by antonskripacev on 08.01.17.
 */
public interface CreateOrderService {
    List<OrderCreateRuleModel> getOrderRulesByInstitute (long idInstitute);
    Long getCurrentSemester (long institute, int formOfControl);
    List<OrderCreateStudentModel> searchStudentsForOrderCreation (long semester, String fio);
}
