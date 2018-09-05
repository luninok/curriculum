package org.edec.utility.constants;


public enum StudentStatus {
    ACADEMIC_LEAVE(-1, "Академ. отпуск"), DEDUCTED(3, "Отчислен"), EDUCATION_COMPLETED(4, "Завершил обучение"), STUDENT(1, "Учащийся");

    public Integer getValue () {
        return value;
    }

    public String getName () {
        return name;
    }

    private Integer value;
    private String name;

    StudentStatus (Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static StudentStatus getStatusByValue (Integer value) {
        for (StudentStatus status : StudentStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
