package org.edec.register.ctrl;

import org.apache.log4j.Logger;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.ModuleModel;
import org.edec.model.GroupModel;
import org.edec.register.comparator.RegisterComparator;
import org.edec.register.model.RegisterModel;
import org.edec.register.model.RegisterRequestModel;
import org.edec.register.model.RetakeSubjectModel;
import org.edec.register.model.SubjectModel;
import org.edec.register.model.dao.RegisterWithSignDateModelESO;
import org.edec.register.renderer.GroupRenderer;
import org.edec.register.renderer.RegisterRenderer;
import org.edec.register.renderer.RegisterRequestRenderer;
import org.edec.register.renderer.SubjectRenderer;
import org.edec.register.service.RetakeService;
import org.edec.register.service.RegisterRequestService;
import org.edec.register.service.RegisterService;
import org.edec.register.service.SubjectService;
import org.edec.register.service.impl.RetakeImpl;
import org.edec.register.service.impl.RegisterRequestServiceImpl;
import org.edec.register.service.impl.RegisterServiceImpl;
import org.edec.register.service.impl.SubjectServiceImpl;
import org.edec.utility.Timing;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.RegisterConst;
import org.edec.utility.constants.RegisterRequestStatusConst;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.File;
import java.util.*;
import java.util.Calendar;

import static org.edec.register.service.RegisterService.INDIVIDUAL_RETAKE;
import static org.edec.register.service.RegisterService.MAIN_RETAKE;
import static org.zkoss.zk.ui.Executions.getCurrent;

/**
 * Created by antonskripacev on 10.06.17.
 */
public class IndexPageCtrl extends SelectorComposer<Component> {
    private final Logger log = Logger.getLogger(RegisterServiceImpl.class.getName());

    ///------------------
    ///~Открытие пересдачи
    @Wire
    private Checkbox chOpenRetakeBachelor, chOpenRetakeMaster, chOpenRetakeEngineer;
    @Wire
    private Combobox cmbOpenRetakeSem, cmbOpenRetakeCourse;
    @Wire
    private Datebox dbOpenRetakeDateBegin, dbOpenRetakeDateEnd;
    @Wire
    private Listbox lbOpenRetakeGroup, lbOpenRetakeSubject;
    @Wire
    private Tabpanel tabpOpenRetake;
    @Wire
    private Textbox tbOpenRetakeGroup, tbOpenRetakeFilter;
    @Wire
    private Auxheader ahDateRegisterRetake;
    ///~/Открытие пересдачи
    ///------------------

    @Wire
    private Tab tabRetake, tabRegisterRequest;
    @Wire
    private Combobox cmbInstituteShow, cmbFKShow, cmbSemesterShow, cmbInstituteOpen, cmbFKOpen, cmbSemesterOpen, cmbCourseOpen, cmbInstituteRegisterRequest, cmbFKRegisterRequest;
    @Wire
    private Textbox tbFioStudent, tbFioTeacher, tbGroupShow, tbGroupFilterGroupName, tbSubjectNameOpen, tbFioTeacherOpen, tbSubjectFilterSubjectName, tbSubjectFilterGroupName, tbSubjectFilterFioTeacher;
    @Wire
    private Checkbox chbOpened, chbClosed, chbOutOfDate, chbMain, chbMainRetake, chbIndividualRetake, chbCommission, chbApproved, chbDenied, chbUnderConsideration, chbBachelor, chbMaster, chbSpeciality, chbWithNumber, chbengineer, chbbachelor, chbmaster;

    @Wire
    private Button btnSearchShow, btnShowSubjects, btnRetakeOpen, btnPersonalRetakeOpen, btnOpenWinGetRegisterReport;
    @Wire
    private Listbox listboxRetakes, lbGroups, listboxSubjects, listboxRegisterRequests;
    @Wire
    private Listheader lhSubj, lhGroup, lhFC, lhTypeReg, lhDate, lhNumber, lhDelete;
    @Wire
    private Radio rRetakeGeneral, rRegisterIndividual;
    @Wire
    private Datebox dateOfBeginRetake, dateOfEndRetake, dateOfBeginPersonalRetake, dateOfEndPersonalRetake;

    // table registers filters
    @Wire
    private Textbox tbRegisterSubjectFilter, tbRegisterGroupFilter, tbRegisterTeacherFilter, tbRegisterNumberFilter;
    @Wire
    private Datebox dbRegisterBegin, dbRegisterEnd;
    @Wire
    private Combobox cmbRegisterFKFilter, cmbSynchStatus;

