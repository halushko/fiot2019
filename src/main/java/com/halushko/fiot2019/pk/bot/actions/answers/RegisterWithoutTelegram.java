package com.halushko.fiot2019.pk.bot.actions.answers;

import com.halushko.fiot2019.pk.bot.Bot;
import com.halushko.fiot2019.pk.bot.actions.entities.Task;
import com.halushko.fiot2019.pk.bot.actions.entities.UserInfo;
import com.halushko.fiot2019.pk.bot.db.DBUtil;
import com.mongodb.BasicDBObject;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterWithoutTelegram extends Register {
    public RegisterWithoutTelegram() {
        super();
    }

    @Override
    protected Message answer(Object answer, Task msg) {
        Pattern pattern = Pattern.compile("/admin_register_(\\d+)\\s+(\\S+\\s+\\S+\\s+\\S+)\\s*");
        if(msg.getUserId() == 43504868L) {
            Matcher matcher = pattern.matcher(msg.getText());
            if(matcher.matches()){
                String speciality = matcher.group(1);
                if(!InputSpecialty.SPECIALITY.contains(speciality)){
                    return Bot.sendTextMessage(msg.getUserId(), null, "Такої спеціальності не існує", null);
                } else {
                    long number = 0;
//                    BasicDBObject query = new BasicDBObject();
//                    query.put("spec", speciality);
                    synchronized (this) {
                        number -= DBUtil.getInstance().getCount(new BasicDBObject(), UserInfo.class) + 1;
                    }
                    UserInfo newUser = new UserInfo(number);
                    newUser.setSpecialisation(speciality);
                    newUser.setName(matcher.group(2));
                    newUser.setNumber(generateNumber(newUser.getSpecialisation()));

                    return Bot.sendTextMessage(msg.getUserId(), null, "Абітурієнту " + newUser.getName() + " присвоєно номер " + newUser.getNumber(), null);
                }
            } else {
                return Bot.sendTextMessage(msg.getUserId(), null, "Щось пішло не так", null);
            }
        } else {
            return Bot.sendTextMessage(msg.getUserId(), msg.getMessageId(), "Продемонструйте, будь ласка, даний QR-код відповідальній особі по ФІОТ", null);
        }
    }
}
