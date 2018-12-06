package org.edec.studyLoad.ctrl.renderer;

import org.edec.studyLoad.model.VacancyModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class VacancyRenderer implements ListitemRenderer<VacancyModel> {

    public void render(Listitem listitem, VacancyModel vacancyModel, int i) throws Exception {
        listitem.appendChild(new Listcell(String.valueOf(i+1)));
        listitem.appendChild(new Listcell(vacancyModel.getRolename()));
        listitem.appendChild(new Listcell(String.valueOf(vacancyModel.getWagerate())));
        listitem.setValue(vacancyModel);
    }
}
