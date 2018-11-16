package org.edec.studyLoad.ctrl.renderer;

import org.edec.studyLoad.model.VacancyModal;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class VacancyRenderer implements ListitemRenderer<VacancyModal> {

    public void render(Listitem listitem, VacancyModal vacancyModal, int i) throws Exception {
        listitem.appendChild(new Listcell(vacancyModal.getVacancy()));
        listitem.appendChild(new Listcell(vacancyModal.getPosition()));
        listitem.appendChild(new Listcell(vacancyModal.getRate()));
        listitem.setValue(vacancyModal);
    }
}
