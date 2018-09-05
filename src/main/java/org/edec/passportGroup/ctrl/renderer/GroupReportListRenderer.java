package org.edec.passportGroup.ctrl.renderer;

import org.edec.admin.model.EmployeeModel;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.passportGroup.ctrl.listener.ChangeCmbMarkListener;
import org.edec.passportGroup.ctrl.listener.DoubleClickRatingCellListener;
import org.edec.passportGroup.ctrl.WinOpenPersonalRetakeCtrl;
import org.edec.passportGroup.model.GroupModel;
import org.edec.passportGroup.model.RatingModel;
import org.edec.passportGroup.model.StudentModel;
import org.edec.passportGroup.model.SubjectModel;
import org.edec.passportGroup.service.PassportGroupService;
import org.edec.passportGroup.service.impl.PassportGroupServiceESO;
import org.edec.register.model.RetakeModel;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.*;

public class GroupReportListRenderer implements ListitemRenderer<StudentModel> {
    private static final int ROW_TITLE = 0;
    private static final int ROW_STATISTIC = 1;

    private List<SubjectModel> subjectModelList;
    protected PassportGroupService service = new PassportGroupServiceESO();
    protected GroupModel groupModel;
    private boolean isCheckScholarship;
    private boolean isShowNegativeMarks;
    private Runnable updateGroupReport;

    protected TemplatePageCtrl template = new TemplatePageCtrl();

    public GroupReportListRenderer(List<SubjectModel> subjectModelList, GroupModel groupModel, boolean isCheckScholarship,
                                   Runnable updateGroupReport, Boolean isShowNegativeMarks) {
        this.subjectModelList = subjectModelList;
        this.groupModel = groupModel;
        this.isCheckScholarship = isCheckScholarship;
        this.updateGroupReport = updateGroupReport;
        this.isShowNegativeMarks = isShowNegativeMarks;
    }

    @Override
    public void render(Listitem listitem, StudentModel studentModel, int i) throws Exception {
        studentModel.setSelected(false);
        listitem.setValue(studentModel);
        if (i == ROW_TITLE) {
            renderTitle(listitem);
        } else if (i == ROW_STATISTIC) {
            renderStatistic(listitem);
        } else {
            renderRating(listitem, studentModel, i);
        }
    }

    /**
     * Отрисовка строки с предметами студента (всегда первая строка)
     */
    private void renderTitle(Listitem listitem) {

        listitem.getListbox().getListhead().getChildren().clear();
        listitem.getListbox().getListhead().setHeight("215px");

        listitem.getListbox().getListhead().appendChild(createListheaderGroupInfo());

        Listcell cellHours = new Listcell("Часы:");
        cellHours.setStyle("align: center; border-right: 1px solid #777777; ");
        listitem.appendChild(cellHours);

        for (SubjectModel aSubjectModelList : subjectModelList) {
            listitem.getListbox().getListhead().appendChild(createListheaderSubject(aSubjectModelList, listitem));

            cellHours = new Listcell(aSubjectModelList.getCountHours() + "ч");
            cellHours.setStyle(
                    "align: center; border-right: 1px solid #777777; border-bottom: 1px solid #777777; border-top: 1px solid #777777;");
            listitem.appendChild(cellHours);
        }

        Listheader listheader = new Listheader();

        Div div = new Div();
        div.appendChild(new Label("Успеваемость"));
        div.setSclass("pg_header_label_new pg_header_fon_pr");

        listheader.appendChild(div);
        listheader.setWidth("60px");
        listitem.getListbox().getListhead().appendChild(listheader);

        cellHours = new Listcell("");
        cellHours.setStyle(
                "align: center; border-right: 1px solid #777777; border-bottom: 1px solid #777777; border-top: 1px solid #777777;");
        listitem.appendChild(cellHours);
    }

    private Listheader createListheaderGroupInfo() {
        Listheader listheader = new Listheader();
        listheader.setSclass("pg_student_name");
        listheader.setWidth("300px");
        Vbox vboxInfo = new Vbox();
        Label labelGroupName = new Label(groupModel.getGroupName());
        labelGroupName.setStyle("font-size: 30px; font-weight: 700;");
        Label labelCourse = new Label(groupModel.getCourse() + " курс");
        labelCourse.setStyle("font-size: 30px; font-weight: 700;");
        Label labelYears = new Label(groupModel.getDateBegin().toString() + " - " + groupModel.getDateEnd().toString());
        labelYears.setStyle("font-size: 30px; font-weight: 700;");
        Label labelSeason = new Label(groupModel.getSeason() == 0 ? "Осенний семестр" : "Весенний семестр");
        labelSeason.setStyle("font-size: 30px; font-weight: 700;");

        vboxInfo.appendChild(labelGroupName);
        vboxInfo.appendChild(labelCourse);
        vboxInfo.appendChild(labelYears);
        vboxInfo.appendChild(labelSeason);
        listheader.appendChild(vboxInfo);

        return listheader;
    }

