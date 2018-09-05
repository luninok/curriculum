package org.edec.newOrder.ctrl;

import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.addStudent.LinkOrderSectionEditModel;
import org.edec.newOrder.model.addStudent.SearchStudentModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.service.ComponentProvider;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;
import org.zkoss.zul.impl.XulElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by antonskripacev on 03.01.17.
 */
public class WinSetParamsForStudentCtrl extends SelectorComposer<Component> {
    public static final int ORDER = 1;
    public static final int SECTION = 2;
    public static final int STUDENTS = 3;
    public static final int UPDATE_ADD_STUDENT_UI = 4;
    public static final int UPDATE_ADD_EDIT_ORDER = 5;
    public static final int ORDER_SERVICE = 6;

    @Wire
    private Window winSetParamsForStudent;

    @Wire
    private Listbox lbStudents;

    @Wire
    private Button btnApprove;

    @Wire
    private Button btnCancel;

    private OrderService service;
    private ComponentProvider componentProvider = new ComponentProvider();

    private OrderEditModel order;
    private LinkOrderSectionEditModel sectionModel;
    private List<SearchStudentModel> listStudentsToAdd = new ArrayList<>();
    private List<XulElement> componentParams = new ArrayList<>();

    private Runnable updateAddStudentWindow, updateEditOrderWindow;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);

        order = (OrderEditModel) Executions.getCurrent().getArg().get(ORDER);
        sectionModel = (LinkOrderSectionEditModel) Executions.getCurrent().getArg().get(SECTION);
        listStudentsToAdd = (List<SearchStudentModel>) Executions.getCurrent().getArg().get(STUDENTS);
        updateAddStudentWindow = (Runnable) Executions.getCurrent().getArg().get(UPDATE_ADD_STUDENT_UI);
        updateEditOrderWindow = (Runnable) Executions.getCurrent().getArg().get(UPDATE_ADD_EDIT_ORDER);
        service = (OrderService) Executions.getCurrent().getArg().get(ORDER_SERVICE);

        componentProvider.createListheader(lbStudents.getListhead(), "Студент", "cwf-listheader-label", "200px");
        componentProvider.createListheader(lbStudents.getListhead(), "Группа", "cwf-listheader-label", "100px");

        List<OrderVisualParamModel> visualParamModels = service.getVisualParamsByIdSection(sectionModel.getIdOS());
        for (OrderVisualParamModel param : visualParamModels) {
            componentProvider.createListheader(lbStudents.getListhead(), param.getName(), "cwf-listheader-label", "80px");
        }

        for (SearchStudentModel studentModel : listStudentsToAdd) {
            Listitem li = new Listitem();

            componentProvider.createListcell(
                    li, studentModel.getSurname() + " " + studentModel.getName() + " " + studentModel.getPatronymic(), "200px");
            componentProvider.createListcell(li, studentModel.getGroupname(), "100px");

            for (OrderVisualParamModel param : visualParamModels) {
                XulElement component = componentProvider.provideComponent(param.getEditComponent());
                componentProvider.createListcell(li, component, "80px");
                componentParams.add(component);
            }

            lbStudents.appendChild(li);
        }
    }

    @Listen("onClick = #btnCancel")
    public void cancel () {
        winSetParamsForStudent.detach();
    }

    @Listen("onClick = #btnApprove")
    public void approveList () {
        int i = 0;

        for (SearchStudentModel studentModel : listStudentsToAdd) {
            for (int j = 0; j < service.getVisualParamsByIdSection(sectionModel.getIdOS()).size(); j++) {
                studentModel.getStudentParams()
                            .add(componentProvider.getValueComponent(
                                    componentParams.get(i * service.getVisualParamsByIdSection(sectionModel.getIdOS()).size() + j)));
            }

            service.addStudentToOrder(studentModel, order, sectionModel);
            i++;
        }

        updateAddStudentWindow.run();
        updateEditOrderWindow.run();

        winSetParamsForStudent.detach();
    }
}
