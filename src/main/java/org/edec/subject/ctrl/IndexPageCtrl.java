package org.edec.subject.ctrl;

import org.apache.log4j.Logger;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.DepartmentModel;
import org.edec.main.model.ModuleModel;
import org.edec.subject.ctrl.renderer.SemesterRenderer;
import org.edec.subject.ctrl.renderer.SubjectListRenderer;
import org.edec.subject.model.SubjectModel;
import org.edec.subject.service.SubjectService;
import org.edec.subject.service.impl.SubjectServiceImpl;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.*;

import static org.zkoss.zk.ui.Executions.getCurrent;

public class IndexPageCtrl extends SelectorComposer<Component> {

    private final Logger log = Logger.getLogger(WinTeachersCtrl.class.getName());

    SubjectService service = new SubjectServiceImpl();

    private ComponentService componentService = new ComponentServiceESOimpl();

    @Wire
    private Combobox cmbDepartment, cmbSem, cmbFos;

    @Wire
    private Vbox vbDepartment, vbFos;

    @Wire
    private Listbox listSubject;

    @Wire
    private Textbox tbSubject, tbGroup;

    @Wire
    private Vlayout teacherVlayout;

    private int index;

    private TemplatePageCtrl template = new TemplatePageCtrl();
    private ModuleModel currentModule;

    private Runnable updateSubjects = this::fillSubjects;
    private List<SubjectModel> subjects;
    private List<SubjectModel> listFilteredSubjects;

    private Long[] idLesg;
    private Runnable updateTeachers = this::fillTeachers;

    private boolean isFirstRender;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        isFirstRender = true;

        template.checkModuleByRole(getCurrent().getDesktop().getRequestPath(), getPage());
        currentModule = template.getCurrentModule();

        componentService.fillCmbDepartment(cmbDepartment, vbDepartment, currentModule.getDepartments());
        componentService.fillCmbFormOfStudy(cmbFos, vbFos, FormOfStudy.ALL.getType());
        cmbFos.setSelectedIndex(-1);

