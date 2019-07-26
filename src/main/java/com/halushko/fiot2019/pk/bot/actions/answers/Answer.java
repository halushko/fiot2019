package com.halushko.fiot2019.pk.bot.actions.answers;

import com.halushko.fiot2019.pk.bot.actions.entities.AnswerInDB;
import com.halushko.fiot2019.pk.bot.actions.entities.Task;
import org.telegram.telegrambots.meta.api.objects.Message;

public abstract class Answer<T> {
    private final T answer;

    public Answer(T answer) {
        this.answer = answer;
    }

    public final AnswerInDB answer(Task task) {
        return new AnswerInDB(answer(answer, task), getKey());
    }

    public abstract String getKey();
    protected abstract Message answer(T answer, Task msg);
//
//    public void save(Message message, String key) {
//        if(message == null) return;
//        AnswerInDB a = new AnswerInDB(message, key);
//        DBUtil.getInstance().insert(a);
//    }
}
