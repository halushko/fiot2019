package com.halushko.fiot2019.pk.bot.actions.answers;

import org.telegram.telegrambots.meta.api.objects.Message;

public final class EmptyAnswer extends Answer<String> {
    EmptyAnswer() {
        super(String.class, "");
    }

    @Override
    protected void answer(String answer, Message msg) {

    }
}
