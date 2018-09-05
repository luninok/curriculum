package org.edec.register.model;

import java.util.HashSet;

public class SubjectModel {

    private Long idSubject, idLgss, idGroup;
    private String groupName, subjectName;
    private HashSet<String> teachers;
    private Integer foc, type, course;

    public Long getIdSubject () {
        return idSubject;
    }

    public void setIdSubject (Long idSubject) {
        this.idSubject = idSubject;
    }

    public String getGroupName () {
        return groupName;
    }

    public void setGroupName (String groupName) {
        this.groupName = groupName;
    }

    public String getSubjectName () {
        return subjectName;
    }

    public void setSubjectName (String subjectName) {
        this.subjectName = subjectName;
    }

    public HashSet<String> getTeachers () {
        return teachers;
    }

    public void setTeachers (HashSet<String> teachers) {
        this.teachers = teachers;
    }

    public Integer getFoc () {
        return foc;
    }

    public void setFoc (Integer foc) {
        this.foc = foc;
    }

    public Long getIdGroup () {
        return idGroup;
    }

    public void setIdGroup (Long idGroup) {
        this.idGroup = idGroup;
    }

    public Long getIdLgss () {
        return idLgss;
    }

    public void setIdLgss (Long idLgss) {
        this.idLgss = idLgss;
    }

    public Integer getType () {
        return type;
    }

    public void setType (Integer type) {
        this.type = type;
    }

    public Integer getCourse () {
        return course;
    }

    public void setCourse (Integer course) {
        this.course = course;
    }
}
