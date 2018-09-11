package org.edec.synchroMine.ctrl;

import lombok.extern.log4j.Log4j;
import org.edec.main.model.DepartmentModel;
import org.edec.model.SemesterModel;
import org.edec.synchroMine.ctrl.renderer.GroupRenderer;
import org.edec.synchroMine.ctrl.renderer.GroupSubjecRenderer;
import org.edec.synchroMine.ctrl.renderer.OrderRenderer;
import org.edec.synchroMine.model.dao.SubjectGroupMineModel;
import org.edec.synchroMine.model.dao.SubjectGroupModel;
import org.edec.synchroMine.model.dao.WorkloadModel;
import org.edec.synchroMine.model.eso.GroupMineModel;
import org.edec.synchroMine.service.GroupSubjectService;
import org.edec.synchroMine.service.OrderService;
import org.edec.synchroMine.service.StudentMineService;
import org.edec.synchroMine.service.impl.GroupSubjectImpl;
import org.edec.synchroMine.service.impl.OrderMineImpl;
import org.edec.synchroMine.service.impl.StudentMineImpl;
import org.edec.utility.component.ctrl.WinStudentSubjectCtrl;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.model.StudentModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.StudentStatus;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


@Log4j
public class IndexPageCtrl extends CabinetSelector {
    @Wire
    private Combobox cmbInst, cmbGroup, cmbFormOfStudy, cmbSem;
    @Wire
    private Datebox dbDateCreated;
    @Wire
    private Hbox hbFilter;
    @Wire
    private Listbox lbOrder, lbStudentMine, lbStudentESO, lbSubjecGroupESO, lbSubjectGroupMine, lbGroupMine, lbGroupESO;
    @Wire
    private Vbox vbInst, vbFormOfStudy;
    @Wire
    private Tab tabGroup;

    private ComponentService componentService = new ComponentServiceESOimpl();
    private GroupSubjectService groupSubjectService = new GroupSubjectImpl();
    private OrderService orderService = new OrderMineImpl();
    private StudentMineService studentService = new StudentMineImpl();

    private IndexPageCtrl indexPageCtrl;
    private List<DepartmentModel> departments;
    private List<StudentModel> studentsEso, studentsMine;
    private List<SubjectGroupModel> subjectsESO, subjectsMine;

    protected void fill() {
        indexPageCtrl = this;
        componentService.fillCmbInst(cmbInst, vbInst, currentModule.getDepartments());
        componentService.fillCmbFormOfStudy(cmbFormOfStudy, vbFormOfStudy, currentModule.getFormofstudy());
        hbFilter.setVisible(vbInst.isVisible() && vbFormOfStudy.isVisible());
        lbOrder.setItemRenderer(new OrderRenderer());
        Events.echoEvent(Events.ON_SELECT, tabGroup, null);
    }

    @Listen("onClick = #btnSearchOrder")
    public void searchOrders() {
        Clients.showBusy(lbOrder, "Загрузка данных");
        Events.echoEvent("onLater", lbOrder, null);
    }

    @Listen("onLater = #lbOrder")
    public void onLaterLbOrder() {
        lbOrder.setModel(new ListModelList<>(orderService.getOrderByInst(
                ((InstituteModel) cmbInst.getSelectedItem().getValue()).getIdInstMine(),
                dbDateCreated.getValue()
        )));
        lbOrder.renderAll();
        Clients.clearBusy(lbOrder);
    }

    @Listen("onClick = #lbOrder")
    public void selectedLbOrder() {
        if (lbOrder.getSelectedItem() == null) {
            return;
        }
        Map arg = new HashMap();
        arg.put(WinOrderInfoCtrl.ORDER, lbOrder.getSelectedItem().getValue());
        arg.put(WinOrderInfoCtrl.INST, cmbInst.getSelectedItem().getValue());

        ComponentHelper.createWindow("/synchroMine/winOrderInfo.zul", "winOrderInfo", arg).doModal();
    }

