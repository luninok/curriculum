package org.edec.successful.service.impl;

import org.edec.successful.manager.SuccessfulEsoDAO;
import org.edec.successful.model.*;
import org.edec.successful.service.SuccessfulService;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.GovFinancedConst;
import org.edec.utility.constants.LevelConst;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SuccessfulServiceImpl implements SuccessfulService {

    @Override
    public List<RatingModel> getRatingByFilter (Long idInst, Long idSemester, Long idDepart, FormOfStudy fos, GovFinancedConst govFin,
                                                String levels, String groupName, Date lastDate, String courses, Long idChair) {
        SuccessfulEsoDAO seDAO = new SuccessfulEsoDAO();
        return seDAO.getRatingByFilter(idInst, idSemester, idDepart, fos, govFin, levels, groupName, lastDate, courses, idChair);
    }

    public List<GroupModel> getGroupsRating (List<StudentModel> students) {
        List<GroupModel> groups = new ArrayList<>();
        for (StudentModel student : students) {
            boolean findGroup = false;
            for (GroupModel group : groups) {
                if (group.getIdGroup().equals(student.getSubjects().get(0).getRatings().get(0).getIdGroup())) {
                    group.getStudents().add(student);
                    findGroup = true;
                    break;
                }
            }
            if (!findGroup) {
                GroupModel newGroup = new GroupModel();
                newGroup.setGroupname(student.getSubjects().get(0).getRatings().get(0).getGroupname());
                newGroup.setIdGroup(student.getSubjects().get(0).getRatings().get(0).getIdGroup());
                newGroup.getStudents().add(student);
                groups.add(newGroup);
            }
        }
        return groups;
    }

    public List<StudentModel> getStudentSubjectRating (List<RatingModel> ratings) {
        List<StudentModel> students = new ArrayList<>();
        for (RatingModel rating : ratings) {
            boolean findStudent = false;
            boolean findSubject = false;
            for (StudentModel student : students) {
                if (student.getIdStudentcard().equals(rating.getIdStudentcard())) {
                    findStudent = true;
                    for (SubjectModel subject : student.getSubjects()) {
                        if (subject.getIdSubject().equals(rating.getIdSubject()) && subject.getFoc().equals(rating.getFOC())) {
                            subject.getRatings().add(rating);
                            findSubject = true;
                            break;
                        }
                    }
                    if (!findSubject) {
                        SubjectModel newSubject = new SubjectModel();
                        newSubject.setFoc(rating.getFOC());
                        newSubject.setIdSubject(rating.getIdSubject());
                        newSubject.setSubjectName(rating.getSubjectName());
                        newSubject.getRatings().add(rating);
                        newSubject.setChairFulltitle(rating.getTChairFulltitle());
                        newSubject.setIdChair(rating.getTChairId());
                        student.getSubjects().add(newSubject);
                    }
                    break;
                }
            }
            if (!findStudent) {
                StudentModel newStudent = new StudentModel();
                newStudent.setFio(rating.getFio());
                newStudent.setIdStudent(rating.getIdStudent());
                newStudent.setIdStudentcard(rating.getIdStudentcard());
                newStudent.setIdHumanface(rating.getIdHumanface());

                SubjectModel newSubject = new SubjectModel();
                newSubject.setFoc(rating.getFOC());
                newSubject.setIdSubject(rating.getIdSubject());
                newSubject.setSubjectName(rating.getSubjectName());
                newSubject.getRatings().add(rating);
                newSubject.setChairFulltitle(rating.getTChairFulltitle());
                newSubject.setIdChair(rating.getTChairId());
                newStudent.getSubjects().add(newSubject);
                students.add(newStudent);
            }
        }
        return students;
    }

    public List<CourseModel> getCourseRating (List<GroupModel> groups) {
        List<CourseModel> courses = new ArrayList<>();
        for (GroupModel group : groups) {
            boolean findCourse = false;
            for (CourseModel cours : courses) {
                if (cours.getCourse().equals(group.getStudents().get(0).getSubjects().get(0).getRatings().get(0).getCourse())) {
                    cours.getGroups().add(group);
                    cours.setCount(cours.getCount() + group.getStudents().size());
                    findCourse = true;
                    break;
                }
            }
            if (!findCourse) {
                CourseModel newCourse = new CourseModel();
                newCourse.setCourse(group.getStudents().get(0).getSubjects().get(0).getRatings().get(0).getCourse());
                newCourse.getGroups().add(group);
                newCourse.setCount(group.getStudents().size());
                courses.add(newCourse);
            }
        }
        //System.out.println(courses.get(0).getGroups().size());
        //System.out.println(courses.get(0).getGroups().get(0).getStudents().size());
        //System.out.println(courses.get(0).getGroups().get(0).getStudents().get(0).getSubjects().get(0).getSubjectName());
        //System.out.println(courses.get(0).getGroups().get(0).getStudents().get(1).getSubjects().get(0).getSubjectName());
        return courses;
    }

    public List<DepartmentModel> getChairRating (List<GroupModel> groups) {
        List<DepartmentModel> departments = new ArrayList<>();
        for (GroupModel group : groups) {
            boolean findDep = false;
            for (DepartmentModel dep : departments) {
                if ((dep.getIdDepartment() == null && group.getStudents().get(0).getSubjects().get(0).getIdChair() == null) ||
                    (dep.getIdDepartment().equals(group.getStudents().get(0).getSubjects().get(0).getIdChair()))) {
                    dep.getGroups().add(group);
                    dep.setCount(dep.getCount() + group.getStudents().size());
                    findDep = true;
                    break;
                }
            }
            if (!findDep) {
                DepartmentModel newDepartment = new DepartmentModel();
                newDepartment.setIdDepartment(group.getStudents().get(0).getSubjects().get(0).getIdChair());
                newDepartment.setFulltitle(group.getStudents().get(0).getSubjects().get(0).getChairFulltitle());
                newDepartment.getGroups().add(group);
                newDepartment.setCount(group.getStudents().size());
                departments.add(newDepartment);
            }
        }
        return departments;
    }

    /**
     * Простите :(
     *
     * @param ratings
     * @return
     */
    @Override
    public List<CourseModel> fullRebuildRating (List<RatingModel> ratings) {
        return getCourseRating(getGroupsRating(getStudentSubjectRating(ratings)));
    }

    /**
     * Простите :(
     *
     * @param ratings
     * @return
     */
    @Override
    public List<DepartmentModel> fullRebuildRatingChair (List<RatingModel> ratings) {
        return getChairRating(getGroupsRating(getStudentSubjectRating(ratings)));
    }
}
