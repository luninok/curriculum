package org.edec.secretaryChair.service.impl;

import org.apache.commons.lang.time.DateUtils;
import org.edec.secretaryChair.manager.EntityManagerSecretaryChair;
import org.edec.secretaryChair.model.CommissionDayModel;
import org.edec.secretaryChair.model.CommissionModel;
import org.edec.secretaryChair.model.EmployeeModel;
import org.edec.secretaryChair.model.StudentCommissionModel;
import org.edec.secretaryChair.service.SecretaryChairService;
import org.edec.teacher.model.commission.StudentModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.converter.DateConverter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Max Dimukhametov
 */
public class SecretaryChairImpl implements SecretaryChairService {
    private EntityManagerSecretaryChair emSecretary = new EntityManagerSecretaryChair();

    @Override
    public List<CommissionModel> getCommission (Long idSem, Long idChair, Integer formOfStudy, boolean signed) {
        return emSecretary.getCommissionByChair(idSem, idChair, formOfStudy, signed);
    }

    @Override
    public List<CommissionDayModel> getInfoCommissionDays (CommissionModel commission) {
        List<Date> commissionPeriod = DateConverter.getDateRangeByTwoDates(commission.getDateBegin(), commission.getDateEnd());
        //Получаем все комиссии у студентов за тот же период, что и у выбранной комиссии
        List<CommissionModel> commissionByStudent = emSecretary.getCommissionByDate(commission.getId());
        List<CommissionDayModel> commissionDays = new ArrayList<>();
        for (Date date : commissionPeriod) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                continue;
            }
            CommissionDayModel commissionDay = new CommissionDayModel(date);
            commissionDays.add(commissionDay);
            List<CommissionModel> findCommissionsInDay = commissionByStudent.stream()
                                                                            //Ищем комиссии на текущий день
                                                                            .filter(commissionModel -> DateUtils.isSameDay(
                                                                                    date, commissionModel.getCommissionDate()))
                                                                            .collect(Collectors.toList());
            if (findCommissionsInDay.size() == 0) {
                continue;
            }
            //Группируем по предметам и получаем количество предметов в этот день
            commissionDay.setCountSubject(findCommissionsInDay.stream()
                                                              .collect(Collectors.groupingBy(CommissionModel::getIdSubject,
                                                                                             Collectors.counting()
                                                              ))
                                                              .size());

            commissionDay.setBusyTimes(findCommissionsInDay.stream()
                                                           .collect(Collectors.groupingBy(
                                                                   CommissionModel::getCommissionDate)) //Вытаскиваем уникальные commissionDate
                                                           .keySet() //т.к. результат мапа, то получаем set key и после этого сортируем
                                                           .stream()
                                                           .sorted()
                                                           .collect(Collectors.toList()));
        }
        return commissionDays;
    }

    /**
     * Получить свободный интервал. Список содержит два значения
     * 1 - минимально возможное время (может быть начало рабочего дня
     * или максимальная возможное время от предыдущей даты)
     * 2 - максимальное возможное время (может быть окончанием рабочего дня
     * или минимальное возможное время следующей комиссии)
     *
     * @return - список из двух переменные: минимальное и максимальное возможно время для комиссии
     */
    public Map<Integer, List<Date>> getFreeIntervalByDate (List<Date> dates) {
        if (dates == null || dates.size() == 0) {
            return null;
        }
        //Ограничения по рабочему дню dateMin = 8:30, dateMax = 21:00
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dates.get(0));

        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);
        Date dateMin = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 0);
        Date dateMax = calendar.getTime();

        Iterator<Date> iterator = dates.iterator();

        int count = 0;
        Map<Integer, List<Date>> mapDates = new HashMap<>();

        while (iterator.hasNext()) {
            Date date = iterator.next();
            if (!DateUtils.isSameDay(date, dateMin)) {
                calendar.setTime(date);
                calendar.set(Calendar.HOUR_OF_DAY, 8);
                calendar.set(Calendar.MINUTE, 30);
                dateMin = calendar.getTime();
            }

            //Интервал, когда нельзя назначать комиссию (+-2 часа от комиссии)
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, -2);
            Date curDateMin = calendar.getTime();

            calendar.add(Calendar.HOUR_OF_DAY, 4);
            Date curDateMax = calendar.getTime();

            if (DateConverter.getMinuteOfDay(dateMin) <= DateConverter.getMinuteOfDay(curDateMin)) {
                mapDates.put(++count, Arrays.asList(dateMin, curDateMin));
            }
            dateMin = curDateMax;
            if (!iterator.hasNext() && DateConverter.getMinuteOfDay(curDateMax) <= DateConverter.getMinuteOfDay(dateMax)) {
                mapDates.put(++count, Arrays.asList(curDateMax, dateMax));
            }
        }
        return mapDates;
    }

    @Override
    public List<SemesterModel> getSemesterByChair (Long idChair, Integer formOfStudy) {
        return emSecretary.getSemestersByIdChair(idChair, formOfStudy);
    }

    @Override
    public List<StudentModel> getStudentByCommission (Long idCommission) {
        return emSecretary.getStudentsByCommission(idCommission);
    }

    @Override
    public List<StudentCommissionModel> getStudentsForCheckFreeDate (Long idComm, Date dateComm) {
        List<StudentCommissionModel> studentsByDate = emSecretary.getStudentsForCheckFreeDate(idComm, dateComm, true);
        //Сначала проверяем, есть ли студенты, у которых количество комиссий больше 1 в этот день
        for (Map.Entry<Long, List<StudentCommissionModel>> studentEntry : studentsByDate.stream()
                                                                                        .collect(Collectors.groupingBy(
                                                                                                StudentCommissionModel::getIdStudentCard))
                                                                                        .entrySet()) {
            if (studentEntry.getValue().size() > 1) {
                return studentsByDate;
            }
        }
        //Если таких нет, то проверяем, есть ли комиссии в назначенное время
        studentsByDate = emSecretary.getStudentsForCheckFreeDate(idComm, dateComm, false);
        return studentsByDate;
    }

    @Override
    public List<EmployeeModel> getEmployeeByChair (Long idChair) {
        return emSecretary.getEmployeesByDepartment(idChair);
    }

    @Override
    public List<EmployeeModel> getEmployeeByCommission (Long idCommission) {
        return emSecretary.getCommissionEmployee(idCommission);
    }

    @Override
    public boolean updateCommissionInfo (Date dateCommission, String classroom, Long idCommission) {
        return emSecretary.updateCommissionInfo(dateCommission, classroom, idCommission);
    }

    @Override
    public boolean deleteCommissionStaff (Long idCommission) {
        return emSecretary.deleteCommissionStaff(idCommission);
    }

    @Override
    public boolean addCommissionStaff (EmployeeModel employee, Long idCommission) {
        return emSecretary.addCommissionStaff(employee, idCommission);
    }
}
