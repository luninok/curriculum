package org.edec.studyLoad.ctrl.windowCtrl;

import org.edec.studyLoad.model.TeacherModel;
import org.edec.utility.zk.CabinetSelector;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;

public class WinRateDialogCtrl extends CabinetSelector {

    @Wire
    private Grid gridRate;

    @Wire
    Textbox tbLastName, tbFirstName, tbMiddleName;

    protected void fill() {
        fillTable();
    }

    private void fillTable() {


    }


    @Listen("onOK = #tbLastName; onOK = #tbFirstName; onOK = #tbtbMiddleName;")
    public void search() {


    }

    //  public static final String REGISTER_COMMISSION = "register_commission";

    //  private Grid gridRate;

    //  private TeacherModel teacher;

    //   @Override
    //   public void doAfterCompose (Component comp) throws Exception {
    //      super.doAfterCompose(comp);

    //      teacher = (TeacherModel) Executions.getCurrent().getArg().get(REGISTER_COMMISSION);
    //      Clients.showBusy(gridRate, "Загрузка данных");
    //       Events.echoEvent("onLater", gridRate, null);
    // teacher = (TeacherModel) Executions.getCurrent().getArg().get(REGISTER_COMMISSION);
    // Clients.showBusy(lbShowStudentCommission, "Загрузка данных");
    // Events.echoEvent("onLater", lbShowStudentCommission, null);
    //   }
    // public static final String ID_COMMISSION = "id_commission";

//    @Wire
    //  private Listbox lbCommissionStructure;

    //  private CommissionService commissionService = new CommissionServiceESOimpl();

    //  @Override
    // public void doAfterCompose (Component comp) throws Exception {
    //  super.doAfterCompose(comp);
    //  Long idComm = (Long) Executions.getCurrent().getArg().get(ID_COMMISSION);
    // lbCommissionStructure.setModel(new ListModelList<>(commissionService.getCommissionStructure(idComm)));
    //  lbCommissionStructure.renderAll();
    // }*/

}