    @Wire
    Textbox tbRegisterRequestFioStudentFilter, tbRegisterRequestFioTeacherFilter, tbRegisterRequestGroupFilter, tbRegisterRequestSubjectFilter;

    private ComponentService componentService = new ComponentServiceESOimpl();
    private RetakeService retakeService = new RetakeImpl();
    private RegisterService registerService = new RegisterServiceImpl();
    private SubjectService subjectService = new SubjectServiceImpl();
    private RegisterRequestService registerRequestService = new RegisterRequestServiceImpl();

    private TemplatePageCtrl template = new TemplatePageCtrl();
    private ModuleModel currentModule;

    private InstituteModel currentInstituteShow, currentInstituteOpen, currentInstituteRegisterRequest;
    private FormOfStudy currentFOSShow, currentFOSOpen, currentFOSRegisterRequest;

    private List<RegisterModel> listRetakes;
    private List<String> listGroups, listSelectedGroups = new ArrayList<>();
    private List<SubjectModel> listSubjects, listFilteredSubjects = new ArrayList<>();
    private List<RegisterRequestModel> listRegisterRequests = new ArrayList<>();

    private ListModelList<RegisterRequestModel> registerRequestModels;

    private Runnable updateRegisterRequests = this::getRegisterRequest;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        lhDelete.setVisible(false);
        template.checkModuleByRole(getCurrent().getDesktop().getRequestPath(), getPage());
        currentModule = template.getCurrentModule();

        // Отключаем общие пересдачи по просьбе деканата
        rRetakeGeneral.setVisible(false);
        rRegisterIndividual.setVisible(false);

