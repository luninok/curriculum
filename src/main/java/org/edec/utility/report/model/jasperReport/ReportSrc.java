package org.edec.utility.report.model.jasperReport;

/**
 * Created by dmmax
 */
public enum ReportSrc {
    ORDER_INDIVIDUAL_DEDUCTION("/orders/Order.jasper", 0), ORDER_ACADEMIC("/orders/Order.jasper", 1), ORDER_SOCIAL(
            "/orders/Order.jasper", 2), ORDER_SOCIAL_INCREASE("/orders/Order.jasper", 3), ORDER_TRANSFER(
            "/orders/Order2.jasper", 4), ORDER_TRANSFER_AFTER_TRANSFER("/orders/Order.jasper", 5), ORDER_SET_ELIMINATION(
            "/orders/Order.jasper", 6), ORDER_SET_ELIMINATION_RESPECTFUL("/orders/Order.jasper", 7), COMM_PROTOCOL(
            "/commission/protocol/list_protocol.jasper", 0), COMM_REGISTER("/commission/register/register.jasper", 0), COMM_SCHEDULE(
            "/commission/schedule/schedule.jasper", 0), SET_ELIMINATION_NOTE("/orders/setEliminationNotion.jasper", 0), REGISTER(
            "/register/register.jasper", 0), NOTION("/orders/notionDirector/notion.jasper", 0), NOTION_BY_BREACH_OF_CONTRACT(
            "/orders/notionDirector/notionByBreachOfContract.jasper", 0), SERVICE_NOTE("/orders/serviceNote.jasper", 0), FACT_SHEET(
            "/factSheet/factSheet.jasper", 0), CONTINGENT_INDIVIDUAL_CURR(
            "/contingentMovement/individualCurriculum/main.jasper", 0), CONTINGENT_PROTOCOL_COMMISSION(
            "/contingentMovement/protocolCommissionMeeting/main.jasper", 0), REGISTER_WITHOUT_MARKS(
            "/register/registerWithoutMarks.jasper", 0), LIST_STUDENT_BY_CHAIR(
            "/commission/listOfStudentByChair/listOfStudentByChair.jasper", 0), LIST_STUDENT_BY_FOS(
            "/commission/listOfStudentByFOS/listOfStudentByFOS.jasper", 0), COMMISSION_SCHEDULE("/commission/schedule/schedule.jasper", 0),
            REGISTER_REPORT("/register/registerReport.jasper", 0),  RESIT_MARK_REPORT("/contingentMovenment/registerMovenment.jasper", 0);

    ReportSrc (String localPath, int type) {
        this.localPath = localPath;
        this.type = type;
    }

    private String localPath;

    public String getLocalPath () {
        return localPath;
    }

    private int type;

    public int getType () {return type;}
}
