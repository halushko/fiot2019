package com.halushko.fiot2019.pk.bot.actions.answers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.halushko.fiot2019.pk.bot.Bot;
import com.halushko.fiot2019.pk.bot.actions.entities.Task;
import com.halushko.fiot2019.pk.bot.actions.entities.UserInfo;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.EnumMap;
import java.util.Hashtable;
import java.util.Map;

public final class QRAnswer extends Answer<Object>{
    public QRAnswer() {
        super(null);
    }

    @Override
    public String getKey() {
        return "GENERATE_QR";
    }

    @Override
    protected Message answer(Object answer, Task task) {
        UserInfo userInfo = UserInfo.getById(task.getUserId());
        if(userInfo == null) {
            userInfo = new UserInfo(task.getUserId());
        }
        if(userInfo.getName() == null || userInfo.getName().trim().equals("")){
            return wrongName(userInfo.userId);
        }
        if(userInfo.getSpecialisation() == null || userInfo.getSpecialisation().trim().equals("")){
            return wrongSpecialisation(userInfo.userId);
        }

        File qr = generateQR(userInfo.userId, userInfo.getSpecialisation(), userInfo.getName());
        if(qr != null) {
            return Bot.sendDocument(userInfo.userId, qr, "Покажіть цей QR відповідальній особі");
        } else {
            return error(userInfo.userId);
        }
    }

    static File generateQR(Long userId, String specialisation, String name) {
        int size = 1000;
        String fileType = "png";
        File myFile = new File("qr" + userId + "." + fileType);

//        String myCodeText = "Спеціяльність: " + specialisation + "\n" + "ПІБ: " + name;

        //https://t.me/share/url?url=
        String myCodeText = "register__" + specialisation + "__" + name.replaceAll(" ", "_");
        myCodeText = "https://t.me/" + Bot.getBotName() + "?start=register_" + userId;

        try {

            Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            hintMap.put(EncodeHintType.MARGIN, 4); /* default = 4 */
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size,
                    size, hintMap);
            int crunchifyWidth = byteMatrix.getWidth();
            BufferedImage image = new BufferedImage(crunchifyWidth, crunchifyWidth, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, crunchifyWidth, crunchifyWidth);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < crunchifyWidth; i++) {
                for (int j = 0; j < crunchifyWidth; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            boolean flag = ImageIO.write(image, fileType, myFile);
            if(flag){
                return myFile;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Message error(long userId) {
        return Bot.sendTextMessage(userId, null, "Щось пішло не так", null);
    }

    private Message wrongName(long userId) {
        return Bot.sendTextMessage(userId, null, "Вибачте, Ваше ім'я або не задано, або має невірний формат.\n" +
                "Отримайте довідку за командою /help", null);
    }

    private Message wrongSpecialisation(long userId) {
        return Bot.sendTextMessage(userId, null, "Вибачте, Ви не вибрали спеціальність на яку подпєте докумени.\n" +
                "Отримайте довідку за командою /help", null);
    }
}
