package com.halushko.fiot2019.pk.bot.actions.questions;

import com.halushko.fiot2019.pk.bot.actions.questions.Question;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.regex.Pattern;

public class RegexpTrigger extends Question<String> {
    private final Pattern pattern;

    public RegexpTrigger(String input) {
        this.pattern = Pattern.compile((input == null ? "" : input).trim().toLowerCase());
    }

    @Override
    protected boolean validateRealisation(String input) {
        return input != null && pattern.matcher(input).matches();
    }

    @Override
    protected String getInput(Update update) {
        return ifNotEmpty(update) ? update.getMessage().getText() : null;
    }

    private static boolean ifNotEmpty(Update update){
        return update != null && update.getMessage() != null && update.getMessage().getText() != null;
    }
}
