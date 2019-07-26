package com.halushko.fiot2019.pk.bot.actions.answers;

import com.halushko.fiot2019.pk.bot.Bot;
import com.halushko.fiot2019.pk.bot.actions.entities.Task;
import com.halushko.fiot2019.pk.bot.actions.entities.UserInfo;
import org.telegram.telegrambots.meta.api.objects.Message;

public class OutputUserInfo extends Answer<Object> {
    OutputUserInfo() {
        super("");
    }

    @Override
    public String getKey() {
        return "USER_INFO";
    }

    @Override
    protected Message answer(Object answer, Task msg) {
        StringBuilder sb = new StringBuilder("Іформація з Вашого профілю:\n");
        UserInfo userInfo = UserInfo.getById(msg.getUserId());
        sb.append("ПІБ: \"").append(userInfo.getName()).append("\"\n");
        sb.append("Спеціальність: \"").append(userInfo.getSpecialisation()).append("\"");
        return Bot.sendTextMessage(userInfo.userId, null, sb.toString(), null);
    }
}
