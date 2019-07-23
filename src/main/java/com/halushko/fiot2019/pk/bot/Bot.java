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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class Bot extends TelegramLongPollingBot {
    private static Bot INSTANCE;
    private Thread messageHandler;

    public static void main(String[] args) {
        ApiContextInitializer.init();
        getInstance();
    }

    private Bot() {
        messageHandler = new Thread(() -> {
            for (; ; ) {
                executeTasks();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        messageHandler.start();
    }

    private void executeTasks() {
        Set<Task> tasks = Tasks.getScheduled();

        try {
            new Thread(() -> {
                for (Task task : tasks) {
                    if (!task.isEdited()) {
                        Answer answer = Answers.find(task.getUpdate());
                        answer.answer(task.getMessage());
                    } else {
                        String str = null;
                        try {
                            str = " або натисніть на " +
                                    "[цей текст](https://t.me/share/url?url=";
                            str += URLEncoder.encode(task.getMessage().getText(), StandardCharsets.UTF_8.toString());
                            str += ") та виберіть серед переліку чатів нашого бота.";
                        } catch (UnsupportedEncodingException e) {
                            str = "";
                        }
                        Bot.sendTextMessage(
                                task.getUserId(),
                                task.getMessage().getMessageId(),
                                "Виправлення вже відправлених повідомлень не приймаються до оброблень задля уникнення " +
                                        "розбіжностей у введених даних. Будь ласка, якщо необхідно виконати якусь дію, то надсилайте " +
                                        "запити новими повідомленнями, а не виправляйте старі. Дякуємо за розуміння!\n\n" +
                                        "Щоб наліслати виправлене повідомлення як нове скопіюйте його" + str,
                                str.equals("") ? null : "markdown"
                        );
                    }
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

    public static void sendTextMessage(long chatId, Integer replyTo, String text, String parsMode) {
        try {
            SendMessage send = new SendMessage().setChatId(chatId);
            if (parsMode != null) send.setParseMode(parsMode);
            if (replyTo != null) send.setReplyToMessageId(replyTo);
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
        return "FIOT2019bot";
    }

    @Override
    public String getBotToken() {
        return "845137922:AAEC0jqOoUREu0-pl4d3QTsfUO81HnlIPmM";
    }
}
