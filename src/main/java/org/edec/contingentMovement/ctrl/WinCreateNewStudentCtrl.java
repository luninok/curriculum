package org.edec.contingentMovement.ctrl;

import org.edec.contingentMovement.service.ContingentMovementService;
import org.edec.contingentMovement.service.impl.ContingentMovementImpl;
import org.edec.studentPassport.model.StudentStatusModel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

/**
 * Created by APogrebnikov
 */
public class WinCreateNewStudentCtrl extends SelectorComposer<Component> {
    public static final String MAIN_PAGE = "main_page";

    @Wire
    private Textbox tbFamily, tbName, tbPatronymic, tbRecordbook;
    @Wire
    private Radio rbMen, rbWomen;
    @Wire
    private Datebox dbDob;
    @Wire
    private Window winCreateNewStudent;

    private ContingentMovementService contingentMovementService = new ContingentMovementImpl();

    private StudentStatusModel createdStudentStatusModel;
    private WinRecoveryCtrl mainPage;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        mainPage = (WinRecoveryCtrl) Executions.getCurrent().getArg().get(MAIN_PAGE);
    }

    @Listen("onClick=#btnCreate")
    public void createStudent () {
        StudentStatusModel student = new StudentStatusModel();
        student.setFamily(tbFamily.getValue());
        student.setName(tbName.getValue());
        student.setPatronymic(tbPatronymic.getValue());
        student.setBirthday(dbDob.getValue());
        student.setRecordBook(tbRecordbook.getValue());
        student.setSex(rbMen.isChecked() ? 1 : 0);

        Long scId = contingentMovementService.createNewStudent(student);

        StudentStatusModel newStudent = contingentMovementService.getStudentSCid(scId);

        mainPage.setSelectedStudent(newStudent);

        winCreateNewStudent.detach();
    }

    @Listen("onClick=#btnCancel")
    public void closeWindow () {
        winCreateNewStudent.detach();
    }
}
