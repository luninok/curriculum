package org.edec.workflow.ctrl.renderer;

import org.edec.utility.zk.PopupUtil;
import org.edec.workflow.model.WorkflowModel;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * @author Max Dimukhametoov
 */
public class TaskArchiveConfirmingRenderer implements ListitemRenderer<WorkflowModel> {
    @Override
    public void render(Listitem li, final WorkflowModel data, int index) throws Exception {
        li.setValue(data);
        new Listcell(data.getFio()).setParent(li);
        new Listcell(data.getTimeCreated()).setParent(li);
        new Listcell(data.getTimeComplited()).setParent(li);
        new Listcell(data.getStatusTask()).setParent(li);
        new Listcell(data.getAge()).setParent(li);

        if (data.getReason() != null && !data.getReason().equals("")) {
            li.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    PopupUtil.showError("Причина: " + data.getReason());
                }
            });
        }
    }
}
