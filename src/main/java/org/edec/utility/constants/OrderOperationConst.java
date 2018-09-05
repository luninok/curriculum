package org.edec.utility.constants;

import java.util.stream.Stream;

/**
 * @author Max Dimukhametov
 */
public enum OrderOperationConst {
    LOTUS_CREATE("ОО создание"), LOTUS_GET("ОО получение номера"), LOTUS_UPDATE("ОО обновление файлов"), MINE(
            "Проведение в шахтах"), ORDER_UPDATE_FILE("Обновление файла приказа"), REVISION("Доработка");
    private String name;

    public String getName () {
        return name;
    }

    OrderOperationConst (String name) {
        this.name = name;
    }

    public static OrderOperationConst getOperationByName (String name) {
        return Stream.of(OrderOperationConst.values()).filter(operation -> operation.getName().equals(name)).findFirst().orElse(null);
    }
}