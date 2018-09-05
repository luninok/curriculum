package org.edec.teacher.model.commission;

import org.edec.teacher.model.SemesterModel;
import org.edec.utility.constants.FormOfControlConst;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class CommissionModel {
    private Date dateOfCommission;

    private Integer formOfControl;
    private Integer type;

    private Long idRC;
    private Long idReg;
    private Long idSem;

    private String certnumber;
    private String chairman;
    private String course;
    private String focStr;
    private String institute;
    private String regNumber;
    private String semesterStr;
    private String signatorytutor;
    private String subjectName;

    private SemesterModel semester;
    private List<StudentModel> students = new ArrayList<>();
    private List<EmployeeModel> employees = new ArrayList<>();
    private List<EmployeeModel> commissionStaff = new ArrayList<>();

    public CommissionModel () {
    }

    public Date getDateOfCommission () {
        return dateOfCommission;
    }

    public void setDateOfCommission (Date dateOfCommission) {
        this.dateOfCommission = dateOfCommission;
    }

    public Integer getFormOfControl () {
        return formOfControl;
    }

    public void setFormOfControl (Integer formOfControl) {
        this.formOfControl = formOfControl;
    }

    public Integer getType () {
        return type;
    }

    public void setType (Integer type) {
        this.type = type;
    }

    public Long getIdRC () {
        return idRC;
    }

    public void setIdRC (Long idRC) {
        this.idRC = idRC;
    }

    public Long getIdReg () {
        return idReg;
    }

    public void setIdReg (Long idReg) {
        this.idReg = idReg;
    }

    public Long getIdSem () {
        return idSem;
    }

    public void setIdSem (Long idSem) {
        this.idSem = idSem;
    }

    public String getInstitute () {
        return institute;
    }

    public void setInstitute (String institute) {
        this.institute = institute;
    }

    public String getCertnumber () {
        return certnumber;
    }

    public void setCertnumber (String certnumber) {
        this.certnumber = certnumber;
    }

    public String getCourse () {
        return course;
    }

    public void setCourse (String course) {
        this.course = course;
    }

    public String getFocStr () {
        return FormOfControlConst.getName(this.formOfControl).getName();
    }

    public void setFocStr (String focStr) {
        this.focStr = focStr;
    }

    public String getRegNumber () {
        return regNumber;
    }

    public void setRegNumber (String regNumber) {
        this.regNumber = regNumber;
    }

    public String getSemesterStr () {
        return semesterStr;
    }

    public void setSemesterStr (String semesterStr) {
        this.semesterStr = semesterStr;
    }

    public String getSignatorytutor () {
        return signatorytutor;
    }

    public void setSignatorytutor (String signatorytutor) {
        this.signatorytutor = signatorytutor;
    }

    public String getSubjectName () {
        return subjectName;
    }

    public void setSubjectName (String subjectName) {
        this.subjectName = subjectName;
    }

    public SemesterModel getSemester () {
        return semester;
    }

    public void setSemester (SemesterModel semester) {
        this.semester = semester;
    }

    public List<StudentModel> getStudents () {
        return students;
    }

    public void setStudents (List<StudentModel> students) {
        this.students = students;
    }

    public List<EmployeeModel> getEmployees () {
        return employees;
    }

    public void setEmployees (List<EmployeeModel> employees) {
        this.employees = employees;
    }

    public String getChairman () {
        return chairman;
    }

    public void setChairman (String chairman) {
        this.chairman = chairman;
    }

    public List<EmployeeModel> getCommissionStaff () {
        return commissionStaff;
    }

    public void setCommissionStaff (List<EmployeeModel> commissionStaff) {
        this.commissionStaff = commissionStaff;
    }

    /**
     * Подписан документ
     **/
    public Boolean isSigned () { return certnumber != null;}
}
