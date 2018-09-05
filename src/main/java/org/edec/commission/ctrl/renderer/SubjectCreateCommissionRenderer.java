package org.edec.commission.ctrl.renderer;

import org.edec.commission.ctrl.WinCreateCommission;
import org.edec.commission.ctrl.WinCreateCommissionStudentCtrl;
import org.edec.commission.model.SubjectDebtModel;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmmax
 */
public class SubjectCreateCommissionRenderer implements ListitemRenderer<SubjectDebtModel> {
    WinCreateCommission winCreateCommission;

    public SubjectCreateCommissionRenderer (WinCreateCommission winCreateCommission) {
        this.winCreateCommission = winCreateCommission;
    }

    @Override
    public void render (final Listitem li, SubjectDebtModel data, int index) throws Exception {
        if (li.getValue() == null) {
            li.setValue(data);
        }

        Listcell lc = new Listcell();
        lc.setStyle("width: 30px;");
        lc.setParent(li);

        lc = new Listcell(data.getSubjectname());
        lc.setStyle("color: #000;");
        lc.setParent(li);
        lc = new Listcell(String.valueOf(data.getStudents().size()));
        lc.setStyle("color: #000;");
        lc.setParent(li);
        lc = new Listcell(data.getFocStr());
        lc.setStyle("color: #000;");
        lc.setParent(li);
        lc = new Listcell(data.getFulltitle());
        lc.setStyle("color: #000;");
        lc.setParent(li);
        lc = new Listcell(data.getSemesterStr());
        lc.setStyle("color: #000;");
        lc.setParent(li);
        lc = new Listcell();
        Button btnInfo = new Button("Подробнее");
        btnInfo.addEventListener(Events.ON_CLICK, event -> {
            if (Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winCreateCommissionStudent") != null) {
                Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("winCreateCommissionStudent").detach();
            }

            Map arg = new HashMap();
            arg.put(WinCreateCommissionStudentCtrl.SELECTED_LIST_ITEM_SUBJECT, li);
            arg.put(WinCreateCommissionStudentCtrl.WIN_CREATE_COMMISSION, winCreateCommission);

            Window win = (Window) Executions.createComponents("winCreateCommissionStudent.zul", null, arg);
            win.doModal();
        });
        btnInfo.setParent(lc);
        lc.setParent(li);

        if (data.isSigned()) {
            li.setStyle("background: #99ff99;");
        }
    }
}
