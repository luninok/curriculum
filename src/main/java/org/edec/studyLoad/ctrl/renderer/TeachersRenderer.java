package org.edec.studyLoad.ctrl.renderer;

import org.edec.studyLoad.model.TeacherModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class TeachersRenderer implements ListitemRenderer<TeacherModel> {

    public void render(Listitem listitem, TeacherModel teacherModel, int i) throws Exception {
        new Listcell(teacherModel.getFamily()).setParent(listitem);
        new Listcell(teacherModel.getName()).setParent(listitem);
        new Listcell(teacherModel.getPatronymic()).setParent(listitem);
        listitem.setValue(teacherModel);
    }
}
