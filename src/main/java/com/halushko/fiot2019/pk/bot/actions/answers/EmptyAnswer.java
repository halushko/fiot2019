package com.halushko.fiot2019.pk.bot.actions.answers;

import com.halushko.fiot2019.pk.bot.actions.entities.Task;
import org.telegram.telegrambots.meta.api.objects.Message;

public final class EmptyAnswer extends Answer<String> {
    public EmptyAnswer() {
        super("");
    }

    @Override
    public String getKey() {
        return "NULL";
    }

    @Override
    protected Message answer(String answer, Task msg) {
        return null;
    }
}
