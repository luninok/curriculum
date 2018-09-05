package org.edec.student.journalOrder.service;

import org.edec.student.journalOrder.manager.EntityManagerJournalOrder;
import org.edec.student.journalOrder.model.JournalOrderModel;

import java.util.List;


public class JournalOrderService {
    private EntityManagerJournalOrder emJournalOrder = new EntityManagerJournalOrder();

    public List<JournalOrderModel> getJournalByHum (Long idHum) {
        return emJournalOrder.getJournalOrder(idHum);
    }
}
