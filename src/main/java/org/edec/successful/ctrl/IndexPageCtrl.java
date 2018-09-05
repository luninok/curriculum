package org.edec.successful.ctrl;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.ModuleModel;
import org.edec.passportGroup.service.impl.ReportsServiceESO;
import org.edec.studentPassport.ctrl.renderer.StudentRanderer;
import org.edec.successful.ctrl.renderer.RatingByCourseRenderer;
import org.edec.successful.ctrl.renderer.RatingByTChairRenderer;
import org.edec.successful.model.CourseModel;
import org.edec.successful.model.DepartmentModel;
import org.edec.successful.service.SuccessfulService;
import org.edec.successful.service.impl.SuccessfulServiceImpl;
import org.edec.utility.component.model.ChairModel;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.GovFinancedConst;
import org.edec.utility.constants.LevelConst;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import static org.zkoss.zk.ui.Executions.getCurrent;


public class IndexPageCtrl extends SelectorComposer<Component> {
    @Wire
    private Combobox cmbInst, cmbFormOfStudy, cmbGovFinance, cmbSemester, cmbChair, cmbLvl, cmbGroup, cmbContingent;

    @Wire
    private Checkbox cbAcadem, cbFiz, cbPract, chSpec, chBach, chMaster, chCourse1, chCourse2, chCourse3, chCourse4, chCourse5;

    @Wire
    private Listbox lbStudent;

    @Wire
    private Groupbox gbFilter;

    @Wire
    private Button btnApply;

    @Wire
    private Vbox vbColumn1;

    @Wire
    private Datebox dbDate;

    @Wire
    private Radio rbCourse, rbChair;

    private TemplatePageCtrl template = new TemplatePageCtrl();
    private ComponentService componentService = new ComponentServiceESOimpl();

    private FormOfStudy currentFOS;
    private org.edec.main.model.DepartmentModel currentDep;
    private InstituteModel currentInst;
    private ModuleModel currentModule;

    private SuccessfulService successfulService = new SuccessfulServiceImpl();
    private ReportsServiceESO reports = new ReportsServiceESO();