    private Listheader createListheaderSubject(SubjectModel subject, Listitem firstListitem) {
        String subjectName = subject.getSubjectName();

        Listheader listheader = new Listheader();

        Div div = new Div();
        div.appendChild(new Label(subjectName));

        div.addEventListener(Events.ON_RIGHT_CLICK, event -> {
            if (!template.getCurrentModule().isReadonly()) {
                List<StudentModel> selectedStudents = new ArrayList<>();

                // TODO подумать над логикой получения выбранных студентов
                for (Listitem item : firstListitem.getListbox().getItems()) {
                    StudentModel student = item.getValue();
                    if (student.getSelected()) {
                        selectedStudents.add(student);
                    }
                }

                if (selectedStudents.isEmpty()) {
                    PopupUtil.showWarning("Выберите хотя бы одного студента для открытия индивидуальной пересдачи!");
                    return;
                }

                Map<String, Object> arg = new HashMap<>();

                arg.put(WinOpenPersonalRetakeCtrl.SUBJECT, subject);
                arg.put(WinOpenPersonalRetakeCtrl.STUDENTS, selectedStudents);
                arg.put(WinOpenPersonalRetakeCtrl.GROUP, groupModel);
                arg.put(WinOpenPersonalRetakeCtrl.RUNNABLE_UPDATE, updateGroupReport);

                ComponentHelper.createWindow("/passportGroup/winOpenPersonalRetake.zul", "winModuleOpenPersonalRetake", arg).doModal();
            }
        });

        String sclass = "";

        switch (subject.getFoc()) {
            case EXAM:
                sclass = "pg_header_label_new pg_header_fon_exam";
                break;
            case PASS:
                sclass = "pg_header_label_new pg_header_fon_pass";
                break;
            case CP:
                sclass = "pg_header_label_new pg_header_fon_cp";
                break;
            case CW:
                sclass = "pg_header_label_new pg_header_fon_cw";
                break;
            case PRACTIC:
                sclass = "pg_header_label_new pg_header_fon_pr";
                break;
        }

        div.setSclass(sclass);

        StringBuilder teachers = new StringBuilder("Преподаватели:\n");
        for (EmployeeModel e : subject.getEmployeeModels()) {
            teachers.append(e.getFio()).append("\n");
        }

        listheader.setTooltiptext(teachers.toString());
        listheader.appendChild(div);
        listheader.setWidth("60px");

        return listheader;
    }

    /**
     * Отрисовка строки со стастистикой студента (всегда вторая строка)
     */
    private void renderStatistic(Listitem listitem) {
        Listcell cellStatistic = new Listcell("Статистика:");
        cellStatistic.setStyle("font-size: 12px; align: center; border-right: 1px solid #777777; ");
        listitem.appendChild(cellStatistic);

        Double sum = 0.0;

        for (SubjectModel aSubjectModelList : subjectModelList) {
            sum += aSubjectModelList.getStatistic();
            cellStatistic = new Listcell(aSubjectModelList.getStatisticStr());
            cellStatistic.setStyle(
                    "font-size: 11px; align: center; border-right: 1px solid #777777; border-bottom: 1px solid #777777; border-top: 1px solid #777777;");
            listitem.appendChild(cellStatistic);
        }

        if (subjectModelList.size() > 0) {
            cellStatistic = new Listcell((int) (sum / subjectModelList.size()) + "%");
            cellStatistic.setStyle(
                    "font-size: 11px; align: center; border-right: 1px solid #777777; border-bottom: 1px solid #777777; border-top: 1px solid #777777;");
            listitem.appendChild(cellStatistic);
        }
    }

