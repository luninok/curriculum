package org.edec.teacher.ctrl.renderer.register;

import lombok.extern.log4j.Log4j;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.register.service.impl.RegisterServiceImpl;
import org.edec.teacher.ctrl.WinRegisterCtrl;
import org.edec.teacher.ctrl.listener.RatingClickListener;
import org.edec.teacher.ctrl.listener.ThemeEditListener;
import org.edec.teacher.model.GroupModel;
import org.edec.teacher.model.register.RatingModel;
import org.edec.teacher.model.register.RegisterModel;
import org.edec.teacher.model.register.RegisterRowModel;
import org.edec.teacher.service.impl.RegisterServiseImpl;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.constants.RegisterConst;
import org.edec.utility.constants.RegisterType;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.pdfViewer.ctrl.PdfViewerCtrl;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;
import java.util.function.Consumer;

/**
 * Created by antonskripacev on 25.02.17.
 */
@Log4j
public class IndivRetakeRegisterRenderer implements ListitemRenderer<RegisterModel> {
    private RegisterServiseImpl service = new RegisterServiseImpl();
    private TemplatePageCtrl template = new TemplatePageCtrl();

    private FormOfControlConst formOfCtrl;
    private int type;

    private GroupModel group;

    private Runnable updateRegisterUI;
    private Consumer<RegisterModel> openRegister;

    private List<RatingConst> listRatings;

    public IndivRetakeRegisterRenderer(FormOfControlConst formOfCtrl, int type, GroupModel group, Runnable updateRegisterUI, Consumer<RegisterModel> openRegister, List<RatingConst> listRatings) {
        this.formOfCtrl = formOfCtrl;
        this.type = type;
        this.group = group;
        this.updateRegisterUI = updateRegisterUI;
        this.openRegister = openRegister;
        this.listRatings = listRatings;
    }

    @Override
    public void render(Listitem item, final RegisterModel data, int index) throws Exception {
        boolean signedStatus = data.isRegisterSigned();
        boolean isOutOfDate = data.isRetakeOutOfDate();

        RegisterRowModel rating = data.getListRegisterRow().get(0);

        try {
            // Рендерим фио
            Listcell lc = new Listcell();
            Label lb = new Label();
            if (rating.getDeducted()) {
                lb.setValue(rating.getStudentFullName() + " (отчислен)");
                lb.setStyle("color: red;");
            } else if (rating.getAcademicLeave()) {
                lb.setValue(rating.getStudentFullName() + " (академ. отпуск)");
                lb.setStyle("color: red;");
            } else {
                lb.setValue(rating.getStudentFullName());
                lb.setStyle("color: #000000;");
            }
            lc.appendChild(lb);
            lc.setStyle("border-bottom: solid 1px #bcbcbc; border-right: solid 1px #bcbcbc; background: #ffffff; collor: black;");
            item.appendChild(lc);

            // Рендерим поле со сроком действия.
            lc = new Listcell();
            lb = new Label();
            if (!signedStatus && !isOutOfDate) {
                lb.setValue(DateConverter.convertDateToString(data.getStartDate()) + " - "
                            + DateConverter.convertDateToString(data.getFinishDate()));
                lb.setStyle("color: black;");
            } else if (!signedStatus) {
                lb.setValue("Просрочена");
                lb.setStyle("color: red;");
            } else if (data.getIdSemester() >= 47) {
                lb.setValue("Подписана с ЭЦП");
                lb.setStyle("color: black;");
            }

            lc.appendChild(lb);
            lc
                    .setStyle("border-bottom: solid 1px #bcbcbc; border-right: solid 1px #bcbcbc; background: #ffffff; text-align: center; width: 140px;");
            item.appendChild(lc);

            // Рендерим поле с оценкой
            lc = new Listcell();

            lb = new Label();

            if (rating.getMark() != null) {
                if (rating.getMark() == RatingConst.FAILED_TO_APPEAR.getRating()
                    || rating.getMark() == RatingConst.NOT_PASS.getRating()
                    || rating.getMark() == RatingConst.UNSATISFACTORILY.getRating()) {
                    lb.setStyle("color: red;");
                }
                if(rating.getMark() == 0) {
                    if(data.isRetakeOutOfDate()) {
                        lb.setValue("");
                    } else {
                        lb.setValue("Введите оценку.");
                    }
                } else {
                    lb.setValue(RatingConst.getDataByRating(rating.getMark()).getShortname());
                }
            } else {
                lb.setValue("");
            }

            lc.appendChild(lb);

            if (!signedStatus && !isOutOfDate) {
                lc.setStyle("border-bottom: solid 1px #bcbcbc; border-right: solid 1px #bcbcbc; background: #ffffff; text-align: center; width: 140px;");
                lc.addEventListener(Events.ON_CLICK, new RatingClickListener(lc, rating, formOfCtrl, RegisterConst.TYPE_RETAKE_INDIV_NOT_SIGNED, type, listRatings));

            } else if (signedStatus) {
                lc.setStyle("border-bottom: solid 1px #bcbcbc; border-right: solid 1px #bcbcbc; background: #B0FFAD; text-align: center; width: 140px;");
            } else {
                lc.setStyle("border-bottom: solid 1px #bcbcbc; border-right: solid 1px #bcbcbc; background: #ffffff; text-align: center; width: 140px;");
            }

            item.appendChild(lc);

            lc = new Listcell();
            if (!isOutOfDate && data.getIdSemester() >= 47 && !signedStatus) {
                Button btnPrint = new Button();
                btnPrint.setLabel("Подписать c ЭЦП");
                btnPrint.setStyle("font-weight: 700; height: 30px;");
                btnPrint.setStyle("background: #B0FFAD; font-weight: 700; height: 30px;");
                btnPrint.addEventListener(Events.ON_CLICK, event -> openRegister.accept(data));
                lc.appendChild(btnPrint);
            } else if (signedStatus) {
                Button btnPrint = new Button();
                btnPrint.setLabel("Печать");
                btnPrint.setStyle("font-weight: 700; height: 30px;");
                btnPrint.setImage("/imgs/pdf.png");
                btnPrint.addEventListener(Events.ON_CLICK, event -> {
                    Map arg = new HashMap();
                    arg.put(PdfViewerCtrl.FILE, new RegisterServiceImpl().getFileRegister(data.getRegisterURL(), data.getIdRegisterESO()));
                    ComponentHelper.createWindow("/utility/pdfViewer/index.zul", "winPdfViewer", arg).doModal();
                });
                lc.appendChild(btnPrint);
            }
            lc.setStyle("border-bottom: solid 1px #bcbcbc; background: #ffffff; text-align: center; width: 140px;");
            item.appendChild(lc);
            // Если форма контроля курсовой/курсовая, выводим колонку для внесения темы
            if ((formOfCtrl == FormOfControlConst.CP || formOfCtrl == FormOfControlConst.CW)) {
                lc = new Listcell();
                if (rating.getTheme() != null) {
                    lc.setImage("/imgs/okCLR.png");
                    lc.setHoverImage("/imgs/editBLACK.png");
                } else {
                    lc.setImage("/imgs/edit.png");
                    lc.setHoverImage("/imgs/editBLACK.png");
                }
                lc.addEventListener(Events.ON_CLICK, new ThemeEditListener(rating, formOfCtrl, updateRegisterUI, signedStatus));
                lc.setStyle("width: 50px; text-align: center; border-left: solid 1px #bcbcbc; border-bottom: solid 1px #bcbcbc; background: #ffffff;");
                item.appendChild(lc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
