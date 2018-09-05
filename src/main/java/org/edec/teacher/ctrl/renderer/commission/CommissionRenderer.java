package org.edec.teacher.ctrl.renderer.commission;

import org.edec.teacher.ctrl.WinCommissionCtrl;
import org.edec.teacher.model.commission.CommissionModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.HashMap;
import java.util.Map;


public class CommissionRenderer implements ListitemRenderer<CommissionModel> {
    private ComponentService componentService = new ComponentServiceESOimpl();
    private Runnable updateIndex;

    public CommissionRenderer (Runnable updateIndex) {
        this.updateIndex = updateIndex;
    }

    @Override
    public void render (Listitem li, final CommissionModel data, int index) throws Exception {
        li.setValue(data);
        componentService.createListcell(li, String.valueOf(index + 1), "color: #000;", "", "");
        componentService.createListcell(li, data.getSubjectName(), "color: #000;", "", "");
        componentService.createListcell(li, FormOfControlConst.getName(data.getFormOfControl()).getName(), "color: #000;", "", "");

        Listcell lcBtn = new Listcell();
        lcBtn.setParent(li);
        lcBtn.setStyle("text-align: center;");
        Button btn = new Button("", "/imgs/docs.png");
        btn.setParent(lcBtn);
        btn.setTooltiptext("Просмотреть ведомость");
        btn.setHoverImage("/imgs/docsCLR.png");
        btn.addEventListener(Events.ON_CLICK, event -> {
            Map arg = new HashMap();
            arg.put(WinCommissionCtrl.SELECTED_COMMISSION, data);
            arg.put(WinCommissionCtrl.UPDATE_INDEX, updateIndex);

            ComponentHelper.createWindow("/teacher/winCommissionRegister.zul", "winCommissionRegister", arg).doModal();
        });

        li.setStyle("background: #" + (data.isSigned() ? "99FF99" : "FFF"));
    }
}
