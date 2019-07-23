package com.halushko.fiot2019.pk.bot.actions.tasks;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.validation.constraints.NotNull;
import java.util.Random;

public class Task {
    private final static Random RANDOM = new Random();
    private final String taskId;
    private final Long userId;
    private final Update update;
    private final Message message;
    private final boolean isEdited;

    public Task(@NotNull Update update){
        this.update = update;
        if(update.hasMessage()) {
            message = update.getMessage();
            isEdited = false;
        } else if (update.hasEditedMessage()){
            message = update.getEditedMessage();
            isEdited = true;
        } else {
            throw new NotImplementedException();
        }


        this.userId = message.getChatId();
        taskId = "ti" + getDate() + getUserId() + RANDOM.nextInt(1000);
    }

    public String getTaskId(){
        return taskId;
    }

    public Long getUserId(){
        return userId;
    }

    public Update getUpdate(){
        return update;
    }

    public Integer getDate() {
        return getMessage().getDate();
    }

    public Message getMessage() {
        return message;
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
