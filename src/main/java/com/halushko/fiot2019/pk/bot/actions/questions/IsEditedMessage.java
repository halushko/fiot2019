package com.halushko.fiot2019.pk.bot.actions.questions;

import org.telegram.telegrambots.meta.api.objects.Update;

public class IsEditedMessage extends Question<Update> {
    @Override
    protected boolean validateRealisation(Update input) {
        return input != null && input.hasEditedMessage();
    }

    @Override
    protected Update getInput(Update update) {
        return update;
    }
}
