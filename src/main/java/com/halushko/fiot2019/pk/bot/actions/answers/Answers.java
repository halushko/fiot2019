package com.halushko.fiot2019.pk.bot.actions.answers;

import com.halushko.fiot2019.pk.bot.actions.questions.Question;
import com.halushko.fiot2019.pk.bot.actions.questions.RegexpTrigger;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

public final class Answers {
    private final static Map<Question, Queue<Answer>> answers = new LinkedHashMap<>();
    public final static Answer EMPTY_ANSWER = new EmptyAnswer();

    private static Map<Question, Queue<Answer>> getAnswers(){
        if(answers.isEmpty()){
            initialize();
        }
        return answers;
    }

    public static Answer find(Update update){
        for(Map.Entry<Question, Queue<Answer>> entry: getAnswers().entrySet()){
            if(entry.getKey().validate(update)) {
                Answer answer = entry.getValue().poll();
                if(answer == null) {
                    return EMPTY_ANSWER;
                } else {
                    entry.getValue().add(answer);
                    return answer;
                }
            }
        }
        return EMPTY_ANSWER;
    }

    private static void initialize(){
        addStart();
        addHelp();
        addRegister();
        readPDF();
    }

    private static void addHelp() {
        Answer help = new TextAnswer("Для реєстрації у черзі натисніть /register");
        Queue<Answer> myAnswers = new LinkedList<>();
        myAnswers.add(help);

        Answers.answers.put(new RegexpTrigger("/help"), myAnswers);
        Answers.answers.put(new RegexpTrigger("допомога"), myAnswers);
    }

    private static void addRegister() {
        Answer help = new TextAnswer("Наразі реєстрація закрита");
        Queue<Answer> myAnswers = new LinkedList<>();
        myAnswers.add(help);

        Answers.answers.put(new RegexpTrigger("/register"), myAnswers);
        Answers.answers.put(new RegexpTrigger("реєстрація"), myAnswers);
    }

    private static void addStart(){
        Queue<Answer> myAnswers = new LinkedList<>();
        myAnswers.add(new TextAnswer("Ласкаво просимо до приймальної комісії ФІОТ!"));
        myAnswers.add(new TextAnswer("Вітаємо у приймальній комісії ФІОТ!"));
        Answers.answers.put(new RegexpTrigger("/start"), myAnswers);
    }

    private static void readPDF(){
        Queue<Answer> myAnswers = new LinkedList<>();
        myAnswers.add(new ReadPDF());
        Answers.answers.put(new com.halushko.fiot2019.pk.bot.actions.questions.ReadPDF(), myAnswers);
    }
}
