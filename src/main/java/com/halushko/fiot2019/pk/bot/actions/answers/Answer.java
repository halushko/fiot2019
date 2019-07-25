package com.halushko.fiot2019.pk.bot.actions.answers;

import com.halushko.fiot2019.pk.bot.actions.entities.AnswerInDB;
import com.halushko.fiot2019.pk.bot.db.DBUtil;
import org.telegram.telegrambots.meta.api.objects.Message;

public abstract class Answer<T> {
    private final T answer;

    public Answer(T answer) {
        this.answer = answer;
    }

    public final AnswerInDB answer(Message msg) {
        return new AnswerInDB(answer(answer, msg), getKey());
    }

    public abstract String getKey();
    protected abstract Message answer(T answer, Message msg);
//
//    public void save(Message message, String key) {
//        if(message == null) return;
//        AnswerInDB a = new AnswerInDB(message, key);
//        DBUtil.getInstance().insert(a);
//    }
}
