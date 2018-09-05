package org.edec.contingentMovement.ctrl.renderer;

import org.edec.contingentMovement.ctrl.IndexPageCtrl;
import org.edec.contingentMovement.ctrl.StudentItemOptionsCtrl;
import org.edec.main.model.ModuleModel;
import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.synchroMine.model.eso.entity.Semester;
import org.edec.utility.component.manager.ComponentManager;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StudentRanderer implements ListitemRenderer<StudentStatusModel> {
    private IndexPageCtrl indexPageCtrl;
    private ModuleModel currentModule;
    private Boolean innerSearch;
    private ComponentManager componentManager = new ComponentManager();
    private List<SemesterModel> semesters = new ArrayList<>();

    public Boolean getInnerSearch () {
        return innerSearch;
    }

    public void setInnerSearch (Boolean innerSearch) {
        this.innerSearch = innerSearch;
    }

    public StudentRanderer (IndexPageCtrl indexPageCtrl, ModuleModel currentModule, Boolean innerSearch) {
        this.indexPageCtrl = indexPageCtrl;
        this.currentModule = currentModule;
        this.innerSearch = innerSearch;
        // TODO: поправить институт
        semesters = componentManager.getSemester(Long.valueOf(1), 3, null);
    }

    public SemesterModel getSemesterNameById(Long idSem){
        SemesterModel semestersFind = semesters.stream()
                .filter(item -> item.getIdSem().equals(idSem)).findAny()
                .orElse(null);
        return semestersFind;
    }

    @Override
    public void render (Listitem li, final StudentStatusModel data, final int index) throws Exception {
        if (!this.innerSearch) {
            if (data.getIdInstitute() == 27) {
                li.setStyle("background-color:#00ff00;");
            }
        }
        li.setValue(data);
        new Listcell(data.getFamily()).setParent(li);
        new Listcell(data.getName()).setParent(li);
        new Listcell(data.getPatronymic()).setParent(li);
        new Listcell(data.getRecordBook()).setParent(li);

        if((data.getCurrentGroupId() == null && data.getGroupId() == null)
                || (data.getCurrentGroupId() != null && data.getCurrentGroupId().equals(data.getGroupId()))
                && !data.getEducationcomplite()) {
            new Listcell(data.getGroupname()+" (текущая)").setParent(li);
        } else {
            new Listcell(data.getGroupname()).setParent(li);
        }
        SemesterModel sm = getSemesterNameById(data.getIdSemester());
        String semesterName = "-";
        if (sm != null) {
            semesterName = DateConverter.convert2dateToString(sm.getDateOfBegin(), sm.getDateOfEnd()) + " " +
                    (sm.getSeason() == 0 ? "осень" : "весна");
        }
        new Listcell(semesterName).setParent(li);
        new Listcell(String.valueOf(data.getCourse())).setParent(li);
        new Listcell("").setParent(li);
        new Listcell("", data.getAcademicLeave() ? "/imgs/okCLR.png" : "").setParent(li);
        new Listcell("", data.getDeducted() ? "/imgs/okCLR.png" : "").setParent(li);
        new Listcell("", data.getEducationcomplite() ? "/imgs/okCLR.png" : "").setParent(li);

        li.addEventListener(Events.ON_RIGHT_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent (Event event) throws Exception {
                // Проерка что студент обучается в текущей группе
                if((data.getCurrentGroupId() == null && data.getGroupId() == null)
                        || (data.getCurrentGroupId() != null && data.getCurrentGroupId().equals(data.getGroupId()))
                        && !data.getEducationcomplite()) {
                    Map arg = new HashMap();
                    arg.put(StudentItemOptionsCtrl.STUDENT_MODEL, data);
                    arg.put(StudentItemOptionsCtrl.MAIN_PAGE, indexPageCtrl);

                    ComponentHelper.createWindow("winStudentItemOptions.zul", "winStudentItemOptions", arg).doModal();
                } else {
                    PopupUtil.showWarning("Студент уже НЕ обучается в данной группе");
                }
            }
        });
    }
}
