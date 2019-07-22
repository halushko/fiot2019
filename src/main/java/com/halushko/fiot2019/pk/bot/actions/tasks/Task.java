package com.halushko.fiot2019.pk.bot.actions.tasks;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.validation.constraints.NotNull;

public class Task {
    private final Long id;
    private final Update update;
    private final Integer date;
    private final boolean isEdited;

    public Task(@NotNull Update update){
        this.update = update;
        Message msg;
        if(update.hasMessage()) {
            msg = update.getMessage();
            isEdited = false;
        } else if (update.hasEditedMessage()){
            msg = update.getEditedMessage();
            isEdited = true;
        } else {
            isEdited = false;
            msg = null;
        }

        this.id = msg != null ? msg.getChatId() : null;
        this.date = msg != null ? msg.getDate() : null;
    }

    public Long getId(){
        return id;
    }

    public Update getUpdate(){
        return update;
    }

    public Integer getDate() {
        return date;
    }

    public boolean isEdited(){
        return isEdited;
    }


    int compareTo(Task compareWith) {
        if(compareWith == null || compareWith.getDate() == null) return 1;
        if(getDate() == null) return -1;
        return getDate().compareTo(compareWith.getDate());
    }
}
