package com.halushko.fiot2019.pk.bot.actions.answers;

import com.halushko.fiot2019.pk.bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Message;

public class TextAnswer extends Answer<String> {
    TextAnswer(String answer) {
        super(String.class, answer);
    }

    @Override
    public String getKey() {
        return "INFO";
    }

    @Override
    protected Message answer(String answer, Message msg) {
        return Bot.sendTextMessage(msg.getChatId(), null, answer, null);
    }
}
