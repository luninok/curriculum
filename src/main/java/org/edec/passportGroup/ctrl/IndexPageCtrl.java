package org.edec.passportGroup.ctrl;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.ModuleModel;
import org.edec.passportGroup.ctrl.renderer.GroupListRenderer;
import org.edec.passportGroup.ctrl.renderer.SemesterListRenderer;
import org.edec.passportGroup.model.GroupModel;
import org.edec.passportGroup.model.SemesterModel;
import org.edec.passportGroup.service.PassportGroupService;
import org.edec.passportGroup.service.impl.PassportGroupServiceESO;
import org.edec.passportGroup.service.impl.ReportsServiceESO;
import org.edec.report.passportGroup.service.PassportGroupReportService;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.DialogUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.zkoss.zk.ui.Executions.getCurrent;


public class IndexPageCtrl extends SelectorComposer<Component> {
    @Wire
    private Combobox typeOfSemesterFilter, formOfStudyFilter, courseFilter, cmbInst, cmbReport;

    @Wire
    private Groupbox gbSemester, gbInstitute, gbFormOfStudy;

    @Wire
    private Checkbox engineer, bachelor, master;

    @Wire
    private Textbox groupFilter, tbSearch;

    @Wire
    private Button btnCheckScholarship, btnEditSubject, btnShowGroup, btnShowStudents;

    @Wire
    private Listbox semesterList, groupList;

    private List<SemesterModel> semesters;
    private List<GroupModel> groups;
    private PassportGroupService service = new PassportGroupServiceESO();
    private ComponentService componentService = new ComponentServiceESOimpl();

    private TemplatePageCtrl template = new TemplatePageCtrl();
    private ModuleModel currentModule;

