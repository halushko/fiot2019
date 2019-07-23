package com.halushko.fiot2019.pk.bot.actions.answers;

import com.halushko.fiot2019.pk.bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AnswerToEditedMessage extends Answer<String> {
    AnswerToEditedMessage(String answer) {
        super(String.class, answer);
    }

    @Override
    public String getKey() {
        return "Warning";
    }

    @Override
    protected Message answer(String answer, Message msg) {
        String str;
        try {
            str = " або натисніть на " +
                    "[цей текст](https://t.me/share/url?url=";
            str += URLEncoder.encode(msg.getText(), StandardCharsets.UTF_8.toString());
            str += ") та виберіть серед переліку чатів нашого бота.";
        } catch (UnsupportedEncodingException e) {
            str = "";
        }
        return Bot.sendTextMessage(
                msg.getChatId(),
                msg.getMessageId(),
                answer + str,
                str.equals("") ? null : "markdown"
        );
    }
}