    @Listen("onSelect = #tabGroup; onChange = #cmbInst; onChange = #cmbFormOfStudy;")
    public void selectedTabStudent() {

        if (cmbInst.getSelectedIndex() == -1 || cmbFormOfStudy.getSelectedItem() == null || ((FormOfStudy) cmbFormOfStudy.getSelectedItem().getValue()).getType() == 3) {
            Clients.showNotification("Выберите институт и форму обучения", cmbFormOfStudy);
            cmbFormOfStudy.focus();
            return;
        }
        if (departments == null) {
            departments = groupSubjectService.getDepartments();
        }
        Clients.showBusy(cmbSem, "Обновление");
        Events.echoEvent("onLater", cmbSem, null);
    }

    @Listen("onLater = #cmbSem")
    public void onLaterCmbSem() {
        cmbSem.setModel(new ListModelList<>(groupSubjectService.getSemesters(
                ((InstituteModel) cmbInst.getSelectedItem().getValue()).getIdInst(),
                ((FormOfStudy) cmbFormOfStudy.getSelectedItem().getValue()).getType()
        )));
        Clients.clearBusy(cmbSem);
        Events.echoEvent("onAfterRender", cmbSem, null);
        Events.echoEvent("onChange", cmbSem, null);
    }

    @Listen("onClick = #btnCreateAllStudents")
    public void createAllStudentsByGroup() {
        if (cmbGroup.getSelectedIndex() == -1) {
            PopupUtil.showWarning("Сначала выберите группу");
            return;
        }
        GroupMineModel selectedGroup = cmbGroup.getSelectedItem().getValue();
        for (StudentModel student : studentsMine) {
            if (student.getStatus() == 3 || student.getOtherStudentModel() != null) {
                continue;
            }
            Long idStudentCard = null;
            List<StudentModel> studentsByFilter = studentService.getStudentsByFilter(student.getFio(), student.getRecordbook(), student.getIdStudCardMine());
            for (StudentModel studentModel : studentsByFilter) {
                if (student.getRecordbook().equals(studentModel.getRecordbook())) {
                    idStudentCard = studentModel.getIdStudCard();
                    break;
                }
            }
            if (idStudentCard == null && studentsByFilter.size() != 0) {
                continue;
            }
            if (idStudentCard == null) {
                idStudentCard = studentService.createStudent(student.getFamily(), student.getName(), student.getPatronymic(), student.getBirthday(), student.getRecordbook(),
                        student.getSex(), student.getIdStudCardMine(), null);
            }
            student.setIdStudCard(idStudentCard);
            int trustAgreement = student.getCondOfEducation() == 2 ? 1 : 0;
            int governmentFinanced = (student.getCondOfEducation() == 1 || student.getCondOfEducation() == 2) ? 1 : 0;
            studentService.createSSSforStudent(student.getIdStudCard(), trustAgreement, governmentFinanced, student.getStatus() == -1 ? 1 : 0, null, selectedGroup.getGroupname());
            studentService.createSRforStudent(student.getIdStudCard(), null, selectedGroup.getGroupname());
        }
        fillStudents();
    }

    @Listen("onClick = #btnDeleteEsoStudents")
    public void deleteEsoStudents() {
        for (StudentModel student : studentsEso) {
            if (student.getOtherStudentModel() == null) {
                studentService.deleteSSS(student.getIdSSS());
            }
        }
        fillStudents();
    }

    @Listen("onClick = #btnUpdateAllStudent")
    public void updateAllStudents() {
        List<StudentModel> students = new ArrayList<>();
        for (Listitem li : lbStudentMine.getItems()) {
            StudentModel student = li.getValue();
            if (student.getStatus() == 3 || student.getOtherStudentModel() != null) {
                continue;
            }
            students.add(student);
        }

        Map arg = new HashMap();
        arg.put(WinCreateStudentCtrl.SELECTED_STUDENTS, students);
        arg.put(WinCreateStudentCtrl.INDEX_PAGE_CTRL, indexPageCtrl);
        arg.put(WinCreateStudentCtrl.SELECTED_GROUP, cmbGroup.getSelectedItem().getLabel());

        ComponentHelper.createWindow("/synchroMine/winCreateStudent.zul", "winCreateStudent", arg).doModal();
    }

