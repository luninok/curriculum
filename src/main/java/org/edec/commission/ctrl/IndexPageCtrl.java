package org.edec.commission.ctrl;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.commission.ctrl.renderer.StudentCountDebtRenderer;
import org.edec.commission.ctrl.renderer.StudentDebtGroupRenderer;
import org.edec.commission.model.PeriodCommissionModel;
import org.edec.commission.model.StudentCountDebtModel;
import org.edec.commission.model.SubjectDebtModel;
import org.edec.commission.report.model.NotionModel;
import org.edec.commission.report.service.CommissionDataManager;
import org.edec.commission.service.CommissionReportService;
import org.edec.commission.service.CommissionService;
import org.edec.commission.service.impl.CommissionReportImpl;
import org.edec.commission.service.impl.CommissionServiceESOimpl;
import org.edec.model.SemesterModel;
import org.edec.passportGroup.service.PassportGroupService;
import org.edec.passportGroup.service.impl.PassportGroupServiceESO;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.*;

public class IndexPageCtrl extends CabinetSelector {

    //Создание комиссии
    @Wire
    private Checkbox chBachelor, chEngineer, chMaster, chProlongation;
    @Wire
    private Combobox cmbGovernment, cmbCourse, cmbTypeDebt;
    @Wire
    private Datebox dbBeginCommission, dbEndCommission, dbProlongation;
    @Wire
    private Listbox lbSemester, lbFoundStudents, lbStudentForGrouping;
    @Wire
    private Spinner spinDebt;
    @Wire
    private Textbox tbFioFoundStudent, tbGroupFoundStudent, tbFioStudentForGrouping, tbGroupStudentForGrouping;
    @Wire
    private Vbox vbFormOfStudy, vbInst;

    //Просмотр комиссиии
    @Wire
    private Combobox cmbPeriodCommission, cmbSem, cmbReportPdf, cmbReportDocx, cmbReportExcel, cmbInst, cmbFormOfStudy;
    @Wire
    private Listbox lbShowCommission;
    @Wire
    private Textbox tbSearchComission;

    private CommissionDataManager dataManager = new CommissionDataManager();
    private CommissionService commissionService = new CommissionServiceESOimpl();
    private CommissionReportService commissionReportService = new CommissionReportImpl();
    private ComponentService componentService = new ComponentServiceESOimpl();
    private PassportGroupService service = new PassportGroupServiceESO();
    JasperReportService jasperReportService = new JasperReportService();
    //private EntityManagerUtility emUtility = new EntityManagerUtility();

    private Integer formOfStudy;
    private Long idInst;
    public String listIdSem;
    private List<org.edec.passportGroup.model.SemesterModel> semesters;
    private List<StudentCountDebtModel> listStudentDebt = new ArrayList<StudentCountDebtModel>();
    private List<StudentCountDebtModel> listStudentGroupDebt = new ArrayList<StudentCountDebtModel>();

    @Override
    protected void fill () {
        //formOfStudy = currentModule.getFormofstudy();
        cmbReportPdf.setModel(new ListModelList<>(ReportCommission.getPdfReports()));
        cmbReportDocx.setModel(new ListModelList<>(ReportCommission.getDocxReports()));
        cmbReportExcel.setModel(new ListModelList<>(ReportCommission.getExcelReports()));

        lbFoundStudents.setItemRenderer(new StudentCountDebtRenderer(this));
        lbStudentForGrouping.setItemRenderer(new StudentDebtGroupRenderer(this));
        //this.initListboxSemester();
        componentService.fillCmbFormOfStudy(cmbFormOfStudy, vbFormOfStudy, currentModule.getFormofstudy());
        componentService.fillCmbInst(cmbInst, vbInst, currentModule.getDepartments());
        Events.echoEvent("onLater", cmbInst, null);
    }

    @Listen("onLater = #cmbInst")
    public void laterCmbInst (Event event) {
        /*if (cmbInst.getItemAtIndex(0).getLabel().equals("Все")) {
            cmbInst.removeItemAt(0);
        }*/
        cmbInst.setSelectedIndex(0);
        initListboxSemester();
    }

    @Listen("onChange = #cmbInst, #cmbFormOfStudy")
    public void changeFilter () {
        initListboxSemester();
    }

