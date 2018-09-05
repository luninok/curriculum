package org.edec.main.service.impl;

import org.edec.main.manager.UserManagerDAO;
import org.edec.main.model.DepartmentModel;
import org.edec.main.model.ModuleModel;
import org.edec.main.model.RoleModel;
import org.edec.main.model.UserModel;
import org.edec.main.model.dao.UserRoleModuleESOmodel;
import org.edec.main.service.UserService;
import org.edec.utility.constants.LevelConst;

import java.util.List;


public class UserServiceESOimpl implements UserService {
    private UserManagerDAO userManagerDAO = new UserManagerDAO();

    @Override
    public UserModel getUserByLdapLogin (String ldapLogin) {
        return getUserModelByEsoModel(userManagerDAO.getUserRoleModuleModel(ldapLogin));
    }

    @Override
    public UserModel getUserByInnerLogin (String login, String password) {
        return getUserModelByEsoModel(userManagerDAO.getUserRoleModuleModelForParent(login, password));
    }

    @Override
    public ModuleModel getModuleByUserAndPath (String path, UserModel user) {
        for (RoleModel role : user.getRoles()) {
            for (ModuleModel module : role.getModules()) {
                if (module.getUrl().equals(path)) {
                    return module;
                }
            }
        }
        return null;
    }

    @Override
    public ModuleModel getModuleByRoleAndPath (String path, RoleModel role) {
        for (ModuleModel module : role.getModules()) {
            if (module.getUrl().equals(path)) {
                return module;
            }
        }
        return null;
    }

    @Override
    public void changeSelected (ModuleModel selectedModule, UserModel user) {
        for (RoleModel role : user.getRoles()) {
            if (role.isSelected()) {
                for (ModuleModel module : role.getModules()) {
                    if (module.isSelected()) {
                        role.setSelected(false);
                        module.setSelected(false);
                        break;
                    }
                }
                break;
            }
        }
        selectedModule.setSelected(true);
        selectedModule.getRole().setSelected(true);
    }

    @Override
    public boolean setVisitedModuleByHum (Long idHum, Long idModule) {
        return userManagerDAO.setVisitedModuleByUser(idHum, idModule);
    }

    private UserModel getUserModelByEsoModel (List<UserRoleModuleESOmodel> models) {
        UserModel userModel = null;
        for (UserRoleModuleESOmodel model : models) {
            if (userModel == null) {
                userModel = new UserModel();
                userModel.setFio(model.getFio());
                userModel.setIdHum(model.getIdHum());
                userModel.setStartPage(model.getStartPage());
                userModel.setGroupname(model.getGroupname());
                userModel.setGroupLeader(model.isGroupLeader());
                userModel.setFormofstudystudent(model.getFormofstudystudent());
                createSingleRole(userModel, "Профиль", "/", true);
                if (userModel.getIdHum() == 219392L || userModel.getIdHum() == 219362 || userModel.getIdHum() == 212484L ||
                    userModel.getIdHum() == 219254L || userModel.getIdHum() == 219371L) {
                    createSingleRole(userModel, "Чат", "/chat", true);
                }

                if (model.isParent()) {
                    userModel.setParent(true);
                    userModel.setIdParent(model.getIdParent());
                    createParentRole(userModel);
                    break;
                }

                userModel.setStudent(model.isStudent());
                userModel.setTeacher(model.isTeacher());
                if (userModel.isStudent()) {
                    createStudentRole(userModel);
                    LevelConst qual = LevelConst.getConstByVal(userManagerDAO.getQualification(userModel.getIdHum()));
                    userModel.setQualification(qual);
                }
                if (userModel.isTeacher()) {
                    createSingleRole(userModel, "Преподаватель", "/teacher", true);
                }
            }
            if (model.getRoleName() == null) {
                continue;
            }
            boolean addRole = true;
            for (RoleModel role : userModel.getRoles()) {
                if (role.isSingle() || role.getName().equals("Студент")) {
                    continue;
                }
                if (role.getName().equals(model.getRoleName())) {
                    setModuleForRole(model, role);
                    addRole = false;
                    break;
                }
            }
            if (addRole) {
                RoleModel role = new RoleModel();
                role.setName(model.getRoleName());
                setModuleForRole(model, role);
                userModel.getRoles().add(role);
            }
        }
        return userModel;
    }

    private void createParentRole (UserModel userModel) {
        RoleModel roleParent = new RoleModel();
        roleParent.setName("Кабинет родителя");
        userModel.getRoles().add(roleParent);

        ModuleModel moduleRecordBook = new ModuleModel();
        moduleRecordBook.setIdModule(16L);
        moduleRecordBook.setName("Зачетная книжка");
        moduleRecordBook.setUrl("/student/recordBook");
        moduleRecordBook.setRole(roleParent);
        roleParent.getModules().add(moduleRecordBook);

        ModuleModel moduleCalendarEvents = new ModuleModel();
        moduleCalendarEvents.setIdModule(20L);
        moduleCalendarEvents.setName("Календарь событий");
        moduleCalendarEvents.setUrl("/student/calendarOfEvents");
        moduleCalendarEvents.setRole(roleParent);
        roleParent.getModules().add(moduleCalendarEvents);

        ModuleModel moduleJournalOrder = new ModuleModel();
        moduleJournalOrder.setIdModule(17L);
        moduleJournalOrder.setName("Список приказов");
        moduleJournalOrder.setUrl("/student/journalOrder");
        moduleJournalOrder.setRole(roleParent);
        roleParent.getModules().add(moduleJournalOrder);
    }