        listSubject.addEventListener(Events.ON_SELECT, event -> {
            index = listSubject.getSelectedItem().getIndex();
            fillTeachers();
        });
    }

    @Listen("onChange = #cmbDepartment")
    public void clearCmb() {
        cmbSem.setSelectedIndex(-1);
        cmbFos.setSelectedIndex(-1);
        teacherVlayout.getChildren().clear();
    }

    @Listen("onChange = #cmbFos")
    public void fillCmbSemester() {
        cmbSem.setSelectedIndex(-1);
        cmbSem.setItemRenderer(new SemesterRenderer());
        cmbSem.setModel(new ListModelList<>(
                service.getSemester(((DepartmentModel) cmbDepartment.getSelectedItem().getValue()).getIdInstitute(),
                                    ((FormOfStudy) cmbFos.getSelectedItem().getValue()).getType(), null)));
        teacherVlayout.getChildren().clear();
    }

    @Listen("onChange = #cmbSem")
    public void changeSem() {
        isFirstRender = true;
        teacherVlayout.getChildren().clear();
        fillSubjects();
    }

    public void fillSubjects() {
        subjects = service.getSubjectsBySem(((DepartmentModel) cmbDepartment.getSelectedItem().getValue()).getIdDepartment(),
                                            ((SemesterModel) cmbSem.getSelectedItem().getValue()).getIdSem());

        listFilteredSubjects = new ArrayList<>(subjects);

        ListModelList<SubjectModel> lmSubjects = new ListModelList<>(listFilteredSubjects);

        listSubject.setItemRenderer(new SubjectListRenderer());
        listSubject.setAttribute("data", listFilteredSubjects);
        listSubject.setModel(lmSubjects);
        listSubject.renderAll();
        listSubject.addEventListener(Events.ON_SELECT, event -> fillTeachers());

        try {
            if (!isFirstRender) {
                listSubject.setSelectedIndex(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        isFirstRender = false;
    }

    @Listen("onOK = #tbGroup; onOK = #tbSubject")
    public void filterSubjectList() {
        listFilteredSubjects = service.filterSubjects(subjects, tbSubject.getText(), tbGroup.getText());

        ListModelList<SubjectModel> lmSubjects = new ListModelList<>(listFilteredSubjects);

        listSubject.setItemRenderer(new SubjectListRenderer());
        listSubject.setAttribute("data", listFilteredSubjects);
        listSubject.setModel(lmSubjects);
        listSubject.renderAll();
        listSubject.addEventListener(Events.ON_SELECT, event -> fillTeachers());
    }

    private void fillTeachers() {
        teacherVlayout.getChildren().clear();
        idLesg = new Long[listFilteredSubjects.get(index).getTeachers().size()];
        if (listFilteredSubjects.get(index).getTeachers().size() > 0) {
            for (int i = 0; i < listFilteredSubjects.get(index).getTeachers().size(); i++) {
                idLesg[i] = listFilteredSubjects.get(index).getTeachers().get(i).getIdLesg();
                Vbox teacherView = new Vbox();
                teacherView.setId("view" + i);
                teacherView.setWidth("100%");
                teacherView
                        .setStyle("background: #AFB9C3;" + "padding: 8px;" + "margin-left: 8px;" + "border-radius: 10px;" + "right: -5px");

                Label lFIO = new Label(listFilteredSubjects.get(index).getTeachers().get(i).getFullName());

                for (int j = 0; j < listFilteredSubjects.get(index).getTeachers().get(i).getDepTitles().size(); j++) {
                    Label lDepList = new Label(listFilteredSubjects.get(index).getTeachers().get(i).getDepTitles().get(j));
                    teacherView.appendChild(lDepList);
                    if (j > 0) {
                        teacherView.setHeight(50 + (i * 10) + "px");
                    }
                }

                Button btnDetach = new Button("Открепить");
                btnDetach.setStyle("float: right;" + "margin-right: 8px");
                btnDetach.addEventListener(Events.ON_CLICK, event -> {
                    try {
                        for (int i1 = 0; i1 < listFilteredSubjects.get(index).getTeachers().size(); i1++) {
                            if ((teacherView.getId().equals("view" + i1))) {
                                if (service.removeTeacherFromSubject(idLesg[i1])) {

                                    log.info("Пользователь " + template.getCurrentUser().getFio() + " успешно открепил преподавателя " +
                                             listFilteredSubjects.get(index).getTeachers().get(i1).getFullName() + " к предмету \'" +
                                             listFilteredSubjects.get(index).getSubjectName() + "\'" + " у группы " +
                                             listFilteredSubjects.get(index).getGroupName());

                                    listFilteredSubjects.get(index).getTeachers().remove(i1);
                                    fillTeachers();
                                    fillSubjects();

                                    break;
                                } else {
                                    PopupUtil.showError("Ошибка");
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                teacherView.appendChild(lFIO);
                teacherView.appendChild(btnDetach);
                teacherVlayout.appendChild(teacherView);
            }
        } else {
            Vbox teacherView = new Vbox();
            Label lNoOne = new Label("Нет прикрепленных преподавателей");
            lNoOne.setStyle(
                    "color: #000000;" + "font-size: 16px;" + "font-weight: 700;" + "font-family: opensans, arial, freesans, sans-serif;" +
                    "font-style: normal;" + "line-height: 20px;");
            teacherView.appendChild(lNoOne);
            teacherView.setAlign("center");
            teacherView.setWidth("100%");
            teacherVlayout.appendChild(teacherView);
        }
    }

    @Listen("onClick = #attach_btn")
    public void attach() {
        if (listSubject.getSelectedItem() != null) {
            Map arg = new HashMap();
            arg.put("subject", listFilteredSubjects.get(listSubject.getSelectedIndex()));
            arg.put("updateSubjects", updateSubjects);
            arg.put("updateTeachers", updateTeachers);
            arg.put("idDep", ((DepartmentModel) cmbDepartment.getSelectedItem().getValue()).getIdDepartment());
            ComponentHelper.createWindow("/subject/winTeachers.zul", "winTeachers", arg).doModal();
        } else {
            Messagebox.show("Выберите предмет", "Ошибка", Messagebox.OK, Messagebox.EXCLAMATION);
        }
    }
}