    @Listen("onOK = #tbFioFoundStudent")
    public void searchByStudentFio () {
        lbFoundStudents.setModel(new ListModelList<>(tbFioFoundStudent.getValue().equals("")
                                                     ? listStudentDebt
                                                     : commissionService.getStudentCountDebtByFio(tbFioFoundStudent.getValue(),
                                                                                                  listStudentDebt
                                                     )));
        lbFoundStudents.renderAll();
    }

    @Listen("onOK = #tbGroupFoundStudent")
    public void searchByStudentGroup () {
        lbFoundStudents.setModel(new ListModelList<>(tbGroupFoundStudent.getValue().equals("")
                                                     ? listStudentDebt
                                                     : commissionService.getStudentCountDebtByGroup(tbGroupFoundStudent.getValue(),
                                                                                                    listStudentDebt
                                                     )));
        lbFoundStudents.renderAll();
    }

    @Listen("onOK = #tbGroupFio")
    public void searchByGroupFio () {
        lbStudentForGrouping.setModel(new ListModelList<>(tbFioStudentForGrouping.getValue().equals("")
                                                          ? listStudentGroupDebt
                                                          : commissionService.getStudentCountDebtByFio(tbFioStudentForGrouping.getValue(),
                                                                                                       listStudentGroupDebt
                                                          )));
        lbStudentForGrouping.renderAll();
    }

    @Listen("onOK = #tbGroupStudentForGrouping")
    public void searchByGroup () {
        lbStudentForGrouping.setModel(new ListModelList<>(tbGroupStudentForGrouping.getValue().equals("")
                                                          ? listStudentGroupDebt
                                                          : commissionService.getStudentCountDebtByGroup(
                                                                  tbGroupStudentForGrouping.getValue(), listStudentGroupDebt)));
        lbStudentForGrouping.renderAll();
    }

    @Listen("onCheckSelectAll = #lbFoundStudents")
    public void selectAllLbStudent () {
        checkSelectAllListbox(lbFoundStudents, "checkSelectAllLbStudent");
    }

    @Listen("onCheckSelectAll = #lbStudentForGrouping")
    public void selectAllLbGroup () {
        checkSelectAllListbox(lbStudentForGrouping, "checkSelectAllLbGroup");
    }

    private void checkSelectAllListbox (Listbox listbox, String nameAttribute) {
        if (listbox.getItems().size() != 0) {
            if ((Boolean) listbox.getAttribute(nameAttribute)) {
                listbox.selectAll();
                listbox.setAttribute(nameAttribute, false);
            } else {
                listbox.clearSelection();
                listbox.setAttribute(nameAttribute, true);
            }
        }
    }

    @Listen("onClick = #btnMoveToGroup")
    public void addAllSelectStudent () {
        if (lbFoundStudents.getSelectedItems().size() == 0) {
            PopupUtil.showWarning("Необходимо отметить галочкой студентов, которых вы хотите перенести в таблицу для группировки");
            return;
        }

        if (lbFoundStudents.getSelectedItems().size() == lbFoundStudents.getItems().size()) {
            listStudentGroupDebt = new ArrayList<>(listStudentDebt);
            ListModelList lmGroup = new ListModelList<>(listStudentGroupDebt);
            lmGroup.setMultiple(true);
            lbStudentForGrouping.setModel(lmGroup);
            lbStudentForGrouping.renderAll();
            return;
        }

        for (Listitem li : lbFoundStudents.getSelectedItems()) {
            StudentCountDebtModel studentCountDebtModel = li.getValue();
            if (listStudentGroupDebt.contains(studentCountDebtModel)) {
                continue;
            }
            listStudentGroupDebt.add(studentCountDebtModel);
        }

        ListModelList lmGroup = new ListModelList<>(listStudentGroupDebt);
        lmGroup.setMultiple(true);
        lbStudentForGrouping.setModel(lmGroup);
        lbStudentForGrouping.renderAll();
    }

