package org.edec.studyLoad.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AssignmentModel {

    public String fio;
    public String nameDiscipline;
    public String typeInstructionString;
    public String groupName;
    public String typeControl;
    public String assignment;

    public Integer numberStudents;
    public Integer course;
    public Integer is_exam;
    public Integer is_pass;
    public Integer is_courseproject;
    public Integer is_coursework;
    public Integer is_practic;
    public Integer typeInstructionInt;
    public Integer hoursCount;
    public Integer hourSaudCount;
    public Integer id_link_group_semester;
    public Integer id_link_employee_subject_group;

    public String getTypeInstructionString() {
        if (typeInstructionInt == 0)
            typeInstructionString = "Лекция";
        else if (typeInstructionInt == 1)
            typeInstructionString = "Практика";
        else if (typeInstructionInt.equals(3))
            typeInstructionString = "Лабораторная";
        return typeInstructionString;
    }


    public String getTypeControl() {
        if (is_exam.equals(1))
            typeControl = "Экзамен";
        else if (is_pass.equals(1))
            typeControl = "Зачёт";
        else if (is_courseproject.equals(1))
            typeControl = "Курсовой проект";
        else if (is_coursework.equals(1))
            typeControl = "Курсовая работа";
        else if (is_practic.equals(1))
            typeControl = "Практика";

        return typeControl;
    }

    public String getFioWithShortInitials() {
        String fioSplit[] = fio.split(" ");
        return fioSplit[0] + " " + fioSplit[1].charAt(0) + ". " + fioSplit[2].charAt(0) + ".";
    }

}
