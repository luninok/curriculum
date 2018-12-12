package org.edec.studyLoad.ctrl.renderer;

import org.edec.studyLoad.model.StudyLoadModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class StudyLoadRenderer implements ListitemRenderer<StudyLoadModel> {
    public void render(Listitem listitem, StudyLoadModel studyLoadModel, int i) throws Exception {
        new Listcell(String.valueOf(i + 1)).setParent(listitem);
        new Listcell(studyLoadModel.getPlanFileName()).setParent(listitem);
        new Listcell(studyLoadModel.getInstituteShortTitle()).setParent(listitem);
        new Listcell(studyLoadModel.getSubjectCode()).setParent(listitem);
        new Listcell(studyLoadModel.getSubjectName()).setParent(listitem);
        new Listcell(studyLoadModel.getDepartmentShortTitle()).setParent(listitem);
        new Listcell(String.valueOf(studyLoadModel.getCourse()) + "/"
                + String.valueOf(studyLoadModel.getSemester())).setParent(listitem);
        new Listcell(studyLoadModel.getGroupName()).setParent(listitem);
        switch (studyLoadModel.getTutoringType()){
            case 0:
                new Listcell("Лек.").setParent(listitem);
                break;
            case 1:
                new Listcell("Прак.").setParent(listitem);
                break;
            case 3:
                new Listcell("Лаб.").setParent(listitem);
                break;
        }
        new Listcell(String.valueOf(studyLoadModel.getHoursCount())).setParent(listitem);
        if (studyLoadModel.getIsExam())
            new Listcell("Экз.").setParent(listitem);
        else
            new Listcell("Зач.").setParent(listitem);
        new Listcell(String.valueOf(studyLoadModel.getHoursCount())).setParent(listitem);
        new Listcell(String.valueOf(studyLoadModel.getHoursCount())).setParent(listitem);
        new Listcell(studyLoadModel.getFamily() + " " + studyLoadModel.getName().substring(0,1) + "." + studyLoadModel.getPatronymic().substring(0,1) + ".").setParent(listitem);
        listitem.setValue(studyLoadModel);
    }
}