    @Listen("onClick = #btnRemove")
    public void removeAllSelectStudent () {
        if (lbStudentForGrouping.getSelectedItems().size() == 0) {
            PopupUtil.showWarning("Необходимо отметить галочкой студентов, которых вы хотите удалить");
            return;
        }

        if (lbStudentForGrouping.getSelectedItems().size() == lbStudentForGrouping.getItems().size()) {
            listStudentGroupDebt.clear();
            ListModelList lmGroup = new ListModelList<>(listStudentGroupDebt);
            lbStudentForGrouping.setModel(lmGroup);
            lbStudentForGrouping.renderAll();
            return;
        }

        for (Listitem li : lbStudentForGrouping.getSelectedItems()) {
            StudentCountDebtModel studentCountDebtModel = li.getValue();
            listStudentGroupDebt.remove(studentCountDebtModel);
        }

        ListModelList lmGroup = new ListModelList<>(listStudentGroupDebt);
        lmGroup.setMultiple(true);
        lbStudentForGrouping.setModel(lmGroup);
        lbStudentForGrouping.renderAll();
    }

    @Listen("onClick = #btnSearch")
    public void search () {
        Clients.showBusy(lbFoundStudents, "Загрузка данных из БД");
        Events.echoEvent("onLater", lbFoundStudents, null);
    }

    @Listen("onLater = #lbFoundStudents")
    public void laterOnLbStudent () {
        this.initListboxStudent();
        Clients.clearBusy(lbFoundStudents);
    }

    @Listen("onClick = #btnGoToCreateCommission")
    public void goToCreateCommission () {
        if (dbBeginCommission.getValue() == null || dbEndCommission.getValue() == null) {
            PopupUtil.showWarning("Введите обе даты!");
            return;
        }
        if (listStudentGroupDebt.size() == 0) {
            PopupUtil.showWarning("Выберите студентов для группировки");
            return;
        }
        StringBuilder listIdSSS = new StringBuilder();
        for (StudentCountDebtModel student : listStudentGroupDebt) {
            listIdSSS.append(student.getListSSS()).append(",");
        }

        Map<String, Object> arg = new HashMap<>();
        arg.put(WinCreateCommission.LIST_ID_SSS, listIdSSS.substring(0, listIdSSS.length() - 1));
        arg.put(WinCreateCommission.DATE_BEGIN_COMMISSION, dbBeginCommission.getValue());
        arg.put(WinCreateCommission.DATE_END_COMMISSION, dbEndCommission.getValue());

        ComponentHelper.createWindow("winCreateCommission.zul", "winCreateCommission", arg).doModal();
    }

    @Listen("onSelect = #tabShowCommission")
    public void selectedTabShowComission () {
        tbSearchComission.setValue("");
        addComboitem(cmbSem, commissionService.getAllSemesterWithCommission(formOfStudy));
        initComboboxPeriodCommission();
        Clients.showBusy(lbShowCommission, "Загрузка данных из БД");
        Events.echoEvent("onLater", lbShowCommission, null);
    }

    @Listen("onClick = #btnSearchComission; onOK = #tbSearchComission; onChange = #cmbPeriodCommission;")
    public void searchCommission () {
        Clients.showBusy(lbShowCommission, "Загрузка данных из БД");
        Events.echoEvent("onLater", lbShowCommission, null);
    }

    @Listen("onChange = #cmbSem")
    public void changeSem () {
        Clients.showBusy(lbShowCommission, "Загрузка данных из БД");
        Events.echoEvent("onLater", lbShowCommission, null);
    }

    @Listen("onLater = #lbShowCommission")
    public void laterForComission () {
        List<SubjectDebtModel> list = commissionService.getSubjectCommissionByFilterAndSem(tbSearchComission.getValue(),
                                                                                           cmbSem.getSelectedIndex() == 0
                                                                                           ? null
                                                                                           : ((SemesterModel) cmbSem.getSelectedItem()
                                                                                                                    .getValue()).getIdSem(),
                                                                                           cmbPeriodCommission.getSelectedItem().getValue(),
                                                                                           formOfStudy
        );

        lbShowCommission.setModel(new ListModelList<>(list));
        lbShowCommission.renderAll();
        Clients.clearBusy(lbShowCommission);
    }

    @Listen("onClick = #lbShowCommission")
    public void onSelectComission () {
        if (lbShowCommission.getSelectedItem() == null) {
            return;
        }
        if (Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winShowCommissionStudent") != null) {
            Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winShowCommissionStudent").detach();
        }

        Map arg = new HashMap();
        arg.put(WinShowCommissionStudentPageCtrl.SELECTED_LISTITEM, lbShowCommission.getSelectedItem());
        arg.put(WinShowCommissionStudentPageCtrl.COMMISSION_CTRL, this);

        Window win = (Window) Executions.createComponents("winShowCommissionStudent.zul", null, arg);
        win.doModal();
    }

