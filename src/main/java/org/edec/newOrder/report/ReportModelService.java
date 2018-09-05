package org.edec.newOrder.report;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.newOrder.model.dao.EmployeeOrderEsoModel;
import org.edec.newOrder.model.report.*;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.constants.OrderStudentJSONConst;
import org.edec.utility.converter.DateConverter;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

// TODO: MUST BE REFACTORED
public class ReportModelService {
    public static final String MAIN_ORDER = "main_order";
    public static final String PREDICATE_FIO = "predicate_fio";
    public static final String PREDICATE_POST = "predicate_post";
    public static final String EMPLOYEES = "employee";

    public static final String FORM_OF_STUDY = "$FOS$";
    public static final String FIRST_DATE = "$DATE1$";

    private OrderReportMainModel main;

    private OrderReportDAO orderReportDAO = new OrderReportDAO();

    private Map<String, Object> getMainOrderMap (Long idOrder) {
        Map<String, Object> map = new HashMap<>();
        main = orderReportDAO.getMainOrderInfoByID(idOrder);

        List<EmployeeOrderEsoModel> employeesOrderModels = orderReportDAO.getEmployeesOrder(idOrder);
        List<OrderReportEmployeeModel> employees = new ArrayList<>();
        String predicatingfio = null, predicatingpost = null;
        for (EmployeeOrderEsoModel employee : employeesOrderModels) {
            if (employee.getSubquery() != null && !employee.getSubquery().equals("")) {
                if (!orderReportDAO.existsStudentsInOrderBySubquery(idOrder, employee.getSubquery())) {
                    continue;
                }
            }
            if (employee.getActionrule() == 0) {
                if (main.getFormOfStudyId().equals(employee.getFormofstudy())) {
                    main.setExecutorfio(employee.getFio());
                    main.setExecutortel(employee.getPost());
                }
            } else if (employee.getActionrule() == 1) {
                predicatingfio = employee.getFio();
                predicatingpost = employee.getPost();
            } else if (employee.getActionrule() == 2) {
                employees.add(new OrderReportEmployeeModel(employee.getFio(), employee.getPost()));
            }
        }
        map.put(MAIN_ORDER, main);
        map.put(PREDICATE_FIO, predicatingfio);
        map.put(PREDICATE_POST, predicatingpost);
        map.put(EMPLOYEES, employees);
        return map;
    }

