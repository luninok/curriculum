package org.edec.studyLoad.ctrl;


import org.edec.studyLoad.ctrl.renderer.VacancyRenderer;
import org.edec.studyLoad.ctrl.renderer.TeachersRenderer;
import org.edec.studyLoad.ctrl.windowCtrl.WinVacancyDialogCtrl;
import org.edec.studyLoad.model.TeacherModel;
import org.edec.studyLoad.model.VacancyModel;
import org.edec.studyLoad.service.impl.StudyLoadServiceImpl;
import org.edec.studyLoad.service.StudyLoadService;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexPageCtrl extends CabinetSelector {
    // public static final String SELECTED_POSITION = "selected_position";
    // public static final String SELECTED_RATE = "selected_rate";
    @Wire
    private Listbox lbTeachers, lbVacancy;
    @Wire
    private Combobox cmbFaculty;
    @Wire
    private Vbox col2;
    @Wire
    private Vbox col1;
    @Wire
    private Label labelFIO;

    private String selectFIO = "";

    private StudyLoadService studyLoadService = new StudyLoadServiceImpl();
    private Combobox selectedPosition;
    private Spinner selectedRate;
    private List<VacancyModel> vacancyModels = new ArrayList<>();
    private List<TeacherModel> teacherModels = new ArrayList<>();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        lbVacancy.setItemRenderer(new VacancyRenderer());
        lbTeachers.setItemRenderer(new TeachersRenderer());
    }

    protected void fill() {

        fillCmbFaculty();
        /*Clients.showBusy("Загрузка данных");
        Events.echoEvent("onLater", vbCalendar, null);
        Events.echoEvent("onFirstRun", vbCalendar, null);
        Events.echoEvent("onLater", cmbSemesterForTabDay, null);
        Events.echoEvent("onLater", cmbGroupForTabDay, null);*/

    }

    private void fillCmbFaculty() {

        List<String> list = studyLoadService.getDepartments();
        for (String department : list) {
            Comboitem comboitem = new Comboitem();
            comboitem.setLabel(department);
            cmbFaculty.getItems().add(comboitem);
        }
        /*List<TeacherModel> teacherModelList = new ArrayList<>();
        teacherModelList.add(new TeacherModel("d", "b", "c"));
        teacherModelList.add(new TeacherModel("f", "d", "s"));
        teacherModelList.add(new TeacherModel("f", "d", "s"));
        teacherModelList.add(new TeacherModel("f", "d", "s"));
        teacherModelList.add(new TeacherModel("f", "d", "s"));
        teacherModelList.add(new TeacherModel("f", "d", "s"));
        teacherModelList.add(new TeacherModel("f", "d", "s"));

        for (TeacherModel teacherModel : teacherModelList) {
            Row row = new Row();
            new Label(teacherModel.getLastName()).setParent(row);
            new Label(teacherModel.getFirstName()).setParent(row);
            new Label(teacherModel.getMiddleName()).setParent(row);

            lbTeachers.getRows().appendChild(row);
        }*/
        //Clients.showBusy(lbTeachers, "Загрузка данных из БД");
        //Events.echoEvent("onLater", lbTeachers, null);
    }

    @Listen("onDoubleClick = #lbTeachers")
    public void teacherRowClick() {
        labelFIO.setValue("");
        col1.setVisible(true);
        col2.setVisible(false);
        TeacherModel selectTeacher = lbTeachers.getSelectedItem().getValue();
        lbTeachers.getSelectedItem().setSelected(false);
        labelFIO.setValue(selectTeacher.toString());
    }

    @Listen("onClick = #btnRemoveVacancy")
    public void removeVacancyClick() {
        if (lbVacancy.getSelectedItem() != null) {
            vacancyModels.remove(lbVacancy.getSelectedIndex());
            lbVacancy.removeItemAt(lbVacancy.getSelectedIndex());
            PopupUtil.showInfo("Вакансия успешно удалена");
        } else {
            PopupUtil.showError("Выберити вакансию для удаления!");
        }

    }

    @Listen("onClick = #btnBackward")
    public void labelFIOClick() {
        col2.setVisible(true);
        col1.setVisible(false);
    }

    @Listen("onClick = #vacancyRows row")
    public void vacancyRowClick(Event e) {
        Row r = (Row) e.getTarget();
        String t = ((Label) r.getChildren().get(0)).getValue();
    }

    @Listen("onChange = #cmbFaculty")
    public void laterForTeachers() {
        lbTeachers.clearSelection();
        teacherModels.clear();
        List<TeacherModel> list = studyLoadService.getTeachers((String) cmbFaculty.getValue());
        teacherModels = list;
        ListModelList<TeacherModel> teacherListModelList = new ListModelList<>(teacherModels);
        lbTeachers.setModel(teacherListModelList);
        lbTeachers.renderAll();
        /*
        for (TeacherModel teacher : list) {
            Listitem listitem = new Listitem();
            listitem.setValue(teacher);
            new Listcell(teacher.getFamily()).setParent(listitem);
            new Listcell(teacher.getName()).setParent(listitem);
            new Listcell(teacher.getPatronymic()).setParent(listitem);
            Listcell cell = new Listcell();
            cell.setParent(listitem);
            lbTeachers.getItems().add(listitem);
        } */

        /*lbShowCommission.setModel(new ListModelList<>(list));
        lbShowCommission.renderAll();
        Clients.clearBusy(lbShowCommission);*/
    }

    @Listen("onClick = #btnAddRate")
    public void openWinRateStructure() {
        Map arg = new HashMap();
        Window win = (Window) Executions.createComponents("window/winRateDialog.zul", null, arg);
        win.doModal();
    }

    @Listen("onClick = #btnAddVacancy")
    public void openWinVacancyStructure() {
        Map arg = new HashMap();
        arg.put(WinVacancyDialogCtrl.INDEX_PAGE, this);
        Window win = (Window) Executions.createComponents("window/winVacancyDialog.zul", null, arg);
        win.doModal();
    }

    @Listen("onClick = #btnChangeVacancy")
    public void changeVacancyClick() {
        if (lbVacancy.getSelectedItem() != null) {

            Map arg = new HashMap();
            arg.put(WinVacancyDialogCtrl.INDEX_PAGE, this);
            arg.put(WinVacancyDialogCtrl.SELECT_VACANCY, lbVacancy.getSelectedItem().getValue());
            Window win = (Window) Executions.createComponents("window/winVacancyDialog.zul", null, arg);
            win.doModal();
        } else {
            PopupUtil.showInfo("Выберите вакансию!");
        }

    }


    public void fillLbVacancy(String position, String rate) {
        VacancyModel selectVacancy = new VacancyModel(position, rate, lbVacancy.getItemCount());
        vacancyModels.add(selectVacancy);
        ListModelList<VacancyModel> vacancyListModelList = new ListModelList<>(vacancyModels);
        lbVacancy.setModel(vacancyListModelList);
        lbVacancy.renderAll();
    }


    public void updateLbVacancy(String position, String rate) {
        VacancyModel changeVacancy = new VacancyModel(position, rate, lbVacancy.getSelectedIndex());
        for (VacancyModel vacancyModel : vacancyModels) {
            if (changeVacancy.getVacancy().equals(vacancyModel.getVacancy())) {
                vacancyModel.setVacancy(changeVacancy.getVacancy());
                vacancyModel.setRate(changeVacancy.getRate());
                vacancyModel.setPosition(changeVacancy.getPosition());
            }
        }
        lbVacancy.clearSelection();
        ListModelList<VacancyModel> vacancyListModelList = new ListModelList<>(vacancyModels);
        lbVacancy.setModel(vacancyListModelList);
        lbVacancy.renderAll();
    }
}
