package org.edec.studyLoad.ctrl.renderer;


import org.edec.studyLoad.model.EmploymentModel;
import org.edec.studyLoad.model.PositionModel;
import org.edec.studyLoad.service.StudyLoadService;
import org.edec.studyLoad.service.impl.StudyLoadServiceImpl;
import org.zkoss.zul.*;

import java.util.List;

public class EmploymentRenderer implements ListitemRenderer<EmploymentModel> {
    private StudyLoadService studyLoadService = new StudyLoadServiceImpl();
    public void render(Listitem listitem, EmploymentModel employmentModel, int i) throws Exception {
        new Listcell(employmentModel.getShorttitle()).setParent(listitem);
        Listcell byworkerCell = new Listcell();
        Combobox byworkerCombobox = new Combobox(employmentModel.getByworker());
        List<String> listByworker = studyLoadService.getByworker();
        byworkerCombobox.setId("cmbByWorker");
        for (String byworker : listByworker) {
            Comboitem comboitem = new Comboitem();
            comboitem.setLabel(byworker);
            byworkerCombobox.getItems().add(comboitem);
        }
        byworkerCell.appendChild(byworkerCombobox);
        byworkerCell.setParent(listitem);
        Listcell rolenameCell = new Listcell();
        Combobox rolenameCombobox = new Combobox(employmentModel.getRolename());
        rolenameCombobox.setId("cmbPosition");
        List<PositionModel> listPosition = studyLoadService.getPositions();
        for (PositionModel position : listPosition) {
            Comboitem comboitem = new Comboitem();
            comboitem.setLabel(position.getPositionName());
            rolenameCombobox.getItems().add(comboitem);
        }
        rolenameCell.appendChild(rolenameCombobox);
        rolenameCell.setParent(listitem);
        Listcell wagerateCell = new Listcell();
        wagerateCell.appendChild(new Doublebox(employmentModel.getWagerate()));
        wagerateCell.setParent(listitem);
        Listcell timeWagerateCell = new Listcell();
        timeWagerateCell.appendChild(new Doublebox(employmentModel.getTime_wagerate()));
        timeWagerateCell.setParent(listitem);
        listitem.setValue(employmentModel);
    }
}