    /**
     * Отрисовка строки с оценками студента (остальные строки)
     */
    private void renderRating(Listitem listitem, StudentModel studentModel, int i) {
        Listcell listcell = new Listcell();
        Checkbox checkbox = new Checkbox();

        listcell.addEventListener(Events.ON_CLICK, event -> {
            checkbox.setChecked(!checkbox.isChecked());
            studentModel.setSelected(checkbox.isChecked());
        });

        checkbox.addEventListener(Events.ON_CHECK, event -> studentModel.setSelected(checkbox.isChecked()));

        if (!template.getCurrentModule().isReadonly()) {
            listcell.appendChild(checkbox);
        }

        listcell.appendChild(getLabelStudentForCell(studentModel, i));
        listcell.setSclass("pg_student_name");
        listitem.appendChild(listcell);

        int sumStatistic = 0;

        for (SubjectModel subject : subjectModelList) {
            boolean isFound = false;
            for (int k = 0; k < studentModel.getRatings().size(); k++) {
                final RatingModel rating = studentModel.getRatings().get(k);

                if (subject.getIdSubject().equals(rating.getIdSubject()) && subject.getFoc() == rating.getFoc()) {
                    listcell = constructCell(rating, studentModel);

                    if (rating.getRating() > 0 && rating.getRating() != 2) {
                        sumStatistic++;
                    }

                    Listcell selectedListcell = listcell;

                    if (!isCheckScholarship && !template.getCurrentModule().isReadonly()) {
                        // Редактирование оценки у студента
                        listcell.addEventListener(Events.ON_DOUBLE_CLICK, new DoubleClickRatingCellListener(selectedListcell, rating));
                    }

                    listcell.addEventListener(Events.ON_RIGHT_CLICK, event -> {
                        if (!template.getCurrentModule().isReadonly()) {
                            Map<String, Object> arg = new HashMap<>();
                            List<StudentModel> studentList = new ArrayList<>();

                            studentList.add(studentModel);

                            arg.put(WinOpenPersonalRetakeCtrl.SUBJECT, subject);
                            arg.put(WinOpenPersonalRetakeCtrl.STUDENTS, studentList);
                            arg.put(WinOpenPersonalRetakeCtrl.GROUP, groupModel);
                            arg.put(WinOpenPersonalRetakeCtrl.RUNNABLE_UPDATE, updateGroupReport);

                            ComponentHelper.createWindow("/passportGroup/winOpenPersonalRetake.zul", "winModuleOpenPersonalRetake", arg)
                                           .doModal();
                        }
                    });

                    listcell.setStyle("text-align: center; border-bottom: 1px solid #777777;");

                    listitem.appendChild(listcell);
                    isFound = true;
                }
            }

            if (!isFound) {
                listcell = new Listcell();
                listcell.setSclass("pg_rating_not_found_sr");
                listcell.setStyle("text-align: center;");
                Image img = new Image();
                img.setSrc("/imgs/cross.png");
                img.setHeight("15px");
                listcell.appendChild(img);
                listitem.appendChild(listcell);
            }
        }

        if (subjectModelList.size() > 0) {
            Listcell cellStatistic = new Listcell(sumStatistic * 100 / subjectModelList.size() + "%(" + sumStatistic + ")");
            cellStatistic.setStyle(
                    "font-size: 11px; align: center; border-right: 1px solid #777777; border-bottom: 1px solid #777777; border-top: 1px solid #777777;");
            listitem.appendChild(cellStatistic);
        }
    }

    private Label getLabelStudentForCell(StudentModel studentModel, int indexStudent) {
        Label lblStudent = new Label((indexStudent - 1) + ") " + studentModel.getFullName());
        lblStudent.setStyle("color: #000000;");
        String tooltiptext = "";

        if (studentModel.getDeducted() != null && studentModel.getDeducted()) {
            lblStudent.setStyle("color: red;");
        } else if (studentModel.getListener() != null && studentModel.getListener()) {
            lblStudent.setStyle("color: yellow;");
        } else if (studentModel.getAcademicLeave() != null && studentModel.getAcademicLeave()) {
            lblStudent.setStyle("color: green;");
        } else if (studentModel.getGovernmentFinanced() != null && !studentModel.getGovernmentFinanced()) {
            lblStudent.setStyle("font-weight: 600");
        }

        if (studentModel.getAcademicLeave() != null && studentModel.getAcademicLeave()) {
            tooltiptext += "Академический отпуск\n";
        }

        if (studentModel.getListener() != null && studentModel.getListener()) {
            tooltiptext += "Слушатель\n";
        }

        if (studentModel.getDeducted() != null && studentModel.getDeducted()) {
            tooltiptext += "Отчислен\n";
        }

        if (studentModel.getGovernmentFinanced() != null && !studentModel.getGovernmentFinanced()) {
            tooltiptext += "Договорник\n";
        }

        if (studentModel.getIdCurrentDicGroup() != null) {
            if (!studentModel.getIdDicGroup().equals(studentModel.getIdCurrentDicGroup())) {
                tooltiptext += "Переведен\n";
                lblStudent.setStyle("color: #b3b3b3");
            }
        }

        if (!tooltiptext.equals("")) {
            lblStudent.setTooltiptext(tooltiptext);
        }

        return lblStudent;
    }

