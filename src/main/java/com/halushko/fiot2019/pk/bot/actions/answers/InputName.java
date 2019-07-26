package com.halushko.fiot2019.pk.bot.actions.answers;

import com.halushko.fiot2019.pk.bot.Bot;
import com.halushko.fiot2019.pk.bot.actions.entities.Task;
import com.halushko.fiot2019.pk.bot.actions.entities.UserInfo;
import com.halushko.fiot2019.pk.bot.db.DBUtil;
import org.telegram.telegrambots.meta.api.objects.Message;

public class InputName extends Answer<Object> {
    InputName() {
        super(null);
    }

    @Override
    public String getKey() {
        return "NAME";
    }

    @Override
    protected Message answer(Object answer, Task msg) {
        UserInfo user = UserInfo.getById(msg.getUserId());
        if (user == null) {
            user = new UserInfo(msg.getUserId());
            DBUtil.getInstance().insert(user);
        }

        return registerUser(msg);
    }

    private Message registerUser(Task task) {
        String textMessage =
                (task.getText() == null ? "" : task.getText().trim().toLowerCase()).
                        replaceAll("^/name ", "").
                        replaceAll("^піб ", "").
                        replaceAll("\\s+", " ");
        StringBuilder capitalizeWord = new StringBuilder();

        for (String w : textMessage.split(" ")) {
            String first = w.substring(0, 1);
            capitalizeWord.append(first.toUpperCase()).append(w.substring(1)).append(" ");
        }

        capitalizeWord = new StringBuilder(capitalizeWord.toString().trim());
        if (capitalizeWord.toString().matches("\\s*\\S+\\s+\\S+\\s+\\S+\\s*")) {
            String text = "Ви зареєстрували ім'я у боті приймальної комісії ФІОТ. Ваше ім'я: " + capitalizeWord;
            UserInfo.getById(task.getUserId()).setName(capitalizeWord.toString());
            return Bot.sendTextMessage(task.getUserId(), task.getMessageId(), text, null);
        } else {
            String t = "Помилка \uD83D\uDE15\nДля реєстрації введіть свої ПІБ у форматі:\n/name <Петренко Петро Петрович>\n\tабо просто напишіть повідомлення <ПІБ Петренко Петро Петрович>";
            t += "\nРеєстрація у Телегам не є обов'язковую, та вона покращить та пришвидчить прийом Ваших документів.";
            return Bot.sendTextMessage(task.getUserId(), task.getMessageId(), t, null);
        }
    }
}
