package com.halushko.fiot2019.pk.bot.actions.answers;

import org.telegram.telegrambots.meta.api.objects.Message;

public class Register extends Answer<Object> {
    Register(Class<Object> clazz, Object answer) {
        super(clazz, answer);
    }

    @Override
    protected void answer(Object answer, Message msg) {

    }
}
