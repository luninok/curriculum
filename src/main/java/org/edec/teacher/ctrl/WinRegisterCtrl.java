package org.edec.teacher.ctrl;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.register.service.impl.RegisterServiceImpl;
import org.edec.teacher.ctrl.renderer.register.IndivRetakeRegisterRenderer;
import org.edec.teacher.ctrl.renderer.register.MainRegisterRenderer;
import org.edec.teacher.ctrl.renderer.register.MainRetakeRegisterRenderer;
import org.edec.teacher.model.GroupModel;
import org.edec.teacher.model.register.RegisterModel;
import org.edec.teacher.model.register.RegisterRowModel;
import org.edec.teacher.service.impl.RegisterServiseImpl;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.constants.RegisterConst;
import org.edec.utility.constants.RegisterType;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.pdfViewer.ctrl.PdfViewerCtrl;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by antonskripacev on 24.02.17.
 */
@Log4j
public class WinRegisterCtrl extends CabinetSelector {
    public static final String SELECTED_GROUP = "group";
    public static final String FORM_CONTROL = "fc";

    /// ~ Основная часть ~
    @Wire private Window winRegister;
    @Wire private Tabpanels tabPanelsRegister;
    @Wire private Label lbNameSubject, lbNameGroup, lbCourse, lbSemester, lbFormOfControl, lbHoursCount;
    @Wire private Hbox hboxMainRegisterBottomPanel;
    /// ~ Основная часть ~

    /// ~ Основная ведомость ~
    @Wire private Label lblMainRegisterDate, lblMainRegisterSignStatusInfo;
    @Wire private Listbox lbMainRegister;
    @Wire private Combobox cmbRatingMainRegister;
    @Wire private Button btnStudentListMainRegisterPrint, btnMainRegisterPrint, btnEnterRatingMainRegister;
    @Wire private Listheader lhrThemeMainRegister;
    @Wire private Tab tabMainRegister;
    /// ~ Основная ведомость ~

    /// ~ Общая пересдача ~
    @Wire private Tab tabMainRetake;
    @Wire private Label lblMainRetakeDate, lblMainRetakeRegisterSignStatusInfo;
    @Wire private Listbox lbMainRetakeRegister;
    @Wire private Combobox cmbRatingMainRetakeRegister;
    @Wire private Button btnEnterRatingMainRetakeRegister, btnPrintStudentListMainRetakeRegister, btnPrintMainRetakeRegister;
    @Wire private Listheader lhrThemeMainRetakeRegister;
    /// ~ Общая пересдача ~

    /// ~ Индивидуальная пересдача ~
    @Wire private Tab tabIndividualRetake;
    @Wire private Button btnOpenRegisterRequest;
    @Wire private Listbox lbIndivRetakeRegister;
    @Wire private Listheader lhrThemeIndivRegister;
    /// ~ Индивидуальная пересдача ~

    private RegisterServiseImpl service = new RegisterServiseImpl();

    private GroupModel curGroup;
    private int idSystemUser = 17;
    private FormOfControlConst foc;
    private List<RegisterModel> listIndividualRegisters;
    private RegisterModel mainRegister, mainRetakeRegister;
    private List<RatingConst> ratingConsts;
    private int type;
    private UserModel currentUser = (UserModel) Executions.getCurrent().getSession().getAttribute(TemplatePageCtrl.CURRENT_USER);

    ListModelList<RegisterRowModel> mainRegisterModel, mainRetakeRegisterModel;
    ListModelList<RegisterModel> indivRetakeRegisterModel;

    @Override
    protected void fill() {
        curGroup = (GroupModel) Executions.getCurrent().getArg().get(SELECTED_GROUP);
        Integer formOfControl = (Integer) Executions.getCurrent().getArg().get(FORM_CONTROL);
        foc = FormOfControlConst.getName(formOfControl);

        Events.echoEvent("onSelect", tabMainRegister, RegisterType.MAIN);
        Events.echoEvent("onSelect", tabMainRetake, RegisterType.MAIN_RETAKE);
        Events.echoEvent("onSelect", tabIndividualRetake, RegisterType.INDIVIDUAL_RETAKE);

        tabMainRegister.setSelected(true);

        initHeader();
        initRegListboxes();
    }

