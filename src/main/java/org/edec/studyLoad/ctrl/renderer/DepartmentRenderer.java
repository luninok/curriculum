package org.edec.studyLoad.ctrl.renderer;

import org.edec.model.GroupSemesterModel;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;

public class DepartmentRenderer implements ComboitemRenderer<String> {

    @Override
    public void render(Comboitem ci, String data, int i) throws Exception {
        ci.setValue(data);
        ci.setLabel(data);
    }
}
