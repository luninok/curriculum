package org.edec.schedule.service.impl;

import lombok.Cleanup;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.edec.model.SemesterModel;
import org.edec.schedule.manager.EntityManagerAttendance;
import org.edec.schedule.model.DicDayLesson;
import org.edec.schedule.model.DicTimeLesson;
import org.edec.schedule.model.GroupSubjectLesson;
import org.edec.schedule.model.dao.GroupSubjectLessonEso;
import org.edec.schedule.model.synch.EfficiencyModel;
import org.edec.schedule.model.xls.DayOfWeekClasses;
import org.edec.schedule.model.xls.TimeClasses;
import org.edec.schedule.model.xls.WeekClasses;
import org.edec.schedule.service.AttendanceService;
import org.edec.utility.component.model.GroupModel;
import org.edec.utility.component.model.SubjectModel;
import org.edec.utility.httpclient.manager.HttpClient;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Max Dimukhametov
 */
@Log4j
public class AttendanceImpl implements AttendanceService {
    private EntityManagerAttendance emAttendance = new EntityManagerAttendance();

    private Workbook workbook1;
    private Workbook workbook2;
    private Workbook workbook3;
    private Workbook workbook4;
    private Workbook workbook5;
    private Workbook workbook1m;
    private Workbook workbook2m;

    private List<DicDayLesson> dicDayLessons;
    private List<DicTimeLesson> dicTimeLessons;

