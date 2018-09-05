package org.edec.student.journalOfAttendance.ctrl;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.model.AttendanceModel;
import org.edec.model.GroupSemesterModel;
import org.edec.schedule.model.GroupSubjectLesson;
import org.edec.schedule.service.AttendanceService;
import org.edec.schedule.service.impl.AttendanceImpl;
import org.edec.student.journalOfAttendance.model.MonthAttendSubjectsModel;
import org.edec.student.journalOfAttendance.model.StudentAttendanceModel;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Button btnPrevMonth, btnNextMonth, btnRefreshData, btnDeleteAttendance;
    @Wire
    private Label lCurrentMonth, lCurrentDate, lBeginSem, lEndSem;
    @Wire
    private Listbox lbSubject;
    @Wire
    private Vbox vbCalendar;

    private ComponentService componentService = new ComponentServiceESOimpl();
    private JournalOfAttendanceService journalOfAttendanceService = new JournalOfAttendanceServiceImpl();
    private TemplatePageCtrl template = new TemplatePageCtrl();

    private Date today = new Date();
    private Date chooseMonth;
    private Integer firstWeekSem = 1;
    private GroupSemesterModel currentGroup;
    private MonthAttendSubjectsModel todayAttendance;
    private MonthAttendSubjectsModel selectedDay;
    private List<GroupSubjectLesson> lessons;
    private List<MonthAttendSubjectsModel> monthAttendance;
    private List<StudentAttendanceModel> students;

    protected void fill() {
        AttendanceService attendanceService;
        try {
            attendanceService = new AttendanceImpl();
        } catch (IOException e) {
            e.printStackTrace();
            PopupUtil.showError("Беда с сайтом расписания, обратитесь к администраторам");
            return;
        }
        template.setVisitedModuleByHum(template.getCurrentUser().getIdHum(), currentModule.getIdModule());
        currentGroup = journalOfAttendanceService.getGroupSemesterByHum(template.getCurrentUser().getIdHum());

        lBeginSem.setValue("Начало семестра: " + (currentGroup.getDateOfBeginSemester() != null ? DateConverter.convertDateToString(currentGroup.getDateOfBeginSemester()) : ""));
        lEndSem.setValue("Окончание семестра: " + (currentGroup.getDateOfEndSemester() != null ? DateConverter.convertDateToString(currentGroup.getDateOfEndSemester()) : ""));
        students = journalOfAttendanceService.getStudentByGroup(currentGroup.getIdLGS());
        lessons = attendanceService.getLessonsFromDb(currentGroup.getGroupname());

        if (today.before(currentGroup.getDateOfBeginSemester())) chooseMonth = currentGroup.getDateOfEndSemester();
        else if (today.after(currentGroup.getDateOfEndSemester())) chooseMonth = currentGroup.getDateOfEndSemester();
        else chooseMonth = today;


        Clients.showBusy("Загрузка данных");
        Events.echoEvent("onLater", vbCalendar, null);
        Events.echoEvent("onFirstRun", vbCalendar, null);
    }

    @Listen("onFirstRun = #vbCalendar")
    public void onFirstRunFillSubject() {
        if (todayAttendance != null) {
            selectedDay = todayAttendance;
            refreshJournal(todayAttendance);
        } else {
            if (monthAttendance.size() > 0) {
                selectedDay = monthAttendance.get(monthAttendance.size() - 1);
                refreshJournal(monthAttendance.get(monthAttendance.size() - 1));
            }
        }
    }

    @Listen("onLater = #vbCalendar")
    public void onLaterRefreshCalendar() {
        refreshCalendar();
        Clients.clearBusy();
    }

    @Listen("onLater = #lbSubject")
    public void laterOnLbSubject() {
        refreshJournal(selectedDay);
        Clients.clearBusy(lbSubject);
    }

    @Listen("onClick = #btnRefreshData")
    public void refreshData() {
        for (StudentAttendanceModel student : students) {
            for (AttendanceModel attendance : student.getAttendances()) {
                if (attendance.getAttend() == null) {
                    Messagebox.show("Заполните расписание у всех студентов", "Ошибка", Messagebox.OK, Messagebox.ERROR);
                    return;
                }
            }
        }
        for (StudentAttendanceModel student : students) {
            for (AttendanceModel attendance : student.getAttendances()) {
                if (attendance.getIdAttendance() == null) journalOfAttendanceService.createAttendance(attendance);
                else journalOfAttendanceService.updateAttendance(attendance);
            }
        }
        Clients.showBusy("Обновление данных");
        Events.echoEvent("onLater", lbSubject, null);
        Clients.showBusy("Обновление данных");
        Events.echoEvent("onLater", vbCalendar, null);
    }

    @Listen("onClick = #btnDeleteAttendance")
    public void deleteAttendanceByDay() {
        journalOfAttendanceService.deleteAttendanceByDate(selectedDay, currentGroup.getIdLGS());
        Clients.showBusy("Обновление данных");
        Events.echoEvent("onLater", lbSubject, null);
        Clients.showBusy("Обновление данных");
        Events.echoEvent("onLater", vbCalendar, null);
    }

    @Listen("onClick = #btnPrevMonth")
    public void choosePrevMonth() {
        chooseMonth = DateConverter.changeDateByMonth(chooseMonth, Calendar.MONTH, -1);
        Clients.showBusy("Загрузка данных");
        Events.echoEvent("onLater", vbCalendar, null);
    }

    @Listen("onClick = #btnNextMonth")
    public void chooseNextMonth() {
        chooseMonth = DateConverter.changeDateByMonth(chooseMonth, Calendar.MONTH, 1);
        Clients.showBusy("Загрузка данных");
        Events.echoEvent("onLater", vbCalendar, null);
    }

    private void refreshCalendar() {
        vbCalendar.getChildren().clear();
        Date dateBegin = DateConverter.getFirstDateOfMonthByCalendar(chooseMonth);
        btnPrevMonth.setDisabled(dateBegin.before(currentGroup.getDateOfBeginSemester()));
        Date dateEnd = DateConverter.getLastDateOfMonthByCalendar(chooseMonth);
        btnNextMonth.setDisabled(dateEnd.after(currentGroup.getDateOfEndSemester()));

        lCurrentMonth.setValue(DateConverter.getMonthByDate(chooseMonth));
        Hbox hboxForFillWeekDay = componentService.createHboxForFillWeekDay();
        hboxForFillWeekDay.setParent(vbCalendar);

        monthAttendance = journalOfAttendanceService.getMonthAttendModel(currentGroup.getIdLGS(), dateBegin, dateEnd);
        journalOfAttendanceService.fillMonthAttendance(monthAttendance, firstWeekSem, lessons);
        int count = 0;
        for (MonthAttendSubjectsModel monthAttend : monthAttendance) {
            if (count == 0) {
                Hbox hboxWithFlex = componentService.createHboxWithFlex();
                hboxWithFlex.setParent(vbCalendar);
            }
            count++;
            fillHboxDay((Hbox) vbCalendar.getChildren().get(vbCalendar.getChildren().size() - 1), monthAttend);
            if (count == 7) count = 0;
        }
    }

    private void refreshJournal(MonthAttendSubjectsModel attendDay) {
        try {
            if (attendDay.getLessons().size() == 0 || attendDay.getDay().after(today) || attendDay.getDay().before(currentGroup.getDateOfBeginSemester())
                    || attendDay.getDay().after(currentGroup.getDateOfEndSemester())) {
                btnRefreshData.setDisabled(true);
                btnDeleteAttendance.setDisabled(true);
            } else {
                btnRefreshData.setDisabled(false);
                btnDeleteAttendance.setDisabled(false);
            }
        } catch (NullPointerException e) {
            System.out.println("Проблема journalAttendance с " + currentGroup.getGroupname() + " " + template.getCurrentUser().getFio());
            throw e;
        }

        lCurrentDate.setValue("Посещаемость за '" + DateConverter.convertDateToString(attendDay.getDay()) + "'");
        List<AttendanceModel> subjectsAttendance = journalOfAttendanceService.getSubjectAttendanceByLGSandDate(currentGroup.getIdLGS(), attendDay.getDay());
        journalOfAttendanceService.fillStudentByAttendance(students, subjectsAttendance, currentGroup.getIdLGS(), attendDay.getDay());
        if (subjectsAttendance.size() == 0 && attendDay.getLessons().size() != 0) {
            for (int i = 0; i < attendDay.getLessons().size(); i++) {
                AttendanceModel attendance = new AttendanceModel();
                GroupSubjectLesson lesson = attendDay.getLessons().get(i);
                attendance.setLesson(lesson.getLesson());
                attendance.setIdLGSS(lesson.getIdLGSS());
                attendance.setPos(i + 1);
                attendance.setSubjectname(lesson.getSubjectName());
                attendance.setVisitDate(attendDay.getDay());
                subjectsAttendance.add(attendance);
            }
        }
        fillStudents(subjectsAttendance, attendDay);
    }

    private void fillStudents(List<AttendanceModel> subjectsAttendance, MonthAttendSubjectsModel attendDay) {
        lbSubject.getItems().clear();
        while (lbSubject.getListhead().getChildren().size() > 2)
            lbSubject.getListhead().getChildren().remove(2);
        int count = 0;
        for (StudentAttendanceModel student : students) {
            if (count == 0) {
                for (AttendanceModel attendance : subjectsAttendance) {
                    Listheader lhr = new Listheader();
                    lhr.setParent(lbSubject.getListhead());
                    Label lHeader = new Label(attendance.getSubjectname());
                    lHeader.setParent(lhr);
                    lHeader.setSclass("cwf-listheader-label");
                    lhr.setHflex("1");
                    lhr.setTooltiptext(attendance.getSubjectname());
                    lhr.setAlign("center");
                    lhr.setStyle("cursor: pointer;");
                    lhr.addEventListener(Events.ON_CLICK, event -> {
                        for (AttendanceModel studentAttend : student.getAttendances()) {
                            //Ищем предмет у студента, проверяем его значение и в зависимости от того
                            //какое значение у него значение меняет aatendance
                            if (studentAttend.getSubjectname().equals(attendance.getSubjectname())
                                    && studentAttend.getPos().equals(attendance.getPos())) {
                                Integer attend = 0;
                                if (studentAttend.getAttend() == null || studentAttend.getAttend() == 3) attend = 0;
                                else if (studentAttend.getAttend() == 0) attend = 1;
                                else if (studentAttend.getAttend() == 1) attend = 2;
                                else if (studentAttend.getAttend() == 2) attend = 3;
                                for (StudentAttendanceModel studentTmp : students) {
                                    for (AttendanceModel attendanceStudent : studentTmp.getAttendances()) {
                                        if (attendanceStudent.getSubjectname().equals(attendance.getSubjectname()) &&
                                                attendanceStudent.getPos().equals(attendance.getPos())) {
                                            attendanceStudent.setAttend(attend);
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                        fillStudents(subjectsAttendance, attendDay);
                    });
                }
            }
            ++count;
            if (student.getAttendances().size() == 0) {
                for (int i = 0; i < attendDay.getLessons().size(); i++) {
                    AttendanceModel attendance = new AttendanceModel();
                    GroupSubjectLesson lesson = attendDay.getLessons().get(i);
                    attendance.setLesson(lesson.getLesson());
                    attendance.setIdLGSS(lesson.getIdLGSS());
                    attendance.setIdSSS(student.getIdSSS());
                    attendance.setPos(i + 1);
                    attendance.setSubjectname(lesson.getSubjectName());
                    attendance.setVisitDate(attendDay.getDay());
                    student.getAttendances().add(attendance);
                }
            }
            Listitem li = new Listitem();
            li.setParent(lbSubject);
            new Listcell(String.valueOf(count)).setParent(li);
            Listcell lcFio = new Listcell(student.getFio());
            lcFio.setParent(li);
            lcFio.addEventListener(Events.ON_CLICK, event -> {
                while (li.getChildren().size() > 2)
                    li.getChildren().remove(2);
                Integer attend = 3;
                for (int i = 0; i < student.getAttendances().size(); i++) {
                    AttendanceModel attendance = student.getAttendances().get(i);
                    if (i == 0 && attendance.getAttend() != null) attend = attendance.getAttend();
                    if (attend == 0) attendance.setAttend(1);
                    else if (attend == 1) attendance.setAttend(2);
                    else if (attend == 2) attendance.setAttend(3);
                    else if (attend == 3) attendance.setAttend(0);
                }
                fillListitemByAttendnace(li, student);
            });
            fillListitemByAttendnace(li, student);
        }
    }

    private void fillListitemByAttendnace(Listitem li, StudentAttendanceModel student) {
        for (AttendanceModel attendance : student.getAttendances()) {
            Listcell lcAttend = new Listcell();
            lcAttend.setHeight("20px");
            if (attendance.getAttend() == null)
                lcAttend.setStyle("background: #ccc;");
            else if (attendance.getAttend() == 0) {
                lcAttend.setLabel("Н");
                lcAttend.setStyle("background: #ff9494;");
            } else if (attendance.getAttend() == 1) {
                lcAttend.setLabel("+");
                lcAttend.setStyle("background: #94db70;");
            } else if (attendance.getAttend() == 2) {
                lcAttend.setLabel("Э");
                lcAttend.setStyle("background: #4DE2F7;");
            } else if (attendance.getAttend() == 3) {
                lcAttend.setLabel("П");
                lcAttend.setStyle("background: #FFFE7E;");
            }
            lcAttend.addEventListener(Events.ON_CLICK, event -> {
                if (attendance.getAttend() == null || attendance.getAttend() == 3) {
                    attendance.setAttend(0);
                    lcAttend.setLabel("Н");
                    lcAttend.setStyle("background: #ff9494;");
                } else if (attendance.getAttend() == 0) {
                    attendance.setAttend(1);
                    lcAttend.setLabel("+");
                    lcAttend.setStyle("background: #94db70;");
                } else if (attendance.getAttend() == 1) {
                    attendance.setAttend(2);
                    lcAttend.setLabel("Э");
                    lcAttend.setStyle("background: #4DE2F7;");
                } else if (attendance.getAttend() == 2) {
                    attendance.setAttend(3);
                    lcAttend.setLabel("П");
                    lcAttend.setStyle("background: #FFFE7E;");
                }
            });
            lcAttend.setParent(li);
        }
    }

    private void fillHboxDay(Hbox hbox, MonthAttendSubjectsModel monthAttend) {
        Hbox hboxDay;
        if (today.getYear() == monthAttend.getDay().getYear() && today.getMonth() == monthAttend.getDay().getMonth() && today.getDate() == monthAttend.getDay().getDate()) {
            todayAttendance = monthAttend;
            hboxDay = componentService.createHboxWithLabel(DateConverter.convertDateToDayString(monthAttend.getDay()), "day today");
        } else
            hboxDay = componentService.createHboxWithLabel(DateConverter.convertDateToDayString(monthAttend.getDay()), "day");
        if (monthAttend.getLessons().size() == 0 || monthAttend.getDay().after(today)
                || monthAttend.getDay().before(currentGroup.getDateOfBeginSemester()) || monthAttend.getDay().after(currentGroup.getDateOfEndSemester())) {
            hboxDay.setStyle("background: #ccc;");
        } else if (monthAttend.getDay().before(today) && monthAttend.getCount() == 0) {
            hboxDay.setStyle("background: #ff9494;");
        }
        hboxDay.setParent(hbox);
        hboxDay.addEventListener(Events.ON_CLICK, event -> {
            Clients.showBusy(lbSubject, "Загрузка данных");
            selectedDay = monthAttend;
            Events.echoEvent("onLater", lbSubject, null);
        });
    }
}