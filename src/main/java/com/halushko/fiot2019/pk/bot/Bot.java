package com.halushko.fiot2019.pk.bot;

import com.halushko.fiot2019.pk.bot.actions.answers.Answer;
import com.halushko.fiot2019.pk.bot.actions.answers.Answers;
import com.halushko.fiot2019.pk.bot.actions.tasks.Task;
import com.halushko.fiot2019.pk.bot.actions.tasks.Tasks;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.Set;

public class Bot extends TelegramLongPollingBot {
    private static Bot INSTANCE;
    private final Thread taskRunner;

    public static void main(String[] args) {
        ApiContextInitializer.init();
    }

    private Bot(){
        taskRunner = new Thread(() -> {
            executeTasks();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        taskRunner.start();
    }

    private void executeTasks() {
        Set<Task> tasks = Tasks.getTasks();
        for(Task task: tasks){
            if(!task.isEdited()){
                Answer answer = Answers.find(task.getUpdate());
            }
        }
    }

    private static Bot getInstance(){
        if(INSTANCE == null) {
            try {
                INSTANCE = new Bot();
                new TelegramBotsApi().registerBot(INSTANCE);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        return INSTANCE;
    }

    public static void sendTextMessage(long chatId, String text, String... params) {
        try {
            SendMessage send = new SendMessage().setChatId(chatId);
            if(params != null && params.length > 0)
                if(params[0] != null) send.setParseMode(params[0]);
            send.setText(text);
            getInstance().execute(send);
        } catch (Exception ignored) {
        }
    }

    public static void sendDocument(Message msg, File file, String... params) {
        try {
            SendDocument send = new SendDocument().setChatId(msg.getChatId());
            send.setDocument(file);
            getInstance().execute(send);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Tasks.add(update);
//        Answers.find(update);
    }

    @Override
    public String getBotUsername() {
        return null;
    }

    @Override
    public String getBotToken() {
        return null;
    }
}