    public AttendanceImpl () throws IOException {
        dicDayLessons = emAttendance.getDays();
        dicTimeLessons = emAttendance.getTimes();
        String resultHtml = HttpClient.makeHttpRequest("http://edu.sfu-kras.ru/timetable.xls", HttpClient.GET, new ArrayList<>(), "");
        String regex = "href=\'(.*?)\'";
        Matcher matcher = Pattern.compile(regex).matcher(resultHtml);
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            if (matcher.group(1).contains("IKIT")) {
                result.add(matcher.group(1));
            }
        }
        @Cleanup InputStream inputStream1 = HttpClient.getFileByUrl(result.get(0));
        @Cleanup InputStream inputStream2 = HttpClient.getFileByUrl(result.get(1));
        @Cleanup InputStream inputStream3 = HttpClient.getFileByUrl(result.get(2));
        @Cleanup InputStream inputStream4 = HttpClient.getFileByUrl(result.get(3));
        @Cleanup InputStream inputStream5 = HttpClient.getFileByUrl(result.get(4));
        @Cleanup InputStream inputStream1m = HttpClient.getFileByUrl(result.get(5));
        @Cleanup InputStream inputStream2m = HttpClient.getFileByUrl(result.get(6));
        workbook1 = new HSSFWorkbook(inputStream1);
        workbook2 = new HSSFWorkbook(inputStream2);
        workbook3 = new HSSFWorkbook(inputStream3);
        workbook4 = new HSSFWorkbook(inputStream4);
        workbook5 = new HSSFWorkbook(inputStream5);
        workbook1m = new HSSFWorkbook(inputStream1m);
        workbook2m = new HSSFWorkbook(inputStream2m);
    }

    @Override
    public List<GroupSubjectLesson> getLessonsFromTimetable (String groupname, int course, int qualification) {
        groupname = groupname.toLowerCase();
        try {
            Workbook workbook = null;
            if (qualification == 3) {
                if (course == 1) {
                    workbook = workbook1m;
                } else {
                    workbook = workbook2m;
                }
            } else {
                if (course == 1) {
                    workbook = workbook1;
                } else if (course == 2) {
                    workbook = workbook2;
                } else if (course == 3) {
                    workbook = workbook3;
                } else if (course == 4) {
                    workbook = workbook4;
                } else if (course == 5) {
                    workbook = workbook5;
                }
            }

            if (workbook == null) {
                System.out.println("Файл не досутпен");
                return null;
            }

            Sheet sheet = workbook.getSheet("Расписание");
            Row rowGroup = sheet.getRow(6);
            Iterator<Cell> iterCellGroup = rowGroup.cellIterator();
            Cell selectedGroup = null;

            while (iterCellGroup.hasNext()) {
                Cell cell = iterCellGroup.next();
                if (cell.getStringCellValue() != null && cell.getStringCellValue().toLowerCase().contains(groupname.toLowerCase())) {
                    selectedGroup = cell;
                    break;
                }
            }

            //Костыль для 5 курса специалетета (не совпадал шаблон)
            if (selectedGroup == null) {
                rowGroup = sheet.getRow(11);
                iterCellGroup = rowGroup.cellIterator();
                while (iterCellGroup.hasNext()) {
                    Cell cell = iterCellGroup.next();
                    if (cell.getStringCellValue() != null && cell.getStringCellValue().toLowerCase().contains(groupname.toLowerCase())) {
                        selectedGroup = cell;
                        break;
                    }
                }
            }

            if (selectedGroup == null) {
                System.out.println("Группа в распинаие не нашлась");
                return null;
            }

            List<GroupSubjectLesson> groupSubjectLessons = new ArrayList<>();
            for (DayOfWeekClasses dayOfWeekClasses : getDayOfWeekClasses(sheet)) {
                String day = dayOfWeekClasses.getDayOfWeek().getStringCellValue().trim();
                for (TimeClasses timeClasses : dayOfWeekClasses.getTimeClasses()) {
                    String time = timeClasses.getTimeClasses().getStringCellValue().trim();
                    for (WeekClasses weekClasses : timeClasses.getWeekClasses()) {
                        String week = weekClasses.getWeek().getStringCellValue().trim();
                        String subject = sheet.getRow(weekClasses.getSubjectIndex())
                                              .getCell(selectedGroup.getColumnIndex())
                                              .getStringCellValue()
                                              .trim();
                        String teacher = sheet.getRow(weekClasses.getTeacherIndex())
                                              .getCell(selectedGroup.getColumnIndex())
                                              .getStringCellValue()
                                              .trim();
                        String room = sheet.getRow(weekClasses.getRoomIndex())
                                           .getCell(selectedGroup.getColumnIndex())
                                           .getStringCellValue()
                                           .trim();
                        if (!subject.equals("")) {
                            GroupSubjectLesson groupSubjectLesson = new GroupSubjectLesson();
                            groupSubjectLesson.setDicDayLesson(getDicDayLessonByName(day, dicDayLessons));
                            groupSubjectLesson.setDicTimeLesson(getDicTimeLessonByName(time, dicTimeLessons));
                            groupSubjectLesson.setRoom(room.contains("/") ? room.substring(room.indexOf("/") + 1) : room);
                            groupSubjectLesson.setSubjectName(subject);
                            groupSubjectLesson.setLesson(room.contains("лек."));
                            groupSubjectLesson.setTeacher(teacher);
                            groupSubjectLesson.setWeek(Integer.valueOf(week));
                            groupSubjectLessons.add(groupSubjectLesson);
                        }
                    }
                }
            }
            return groupSubjectLessons;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Не удалось найти расписание у группы '" + groupname + "' и причина: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<GroupSubjectLesson> getLessonsFromDb (String groupname) {
        List<GroupSubjectLessonEso> models = emAttendance.getModelLesson(groupname);
        List<GroupSubjectLesson> result = new ArrayList<>();
        for (GroupSubjectLessonEso model : models) {
            GroupSubjectLesson lesson = new GroupSubjectLesson();
            lesson.setIdLGSS(model.getIdLGSS());
            lesson.setWeek(model.getWeek());
            lesson.setLesson(model.getLesson());
            lesson.setTeacher(model.getTeacher());
            lesson.setRoom(model.getRoom());
            lesson.setSubjectName(model.getSubjectName());
            lesson.setIdLSCH(model.getIdLSCH());
            for (DicDayLesson dayLesson : dicDayLessons) {
                if (dayLesson.getName().equals(model.getDayName())) {
                    lesson.setDicDayLesson(dayLesson);
                    break;
                }
            }
            for (DicTimeLesson timeLesson : dicTimeLessons) {
                if (timeLesson.getTimeName().equals(model.getTimeName())) {
                    lesson.setDicTimeLesson(timeLesson);
                    break;
                }
            }
            result.add(lesson);
        }
        return result;
    }

    @Override
    public List<GroupModel> getGroupByInstAndFormOfStudy (Long idInst, Integer formOfStudy) {
        return emAttendance.getGroupsByInstAndFormOfStudy(idInst, formOfStudy);
    }

    @Override
    public List<SubjectModel> getSubjectsByGroupname (String groupname) {
        return emAttendance.getSubjectsByGroupname(groupname);
    }

    @Override
    public boolean createSchedule (Long idLGSS, Integer week, String room, String teacher, Long idDicDayLesson, Long idDicTimeLesson,
                                   Boolean lesson) {
        return emAttendance.addAttend(idLGSS, week, room, teacher, idDicDayLesson, idDicTimeLesson, lesson);
    }

    @Override
    public boolean deleteScheduleByGroup (String groupname) {
        return emAttendance.removeAllScheduleByGroupname(groupname);
    }

    @Override
    public boolean deleteScheduleById (Long idLSCH) {
        return emAttendance.removeScheduleById(idLSCH);
    }

    @Override
    public void generateForAllGroups (Long idInst, Integer formOfStudy) {
        List<GroupModel> groups = getGroupByInstAndFormOfStudy(idInst, formOfStudy);

        for (GroupModel group : groups) {
            deleteScheduleByGroup(group.getGroupname());
            List<SubjectModel> subjects = getSubjectsByGroupname(group.getGroupname());
            List<GroupSubjectLesson> lessonsTimetable = getLessonsFromTimetable(
                    group.getGroupname(), group.getCourse(), group.getQualification());
            if (lessonsTimetable == null) {
                System.out.println(group.getGroupname() + " расписание не нашлось");
                continue;
            }
            for (GroupSubjectLesson lesson : lessonsTimetable) {
                for (SubjectModel subject : subjects) {
                    if (subject.getSubjectname().equals(lesson.getSubjectName())) {
                        lesson.setIdLGSS(subject.getIdLGSS());
                        break;
                    }
                }
                if (lesson.getIdLGSS() != null) {
                    if (!createSchedule(
                            lesson.getIdLGSS(), lesson.getWeek(), lesson.getRoom(), lesson.getTeacher(),
                            lesson.getDicDayLesson().getIdDicDayLesson(), lesson.getDicTimeLesson().getIdDicTimeLesson(), lesson.getLesson()
                    )) {
                        System.out.println("Ну удалось создать расписание: " + group.getGroupname() + ", " + lesson.getSubjectName());
                    }
                }
            }
        }
    }

    private DicDayLesson getDicDayLessonByName (String name, List<DicDayLesson> dicDayLessons) {
        for (DicDayLesson dicDayLesson : dicDayLessons) {
            if (dicDayLesson.getName().equals(name)) {
                return dicDayLesson;
            }
        }
        return null;
    }

    private DicTimeLesson getDicTimeLessonByName (String name, List<DicTimeLesson> dicTimeLessons) {
        for (DicTimeLesson dicTimeLesson : dicTimeLessons) {
            if (dicTimeLesson.getTimeName().equals(name)) {
                return dicTimeLesson;
            }
        }
        return null;
    }

    private List<DayOfWeekClasses> getDayOfWeekClasses (Sheet sheet) {
        Iterator<Row> rowIterator = sheet.rowIterator();

        DayOfWeekClasses mondayClasses = new DayOfWeekClasses(), tuesdayClasses = new DayOfWeekClasses(), wednesdayClasses = new DayOfWeekClasses(), thursdayClasses = new DayOfWeekClasses(), fridayClasses = new DayOfWeekClasses(), saturdayClasses = new DayOfWeekClasses();
        boolean monday = false, tuesday = false, wednesday = false, thursday = false, friday = false, saturday = false;

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Cell cellDayOfWeek = row.getCell(0);
            if (cellDayOfWeek == null) {
                continue;
            }
            if (cellDayOfWeek.getStringCellValue() != null && cellDayOfWeek.getStringCellValue().trim().equals("Понедельник")) {
                monday = true;
                mondayClasses.setDayOfWeek(cellDayOfWeek);
            } else if (cellDayOfWeek.getStringCellValue() != null && cellDayOfWeek.getStringCellValue().trim().equals("Вторник")) {
                monday = false;
                tuesday = true;
                tuesdayClasses.setDayOfWeek(cellDayOfWeek);
            } else if (cellDayOfWeek.getStringCellValue() != null && cellDayOfWeek.getStringCellValue().trim().equals("Среда")) {
                tuesday = false;
                wednesday = true;
                wednesdayClasses.setDayOfWeek(cellDayOfWeek);
            } else if (cellDayOfWeek.getStringCellValue() != null && cellDayOfWeek.getStringCellValue().trim().equals("Четверг")) {
                wednesday = false;
                thursday = true;
                thursdayClasses.setDayOfWeek(cellDayOfWeek);
            } else if (cellDayOfWeek.getStringCellValue() != null && cellDayOfWeek.getStringCellValue().trim().equals("Пятница")) {
                thursday = false;
                friday = true;
                fridayClasses.setDayOfWeek(cellDayOfWeek);
            } else if (cellDayOfWeek.getStringCellValue() != null && cellDayOfWeek.getStringCellValue().trim().equals("Суббота")) {
                friday = false;
                saturday = true;
                saturdayClasses.setDayOfWeek(cellDayOfWeek);
            }

            Cell cellTime = row.getCell(1);

            if (monday && !cellTime.getStringCellValue().equals("")) {
                setTimeForDay(mondayClasses, cellTime);
            } else if (tuesday && !cellTime.getStringCellValue().equals("")) {
                setTimeForDay(tuesdayClasses, cellTime);
            } else if (wednesday && !cellTime.getStringCellValue().equals("")) {
                setTimeForDay(wednesdayClasses, cellTime);
            } else if (thursday && !cellTime.getStringCellValue().equals("")) {
                setTimeForDay(thursdayClasses, cellTime);
            } else if (friday && !cellTime.getStringCellValue().equals("")) {
                setTimeForDay(fridayClasses, cellTime);
            } else if (saturday && !cellTime.getStringCellValue().equals("")) {
                setTimeForDay(saturdayClasses, cellTime);
            }

            Cell cellWeek = row.getCell(2);

            if (monday && !cellWeek.getStringCellValue().equals("")) {
                setWeekForLastTimeByDay(mondayClasses, cellWeek);
            } else if (tuesday && !cellWeek.getStringCellValue().equals("")) {
                setWeekForLastTimeByDay(tuesdayClasses, cellWeek);
            } else if (wednesday && !cellWeek.getStringCellValue().equals("")) {
                setWeekForLastTimeByDay(wednesdayClasses, cellWeek);
            } else if (thursday && !cellWeek.getStringCellValue().equals("")) {
                setWeekForLastTimeByDay(thursdayClasses, cellWeek);
            } else if (friday && !cellWeek.getStringCellValue().equals("")) {
                setWeekForLastTimeByDay(fridayClasses, cellWeek);
            } else if (saturday && !cellWeek.getStringCellValue().equals("")) {
                setWeekForLastTimeByDay(saturdayClasses, cellWeek);
            }
        }

        List<DayOfWeekClasses> dayOfWeekClasses = new ArrayList<>();
        dayOfWeekClasses.add(mondayClasses);
        dayOfWeekClasses.add(tuesdayClasses);
        dayOfWeekClasses.add(wednesdayClasses);
        dayOfWeekClasses.add(thursdayClasses);
        dayOfWeekClasses.add(fridayClasses);
        dayOfWeekClasses.add(saturdayClasses);
        return dayOfWeekClasses;
    }

    private void setTimeForDay (DayOfWeekClasses dayOfWeekClasses, Cell cell) {
        TimeClasses time = new TimeClasses();
        time.setTimeClasses(cell);
        dayOfWeekClasses.getTimeClasses().add(time);
    }

    private void setWeekForLastTimeByDay (DayOfWeekClasses dayOfWeekClasses, Cell cell) {
        TimeClasses timeClasses = dayOfWeekClasses.getTimeClasses().get(dayOfWeekClasses.getTimeClasses().size() - 1);
        WeekClasses weekClasses = new WeekClasses();
        weekClasses.setWeek(cell);
        weekClasses.setSubjectIndex(cell.getRowIndex());
        weekClasses.setTeacherIndex(cell.getRowIndex() + 1);
        weekClasses.setRoomIndex(cell.getRowIndex() + 2);
        timeClasses.getWeekClasses().add(weekClasses);
    }
}