    @Listen("onLater = #tabPanelsRegister")
    public void onLoadingRegister(Event e) {
        init((RegisterType) e.getData());
    }

    @Listen("onSelect = #tabMainRegister")
    public void onSelectTabMain() {
        Clients.showBusy(tabPanelsRegister, "Загрузка данных");
        Events.echoEvent("onLater", tabPanelsRegister, RegisterType.MAIN);
    }

    @Listen("onSelect = #tabMainRetake")
    public void onSelectTabMainRetake() {
        Clients.showBusy(tabPanelsRegister, "Загрузка данных");
        Events.echoEvent("onLater", tabPanelsRegister, RegisterType.MAIN_RETAKE);
    }

    @Listen("onSelect = #tabIndividualRetake")
    public void onSelectTabIndividualRetake() {
        Clients.showBusy(tabPanelsRegister, "Загрузка данных");
        Events.echoEvent("onLater", tabPanelsRegister, RegisterType.INDIVIDUAL_RETAKE);
    }

    private void initHeader() {
        lbNameSubject.setValue(curGroup.getSubject().getSubjectname());
        lbNameGroup.setValue(curGroup.getGroupname());
        lbCourse.setValue(curGroup.getCourse() + "");

        String season = "осенний";
        if (curGroup.getSubject().getSemester().getSeason() == 1) {
            season = "весенний";
        }

        String yearOfBegin = new SimpleDateFormat("yyyy").format(curGroup.getSubject().getSemester().getDateOfBeginYear());
        String yearOfEnd = new SimpleDateFormat("yyyy").format(curGroup.getSubject().getSemester().getDateOfEndYear());
        lbSemester.setValue(yearOfBegin + " / " + yearOfEnd + " (" + season + ")");

        lbFormOfControl.setValue(FormOfControlConst.getName(curGroup.getSubject().getFormofcontrol()).getName());

        double hoursCount = curGroup.getHoursCount();
        double creditUnits = new BigDecimal(hoursCount / 36).setScale(2, RoundingMode.UP).doubleValue();

        lbHoursCount.setValue(String.valueOf((int) hoursCount) + " (" + String.valueOf(creditUnits) + ")");
    }

    private void initCmbMarks() {
        int type = 1;
        boolean isNotDifPass = FormOfControlConst.getName(curGroup.getSubject().getFormofcontrol()) == FormOfControlConst.PASS &&
                               this.type == 0;
        boolean practicIsPass = FormOfControlConst.getName(curGroup.getSubject().getFormofcontrol()) == FormOfControlConst.PRACTIC &&
                                this.type == 1;

        if (isNotDifPass || practicIsPass) {
            type = 2;
        }

        ratingConsts = RatingConst.getRatingsByType(type);

        for (RatingConst ratingConst : ratingConsts) {
            appendComboitem(cmbRatingMainRegister, ratingConst.getShortname(), "color: black;", ratingConst);
            appendComboitem(cmbRatingMainRetakeRegister, ratingConst.getShortname(), "color: black;", ratingConst);
        }

        cmbRatingMainRegister.setSelectedIndex(0);
        cmbRatingMainRetakeRegister.setSelectedIndex(0);
    }

    private void initRegListboxes() {
        if (FormOfControlConst.getName(curGroup.getSubject().getFormofcontrol()) == FormOfControlConst.CP ||
            FormOfControlConst.getName(curGroup.getSubject().getFormofcontrol()) == FormOfControlConst.CW) {
            lhrThemeMainRegister.setVisible(true);
            lhrThemeMainRetakeRegister.setVisible(true);
            lhrThemeIndivRegister.setVisible(true);
        }
    }

    public void init(RegisterType registerType) {
        switch (registerType) {
            case MAIN:
                initMainRegisterData();
                break;
            case MAIN_RETAKE:
                initMainRetakeRegisterData();
                break;
            case INDIVIDUAL_RETAKE:
                initIndividualRetakeRegisterData();
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип ведомости!");
        }

        Clients.clearBusy(tabPanelsRegister);
    }

