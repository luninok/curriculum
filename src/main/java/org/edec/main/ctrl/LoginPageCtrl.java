package org.edec.main.ctrl;

import lombok.extern.log4j.Log4j;
import org.edec.main.auth.AuthInit;
import org.edec.main.auth.AuthLDAP;
import org.edec.main.model.UserModel;
import org.edec.main.service.UserService;
import org.edec.main.service.impl.UserServiceESOimpl;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

import javax.naming.NamingException;


@Log4j
public class LoginPageCtrl extends SelectorComposer<Component> {

    /**
     * Объект класса связи с LDAP
     */
    private AuthLDAP authLDAP = new AuthLDAP();

    private UserService userService = new UserServiceESOimpl();

    private String prevPage;

    private String login;
    private String password;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        prevPage = (String) Executions.getCurrent().getSession().getAttribute(AuthInit.FROM_PAGE);
    }

    @Listen("onClick = #btnLogin; onOK=#winLogin")
    public void login() {
        //Проверка на пустоту
        if (login.equals("") || password.equals("")) {
            PopupUtil.showWarning("Заполните поля логина и пароля!");
            return;
        }
        //Админский бэкдор
        if (login.equals("admin") && password.equals("admin") && Executions.getCurrent().getServerName().equals("localhost")) {
            UserModel userModel = userService.getUserByLdapLogin("MDimukhametov-KI16"); // ASkripachev-KI17

            checkUser(userModel, login);
            return;
        }
        //Авторизация за Родителя
        if (login.startsWith("PA_")) {
            UserModel currentUser = userService.getUserByInnerLogin(login, password);
            if (currentUser != null) {
                checkUser(currentUser, login);
            } else {
                PopupUtil.showError("Пользователь с данной парой логин/пароль не найден");
                log.warn("login: " + login);
            }
            return;
        }
        //Нормальная авторизация
        try {
            String cn = authLDAP.getCN(login, password);
            if (cn != null) {
                UserModel currentUser = userService.getUserByLdapLogin(login);
                checkUser(currentUser, login);
            } else {
                PopupUtil.showError("Проблема авторизации LDAP. Проверьте свой логин на сервисе: users.sfu-kras.ru");
                log.warn("login: " + login + ", cn is null");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            PopupUtil.showError("Ошибка соединения с сервером LDAP. Обратитесь к администратору!");
        } catch (NamingException e) {
            e.printStackTrace();
            PopupUtil.showError("Проблема авторизации LDAP. Проверьте свой логин на сервисе: users.sfu-kras.ru");
        }
        login = "";
        password = "";
    }

    private void checkUser(UserModel currentUser, String login) {
        if (currentUser != null) {
            Executions.getCurrent().getSession().setAttribute(TemplatePageCtrl.CURRENT_USER, currentUser);

            if (currentUser.getStartPage() != null && !currentUser.getStartPage().equals("")) {
                Executions.sendRedirect(currentUser.getStartPage());
            } else if (prevPage != null) {
                if (prevPage.equals("") || prevPage.equals("/login.zul") || prevPage.equals("login.zul")) {
                    Executions.sendRedirect("/student");
                } else {
                    Executions.sendRedirect(prevPage);
                }
            } else {
                Executions.sendRedirect("/student");
            }
        } else {
            DialogUtil.error("Проблема авторизации БД. Напишите о проблеме, отправив ее описание по адресу dec.developers@gmail.com");

            log.warn("login: " + login + ", db problem");
        }
    }

    @Listen("onFinish = #hbMain")
    public void onFinish(Event ev) {
        JSONObject evData = (JSONObject) ev.getData();
        login = (String) evData.get("login");
        password = (String) evData.get("password");
        login();
    }

    @Listen("onOK = #hbMain")
    public void okHbox() {
        Clients.evalJavaScript("sendToServer()");
    }
}