    private List<CourseModel> courseModels;
    private List<DepartmentModel> departmentModel;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        template.checkModuleByRole(getCurrent().getDesktop().getRequestPath(), getPage());
        currentModule = template.getCurrentModule();
        if (currentModule != null) {
            fill();
        }
    }

    private void fill () {
        //lbStudent.setItemRenderer(new StudentRanderer(this, currentModule));
        currentInst = componentService.fillCmbInst(cmbInst, vbColumn1, currentModule.getDepartments());
        //special for Sanchez
        vbColumn1.setVisible(true);

        currentFOS = componentService.fillCmbFormOfStudy(cmbFormOfStudy, vbColumn1, currentModule.getFormofstudy());
        //TODO: заполнить Кафедры в зависимости от института
        componentService.fillCmbSem(cmbSemester, currentInst.getIdInst(), 1, null);
        componentService.fillCmbGovFinanced(cmbGovFinance);
        componentService.fillCmbLevel(cmbLvl);
        componentService.fillCmbChair(cmbChair);
        //componentService.fillCmbSem(cmbSemester, ((Integer)(1)).longValue(), 1, null);
        dbDate.setValue(new Date());
    }

    @Listen("onChange = #cmbSemester; onClick=#chSpec, #chBach, #chMaster, #chCourse1, #chCourse2, #chCourse3, #chCourse4, #chCourse5")
    public void onChangeSemOpen () {
        String courses = concatCourses();
        String levels = concatLevels();
        //TODO: Проверка семестра
        componentService.fillCmbGroupsWithEmpty(cmbGroup, ((SemesterModel) cmbSemester.getSelectedItem().getValue()).getIdSem(), courses,
                                                levels);

        cmbGroup.setText("");
    }

    @Listen("onOK=#btnApply; onClick = #btnApply")
    public void searchStudents () {
        Clients.showBusy(lbStudent, "Загрузка данных");
        if (rbCourse.isChecked()) {
            Events.echoEvent("onLater", lbStudent, null);
        } else if (rbChair.isChecked()) {
            Events.echoEvent("onChairLater", lbStudent, null);
        }
    }

    public String concatCourses () {
        String res = "";
        int i = 0;
        if (chCourse1.isChecked()) {
            res = "1";
            i++;
        }
        if (chCourse2.isChecked()) {
            if (i > 0) {
                res = res + ",2";
            } else {
                res = "2";
            }
            i++;
        }
        if (chCourse3.isChecked()) {
            if (i > 0) {
                res = res + ",3";
            } else {
                res = "3";
            }
            i++;
        }
        if (chCourse4.isChecked()) {
            if (i > 0) {
                res = res + ",4";
            } else {
                res = "4";
            }
            i++;
        }
        if (chCourse5.isChecked()) {
            if (i > 0) {
                res = res + ",5";
            } else {
                res = "5";
            }
            i++;
        }
        return res;
    }

    public String concatLevels () {
        String res = "";
        int i = 0;
        if (chBach.isChecked()) {
            res = LevelConst.BACH.getType().toString();
            i++;
        }
        if (chMaster.isChecked()) {
            if (i > 0) {
                res = res + "," + LevelConst.MAGISTR.getType().toString();
            } else {
                res = LevelConst.MAGISTR.getType().toString();
            }
            i++;
        }
        if (chSpec.isChecked()) {
            if (i > 0) {
                res = res + "," + LevelConst.SPEC.getType().toString();
            } else {
                res = LevelConst.SPEC.getType().toString();
            }
            i++;
        }
        return res;
    }

    //В разрезе курса
    @Listen("onLater = #lbStudent")
    public void laterLbStudent (Event event) {
        FormOfStudy fos = ((FormOfStudy) cmbFormOfStudy.getSelectedItem().getValue());
        String courses = concatCourses();
        String levels = concatLevels();
        Long idChair = null;
        if (cmbChair.getSelectedItem() != null) {
            idChair = ((ChairModel) cmbChair.getSelectedItem().getValue()).getIdChair();
        }

        //TODO: fix with Service
        addListHeadersForCourse();
        RatingByCourseRenderer ratingByCourseRenderer = new RatingByCourseRenderer();
        lbStudent.setItemRenderer(ratingByCourseRenderer);
        //TODO: check all fields
        courseModels = successfulService.fullRebuildRating(successfulService.getRatingByFilter(currentInst.getIdInst(),
                                                                                               ((SemesterModel) cmbSemester.getSelectedItem()
                                                                                                                           .getValue()).getIdSem(),
                                                                                               currentDep != null
                                                                                               ? currentDep.getIdDepartment()
                                                                                               : null, (fos.getType() == 3 ? null : fos),
                                                                                               //Спасибо Максиму за тройку
                                                                                               ((GovFinancedConst) cmbGovFinance.getSelectedItem()
                                                                                                                                .getValue()),
                                                                                               levels, cmbGroup.getValue(),
                                                                                               dbDate.getValue(), courses, idChair
        ));
        ratingByCourseRenderer.setCourseModels(courseModels);
        lbStudent.setModel(new ListModelList<>(courseModels));

        lbStudent.renderAll();
        Listfoot lfStudent = lbStudent.getListfoot();
        ratingByCourseRenderer.calcFooter(lfStudent);

        Clients.clearBusy(lbStudent);
    }

    // В разрезе кафедр
    @Listen("onChairLater = #lbStudent")
    public void laterLbStudentChair (Event event) {
        FormOfStudy fos = ((FormOfStudy) cmbFormOfStudy.getSelectedItem().getValue());
        String courses = concatCourses();
        String levels = concatLevels();
        Long idChair = null;
        if (cmbChair.getSelectedItem() != null) {
            idChair = ((ChairModel) cmbChair.getSelectedItem().getValue()).getIdChair();
        }

        //TODO: fix with Service
        addListHeadersForCourse();
        RatingByTChairRenderer ratingByTChairRenderer = new RatingByTChairRenderer();
        lbStudent.setItemRenderer(ratingByTChairRenderer);
        departmentModel = successfulService.fullRebuildRatingChair(successfulService.getRatingByFilter(currentInst.getIdInst(),
                                                                                                       ((SemesterModel) cmbSemester.getSelectedItem()
                                                                                                                                   .getValue())
                                                                                                               .getIdSem(),
                                                                                                       currentDep != null
                                                                                                       ? currentDep.getIdDepartment()
                                                                                                       : null,
                                                                                                       (fos.getType() == 3 ? null : fos),
                                                                                                       //Спасибо Максиму за тройку
                                                                                                       ((GovFinancedConst) cmbGovFinance.getSelectedItem()
                                                                                                                                        .getValue()),
                                                                                                       levels, cmbGroup.getValue(),
                                                                                                       dbDate.getValue(), courses, idChair
        ));
        ratingByTChairRenderer.setDepartmentModel(departmentModel);
        lbStudent.setModel(new ListModelList<>(departmentModel));

        lbStudent.renderAll();
        Listfoot lfStudent = lbStudent.getListfoot();
        ratingByTChairRenderer.calcFooter(lfStudent);

        Clients.clearBusy(lbStudent);
    }

    public void addListHeadersForCourse () {
        lbStudent.getListhead().detach();
        Listhead lh = new Listhead();
        lh.setParent(lbStudent);

        Listheader lhr = new Listheader();
        lhr.setParent(lbStudent.getListhead());
        Label lHeader = new Label("#");
        lHeader.setParent(lhr);
        lHeader.setSclass("cwf-listheader-label");
        lhr.setHflex("1");
        lhr.setTooltiptext("#");
        lhr.setAlign("center");
        lhr.setStyle("cursor: pointer;");

        Listheader lhrCourse = new Listheader();
        lhrCourse.setParent(lbStudent.getListhead());
        Label lHeaderCourse = new Label("Курс");
        lHeaderCourse.setParent(lhrCourse);
        lHeaderCourse.setSclass("cwf-listheader-label");
        lhrCourse.setHflex("1");
        lhrCourse.setTooltiptext("Курс");
        lhrCourse.setAlign("center");
        lhrCourse.setStyle("cursor: pointer;");

        Listheader lhrCount = new Listheader();
        lhrCount.setParent(lbStudent.getListhead());
        Label lHeaderCount = new Label("Кол-во");
        lHeaderCount.setParent(lhrCount);
        lHeaderCount.setSclass("cwf-listheader-label");
        lhrCount.setHflex("1");
        lhrCount.setTooltiptext("Кол-во");
        lhrCount.setAlign("center");
        lhrCount.setStyle("cursor: pointer;");

        Listheader lhrAll = new Listheader();
        lhrAll.setParent(lbStudent.getListhead());
        Label lAll = new Label("Сдали");
        lAll.setParent(lhrAll);
        lAll.setSclass("cwf-listheader-label");
        lhrAll.setHflex("1");
        lhrAll.setTooltiptext("Сдали");
        lhrAll.setAlign("center");
        lhrAll.setStyle("cursor: pointer;");

        Listheader lhrAll5 = new Listheader();
        lhrAll5.setParent(lbStudent.getListhead());
        Label lAll5 = new Label("Все 5");
        lAll5.setParent(lhrAll5);
        lAll5.setSclass("cwf-listheader-label");
        lhrAll5.setHflex("1");
        lhrAll5.setTooltiptext("Все 5");
        lhrAll5.setAlign("center");
        lhrAll5.setStyle("cursor: pointer;");

        Listheader lhrAll45 = new Listheader();
        lhrAll45.setParent(lbStudent.getListhead());
        Label lAll45 = new Label("На 4 и 5");
        lAll45.setParent(lhrAll45);
        lAll45.setSclass("cwf-listheader-label");
        lhrAll45.setHflex("1");
        lhrAll45.setTooltiptext("На 4 и 5");
        lhrAll45.setAlign("center");
        lhrAll45.setStyle("cursor: pointer;");

        Listheader lhrAll4 = new Listheader();
        lhrAll4.setParent(lbStudent.getListhead());
        Label lAll4 = new Label("Все 4");
        lAll4.setParent(lhrAll4);
        lAll4.setSclass("cwf-listheader-label");
        lhrAll4.setHflex("1");
        lhrAll4.setTooltiptext("Все 4");
        lhrAll4.setAlign("center");
        lhrAll4.setStyle("cursor: pointer;");

        Listheader lhrAll34 = new Listheader();
        lhrAll34.setParent(lbStudent.getListhead());
        Label lAll34 = new Label("На 3 и 4-5");
        lAll34.setParent(lhrAll34);
        lAll34.setSclass("cwf-listheader-label");
        lhrAll34.setHflex("1");
        lhrAll34.setTooltiptext("На 3 и 4-5");
        lhrAll34.setAlign("center");
        lhrAll34.setStyle("cursor: pointer;");

        Listheader lhrAll3 = new Listheader();
        lhrAll3.setParent(lbStudent.getListhead());
        Label lAll3 = new Label("Все 3");
        lAll3.setParent(lhrAll3);
        lAll3.setSclass("cwf-listheader-label");
        lhrAll3.setHflex("1");
        lhrAll3.setTooltiptext("Все 3");
        lhrAll3.setAlign("center");
        lhrAll3.setStyle("cursor: pointer;");

        Listheader lhrAll23 = new Listheader();
        lhrAll23.setParent(lbStudent.getListhead());
        Label lAll23 = new Label("На 2 и 3");
        lAll23.setParent(lhrAll23);
        lAll23.setSclass("cwf-listheader-label");
        lhrAll23.setHflex("1");
        lhrAll23.setTooltiptext("На 2 и 3");
        lhrAll23.setAlign("center");
        lhrAll23.setStyle("cursor: pointer;");

        Listheader lhrMiss = new Listheader();
        lhrMiss.setParent(lbStudent.getListhead());
        Label lMiss = new Label("Долги");
        lMiss.setParent(lhrMiss);
        lMiss.setSclass("cwf-listheader-label");
        lhrMiss.setHflex("1");
        lhrMiss.setTooltiptext("Долги");
        lhrMiss.setAlign("center");
        lhrMiss.setStyle("cursor: pointer;");

        Listheader lhrMiss1 = new Listheader();
        lhrMiss1.setParent(lbStudent.getListhead());
        Label lMiss1 = new Label("1 долг");
        lMiss1.setParent(lhrMiss1);
        lMiss1.setSclass("cwf-listheader-label");
        lhrMiss1.setHflex("1");
        lhrMiss1.setTooltiptext("1 долг");
        lhrMiss1.setAlign("center");
        lhrMiss1.setStyle("cursor: pointer;");

        Listheader lhrMiss2 = new Listheader();
        lhrMiss2.setParent(lbStudent.getListhead());
        Label lMiss2 = new Label("2 долга");
        lMiss2.setParent(lhrMiss2);
        lMiss2.setSclass("cwf-listheader-label");
        lhrMiss2.setHflex("1");
        lhrMiss2.setTooltiptext("2 долга");
        lhrMiss2.setAlign("center");
        lhrMiss2.setStyle("cursor: pointer;");

        Listheader lhrMiss34 = new Listheader();
        lhrMiss34.setParent(lbStudent.getListhead());
        Label lMiss34 = new Label("3-4 долга");
        lMiss34.setParent(lhrMiss34);
        lMiss34.setSclass("cwf-listheader-label");
        lhrMiss34.setHflex("1");
        lhrMiss34.setTooltiptext("3-4 долга");
        lhrMiss34.setAlign("center");
        lhrMiss34.setStyle("cursor: pointer;");

        Listheader lhrMiss5M = new Listheader();
        lhrMiss5M.setParent(lbStudent.getListhead());
        Label lMiss5M = new Label("Более 5");
        lMiss5M.setParent(lhrMiss5M);
        lMiss5M.setSclass("cwf-listheader-label");
        lhrMiss5M.setHflex("1");
        lhrMiss5M.setTooltiptext("Более 5");
        lhrMiss5M.setAlign("center");
        lhrMiss5M.setStyle("cursor: pointer;");

        Listheader lhrMissFull = new Listheader();
        lhrMissFull.setParent(lbStudent.getListhead());
        Label lMissFull = new Label("Не сдал полностью");
        lMissFull.setParent(lhrMissFull);
        lMissFull.setSclass("cwf-listheader-label");
        lhrMissFull.setHflex("1");
        lhrMissFull.setTooltiptext("Не сдал полностью");
        lhrMissFull.setAlign("center");
        lhrMissFull.setStyle("cursor: pointer;");
    }
}
