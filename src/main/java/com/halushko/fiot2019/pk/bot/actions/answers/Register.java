package com.halushko.fiot2019.pk.bot.actions.answers;

import com.halushko.fiot2019.pk.bot.Bot;
import com.halushko.fiot2019.pk.bot.actions.tasks.Tasks;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Set;

public class Register extends Answer<Void> {
    Register() {
        super(Void.class, null);
    }

    @Override
    public String getKey() {
        return "REGISTER";
    }

    @Override
    protected Message answer(Void answer, Message msg) {

        if(Tasks.getUser(msg.getChatId()).getNumber() == null ) {
            return registerUser(msg);
        } else {
            Set<Message> previousAnswers = Tasks.getAnswersToUserByKey(msg.getChatId(), getKey());
            if(previousAnswers.size() > 0) {
                return alreadyRegistered(previousAnswers.stream().findFirst().get());
            } else {
                return Bot.sendTextMessage(msg.getChatId(), null, "Щось пішло не так, бот не може знайти Ваш номер.", null);
            }
        }
    }

    private Message alreadyRegistered(Message reg) {
        String text = "Ви вже зареєстровані. Якщо ваша черга вже давно пройшла, то перереєструйтесь " +
            "якщо ви натиснули помилково, то нічого не робіть. Якщо щось не зрозуміло, то можете звернутися до " +
                "консультантів за допомогою.";
        return Bot.sendTextMessage(reg.getChatId(), reg.getMessageId(), text, null);
    }

    //TODO
    private Message registerUser(Message msg) {
        int number = 12100042;
        String text = "Ви стали в електронну чергу. Ваш номер: " + number + ". Наразі перед вами 3 абітурієнти.";

        Tasks.getUser(msg.getChatId()).setNumber(number);
        return Bot.sendTextMessage(msg.getChatId(), null, text, null);
    }
}