    private void initMainRegisterData() {
        mainRegister = service.getMainRegister(curGroup.getIdLGSS(), FormOfControlConst.getName(curGroup.getSubject().getFormofcontrol()));
        type = curGroup.getSubject().getTypePass();

        if (ratingConsts == null) {
            initCmbMarks();
        }

        mainRegisterModel = new ListModelList<>(mainRegister.getListRegisterRow());

        if (mainRegister.isRegisterSigned()) {
            btnMainRegisterPrint.setLabel("Печать ведомости");
            btnMainRegisterPrint.setImage("/imgs/pdf.png");
            btnMainRegisterPrint.setStyle("height: 40px; width: 200px; font-size: 14px; font-weight: 700; color: #000000;");
            btnMainRegisterPrint.setVisible(true);
            hboxMainRegisterBottomPanel.setVisible(true);
            lblMainRegisterSignStatusInfo.setValue("Ведомость подписана с помощью ЭЦП.");
            lblMainRegisterSignStatusInfo.setStyle("color: black; font-weight: 700;");

            mainRegisterModel.setMultiple(false);
            lbMainRegister.setMultiple(false);
            lbMainRegister.setCheckmark(false);

            btnEnterRatingMainRegister.setVisible(false);
            cmbRatingMainRegister.setVisible(false);
        } else {
            if (!mainRegister.getListRegisterRow().isEmpty()) {
                mainRegisterModel.setMultiple(true);
                lbMainRegister.setMultiple(true);
                lbMainRegister.setCheckmark(true);

                btnEnterRatingMainRegister.setVisible(true);
                cmbRatingMainRegister.setVisible(true);
                hboxMainRegisterBottomPanel.setVisible(true);
                btnMainRegisterPrint.setVisible(true);
                btnMainRegisterPrint.setLabel("Подписать ведомость");
                btnMainRegisterPrint.setImage(null);
                btnMainRegisterPrint
                        .setStyle("height: 40px; width: 200px; background: #B0FFAD; font-size: 14px; font-weight: 700; color: #000000;");
                lblMainRegisterSignStatusInfo.setValue("Ведомость не подписана с помощью ЭЦП!");
                lblMainRegisterSignStatusInfo.setStyle("color: red; font-weight: 700;");
            } else {
                hboxMainRegisterBottomPanel.setVisible(false);
                lbMainRegister.onInitRender();
            }
        }

        lbMainRegister.setItemRenderer(new MainRegisterRenderer(mainRegister.isRegisterSigned(), ratingConsts,
                                                                FormOfControlConst.getName(curGroup.getSubject().getFormofcontrol()),
                                                                type, this::initMainRegisterData
        ));
        lbMainRegister.setModel(mainRegisterModel);
        lbMainRegister.renderAll();

        lblMainRegisterDate.setValue(DateConverter.convertDateToString(mainRegister.getCompletionDate()));
    }

