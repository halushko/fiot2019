package com.halushko.fiot2019.pk.bot.actions.answers;

import com.halushko.fiot2019.pk.bot.Bot;
import com.halushko.fiot2019.pk.bot.actions.entities.Task;
import com.halushko.fiot2019.pk.bot.actions.entities.UserInfo;
import com.halushko.fiot2019.pk.bot.db.DBUtil;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Set;
import java.util.TreeSet;

public class Specialty extends Answer<Object> {
    private static Set<String> SPECIALITY = new TreeSet<String>() {{
        add("121");
        add("126");
        add("123");
    }};

    public Specialty() {
        super("");
    }

    @Override
    public String getKey() {
        return "SPEC";
    }

    @Override
    protected Message answer(Object answer, Task msg) {
        UserInfo user = UserInfo.getById(msg.getUserId());
        if (user == null) {
            user = new UserInfo(msg.getUserId());
            DBUtil.getInstance().insert(user);
        }

        return setSpec(msg);
    }

    private Message setSpec(Task task) {
        String textMessage =
                (task.getText() == null ? "" : task.getText().trim().toLowerCase()).
                        replaceAll("^/spec ", "").
                        replaceAll("^спеціальність ", "").
                        replaceAll("\\s+", " ").trim();
        String delimiter = ", ";
        StringBuilder specConcat = new StringBuilder("Помилка \uD83D\uDE15\nНа нашому факультеті присутні тільки три спеціальності: ");
        for (String spec : SPECIALITY) {
            if (spec.equalsIgnoreCase(textMessage)) {
                String t = "Спеціальність " + textMessage + " обрано";
                return Bot.sendTextMessage(task.getUserId(), task.getMessageId(), t, null);
            }
            specConcat.append(spec).append(delimiter);
        }
        return Bot.sendTextMessage(task.getUserId(), task.getMessageId(), specConcat.toString().replaceAll(delimiter + "$", ""), null);
    }
}