    @Listen("onAfterRender = #cmbSem")
    public void afterRender() {
        cmbSem.setSelectedIndex(0);
    }

    @Listen("onChange = #cmbSem")
    public void changeSem() {
        SemesterModel selectedSemester = cmbSem.getSelectedItem().getValue();
        InstituteModel selectedInst = cmbInst.getSelectedItem().getValue();
        cmbGroup.setModel(new ListModelList<>(groupSubjectService.getGroupsBySem(selectedSemester.getIdSem())));
        String year = DateConverter.convertDateToYearString(selectedSemester.getDateOfBegin()) + "-" + DateConverter.convertDateToYearString(selectedSemester.getDateOfEnd());
        Long idInstMine = selectedInst.getIdInstMine();
        Integer formOfStudy = ((FormOfStudy) cmbFormOfStudy.getSelectedItem().getValue()).getType();
        List<GroupMineModel> groupsMine = groupSubjectService.getGroupsByInstMineAndYear(idInstMine, year, formOfStudy == 1 ? "1" : "2,3");
        List<GroupMineModel> groupsESO = groupSubjectService.getGroupsBySem(selectedSemester.getIdSem());
        groupSubjectService.fillGroups(groupsESO, groupsMine);

        lbGroupMine.setItemRenderer(new GroupRenderer(selectedInst.getIdInst(), selectedSemester));
        lbGroupESO.setItemRenderer(new GroupRenderer(selectedInst.getIdInst(), selectedSemester));

        lbGroupMine.setModel(new ListModelList<>(groupsMine));
        lbGroupMine.renderAll();
        lbGroupESO.setModel(new ListModelList<>(groupsESO));
        lbGroupESO.renderAll();
    }

    @Listen("onChange = #cmbGroup")
    public void changeGroup() {
        fillStudents();
        fillGroups();
    }

    @Listen("onClick = #btnSynchroGroupSubject")
    public void synchroGroupSubject() {
        GroupMineModel selectedGroup = cmbGroup.getSelectedItem().getValue();
        groupSubjectService.createSubjects(subjectsMine, selectedGroup.getIdLGS());
        fillGroups();
    }

    @Listen("onClick = #btnGetNotSynchSubject")
    public void getNotSynchSubjects() {
        if (subjectsESO.size() == 0) {
            Messagebox.show("Нужны дисциплины из нашей системы для сравнения!", "Предупреждение", Messagebox.OK, Messagebox.EXCLAMATION);
            return;
        }
        Map arg = new HashMap<>();

        arg.put(WinAddNotSynchSubjectCtrl.SELECTED_INST, cmbInst.getSelectedItem().getValue());
        arg.put(WinAddNotSynchSubjectCtrl.SELECTED_GROUP, cmbGroup.getSelectedItem().getValue());
        arg.put(WinAddNotSynchSubjectCtrl.ALL_DEPARTMENTS, departments);
        arg.put(WinAddNotSynchSubjectCtrl.SUBJECTS_DEC, subjectsESO);
        arg.put(WinAddNotSynchSubjectCtrl.SUBJECTS_MINE, subjectsMine);

        Window winAddNotSynchSubject = ComponentHelper.createWindow("/synchroMine/winAddNotSynchSubject.zul", "winAddNotSynchSubject", arg);
        winAddNotSynchSubject.addEventListener("onLater", event -> fillGroups());
        winAddNotSynchSubject.doModal();
    }

