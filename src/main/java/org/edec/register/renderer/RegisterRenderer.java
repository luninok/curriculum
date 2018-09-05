package org.edec.register.renderer;

import org.edec.register.ctrl.WinRegisterCtrl;
import org.edec.register.model.RegisterModel;
import org.edec.register.service.impl.RegisterServiceImpl;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RegisterConst;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.pdfViewer.ctrl.PdfViewerCtrl;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by antonskripacev on 10.06.17.
 */
public class RegisterRenderer implements ListitemRenderer<RegisterModel> {
    private Runnable update;
    private String fioUser;
    private boolean readOnly;

    public RegisterRenderer(Runnable update, String fioUser, boolean readOnly) {
        this.fioUser = fioUser;
        this.update = update;
        this.readOnly = readOnly;
    }

    @Override
    public void render(Listitem li, final RegisterModel data, int i) throws Exception {
        String typeRegister = "";

        if(data.getRetakeCount() == RegisterConst.TYPE_MAIN || data.getRetakeCount() == RegisterConst.TYPE_MAIN_NOT_SIGNED) {
            typeRegister = "Осн. сдача";
        }

        if(data.getRetakeCount() == RegisterConst.TYPE_RETAKE_MAIN || data.getRetakeCount() == RegisterConst.TYPE_RETAKE_MAIN_NOT_SIGNED) {
            typeRegister = "Общ. пересдача";
        }

        if(data.getRetakeCount() == RegisterConst.TYPE_COMMISSION_NOT_SIGNED || data.getRetakeCount() == RegisterConst.TYPE_COMMISSION) {
            typeRegister = "Комиссия";
        }

        if(data.getRetakeCount() == RegisterConst.TYPE_RETAKE_INDIV || data.getRetakeCount() == RegisterConst.TYPE_RETAKE_INDIV_NOT_SIGNED) {
            typeRegister = "Инд. пересдача";
        }

        Menupopup menupopup = new Menupopup();

        Menuitem menuitemDel = new Menuitem("Удалить ведомость");
        menuitemDel.addEventListener(Events.ON_CLICK, e -> {
            if(new RegisterServiceImpl().removeIndivRetake(data, fioUser)) {
                PopupUtil.showInfo("Ведомость удалена");
                update.run();
            }
        });

        Menuitem menuitemShow = new Menuitem("Просмотреть ведомость");
        menuitemShow.addEventListener(Events.ON_CLICK, e -> {
            Map<String, Object> arg = new HashMap<>();
            arg.put(PdfViewerCtrl.FILE, new RegisterServiceImpl().getFileRegister(data.getRegisterUrl(), data.getIdRegister()));
            ComponentHelper.createWindow("/utility/pdfViewer/index.zul", "winPdfViewer", arg).doModal();
        });

        menupopup.appendChild(menuitemShow);
        menupopup.appendChild(menuitemDel);

        li.setContext(menupopup);

        new Listcell(data.getSubjectName()).setParent(li);
        new Listcell(data.getGroupName()).setParent(li);
        String teachersStr = data.getTeachers().toString();
        Listcell teachers = new Listcell(teachersStr.length() < 2 ? "" : teachersStr.substring(1, teachersStr.length() - 1));
        teachers.setParent(li);
        teachers.setTooltiptext(teachers.getLabel());

        if(data.getRetakeCount() == RegisterConst.TYPE_RETAKE_INDIV || data.getRetakeCount() == RegisterConst.TYPE_RETAKE_INDIV_NOT_SIGNED) {
            new Listcell(data.getStudents().get(0).getFio()).setParent(li);
        } else {
            new Listcell("").setParent(li);
        }

        new Listcell(FormOfControlConst.getName(data.getFoc()) != null ? FormOfControlConst.getName(data.getFoc()).getName() : "null").setParent(li);
        new Listcell(typeRegister).setParent(li);
        new Listcell(DateConverter.convertDateToString(data.getSignDate(), "-")).setParent(li);
        new Listcell(data.getRegisterNumber() == null ? "" : data.getRegisterNumber()).setParent(li);

        Listcell lcSynch = new Listcell();
        lcSynch.setParent(li);

        if (data.getSynchStatus() != null && data.getSynchStatus() != 0) {
            Div divSynch = new Div();

            String colorBg = "";

            if(data.getSynchStatus() == 1) {
                colorBg = "#95FF82";
            } else if(data.getSynchStatus() == 2){
                colorBg = "#EEFC22";
            } else if(data.getSynchStatus() == -1){
                colorBg = "#FF7373";
            }

            divSynch.setStyle("border: 1px solid black; background: " + colorBg + "; width: 40px; height: 40px; margin: 5px 0px 0px 24px");
            divSynch.setParent(lcSynch);
        }

        Listcell cellShow = new Listcell();
        Listcell cellDelete = new Listcell();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);

        if(data.getCertNumber() == null && data.getDateOfEnd() != null && data.getDateOfEnd().before(cal.getTime())) {
            li.setStyle("background: #FF7373");
            if(data.getRetakeCount() == RegisterConst.TYPE_RETAKE_INDIV || data.getRetakeCount() == RegisterConst.TYPE_RETAKE_INDIV_NOT_SIGNED) {
                if(!readOnly && (data.getRegisterNumber() == null || data.getRegisterNumber().equals(""))) createButtonDelete(cellDelete, data);
            }
        } else if(data.getCertNumber() != null && !data.getCertNumber().equals("")) {
            li.setStyle("background: #95FF82");
            createButtonShow(cellShow, data);
        } else {
            if(data.getRetakeCount() == RegisterConst.TYPE_RETAKE_INDIV || data.getRetakeCount() == RegisterConst.TYPE_RETAKE_INDIV_NOT_SIGNED) {
                if(!readOnly && (data.getRegisterNumber() == null || data.getRegisterNumber().equals(""))) createButtonDelete(cellDelete, data);
            }
        }

        cellShow.setParent(li);
        cellDelete.setParent(li);

        li.addEventListener(Events.ON_CLICK, event -> {
            Map arg = new HashMap();
            arg.put(WinRegisterCtrl.REGISTER, data);
            arg.put(WinRegisterCtrl.UPDATE_INTERFACE, update);
            ComponentHelper.createWindow("/register/winRegister.zul", "winLookRegister", arg).doModal();
        });
    }

    private void createButtonDelete(Listcell listcell, RegisterModel registerModel) {
        Button btnDel = new Button("", "/imgs/del.png");

        btnDel.addEventListener(Events.ON_CLICK,
                event -> {
                    if(new RegisterServiceImpl().removeIndivRetake(registerModel, fioUser) == true) {
                        PopupUtil.showInfo("Ведомость удалена");
                        update.run();
                    }
                });

        btnDel.setHeight("100%");
        btnDel.setParent(listcell);
    }

    private void createButtonShow(Listcell listcell, RegisterModel registerModel) {
        Button btnShow = new Button("", "/imgs/pdf.png");
        btnShow.addEventListener(Events.ON_CLICK, event -> {
            Map<String, Object> arg = new HashMap<>();
            arg.put(PdfViewerCtrl.FILE, new RegisterServiceImpl().getFileRegister(registerModel.getRegisterUrl(), registerModel.getIdRegister()));
            ComponentHelper.createWindow("/utility/pdfViewer/index.zul", "winPdfViewer", arg).doModal();
        });
        btnShow.setHeight("100%");
        btnShow.setParent(listcell);
    }
}