    private void initMainRetakeRegisterData() {
        mainRetakeRegister = service
                .getMainRetakeRegister(curGroup.getIdLGSS(), FormOfControlConst.getName(curGroup.getSubject().getFormofcontrol()));

        if(mainRetakeRegister.getListRegisterRow().size() == 0) {
            return;
        }

        tabMainRetake.setVisible(true);

        if (mainRetakeRegister.getStartDate() != null) {
            lblMainRetakeDate.setValue("Сроки проведения: " + DateConverter.convertDateToString(mainRetakeRegister.getStartDate()) + " - " +
                                       DateConverter.convertDateToString(mainRetakeRegister.getFinishDate()));
        }

        if (mainRetakeRegister.getSignDate() != null) {
            lblMainRetakeDate.setValue("Дата подписания: " + DateConverter.convertDateToString(mainRetakeRegister.getSignDate()));
        }

        mainRetakeRegisterModel = new ListModelList<>(mainRetakeRegister.getListRegisterRow());

        if (mainRetakeRegister.isRegisterSigned()) {
            lblMainRetakeRegisterSignStatusInfo.setValue("Ведомость подписана с помощью ЭЦП.");
            lblMainRetakeRegisterSignStatusInfo.setStyle("color: black; font-weight: 700;");
            btnPrintMainRetakeRegister.setVisible(true);
            btnPrintMainRetakeRegister.setLabel("Печать ведомости");
            btnPrintMainRetakeRegister.setImage("/imgs/pdf.png");
            btnPrintMainRetakeRegister.setStyle("height: 40px; width: 200px; font-size: 14px; font-weight: 700; color: #000000;");
        } else {
            if (!mainRetakeRegister.getListRegisterRow().isEmpty()) {
                cmbRatingMainRetakeRegister.setVisible(true);
                btnPrintMainRetakeRegister.setVisible(true);
                btnEnterRatingMainRetakeRegister.setVisible(true);
                btnPrintStudentListMainRetakeRegister.setVisible(true);

                mainRetakeRegisterModel.setMultiple(true);
                lbMainRetakeRegister.setMultiple(true);
                lbMainRetakeRegister.setCheckmark(true);

                btnPrintMainRetakeRegister.setLabel("Подписать ведомость");
                btnPrintMainRetakeRegister.setImage(null);
                btnPrintMainRetakeRegister
                        .setStyle("height: 40px; width: 200px; background: #B0FFAD; font-size: 14px; font-weight: 700; color: #000000;");
                lblMainRetakeRegisterSignStatusInfo.setValue("Ведомость не подписана с помощью ЭЦП!");
                lblMainRetakeRegisterSignStatusInfo.setStyle("color: red; font-weight: 700;");
            }
        }

        lbMainRetakeRegister.setItemRenderer(
                new MainRetakeRegisterRenderer(FormOfControlConst.getName(curGroup.getSubject().getFormofcontrol()),
                                               mainRetakeRegister.isRegisterSigned(), this::initMainRetakeRegisterData, type, ratingConsts
                ));
        lbMainRetakeRegister.setModel(mainRetakeRegisterModel);
        lbMainRetakeRegister.renderAll();
    }

    private void initIndividualRetakeRegisterData() {
        listIndividualRegisters = service
                .getListIndividualRegisters(curGroup.getIdLGSS(), FormOfControlConst.getName(curGroup.getSubject().getFormofcontrol()));

        indivRetakeRegisterModel = new ListModelList<>(listIndividualRegisters);

        lbIndivRetakeRegister.setModel(indivRetakeRegisterModel);
        lbIndivRetakeRegister.setItemRenderer(
                new IndivRetakeRegisterRenderer(FormOfControlConst.getName(curGroup.getSubject().getFormofcontrol()), type, curGroup,
                                                this::initIndividualRetakeRegisterData, this::openIndividualRegister, ratingConsts
                ));
        lbIndivRetakeRegister.renderAll();
    }

    private void appendComboitem(Combobox cmb, String label, String style, RatingConst rating) {
        Comboitem item = new Comboitem(label);
        item.setValue(rating);
        item.setStyle(style);
        cmb.appendChild(item);
    }