    @Listen("onClick = #btnSearchNotSynchAllGroupSubject")
    public void synchAllNotSynchSubjectGroup() throws IOException {
        SemesterModel selectedSemester = cmbSem.getSelectedItem().getValue();
        String path = "C://1.txt";
        FileWriter fileWriter = new FileWriter(path);

        for (GroupMineModel group : groupSubjectService.getGroupsBySem(selectedSemester.getIdSem())) {
            List<SubjectGroupModel> subjectsDEC = groupSubjectService.getSubjectGroupESO(group.getIdLGS());
            List<SubjectGroupModel> subjectsMine = groupSubjectService.getSubjectGroupMine(
                    group.getGroupname(), ((InstituteModel) cmbInst.getSelectedItem().getValue()).getIdInstMine(),
                    group.getSemester(), group.getCourse());
            if (subjectsMine.size() == 0 || subjectsDEC.size() == 0) {

                fileWriter.write(group.getGroupname() + "\n");
                if (subjectsMine.size() == 0) {
                    fileWriter.write("\t Дисциплин в шахтах не нашлось\n");
                }
                if (subjectsDEC.size() == 0) {
                    fileWriter.write("\t Дисциплин в АСУ ИКИТ не нашлось\n");
                }
                continue;
            }

            Set<Long> subjects = new HashSet<>();
            subjectsDEC.forEach(subjectGroupModel -> subjects.add(subjectGroupModel.getIdSubjMine()));

            List<SubjectGroupMineModel> subjectsFromCurriculumNotSynched = groupSubjectService.getSubjectMineModel(
                    ((InstituteModel) cmbInst.getSelectedItem().getValue()).getIdInstMine(),
                    group.getSemester(),
                    group.getCourse(),
                    group.getGroupname(),
                    subjects
            );

            Set<Long> workloadsList = new HashSet<>();

            subjectsMine.forEach(subjectGroupModel -> workloadsList.addAll(subjectGroupModel.getWorkLoads()));

            List<WorkloadModel> workloads = groupSubjectService.getWorkloadByGroup(
                    ((InstituteModel) cmbInst.getSelectedItem().getValue()).getIdInstMine(),
                    group.getCourse(),
                    group.getGroupname(),
                    workloadsList
            );

            if (subjectsFromCurriculumNotSynched.size() > 0 || workloads.size() > 0) {
                fileWriter.write(group.getGroupname() + "\n");
                if (subjectsFromCurriculumNotSynched.size() > 0) {
                    fileWriter.write("\tДисциплины из УП:" + "\n");
                    for (SubjectGroupMineModel subjectCurr : subjectsFromCurriculumNotSynched) {
                        fileWriter.write("\t\t" + subjectCurr.getSubjectname() + ": "
                                + subjectCurr.printAllFocForSubject() + "\n");
                    }
                }
                if (workloads.size() > 0) {
                    fileWriter.write("\tДисциплины из нагрузки:" + "\n");
                    for (WorkloadModel workload : workloads) {
                        fileWriter.write("\t\t" + workload.getSubjectname() + ": " + workload.getCourse() + " курс, " +
                                workload.getSemester() + " семестр. " + workload.getFamily() + " "
                                + workload.getName() + " " + workload.getPatronymic() + "\n");
                    }
                }
            }
        }
        fileWriter.close();
    }