        if (currentModule != null) {
            init();
        }
    }

    /**
     * Функция используется для заполнения даты подписи коммиссий по дате создания файла в случае,
     * если эта дата в бд не заполнена.
     */
    private void updateDateSignForCommissionRegisters() {
        RegisterServiceImpl registerService = new RegisterServiceImpl();
        List<RegisterWithSignDateModelESO> listCommissions = registerService.getAllCommissionsWithNullSignDate();
        FileManager manager = new FileManager();
        for (RegisterWithSignDateModelESO commission : listCommissions) {
            if (commission.getPathToFile() != null && !commission.getPathToFile().equals("")) {
                String path = commission.getPathToFile();
                if (!path.contains(".pdf")) {
                    int lastIndexOfSeparator = path.lastIndexOf(File.separator);
                    String fileRegisterName = path.substring(lastIndexOfSeparator + 1, path.length()) + ".pdf";
                    path = path + File.separator + fileRegisterName;
                }

                File commissionFile = manager.getFileByRelativePath(path);

                if (commissionFile.lastModified() == 0) {
                    log.error("Проблемы с ведомостью с id " + commission.getIdRegister() + " при обновлении даты подписания");
                    continue;
                }

                Date lastModify = new Date(commissionFile.lastModified());
                registerService.updateDateSignForRegister(commission.getIdRegister(), commission.getIdSessionRatingHistory(), lastModify);
            }
        }
    }

    private void init () {
        fillOpenRetakeTab();
        listboxRetakes.setItemRenderer(
                new RegisterRenderer(this::searchRetakes, template.getCurrentUser().getFio(), currentModule.isReadonly()));
        lbGroups.setItemRenderer(new GroupRenderer(this::searchSubjects));
        listboxSubjects.setItemRenderer(new SubjectRenderer(this::showSubjects));

        if (currentModule.isReadonly()) {
            tabRetake.setVisible(false);
            tabRetake.setDisabled(true);
            tabRegisterRequest.setVisible(false);
            tabRegisterRequest.setDisabled(true);
        }

        for (FormOfControlConst controlConst : FormOfControlConst.values()) {
            cmbRegisterFKFilter.appendItem(controlConst.getName());
        }

        currentInstituteShow = componentService.fillCmbInst(cmbInstituteShow, cmbInstituteShow, currentModule.getDepartments());
        currentInstituteOpen = componentService.fillCmbInst(cmbInstituteOpen, cmbInstituteOpen, currentModule.getDepartments());
        currentInstituteRegisterRequest = componentService
                .fillCmbInst(cmbInstituteRegisterRequest, cmbInstituteRegisterRequest, currentModule.getDepartments());
        currentFOSShow = componentService.fillCmbFormOfStudy(cmbFKShow, cmbFKShow, currentModule.getFormofstudy());
        currentFOSOpen = componentService.fillCmbFormOfStudy(cmbFKOpen, cmbFKOpen, currentModule.getFormofstudy());
        currentFOSRegisterRequest = componentService
                .fillCmbFormOfStudy(cmbFKRegisterRequest, cmbFKRegisterRequest, currentModule.getFormofstudy());
        componentService.fillCmbSem(cmbSemesterShow, currentInstituteShow.getIdInst(), currentFOSShow.getType(), null);
        componentService.fillCmbSem(cmbSemesterOpen, currentInstituteOpen.getIdInst(), currentFOSOpen.getType(), null);

        if (cmbSemesterOpen.getItemCount() > 0) {
            cmbSemesterOpen.setSelectedIndex(0);
        }
        if (cmbSemesterShow.getItemCount() > 0) {
            cmbSemesterShow.setSelectedIndex(0);
        }

        setCalendarDate();
        setSortMethods();
        getRegisterRequest();
    }

    public void setCalendarDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 3);

        dateOfBeginRetake.setValue(new Date());
        dateOfBeginPersonalRetake.setValue(new Date());
        dateOfEndRetake.setValue(cal.getTime());
        dateOfEndPersonalRetake.setValue(cal.getTime());
    }

    public void setSortMethods() {
        lhNumber.setSortAscending(new RegisterComparator(RegisterComparator.CompareMethods.BY_REG_NUMBER));
        lhNumber.setSortDescending(new RegisterComparator(RegisterComparator.CompareMethods.BY_REG_NUMBER_REV));

        lhGroup.setSortAscending(new RegisterComparator(RegisterComparator.CompareMethods.BY_GROUP));
        lhGroup.setSortDescending(new RegisterComparator(RegisterComparator.CompareMethods.BY_GROUP_REV));

        lhDate.setSortAscending(new RegisterComparator(RegisterComparator.CompareMethods.BY_DATE));
        lhDate.setSortDescending(new RegisterComparator(RegisterComparator.CompareMethods.BY_DATE_REV));
    }

    @Listen("onChange = #cmbInstituteShow; onChange = #cmbFKShow")
    public void onChangeShow() {
        currentFOSShow = cmbFKShow.getSelectedItem().getValue();
        componentService.fillCmbSem(cmbSemesterShow, currentInstituteShow.getIdInst(), currentFOSShow.getType(), null);
    }

    @Listen("onChange = #cmbInstituteOpen; onChange = #cmbFKOpen")
    public void onChangeOpen() {
        currentFOSOpen = cmbFKOpen.getSelectedItem().getValue();
        componentService.fillCmbSem(cmbSemesterOpen, currentInstituteOpen.getIdInst(), currentFOSOpen.getType(), null);
    }

    @Listen("onClick = #btnSearchShow")
    public void searchRetakes() {

        SemesterModel semester = cmbSemesterShow.getSelectedItem() == null ? null : cmbSemesterShow.getSelectedItem().getValue();

        boolean[] chbs = new boolean[11];
        chbs[RegisterService.OPENED] = chbOpened.isChecked();
        chbs[RegisterService.SIGNED] = chbClosed.isChecked();
        chbs[RegisterService.OUT_OF_DATE] = chbOutOfDate.isChecked();
        if (!chbOpened.isChecked() && !chbClosed.isChecked() && !chbOutOfDate.isChecked()) {
            chbs[RegisterService.OPENED] = chbs[RegisterService.SIGNED] = chbs[RegisterService.OUT_OF_DATE] = true;
        }

        chbs[RegisterService.MAIN] = chbMain.isChecked();
        chbs[MAIN_RETAKE] = chbMainRetake.isChecked();
        chbs[RegisterService.INDIVIDUAL_RETAKE] = chbIndividualRetake.isChecked();
        chbs[RegisterService.COMMISSION] = chbCommission.isChecked();
        if (!chbMain.isChecked() && !chbMainRetake.isChecked() && !chbIndividualRetake.isChecked() && !chbCommission.isChecked()) {
            chbs[RegisterService.MAIN] = chbs[MAIN_RETAKE] = chbs[RegisterService.INDIVIDUAL_RETAKE] = chbs[RegisterService.COMMISSION] = true;
        }

        chbs[RegisterService.BACHELOR] = chbBachelor.isChecked();
        chbs[RegisterService.MASTER] = chbMaster.isChecked();
        chbs[RegisterService.SPECIALITY] = chbSpeciality.isChecked();
        if (!chbBachelor.isChecked() && !chbMaster.isChecked() && !chbSpeciality.isChecked()) {
            chbs[RegisterService.BACHELOR] = chbs[RegisterService.MASTER] = chbs[RegisterService.SPECIALITY] = true;
        }

        chbs[RegisterService.WITH_NUMBER] = chbWithNumber.isChecked();

        listRetakes = registerService.getRetakesByFilter(currentInstituteShow.getIdInst(), currentFOSShow.getType(),
                                                         semester != null ? semester.getIdSem() : null, tbGroupShow.getText(),
                                                         tbFioStudent.getText(), tbFioTeacher.getText(), chbs
        );

        filterRegisters();
    }

    @Listen("onChange = #cmbSemesterOpen")
    public void changeSemester() {
        cmbCourseOpen.setSelectedIndex(0);
    }

    @Listen("onClick = #btnSearchSubjects")
    public void searchSubjects() {
        if (cmbSemesterOpen.getSelectedItem() == null) {
            Clients.showNotification("Сначала выберите семестр!", Clients.NOTIFICATION_TYPE_WARNING, null, null, 2000);
            return;
        }
        SemesterModel semester = cmbSemesterOpen.getSelectedItem().getValue();

        listSubjects = subjectService
                .getSubjects(currentInstituteOpen.getIdInst(), currentFOSOpen.getType(), semester != null ? semester.getIdSem() : null,
                             cmbCourseOpen.getSelectedIndex(), tbFioTeacherOpen.getText(), tbSubjectNameOpen.getText(),
                             chbbachelor.isChecked(), chbengineer.isChecked(), chbmaster.isChecked()
                );

        listGroups = subjectService.getGroupNameList(listSubjects);
        listSelectedGroups.clear();

        fillGroupListBox(lbGroups, listGroups);
    }

    @Listen("onClick = #btnShowSubjects")
    public void showSubjects() {
        if (lbGroups.getSelectedCount() != 0) {
            List<Listitem> groupItems = new ArrayList<>();
            groupItems.addAll(lbGroups.getSelectedItems());

            listSubjects.sort(Comparator.comparing(SubjectModel::getSubjectName));

            listFilteredSubjects = subjectService.getSubjectsBySelectedGroups(listSubjects, groupItems);

            fillSubjectListBox(listboxSubjects, listFilteredSubjects);
        } else {
            DialogUtil.exclamation("Сначала выберите группу");
        }
    }

    @Listen("onOK = #tbGroupFilterGroupName")
    public void filterGroupList() {
        listGroups = subjectService
                .getGroupNameList(subjectService.filterSubjectList(listSubjects, "", "", tbGroupFilterGroupName.getValue()));

        fillGroupListBox(lbGroups, listGroups);

        Set<Listitem> selectedItems = new HashSet<>();
        for (Listitem listitem : lbGroups.getItems()) {
            if (listSelectedGroups.contains(listitem.getValue())) {
                selectedItems.add(listitem);
            }
        }
        lbGroups.setSelectedItems(selectedItems);
    }

    @Listen("onOK = #tbSubjectFilterFioTeacher; onOK = #tbSubjectFilterSubjectName; onOK = #tbSubjectFilterGroupName")
    public void filterSubjectList() {
        List<Listitem> groupItems = new ArrayList<>();
        groupItems.addAll(lbGroups.getSelectedItems());
        listFilteredSubjects = subjectService.getSubjectsBySelectedGroups(listSubjects, groupItems);

        listFilteredSubjects = subjectService
                .filterSubjectList(listFilteredSubjects, tbSubjectFilterFioTeacher.getText(), tbSubjectFilterSubjectName.getText(),
                                   tbSubjectFilterGroupName.getText()
                );

        fillSubjectListBox(listboxSubjects, listFilteredSubjects);
    }

    public void fillGroupListBox(Listbox listbox, List<String> data) {
        ListModelList<String> lmGroup = new ListModelList<>(data);
        lmGroup.setMultiple(true);
        listbox.setMultiple(true);
        listbox.setCheckmark(true);
        listbox.setAttribute("data", lbGroups);
        listbox.setModel(lmGroup);
        listbox.renderAll();

        listbox.addEventListener(Events.ON_SELECT, event -> {
            listSelectedGroups.clear();
            List<Listitem> groupItems = new ArrayList<>();
            groupItems.addAll(listbox.getSelectedItems());
            for (Listitem listitem : groupItems) {
                listSelectedGroups.add(listitem.getValue());
            }
        });
    }

    public void fillSubjectListBox(Listbox listbox, List<SubjectModel> data) {
        ListModelList<SubjectModel> lmSubject = new ListModelList<>(data);
        lmSubject.setMultiple(true);
        listbox.setMultiple(true);
        listbox.setCheckmark(true);
        listbox.setAttribute("data", listboxSubjects);
        listbox.setModel(lmSubject);
        listbox.renderAll();
    }

    @Listen("onClick = #btnRetakeOpen")
    public void openRetakes() {
        if (listboxSubjects.getSelectedCount() == 0) {
            DialogUtil.exclamation("Не выбрана ни одна дисциплина");
            return;
        }

        /*
        if (!rRetakeGeneral.isChecked() && !rRegisterIndividual.isChecked()) {
            DialogUtil.exclamation("Не выбран тип пересдачи");
            return;
        }*/

        if (dateOfBeginRetake.getValue() == null || rRegisterIndividual == null) {
            DialogUtil.exclamation("Не заполнены даты пересдач");
            return;
        }

        if (dateOfBeginRetake.getValue().after(dateOfEndRetake.getValue())) {
            DialogUtil.exclamation("Дата окончания пересдачи не может быть раньше даты начала пересдач.");
            return;
        }

        if (dateOfEndRetake.getValue().before(new Date())) {
            DialogUtil.questionWithYesNoButtons("Открытые ведомости будут просроченными. Вы уверены?", "Внимание!", event -> {
                if (event.getName().equals(DialogUtil.ON_YES)) {
                    openRetakesForSelectedItems();
                }
            });

            return;
        }

        openRetakesForSelectedItems();
    }

    @Listen("onOK = #tbRegisterSubjectFilter, #tbRegisterGroupFilter, #tbRegisterTeacherFilter, #tbRegisterNumberFilter;" +
            "onChange = #dbRegisterBegin, #dbRegisterEnd, #cmbRegisterFKFilter, #cmbSynchStatus")
    public void filterRegisters() {
        List<RegisterModel> filteredList = new ArrayList<>();

        if (listRetakes != null) {
            for (RegisterModel registerModel : listRetakes) {
                if (!tbRegisterSubjectFilter.getValue().equals("")) {
                    if (!registerModel.getSubjectName().toLowerCase().contains(tbRegisterSubjectFilter.getValue().toLowerCase())) {
                        continue;
                    }
                }

                if (!tbRegisterGroupFilter.getValue().equals("")) {
                    if (!registerModel.getGroupName().toLowerCase().contains(tbRegisterGroupFilter.getValue().toLowerCase())) {
                        continue;
                    }
                }

                if (!tbRegisterTeacherFilter.getValue().equals("")) {
                    if (!registerModel.getTeachers().toString().toLowerCase().contains(tbRegisterTeacherFilter.getValue().toLowerCase())) {
                        continue;
                    }
                }

                if (!tbRegisterNumberFilter.getValue().equals("")) {
                    if (registerModel.getRegisterNumber() == null || registerModel.getRegisterNumber().equals("")) {
                        continue;
                    }

                    if (!registerModel.getRegisterNumber().toLowerCase().contains(tbRegisterNumberFilter.getValue().toLowerCase())) {
                        continue;
                    }
                }

                if (cmbRegisterFKFilter.getSelectedIndex() != -1) {
                    FormOfControlConst fkFilter = FormOfControlConst.getValue(cmbRegisterFKFilter.getSelectedItem().getLabel());

                    if (!registerModel.getFoc().equals(fkFilter.getValue())) {
                        continue;
                    }
                }

                if (cmbSynchStatus.getSelectedIndex() != -1) {
                    if (registerModel.getSynchStatus() == null && cmbSynchStatus.getSelectedIndex() != 0) {
                        continue;
                    }

                    if (registerModel.getSynchStatus() != null && (cmbSynchStatus.getSelectedIndex() == 0 ||
                        registerModel.getSynchStatus() != cmbSynchStatus.getSelectedIndex())) {
                        continue;
                    }
                }

                if (dbRegisterBegin.getValue() != null) {
                    if (registerModel.getSignDate() == null) {
                        continue;
                    }

                    if (registerModel.getSignDate().before(dbRegisterBegin.getValue())) {
                        continue;
                    }
                }

                if (dbRegisterEnd.getValue() != null) {
                    if (registerModel.getSignDate() == null) {
                        continue;
                    }

                    if (registerModel.getSignDate().after(dbRegisterEnd.getValue())) {
                        continue;
                    }
                }

                filteredList.add(registerModel);
            }
        }

        listboxRetakes.setModel(new ListModelList<>(filteredList));
        listboxRetakes.renderAll();
    }

    private void openRetakesForSelectedItems() {
        //int typeRetake = rRegisterIndividual.isChecked() ? INDIVIDUAL_RETAKE : MAIN_RETAKE;
        int typeRetake = INDIVIDUAL_RETAKE;

        List<SubjectModel> selectedSubjects = new ArrayList<>();

        for (Listitem item : listboxSubjects.getSelectedItems()) {
            selectedSubjects.add(item.getValue());
        }

        boolean isAllSuccessful = true;

        for (SubjectModel sbj : selectedSubjects) {
            if (!registerService.openRetake(sbj, typeRetake, dateOfBeginRetake.getValue(), dateOfEndRetake.getValue(),
                                            template.getCurrentUser().getFio()
            )) {
                isAllSuccessful = false;
            }
        }

        if (isAllSuccessful) {
            PopupUtil.showInfo("Все пересдачи были успешно открыты");
        } else {
            PopupUtil.showError("При открытии одной или более пересдач произошла ошибка, обратитесь к администратору");
        }
    }

    @Listen("onClick = #btnUpdate")
    public void getRegisterRequest() {
        listRegisterRequests = registerRequestService
                .getRegisterRequests(currentInstituteRegisterRequest.getIdInst(), currentFOSRegisterRequest.getType());
        filterRegisterRequests();
    }

    public void fillRequestHistory(List<RegisterRequestModel> requestHistory) {
        registerRequestModels = new ListModelList<>(requestHistory);
        registerRequestModels.setMultiple(true);
        listboxRegisterRequests.setModel(registerRequestModels);
        listboxRegisterRequests.setItemRenderer(new RegisterRequestRenderer(updateRegisterRequests));
        listboxRegisterRequests.renderAll();
    }

    @Listen("onClick = #chbUnderConsideration, #chbApproved, #chbDenied; onOK = #tbRegisterRequestFioStudentFilter, #tbRegisterRequestFioTeacherFilter, #tbRegisterRequestGroupFilter, #tbRegisterRequestSubjectFilter;")
    public void filterRegisterRequests() {
        fillRequestHistory(registerRequestService.filterRequestHistory(listRegisterRequests, chbApproved.isChecked(), chbDenied.isChecked(),
                                                                       chbUnderConsideration.isChecked(),
                                                                       tbRegisterRequestFioTeacherFilter.getValue(),
                                                                       tbRegisterRequestFioStudentFilter.getValue(),
                                                                       tbRegisterRequestGroupFilter.getValue(),
                                                                       tbRegisterRequestSubjectFilter.getValue()
        ));
    }

    @Listen("onClick= #btnPersonalRetakeOpen")
    public void openPersonalRetake() {

        List<RegisterRequestModel> selectedRegisterRequests = new ArrayList<>();

        if (listboxRegisterRequests.getSelectedCount() == 0) {
            DialogUtil.exclamation("Не выбрана ни одна заявка");
            return;
        }

        if ((dateOfBeginPersonalRetake.getValue() == null) || (dateOfEndPersonalRetake.getValue() == null)) {
            DialogUtil.exclamation("Не заполнены даты пересдач");
            return;
        }

        if (dateOfBeginPersonalRetake.getValue().after(dateOfEndPersonalRetake.getValue())) {
            DialogUtil.exclamation("Дата начала пересдач не может быть позже даты окончания пересдач.");
            return;
        }

        for (Listitem item : listboxRegisterRequests.getSelectedItems()) {
            selectedRegisterRequests.add(item.getValue());
            if (((RegisterRequestModel) item.getValue()).getStatus() != RegisterRequestStatusConst.UNDER_CONSIDERATION) {
                DialogUtil.exclamation("Выберите только те заявки, которые находятся на рассмотрении!");
                return;
            }
        }

        if (dateOfEndPersonalRetake.getValue().before(new Date())) {
            DialogUtil.questionWithYesNoButtons("Открытые ведомости будут просроченными. Вы уверены?", "Внимание!", event -> {
                if (event.getName().equals(DialogUtil.ON_YES)) {
                    openSelectedPersonalRetakes(selectedRegisterRequests);
                }
            });

            return;
        }

        openSelectedPersonalRetakes(selectedRegisterRequests);
    }

    public void openSelectedPersonalRetakes(List<RegisterRequestModel> selectedRegisterRequests) {
        if (registerRequestService
                .openRetake(selectedRegisterRequests, dateOfBeginPersonalRetake.getValue(), dateOfEndPersonalRetake.getValue(),
                            template.getCurrentUser().getFio()
                )) {
            PopupUtil.showInfo("Все пересдачи были успешно открыты");
        } else {
            PopupUtil.showError("При открытии одной или более пересдач произошла ошибка, обратитесь к администратору");
        }

        getRegisterRequest();
    }

    @Listen("onClick = #btnOpenWinGetRegisterReport")
    public void openWinGetRegisterReport(){
        if (cmbSemesterShow.getSelectedIndex()== -1){
            PopupUtil.showWarning("Выберите семестр");
            return;
        }

        Map<String, Object> arg  = new HashMap<>();
        arg.put("Semester", cmbSemesterShow.getSelectedItem().getValue());

        ComponentHelper.createWindow("/register/winGetRegisterReport.zul", "winGetRegisterReport", arg).doModal();
    }

    ///------------------------
    ///~Открытие пересдачи
    private void fillOpenRetakeTab () {
        lbOpenRetakeGroup.setItemRenderer((ListitemRenderer<GroupModel>) (listitem, data, index) -> {
            listitem.setValue(data);
            new Listcell(data.getGroupname()).setParent(listitem);
        });
        lbOpenRetakeSubject.setItemRenderer((ListitemRenderer<RetakeSubjectModel>) (listitem, data, index) -> {
            listitem.setValue(data);
            new Listcell(data.getSubjectname()).setParent(listitem);
            new Listcell(data.getGroupname()).setParent(listitem);
            new Listcell(data.getTeachers()).setParent(listitem);
            new Listcell(data.getFoc()).setParent(listitem);
        });
    }

    @Listen("onSelect = #tabOpenRetake")
    public void selectTabOpenRekate () {
        componentService.fillCmbSem(cmbOpenRetakeSem, currentInstituteOpen.getIdInst(), currentFOSOpen.getType(), null);
    }

    @Listen("onChange = #cmbOpenRetakeSem; onChange = #cmbOpenRetakeCourse; onOK = #tbOpenRetakeGroup; " +
            "onCheck = #chOpenRetakeBachelor; onCheck = #chOpenRetakeMaster; onCheck = #chOpenRetakeEngineer")
    public void searchGroupInTabOpenRetake () {
        if (cmbOpenRetakeSem.getSelectedItem() == null) {
            PopupUtil.showWarning("Выберите семестр!");
            cmbOpenRetakeSem.focus();
            return;
        }
        if (!chOpenRetakeEngineer.isChecked() && !chOpenRetakeMaster.isChecked() && !chOpenRetakeBachelor.isChecked()) {
            PopupUtil.showWarning("Нужно выбрать хотя бы одну квалификацию!");
            return;
        }
        Clients.showBusy(lbOpenRetakeGroup, "Загрузка данных");
        Events.echoEvent("onLater", lbOpenRetakeGroup, null);
    }

    @Listen("onLater = #lbOpenRetakeGroup")
    public void lazyLoadingLbOpenRetakeGroup () {
        SemesterModel selectedSem = cmbOpenRetakeSem.getSelectedItem().getValue();
        Integer course = cmbOpenRetakeCourse.getSelectedIndex();
        String groupname = tbOpenRetakeGroup.getValue();
        String qualification = "";
        if (chOpenRetakeEngineer.isChecked()) {
            qualification += "1,";
        }
        if (chOpenRetakeBachelor.isChecked()) {
            qualification += "2,";
        }
        if (chOpenRetakeMaster.isChecked()) {
            qualification += "3,";
        }
        if (qualification.length() > 0) {
            qualification = qualification.substring(0, qualification.length() - 1);
        }
        ListModelList<GroupModel> lmGroups = new ListModelList<>(
                retakeService.getGroupsByFilter(qualification, course, selectedSem.getIdSem(), groupname));
        lmGroups.setMultiple(true);
        lmGroups.setSelection(lmGroups);
        lbOpenRetakeGroup.setModel(lmGroups);
        lbOpenRetakeGroup.renderAll();
        Clients.clearBusy(lbOpenRetakeGroup);
    }

    @Listen("onClick = #btnOpenRetakeShowSubject; onOK = #tbOpenRetakeFilter")
    public void searchSubjectForOpenRetake () {
        if (cmbOpenRetakeSem.getSelectedItem() == null) {
            PopupUtil.showWarning("Выберите семестр!");
            cmbOpenRetakeSem.focus();
            return;
        }
        Clients.showBusy(lbOpenRetakeSubject, "Загрузка данных");
        Events.echoEvent("onLater", lbOpenRetakeSubject, null);
    }

    @Listen("onLater = #lbOpenRetakeSubject")
    public void lazyLoadingLbOpenRetakeSubject () {
        StringBuilder sbOfIdGroup = new StringBuilder();
        SemesterModel selectedSemester = cmbOpenRetakeSem.getSelectedItem().getValue();
        for (Listitem liGroup : lbOpenRetakeGroup.getSelectedItems()) {
            GroupModel group = liGroup.getValue();
            sbOfIdGroup.append(group.getIdDG()).append(",");
        }
        String listOfIdGroup = "";
        if (sbOfIdGroup.length() > 0) {
            listOfIdGroup = sbOfIdGroup.substring(0, sbOfIdGroup.length() - 1);
        }
        ListModelList<RetakeSubjectModel> lmSubjects = new ListModelList<>(retakeService.getRetakeSubjects(
                selectedSemester.getIdSem(), listOfIdGroup, tbOpenRetakeFilter.getValue()));
        lmSubjects.setMultiple(true);
        lbOpenRetakeSubject.setModel(lmSubjects);
        lbOpenRetakeSubject.renderAll();
        Clients.clearBusy(lbOpenRetakeSubject);
    }

    @Listen("onClick = #btnOpenRetake")
    public void clickOnOpenRetake() {
        if (lbOpenRetakeSubject.getSelectedCount() == 0) {
            PopupUtil.showWarning("Выберите хотя бы один предмет, по которому хотите открыть пересдачу.");
            return;
        }
        if (dbOpenRetakeDateBegin.getValue() == null || dbOpenRetakeDateEnd.getValue() == null) {
            PopupUtil.showWarning("Дата начала и дата окончания должны быть заполнены!");
            return;
        }
        Date dateBegin = dbOpenRetakeDateBegin.getValue();
        Date dateEnd = dbOpenRetakeDateEnd.getValue();
        if (dateBegin.after(dateEnd)) {
            PopupUtil.showWarning("Дата окончания не может быть раньше даты начала пересдач!");
            return;
        }
        if (dateEnd.before(new Date())) {
            DialogUtil.questionWithYesNoButtons("Открытые ведомости будут просроченными. Вы уверены?", "Внимание!", event -> {
                if (event.getName().equals(DialogUtil.ON_YES)) {
                    callOpenRetakesWithBusy();
                    Events.echoEvent("onLater", tabpOpenRetake, null);
                }
            });
            return;
        }
        callOpenRetakesWithBusy();
    }

    private void callOpenRetakesWithBusy () {
        Clients.showBusy(tabpOpenRetake, "Открытие пересдач...");
        Events.echoEvent("onLater", tabpOpenRetake, null);
    }

    @Listen("onLater = #tabpOpenRetake")
    public void showBusyOnOpenRetakes() {
        Date dateBegin = dbOpenRetakeDateBegin.getValue();
        Date dateEnd = dbOpenRetakeDateEnd.getValue();
        for (Listitem listitem : lbOpenRetakeSubject.getSelectedItems()) {
            RetakeSubjectModel subject = listitem.getValue();
            if (retakeService.openRetake(subject.getIdLGSS(), subject.getFocInt(), dateBegin, dateEnd, RegisterConst.TYPE_RETAKE_INDIV_NOT_SIGNED)) {
                log.info("Успешно открыли пересдачу по предмету '" + subject.getSubjectname() + "', "
                         + subject.getGroupname() + ", " + subject.getFoc() + ". Открыл пересдачу: " + template.getCurrentUser().getFio());
            } else {
                log.warn("Не удалось создать пересдачу по предмету '" + subject.getSubjectname() + "', "
                         + subject.getGroupname() + ", " + subject.getFoc() + ". Попытался открыть пересдачу: " + template.getCurrentUser().getFio());
            }
        }
        Clients.clearBusy(tabpOpenRetake);
    }
}
