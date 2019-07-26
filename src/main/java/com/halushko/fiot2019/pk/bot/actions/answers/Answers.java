package com.halushko.fiot2019.pk.bot.actions.answers;

import com.halushko.fiot2019.pk.bot.actions.entities.UserInfo;
import com.halushko.fiot2019.pk.bot.actions.questions.IsEditedMessage;
import com.halushko.fiot2019.pk.bot.actions.questions.Question;
import com.halushko.fiot2019.pk.bot.actions.questions.IsPDF;
import com.halushko.fiot2019.pk.bot.actions.questions.RegexpTrigger;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

import static com.halushko.fiot2019.pk.bot.actions.answers.InputSpecialty.SPECIALITY;

public final class Answers {
    private final static Map<Question, Queue<Answer>> answers = new LinkedHashMap<>();
    public final static Answer EMPTY_ANSWER = new EmptyAnswer();

    private static Map<Question, Queue<Answer>> getAnswers() {
        if (answers.isEmpty()) {
            initialize();
        }
        return answers;
    }

    public static Answer find(Update update) {
        for (Map.Entry<Question, Queue<Answer>> entry : getAnswers().entrySet()) {
            if (entry.getKey().validate(update)) {
                Answer answer = entry.getValue().poll();
                if (answer == null) {
                    return EMPTY_ANSWER;
                } else {
                    entry.getValue().add(answer);
                    return answer;
                }
            }
        }
        return EMPTY_ANSWER;
    }

    private static void initialize() {
        editedMessages();
        addStart();
        addHelp();
        addHelpName();
        addHelpSpec();
        specInfo();
        addName();
        addSpec();
        readPDF();
        writeQR();
        registerNewUser();
        adminRegisterNewUser();
        userInfo();
    }

    private static void userInfo() {
        Answer help = new OutputUserInfo();
        Queue<Answer> myAnswers = new LinkedList<>();
        myAnswers.add(help);

        Answers.answers.put(new RegexpTrigger("/user_info"), myAnswers);
    }

    private static void specInfo() {
        StringBuilder sb = new StringBuilder("Наразі на факультеті присутні такі спеціальності:\n");
        for(int i = 0; i < SPECIALITY.size() - 2; i++){
            sb.append(SPECIALITY.get(i)).append(", ");
        }
        sb.append(SPECIALITY.get(SPECIALITY.size() - 2)).append(" та ");
        sb.append(SPECIALITY.get(SPECIALITY.size() - 1));


        Answer help = new TextAnswer(sb.toString());

        Queue<Answer> myAnswers = new LinkedList<>();
        myAnswers.add(help);

        Answers.answers.put(new RegexpTrigger("/spec_info"), myAnswers);
    }

    private static void addHelpSpec() {
        Answer help = new TextAnswer("Для реєстрації введіть свої ПІБ у форматі:\n" +
                "/name <Прізвище Ім'я По-батькові>\n" +
                "\tабо просто напишіть повідомлення типу\n<ПІБ Прізвище Ім'я По-батькові>");
        Queue<Answer> myAnswers = new LinkedList<>();
        myAnswers.add(help);

        Answers.answers.put(new RegexpTrigger("/name"), myAnswers);
        Answers.answers.put(new RegexpTrigger("ПІБ"), myAnswers);
    }

    private static void addHelpName() {
        Answer help = new TextAnswer("Для реєстрації введіть свою спеціальність у форматі:\n" +
                "/spec <номер спеціальності> (див. /spec_info)\n" +
                "\tабо просто напишіть повідомлення типу\n<Спеціальність номер>");
        Queue<Answer> myAnswers = new LinkedList<>();
        myAnswers.add(help);

        Answers.answers.put(new RegexpTrigger("/spec"), myAnswers);
        Answers.answers.put(new RegexpTrigger("спеціальність"), myAnswers);
    }

    private static void adminRegisterNewUser() {
        Queue<Answer> myAnswers = new LinkedList<>();
        myAnswers.add(new RegisterWithoutTelegram());

        Answers.answers.put(new RegexpTrigger("/admin_register_\\d+\\s+.+"), myAnswers);
    }

