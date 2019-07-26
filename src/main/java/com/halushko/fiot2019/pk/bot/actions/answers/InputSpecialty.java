package com.halushko.fiot2019.pk.bot.actions.answers;

import com.halushko.fiot2019.pk.bot.Bot;
import com.halushko.fiot2019.pk.bot.actions.entities.Task;
import com.halushko.fiot2019.pk.bot.actions.entities.UserInfo;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

public class InputSpecialty extends Answer<Object> {
    static List<String> SPECIALITY = new ArrayList<String>() {{
        add("121");
        add("123");
        add("126");
    }};

    public InputSpecialty() {
        super("");
    }

    @Override
    public String getKey() {
        return "SPEC";
    }

    @Override
    protected Message answer(Object answer, Task msg) {
        return setSpec(msg);
    }

    private Message setSpec(Task task) {
        String textMessage =
                (task.getText() == null ? "" : task.getText().trim().toLowerCase()).
                        replaceAll("^/spec ", "").
                        replaceAll("^спеціальність ", "").
                        replaceAll("\\s+", " ").trim();
        String delimiter = ", ";
        StringBuilder specConcat = new StringBuilder("Помилка \uD83D\uDE15\nНа нашому факультеті присутні тільки спеціальності: ");
        for (String spec : SPECIALITY) {
            if (spec.equalsIgnoreCase(textMessage)) {
                String t = "Спеціальність " + textMessage + " обрано";
                UserInfo.getById(task.getUserId()).setSpecialisation(spec);
                return Bot.sendTextMessage(task.getUserId(), task.getMessageId(), t, null);
            }
            specConcat.append(spec).append(delimiter);
        }

        return Bot.sendTextMessage(task.getUserId(), task.getMessageId(), specConcat.toString().replaceAll(delimiter + "$", ""), null);
    }
}
