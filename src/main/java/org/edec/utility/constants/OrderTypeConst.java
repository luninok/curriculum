package org.edec.utility.constants;

/**
 * Created by dmmax
 */
public enum OrderTypeConst {
    ACADEMIC("Академическая стипендия", 1l), TRANSFER("Переводной", 2l), DEDUCTION("Отчисление", 3l), SOCIAL(
            "Соц. стипендия", 5l), SOCIAL_INCREASED("Соц. повышенная стипендия", 6L), SET_ELIMINATION_DEBTS("Установление сроков ЛАЗ", 7L);

    private Long type;
    private String name;

    public Long getType () {
        return type;
    }

    OrderTypeConst (String name, Long type) {
        this.type = type;
        this.name = name;
    }

    public static OrderTypeConst getByType (Long type) {
        for (OrderTypeConst typeConst : OrderTypeConst.values()) {
            if (typeConst.getType() - type == 0) {
                return typeConst;
            }
        }
        return null;
    }

}
