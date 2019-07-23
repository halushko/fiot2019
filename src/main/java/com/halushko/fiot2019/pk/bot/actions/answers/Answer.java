package com.halushko.fiot2019.pk.bot.actions.answers;

import org.telegram.telegrambots.meta.api.objects.Message;

public abstract class Answer<T> {
    private final T answer;

    Answer(Class<T> clazz, T answer) {
        this.answer = answer;
    }

    public final Message answer(Message msg) {
        return answer(answer, msg);
    }

    public abstract String getKey();
    protected abstract Message answer(T answer, Message msg);
}