    /**
     * Метод группового ввода оценки основной ведомости.
     */
    @Listen("onClick = #btnEnterRatingMainRegister")
    public void setRatingForSelectedStudentsMainRegister() {
        try {
            if (cmbRatingMainRegister.getValue() != null) {
                if (lbMainRegister.getSelectedCount() != 0) {
                    mainRegisterModel.getSelection().stream().filter(el -> !el.getAcademicLeave() && !el.getDeducted()).forEach(el -> {
                        // TODO add to service
                        if (null != el.getIdSRH()) {
                            service.updateSRHDateAndRating(el.getIdSRH(),
                                                           ((RatingConst) cmbRatingMainRegister.getSelectedItem().getValue()).getRating()
                            );
                            el.setChangeDateTime(new Date());
                            el.setMark(((RatingConst) cmbRatingMainRegister.getSelectedItem().getValue()).getRating());
                            log.info("Запись оценки " +el.getMark()+ " для студента " +el.getStudentFullName()+ " преподавателем " + currentUser.getShortFIO() + " при групповом вводе оценок в основную ведомость прошла успешно.");

                        } else {
                            long idSRH = service
                                    .createSRH(foc == FormOfControlConst.EXAM, foc == FormOfControlConst.PASS, foc == FormOfControlConst.CP,
                                               foc == FormOfControlConst.CW, foc == FormOfControlConst.PRACTIC, mainRegister.getType(),
                                               "0.0.0", ((RatingConst) cmbRatingMainRegister.getSelectedItem().getValue()).getRating(),
                                               el.getIdSR(), idSystemUser, RegisterConst.TYPE_MAIN_NOT_SIGNED

                                    );

                            el.setChangeDateTime(new Date());
                            el.setMark(((RatingConst) cmbRatingMainRegister.getSelectedItem().getValue()).getRating());
                            el.setRetakeCount(RegisterConst.TYPE_MAIN_NOT_SIGNED);
                            el.setIdSRH(idSRH);

                                   //TODO обработка ошибки в случае не успешного занесения оценки в бд
                        }
                    });

                    lbMainRegister.setModel(mainRegisterModel);
                    lbMainRegister.renderAll();
                    lbMainRegister.clearSelection();
                    mainRegisterModel.clearSelection();
                } else {
                    Messagebox
                            .show("Вы не выбрали ни одной записи в списке студентов.", "Внимание!", Messagebox.OK, Messagebox.EXCLAMATION);
                }
            } else {
                Messagebox.show("Укажите оценку.", "Внимание!", Messagebox.OK, Messagebox.EXCLAMATION);
                cmbRatingMainRegister.focus();
            }
        } catch (Exception e) {
            Messagebox.show("Ошибка ввода оценки. Обратитесь к администратору.", "Ошибка!", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
            }
    }

    /**
     * Метод группового ввода оценки ведомости общей пересдачи.
     */
    @Listen("onClick = #btnEnterRatingMainRetakeRegister")
    public void setRatingForSelectedStudentsMainRetakeRegister() {
        try {
            if (cmbRatingMainRetakeRegister.getValue() != null) {
                if (lbMainRetakeRegister.getSelectedCount() != 0) {
                    mainRetakeRegisterModel.getSelection().stream()
                                           .filter(el -> !el.getAcademicLeave() && !el.getDeducted() && null != el.getIdSRH())
                                           .forEach(el -> {
                                               // TODO add to service
                                               el.setMark(((RatingConst) cmbRatingMainRetakeRegister.getSelectedItem().getValue())
                                                                  .getRating());
                                               el.setChangeDateTime(new Date());
                                               log.info("Запись оценки " +el.getMark()+ " для студента " +el.getStudentFullName()+ " преподавателем " + currentUser.getShortFIO() + " при групповом вводе оценок в ведомость общей пересдачи прошла успешно.");
                                               service.updateSRHDateAndRating(el.getIdSRH(),
                                                                              ((RatingConst) cmbRatingMainRetakeRegister.getSelectedItem()
                                                                                                                        .getValue())
                                                                                      .getRating()
                                               );
                                           });

                    lbMainRetakeRegister.setModel(mainRetakeRegisterModel);
                    lbMainRetakeRegister.renderAll();
                    lbMainRetakeRegister.clearSelection();
                    mainRetakeRegisterModel.clearSelection();

                } else {
                    Messagebox
                            .show("Вы не выбрали ни одной записи в списке студентов.", "Внимание!", Messagebox.OK, Messagebox.EXCLAMATION);
                }
            } else {
                Messagebox.show("Укажите оценку.", "Внимание!", Messagebox.OK, Messagebox.EXCLAMATION);
                cmbRatingMainRegister.focus();
            }
        } catch (Exception e) {
            Messagebox.show("Ошибка ввода оценки. Обратитесь к администратору.", "Ошибка!", Messagebox.OK, Messagebox.ERROR);

        }
    }

    /**
     * Печать основной ведомости.
     */
    @Listen("onClick = #btnMainRegisterPrint")
    public void printMainRegister() {
        if (mainRegister.isRegisterSigned()) {
            openPdf(mainRegister);
        } else {
            openRegister(RegisterType.MAIN, null);
        }
    }

    /**
     * Печать ведомости общей пересдачи.
     */
    @Listen("onClick = #btnPrintMainRetakeRegister, #btnMainRetakeRegisterSign")
    public void printMainRetakeRegister() {
        if (mainRetakeRegister.isRegisterSigned()) {
            openPdf(mainRetakeRegister);
        } else {
            openRegister(RegisterType.MAIN_RETAKE, null);
        }
    }

    public void openIndividualRegister(RegisterModel registerModel) {
        openRegister(RegisterType.INDIVIDUAL_RETAKE, registerModel);
    }

    public void openRegister(RegisterType registerType, RegisterModel individualRegister) {
        RegisterModel registerModel = null;

        switch (registerType) {
            case INDIVIDUAL_RETAKE:
                registerModel = individualRegister;
                break;
            case MAIN_RETAKE:
                registerModel = mainRetakeRegister;
                break;
            case MAIN:
                registerModel = mainRegister;
                break;
        }

        for (RegisterRowModel rating : registerModel.getListRegisterRow()) {
            if (rating.getMark() == null || rating.getMark() == 0) {
                Messagebox.show("Необходимо заполнить все поля с оценками!", "Предупреждение!", Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
        }

        if (registerModel.getRegisterNumber() == null || registerModel.getRegisterNumber().equals("")) {
            if (!service.setRegisterNumber(registerModel, template.getCurrentUser().getFio(), registerType)) {
                Messagebox.show("Не удалось открыть ведомость", "Ошибка", Messagebox.OK, Messagebox.ERROR);
                return;
            } else {
                init(registerType);

                // у основной ведомости изначально нет сущности register, из-за этого мы после создания register перезагружаем данные,
                // для получения id_register и локальной переменной указываем на новый подгруженный объект
                if(registerType == RegisterType.MAIN) {
                    registerModel = mainRegister;
                }
            }
        }

        Runnable updateRegisterUI = null;

        switch (registerType) {
            case MAIN:
                updateRegisterUI = this::initMainRegisterData;
                break;
            case MAIN_RETAKE:
                updateRegisterUI = this::initMainRetakeRegisterData;
                break;
            case INDIVIDUAL_RETAKE:
                updateRegisterUI = this::initIndividualRetakeRegisterData;
                break;
        }

        new JasperReportService().getJasperReportRegister(updateRegisterUI, registerModel.getIdRegisterESO(),
                                                          curGroup.getSubject().getSemester().getIdInstitute(),
                                                          template.getCurrentUser().getIdHum(), registerModel.getIdSemester(),
                                                          foc.getValue(), registerType.getNotSignRetakeCount(), registerType
        ).showPdf();
    }

    private void openPdf(RegisterModel individualRating) {
        Map arg = new HashMap();
        arg.put(PdfViewerCtrl.FILE,
                new RegisterServiceImpl().getFileRegister(individualRating.getRegisterURL(), individualRating.getIdRegisterESO())
        );
        ComponentHelper.createWindow("/utility/pdfViewer/index.zul", "winPdfViewer", arg).doModal();
    }

    @Listen("onClick = #btnOpenRegisterRequest")
    public void openRequestPage() {
        Map arg = new HashMap();

        arg.put(WinRegisterCtrl.SELECTED_GROUP, curGroup);
        arg.put(WinRegisterCtrl.FORM_CONTROL, foc.getValue());

        ComponentHelper.createWindow("/teacher/winRegisterRequest.zul", "winRequest", arg).doModal();
    }

    /**
     * Печать списка студентов основной ведомости.
     */
    @Listen("onClick = #btnStudentListMainRegisterPrint")
    public void printStudentListMainRegister() {
        new JasperReportService().getJasperReportRegisterWithoutMarks(foc.getValue(), curGroup, mainRegister).showPdf();
    }
}
