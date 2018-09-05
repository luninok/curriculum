package org.edec.questionnaire.ctrl;

import org.edec.notification.model.NotificationModel;
import org.edec.notification.service.NotificationService;
import org.edec.notification.service.impl.NotificationServiceImpl;
import org.edec.questionnaire.model.*;
import org.edec.questionnaire.service.QuestionnaireServiceESO;
import org.edec.questionnaire.service.QuestionnaireServiceEnsemble;
import org.edec.questionnaire.service.impl.QuestionnaireServiceESOimpl;
import org.edec.questionnaire.service.impl.QuestionnaireServiceEnsembleImpl;
import org.edec.utility.zk.CabinetSelector;
import org.json.JSONObject;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Html html1234;

    private QuestionnaireServiceESO questionnaireServiceESO = new QuestionnaireServiceESOimpl();
    private QuestionnaireServiceEnsemble questionnaireServiceEnsemble = new QuestionnaireServiceEnsembleImpl();
    private NotificationService notificationService = new NotificationServiceImpl();

    protected void fill () {
        html1234.setContent("<p>Уважаемые студенты,</p>\n" +
                            "<p>обратите внимание, что с 1 февраля начался прием заявок на повышенную государственную академическую стипендию за достижения в учебной, научно-исследовательской, общественной, культурно-творческой и спортивной деятельности.</p>\n" +
                            "<p>Прием заявок осуществляется на сервисе личного кабинета «Мой СФУ» (<a href='http://i.sfu-kras.ru' style='cursor: pointer' target='_blank'>i.sfu-kras.ru</a>) в 2 этапа:</p>\n" +
                            "<p>1. Прием достижений на верификацию – с 1 февраля до 5 марта 2018 г.\n</p>" +
                            "<p>2. Прием заявок на получение ПГАС - с 1 февраля до 15 марта 2018 г.\n</p>" +
                            "<p>Также обращаем ваше внимание на то, что 13 февраля 2018 г. в 18:00 состоится семинар для студентов, где специалисты Отдела молодежных проектов, расскажут, как правильно подавать заявку и ответят на интересующие вопросы. Участниками могут стать все желающие. Место проведения семинара – пр. Свободный 82 (корпус К), Малый актовый зал.</p>\n" +
                            "<p>Все подробности по ссылке: <a href='http://www.sfu-kras.ru/stip' style='cursor: pointer' target='_blank'>sfu-kras.ru/stip</a></p>");
    }

    @Listen("onClick = #btnSendNotification")
    public void sendNotification () {
        List<GroupModel> groups = questionnaireServiceESO.getGroupBySem(58L);
        for (GroupModel group : groups) {
            NotificationModel notification = new NotificationModel();
            notification.setSubject("Заявка на ПГАС");
            notification.setMessage("<p>Уважаемые студенты,</p>\n" +
                                    "<p>обратите внимание, что с 1 февраля начался прием заявок на повышенную государственную академическую стипендию за достижения в учебной, научно-исследовательской, общественной, культурно-творческой и спортивной деятельности.</p>\n" +
                                    "<p>Прием заявок осуществляется на сервисе личного кабинета «Мой СФУ» (<a href='http://i.sfu-kras.ru' style='cursor: pointer' target='_blank'>i.sfu-kras.ru</a>) в 2 этапа:</p>\n" +
                                    "<p>1. Прием достижений на верификацию – с 1 февраля до 5 марта 2018 г.\n</p>" +
                                    "<p>2. Прием заявок на получение ПГАС - с 1 февраля до 15 марта 2018 г.\n</p>" +
                                    "<p>Также обращаем ваше внимание на то, что 13 февраля 2018 г. в 18:00 состоится семинар для студентов, где специалисты Отдела молодежных проектов, расскажут, как правильно подавать заявку и ответят на интересующие вопросы. Участниками могут стать все желающие. Место проведения семинара – пр. Свободный 82 (корпус К), Малый актовый зал.</p>\n" +
                                    "<p>Все подробности по ссылке: <a href='http://www.sfu-kras.ru/stip' style='cursor: pointer' target='_blank'>sfu-kras.ru/stip</a></p>");
            notification.setPriority(5);
            notification.setSenderId(2493l);
            notification.setType("info");
            notification.setDate(new Date());
            List<String> recipients = questionnaireServiceESO.getRecipients(58L, group.getIdDG());
            if (recipients == null || recipients.size() == 0) {
                continue;
            }
            notification.setRecipients(recipients);
            Long idNotification = notificationService.sendNotification(notification);
            if (idNotification != null) {
                System.out.println("IdNotification: " + idNotification);
            }
        }
    }

    @Listen("onClick = #btnSendQuestionnaire")
    public void sendQuestionnaire () {
        TopQuestModel topQuest = new TopQuestModel("Опросник для оценки качества преподавания дисциплин в весеннем семестре 2016-2017гг",
                                                   "Анкета студента (Весна 2016-2017)"
        );
        Long idTopQuest = questionnaireServiceEnsemble.sendTopQuest(topQuest);

        System.out.println("ID TopQuest: " + idTopQuest);

        List<AnswerModel> radioAnswers = generateAnswersRadio(5, 3);
        List<AnswerModel> answerModelsForText = Collections.singletonList(new AnswerModel(0, "Введите свои пожелания", null));

        List<QuestionnaireModel> questionnaires = new ArrayList<>();
        List<GroupModel> groups = questionnaireServiceESO.getGroupBySem(56L);
        for (GroupModel group : groups) {
            List<BlockModel> blockModels = new ArrayList<>();

            //Блок по кафедре
            List<QuestionModel> questionsChair = new ArrayList<>();
            questionsChair.add(new QuestionModel(18L, 1, 1, 0, "",
                                                 "Насколько хорошо Вы знакомы с деятельностью заведующего кафедрой, на которой обучаетесь?",
                                                 radioAnswers
            ));
            questionsChair.add(new QuestionModel(14L, 1, 1, 0, "", "Оцените работу Вашего куратора", radioAnswers));
            questionsChair.add(new QuestionModel(15L, 1, 1, 0, "", "Оцените Ваше участие в деятельности кафедры, на которой обучаетесь",
                                                 radioAnswers
            ));
            questionsChair.add(new QuestionModel(16L, 1, 1, 0, "",
                                                 "На сколько Вы удовлетворены организацией и проведением практик (учебной, производственной и др.)",
                                                 radioAnswers
            ));
            questionsChair.add(new QuestionModel(17L, 0, 0, 0, "", "Опишите Ваши замечания и пожелания по улучшению работы кафедры",
                                                 answerModelsForText
            ));
            blockModels.add(new BlockModel(3L, new JSONObject().put("chair", group.getIdChair()).toString(),
                                           "Взаимодействие с выпускающей кафедрой", "Ваша кафедра", questionsChair
            ));

            //Блок по УОО
            List<QuestionModel> questionsUOO = new ArrayList<>();
            questionsUOO.add(
                    new QuestionModel(8L, 1, 1, 0, "", "Уровень сервиса по работе с заявлениями студентов, выдачи справок", radioAnswers));
            questionsUOO.add(
                    new QuestionModel(11L, 1, 1, 0, "", "Уровень ведения разъяснительной работы (консультирования) сотрудниками отдела",
                                      radioAnswers
                    ));
            questionsUOO.add(new QuestionModel(9L, 1, 1, 0, "", "Удобство графика работы со студентами", radioAnswers));
            questionsUOO.add(new QuestionModel(10L, 1, 1, 0, "", "Доброжелательность и корректность сотрудников отдела", radioAnswers));
            questionsUOO.add(new QuestionModel(12L, 0, 0, 2, "",
                                               "Опишите Ваши замечания и пожелания по улучшению работы учебно-организационного отдела",
                                               answerModelsForText
            ));
            blockModels.add(new BlockModel(2L, "", "Взаимодействие с учебно-организационным отделом", "Учебно-организационный отдел",
                                           questionsUOO
            ));

            for (SubjectModel subject : group.getSubjects()) {
                List<QuestionModel> subjectQuestions = new ArrayList<>();
                subjectQuestions.add(
                        new QuestionModel(1L, 1, 1, 0, "", "Полнота и качество электронного образовательного ресурса", radioAnswers));
                subjectQuestions.add(
                        new QuestionModel(2L, 1, 1, 0, "", "Доступность изложения теоретического материала на лекционных занятиях",
                                          radioAnswers
                        ));
                subjectQuestions.add(new QuestionModel(6L, 1, 1, 0, "", "Качество проведения практических занятий", radioAnswers));
                subjectQuestions.add(
                        new QuestionModel(7L, 0, 0, 2, "", "Предложения по улучшению преподавания дисциплины", answerModelsForText));

                blockModels.add(new BlockModel(1L, new JSONObject().put("subject", subject.getIdSubj()).toString(),
                                               "Качество подготовки по дисциплине " + subject.getSubjectname() +
                                               " (дисциплина за предыдущий семестр)", subject.getSubjectname(), subjectQuestions
                ));
            }

            //Блок по своим пожеланиям
            List<QuestionModel> questionComments = new ArrayList<>();
            questionComments.add(new QuestionModel(19L, 0, 0, 2, "", "Опишите Ваши замечания и пожелания по улучшению работы ИКИТ",
                                                   answerModelsForText
            ));
            blockModels.add(new BlockModel(4L, "", "Качество подготовки ИКИТ", "Качество подготовки ИКИТ", questionComments));
            List<String> recipients = questionnaireServiceESO.getRecipients(58L, group.getIdDG());
            if (recipients == null || recipients.size() == 0) {
                System.out.println("Не удалось получить получателей!!!!!" + group.getGroupname());
                continue;
            }
            questionnaires.add(new QuestionnaireModel(new Date(), //Дата отправления
                                                      new JSONObject().put("group", group.getIdDG()).put("semester", 56).toString(),
                                                      //Дополнителньые свойства
                                                      "Шкала оценок: <br/>" +//Описание опроса
                                                      "<b>5</b> - высший балл;<br/>" + "<b>1</b> - низший бал;<br/>",
                                                      "Анкета студента (осень 2016-2017) - " + group.getGroupname(),//Название опроса
                                                      idTopQuest, 214503L,//Кто отправил <= это ид Сомовой
                                                      recipients, //Получатели
                                                      blockModels
            ));
        }

        Integer quirc = 0, bc = 0, qc = 0, ansc = 0;

        for (QuestionnaireModel questionnaire : questionnaires) {
            questionnaireServiceEnsemble.sendQuestionnaire(questionnaire);
            quirc++;
            for (BlockModel block : questionnaire.getBlocks()) {
                bc++;
                for (QuestionModel question : block.getQuestions()) {
                    qc++;
                    ansc += question.getAnswers().size();
                }
            }
        }

        System.out.println("Создано:");
        System.out.println("Опросников:" + quirc);
        System.out.println("Блоков:" + bc);
        System.out.println("Вопросов:" + qc);
        System.out.println("Ответов:" + ansc);
    }

    private List<AnswerModel> generateAnswersRadio (int countAnswers, int countAnswerWithText) {
        if (countAnswerWithText > countAnswers) {
            return null;
        }
        List<AnswerModel> answers = new ArrayList<>();
        for (int i = 0; i < countAnswers; i++) {
            answers.add(new AnswerModel((i < countAnswerWithText) ? 1 : 0, String.valueOf(i + 1), i + 1));
        }
        return answers;
    }
}