    public JRBeanCollectionDataSource getBeanDataForAcademicOrder (Long idOrder) {
        Map mainOrderMap = getMainOrderMap(idOrder);
        OrderReportMainModel main = (OrderReportMainModel) mainOrderMap.get(MAIN_ORDER);

        List<OrderReportGroupModel> listGroup = new ArrayList<>();
        List<AcademicReportModel> academicList = orderReportDAO.getAcademicModel(idOrder);
        for (AcademicReportModel academic : academicList) {
            boolean addGroup = true;
            for (OrderReportGroupModel group : listGroup) {
                if (group.getGroupname().equals(academic.getGroupname())) {
                    setSection(academic, group);
                    addGroup = false;
                    break;
                }
            }
            if (addGroup) {
                OrderReportGroupModel newGroup = new OrderReportGroupModel();
                newGroup.setGroupname(academic.getGroupname());
                newGroup.setPredicatingfio((String) mainOrderMap.get(PREDICATE_FIO));
                newGroup.setPredicatingpost((String) mainOrderMap.get(PREDICATE_POST));
                newGroup.setEmployees((List<OrderReportEmployeeModel>) mainOrderMap.get(EMPLOYEES));
                setSection(academic, newGroup);
                listGroup.add(newGroup);
            }
        }
        main.setGroups(listGroup);

        List<OrderReportMainModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    public JRBeanCollectionDataSource getBeanDataForDeductionOrder (Long idOrder) {
        Map mainOrderMap = getMainOrderMap(idOrder);
        OrderReportMainModel main = (OrderReportMainModel) mainOrderMap.get(MAIN_ORDER);
        List<OrderReportIndividualModel> listIndividual = orderReportDAO.getListIndividuals(idOrder);

        for (OrderReportIndividualModel student : listIndividual) {
            if (student.getAdditional() != null && !student.getAdditional().equals("")) {
                JSONObject additional = new JSONObject(student.getAdditional());

                if (additional.has(OrderStudentJSONConst.FOUNDATION)) {
                    student.setFoundation(additional.getString(OrderStudentJSONConst.FOUNDATION));
                }
            }
        }

        main.setIndividualsStudents(listIndividual);
        main.setPredicatingfio((String) mainOrderMap.get(PREDICATE_FIO));
        main.setPredicatingpost((String) mainOrderMap.get(PREDICATE_POST));
        main.setEmployees((List<OrderReportEmployeeModel>) mainOrderMap.get(EMPLOYEES));

        List<OrderReportMainModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    public JRBeanCollectionDataSource getBeanDataForSocialOrder (Long idOrder) {
        Map mainOrderMap = getMainOrderMap(idOrder);
        OrderReportMainModel main = (OrderReportMainModel) mainOrderMap.get(MAIN_ORDER);

        main.setPredicatingfio((String) mainOrderMap.get(PREDICATE_FIO));
        main.setPredicatingpost((String) mainOrderMap.get(PREDICATE_POST));
        main.setEmployees((List<OrderReportEmployeeModel>) mainOrderMap.get(EMPLOYEES));

        List<SocialReportModel> socialModels = orderReportDAO.getListSocial(idOrder);
        List<OrderReportSectionModel> sections = new ArrayList<>();

        for (SocialReportModel social : socialModels) {
            boolean addSection = true;
            for (OrderReportSectionModel section : sections) {
                if (section.getDescription().equals(social.getDescription())) {
                    setCourseForSection(section, social);
                    addSection = false;
                    break;
                }
            }
            if (addSection) {
                OrderReportSectionModel section = new OrderReportSectionModel();
                section.setDescription(social.getDescription());
                section.setFoundation(social.getFoundation());
                setCourseForSection(section, social);
                sections.add(section);
            }
        }
        main.setSections(sections);

        List<OrderReportMainModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    public JRBeanCollectionDataSource getBeanDataForSocialIncreasedOrder (Long idOrder) {
        Map mainOrderMap = getMainOrderMap(idOrder);
        OrderReportMainModel main = (OrderReportMainModel) mainOrderMap.get(MAIN_ORDER);

        main.setPredicatingfio((String) mainOrderMap.get(PREDICATE_FIO));
        main.setPredicatingpost((String) mainOrderMap.get(PREDICATE_POST));
        main.setEmployees((List<OrderReportEmployeeModel>) mainOrderMap.get(EMPLOYEES));

        List<SocialIncreaseReportModel> socialIncreaseModels = orderReportDAO.getListSocialIncrease(idOrder);
        List<OrderReportSectionModel> sections = new ArrayList<>();

        for (SocialIncreaseReportModel socialInc : socialIncreaseModels) {
            boolean addSection = true;
            for (OrderReportSectionModel section : sections) {
                if (section.getDescription().equals(socialInc.getDescription())) {
                    setSubsectionForSection(section, socialInc);
                    addSection = false;
                    break;
                }
            }
            if (addSection) {
                OrderReportSectionModel section = new OrderReportSectionModel();
                section.setDescription(socialInc.getDescription());
                section.setFoundation(socialInc.getFoundation());
                setSubsectionForSection(section, socialInc);
                sections.add(section);
            }
        }
        main.setSections(sections);
        for (OrderReportSectionModel section : main.getSections()) {
            if (section.getDescription().contains("Прекратить")) {
                main.setTypeorder(main.getTypeorder() + "\nО прекращении выплаты государственной социальной стипендии");
            }
        }

        List<OrderReportMainModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    public JRBeanCollectionDataSource getBeanDataForTransferOrder (Long idOrder) {
        Map mainOrderMap = getMainOrderMap(idOrder);
        main = (OrderReportMainModel) mainOrderMap.get(MAIN_ORDER);

        main.setPredicatingfio((String) mainOrderMap.get(PREDICATE_FIO));
        main.setPredicatingpost((String) mainOrderMap.get(PREDICATE_POST));
        main.setEmployees((List<OrderReportEmployeeModel>) mainOrderMap.get(EMPLOYEES));

        List<OrderReportSectionModel> sections = new ArrayList<>();
        List<TransferReportModel> transferModels = orderReportDAO.getTransferEsoModel(idOrder);

        for (TransferReportModel transfer : transferModels) {

            if (transfer.getAdditional() != null && !transfer.getAdditional().equals("")) {
                JSONObject json = new JSONObject(transfer.getAdditional());
                if (json.has(OrderStudentJSONConst.PREV_TRANSFER_ORDER_NUMBER) &&
                    json.has(OrderStudentJSONConst.DATE_SIGN_PREV_TRANSFER_ORDER)) {
                    Date datePrev = DateConverter.convertStringToDate(
                            json.getString(OrderStudentJSONConst.DATE_SIGN_PREV_TRANSFER_ORDER), "yyyy-MM-dd");
                    transfer.setPrevOrderDate(DateConverter.convertDateToString(datePrev));
                    transfer.setPrevOrderNum(json.getString(OrderStudentJSONConst.PREV_TRANSFER_ORDER_NUMBER));
                    transfer.setDescription(transfer.getDescription().replace("$prevNum$", transfer.getPrevOrderNum()));
                    transfer.setDescription(transfer.getDescription().replace("$prevDate$", transfer.getPrevOrderDate()));
                }
            }

            boolean addSection = true;
            for (OrderReportSectionModel section : sections) {
                if (section.getDescription().equals(transfer.getDescription())) {
                    setCourseForSection(transfer, section);
                    addSection = false;
                    break;
                }
            }

            if (addSection) {
                OrderReportSectionModel section = new OrderReportSectionModel();
                //FIXME: костыль
                section.setDescription(transfer.getDescription());
                section.setFoundation(transfer.getFoundation());
                setCourseForSection(transfer, section);
                sections.add(section);
            }
        }

        for (int i = 0; i < sections.size(); i++) {
            OrderReportSectionModel section = sections.get(i);
            OrderReportSectionModel nextSection = sections.size() != (i + 1) ? sections.get(i + 1) : null;

            if (nextSection == null) {
                continue;
            }
            if (section.getDescription().contains("уважител") && !nextSection.getDescription().contains("уважител")) {
                continue;
            }

            section.setFoundation(null);
        }

        main.setSections(sections);
        List<OrderReportMainModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    public JRBeanCollectionDataSource getBeanDataForSetElimination (Long idOrder, OrderRuleConst orderRuleConst) {
        Map mainOrderMap = getMainOrderMap(idOrder);

        if (orderRuleConst.equals(OrderRuleConst.SET_ELIMINATION_RESPECTFUL)) {
            main = getBeanDataForReportWithPaymentDesc(idOrder, false);
        } else {
            main = (OrderReportMainModel) mainOrderMap.get(MAIN_ORDER);

            List<OrderReportSectionModel> sections = new ArrayList<>();
            List<SetEliminationReportModel> setEliminationModels = orderReportDAO.getSetEliminationModel(idOrder);

            for (SetEliminationReportModel model : setEliminationModels) {
                boolean addSection = true;

                for (OrderReportSectionModel section : sections) {
                    if (section.getDescription().equals(model.getDescription())) {
                        setCourseForSection(model, section);
                        addSection = false;
                        break;
                    }
                }

                if (addSection) {
                    if (sections.size() > 0) {
                        if (sections.get(sections.size() - 1).getSubsections().get(1).getCourses().size() == 0) {
                            sections.get(sections.size() - 1).getSubsections().remove(1);
                        }

                        if (sections.get(sections.size() - 1).getSubsections().get(0).getCourses().size() == 0) {
                            sections.get(sections.size() - 1).getSubsections().remove(0);
                        }
                    }

                    OrderReportSectionModel section = new OrderReportSectionModel();
                    section.setDescription(model.getDescription());
                    section.setFoundation(model.getFoundation());
                    section.setSubsections(new ArrayList<>());

                    OrderReportSectionModel budgetSection = new OrderReportSectionModel();
                    budgetSection.setCourses(new ArrayList<>());
                    if (orderRuleConst.equals(OrderRuleConst.SET_ELIMINATION_RESPECTFUL)) {
                        budgetSection.setDescription(null);
                    } else {
                        budgetSection.setDescription("обучающимся за счет бюджетных ассигнований федерального бюджета:");
                    }
                    section.getSubsections().add(budgetSection);

                    OrderReportSectionModel paymentSection = new OrderReportSectionModel();
                    paymentSection.setCourses(new ArrayList<>());
                    paymentSection.setDescription("обучающимся на условиях договора об оказании платных образовательных услуг:");
                    section.getSubsections().add(paymentSection);

                    setCourseForSection(model, section);

                    sections.add(section);
                }
            }

            if (sections.size() > 0) {
                if (sections.get(sections.size() - 1).getSubsections().get(1).getCourses().size() == 0) {
                    sections.get(sections.size() - 1).getSubsections().remove(1);
                }

                if (sections.get(sections.size() - 1).getSubsections().get(0).getCourses().size() == 0) {
                    sections.get(sections.size() - 1).getSubsections().remove(0);
                }
            }

            main.setSections(sections);
        }

        main.setPredicatingfio((String) mainOrderMap.get(PREDICATE_FIO));
        main.setPredicatingpost((String) mainOrderMap.get(PREDICATE_POST));
        main.setEmployees((List<OrderReportEmployeeModel>) mainOrderMap.get(EMPLOYEES));

        List<OrderReportMainModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    public JRBeanCollectionDataSource getBeanDataForEliminationNote (Long idOrder) {
        main = getBeanDataForReportWithPaymentDesc(idOrder, true);

        List<OrderReportMainModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    public OrderReportMainModel getBeanDataForReportWithPaymentDesc (Long idOrder, Boolean isForNotion) {
        Map mainOrderMap = getMainOrderMap(idOrder);
        main = (OrderReportMainModel) mainOrderMap.get(MAIN_ORDER);

        List<OrderReportSectionModel> sections = new ArrayList<>();
        List<SetEliminationReportModel> setEliminationModels = orderReportDAO.getSetEliminationModel(idOrder);

        for (SetEliminationReportModel model : setEliminationModels) {
            boolean addSection = true;

            String description = "Прошу установить срок ликвидации академических задолженностей по итогам осеннего семестра " +
                                 (model.getBeginYear().getYear() + 1900) + "-" + (model.getEndYear().getYear() + 1900) +
                                 " учебного года до " + DateConverter.convertDateToString(model.getFirstDateStudent()) +
                                 "г. следующим студентам очной формы обучения, " +
                                 " обучающимся за счет бюджетных ассигнований федерального бюджета:";

            for (OrderReportSectionModel section : sections) {
                if (section.getDescription().equals(isForNotion ? description : model.getDescription())) {
                    setCourseForSection(model, section, model.getGovernmentFinanced() ? true : false);
                    addSection = false;
                    break;
                }
            }

            if (addSection) {
                OrderReportSectionModel section = new OrderReportSectionModel();
                section.setDescription(isForNotion ? description : model.getDescription());
                section.setFoundation(model.getFoundation());
                section.setSubsections(new ArrayList<>());
                setCourseForSection(model, section, model.getGovernmentFinanced() ? true : false);
                sections.add(section);
            }
        }

        for (int i = 0; i < sections.size(); i++) {
            OrderReportSectionModel section = sections.get(i);
            OrderReportSectionModel nextSection = sections.size() != (i + 1) ? sections.get(i + 1) : null;

            if (nextSection == null) {
                continue;
            }
            if (section.getDescription().contains("уважител") && !nextSection.getDescription().contains("уважител")) {
                continue;
            }

            section.setFoundation(null);
        }

        main.setSections(sections);

        return main;
    }

    private void setCourseForSection (SetEliminationReportModel model, OrderReportSectionModel section) {
        boolean addCourse = true;
        if (section.getSubsections().size() != 0) {
            if (model.getGovernmentFinanced()) {
                for (OrderReportCourseModel course : section.getSubsections().get(0).getCourses()) {
                    if (course.getCourse() - model.getCourse() == 0) {
                        setGroupForCourse(model, course);
                        addCourse = false;
                        break;
                    }
                }
            } else {
                for (OrderReportCourseModel course : section.getSubsections().get(1).getCourses()) {
                    if (course.getCourse() - model.getCourse() == 0) {
                        setGroupForCourse(model, course);
                        addCourse = false;
                        break;
                    }
                }
            }
        }

        if (addCourse) {
            OrderReportCourseModel course = new OrderReportCourseModel();
            course.setCourse(model.getCourse());

            course.setFullcourse("Курс " + model.getCourse());

            setGroupForCourse(model, course);

            if (model.getGovernmentFinanced()) {
                section.getSubsections().get(0).getCourses().add(course);
            } else {
                section.getSubsections().get(1).getCourses().add(course);
            }
        }
    }

    private void setCourseForSection (SetEliminationReportModel model, OrderReportSectionModel section, boolean governmentFinanced) {
        if (governmentFinanced) {
            boolean addCourse = true;
            for (OrderReportCourseModel course : section.getCourses()) {
                if (course.getCourse() - model.getCourse() == 0) {
                    setGroupForCourse(model, course);
                    addCourse = false;
                    break;
                }
            }
            if (addCourse) {
                OrderReportCourseModel course = new OrderReportCourseModel();
                course.setCourse(model.getCourse());

                course.setFullcourse("Курс " + model.getCourse());

                setGroupForCourse(model, course);
                section.getCourses().add(course);
            }
        } else {
            boolean addCourse = true;
            if (section.getSubsections().size() != 0) {
                for (OrderReportCourseModel course : section.getSubsections().get(0).getCourses()) {
                    if (course.getCourse() - model.getCourse() == 0) {
                        setGroupForCourse(model, course);
                        addCourse = false;
                        break;
                    }
                }
            }

            if (addCourse) {
                OrderReportCourseModel course = new OrderReportCourseModel();
                course.setCourse(model.getCourse());

                course.setFullcourse("Курс " + model.getCourse());

                setGroupForCourse(model, course);

                if (section.getSubsections().size() == 0) {
                    OrderReportSectionModel paymentSection = new OrderReportSectionModel();
                    paymentSection.setCourses(new ArrayList<>());
                    paymentSection.setDescription("обучающимся на условиях договора об оказании платных образовательных услуг:");
                    section.getSubsections().add(paymentSection);
                }

                section.getSubsections().get(0).getCourses().add(course);
            }
        }
    }

    private void setGroupForCourse (SetEliminationReportModel model, OrderReportCourseModel course) {
        boolean addGroup = true;
        for (OrderReportGroupModel group : course.getGroups()) {
            if (group.getGroupname().equals(model.getGroupname())) {
                setStudentForGroup(model, group);
                addGroup = false;
                break;
            }
        }
        if (addGroup) {
            OrderReportGroupModel group = new OrderReportGroupModel();
            group.setGroupname(model.getGroupname());
            setStudentForGroup(model, group);
            course.getGroups().add(group);
        }
    }

    private void setStudentForGroup (SetEliminationReportModel model, OrderReportGroupModel group) {
        OrderReportStudentModel student = new OrderReportStudentModel();
        student.setFio(model.getFio());
        student.setRecordbook(model.getRecordbook());
        group.getStudents().add(student);
    }

    private void setSubsectionForSection (OrderReportSectionModel section, SocialIncreaseReportModel socialInc) {
        boolean addSubSection = true;
        for (OrderReportSectionModel subSection : section.getSubsections()) {
            if (subSection.getDescription().equals(socialInc.getDescriptionDate())) {
                setCourseForSubSection(subSection, socialInc);
                addSubSection = false;
                break;
            }
        }
        if (addSubSection) {
            OrderReportSectionModel subSection = new OrderReportSectionModel();
            subSection.setDescription(socialInc.getDescriptionDate());
            setCourseForSubSection(subSection, socialInc);
            section.getSubsections().add(subSection);
        }
    }

    private void setCourseForSubSection (OrderReportSectionModel subSection, SocialIncreaseReportModel socialInc) {
        boolean addCourse = true;
        for (OrderReportCourseModel course : subSection.getCourses()) {
            if (course.getCourse() - socialInc.getCourse() == 0) {
                setGroupForCourse(course, socialInc);
                addCourse = false;
                break;
            }
        }
        if (addCourse) {
            OrderReportCourseModel course = new OrderReportCourseModel();
            course.setCourse(socialInc.getCourse());
            setGroupForCourse(course, socialInc);
            subSection.getCourses().add(course);
        }
    }

    private void setGroupForCourse (OrderReportCourseModel course, SocialIncreaseReportModel socialInc) {
        boolean addGroup = true;
        for (OrderReportGroupModel group : course.getGroups()) {
            if (group.getGroupname().equals(socialInc.getGroupname())) {
                setStudentForGroup(group, socialInc);
                addGroup = false;
                break;
            }
        }
        if (addGroup) {
            OrderReportGroupModel group = new OrderReportGroupModel();
            group.setGroupname(socialInc.getGroupname());
            setStudentForGroup(group, socialInc);
            course.getGroups().add(group);
        }
    }

    private void setStudentForGroup (OrderReportGroupModel group, SocialIncreaseReportModel socialInc) {
        OrderReportStudentModel student = new OrderReportStudentModel();
        student.setFio(socialInc.getFio());
        student.setRecordbook(socialInc.getRecordbook());
        group.getStudents().add(student);
    }

    private void setCourseForSection (OrderReportSectionModel section, SocialReportModel social) {
        boolean addCourse = true;
        for (OrderReportCourseModel course : section.getCourses()) {
            if (course.getCourse() - social.getCourse() == 0) {
                setGroupForCourse(course, social);
                addCourse = false;
                break;
            }
        }
        if (addCourse) {
            OrderReportCourseModel course = new OrderReportCourseModel();
            course.setCourse(social.getCourse());
            course.setFullcourse(social.getCourse() + " курс");
            setGroupForCourse(course, social);
            section.getCourses().add(course);
        }
    }

    private void setGroupForCourse (OrderReportCourseModel course, SocialReportModel social) {
        boolean addGroup = true;
        for (OrderReportGroupModel group : course.getGroups()) {
            if (group.getGroupname().equals(social.getGroupname())) {
                setStudentForGroup(group, social);
                addGroup = false;
                break;
            }
        }
        if (addGroup) {
            OrderReportGroupModel group = new OrderReportGroupModel();
            group.setGroupname(social.getGroupname());
            setStudentForGroup(group, social);
            course.getGroups().add(group);
        }
    }

    private void setStudentForGroup (OrderReportGroupModel group, SocialReportModel social) {
        OrderReportStudentModel student = new OrderReportStudentModel();
        student.setFio(social.getFio());
        student.setRecordbook(social.getRecordbook());
        group.getStudents().add(student);
    }

    private void setSection (AcademicReportModel academic, OrderReportGroupModel group) {
        boolean addSection = true;
        for (OrderReportSectionModel section : group.getSections()) {
            if (section.getProlongation() == academic.getProlongation()) {
                setSubsection(academic, section);
                addSection = false;
                break;
            }
        }
        if (addSection) {
            OrderReportSectionModel newSection = new OrderReportSectionModel();
            newSection.setDescription(academic.getDescription());
            newSection.setProlongation(academic.getProlongation());
            setSubsection(academic, newSection);
            group.getSections().add(newSection);
        }
    }

    private void setSubsection (AcademicReportModel academic, OrderReportSectionModel section) {
        boolean addSubSection = true;
        for (OrderReportSectionModel subSection : section.getSubsections()) {
            if (subSection.getDescription().equals(academic.getSubDescription())) {
                subSection.getStudents().add(new OrderReportStudentModel(academic.getFio(), academic.getRecordbook()));
                addSubSection = false;
                break;
            }
        }
        if (addSubSection) {
            OrderReportSectionModel newSubSection = new OrderReportSectionModel();
            newSubSection.setDescription(academic.getSubDescription());
            newSubSection.getStudents().add(new OrderReportStudentModel(academic.getFio(), academic.getRecordbook()));
            section.getSubsections().add(newSubSection);
        }
    }

    public JRBeanCollectionDataSource getBeanDataForServiceNote (Long idOrder, String desc) {
        Map mainOrderMap = getMainOrderMap(idOrder);
        main = (OrderReportMainModel) mainOrderMap.get(MAIN_ORDER);

        main.setPredicatingfio((String) mainOrderMap.get(PREDICATE_FIO));
        main.setPredicatingpost((String) mainOrderMap.get(PREDICATE_POST));
        main.setEmployees((List<OrderReportEmployeeModel>) mainOrderMap.get(EMPLOYEES));

        List<OrderReportSectionModel> sections = new ArrayList<>();
        List<TransferReportModel> transferModels = orderReportDAO.getTransferEsoModelForServiceNote(idOrder);

        for (TransferReportModel transfer : transferModels) {
            String formOfStudy = transfer.getDescription().contains("договор")
                                 ? "на условиях договора об оказании платных образовательных услуг"
                                 : "за счет бюджетных ассигнований федерального бюджета";
            transfer.setDescription(desc.replace(FORM_OF_STUDY, formOfStudy)
                                        .replace(FIRST_DATE, new SimpleDateFormat("dd.MM.yyyy").format(transfer.getFirstDateStudent())));

            boolean addSection = true;
            for (OrderReportSectionModel section : sections) {
                if (section.getDescription().equals(transfer.getDescription())) {
                    setCourseForSection(transfer, section);
                    addSection = false;
                    break;
                }
            }

            if (addSection) {
                OrderReportSectionModel section = new OrderReportSectionModel();
                section.setDescription(transfer.getDescription());
                section.setFoundation(transfer.getFoundation());
                setCourseForSection(transfer, section);
                sections.add(section);
            }
        }

        for (int i = 0; i < sections.size(); i++) {
            OrderReportSectionModel section = sections.get(i);
            OrderReportSectionModel nextSection = sections.size() != (i + 1) ? sections.get(i + 1) : null;

            if (nextSection == null) {
                continue;
            }
            if (section.getDescription().contains("уважител") && !nextSection.getDescription().contains("уважител")) {
                continue;
            }

            section.setFoundation(null);
        }

        main.setSections(sections);
        List<OrderReportMainModel> list = new ArrayList<>();
        list.add(main);
        return new JRBeanCollectionDataSource(list);
    }

    private void setCourseForSection (TransferReportModel transfer, OrderReportSectionModel section) {
        boolean addCourse = true;
        for (OrderReportCourseModel course : section.getCourses()) {
            if (course.getCourse() - transfer.getCourse() == 0) {
                setGroupForCourse(transfer, course);
                addCourse = false;
                break;
            }
        }
        if (addCourse) {
            OrderReportCourseModel course = new OrderReportCourseModel();
            course.setCourse(transfer.getCourse());

            if (main.getIdOrderRule().equals(OrderRuleConst.TRANSFER_PROLONGATION.getId())) {
                course.setFullcourse(transfer.getCourse() + " курс");
            }

            if (main.getIdOrderRule().equals(OrderRuleConst.TRANSFER_AFTER_TRANSFER_CONDITIONALLY_RESPECTFUL.getId()) ||
                main.getIdOrderRule().equals(OrderRuleConst.TRANSFER_AFTER_TRANSFER_CONDITIONALLY_NOT_RESPECTFUL.getId())) {
                course.setFullcourse(transfer.getCourse() + " курса");
            }

            setGroupForCourse(transfer, course);
            section.getCourses().add(course);
        }
    }

    private void setGroupForCourse (TransferReportModel transfer, OrderReportCourseModel course) {
        boolean addGroup = true;
        for (OrderReportGroupModel group : course.getGroups()) {
            if (group.getGroupname().equals(transfer.getGroupname())) {
                setStudentForGroup(transfer, group);
                addGroup = false;
                break;
            }
        }
        if (addGroup) {
            OrderReportGroupModel group = new OrderReportGroupModel();
            group.setGroupname(transfer.getGroupname());
            setStudentForGroup(transfer, group);
            course.getGroups().add(group);
        }
    }

    private void setStudentForGroup (TransferReportModel transfer, OrderReportGroupModel group) {
        OrderReportStudentModel student = new OrderReportStudentModel();
        student.setFio(transfer.getFio());
        student.setRecordbook(transfer.getRecordbook());
        group.getStudents().add(student);
    }
}
