package org.edec.utility.report.model.register;

import java.util.ArrayList;
import java.util.List;

/**
 @author Max Dimukhametov
 */
public class RegisterJasperModel
{
    private Integer formOfControl;
    private Integer retakeCount;
    private Integer type;

    private String certnumber;
    private String course;
    private String coWorkOrProj;
    private String dateOfExamination;
    private String dateOfExaminationTitle;
    private String groupname;
    private String institute;
    private String major;
    private String marksCount;
    private String registerNumber = "";
    private String semester;
    private String signatorytutor;
    private String signdate;
    private String subject;
    private String teacher;
    private String totalHours;
    private String typeOfRegister;

    private List<StudentModel> students = new ArrayList<>();

    public RegisterJasperModel()
    {
    }

    public Integer getFormOfControl()
    {
        return formOfControl;
    }

    public void setFormOfControl(Integer formOfControl)
    {
        this.formOfControl = formOfControl;
    }

    public Integer getRetakeCount()
    {
        return retakeCount;
    }

    public void setRetakeCount(Integer retakeCount)
    {
        this.retakeCount = retakeCount;
    }

    public String getDateOfExamination()
    {
        return dateOfExamination;
    }

    public void setDateOfExamination(String dateOfExamination)
    {
        this.dateOfExamination = dateOfExamination;
    }

    public String getCourse()
    {
        return course;
    }

    public void setCourse(String course)
    {
        this.course = course;
    }

    public String getGroupname()
    {
        return groupname;
    }

    public void setGroupname(String groupname)
    {
        this.groupname = groupname;
    }

    public String getInstitute()
    {
        return institute;
    }

    public void setInstitute(String institute)
    {
        this.institute = institute;
    }

    public String getMajor()
    {
        return major;
    }

    public void setMajor(String major)
    {
        this.major = major;
    }

    public String getRegisterNumber()
    {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber)
    {
        this.registerNumber = registerNumber;
    }

    public String getSemester()
    {
        return semester;
    }

    public void setSemester(String semester)
    {
        this.semester = semester;
    }

    public String getSigndate()
    {
        return signdate;
    }

    public void setSigndate(String signdate)
    {
        this.signdate = signdate;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getTeacher()
    {
        return teacher;
    }

    public void setTeacher(String teacher)
    {
        this.teacher = teacher;
    }

    public String getTotalHours()
    {
        return totalHours;
    }

    public void setTotalHours(String totalHours)
    {
        this.totalHours = totalHours;
    }

    public List<StudentModel> getStudents()
    {
        return students;
    }

    public void setStudents(List<StudentModel> students)
    {
        this.students = students;
    }

    public String getCoWorkOrProj()
    {
        return coWorkOrProj;
    }

    public void setCoWorkOrProj(String coWorkOrProj)
    {
        this.coWorkOrProj = coWorkOrProj;
    }

    public String getTypeOfRegister()
    {
        return typeOfRegister;
    }

    public void setTypeOfRegister(String typeOfRegister)
    {
        this.typeOfRegister = typeOfRegister;
    }

    public String getCertnumber()
    {
        return certnumber;
    }

    public void setCertnumber(String certnumber)
    {
        this.certnumber = certnumber;
    }

    public String getDateOfExaminationTitle()
    {
        return dateOfExaminationTitle;
    }

    public void setDateOfExaminationTitle(String dateOfExaminationTitle)
    {
        this.dateOfExaminationTitle = dateOfExaminationTitle;
    }

    public String getMarksCount()
    {
        return marksCount;
    }

    public void setMarksCount(String marksCount)
    {
        this.marksCount = marksCount;
    }

    public String getSignatorytutor()
    {
        return signatorytutor;
    }

    public void setSignatorytutor(String signatorytutor)
    {
        this.signatorytutor = signatorytutor;
    }

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }
}