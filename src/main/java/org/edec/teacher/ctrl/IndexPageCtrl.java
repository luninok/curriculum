package org.edec.teacher.ctrl;

import org.edec.teacher.ctrl.renderer.commission.CommissionRenderer;
import org.edec.teacher.ctrl.renderer.SubjectRenderer;
import org.edec.teacher.model.SemesterModel;
import org.edec.teacher.service.CompletionCommissionService;
import org.edec.teacher.service.CompletionService;
import org.edec.teacher.service.impl.CompletionCommissionImpl;
import org.edec.teacher.service.impl.CompletionServiceImpl;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.DialogUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class IndexPageCtrl extends CabinetSelector {
    @Wire
    private Checkbox chShowSignComm, chRegisterOnlyUnsigned;

    /**
     * Для обычных ведомостей
     **/
    @Wire
    private Combobox cmbInst, cmbFormOfStudy, cmbSem;

    /**
     * Для комиссионных ведомостей
     **/
    @Wire
    private Combobox cmbCommInst, cmbCommFormOfStudy, cmbCommSem;

    @Wire
    private Listbox lbCommission, lbSubject;

    @Wire
    private Tabpanel tpSubject, tpCommission;

    private CompletionService completionService = new CompletionServiceImpl();
    private CompletionCommissionService commissionService = new CompletionCommissionImpl();

    private List<SemesterModel> semesters;
    private List<SemesterModel> semestersCommission;

    protected void fill () {
        Clients.showBusy(tpSubject, "Загрузка данных");
        lbSubject.setItemRenderer(new SubjectRenderer(completionService.getEsoCourses()));
        lbCommission.setItemRenderer(new CommissionRenderer(this::refreshCommission));

        searchOnlyUnsignedRegister();
    }

    @Listen("onCheck = #chRegisterOnlyUnsigned")
    public void searchOnlyUnsignedRegister () {
        Clients.showBusy(tpSubject, "Загрузка данных");
        semesters = completionService.getSemesterByHumanface(getCurrentUser().getIdHum(), chRegisterOnlyUnsigned.isChecked());
        fillCmbInstAndEchoEvent(semesters, cmbInst);
    }

    @Listen("onChange = #cmbInst")
    public void changeCmbInst () {
        fillCmbFOSandEchoEvent(semesters, cmbInst, cmbFormOfStudy);
    }

    @Listen("onChange = #cmbFormOfStudy")
    public void changeCmbFormOfStudy (Event e) {
        fillCmbSemAndEchoEvent(semesters, cmbInst, cmbFormOfStudy, cmbSem);
    }

    @Listen("onChange = #cmbSem")
    public void changeCmbSem () {
        SemesterModel selectedSemester = cmbSem.getSelectedItem().getValue();
        lbSubject.setModel(new ListModelList<Object>(selectedSemester.getSubjects()));
        lbSubject.renderAll();
        Clients.clearBusy(tpSubject);
    }

    @Listen("onSelect = #tabCommission")
    public void fillCommission () {
        chShowSignComm.setChecked(false);
        refreshCommission();
    }

    @Listen("onCheck = #chShowSignComm")
    public void checkShowSignComm () {
        refreshCommission();
    }

    private void refreshCommission () {
        Clients.clearBusy(tpSubject);
        Clients.showBusy(tpCommission, "Загрузка данных");
        cmbCommSem.getChildren().clear();
        cmbCommSem.setValue("");
        cmbCommFormOfStudy.getChildren().clear();
        cmbCommFormOfStudy.setValue("");
        cmbCommInst.getChildren().clear();
        cmbCommInst.setValue("");
        lbCommission.getItems().clear();
        semestersCommission = commissionService.getSemesterCommByHum(template.getCurrentUser().getIdHum(), null,
                                                                     chShowSignComm.isChecked()
        );
        if (semestersCommission.size() == 0) {
            Clients.clearBusy(tpCommission);
            return;
        }
        fillCmbInstAndEchoEvent(semestersCommission, cmbCommInst);
    }

    @Listen("onChange = #cmbCommInst")
    public void changeCmbCommInst () {
        fillCmbFOSandEchoEvent(semestersCommission, cmbCommInst, cmbCommFormOfStudy);
    }

    @Listen("onChange = #cmbCommFormOfStudy")
    public void changeFormOfStudy () {
        fillCmbSemAndEchoEvent(semestersCommission, cmbCommInst, cmbCommFormOfStudy, cmbCommSem);
    }

    @Listen("onChange = #cmbCommSem")
    public void changeSem () {
        SemesterModel selectedSemester = cmbCommSem.getSelectedItem().getValue();
        lbCommission.setModel(new ListModelList<Object>(selectedSemester.getCommissions()));
        Clients.clearBusy(tpCommission);
    }

    private void fillCmbInstAndEchoEvent (List<SemesterModel> semesters, Combobox cmbInst) {
        cmbInst.getChildren().clear();
        Set<String> institutes = completionService.getInstitutesByModelSemester(semesters);
        for (String inst : institutes) {
            new Comboitem(inst).setParent(cmbInst);
        }

        if (institutes.size() == 0) {
            DialogUtil.exclamation("У Вас нет не подписанных ведомостей!");
            Clients.clearBusy(tpSubject);
            cmbInst.getChildren().clear();
            cmbInst.setValue("");
            cmbFormOfStudy.setValue("");
            cmbFormOfStudy.getChildren().clear();
            cmbSem.getChildren().clear();
            cmbSem.setValue("");
            lbSubject.getItems().clear();
            return;
        }
        cmbInst.setSelectedIndex(0);
        Events.echoEvent("onChange", cmbInst, null);
    }

    private void fillCmbFOSandEchoEvent (List<SemesterModel> semesters, Combobox cmbInst, Combobox cmbFOS) {
        cmbFOS.getChildren().clear();
        Set<String> formOfStudies = completionService.getFormOfStudyByInst(cmbInst.getValue(), semesters);
        for (String fos : formOfStudies) {
            new Comboitem(fos).setParent(cmbFOS);
        }
        cmbFOS.setSelectedIndex(0);
        Events.echoEvent("onChange", cmbFOS, null);
    }

    private void fillCmbSemAndEchoEvent (List<SemesterModel> semesters, Combobox cmbInst, Combobox cmbFOS, Combobox cmbSem) {
        cmbSem.getChildren().clear();
        int indexSem = 0;
        List<SemesterModel> semestersForCmb = completionService.getSemesterByFOSandInst(cmbInst.getValue(), cmbFOS.getValue(), semesters);
        for (SemesterModel semester : semestersForCmb) {
            Comboitem ci = new Comboitem(semester.getSemesterStr());
            ci.setValue(semester);
            ci.setParent(cmbSem);
            if (semester.getCurSem()) {
                indexSem = semestersForCmb.indexOf(semester);
            }
        }

        cmbSem.setSelectedIndex(indexSem);
        Events.echoEvent("onChange", cmbSem, null);
    }

    @Listen("onClick = #btnCourseAutoBinding")
    public void openCourseBindingWindow () {
        Map arg = new HashMap();

        arg.put(WinCourseBindingCtrl.USER, template.getCurrentUser());
        arg.put(WinCourseBindingCtrl.ID_CURRENT_SEMESTER, ((SemesterModel) cmbSem.getSelectedItem().getValue()).getIdSemester());

        ComponentHelper.createWindow("/teacher/winCourseBinding.zul", "winCourseBinding", arg).doModal();
    }
}
