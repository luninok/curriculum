package org.edec.utility.zk;

import lombok.experimental.UtilityClass;
import org.zkoss.zk.ui.util.Clients;

/* Утилити-класс для показа всплывающих сообщений */
@UtilityClass
public class PopupUtil {

    /* Окно информации (голубое) используем для оповещения об успешном событии */
    public static void showInfo (String msg) {
        Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_INFO, null, null, 2000);
    }

    /* Окно предупреждения (оранжевое) - используем если пользователь не выполнил требования(например не заполнил нужные поля) */
    public static void showWarning (String msg) {
        Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_WARNING, null, null, 2000);
    }

    /* Окно ошибки (красное) используем для оповещения об ошибке */
    public static void showError (String msg) {
        Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_ERROR, null, null, 2000);
    }
}
