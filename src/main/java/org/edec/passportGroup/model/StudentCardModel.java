package org.edec.passportGroup.model;

import java.util.List;

/**
 * Created by ilyabaikalow on 09.02.18.
 */
public class StudentCardModel extends StudentModel {
    private Long idLgs, idSC;
    private List<SubjectCardModel> subjectNameList;
    private List<SemesterCardModel> semesterList;
    private String idRecordBook;

    public Long getIdLgs () {
        return idLgs;
    }

    public void setIdLgs (Long idLgs) {
        this.idLgs = idLgs;
    }

    public String getIdRecordBook () {
        return idRecordBook;
    }

    public void setIdRecordBook (String idRecordBook) {
        this.idRecordBook = idRecordBook;
    }

    public Long getIdSC () {
        return idSC;
    }

    public void setIdSC (Long idSC) {
        this.idSC = idSC;
    }

    public List<SubjectCardModel> getSubjectNameList () {
        return subjectNameList;
    }

    public void setSubjectNameList (List<SubjectCardModel> subjectNameList) {
        this.subjectNameList = subjectNameList;
    }

    public List<SemesterCardModel> getSemesterList () {
        return semesterList;
    }

    public void setSemesterList (List<SemesterCardModel> semesterList) {
        this.semesterList = semesterList;
    }
}