    @Listen("onClick = #btnSynchAllGroupSubjectByHours")
    public void synchAllGroupSubjectByHours() {
        SemesterModel selectedSemester = cmbSem.getSelectedItem().getValue();
        for (GroupMineModel group : groupSubjectService.getGroupsBySem(selectedSemester.getIdSem())) {
            List<SubjectGroupModel> subjectsDEC = groupSubjectService.getSubjectGroupESO(group.getIdLGS());
            List<SubjectGroupModel> subjectsMine = groupSubjectService.getSubjectGroupMine(
                    group.getGroupname(), ((InstituteModel) cmbInst.getSelectedItem().getValue()).getIdInstMine(),
                    group.getSemester(), group.getCourse());
            groupSubjectService.fillGroupsSubjects(subjectsDEC, subjectsMine, departments);
            if (subjectsMine.size() == 0 || subjectsDEC.size() == 0) {
                continue;
            }
            for (SubjectGroupModel subjectGroupDec : subjectsDEC) {
                SubjectGroupModel otherSubject = subjectGroupDec.getOtherSubject();
                if (otherSubject == null || Objects.equals(subjectGroupDec.getHoursCount(), otherSubject.getHoursCount())
                        || !groupSubjectService.getRegisterNumberByLGSandSubj(group.getIdLGS(), subjectGroupDec.getIdSubj()).isEmpty()) {
                    continue;
                }
                /*if (otherSubject == null || otherSubject.getFacultative() == null
                        || subjectGroupDec.getFacultative() == otherSubject.getFacultative()) {
                    continue;
                }*/
                /*if (groupSubjectService.updateSubjectFacultative(subjectGroupDec.getIdSubj(), otherSubject.getFacultative())) {
                    log.info("Обновлен subject " + subjectGroupDec.getSubjectname() + ", " + otherSubject.getFacultative());
                }*/
                if (groupSubjectService.updateSubject(subjectGroupDec, otherSubject)) {
                    log.info("Обновлен subject " + subjectGroupDec.getSubjectname() + ", " + otherSubject.getHoursCount());
                }
            }
        }
    }

    private void fillGroups() {
        GroupMineModel selectedGroup = cmbGroup.getSelectedItem().getValue();
        lbSubjecGroupESO.setItemRenderer(new GroupSubjecRenderer(selectedGroup.getIdLGS(), this::fillGroups));
        lbSubjectGroupMine.setItemRenderer(new GroupSubjecRenderer(selectedGroup.getIdLGS(), this::fillGroups));
        subjectsESO = groupSubjectService.getSubjectGroupESO(selectedGroup.getIdLGS());
        subjectsMine = groupSubjectService.getSubjectGroupMine(
                selectedGroup.getGroupname(), ((InstituteModel) cmbInst.getSelectedItem().getValue()).getIdInstMine(),
                selectedGroup.getSemester(), selectedGroup.getCourse());
        groupSubjectService.fillGroupsSubjects(subjectsESO, subjectsMine, departments);
        lbSubjectGroupMine.setModel(new ListModelList<>(subjectsMine));
        lbSubjectGroupMine.renderAll();
        lbSubjecGroupESO.setModel(new ListModelList<>(subjectsESO));
        lbSubjecGroupESO.renderAll();
    }

    public void fillStudents() {
        lbStudentESO.getItems().clear();
        lbStudentMine.getItems().clear();
        GroupMineModel selectedGroup = cmbGroup.getSelectedItem().getValue();
        studentsMine = studentService.getStudentsMineByGroupname(selectedGroup.getGroupname());
        studentsEso = studentService.getStudentsEsoByGroupInSem(selectedGroup.getIdLGS());

        for (StudentModel studentModelMine : studentsMine) {
            studentModelMine.setGroupname(cmbGroup.getSelectedItem().getLabel());
            for (StudentModel studentModelEso : studentsEso) {
                if (studentModelEso.getOtherStudentModel() != null) {
                    continue;
                }
                if (studentModelMine.getIdStudCardMine().equals(studentModelEso.getIdStudCardMine())) {
                    studentModelEso.setOtherStudentModel(studentModelMine);
                    studentModelMine.setOtherStudentModel(studentModelEso);
                    break;
                } else if (studentModelMine.getFio().equals(studentModelEso.getFio())) {
                    studentModelEso.setOtherStudentModel(studentModelMine);
                    studentModelMine.setOtherStudentModel(studentModelEso);
                    break;
                }
            }
        }

        createListboxStudent(lbStudentMine, studentsMine, true);
        createListboxStudent(lbStudentESO, studentsEso, false);
    }