    @Listen("onClick = #btnShowPdf")
    public void showPdf () {
        if (cmbReportPdf.getSelectedItem() == null) {
            PopupUtil.showWarning("Выберите один из отчетов");
            return;
        }
        ReportCommission report = cmbReportPdf.getSelectedItem().getValue();
        switch (report) {
            case SCHEDULE:
                getScheduleJasper().showPdf();
                break;
            case NOTION:
                showNotion(false);
                break;
            case LIST_OF_STUDENT_BY_CHAIR:
                getListOfStudentByChair().showPdf();
                break;
            case LIST_OF_STUDENT_BY_FOS:
                JasperReport jasperReport = getScheduleByFormOfStudyJasper();
                if (jasperReport != null) {
                    jasperReport.showPdf();
                }
                break;
            default:
                PopupUtil.showError("Такой вид отчетов не обрабатывается");
        }
    }

    @Listen("onClick = #btnDownloadDocx")
    public void downloadDocx () {
        if (cmbReportDocx.getSelectedItem() == null) {
            PopupUtil.showWarning("Выберите один из отчетов");
            return;
        }
        ReportCommission report = cmbReportDocx.getSelectedItem().getValue();
        switch (report) {
            case SCHEDULE:
                getScheduleJasper().downloadDocx();
                break;
            case NOTION:
                showNotion(true);
                break;
            case LIST_OF_STUDENT_BY_CHAIR:
                getListOfStudentByChair().downloadDocx();
                break;
            case LIST_OF_STUDENT_BY_FOS:
                JasperReport jasperReport = getScheduleByFormOfStudyJasper();
                if (jasperReport != null) {
                    jasperReport.downloadDocx();
                }
                break;
            default:
                PopupUtil.showError("Такой вид отчетов не обрабатывается");
        }
    }

    @Listen("onClick = #btnDownloadExcel")
    public void downloadExcel () {
        if (cmbReportExcel.getSelectedItem() == null) {
            PopupUtil.showWarning("Выберите один из отчетов");
            return;
        }
        ReportCommission report = cmbReportExcel.getSelectedItem().getValue();
        PeriodCommissionModel periodCommission = cmbPeriodCommission.getSelectedItem().getValue();
        switch (report) {
            case LIST_OF_STUDENT:
                AMedia aMedia = new AMedia("Список студентов на комиссии  (" + DateConverter.convertDateToString(new Date()) + ").xls",
                                           "xls", "application/xls", commissionReportService.getXlsxForStudentCommission(formOfStudy,
                                                                                                                         periodCommission.getDateOfBegin(),
                                                                                                                         periodCommission.getDateOfEnd()
                )
                );
                Filedownload.save(aMedia);
                break;
            default:
                PopupUtil.showError("Такой вид отчетов не обрабатывается");
        }
    }

    private void showNotion (boolean docx) {
        List<NotionModel> notions = getNotions();

        Map<String, Object> arg = new HashMap<>();
        arg.put(WinSelectStudentForNotion.STUDENTS, notions);
        arg.put(WinSelectStudentForNotion.IS_DOCX, docx);

        ComponentHelper.createWindow("winSelectStudentForNotion.zul", "winSelectStudentForNotion", arg).doModal();
    }

    private JasperReport getScheduleJasper () {
        JRBeanCollectionDataSource jrBeanCollectionDataSource = dataManager.getSchedule(
                commissionService.getSubjectCommissionByFilterAndSem(tbSearchComission.getValue(), cmbSem.getSelectedIndex() == 0
                                                                                                   ? null
                                                                                                   : ((SemesterModel) cmbSem.getSelectedItem()
                                                                                                                            .getValue()).getIdSem(),
                                                                     cmbPeriodCommission.getSelectedItem().getValue(), formOfStudy
                ));

        return jasperReportService.getScheduleJasper(jrBeanCollectionDataSource);
    }

    private JasperReport getListOfStudentByChair () {
        PeriodCommissionModel periodCommission = cmbPeriodCommission.getSelectedItem().getValue();
        JRBeanCollectionDataSource jrBeanCollectionDataSource = dataManager.getListOfStudentByChair(periodCommission.getDateOfBegin(),
                                                                                                    periodCommission.getDateOfEnd(),
                                                                                                    formOfStudy
        );
        return jasperReportService.getListOfStudentByChair(jrBeanCollectionDataSource);
    }

