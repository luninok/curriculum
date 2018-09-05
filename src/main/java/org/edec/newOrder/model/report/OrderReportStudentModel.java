package org.edec.newOrder.model.report;

/**
 * Created by dmmax
 */
public class OrderReportStudentModel {
    /**
     * ФИО
     */
    private String fio;
    /**
     * Номер зачетной книжки;
     */
    private String recordbook;

    public OrderReportStudentModel (String fio, String recordbook) {
        this.fio = fio;
        this.recordbook = recordbook;
    }

    public OrderReportStudentModel () {
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getRecordbook () {
        return recordbook;
    }

    public void setRecordbook (String recordbook) {
        this.recordbook = recordbook;
    }
}
