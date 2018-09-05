package org.edec.utility.constants;

public enum FinalGrade {
    NEZACHET(0, "Незачет"), ZACHET(1, "Зачет"), NEUDOV(2, "Неудовлетворительно"), UDOV(3, "Удовлетворительно"), HOROSHO(
            4, "Хорошо"), OTLICHNO(5, "Отлично");

    private Integer value;
    private String name;

    public Integer getValue () {
        return value;
    }

    public String getName () {
        return name;
    }

    FinalGrade (Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static FinalGrade getName (Integer value) {
        for (FinalGrade grade : FinalGrade.values()) {
            if (grade.getValue().equals(value)) {
                return grade;
            }
        }
        return null;
    }

    public static FinalGrade getValue (String name) {
        for (FinalGrade grade : FinalGrade.values()) {
            if (grade.getName().equals(name)) {
                return grade;
            }
        }
        return null;
    }
}
