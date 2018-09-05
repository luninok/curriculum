package org.edec.profile.ctrl;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.ModuleModel;
import org.edec.profile.model.ProfileModel;
import org.edec.profile.service.ProfileService;
import org.edec.profile.service.impl.ProfileServiceEsoImpl;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.HashMap;
import java.util.Map;

import static org.zkoss.zk.ui.Executions.getCurrent;

/**
 * @autor Max Dimukhametov
 */
public class IndexPageCtrl extends SelectorComposer<Component> {

    @Wire
    private Button btnEditEmail, btnOkEmail, btnEditBirthDay, btnOkBirthDay;
    @Wire
    private Checkbox chNotification;
    @Wire
    private Datebox dbBirthDay;
    @Wire
    private Label lFio, lBirthDay, lEmail, lStartPage;
    @Wire
    private Listitem liBirthday, liEmail, liNotification;
    @Wire
    private Textbox tbEmail;

    private ProfileService profileService = new ProfileServiceEsoImpl();
    private TemplatePageCtrl template = new TemplatePageCtrl();

    private ProfileModel profileModel;
    private ModuleModel currentModule;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        template.checkModuleByRole(getCurrent().getDesktop().getRequestPath(), getPage());
        currentModule = template.getCurrentModule();
        if (currentModule != null) {
            fill();
        }
    }

    private void fill () {
        try {
            if (template.getCurrentUser().isParent()) {
                liBirthday.detach();
                liEmail.detach();
                liNotification.detach();
                lFio.setValue(template.getCurrentUser().getFio());
                String startPage = profileService.getNameStartPage(template.getCurrentUser());
                lStartPage.setValue(startPage == null ? "Не значена" : startPage);
            } else {
                profileModel = profileService.getProfileByHum(template.getCurrentUser().getIdHum());
                lFio.setValue(profileModel.getFio());
                lBirthDay.setValue(DateConverter.convertDateToString(profileModel.getBirthDay()));
                lEmail.setValue(profileModel.getEmail());
                String startPage = profileService.getNameStartPage(template.getCurrentUser());
                lStartPage.setValue(startPage == null ? "Не значена" : startPage);
                chNotification.setChecked(profileModel.isGetNotification());
            }
        } catch (NullPointerException ignored) {

        }
    }

    @Listen("onClick = #btnEditBirthDay")
    public void editBirthDay () {
        lBirthDay.setVisible(false);
        btnEditBirthDay.setVisible(false);
        dbBirthDay.setValue(profileModel.getBirthDay());
        dbBirthDay.setVisible(true);
        btnOkBirthDay.setVisible(true);
    }

    @Listen("onClick = #btnOkBirthDay")
    public void saveBirthDay () {
        if (dbBirthDay.getValue() == null) {
            PopupUtil.showWarning("Заполните дату рождения!");
            return;
        }
        if (profileService.updateBirthDay(template.getCurrentUser().getIdHum(), dbBirthDay.getValue())) {
            PopupUtil.showInfo("Дата рождения успешно обновлена");
            profileModel.setBirthDay(dbBirthDay.getValue());
            lBirthDay.setValue(DateConverter.convertDateToString(profileModel.getBirthDay()));
        } else {
            PopupUtil.showError("Дату рождения обновить не удалось");
        }

        lBirthDay.setVisible(true);
        btnEditBirthDay.setVisible(true);
        dbBirthDay.setVisible(false);
        btnOkBirthDay.setVisible(false);
    }

    @Listen("onClick = #btnEditEmail")
    public void editEmail () {
        lEmail.setVisible(false);
        btnEditEmail.setVisible(false);
        tbEmail.setValue(profileModel.getEmail());
        tbEmail.setVisible(true);
        btnOkEmail.setVisible(true);
    }

    @Listen("onClick = #btnOkEmail")
    public void saveEmail () {
        if (tbEmail.getValue() == null || tbEmail.getValue().equals("")) {
            PopupUtil.showWarning("Заполните поле с e-mail!");
            return;
        }
        if (profileService.updateEmail(template.getCurrentUser().getIdHum(), tbEmail.getValue())) {
            PopupUtil.showInfo("E-mail успешно обновлен");
            profileModel.setEmail(tbEmail.getValue());
            lEmail.setValue(profileModel.getEmail());
        } else {
            PopupUtil.showError("Не удалось изменить e-mail");
        }
        tbEmail.setVisible(false);
        btnOkEmail.setVisible(false);
        lEmail.setVisible(true);
        btnEditEmail.setVisible(true);
    }

    @Listen("onCheck = #chNotification")
    public void changeNotification () {
        if (profileService.updateGetNotification(template.getCurrentUser().getIdHum(), chNotification.isChecked())) {
            PopupUtil.showInfo("Статус уведомления на почту обновлен");
            profileModel.setGetNotification(chNotification.isChecked());
        } else {
            PopupUtil.showError("Не удалось обновить статус уведомления");
        }
    }

    @Listen("onClick = #btnStartPage")
    public void checkStartPage () {
        Map<String, Object> arg = new HashMap<>();
        arg.put(WinModuleStartPageCtrl.PROFILE_PAGE, this);

        ComponentHelper.createWindow("/profile/winModuleStartPage.zul", "winModuleStartPage", arg).doModal();
    }

    public void setStartPage (ModuleModel module) {
        lStartPage.setValue(module.getName());
    }
}
