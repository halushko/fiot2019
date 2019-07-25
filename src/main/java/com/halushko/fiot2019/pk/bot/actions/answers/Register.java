package com.halushko.fiot2019.pk.bot.actions.answers;

import com.halushko.fiot2019.pk.bot.Bot;
import com.halushko.fiot2019.pk.bot.actions.entities.AnswerInDB;
import com.halushko.fiot2019.pk.bot.actions.entities.UserInfo;
import com.halushko.fiot2019.pk.bot.db.DBUtil;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Set;

public class Register extends Answer<Object> {
    Register() {
        super(null);
    }

    @Override
    public String getKey() {
        return "REGISTER";
    }

    @Override
    protected Message answer(Object answer, Message msg) {
        UserInfo user = UserInfo.getById(msg.getChatId());
        if(user == null) {
            user = new UserInfo(msg.getChatId());
            DBUtil.getInstance().insert(user);
        }

        if(user.getNumber() .equals(Integer.MAX_VALUE) ) {
            return registerUser(msg);
        } else {
            Set<AnswerInDB> previousAnswers = AnswerInDB.getAnswersToUserByKey(msg.getChatId(), getKey());
            if(previousAnswers.size() > 0) {
                return alreadyRegistered(previousAnswers.stream().findFirst().get());
            } else {
                return Bot.sendTextMessage(msg.getChatId(), null, "Щось пішло не так, бот не може знайти Ваш номер.", null);
            }
        }
    }

    private Message alreadyRegistered(AnswerInDB reg) {
        String text = "Ви вже зареєстровані. Якщо ваша черга вже давно пройшла, то перереєструйтесь " +
            "якщо ви натиснули помилково, то нічого не робіть. Якщо щось не зрозуміло, то можете звернутися до " +
                "консультантів за допомогою.";
        return Bot.sendTextMessage(reg.userId, reg.messageId, text, null);
    }

    //TODO
    private Message registerUser(Message msg) {
        int number = 12100042;
        String text = "Ви стали в електронну чергу. Ваш номер: " + number + ". Наразі перед вами 3 абітурієнти.";
        UserInfo.getById(msg.getChatId()).setNumber(number);
        return Bot.sendTextMessage(msg.getChatId(), null, text, null);
    }
}
