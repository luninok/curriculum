package org.edec.passportGroup.ctrl;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.passportGroup.model.GroupModel;
import org.edec.passportGroup.model.StudentModel;
import org.edec.passportGroup.model.SubjectModel;
import org.edec.passportGroup.service.PassportGroupService;
import org.edec.passportGroup.service.impl.PassportGroupServiceESO;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.zkoss.zk.ui.Executions.getCurrent;

public class WinOpenPersonalRetakeCtrl extends SelectorComposer<Component> {

    public static final String SUBJECT = "subject";
    public static final String STUDENTS = "students";
    public static final String GROUP = "group";
    public static final String RUNNABLE_UPDATE = "runnable_update";

    @Wire
    private Datebox dateOfBeginRetake, dateOfEndRetake;

    @Wire
    private Button openRetake;

    @Wire
    private Window winOpenPersonalRetake;

    private TemplatePageCtrl template = new TemplatePageCtrl();

    private SubjectModel subject;
    private List<StudentModel> studentsList = new ArrayList<>();
    private GroupModel group;

    private Runnable updateGroupReport;

    private PassportGroupService service = new PassportGroupServiceESO();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        template.checkModuleByRole(getCurrent().getDesktop().getRequestPath(), getPage());

        subject = (SubjectModel) Executions.getCurrent().getArg().get(SUBJECT);
        studentsList = (List<StudentModel>) Executions.getCurrent().getArg().get(STUDENTS);
        group = (GroupModel) Executions.getCurrent().getArg().get(GROUP);
        updateGroupReport = (Runnable) Executions.getCurrent().getArg().get(RUNNABLE_UPDATE);

        setCalendarDate();
    }

    public void setCalendarDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 3);

        dateOfBeginRetake.setValue(new Date());
        dateOfBeginRetake.setValue(new Date());
        dateOfEndRetake.setValue(cal.getTime());
        dateOfEndRetake.setValue(cal.getTime());
    }

    @Listen("onClick = #btnOpenRetake")
    public void openRetake() {
        if (dateOfBeginRetake.getValue() == null) {
            PopupUtil.showWarning("Не заполнены даты пересдач!");
            return;
        }

        if (dateOfBeginRetake.getValue().after(dateOfEndRetake.getValue())) {
            PopupUtil.showWarning("Дата начала комиссии не может назначаться позже даты конца комиссии!");
            return;
        }

        if (dateOfEndRetake.getValue().before(new Date())) {
            Messagebox.show("Открытые ведомости будут просроченными. Вы уверены?", "Внимание!", Messagebox.YES | Messagebox.NO,
                            Messagebox.QUESTION, event -> {
                        if (event.getName().equals(Messagebox.ON_YES)) {
                            if (!service.openRetake(subject, studentsList, group, dateOfBeginRetake.getValue(), dateOfEndRetake.getValue(),
                                                    template.getCurrentUser().getFio())) {
                                PopupUtil.showError("Открыть ведомость не удалось!");
                            } else {
                                PopupUtil.showInfo("Открытие ведомости прошло успешно!");
                            }
                            updateGroupReport.run();

                            winOpenPersonalRetake.detach();
                        }
                    }
            );

            return;
        }

        if (!service.openRetake(subject, studentsList, group, dateOfBeginRetake.getValue(), dateOfEndRetake.getValue(),
                                template.getCurrentUser().getFio()
        )) {
            PopupUtil.showError("Открыть ведомость не удалось!");
        } else {
            PopupUtil.showInfo("Открытие ведомости прошло успешно!");
        }

        updateGroupReport.run();

        winOpenPersonalRetake.detach();
    }
}

