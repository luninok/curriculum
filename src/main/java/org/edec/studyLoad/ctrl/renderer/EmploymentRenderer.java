package org.edec.studyLoad.ctrl.renderer;


import org.edec.studyLoad.model.ByworkerModel;
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
        ////
        Listcell byworkerCell = new Listcell();
        Combobox byworkerCombobox = new Combobox(employmentModel.getByworker());
        List<ByworkerModel> listByworker = studyLoadService.getByworker();
        byworkerCombobox.setId("cmbByWorker");
        for (ByworkerModel byworker : listByworker) {
            Comboitem comboitem = new Comboitem();
            comboitem.setLabel(byworker.getByworker());
            comboitem.setValue(byworker);
            byworkerCombobox.getItems().add(comboitem);
        }
        byworkerCell.appendChild(byworkerCombobox);
        byworkerCell.setParent(listitem);
        ///
        Listcell rolenameCell = new Listcell();
        Combobox rolenameCombobox = new Combobox(employmentModel.getRolename());
        List<PositionModel> listPosition = studyLoadService.getPositions();
        rolenameCombobox.setId("cmbPosition");
        for (PositionModel position : listPosition) {
            Comboitem comboitem = new Comboitem();
            comboitem.setLabel(position.getPositionName());
            comboitem.setValue(position);
            rolenameCombobox.getItems().add(comboitem);
        }
        rolenameCell.appendChild(rolenameCombobox);
        rolenameCell.setParent(listitem);
        ///
        Listcell wagerateCell = new Listcell();
        wagerateCell.appendChild(new Doublebox(employmentModel.getWagerate()));
        wagerateCell.setParent(listitem);
        Listcell timeWagerateCell = new Listcell();
        timeWagerateCell.appendChild(new Doublebox(employmentModel.getTime_wagerate()));
        timeWagerateCell.setParent(listitem);
        Listcell deviationCell = new Listcell();
        deviationCell.appendChild(new Doublebox(employmentModel.getDeviation()));
        deviationCell.setParent(listitem);
        Listcell maximumLoadCell = new Listcell();
        maximumLoadCell.appendChild(new Doublebox(employmentModel.getMaximum_load()));
        maximumLoadCell.setParent(listitem);
        listitem.setValue(employmentModel);
    }
}
