package com.halushko.fiot2019.pk.bot.actions.answers;

import com.halushko.fiot2019.pk.bot.actions.questions.IsEditedMessage;
import com.halushko.fiot2019.pk.bot.actions.questions.Question;
import com.halushko.fiot2019.pk.bot.actions.questions.IsPDF;
import com.halushko.fiot2019.pk.bot.actions.questions.RegexpTrigger;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

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
        addName();
        addSpec();
        readPDF();
        writeQR();
        registerNewUser();
        adminRegisterNewUser();

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

        Answers.answers.put(new RegexpTrigger("/spec.*"), myAnswers);
        Answers.answers.put(new RegexpTrigger("спеціальність.*"), myAnswers);
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
        Answer help = new TextAnswer("Для реєстрації у черзі натисніть /register");
        Queue<Answer> myAnswers = new LinkedList<>();
        myAnswers.add(help);

        Answers.answers.put(new RegexpTrigger("/help"), myAnswers);
        Answers.answers.put(new RegexpTrigger("допомога"), myAnswers);
    }

    private static void addName() {
        Queue<Answer> myAnswers = new LinkedList<>();
        myAnswers.add(new InputName());

        Answers.answers.put(new RegexpTrigger("/name.*"), myAnswers);
        Answers.answers.put(new RegexpTrigger("піб.*"), myAnswers);
    }

    private static void addStart() {
        Queue<Answer> myAnswers = new LinkedList<>();
        String t = "\nДля реєстрації введіть свої ПІБ у форматі:\n/name Петренко Петро Петрович\n\tабо ПІБ Петренко Петро Петрович";
        t += "\nРеєстрація у Телегам не є обов'язковую, та вона покращить та пришвидчить прийом Ваших документів.";
        myAnswers.add(new TextAnswer("Ласкаво просимо до приймальної комісії ФІОТ!" + t));
        myAnswers.add(new TextAnswer("Вітаємо у приймальній комісії ФІОТ!" + t));
        Answers.answers.put(new RegexpTrigger("/start"), myAnswers);
    }

    private static void readPDF() {
        Queue<Answer> myAnswers = new LinkedList<>();
        myAnswers.add(new FindStudentsInPDF());
        Answers.answers.put(new IsPDF(), myAnswers);
    }
}
