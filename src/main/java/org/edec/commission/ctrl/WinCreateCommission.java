package org.edec.commission.ctrl;

import org.apache.log4j.Logger;
import org.edec.commission.ctrl.renderer.SubjectCreateCommissionRenderer;
import org.edec.commission.model.SubjectDebtModel;
import org.edec.commission.model.comporator.SubjectDebtComporator;
import org.edec.commission.service.CommissionService;
import org.edec.commission.service.impl.CommissionServiceESOimpl;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by dmmax
 */
public class WinCreateCommission extends SelectorComposer<Component> {
    public static final String LIST_ID_SSS = "list_id_sss";
    public static final String DATE_BEGIN_COMMISSION = "date_begin_commission";
    public static final String DATE_END_COMMISSION = "date_end_commission";

    private static final Logger log = Logger.getLogger(WinCreateCommission.class.getName());

    @Wire
    private Listbox lbCreateCommission;

    @Wire
    private Listheader lhSubject, lhCountDebts, lhFoc, lhChair, lhSem;

    @Wire
    private Textbox tbCreateCommissionSearch;

    @Wire
    private Window winCreateCommission;

    private CommissionService commissionService = new CommissionServiceESOimpl();
    private TemplatePageCtrl template = new TemplatePageCtrl();

    String listIdSSS;
    private Date dateBegin, dateEnd;
    private List<SubjectDebtModel> subjects;

    private SubjectDebtComporator subjectDebtComp;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        listIdSSS = (String) Executions.getCurrent().getArg().get(LIST_ID_SSS);
        dateBegin = (Date) Executions.getCurrent().getArg().get(DATE_BEGIN_COMMISSION);
        dateEnd = (Date) Executions.getCurrent().getArg().get(DATE_END_COMMISSION);

        lhSubject.setSortAscending(new SubjectDebtComporator(SubjectDebtComporator.CompareMethods.BY_SUBJECT));
        lhSubject.setSortDescending(new SubjectDebtComporator(SubjectDebtComporator.CompareMethods.BY_SUBJECT_REV));
        lhCountDebts.setSortAscending(new SubjectDebtComporator(SubjectDebtComporator.CompareMethods.BY_COUNT_DEBTS));
        lhCountDebts.setSortDescending(new SubjectDebtComporator(SubjectDebtComporator.CompareMethods.BY_COUNT_DEBTS_REV));
        lhFoc.setSortAscending(new SubjectDebtComporator(SubjectDebtComporator.CompareMethods.BY_FOC));
        lhFoc.setSortDescending(new SubjectDebtComporator(SubjectDebtComporator.CompareMethods.BY_FOC_REV));
        lhChair.setSortAscending(new SubjectDebtComporator(SubjectDebtComporator.CompareMethods.BY_CHAIR));
        lhChair.setSortDescending(new SubjectDebtComporator(SubjectDebtComporator.CompareMethods.BY_CHAIR_REV));
        lhSem.setSortAscending(new SubjectDebtComporator(SubjectDebtComporator.CompareMethods.BY_SEM));
        lhSem.setSortDescending(new SubjectDebtComporator(SubjectDebtComporator.CompareMethods.BY_SEM_REV));

        lbCreateCommission.setItemRenderer(new SubjectCreateCommissionRenderer(this));

        callShowBusyForListbox("Загрузка данных из БД", null);
    }

    @Listen("onOK=#tbCreateCommissionSearch;onClick=#btnCreateCommissionSearch")
    public void searchByFilter() {
        callShowBusyForListbox("Загрузка данных из БД", null);
    }

    @Listen("onLater = #lbCreateCommission")
    public void laterOnCreateCommission(Event event) {
        List<SubjectDebtModel> tempList = initListboxSubject(tbCreateCommissionSearch.getValue());
        if (event.getData() != null)
            Collections.sort(tempList, (Comparator<? super SubjectDebtModel>) event.getData());
        ListModelList lmSubject = new ListModelList(tempList);
        lmSubject.setMultiple(true);
        lbCreateCommission.setModel(lmSubject);
        lbCreateCommission.renderAll();
        Clients.clearBusy(lbCreateCommission);
    }

    @Listen("onClick = #btnCreateCommission")
    public void createCommission() {
        if (lbCreateCommission.getSelectedCount() == 0) {
            PopupUtil.showWarning("Выберите один или несколько предметов");
            return;
        }
        for (Listitem li : lbCreateCommission.getSelectedItems()) {
            SubjectDebtModel subject = li.getValue();
            if (subject.isSigned())
                continue;
            if (commissionService.createCommonCommission(subject, subject.getStudents(), template.getCurrentUser().getIdHum())) {
                commissionService.setStatusSignedForSubjAndStudent(subjects.get(subjects.indexOf(subject)));
                log.info("CommissionModel created(" + DateConverter.convertDateToString(new Date()) + "):" + subject.getSubjectname() + "(" + subject.getFocStr() + ")");
            } else
                PopupUtil.showError("Не удалось создать комиссию по предмету: " + subject.getSubjectname() + " (" + subject.getFocStr() + "), " + subject.getSemesterStr());
        }
        callShowBusyForListbox("Загрузка данных из БД", null);
    }

    @Listen("onSort = #lhSubject")
    public void sortSubject() {
        callShowBusyForListbox("Сортировка", getComporatorByListhead(lhSubject));
    }

    @Listen("onSort = #lhChair")
    public void sortChair() {
        callShowBusyForListbox("Сортировка", getComporatorByListhead(lhChair));
    }

    @Listen("onSort = #lhCountDebts")
    public void sortCountDebts() {
        callShowBusyForListbox("Сортировка", getComporatorByListhead(lhCountDebts));
    }

    @Listen("onSort = #lhFoc")
    public void sortFoc() {
        callShowBusyForListbox("Сортировка", getComporatorByListhead(lhFoc));
    }

    @Listen("onSort = #lhSem")
    public void sortSem() {
        callShowBusyForListbox("Сортировка", getComporatorByListhead(lhSem));
    }

    public void changeStatusForSubject(SubjectDebtModel subject) {
        subjects.get(subjects.indexOf(subject)).setSigned(true);
        callShowBusyForListbox("Загрузка данных", null);
    }

    private SubjectDebtComporator getComporatorByListhead(Listheader lh) {
        if (lh.getSortDirection().equals("natural") || lh.getSortDirection().equals("descending"))
            return (SubjectDebtComporator) lh.getSortAscending();
        else
            return (SubjectDebtComporator) lh.getSortDescending();
    }

    private void callShowBusyForListbox(String msg, Object data) {
        Clients.showBusy(lbCreateCommission, msg);
        Events.echoEvent("onLater", lbCreateCommission, data);
    }

    private List<SubjectDebtModel> initListboxSubject(String filter) {
        if (subjects == null) subjects = commissionService.getSubjForCreateCommission(listIdSSS, dateBegin, dateEnd);
        if (filter.equals("")) return subjects;
        else return commissionService.getSubjectsByFilter(filter, subjects);
    }
}
