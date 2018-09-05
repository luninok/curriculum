package org.edec.teacher.model.register;

import lombok.Data;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RegisterType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Data
public class RegisterModel {
    private FormOfControlConst foc;
    private RegisterType registerType;
    // диф зачет или не диф зачет
    private Integer type;
    private Integer course, synchStatus;
    private Double hoursCount;
    private Long idRegisterESO, idRegisterMine, idSemester;
    private String registerNumber, registerURL, signatoryTutor, certNumber, thumbPrint, subjectName, semesterName, groupName;
    private Date signDate, completionDate;
    // Даты пересдачи
    private Date startDate, finishDate;
    private Boolean isCanceled;
    private List<RegisterRowModel> listRegisterRow;

    public RegisterModel() {
        listRegisterRow = new ArrayList<>();
    }

    public boolean isRegisterSigned() {
        if(registerURL != null && !registerURL.equals("")) {
            return true;
        }

        for(RegisterRowModel rating : listRegisterRow) {
            if(rating.getRetakeCount() != null && rating.getRetakeCount() > 0) {
                return true;
            }

            // TODO если количество положительных retakeCount не совпадает с количеством студентов в ведомости - лог об ошибке!
        }

        return false;
    }

    public boolean isRetakeOutOfDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);

        if(finishDate != null && finishDate.before(cal.getTime())) {
            return true;
        }

        return false;
    }
}
