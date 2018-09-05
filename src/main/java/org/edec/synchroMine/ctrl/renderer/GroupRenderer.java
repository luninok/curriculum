package org.edec.synchroMine.ctrl.renderer;

import org.edec.model.SemesterModel;
import org.edec.synchroMine.model.eso.GroupMineModel;
import org.edec.synchroMine.service.GroupSubjectService;
import org.edec.synchroMine.service.impl.GroupSubjectImpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GroupRenderer implements ListitemRenderer<GroupMineModel> {
    private GroupSubjectService groupSubjectService = new GroupSubjectImpl();

    private Long idInst;
    private SemesterModel selectedSemester;

    private final Pattern pattern = Pattern.compile("((\\d{2,}\\.)+(\\d{2,})?){1}(.)*");

    public GroupRenderer (Long idInst, SemesterModel selectedSemester) {
        this.idInst = idInst;
        this.selectedSemester = selectedSemester;
    }

    @Override
    public void render (Listitem li, GroupMineModel data, int index) throws Exception {
        li.setValue(data);
        Listcell lcGroupname = new Listcell(data.getGroupname());
        lcGroupname.setParent(li);
        new Listcell(String.valueOf(data.getCourse())).setParent(li);
        new Listcell(data.getIdGroupMine() == null ? "" : String.valueOf(data.getIdGroupMine())).setParent(li);

        if (data.getOtherGroup() != null) {
            GroupMineModel otherGroup = data.getOtherGroup();
            if (data.getIdGroupMine() != null && data.getIdGroupMine().equals(otherGroup.getIdGroupMine())) {
                li.setStyle("background: #99ff99;");
            } else {
                li.setStyle("background: #FFFE7E;");
                //Группа из АСУ ИКИТ
                if (data.getIdLGS() != null) {
                    Popup popup = new Popup();
                    popup.setParent(lcGroupname);
                    popup.setId("popup" + data.getIdLGS() + li.getListbox().getUuid());
                    li.setPopup("popup" + data.getIdLGS() + li.getListbox().getUuid());

                    Vbox vb = new Vbox();
                    vb.setParent(popup);

                    new Label("Название поменяется с " + data.getGroupname() + " на " + otherGroup.getGroupname()).setParent(vb);
                    new Label("ИД группы поменяется с " + String.valueOf(data.getIdGroupMine()) + " на " +
                              otherGroup.getIdGroupMine()).setParent(vb);
                    new Label("Курс поменяется с " + data.getCourse() + " на " + otherGroup.getCourse()).setParent(vb);

                    Button btnAddGroup = new Button("Изменить группу группу");
                    btnAddGroup.setParent(vb);
                    btnAddGroup.addEventListener(Events.ON_CLICK, event -> {
                        if (groupSubjectService.updateGroup(data.getIdLGS(), otherGroup.getIdGroupMine())) {
                            PopupUtil.showInfo("Группа успешно поменяла свои свойства!");
                        }
                    });
                }
            }
        } else {
            li.setStyle("background: #FF7373;");
            //Группа из шахт
            if (data.getIdLGS() == null) {
                Popup popup = new Popup();
                popup.setParent(lcGroupname);
                popup.setId("popup" + data.getIdGroupMine() + li.getListbox().getUuid());
                li.setPopup("popup" + data.getIdGroupMine() + li.getListbox().getUuid());

                Vbox vb = new Vbox();
                vb.setParent(popup);

                new Label("Название: " + data.getGroupname()).setParent(vb);
                new Label("ИД группы: " + String.valueOf(data.getIdGroupMine())).setParent(vb);
                new Label("Курс: " + data.getCourse()).setParent(vb);
                new Label("Специальность: " + data.getDirectionCode() + ", " + data.getSpecialityTitle()).setParent(vb);
                if (data.getDirectionTitle() != null) {
                    Matcher m = pattern.matcher(data.getDirectionTitle());
                    if (m.find()) {
                        new Label("Профиль (код): '" + data.getDirectionTitle().substring(0, m.end(1)) + "'").setParent(vb);
                        new Label("Профиль (название): '" + data.getDirectionTitle().substring(m.end(1)).trim() + "'").setParent(vb);
                    }
                }
                new Label("Период обучения: " + data.getPeriodOfStudy()).setParent(vb);
                new Label("Название файла: " + data.getPlanfileName()).setParent(vb);

                Button btnAddGroup = new Button("Добавить группу");
                btnAddGroup.setParent(vb);
                btnAddGroup.addEventListener(Events.ON_CLICK, event -> {
                    groupSubjectService.createGroup(data, idInst, selectedSemester);
                });
            }
        }
    }
}
