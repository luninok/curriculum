package org.edec.utility.constants;

import java.util.stream.Stream;

/**
 * @author Max Dimukhametov
 */
public enum FormOfControlConst {
    EXAM(1, "Экзамен"), PASS(2, "Зачет"), CP(3, "КП"), CW(4, "КР"), PRACTIC(5, "Практика");

    private Integer value;
    private String name;

    public Integer getValue () {
        return value;
    }

    public String getName () {
        return name;
    }

    FormOfControlConst (Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static FormOfControlConst getName (Integer value) {
        return Stream.of(FormOfControlConst.values()).filter(foc -> foc.getValue().equals(value)).findFirst().orElse(null);
    }

    public static FormOfControlConst getValue (String name) {
        return Stream.of(FormOfControlConst.values()).filter(foc -> foc.getName().equals(name)).findFirst().orElse(null);
    }
}
