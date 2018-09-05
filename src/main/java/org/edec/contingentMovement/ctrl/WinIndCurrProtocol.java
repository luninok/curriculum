package org.edec.contingentMovement.ctrl;

import org.edec.contingentMovement.model.ResitRatingModel;
import org.edec.contingentMovement.report.model.ProtocolCommissionModel;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;

public class WinIndCurrProtocol extends SelectorComposer<Window> {

    private static final String TEMPLATE_FIO_ST = "$ФИО студента$";
    private static final String TEMPLATE_UNIV = "$Название университета$";
    private static final String TEMPLATE_DIRECTION = "$направление$";
    private static final String TEMPLATE_RECORDBOOK_DATE = "$дата получения зачетки$";
    private static final String TEMPLATE_RECORDBOOK = "$номер зачетной книжки$";
    private static final String TEMPLATE_AGENDA =
            "Перезачет учебных дисциплин, практик и переаттестация переводящегося " + TEMPLATE_FIO_ST + " на основании зачетной книжки, " +
            "выданной " + TEMPLATE_UNIV + " по направлению подготовки " + TEMPLATE_DIRECTION + " " + "от " + TEMPLATE_RECORDBOOK_DATE +
            "г. №" + TEMPLATE_RECORDBOOK;

    public static final String ARG_SELECTED_DIRECTION = "selected_direction";
    public static final String ARG_SELECTED_FIO_STUDENT = "selected_fio_student";
    public static final String ARG_SELECTED_RECORDBOOK = "selected_recordbook";
    public static final String ARG_SELECTED_RESIST_SUBJECTS = "selected_subjects";

    @Wire
    private Datebox dbIndCurrProtocolDateCommission, dbIndCurrProtocolRecordbook;
    @Wire
    private Intbox ibIndCurrProtocolNumber;
    @Wire
    private Label lIndCurrProtocolAgenda;
    @Wire
    private Textbox tbIndCurrProtocolAgenda, tbIndCurrProtocolFioStudent, tbIndCurrChairman, tbIndCurrProtocolCommission, tbIndCurrProtocolDirection, tbIndCurrProtocolRecordbook, tbIndCurrProtocolUniversity;
    @Wire
    private Vbox vbIndCurrProtocolCommission;

    private JasperReportService jasperReportService = new JasperReportService();

    private List<ResitRatingModel> resistSubjets;
    private List<String> commissionMembers;

    @Override
    public void doAfterCompose (Window comp) throws Exception {
        super.doAfterCompose(comp);
        tbIndCurrProtocolAgenda.setValue(TEMPLATE_AGENDA);
        tbIndCurrProtocolDirection.setValue((String) Executions.getCurrent().getArg().get(ARG_SELECTED_DIRECTION));
        tbIndCurrProtocolFioStudent.setValue((String) Executions.getCurrent().getArg().get(ARG_SELECTED_FIO_STUDENT));
        tbIndCurrProtocolRecordbook.setValue((String) Executions.getCurrent().getArg().get(ARG_SELECTED_RECORDBOOK));
        commissionMembers = new ArrayList<>();
        resistSubjets = (List<ResitRatingModel>) Executions.getCurrent().getArg().get(ARG_SELECTED_RESIST_SUBJECTS);
        fillAgenda();
    }

    @Listen("onChange = #tbIndCurrProtocolAgenda; onChange = #tbIndCurrProtocolFioStudent; onChange = #tbIndCurrProtocolDirection;" +
            "onChange = #tbIndCurrProtocolRecordbook; onChange = #tbIndCurrProtocolUniversity;" +
            "onChange = #dbIndCurrProtocolRecordbook;")
    public void fillAgenda () {
        lIndCurrProtocolAgenda.setValue(getAgenda());
    }

    @Listen("onClick = #btnIndCurrProtocolPrint;")
    public void showPdf () {
        ProtocolCommissionModel protocol = new ProtocolCommissionModel();
        protocol.setAgenda(getAgenda());
        protocol.setChairman(tbIndCurrChairman.getValue());
        protocol.setDateCommission(dbIndCurrProtocolDateCommission.getValue());
        protocol.setFioStudent(tbIndCurrProtocolFioStudent.getValue());
        protocol.setResitSubjects(resistSubjets);
        protocol.setNumberProtocol(ibIndCurrProtocolNumber.getValue());
        protocol.setСommissionMembers(commissionMembers);
        jasperReportService.getIndCurrProtocol(protocol).showPdf();
    }

    @Listen("onClick = #btnIndCurrProtocolAddCommission; onOK = #tbIndCurrProtocolCommission;")
    public void addCommissionInProcol () {
        if (tbIndCurrProtocolCommission.getValue().equals("")) {
            return;
        }
        String fioCommission = tbIndCurrProtocolCommission.getValue();
        Hbox hboxComm = new Hbox();
        hboxComm.setParent(vbIndCurrProtocolCommission);

        Label lComm = new Label(fioCommission);
        Button btnDelCommission = new Button("Удалить");
        lComm.setParent(hboxComm);
        btnDelCommission.setParent(hboxComm);
        commissionMembers.add(fioCommission);
        btnDelCommission.addEventListener(Events.ON_CLICK, event -> {
            commissionMembers.remove(fioCommission);
            vbIndCurrProtocolCommission.removeChild(hboxComm);
        });
    }

    private String getAgenda () {
        String result = TEMPLATE_AGENDA;
        if (!tbIndCurrProtocolFioStudent.getValue().equals("")) {
            result = result.replace(TEMPLATE_FIO_ST, tbIndCurrProtocolFioStudent.getValue());
        }
        if (!tbIndCurrProtocolUniversity.getValue().equals("")) {
            result = result.replace(TEMPLATE_UNIV, tbIndCurrProtocolUniversity.getValue());
        }
        if (!tbIndCurrProtocolDirection.getValue().equals("")) {
            result = result.replace(TEMPLATE_DIRECTION, tbIndCurrProtocolDirection.getValue());
        }
        if (!tbIndCurrProtocolRecordbook.getValue().equals("")) {
            result = result.replace(TEMPLATE_RECORDBOOK, tbIndCurrProtocolRecordbook.getValue());
        }
        if (dbIndCurrProtocolRecordbook.getValue() != null) {
            result = result.replace(TEMPLATE_RECORDBOOK_DATE, DateConverter.convertDateToString(dbIndCurrProtocolRecordbook.getValue()));
        }
        return result;
    }
}