    private JasperReport getScheduleByFormOfStudyJasper () {
        PeriodCommissionModel periodCommission = cmbPeriodCommission.getSelectedItem().getValue();
        Date dateOfBegin = periodCommission.getDateOfBegin();
        Date dateOfEnd = periodCommission.getDateOfEnd();
        JRBeanCollectionDataSource jrBeanCollectionDataSource = dataManager.getScheduleByFormOfStudy(dateOfBegin, dateOfEnd, formOfStudy);

        return jasperReportService.getScheduleByFormOfStudyJasper(dateOfBegin, dateOfEnd, jrBeanCollectionDataSource);
    }

    private List<NotionModel> getNotions () {
        List<SubjectDebtModel> listComissions = commissionService.getSubjectCommissionByFilterAndSem(tbSearchComission.getValue(),
                                                                                                     cmbSem.getSelectedIndex() == 0
                                                                                                     ? null
                                                                                                     : ((SemesterModel) cmbSem.getSelectedItem()
                                                                                                                              .getValue()).getIdSem(),
                                                                                                     cmbPeriodCommission.getSelectedItem()
                                                                                                                        .getValue(),
                                                                                                     formOfStudy
        );

        CommissionDataManager dataManager = new CommissionDataManager();
        return dataManager.getListForNotionByListComissions(listComissions);
    }

    public void removeStudentDebt (Listitem selectedItem) {
        StudentCountDebtModel studentCountDebtModel = selectedItem.getValue();
        listStudentGroupDebt.remove(studentCountDebtModel);
        lbStudentForGrouping.getItems().remove(selectedItem);
    }

    public void addStudentRetake (Listitem selectedItem) {
        StudentCountDebtModel studentCountDebtModel = selectedItem.getValue();
        if (listStudentGroupDebt.contains(studentCountDebtModel)) {
            PopupUtil.showWarning("В таблице уже есть этот студент!");
            return;
        }
        listStudentGroupDebt.add(studentCountDebtModel);
        ListModelList lmStudent = new ListModelList<>(listStudentGroupDebt);
        lmStudent.setMultiple(true);
        lbStudentForGrouping.setModel(lmStudent);
        lbStudentForGrouping.renderItem(selectedItem);
    }

    public void initComboboxPeriodCommission () {
        List<PeriodCommissionModel> periods = commissionService.getPeriodCommission(formOfStudy);
        cmbPeriodCommission.getItems().clear();
        new Comboitem("Все").setParent(cmbPeriodCommission);
        for (PeriodCommissionModel period : periods) {
            Comboitem ci = new Comboitem(DateConverter.convertDateToString(period.getDateOfBegin()) + "-" +
                                         DateConverter.convertDateToString(period.getDateOfEnd()));
            ci.setParent(cmbPeriodCommission);
            ci.setValue(period);
            if (periods.size() == (periods.indexOf(period) + 1)) {
                cmbPeriodCommission.setSelectedItem(ci);
            }
        }
    }

    public void initListboxSemester () {
        //componentService.
        formOfStudy = ((FormOfStudy) cmbFormOfStudy.getSelectedItem().getValue()).getType();
        idInst = ((InstituteModel) cmbInst.getSelectedItem().getValue()).getIdInst();
        ListModelList lmSemester = new ListModelList<>(commissionService.getSemesterByInstAndFOS(idInst, formOfStudy));
        lmSemester.setMultiple(true);
        lbSemester.setModel(lmSemester);
        lbSemester.renderAll();
    }