    private void createListboxStudent(Listbox listbox, List<StudentModel> studentModels, boolean mine) {
        for (final StudentModel studentModel : studentModels) {
            final Listitem li = new Listitem();
            li.setParent(listbox);
            li.setValue(studentModel);

            StudentModel otherStudentModel = studentModel.getOtherStudentModel();

            Listcell lcPopupFio = new Listcell(studentModel.getFio());
            lcPopupFio.setParent(li);

            if (otherStudentModel != null) {
                if (studentModel.getIdStudCardMine() != null && otherStudentModel.getIdStudCardMine() != null
                        && studentModel.getIdStudCardMine().equals(otherStudentModel.getIdStudCardMine())) {
                    if (studentModel.getRecordbook().equals(otherStudentModel.getRecordbook())) {
                        li.setStyle("background: #99ff99;");
                    } else {
                        li.setStyle("background: #FFFE7E;");
                        final Popup popup = new Popup();
                        popup.setParent(lcPopupFio);
                        popup.setId("popup" + studentModel.getFio() + listbox.getUuid());

                        Vbox vb = new Vbox();
                        vb.setParent(popup);

                        new Label("Зачетные книжки: " + studentModel.getRecordbook() + ":" + otherStudentModel.getRecordbook()).setParent(vb);
                        new Label("Идентификаторы: " + studentModel.getIdStudCardMine() + ":" + otherStudentModel.getIdStudCardMine()).setParent(vb);
                        li.setPopup("popup" + studentModel.getFio() + listbox.getUuid());
                    }
                } else {
                    li.setStyle("background: red;");

                    final Popup popup = new Popup();
                    popup.setParent(lcPopupFio);
                    popup.setId("popup" + studentModel.getFio() + listbox.getUuid());

                    Vbox vb = new Vbox();
                    vb.setParent(popup);

                    new Label("Зачетные книжки: " + studentModel.getRecordbook() + ":" + otherStudentModel.getRecordbook()).setParent(vb);
                    new Label("Идентификаторы: " + studentModel.getIdStudCardMine() + ":" + otherStudentModel.getIdStudCardMine()).setParent(vb);
                    if (studentModel.getIdStudCard() != null) {
                        Button btnSynch = new Button("Синхронизировать");
                        btnSynch.setParent(vb);
                        btnSynch.addEventListener(Events.ON_CLICK, event -> {
                            studentService.updateStudentCardFromMine(studentModel.getIdStudCard(),
                                    otherStudentModel.getIdStudCardMine(), otherStudentModel.getRecordbook());
                            fillStudents();
                        });
                    }
                    li.setPopup("popup" + studentModel.getFio() + listbox.getUuid());
                }
            } else {
                if (mine) {
                    /*li.addEventListener(Events.ON_CLICK, event -> {
                            Map arg = new HashMap();

                            List<StudentStatusModel> students = new ArrayList<>();
                            students.add((StudentStatusModel) li.getValue());

                            arg.put(WinCreateStudentCtrl.SELECTED_STUDENTS, students);
                            arg.put(WinCreateStudentCtrl.INDEX_PAGE_CTRL, indexPageCtrl);
                            arg.put(WinCreateStudentCtrl.SELECTED_GROUP, cmbGroup.getSelectedItem().getLabel());
                            ComponentHelper.createWindow("/synchroMine/winCreateStudent.zul", "winCreateStudent", arg).doModal();
                    });*/
                } else {
                    li.addEventListener(Events.ON_CLICK, event -> {
                        Map arg = new HashMap();
                        arg.put(WinStudentSubjectCtrl.STUDENT_MODEL, studentModel);

                        ComponentHelper.createWindow("/utility/component/winStudentSubject.zul", "winStudentSubject", arg).doModal();
                    });
                }
            }
            new Listcell(StudentStatus.getStatusByValue(studentModel.getStatus()).getName()).setParent(li);
        }
    }
}