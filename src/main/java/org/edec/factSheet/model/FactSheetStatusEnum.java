package org.edec.factSheet.model;

import java.util.ArrayList;
import java.util.List;

public enum FactSheetStatusEnum {
    CREATED("Создан", 0), APPROVED("Одобрен", 1), CANCELED("Отменен", 2);
    private String name;
    private int status;

    @Override
    public String toString () {
        return this.name;
    }

    FactSheetStatusEnum (String name, int status) {
        this.name = name;
        this.status = status;
    }

    public String getName () {
        return name;
    }

    public int getStatus () {
        return status;
    }

    public static FactSheetStatusEnum getStatusByValue (int status) {
        for (FactSheetStatusEnum statusEnum : FactSheetStatusEnum.values()) {
            if (statusEnum.getStatus() == status) {
                return statusEnum;
            }
        }
        return null;
    }

    public static FactSheetStatusEnum getStatusByName (String name) {
        for (FactSheetStatusEnum statusEnum : FactSheetStatusEnum.values()) {
            if (statusEnum.toString().equals(name)) {
                return statusEnum;
            }
        }
        return null;
    }

    public static List<FactSheetStatusEnum> getStatusForSearch (String name) {
        List<FactSheetStatusEnum> statusEnumList = new ArrayList<>();
        for (FactSheetStatusEnum statusEnum : FactSheetStatusEnum.values()) {
            if (statusEnum.toString().toLowerCase().contains(name.toLowerCase())) {
                statusEnumList.add(statusEnum);
            }
        }
        return statusEnumList;
    }

}
