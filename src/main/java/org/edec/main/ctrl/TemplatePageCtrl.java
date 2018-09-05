package org.edec.main.ctrl;

import org.edec.main.model.ModuleModel;
import org.edec.main.model.RoleModel;
import org.edec.main.model.UserModel;
import org.edec.main.service.UserService;
import org.edec.main.service.impl.UserServiceESOimpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zhtml.Li;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zhtml.Ul;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;


public class TemplatePageCtrl extends SelectorComposer<Component> {
    private static final String CURRENT_MODULE = "current_module";
    public static final String CURRENT_USER = "current_user";

    @Wire
    private Div divSidebar, divBadgeNotification;
    @Wire
    private Hbox hbTemplateNotification;
    @Wire
    private Label lPageName, lCurrentUser;
    @Wire
    private Popup popupTemplateCogs;
    @Wire
    private Span spanCogs, spanShowNav;

    private UserService userService = new UserServiceESOimpl();

    private UserModel userModel;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        userModel = getCurrentUser();
        if (userModel != null) {
            generateDiv();
        }
    }

    @Listen("onClick = #divProfile")
    public void showProfile() {
        Executions.sendRedirect("/");
    }

    @Listen("onClick = #divOverlay")
    public void clickOnDivOverlay() {
        divSidebar.setSclass("sidebar close");
    }

    @Listen("onClick = #spanShowNav")
    public void showNav() {
        if (divSidebar.getSclass().equals("sidebar close")) {
            divSidebar.setSclass("sidebar");
            spanShowNav.setSclass("z-icon-angle-double-up");
        } else {
            divSidebar.setSclass("sidebar close");
            spanShowNav.setSclass("z-icon-angle-double-down");
        }
    }

    @Listen("onClick = #spanCogs")
    public void clickOnCogs() {
        if (!getCurrentUser().isStudent()) {
            hbTemplateNotification.setVisible(false);
        }
        popupTemplateCogs.open(spanCogs);
    }

    @Listen("onClick = #hbTemplateQuestion")
    public void showMessageBoxQuestion() {
        Messagebox.show(
                "На данный момент Вы можете сообщить о проблеме, отправив ее описание по адресу dec.developers@gmail.com"
                , "Внимание!", Messagebox.OK, org.zkoss.zul.Messagebox.INFORMATION);
    }

    @Listen("onClick = #hbTemplateNotification")
    public void goToNotification() {
        PopupUtil.showError("Модуль пока не доступен");
        //Executions.sendRedirect("/msg");
    }

    @Listen("onClick = #hbTemplateExit")
    public void exit() {
        Executions.getCurrent().getSession().setAttribute(CURRENT_USER, null);
        Executions.getCurrent().getSession().setAttribute(CURRENT_MODULE, null);
        Executions.sendRedirect("/login.zul");
    }

    private void generateDiv() {
        if (getCurrentModule() != null) {
            userService.changeSelected(getCurrentModule(), getCurrentUser());
        }

        lCurrentUser.setValue(getCurrentUser().getShortFIO());
        Ul ul = new Ul();
        ul.setParent(divSidebar);
        ul.setSclass("side-nav");
        for (final RoleModel role : userModel.getRoles()) {
            if (!role.isShow()) {
                continue;
            }

            Li li = new Li();
            li.setParent(ul);
            if (role.isSingle()) {
                li.setSclass("single");
                final ModuleModel module = role.getModules().get(0);
                A a = new A(module.getName());
                a.setParent(li);
                a.setSclass(role.isSelected() ? "b" : "");
                a.setStyle("padding-left: 18px");
                a.addEventListener(Events.ON_CLICK, event -> {
                    Executions.getCurrent().getSession().setAttribute(CURRENT_MODULE, module);
                    Executions.sendRedirect(module.getUrl());
                });
                continue;
            }
            final A aRole = new A(role.getName());
            aRole.setParent(li);
            aRole.setStyle("border-left: 8px solid #E66E24;");
            aRole.setSclass(role.isSelected() ? "b" : "");

            final Div divCategory = new Div();
            divCategory.setParent(li);
            divCategory.setSclass("side-nav-cat");
            divCategory.setStyle(role.isOpenTree() ? "display: block;" : "display: none;");

            Ul ulRole = new Ul();
            ulRole.setParent(divCategory);

            for (final ModuleModel module : role.getModules()) {
                Li liModule = new Li();
                liModule.setParent(ulRole);

                A aModule = new A(module.getName());
                aModule.setParent(liModule);
                aModule.setSclass(module.isSelected() ? "waves-effect b" : "waves-effect");
                aModule.addEventListener(Events.ON_CLICK, event -> {
                    Executions.getCurrent().getSession().setAttribute(CURRENT_MODULE, module);
                    Executions.sendRedirect(module.getUrl());
                });
            }
            aRole.addEventListener(Events.ON_CLICK, event -> {
                if (role.isOpenTree()) {
                    role.setOpenTree(false);
                    divCategory.setStyle("display: none;");
                } else {
                    role.setOpenTree(true);
                    divCategory.setStyle("display: block;");
                }
            });
        }
    }

    private void changeCurrentPageName(Page page, String name) {
        page.setTitle(name);
        lPageName = (Label) Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("lPageName");
        lPageName.setValue(name);
    }

    public void checkModuleByRole(String path, Page page) {
        path = path.replace("index.zul", "");
        if (path.length() > 1 && path.substring(path.length() - 1, path.length()).equals("/")) {
            path = path.substring(0, path.length() - 1);
        }
        //if (!path.equals("/")&&!path.equals("")) path = "/" + path.split("/")[1];
        ModuleModel module;
        if (getCurrentModule() != null) {
            if (getCurrentModule().getUrl().equals(path)) {
                changeCurrentPageName(page, getCurrentModule().getRole().isSingle()
                        ? getCurrentModule().getName()
                        : getCurrentModule().getRole().getName() + " / " + getCurrentModule().getName()
                );
                return;
            }
            module = userService.getModuleByRoleAndPath(path, getCurrentModule().getRole());
            if (module != null) {
                Executions.getCurrent().getSession().setAttribute(CURRENT_MODULE, module);
                changeCurrentPageName(page, getCurrentModule().getRole().isSingle()
                        ? getCurrentModule().getName()
                        : getCurrentModule().getRole().getName() + " / " + getCurrentModule().getName());
                Executions.sendRedirect(module.getUrl());
            } else {
                checkModuleByUser(path, page);
            }
        } else {
            checkModuleByUser(path, page);
        }
    }

    private void checkModuleByUser(String path, Page page) {
        ModuleModel module = userService.getModuleByUserAndPath(path, getCurrentUser());
        if (module != null) {
            Executions.getCurrent().getSession().setAttribute(CURRENT_MODULE, module);
            changeCurrentPageName(page, getCurrentModule().getRole().isSingle()
                    ? getCurrentModule().getName()
                    : getCurrentModule().getRole().getName() + " / " + getCurrentModule().getName());
            if (!module.getUrl().equals(path)) {
                Executions.sendRedirect(module.getUrl());
            }
        } else {
            Executions.getCurrent().getSession().setAttribute(CURRENT_MODULE, null);
            Executions.sendRedirect("/");
        }
    }

    public void setVisitedModuleByHum(Long idHum, Long idModule) {
        userService.setVisitedModuleByHum(idHum, idModule);
    }

    public ModuleModel getCurrentModule() {
        return (ModuleModel) Executions.getCurrent().getSession().getAttribute(CURRENT_MODULE);
    }

    public UserModel getCurrentUser() {
        return Executions.getCurrent() == null
                ? null
                : (UserModel) Executions.getCurrent().getSession().getAttribute(CURRENT_USER);
    }
}