    private Listcell constructCell(RatingModel rating, StudentModel studentModel) {
        final Listcell listcell = new Listcell();

        // TODO dezmontx: избавляемся от статусов
        // Логика назначения цветов должна быть следующей:
        // 1) Если у студента нет srh, или есть только srh с retake_count = -1 - серый цвет
        // 2) Если у студента последняя srh с retake_count > 0 - красный цвет
        // 3) Если у студента последняя srh с retake_count <= 0 или retake_count = 5 - белый цвет
        if (rating.getStatus() != null && rating.getStatus() != "" && rating.getStatus().charAt(0) == '1' &&
            rating.getStatus().charAt(2) == '1') {
            listcell.setSclass("pg_rating_block");
        } else if (rating.getStatus() != null && rating.getStatus() != "" && rating.getStatus().charAt(0) == '1' &&
                   rating.getStatus().charAt(2) == '2') {
            listcell.setSclass("pg_rating_block");
        } else if (rating.getStatus() != null && rating.getStatus() != "" && rating.getStatus().charAt(0) == '1' &&
                   rating.getStatus().charAt(2) == '3') {
            listcell.setSclass("pg_rating_block");
        } else if (rating.getStatus() != null && rating.getStatus() != "" && rating.getStatus().charAt(0) == '1' &&
                   rating.getStatus().charAt(2) == '0') {
            listcell.setSclass("pg_rating_activ");
        } else if (rating.getStatus() != null && rating.getStatus() != "" && rating.getStatus().charAt(0) == '0' &&
                   rating.getStatus().charAt(2) == '0') {
            listcell.setSclass("pg_rating_activ");
        } else if (rating.getStatus() != null && rating.getStatus() != "" && rating.getStatus().charAt(0) == '0' &&
                   rating.getStatus().charAt(2) == '1') {
            listcell.setSclass("pg_rating");
        } else {
            listcell.setSclass("pg_rating");
        }

        listcell.setValue(rating);
        listcell.setStyle("text-align: center; width: 60px;");

        Combobox cmb = new Combobox();
        cmb.setWidth("50px");
        cmb.setReadonly(true);
        cmb.setVisible(false);

        Label label = new Label();
        Image img = new Image();

        cmb.addEventListener(Events.ON_CHANGE,
                             new ChangeCmbMarkListener(rating, template.getCurrentUser(), studentModel, img, subjectModelList, groupModel,
                                                       label, listcell
                             )
        );

        cmb.addEventListener(Events.ON_BLUR, event -> {
            cmb.setVisible(false);
            if (rating.getFoc() == FormOfControlConst.PASS && rating.getRating() == 1) {
                img.setVisible(true);
            } else {
                label.setVisible(true);
            }
        });

        //Заполняем комбобокс с оценками
        ListModelList listModelList;
        if (rating.getFoc().getValue() == FormOfControlConst.PASS.getValue() && rating.getType() == 0) {
            listModelList = new ListModelList(Arrays.asList(" ", RatingConst.NOT_LEARNED.getShortname(), RatingConst.PASS.getShortname(),
                                                            RatingConst.NOT_PASS.getShortname()
            ));
        } else {
            listModelList = new ListModelList(
                    Arrays.asList(" ", RatingConst.NOT_LEARNED.getShortname(), RatingConst.UNSATISFACTORILY.getShortname(),
                                  RatingConst.SATISFACTORILY.getShortname(), RatingConst.GOOD.getShortname(),
                                  RatingConst.EXCELLENT.getShortname()
                    ));
        }

        cmb.setModel(listModelList);

        //Выставляем оценку в клетку
        if (rating.getType() == 0 && rating.getRating() == 1) {
            img.setSrc("/imgs/okCLR.png");
            img.setHeight("15px");
        } else if (rating.getRating() > RatingConst.UNSATISFACTORILY.getRating()) {
            label.setValue(rating.getRating().toString());
            label.setStyle("color: #000000;");
        }
        if (rating.getRating() == RatingConst.NOT_LEARNED.getRating()) {
            label.setValue(RatingConst.NOT_LEARNED.getShortname());
            label.setStyle("color: #000000;");
        } else if (isShowNegativeMarks) {
            if (rating.getRating() == RatingConst.UNSATISFACTORILY.getRating()) {
                label.setValue(rating.getRating().toString());
                label.setStyle("color: #000000;");
            }
            if (rating.getRating() == RatingConst.FAILED_TO_APPEAR.getRating()) {
                label.setValue(RatingConst.FAILED_TO_APPEAR.getShortname());
                label.setStyle("color: #000000;");
            } else if (rating.getRating() == RatingConst.NOT_PASS.getRating()) {
                if (rating.getFoc().equals(FormOfControlConst.PASS)) {
                    label.setValue(RatingConst.NOT_PASS.getShortname());
                    label.setStyle("color: #000000;");
                } else {
                    label.setValue(Integer.toString(RatingConst.UNSATISFACTORILY.getRating()));
                    label.setStyle("color: #000000;");
                }
            }
        }

        listcell.appendChild(cmb);
        listcell.appendChild(label);
        listcell.appendChild(img);

        return listcell;
    }
}