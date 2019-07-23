package com.halushko.fiot2019.pk.bot.actions.questions;

import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class Question<T> {
    protected abstract boolean validateRealisation(T input);
    protected abstract T getInput(Update update);

    public final boolean validate(Update update){
        return validateRealisation(getInput(update));
    }
}