    public void initListboxStudent () {
        if (chProlongation.isChecked() && dbProlongation.getValue() == null) {
            PopupUtil.showWarning("Выберите дату, до которого учитывать продление.");
            return;
        }
        if (cmbFormOfStudy.getSelectedIndex() == -1 || cmbInst.getSelectedIndex() == -1) {
            PopupUtil.showWarning("Выберите институт или форму обучения.");
            return;
        }

        initListboxStudentForGrouping();
        listIdSem = "";
        if (lbSemester.getSelectedCount() > 0) {
            for (Listitem li : lbSemester.getSelectedItems()) {
                SemesterModel sem = li.getValue();
                listIdSem += sem.getIdSem() + ",";
            }
            listIdSem = listIdSem.substring(0, listIdSem.length() - 1);
        }
        String qualification =
                (chEngineer.isChecked() ? "1," : "") + (chBachelor.isChecked() ? "2," : "") + (chMaster.isChecked() ? "3," : "");
        qualification = qualification.substring(0, qualification.length() - 1);
        listStudentDebt = commissionService.getListStudentCountDebt(qualification, cmbCourse.getSelectedIndex(), spinDebt.getValue(),
                                                                    getConditionalByString(cmbTypeDebt.getValue()),
                                                                    cmbGovernment.getSelectedIndex(),
                                                                    lbSemester.getSelectedCount() == 0 ? null : listIdSem,
                                                                    chProlongation.isChecked(), dbProlongation.getValue(), formOfStudy,
                                                                    idInst
        );
        ListModelList lmStudent = new ListModelList<>(listStudentDebt);
        lmStudent.setMultiple(true);
        lbFoundStudents.setAttribute("checkSelectAllLbStudent", true);
        lbFoundStudents.setModel(lmStudent);
        lbFoundStudents.renderAll();
    }

    public void initListboxStudentForGrouping () {
        if (listStudentGroupDebt.size() != 0) {
            listStudentGroupDebt.clear();
        }
        ListModelList lmGroup = new ListModelList<>(listStudentGroupDebt);
        lmGroup.setMultiple(true);
        lbStudentForGrouping.setAttribute("checkSelectAllLbGroup", true);
        lbStudentForGrouping.setModel(lmGroup);
        lbStudentForGrouping.renderAll();
    }

    private void addComboitem (Combobox cmb, List<SemesterModel> semesters) {
        while (cmb.getChildren().size() > 1) {
            cmb.getChildren().remove(1);
        }
        for (SemesterModel sem : semesters) {
            Comboitem ci = new Comboitem(DateConverter.convert2dateToString(sem.getDateOfBegin(), sem.getDateOfEnd()) + " " +
                                         (sem.getSeason() == 0 ? "осень" : "весна"));
            ci.setValue(sem);
            ci.setParent(cmb);
        }
    }

    private enum ReportCommission {
        SCHEDULE("Расписание", true, true, false), NOTION("Представление директора", true, true, false), LIST_OF_STUDENT(
                "Список студентов", false, false, true), LIST_OF_STUDENT_BY_FOS(
                "Список студентов на комиссии", true, true, false), LIST_OF_STUDENT_BY_CHAIR(
                "Список студентов по кафедрам", true, true, false);

        ReportCommission (String typeOfReport, boolean pdf, boolean docx, boolean excel) {
            this.typeOfReport = typeOfReport;
            this.pdf = pdf;
            this.docx = docx;
            this.excel = excel;
        }

        @Override
        public String toString () {
            return this.typeOfReport;
        }

        boolean pdf, docx, excel;
        String typeOfReport;

        public boolean isPdf () {
            return pdf;
        }

        public boolean isDocx () {
            return docx;
        }

        public boolean isExcel () {
            return excel;
        }

        public String getTypeOfReport () {
            return typeOfReport;
        }

        public static List<ReportCommission> getPdfReports () {
            return getReports(0);
        }

        public static List<ReportCommission> getDocxReports () {
            return getReports(1);
        }

        public static List<ReportCommission> getExcelReports () {
            return getReports(2);
        }

        /**
         * Получаем отчеты
         *
         * @param type 0 - pdf, 1 - docx, 3 - excel
         * @return
         */
        private static List<ReportCommission> getReports (int type) {
            List<ReportCommission> reports = new ArrayList<>();
            for (ReportCommission report : ReportCommission.values()) {
                if (((type == 0) && report.isPdf()) || ((type == 1) && report.isDocx()) || ((type == 2) && report.isExcel())) {
                    reports.add(report);
                }
            }
            return reports;
        }
    }

    private String getConditionalByString (String value) {
        if (value.equals("Больше либо равно")) {
            return ">=";
        } else if (value.equals("Меньше либо равно")) {
            return "<=";
        } else if (value.equals("Равно")) {
            return "=";
        } else if (value.equals("Не равно")) {
            return "<>";
        } else {
            return "=";
        }
    }
}
