package org.edec.rest.manager;

import org.edec.dao.DAO;
import org.edec.synchroMine.model.eso.entity.Order;
import org.edec.synchroMine.model.eso.entity.OrderStatusType;
import org.edec.utility.constants.OrderStatusConst;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.type.DateType;

import java.math.BigInteger;
import java.sql.Types;
import java.util.Date;
import java.util.List;


public class OrderRestDAO extends DAO {
    public boolean updateOrderAfterLouts (Long idOrder, String ordernumber, String idLotus) {
        String query = "UPDATE order_head SET ordernumber = :ordernumber, lotus_id = :idLotus, dateofend = now() WHERE id_order_head = :idOrder";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("idOrder", idOrder).setParameter("ordernumber", ordernumber).setParameter("idLotus", idLotus);
        return executeUpdate(q);
    }

    public boolean updateOrderStatus (Long idOrder, Long idStatus, Long idHum, String fio, String certnumber, String operation) {
        String query = "UPDATE order_head SET id_order_status_type = :idStatus, current_hum = :idHum, dateoffinish = :dateOfFinish,\n" +
                       "\tcertfio = :certfio, certnumber = :certnumber, operation = :operation\n" + "WHERE id_order_head = :idOrder";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idStatus", idStatus)
         .setParameter("idHum", idHum == null ? Types.NULL : BigInteger.valueOf(idHum))
         .setLong("idOrder", idOrder)
         .setParameter("dateOfFinish", OrderStatusConst.getOrderStatusConstById(idStatus) == OrderStatusConst.AGREED ? new Date() : null,
                       DateType.INSTANCE
         )
         .setString("certfio", fio)
         .setString("certnumber", certnumber)
         .setString("operation", operation);
        return executeUpdate(q);
    }

    public boolean updateSSSdeduction (Long idOrder) {
        return updateStatus("is_deducted", idOrder);
    }

    public boolean updateSSSacademic (Long idOrder) {
        return updateStatus("is_get_academic", idOrder);
    }

    public boolean updateSSSsocial (Long idOrder) {
        return updateStatus("is_get_social", idOrder);
    }

    public boolean updateSSSsocialIncreased (Long idOrder) {
        return updateStatus("is_get_social_increased", idOrder);
    }

    private boolean updateStatus (String columnName, Long idOrder) {
        String query = "UPDATE student_semester_status SET " + columnName + " = 1 WHERE id_student_semester_status IN (\n" +
                       "\tSELECT LOSS.id_student_semester_status\n" + "\tFROM\n" + "\t\tlink_order_student_status LOSS \n" +
                       "\t\tINNER JOIN link_order_section USING (id_link_order_section)\n" + "\tWHERE id_order_head = :idOrder)";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idOrder", idOrder);
        return executeUpdate(q);
    }

    public Order getOrderById (Long id) {
        Query q = getSession().createQuery("from Order where id = :id").setParameter("id", id);
        List<?> list = getList(q);
        return list.size() == 0 ? null : (Order) list.get(0);
    }

    public OrderStatusType getOrderStatusById (Long id) {
        Query q = getSession().createQuery("from OrderStatusType where id = :id").setParameter("id", id);
        List<?> list = getList(q);
        return list.size() == 0 ? null : (OrderStatusType) list.get(0);
    }

    public void updateOrder (Order order) {
        try {
            begin();
            getSession().update(order);
            commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            rollback();
        } finally {
            close();
        }
    }

    public boolean updateSSSTransfer (Long idOrder) {
        String query = "UPDATE student_semester_status SET is_transfered = 1 WHERE id_student_semester_status IN (\n" +
                       "\tSELECT LOSS.id_student_semester_status\n" + "\tFROM\n" + "\t\tlink_order_student_status LOSS \n" +
                       "\t\tINNER JOIN link_order_section USING (id_link_order_section)\n" +
                       "\t\tINNER JOIN order_head oh USING (id_order_head)\n" +
                       "\tWHERE id_order_head = :idOrder and id_order_rule in (29, 30))";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idOrder", idOrder);
        return executeUpdate(q);
    }
}
