package org.edec.studyLoad.ctrl.renderer;


import org.edec.studyLoad.model.AssignmentModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class AssignmentRenderer implements ListitemRenderer<AssignmentModel> {

    public void render(Listitem listitem, AssignmentModel assignmentModel, int i) throws Exception {
        listitem.appendChild(new Listcell(String.valueOf(i+1)));
        listitem.appendChild(new Listcell(assignmentModel.getFioWithShortInitials()));
        listitem.appendChild(new Listcell(assignmentModel.getNameDiscipline()));
        listitem.appendChild(new Listcell(assignmentModel.getTypeInstructionString()));
        listitem.appendChild(new Listcell(assignmentModel.getGroupName()));
        listitem.appendChild(new Listcell(assignmentModel.getTypeControl()));
        listitem.appendChild(new Listcell(String.valueOf(assignmentModel.getCourse())));
        listitem.appendChild(new Listcell(String.valueOf(assignmentModel.getHourSaudCount())));
        listitem.appendChild(new Listcell(String.valueOf(assignmentModel.getHoursCount())));
        listitem.setValue(assignmentModel);
    }
}