    private void createStudentRole (UserModel userModel) {
        RoleModel roleStudent = new RoleModel();
        roleStudent.setName("Студент");
        userModel.getRoles().add(roleStudent);

        //Зачетная книжка доступна всем
        ModuleModel moduleRecordBook = new ModuleModel();
        moduleRecordBook.setIdModule(16L);
        moduleRecordBook.setName("Зачетная книжка");
        moduleRecordBook.setUrl("/student/recordBook");
        moduleRecordBook.setRole(roleStudent);
        roleStudent.getModules().add(moduleRecordBook);

        //Календарь событий доступен только для студентов, которые сейчас учатся на очном отделении
        if (userModel.getGroupname() != null && userModel.getFormofstudystudent() != null && userModel.getFormofstudystudent() == 1) {
            ModuleModel moduleCalendarEvents = new ModuleModel();
            moduleCalendarEvents.setIdModule(20L);
            moduleCalendarEvents.setName("Календарь событий");
            moduleCalendarEvents.setUrl("/student/calendarOfEvents");
            moduleCalendarEvents.setRole(roleStudent);
            roleStudent.getModules().add(moduleCalendarEvents);

            if (userModel.isGroupLeader()) {
                ModuleModel moduleJournalOfAttendance = new ModuleModel();
                moduleJournalOfAttendance.setIdModule(21L);
                moduleJournalOfAttendance.setName("Журнал посещаемости");
                moduleJournalOfAttendance.setUrl("/student/journalOfAttendance");
                moduleJournalOfAttendance.setRole(roleStudent);
                roleStudent.getModules().add(moduleJournalOfAttendance);
            }
        }

        //Справки доступны всем текущим студентам
        if (userModel.getGroupname() != null && userModel.getFormofstudystudent() != null) {
            ModuleModel moduleFactSheet = new ModuleModel();
            moduleFactSheet.setIdModule(33L);
            moduleFactSheet.setName("Заказ справок");
            moduleFactSheet.setUrl("/student/factSheet");
            moduleFactSheet.setRole(roleStudent);
            roleStudent.getModules().add(moduleFactSheet);
        }

        //Опросник доступен всем
        ModuleModel moduleQuestionnaire = new ModuleModel();
        moduleQuestionnaire.setIdModule(19L);
        moduleQuestionnaire.setName("Опросник");
        moduleQuestionnaire.setUrl("/student/questionnaire");
        moduleQuestionnaire.setRole(roleStudent);
        roleStudent.getModules().add(moduleQuestionnaire);

        //Список приказов доступен всем
        ModuleModel moduleJournalOrder = new ModuleModel();
        moduleJournalOrder.setIdModule(17L);
        moduleJournalOrder.setName("Список приказов");
        moduleJournalOrder.setUrl("/student/journalOrder");
        moduleJournalOrder.setRole(roleStudent);
        roleStudent.getModules().add(moduleJournalOrder);
    }

    private void createSingleRole (UserModel userModel, String name, String url, boolean show) {
        RoleModel role = new RoleModel();
        role.setSingle(true);
        role.setShow(show);
        userModel.getRoles().add(role);

        ModuleModel moduleModel = new ModuleModel();
        moduleModel.setUrl(url);
        moduleModel.setName(name);
        moduleModel.setRole(role);
        role.getModules().add(moduleModel);
    }

    private void setModuleForRole (UserRoleModuleESOmodel model, RoleModel role) {
        boolean addModule = true;
        for (ModuleModel module : role.getModules()) {
            if (module.getName().equals(model.getModuleName()) && module.getFormofstudy().equals(model.getFormofstudy()) &&
                module.isReadonly() == model.isReadonly()) {
                setDepartment(model, module);
                addModule = false;
                break;
            }
        }
        if (addModule) {
            ModuleModel module = new ModuleModel();
            module.setIdModule(model.getIdModule());
            module.setName(model.getModuleName());
            module.setFormofstudy(model.getFormofstudy());
            module.setType(model.getType());
            module.setImagePath(model.getImagePath());
            module.setReadonly(model.isReadonly());
            module.setUrl(model.getUrl());
            module.setRole(role);
            setDepartment(model, module);
            role.getModules().add(module);
        }
    }

    private void setDepartment (UserRoleModuleESOmodel model, ModuleModel module) {
        DepartmentModel department = new DepartmentModel();
        department.setFulltitle(model.getFulltitle());
        department.setShorttitle(model.getShorttitle());
        department.setIdChair(model.getIdChair());
        department.setIdDepartment(model.getIdDepartment());
        department.setIdInstitute(model.getIdInstitute());
        department.setIdInstituteMine(model.getIdInstituteMine());
        department.setInstitute(model.getInstitute());
        module.getDepartments().add(department);
    }
}