package com.halushko.fiot2019.pk.bot;

import com.halushko.fiot2019.pk.bot.actions.answers.Answer;
import com.halushko.fiot2019.pk.bot.actions.answers.Answers;
import com.halushko.fiot2019.pk.bot.actions.entities.Task;
import com.halushko.fiot2019.pk.bot.db.DBClass;
import com.halushko.fiot2019.pk.bot.db.DBUtil;
import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

public class Bot extends TelegramLongPollingBot {
    private static Bot INSTANCE;

    public static void main(String[] args) {
        ApiContextInitializer.init();
        DBClass.init();
        getInstance();
    }

    private Bot() {

        new Thread(() -> {
            for (; ; ) {
                executeTasks();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static File getDocument(String fileId, String fileName) {
        GetFile uploadedFile = new GetFile();
        uploadedFile.setFileId(fileId);
        String uploadedFilePath;
        try {
            uploadedFilePath = INSTANCE.execute(uploadedFile).getFilePath();
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return null;
        }

        File localFile = new File(fileName);
        try {
            InputStream is = new URL("https://api.telegram.org/file/bot" + INSTANCE.getBotToken() + "/" + uploadedFilePath).openStream();
            FileUtils.copyInputStreamToFile(is, localFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return localFile;
    }

    private void executeTasks() {
        Set<Task> tasks = Task.unreadMessages();

        try {
            new Thread(() -> {
                for (Task task : tasks) {
                    Answer a = Answers.find(task.getUpdate());
                    DBUtil.getInstance().insert(a.answer(task));
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Bot getInstance() {
        if (INSTANCE == null) {
            try {
                INSTANCE = new Bot();
                new TelegramBotsApi().registerBot(INSTANCE);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        return INSTANCE;
    }

    public static Message sendTextMessage(long chatId, Integer replyTo, String text, String parsMode) {
        try {
            SendMessage send = new SendMessage().setChatId(chatId);
            if (parsMode != null) send.setParseMode(parsMode);
            if (replyTo != null) send.setReplyToMessageId(replyTo);
            send.setText(text);
            return getInstance().execute(send);
        } catch (Exception ignored) {
            return null;
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
        Task.add(update);
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
