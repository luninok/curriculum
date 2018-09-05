package org.edec.teacher.ctrl;

import org.apache.log4j.Logger;
import org.edec.teacher.ctrl.renderer.commission.CommissionStudentRenderer;
import org.edec.teacher.model.SemesterModel;
import org.edec.teacher.model.commission.CommissionModel;
import org.edec.teacher.model.commission.EmployeeModel;
import org.edec.teacher.model.commission.StudentModel;
import org.edec.teacher.service.CompletionCommissionService;
import org.edec.teacher.service.CompletionService;
import org.edec.teacher.service.impl.CompletionCommissionImpl;
import org.edec.teacher.service.impl.CompletionServiceImpl;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;

import java.util.List;


public class WinCommissionCtrl extends SelectorComposer<Component> {
    private static final Logger log = Logger.getLogger(WinCommissionCtrl.class.getName());

    public static final String SELECTED_COMMISSION = "selected_commission";
    public static final String UPDATE_INDEX = "updateIndex";

    @Wire
    private Button btnCommonRating, btnShowProtocols, btnSign, btnShowRegister;
    @Wire
    private Combobox cmbCommonRating;
    @Wire
    private Label lSubjectName, lFormOfControl, lSemesterStr;
    @Wire
    private Listbox lbCommissionStudent;
    @Wire
    private Vbox vbCommissionStaff;

    private CompletionService completionService = new CompletionServiceImpl();
    private CompletionCommissionService commissionService = new CompletionCommissionImpl();
    private JasperReportService jasperReportService = new JasperReportService();

    private CommissionModel selectedCommission;
    private Runnable updateIndex;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        selectedCommission = (CommissionModel) Executions.getCurrent().getArg().get(SELECTED_COMMISSION);
        updateIndex = (Runnable) Executions.getCurrent().getArg().get(UPDATE_INDEX);
        init();
        fill();
    }

    public void init() {
        SemesterModel semester = selectedCommission.getSemester();
        semester.getCommissions().remove(selectedCommission);
        selectedCommission = commissionService.getCommissionByHumAndRC(selectedCommission.getIdRC());
        selectedCommission.setSemester(semester);

        List<EmployeeModel> employees = commissionService.getEmployeesByCommission(selectedCommission.getIdRC());
        List<StudentModel> students = commissionService.getStudentsByCommission(selectedCommission.getIdRC());
        selectedCommission.setStudents(students);
        selectedCommission.setEmployees(employees);
        for (EmployeeModel employee : employees) {
            if (employee.getChairman()) {
                selectedCommission.setChairman(employee.getFio());
            } else {
                selectedCommission.getCommissionStaff().add(employee);
            }
        }
    }

    public void fill() {
        lSemesterStr.setValue(selectedCommission.getSemesterStr());
        lFormOfControl.setValue(FormOfControlConst.getName(selectedCommission.getFormOfControl()).getName());
        lSubjectName.setValue(selectedCommission.getSubjectName());
        int type = 1;
        if (((FormOfControlConst.getName(selectedCommission.getFormOfControl()) == FormOfControlConst.PASS)
                    && selectedCommission.getType() == 0)
                || (FormOfControlConst.getName(selectedCommission.getFormOfControl()) == FormOfControlConst.PRACTIC)
                    && selectedCommission.getType() == 1) {
            type = 2;
        }
        List<RatingConst> ratingConsts = RatingConst.getRatingCommissionByType(type);
        cmbCommonRating.getItems().clear();
        for (RatingConst ratingConst : ratingConsts) {
            Comboitem ci = new Comboitem(ratingConst.getShortname());
            ci.setValue(ratingConst);
            ci.setParent(cmbCommonRating);
        }
        cmbCommonRating.setSelectedIndex(0);

        vbCommissionStaff.getChildren().clear();
        for (EmployeeModel employee : selectedCommission.getEmployees()) {
            new Label(employee.getFio()).setParent(vbCommissionStaff);
        }

        ListModelList<StudentModel> lmStudents = new ListModelList<>(selectedCommission.getStudents());
        lmStudents.setMultiple(true);
        lbCommissionStudent.setModel(lmStudents);
        lbCommissionStudent.setCheckmark(true);
        lbCommissionStudent.setMultiple(true);
        lbCommissionStudent.setItemRenderer(new CommissionStudentRenderer(selectedCommission.isSigned(), type));
        if (selectedCommission.isSigned()) {
            btnCommonRating.setDisabled(true);
            cmbCommonRating.setDisabled(true);
            btnShowRegister.setVisible(true);
            btnSign.setDisabled(true);
            btnSign.setVisible(false);
        } else {
            btnSign.setVisible(true);
            btnSign.setDisabled(false);
            btnCommonRating.setDisabled(false);
            cmbCommonRating.setDisabled(false);
            btnShowRegister.setVisible(false);
        }
    }

    @Listen("onClick = #btnCommonRating")
    public void setCommonRating() {
        try {
            if (lbCommissionStudent.getSelectedCount() == 0) {
                Messagebox.show("Необходимо выделить хотябы одного студента из списка.", "Внимание!", org.zkoss.zhtml.Messagebox.OK, org.zkoss.zhtml.Messagebox.EXCLAMATION);
                return;
            } else {
                for (Listitem li : lbCommissionStudent.getSelectedItems()) {
                    StudentModel rating = li.getValue();
                    RatingConst selectedRating = cmbCommonRating.getSelectedItem().getValue();
                    completionService.updateRating(selectedRating.getRating(), rating.getIdSRH());
                }
                init();
                fill();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("", e);
            Messagebox.show("Ошибка ввода общей оценки.", "Ошибка!", org.zkoss.zhtml.Messagebox.OK, org.zkoss.zhtml.Messagebox.ERROR);
        }
    }

    @Listen("onClick = #btnShowProtocols")
    public void showProtocols() {
        try {
            jasperReportService.getJasperReportProtocolCommission(selectedCommission.getIdRC()).showPdf();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("", e);
            Messagebox.show("Ошибка печати протоколов.", "Ошибка!", Messagebox.OK, Messagebox.ERROR);
        }
    }

    @Listen("onClick = #btnShowRegister")
    public void showRegister() {
        try {
            jasperReportService.getJasperReportCommRegister(null, selectedCommission).showPdf();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("", e);
            Messagebox.show("Ошибка печати ведомости.", "Ошибка!", Messagebox.OK, Messagebox.ERROR);
        }
    }

    @Listen("onClick = #btnSign")
    public void sign() {
        try {
            boolean allInputed = true;
            for (StudentModel student : selectedCommission.getStudents()) {
                if (student.getRating() == 0) {
                    allInputed = false;
                    break;
                }
            }
            if (!allInputed) {
                Messagebox.show("Необходимо ввести все оценки.", "Внимание!", Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }

            if (selectedCommission.getRegNumber() == null) {
                if (!completionService.updateRegisterNumber(selectedCommission.getIdReg(), selectedCommission.getIdSem(), "/к")) {
                    PopupUtil.showError("Не удалось присвоить номер ведомости");
                    return;
                }
            }

            init();
            jasperReportService.getJasperReportCommRegister(this, selectedCommission).showPdf();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("", e);
            Messagebox.show("Ошибка печати ведомости.", "Ошибка!", Messagebox.OK, Messagebox.ERROR);
        }
    }

    public Runnable getUpdateIndex() {
        return updateIndex;
    }
}