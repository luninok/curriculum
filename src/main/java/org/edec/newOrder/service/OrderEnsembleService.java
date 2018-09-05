package org.edec.newOrder.service;

import org.edec.newOrder.model.editOrder.OrderEditModel;

/**
 * @author Max Dimukhametov
 */
public interface OrderEnsembleService {
    boolean sendOrderToEnsemble (OrderEditModel order);
}
