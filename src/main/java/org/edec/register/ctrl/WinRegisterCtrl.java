package org.edec.register.ctrl;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.ModuleModel;
import org.edec.register.model.RegisterModel;
import org.edec.register.model.StudentModel;
import org.edec.register.model.mine.StudentMineModel;
import org.edec.register.service.RegisterService;
import org.edec.register.service.impl.RegisterServiceImpl;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.constants.RegisterConst;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import static org.zkoss.zk.ui.Executions.getCurrent;

/**
 * Created by antonskripacev on 10.06.17.
 */
public class WinRegisterCtrl extends SelectorComposer<Component> {
    public static String REGISTER = "register";
    public static String UPDATE_INTERFACE = "update";

    @Wire
    private Label lbName;
    @Wire
    private Window winLookRegister;
    @Wire
    private Listbox lbStudents, lbAsuIkit, lbMine;
    @Wire
    private Groupbox gbDates;
    @Wire
    private Datebox dateOfBegin, dateOfEnd;
    @Wire
    private Button saveDates, btnCancelSign;
    @Wire
    private Tab tabMine;

    private TemplatePageCtrl template = new TemplatePageCtrl();
    private ModuleModel currentModule;
    private Runnable updateIndex;
    private RegisterModel model;

    private RegisterService service = new RegisterServiceImpl();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        template.checkModuleByRole(getCurrent().getDesktop().getRequestPath(), getPage());
        currentModule = template.getCurrentModule();

        model = (RegisterModel) Executions.getCurrent().getArg().get(REGISTER);
        updateIndex = (Runnable) Executions.getCurrent().getArg().get(UPDATE_INTERFACE);

        dateOfBegin.setValue(model.getDateOfBegin());
        dateOfEnd.setValue(model.getDateOfEnd());

        if (model.getRetakeCount() == RegisterConst.TYPE_MAIN || model.getRetakeCount() == RegisterConst.TYPE_COMMISSION) {
            gbDates.setVisible(false);
        }

        if (currentModule.isReadonly()) {
            gbDates.setVisible(false);
            dateOfBegin.setDisabled(true);
            dateOfEnd.setDisabled(true);
            saveDates.setDisabled(true);
        } else if (model.getRetakeCount() == -2 || model.getRetakeCount() == -4) {
            saveDates.addEventListener(Events.ON_CLICK, event -> {
                if (dateOfBegin.getValue() == null || dateOfEnd.getValue() == null) {
                    PopupUtil.showWarning("Заполните даты");
                } else {
                    for (StudentModel student : model.getStudents()) {
                        service.updateDatesRegister(dateOfBegin.getValue(), dateOfEnd.getValue(), student.getIdSRH());
                    }

                    updateIndex.run();
                }
            });
        } else {
            dateOfBegin.setDisabled(true);
            dateOfEnd.setDisabled(true);
            saveDates.setDisabled(true);
        }

        if (!currentModule.isReadonly() && model.getCertNumber() != null) {
            btnCancelSign.setDisabled(false);
            btnCancelSign.setVisible(true);

            btnCancelSign.addEventListener(Events.ON_CLICK, event -> {
                if (service.cancelRegister(model)) {
                    PopupUtil.showInfo("Отмена подписи прошла успешно");
                    getSelf().detach();
                } else {
                    PopupUtil.showError("Не удалось отменить подпись ведомости, обратитесь в поддержку, код - " + model.getIdRegister());
                }

                updateIndex.run();
            });
        }

        model.getFoc();
        lbName.setValue(model.getSubjectName() + "/" + FormOfControlConst.getName(model.getFoc()).getName());

        lbStudents.setModel(new ListModelList<>(model.getStudents()));
        lbStudents.setHeight("400px");
        lbStudents.setItemRenderer((Listitem li, StudentModel data, int i) -> {
            new Listcell(data.getFio()).setParent(li);

            String rating = "";
            switch (data.getRating()) {
                case 5:
                case 4:
                case 3:
                case 2:
                    rating = Integer.toString(data.getRating());
                    break;
                case 1:
                    rating = "Зачтено";
                    break;
                case 0:
                    rating = (model.getFoc() == FormOfControlConst.PASS.getValue() && data.getType() == 0)
                             ? "Не зачтено"
                             : "Оценка не выставлена";
                    break;
                case -2:
                    rating = "Не зачтено";
                    break;
                case -3:
                    rating = "Неявка";
                    break;
            }

            new Listcell(rating).setParent(li);
        });

        lbStudents.renderAll();

        if(model.getSignDate() != null) {
            tabMine.setVisible(true);
            if(configureListBoxTabCompareRegister()) {
                lbAsuIkit.setModel(new ListModelList<>(model.getStudents()));
                lbMine.setModel(new ListModelList<>(service.getFilteredStudentsForRegisterFromMine(model)));
            }
        }
    }

    private boolean configureListBoxTabCompareRegister() {
        if(model.getRetakeCount() == null || model.getRetakeCount() <= 0) {
            DialogUtil.error("Произошла ошибка при получении данных, обратитесь к администратору.");
            return false;
        }

        lbAsuIkit.setItemRenderer((ListitemRenderer<StudentModel>) (li, data, i) -> {
            new Listcell(data.getFio()).setParent(li);

            RatingConst rc = RatingConst.getDataByRating(data.getRating());
            new Listcell(rc != null ? rc.getName() : "").setParent(li);
        });

        lbMine.getListhead().appendChild(createListheader("ФИО", "3"));

        if(model.getRetakeCount() == 1) {
            lbMine.getListhead().appendChild(createListheader("Оценка", "1"));

            lbMine.setItemRenderer((ListitemRenderer<StudentMineModel>)(li, data, i) -> {
                new Listcell(data.getFio()).setParent(li);
                new Listcell(data.getMainMark() != null ? data.getMainMark().getName() : "").setParent(li);
            });
        } else {
            lbMine.getListhead().appendChild(createListheader("Оценка 1", "1"));
            lbMine.getListhead().appendChild(createListheader("Оценка 2", "1"));

            lbMine.setItemRenderer((ListitemRenderer<StudentMineModel>)(li, data, i) -> {
                new Listcell(data.getFio()).setParent(li);
                new Listcell(data.getMark1() != null ? data.getMark1().getName() : "").setParent(li);
                new Listcell(data.getMark2() != null ? data.getMark2().getName() : "").setParent(li);
            });
        }

        return true;
    }

    private Listheader createListheader(String label, String hflex) {
        Listheader lhr = new Listheader();
        lhr.setHflex(hflex);
        Label lbl = new Label(label);
        lbl.setSclass("cwf-listheader-label");
        lhr.appendChild(lbl);
        return lhr;
    }
}
