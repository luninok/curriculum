package org.edec.utility.constants;

/**
 * @author Max Dimukhametov
 */
public enum OrderStatusConst {
    CREATED("Создан", 1L), APPROVAL("На согласовании", 2L), AGREED("Утвержден", 3L), REVISION("Отправлен на доработку", 4L), CANCELED(
            "Отменен", 5L);

    private String name;
    private Long id;

    public String getName () {
        return name;
    }

    public Long getId () {
        return id;
    }

    OrderStatusConst (String name, Long id) {
        this.name = name;
        this.id = id;
    }

    public static OrderStatusConst getOrderStatusConstById (Long id) {
        for (OrderStatusConst orderStatus : OrderStatusConst.values()) {
            if (orderStatus.getId() - id == 0) {
                return orderStatus;
            }
        }
        return null;
    }

    public static OrderStatusConst getOrderStatusConstByName (String name) {
        for (OrderStatusConst orderStatus : OrderStatusConst.values()) {
            if (orderStatus.getName().equals(name)) {
                return orderStatus;
            }
        }
        return null;
    }
}
