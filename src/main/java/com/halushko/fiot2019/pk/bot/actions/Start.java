package com.halushko.fiot2019.pk.bot.actions;

import java.util.Map;

import static com.halushko.fiot2019.pk.bot.actions.Commands.START;

public class Start extends Action<String> {
    public Start(String text) {
        super(text);
    }

    @Override
    protected boolean isValidCase(String input) {
        return START.is(input);
    }

    @Override
    protected void doActionRealisation(Map<String, Object> params) {
//        Answers.
//        Bot.getInstance().sendTextMessage("");
    }
}
