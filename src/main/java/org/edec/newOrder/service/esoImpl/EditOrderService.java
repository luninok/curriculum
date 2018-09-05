package org.edec.newOrder.service.esoImpl;

import org.edec.newOrder.manager.EditOrderManagerESO;
import org.edec.newOrder.model.addStudent.LinkOrderSectionEditModel;
import org.edec.newOrder.model.addStudent.SearchGroupModel;
import org.edec.newOrder.model.addStudent.SearchStudentModel;
import org.edec.newOrder.model.editOrder.GroupModel;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.model.editOrder.SectionModel;
import org.edec.newOrder.model.editOrder.StudentModel;
import org.edec.newOrder.model.dao.OrderModelESO;
import org.edec.studentPassport.model.dao.RatingEsoModel;
import org.edec.utility.component.model.RatingModel;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.constants.OrderStudentJSONConst;
import org.edec.utility.constants.OrderTypeConst;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class EditOrderService {
    private EditOrderManagerESO managerOrderESO = new EditOrderManagerESO();
    private SimpleDateFormat formatYYYY = new SimpleDateFormat("YYYY");

    public void fillOrderModel (OrderEditModel orderModel) {
        List<OrderModelESO> listModelsESO = managerOrderESO.getListOrderModelESO(orderModel.getIdOrder().longValue());

        switch (OrderTypeConst.getByType(orderModel.getOrderType())) {
            case ACADEMIC:
                if (OrderRuleConst.getById(orderModel.getIdOrderRule()).equals(OrderRuleConst.CANCEL_ACADEMICAL_SCHOLARSHIP_IN_SESSION)) {
                    groupOrderBySections(orderModel, listModelsESO);
                } else {
                    groupOrderByGroups(orderModel, listModelsESO);
                }
                break;
            case DEDUCTION:
                groupOrderBySections(orderModel, listModelsESO);
                break;
            case SOCIAL:
                groupOrderBySections(orderModel, listModelsESO);
                break;
            case SOCIAL_INCREASED:
                groupOrderBySections(orderModel, listModelsESO);
                break;
            case TRANSFER:
                groupOrderBySections(orderModel, listModelsESO);
                break;
            case SET_ELIMINATION_DEBTS:
                groupOrderBySections(orderModel, listModelsESO);
                break;
        }
    }

    private void groupOrderByGroups (OrderEditModel orderModel, List<OrderModelESO> listModelsESO) {
        orderModel.setGroups(new ArrayList<>());
        for (OrderModelESO item : listModelsESO) {
            GroupModel groupToAdd = new GroupModel();
            SectionModel sectionToAdd = new SectionModel();

            boolean isExistGroupInModel = false;
            for (GroupModel group : orderModel.getGroups()) {
                if (group.getName().equals(item.getGroupname())) {
                    isExistGroupInModel = true;
                    groupToAdd = group;
                }
            }

            if (!isExistGroupInModel) {
                groupToAdd = new GroupModel();
                fillGroupInformation(groupToAdd, item);
                orderModel.getGroups().add(groupToAdd);
            }

            boolean isExistSectionInModel = false;
            for (SectionModel section : groupToAdd.getSections()) {
                if (section.getName().equals(item.getSectionname())) {
                    isExistSectionInModel = true;
                    sectionToAdd = section;
                }
            }

            if (!isExistSectionInModel) {
                sectionToAdd = new SectionModel();
                fillSectionInformation(sectionToAdd, item);
                groupToAdd.getSections().add(sectionToAdd);
            }

            StudentModel studentToAdd = new StudentModel();
            transformOrderModelESOToStudentModel(item, studentToAdd);

            sectionToAdd.getStudentModels().add(studentToAdd);
        }
    }

    private void groupOrderBySections (OrderEditModel orderModel, List<OrderModelESO> listModelsESO) {
        orderModel.setSections(new ArrayList<>());
        for (OrderModelESO item : listModelsESO) {
            GroupModel groupToAdd = new GroupModel();
            SectionModel sectionToAdd = new SectionModel();

            boolean isExistSectionInModel = false;
            for (SectionModel section : orderModel.getSections()) {
                if (section.getName().equals(item.getSectionname())) {
                    isExistSectionInModel = true;
                    sectionToAdd = section;
                }
            }

            if (!isExistSectionInModel) {
                sectionToAdd = new SectionModel();
                fillSectionInformation(sectionToAdd, item);
                orderModel.getSections().add(sectionToAdd);
            }

            boolean isExistGroupInModel = false;
            for (GroupModel group : sectionToAdd.getGroups()) {
                if (group.getName().equals(item.getGroupname())) {
                    isExistGroupInModel = true;
                    groupToAdd = group;
                }
            }

            if (!isExistGroupInModel) {
                groupToAdd = new GroupModel();
                fillGroupInformation(groupToAdd, item);
                sectionToAdd.getGroups().add(groupToAdd);
            }

            StudentModel studentToAdd = new StudentModel();
            transformOrderModelESOToStudentModel(item, studentToAdd);

            groupToAdd.getStudentModels().add(studentToAdd);
        }
    }

    private void fillSectionInformation (SectionModel section, OrderModelESO item) {
        section.setName(item.getSectionname());
        section.setFoundation(item.getFoundation());
        section.setFoundationLos(item.getFoundationLos());
        section.setStudentModels(new ArrayList<>());
        section.setId(item.getIdSection());
        section.setIdOS(item.getIdOS());
        section.setFirstDate(item.getFirstDateSection());
        section.setSecondDate(item.getSecondDateSection());
    }

    private void fillGroupInformation (GroupModel group, OrderModelESO item) {
        group.setName(item.getGroupname());
        group.setSections(new ArrayList<>());
    }

    public void transformOrderModelESOToStudentModel (OrderModelESO orderModelESO, StudentModel studentModel) {
        studentModel.setId(orderModelESO.getIdStudent());
        studentModel.setFio(orderModelESO.getFio());
        studentModel.setFirstDate(orderModelESO.getFirstDate());
        studentModel.setSecondDate(orderModelESO.getSecondDate());
        studentModel.setThirdDate(orderModelESO.getThirdDate());
        studentModel.setRecordnumber(orderModelESO.getRecordbook());
        studentModel.setAdditional(orderModelESO.getAdditionalInfo());

        if (orderModelESO.getAdditionalInfo() != null && !orderModelESO.getAdditionalInfo().equals("")) {
            JSONObject additionalInfo = new JSONObject(orderModelESO.getAdditionalInfo());

            if (additionalInfo.has(OrderStudentJSONConst.DATE_SIGN_PREV_TRANSFER_ORDER)) {
                if (additionalInfo.getString(OrderStudentJSONConst.DATE_SIGN_PREV_TRANSFER_ORDER) != null) {
                    try {
                        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(
                                additionalInfo.getString(OrderStudentJSONConst.DATE_SIGN_PREV_TRANSFER_ORDER));
                        studentModel.setDatePrevOrder(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (additionalInfo.has(OrderStudentJSONConst.PREV_TRANSFER_ORDER_NUMBER)) {
                if (additionalInfo.getString(OrderStudentJSONConst.PREV_TRANSFER_ORDER_NUMBER) != null) {
                    studentModel.setNumberPrevOrder(additionalInfo.getString(OrderStudentJSONConst.PREV_TRANSFER_ORDER_NUMBER));
                }
            }

            if (additionalInfo.has(OrderStudentJSONConst.FOUNDATION)) {
                if (additionalInfo.getString(OrderStudentJSONConst.FOUNDATION) != null) {
                    studentModel.setFoundation(additionalInfo.getString(OrderStudentJSONConst.FOUNDATION));
                }
            }
        }
    }

    public void saveFoundation (Long id, String foundation) {
        new EditOrderManagerESO().saveFoundation(id, foundation);
    }

    public void saveFoundationStudent (StudentModel student, String foundation) {
        JSONObject object;
        if (student.getAdditional() == null || student.getAdditional().equals("")) {
            object = new JSONObject();
        } else {
            object = new JSONObject(student.getAdditional());
        }

        object.put(OrderStudentJSONConst.FOUNDATION, foundation);
        student.setAdditional(object.toString());

        new EditOrderManagerESO().saveFoundationStudent(student.getId(), object.toString());
    }

    public List<RatingModel> getMarksStudent (Long idLoss, boolean isDebt) {
        managerOrderESO = new EditOrderManagerESO();
        return divideEsoModelForRatingModel(managerOrderESO.getMarksForStudentInOrder(idLoss), isDebt);
    }

    private List<RatingModel> divideEsoModelForRatingModel (List<RatingEsoModel> listEsoModel, boolean debt) {
        final List<RatingModel> result = new ArrayList<>();
        List<RatingModel> examList = new ArrayList<>();
        List<RatingModel> passList = new ArrayList<>();
        List<RatingModel> cpList = new ArrayList<>();
        List<RatingModel> cwList = new ArrayList<>();
        List<RatingModel> practicList = new ArrayList<>();

        for (RatingEsoModel esoModel : listEsoModel) {
            if (esoModel.getExam() && (!debt || (debt && esoModel.getExamrating() < 3))) {
                RatingModel ratingModel = getRatingModelByEsoModel(esoModel);
                ratingModel.setRating(esoModel.getExamrating());
                ratingModel.setFoc("Экзамен");
                examList.add(ratingModel);
            }
            if (esoModel.getPass() && (!debt || (debt && (esoModel.getPassrating() == 2 || esoModel.getPassrating() < 1)))) {
                RatingModel ratingModel = getRatingModelByEsoModel(esoModel);
                ratingModel.setRating(esoModel.getPassrating());
                ratingModel.setFoc("Зачет");
                passList.add(ratingModel);
            }
            if (esoModel.getCp() && (!debt || (debt && esoModel.getCprating() < 3))) {
                RatingModel ratingModel = getRatingModelByEsoModel(esoModel);
                ratingModel.setRating(esoModel.getCprating());
                ratingModel.setFoc("КП");
                cpList.add(ratingModel);
            }
            if (esoModel.getCw() && (!debt || (debt && esoModel.getCwrating() < 3))) {
                RatingModel ratingModel = getRatingModelByEsoModel(esoModel);
                ratingModel.setRating(esoModel.getCwrating());
                ratingModel.setFoc("КР");
                cwList.add(ratingModel);
            }
            if (esoModel.getPractic() && (!debt || (debt && esoModel.getPracticrating() < 3))) {
                RatingModel ratingModel = getRatingModelByEsoModel(esoModel);
                ratingModel.setRating(esoModel.getPracticrating());
                ratingModel.setFoc("Практика");
                practicList.add(ratingModel);
            }
        }

        result.addAll(examList);
        result.addAll(passList);
        result.addAll(cpList);
        result.addAll(cwList);
        result.addAll(practicList);

        Collections.sort(result, (o1, o2) -> {
            try {
                if (formatYYYY.parse(o1.getSemester()).compareTo(formatYYYY.parse(o2.getSemester())) == 0) {
                    return o1.getSemester().compareTo(o2.getSemester());
                }
                return formatYYYY.parse(o2.getSemester()).compareTo(formatYYYY.parse(o1.getSemester()));
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        });

        return result;
    }

    private RatingModel getRatingModelByEsoModel (RatingEsoModel esoModel) {
        RatingModel ratingModel = new RatingModel();
        ratingModel.setSemester(esoModel.getSemester());
        ratingModel.setSubjectname(esoModel.getSubjectname());
        return ratingModel;
    }

    public List<LinkOrderSectionEditModel> getSectionsFromOrder (long idOrder) {
        return managerOrderESO.getLinkOrderSections(idOrder)
                              .stream()
                              .map(los -> new LinkOrderSectionEditModel(los.getIdLOS(), los.getIdOS(), los.getName()))
                              .collect(Collectors.toList());
    }

    public List<SearchGroupModel> getGroupsForSearch (long idOrder) {
        return managerOrderESO.getGroupsForOrderSearch(idOrder)
                              .stream()
                              .map(group -> new SearchGroupModel(group.getId(), group.getName()))
                              .collect(Collectors.toList());
    }

    public List<SearchStudentModel> getStudentsForSearch (String surname, String name, String patronymic, String groupname, long idOrder) {
        return managerOrderESO.getStudentsByFilterNotInOrder(surname, name, patronymic, groupname, idOrder)
                              .stream()
                              .map(student -> new SearchStudentModel(student.getSurname(), student.getName(), student.getPatronymic(),
                                                                     student.getGroupname(), student.getIdSSS()
                              ))
                              .collect(Collectors.toList());
    }

    public void deleteStudents (StudentModel student) {
        managerOrderESO.removeStudentFromOrder(student.getId());
    }
}
