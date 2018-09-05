package org.edec.student.calendarOfEvents.ctrl;

import org.edec.model.AttendanceModel;
import org.edec.model.GroupSemesterModel;
import org.edec.schedule.model.GroupSubjectLesson;
import org.edec.schedule.service.AttendanceService;
import org.edec.schedule.service.impl.AttendanceImpl;
import org.edec.student.calendarOfEvents.model.MonthAttendStudentModel;
import org.edec.student.calendarOfEvents.service.CalOfEventService;
import org.edec.student.calendarOfEvents.service.impl.CalOfEventImpl;
import org.edec.student.journalOfAttendance.service.JournalOfAttendanceService;
import org.edec.student.journalOfAttendance.service.impl.JournalOfAttendanceServiceImpl;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class IndexPageCtrl extends CabinetSelector {
    @Wire
    private Button btnPrevMonth, btnNextMonth;
    @Wire
    private Label lSelectedDay, lCurrentMonth, lBeginSem, lEndSem;
    @Wire
    private Vbox vbCalendar, vbEvents;

    private AttendanceService attendanceService;
    private CalOfEventService calOfEventService = new CalOfEventImpl();
    private ComponentService componentService = new ComponentServiceESOimpl();
    private JournalOfAttendanceService journalOfAttendanceService = new JournalOfAttendanceServiceImpl();

    private Date chooseMonth;
    private Date today = new Date();
    private GroupSemesterModel currentGroup;
    private Integer firstWeekSem = 1;
    private Long idSSS;
    private MonthAttendStudentModel todayAttendance;
    private List<GroupSubjectLesson> lessons = new ArrayList<>();
    private List<MonthAttendStudentModel> monthAttendances;

    protected void fill() {
        try {
            attendanceService = new AttendanceImpl();
        } catch (IOException e) {
            e.printStackTrace();
            PopupUtil.showError("Проблема с сайтом расписания, обратитесь к администратору");
        }
        template.setVisitedModuleByHum(template.getCurrentUser().getIdHum(), currentModule.getIdModule());
        lessons = attendanceService.getLessonsFromDb(template.getCurrentUser().getGroupname());
        currentGroup = journalOfAttendanceService.getGroupSemesterByHum(template.getCurrentUser().getIdHum());
        idSSS = calOfEventService.getIdSSSbyHum(template.getCurrentUser().getIdHum());

        if (today.before(currentGroup.getDateOfBeginSemester())) chooseMonth = currentGroup.getDateOfEndSemester();
        else if (today.after(currentGroup.getDateOfEndSemester())) chooseMonth = currentGroup.getDateOfEndSemester();
        else chooseMonth = today;

        Clients.showBusy("Загрузка данных");
        Events.echoEvent("onLater", vbCalendar, null);
        Events.echoEvent("onFirstRun", vbCalendar, null);
    }

    @Listen("onLater = #vbCalendar")
    public void onLaterRefreshCalendar() {
        refreshCalendar();
        Clients.clearBusy();
    }

    @Listen("onFirstRun = #vbCalendar")
    public void onFirstRun() {
        if (todayAttendance != null) {
            selectDay(todayAttendance);
        } else {
            selectDay(monthAttendances.get(monthAttendances.size() - 1));
        }
    }

    @Listen("onClick = #btnPrevMonth")
    public void changeOnPrevMonth() {
        chooseMonth = DateConverter.changeDateByMonth(chooseMonth, Calendar.MONTH, -1);
        Clients.showBusy("Загрузка данных");
        Events.echoEvent("onLater", vbCalendar, null);
    }

    @Listen("onClick = #btnNextMonth")
    public void changeOnNextMonth() {
        chooseMonth = DateConverter.changeDateByMonth(chooseMonth, Calendar.MONTH, 1);
        Clients.showBusy("Загрузка данных");
        Events.echoEvent("onLater", vbCalendar, null);
    }

    public void refreshCalendar() {
        while (vbCalendar.getChildren().size() > 1)
            vbCalendar.getChildren().remove(1);
        Date dateBegin = DateConverter.getFirstDateOfMonthByCalendar(chooseMonth);
        btnPrevMonth.setDisabled(dateBegin.before(currentGroup.getDateOfBeginSemester()));
        Date dateEnd = DateConverter.getLastDateOfMonthByCalendar(chooseMonth);
        btnNextMonth.setDisabled(dateEnd.after(currentGroup.getDateOfEndSemester()));
        lCurrentMonth.setValue(DateConverter.getMonthByDate(chooseMonth));

        componentService.createHboxForFillWeekDay().setParent(vbCalendar);

        monthAttendances = calOfEventService.getAttendancesByMonth(idSSS, dateBegin, dateEnd);
        calOfEventService.fillAtendanceByLessons(monthAttendances, firstWeekSem, lessons);

        int count = 0;
        for (MonthAttendStudentModel monthAttendance : monthAttendances) {
            if (count == 0) componentService.createHboxWithFlex().setParent(vbCalendar);
            count++;
            fillHboxDay((Hbox) vbCalendar.getChildren().get(vbCalendar.getChildren().size() - 1), monthAttendance);
            if (count == 7)
                count = 0;
        }
    }

    private void fillHboxDay(Hbox hbox, MonthAttendStudentModel monthAttendance) {
        Hbox hboxDay;
        if (today.getYear() == monthAttendance.getDay().getYear() && today.getMonth() == monthAttendance.getDay().getMonth() && today.getDate() == monthAttendance.getDay().getDate()) {
            todayAttendance = monthAttendance;
            hboxDay = componentService.createHboxWithLabel(DateConverter.convertDateToDayString(monthAttendance.getDay()), "day today");
        } else
            hboxDay = componentService.createHboxWithLabel(DateConverter.convertDateToDayString(monthAttendance.getDay()), "day");

        if (monthAttendance.getDay().before(currentGroup.getDateOfBeginSemester()) || monthAttendance.getDay().after(currentGroup.getDateOfEndSemester())
                || (monthAttendance.getCount() == 0 && monthAttendance.getLessons().size() == 0))
            hboxDay.setStyle("background: #ccc;");

        if (monthAttendance.getDay().before(today) && monthAttendance.getCount() == 0 && monthAttendance.getLessons().size() > 0)
            hboxDay.setStyle("background: #ff9494;;");

        if (monthAttendance.getCount() > 0 && monthAttendance.getAttendCount().equals(monthAttendance.getCount()))
            hboxDay.setStyle("background: #95FF82;");
        else if (monthAttendance.getCount() > 0 && monthAttendance.getAttendCount() < monthAttendance.getCount())
            hboxDay.setStyle("background: #FFFE7E;");


        hboxDay.setParent(hbox);
        hboxDay.addEventListener(Events.ON_CLICK, event -> {
            selectDay(monthAttendance);
        });
    }

    private void selectDay(MonthAttendStudentModel selectedDay) {
        while (vbEvents.getChildren().size() > 1)
            vbEvents.getChildren().remove(1);
        if (selectedDay.getAttendances().size() == 0) calOfEventService.fillAttendanceBySelectedDay(selectedDay, idSSS);
        if (selectedDay.getAttendances().size() != 0) {
            lSelectedDay.setValue("Посещаемость за '" + (DateConverter.convertDateToString(selectedDay.getDay())) + "'");
            for (AttendanceModel attendance : selectedDay.getAttendances()) {
                String textAttend = attendance.getSubjectname();
                if (attendance.getAttend() == 1)
                    textAttend += " - (посетил занятие)";
                else if (attendance.getAttend() == 2)
                    textAttend += " - (работа в электронных курсах)";
                else if (attendance.getAttend() == 3)
                    textAttend += " - (преподаватель не пришел)";
                else
                    textAttend += " - (не посетил занятие)";
                Label label = new Label(textAttend);
                label.setParent(vbEvents);
                label.setStyle("font-size: 16pt;");
            }
        } else {
            lSelectedDay.setValue("Расписание на '" + (DateConverter.convertDateToString(selectedDay.getDay())) + "'");
            for (GroupSubjectLesson lesson : selectedDay.getLessons()) {
                Label label = new Label(lesson.getSubjectName() + " - " + lesson.getTeacher() + "(" + (lesson.getRoom()) + ")");
                label.setParent(vbEvents);
                label.setStyle("font-size: 16pt;");
            }
        }
    }
}
