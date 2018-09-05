package org.edec.commission.ctrl.renderer;

import org.edec.commission.model.SubjectDebtModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dmmax
 */
public class SubjectDebtRenderer implements ListitemRenderer<SubjectDebtModel> {
    @Override
    public void render (Listitem li, SubjectDebtModel data, int index) throws Exception {
        li.setValue(data);

        new Listcell(data.getSubjectname()).setParent(li);
        new Listcell(data.getFocStr()).setParent(li);
        new Listcell(data.getFulltitle()).setParent(li);
        new Listcell(data.getSemesterStr()).setParent(li);
        new Listcell(data.getDateComission() == null
                     ? "Не назначена"
                     : new SimpleDateFormat("dd.MM.yyyyг. HH:mm").format(data.getDateComission())).setParent(li);
        new Listcell(data.getClassroom()).setParent(li);
        new Listcell(data.getCheckedcount() + "/" + data.getCountstudent()).setParent(li);
        li.setStyle("background: #" +
                    (data.getDateComission() != null ? (data.getDateComission().after(new Date()) ? "fff;" : "ffcccc;") : "ccc;"));
        if (data.isSigned()) {
            li.setStyle("background: #99ff99;");
        }
    }
}