    private static void registerNewUser() {
        Queue<Answer> myAnswers = new LinkedList<>();
        myAnswers.add(new Register());

        Answers.answers.put(new RegexpTrigger("/start register_\\d+"), myAnswers);
    }

    private static void writeQR() {
        Queue<Answer> myAnswers = new LinkedList<>();
        myAnswers.add(new QRAnswer());

        Answers.answers.put(new RegexpTrigger("/qr"), myAnswers);
        Answers.answers.put(new RegexpTrigger("qr"), myAnswers);
    }

    private static void addSpec() {
        Queue<Answer> myAnswers = new LinkedList<>();
        myAnswers.add(new InputSpecialty());

        Answers.answers.put(new RegexpTrigger("/spec .*"), myAnswers);
        Answers.answers.put(new RegexpTrigger("спеціальність .*"), myAnswers);
    }

    private static void editedMessages() {
        String text = "Виправлення вже відправлених повідомлень не приймаються до оброблень задля уникнення " +
                "розбіжностей у введених даних. Будь ласка, якщо необхідно виконати якусь дію, то надсилайте " +
                "запити новими повідомленнями, а не виправляйте старі. Дякуємо за розуміння!\n\n" +
                "Щоб наліслати виправлене повідомлення як нове можете просто скопіювати його";
        Queue<Answer> myAnswers = new LinkedList<>();
        myAnswers.add(new AnswerToEditedMessage(text));

        Answers.answers.put(new IsEditedMessage(), myAnswers);
    }

    private static void addHelp() {
        Answer help = new TextAnswer("Для реєстрації у черзі необхідно згенерувати QR-код і показати його відповідальній особі.\n" +
                "QR-код генерується за допомогою команди /qr але треба попередньо ввести Ваші ПІБ та номер спеціальності, " +
                "на яку Ви подаєте документи.\n\n" +
                "Команди:\n" +
                "/qr - Отримати QR-код для реєстрації у черзі. Обов'язково необхідно заповнити свої ПІБ та номер спеціальності.\n" +
                "/name - або \"ПІБ\" використовуються як префікс до повідомлення у комбінації з вашими ПІБ для вводу Вашого імені\n" +
                "/spec - або \"спеціальність\" використовуються як префікс до повідомлення у комбінації з вашою спеціальність для вводу Вашого імені");
        Queue<Answer> myAnswers = new LinkedList<>();
        myAnswers.add(help);

        Answers.answers.put(new RegexpTrigger("/help"), myAnswers);
        Answers.answers.put(new RegexpTrigger(".*допомога.*"), myAnswers);
    }

    private static void addName() {
        Queue<Answer> myAnswers = new LinkedList<>();
        myAnswers.add(new InputName());

        Answers.answers.put(new RegexpTrigger("/name .*"), myAnswers);
        Answers.answers.put(new RegexpTrigger("піб .*"), myAnswers);
    }

    private static void addStart() {
        Queue<Answer> myAnswers = new LinkedList<>();
        String t = "Реєстрація електронна, але треба отримати Ваш номер у відповідальної особи для того щоб стати у чергу \uD83D\uDE43\n\n" +
                "Для інформації про реєстрацію введть /help";
        t += "\nРеєстрація у Телегам покращить та пришвидчить прийом Ваших документів.\n" +
                "Якщо ви не бажаєте реєструватися у Телеграм боті просто підійдіть до відповідальної особи для " +
                "отримання додаткової інформації";
        myAnswers.add(new TextAnswer("Ласкаво просимо до приймальної комісії ФІОТ!\n" + t));
        myAnswers.add(new TextAnswer("Вітаємо у приймальній комісії ФІОТ!\n" + t));
        Answers.answers.put(new RegexpTrigger("/start"), myAnswers);
    }

    private static void readPDF() {
        Queue<Answer> myAnswers = new LinkedList<>();
        myAnswers.add(new FindStudentsInPDF());
        Answers.answers.put(new IsPDF(), myAnswers);
    }
}
