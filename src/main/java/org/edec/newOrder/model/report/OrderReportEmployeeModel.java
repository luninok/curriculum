package org.edec.newOrder.model.report;

/**
 * Created by dmmax
 */
public class OrderReportEmployeeModel {
    /**
     * ФИО согласующего лица
     */
    private String fio;
    /**
     * Должность согласующего лица
     */
    private String role;

    /**
     * Конструктор модели согласующего лица
     *
     * @param fio
     * @param role
     */
    public OrderReportEmployeeModel (String fio, String role) {
        super();
        this.fio = fio;
        this.role = role;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getRole () {
        return role;
    }

    public void setRole (String role) {
        this.role = role;
    }
}
