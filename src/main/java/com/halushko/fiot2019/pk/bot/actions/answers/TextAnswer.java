package com.halushko.fiot2019.pk.bot.actions.answers;

import com.halushko.fiot2019.pk.bot.Bot;
import com.halushko.fiot2019.pk.bot.actions.entities.Task;
import org.telegram.telegrambots.meta.api.objects.Message;

public class TextAnswer extends Answer<String> {
    TextAnswer(String answer) {
        super(answer);
    }

    @Override
    public String getKey() {
        return "INFO";
    }

    @Override
    protected Message answer(String answer, Task msg) {
        return Bot.sendTextMessage(msg.getUserId(), null, answer, null);
    }
}
