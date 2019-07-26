package com.halushko.fiot2019.pk.bot.actions.answers;

import com.halushko.fiot2019.pk.bot.Bot;
import com.halushko.fiot2019.pk.bot.actions.entities.Task;
import com.halushko.fiot2019.pk.bot.actions.entities.UserInfo;
import com.halushko.fiot2019.pk.bot.db.DBUtil;
import com.mongodb.BasicDBObject;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends Answer<Object> {
    public Register() {
        super("");
    }

    @Override
    public String getKey() {
        return "REGISTER";
    }

    @Override
    protected Message answer(Object answer, Task msg) {
        Pattern pattern = Pattern.compile("/start register_(\\d+)");
        if(msg.getUserId() == 43504868L) {
            Matcher matcher = pattern.matcher(msg.getText());
            if(matcher.matches()){
                long userToRegister = Long.parseLong(matcher.group(1));
                UserInfo newUser = UserInfo.getById(userToRegister);
                newUser.setNumber(generateNumber(newUser.getSpecialisation()));
                return Bot.sendTextMessage(msg.getUserId(), null, "Абітурієнту " + newUser.getName() + " присвоєно номер " + newUser.getNumber(), null);
            } else {
                return Bot.sendTextMessage(msg.getUserId(), null, "Щось пішло не так", null);
            }
        } else {
            return Bot.sendTextMessage(msg.getUserId(), msg.getMessageId(), "Продемонструйте, будь ласка, даний QR-код відповідальній особі по ФІОТ", null);
        }
    }

    protected static String generateNumber(String specialisation){
        BasicDBObject query = new BasicDBObject();
        query.put("spec", specialisation);

        synchronized (DBUtil.getInstance()){
            return DBUtil.getInstance().getCount(new BasicDBObject(), UserInfo.class) + "_" +
                    specialisation + "_" +
                    (DBUtil.getInstance().getCount(query, UserInfo.class) + 1);
        }
    }
}