    private ReportsServiceESO reports = new ReportsServiceESO();

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);

        template.checkModuleByRole(getCurrent().getDesktop().getRequestPath(), getPage());
        currentModule = template.getCurrentModule();
        semesterList.setItemRenderer(new SemesterListRenderer());
        groupList.setItemRenderer(new GroupListRenderer());
        if (currentModule != null) {
            fill();
        }
    }

    private void fill () {
        if (currentModule.isReadonly()) {
            btnCheckScholarship.setVisible(false);
            btnCheckScholarship.setDisabled(true);
            btnEditSubject.setVisible(false);
            btnEditSubject.setDisabled(true);
        }

        typeOfSemesterFilter.setSelectedIndex(0);
        cmbReport.setModel(new ListModelList<>(ReportPasportGroup.values()));
        cmbReport.setItemRenderer(((comboitem, o, i) -> {
            comboitem.setValue(o);
            comboitem.setLabel(((ReportPasportGroup) o).name);
        }));
        engineer.setChecked(true);
        bachelor.setChecked(true);
        master.setChecked(true);

        componentService.fillCmbFormOfStudy(formOfStudyFilter, gbFormOfStudy, currentModule.getFormofstudy());
        componentService.fillCmbInst(cmbInst, gbInstitute, currentModule.getDepartments());
        Events.echoEvent("onLater", cmbInst, null);
    }

    private void fillGroups () {
        if (semesterList.getSelectedIndex() != -1) {

            SemesterModel model = semesterList.getSelectedItem().getValue();

            groups = service.getGroupsByFilter(model.getIdSemester(), courseFilter.getSelectedIndex(), groupFilter.getValue(),
                                               bachelor.isChecked(), master.isChecked(), engineer.isChecked()
            );

            ListModelList<GroupModel> listGroups = new ListModelList<>(groups);
            listGroups.setMultiple(true);
            groupList.setCheckmark(true);
            groupList.setModel(listGroups);
            groupList.renderAll();
            if (courseFilter.getItemCount() == 0) {
                if (groupList.getModel().getSize() > 0) {
                    int countOfCourse = ((GroupModel) groupList.getModel().getElementAt(groupList.getModel().getSize() - 1)).getCourse();
                    courseFilter.appendItem("Все");
                    for (int i = 1; i <= countOfCourse; ++i) {
                        courseFilter.appendItem(String.valueOf(i));
                    }
                }
            }
        }
    }

    @Listen("onSelect = #groupList")
    public void toggleBtnFromTable (Event event) {
        if (groupList.getSelectedCount() == 0) {
            btnShowGroup.setDisabled(true);
            btnEditSubject.setDisabled(true);
        } else if (groupList.getSelectedCount() == 1) {
            btnShowGroup.setDisabled(false);
            btnEditSubject.setDisabled(false);
        } else if (groupList.getSelectedCount() >= 1) {
            btnShowGroup.setDisabled(true);
            btnEditSubject.setDisabled(false);
        }
    }

    @Listen("onLater = #cmbInst")
    public void laterCmbInst (Event event) {
        if (cmbInst.getItemAtIndex(0).getLabel().equals("Все")) {
            cmbInst.removeItemAt(0);
        }
        cmbInst.setSelectedIndex(0);

        changeFilter();
    }

    @Listen("onChange = #cmbInst, #typeOfSemesterFilter, #formOfStudyFilter")
    public void changeFilter () {
        semesters = service.getSemestersByParams(((InstituteModel) cmbInst.getSelectedItem().getValue()).getIdInst(),
                                                 typeOfSemesterFilter.getSelectedIndex(),
                                                 ((FormOfStudy) formOfStudyFilter.getSelectedItem().getValue()).getType()
        );

        semesterList.setModel(new ListModelList<>(semesters));
        semesterList.renderAll();

        semesterList.addEventListener(Events.ON_CLICK, event -> fillGroups());
    }

    @Listen("onChange = #courseFilter; onOK = #groupFilter; onClick = #engineer, #bachelor, #master")
    public void changeFilterGroup () {
        fillGroups();
    }

    // Кнопка "Отчет по группе"
    @Listen("onClick = #btnShowGroup;")
    public void showGroupReport () {
        if (groupList.getSelectedCount() == 1) {
            Map<String, Object> arg = new HashMap<>();
            arg.put("group", groupList.getSelectedItem().getValue());
            ComponentHelper.createWindow("/passportGroup/winGroupReport.zul", "winGroupReport", arg).doModal();
        } else if (groupList.getSelectedCount() == 0) {
            PopupUtil.showWarning("Ни одной группы не выбрано");
        }
    }

    // Кнопка "Редактор предметов"
    @Listen("onClick = #btnEditSubject;")
    public void ShowSubject () {
        if (groupList.getSelectedIndex() == -1) {
            PopupUtil.showWarning("Выберите хотя бы один предмет");
        } else {
            try {
                Map<String, Object> arg = new HashMap<>();

                arg.put("groupList", groupList.getSelectedItems().stream().map(Listitem::getValue).collect(Collectors.toList()));

                ComponentHelper.createWindow("/passportGroup/winSubjectsGroups.zul", "winSubjectsGroups", arg).doModal();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Listen("onClick = #btnCheckScholarship;")
    public void ShowWindowCheckScholarship () {
        ComponentHelper.createWindow("/passportGroup/winCheckScholarship.zul", "winCheckScholarship", null).doModal();
    }

    @Listen("onClick = #btnShowReport;")
    public void showReport () {
        if (cmbReport.getSelectedIndex() == -1) {
            DialogUtil.exclamation("Выберите один из отчетов!");
            return;
        }
        ReportPasportGroup report = cmbReport.getSelectedItem().getValue();
        switch (report) {
            case STUDENT_LIST:
                showStudentlistReport();
                break;
            case DEBTORDS:
                showDebtorsReport();
                break;
            case DEBTORS_GOVERNMENT:
                showDebtorsBudgetReport();
                break;
            case DEBTORS_CONTRACTOR:
                showDebtorsNotBudgetReport();
                break;
            case DECAN:
                showDecanReport();
                break;
            case FORM_CONTROL:
                showFormControlReport();
                break;
            case PASSPORT_GROUP:
                downloadReportPassportGroup();
                break;
        }
    }

    /**
     * Скачать договор по паспорту групп
     */
    private void downloadReportPassportGroup () {
        if (groupList.getSelectedCount() == 0) {
            PopupUtil.showWarning("Выберите одну или более групп");
            return;
        }
        PassportGroupReportService passportGroupReportService = new PassportGroupReportService();
        List<GroupModel> tempListOfGroup = new ArrayList<>();
        for (Listitem li : groupList.getSelectedItems()) {
            tempListOfGroup.add(li.getValue());
        }
        try {
            Filedownload.save(
                    passportGroupReportService.generatePasportGroupXls(semesterList.getSelectedItem().getValue(), tempListOfGroup));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Список студентов
     */
    private void showStudentlistReport () {

        String rep = reports.getStudentlistReport(new ArrayList<>(groupList.getSelectedItems()),
                                                  ((SemesterModel) semesterList.getSelectedItem().getValue()).getIdSemester().intValue()
        );
        Filedownload.save(rep, "text/xml", "Список студентов (" + DateConverter.convertTimestampToString(new Date()) + ").xls");
    }

    /**
     * Справка декана
     */
    private void showDecanReport () {
        String rep = reports.getDecanReport(new ArrayList<>(groupList.getSelectedItems()), groupList.getSelectedItems().size());
        Filedownload.save(rep, "text/xml", "Справка декана (" + DateConverter.convertTimestampToString(new Date()) + ").xls");
    }

    /**
     * Отчет по должникам
     */
    private void showDebtorsReport () {
        String rep = reports.getDebtorsReport(typeOfSemesterFilter.getSelectedIndex(),
                                              ((InstituteModel) cmbInst.getSelectedItem().getValue()).getIdInst(),
                                              ((FormOfStudy) formOfStudyFilter.getSelectedItem().getValue()).getType(),
                                              courseFilter.getSelectedIndex(), groupFilter.getValue(), bachelor.isChecked(),
                                              master.isChecked(), engineer.isChecked(), formOfStudyFilter.getText()
        );
        Filedownload.save(rep, "text/xml", "Отчет по должникам (" + DateConverter.convertTimestampToString(new Date()) + ").xls");
    }

    /**
     * Должники
     */
    private void showDebtorsBudgetReport () {
        String rep = reports.getReportDeb(((SemesterModel) semesterList.getSelectedItem().getValue()).getIdSemester().intValue(), 1,
                                          ((FormOfStudy) formOfStudyFilter.getSelectedItem().getValue()).getType(),
                                          new ArrayList<>(groupList.getSelectedItems())
        );
        DateFormat df = new SimpleDateFormat("MM.dd.yyyy HH.mm");

        Date today = java.util.Calendar.getInstance().getTime();
        String reportDate = df.format(today);
        Filedownload.save(rep, "text/xml", "Должники(" + df.format(today) + ").xls");
    }

    /**
     * Должники-договорники
     */
    private void showDebtorsNotBudgetReport () {
        String rep = reports.getReportDeb(((SemesterModel) semesterList.getSelectedItem().getValue()).getIdSemester().intValue(), 0,
                                          ((FormOfStudy) formOfStudyFilter.getSelectedItem().getValue()).getType(),
                                          new ArrayList<>(groupList.getSelectedItems())
        );
        DateFormat df = new SimpleDateFormat("MM.dd.yyyy HH.mm");

        Date today = java.util.Calendar.getInstance().getTime();
        String reportDate = df.format(today);
        Filedownload.save(rep, "text/xml", "Должники-Договор(" + df.format(today) + ").xls");
    }

    /**
     * Формы-контроля за семестр
     */
    private void showFormControlReport () {
        String rep = reports.getFormControlReport(((SemesterModel) semesterList.getSelectedItem().getValue()).getIdSemester());
        Filedownload.save(rep, "text/xml", "Отчет по формам-контролю (" + DateConverter.convertTimestampToString(new Date()) + ").xls");
    }

    public enum ReportPasportGroup {
        STUDENT_LIST("Список студентов"), DEBTORS_CONTRACTOR("Должники (договор)"), DEBTORS_GOVERNMENT("Должники (бюджет)"), FORM_CONTROL(
                "Форма-контроль"), DECAN("Справка декана"), DEBTORDS("Должники (в разработке)"), PASSPORT_GROUP("Паспорт групп");
        private String name;

        ReportPasportGroup (String name) {
            this.name = name;
        }
    }
}