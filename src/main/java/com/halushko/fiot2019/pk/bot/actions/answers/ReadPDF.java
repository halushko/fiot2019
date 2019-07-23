package com.halushko.fiot2019.pk.bot.actions.answers;

import com.halushko.fiot2019.pk.bot.Bot;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReadPDF extends Answer<Void> {
    public ReadPDF() {
        super(Void.class, null);
    }

    @Override
    protected void answer(Void answer, Message msg) {
        File pdf = Bot.getDocument(
                msg.getDocument().getFileId(),
                msg.getDocument().getFileName()
        );
        Map<Integer, List<Integer>> pageUser = new TreeMap<>();
        try {
            PDDocument document = PDDocument.load(pdf);
            for (int page = 0; page < document.getNumberOfPages(); page++) {
                int currentPage = page + 1;
                List<Integer> users = new ArrayList<>();
                int count = 0;
                PDFTextStripper s = new PDFTextStripper();
                s.setStartPage(currentPage);
                s.setEndPage(currentPage);
                for (String word : s.getText(document).split("\\s+")) {
                    if (word != null) {
                        if (word.matches("\\d{7}")) {
                            count++;
                            users.add(new Integer(word));
                        }
                    }
                }
                pageUser.put(currentPage, users);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<Integer, Integer> counter = new TreeMap<>();
        int firstPage = -1;
        int lastPage = -1;

        for (Map.Entry<Integer, List<Integer>> a : pageUser.entrySet()) {
            if (firstPage < 0) firstPage = a.getKey();
            lastPage = a.getKey();
            int size = a.getValue().size();
            if (counter.containsKey(size)) {
                counter.put(size, counter.get(size) + 1);
            } else {
                counter.put(size, 1);
            }
        }

        String info = "";
        for (Map.Entry<Integer, Integer> a : counter.entrySet()) {
            info += "З " + a.getKey() + " номерами є " + a.getValue() + " сторінок\n";
        }

        Bot.sendTextMessage(
                msg.getChatId(),
                null,
                "Кількість сторінок: " + pageUser.size() + "\n" +
                        "Перша сторінка: " + firstPage + "\n" +
                        "Остання сторінка: " + lastPage + "\n" +
                        "Кількість абітурієнтів: " + counter.entrySet().stream().mapToInt(c -> c.getKey() * c.getValue()).sum() + "\n" +
                        "Перший номер: " + pageUser.get(firstPage).get(0) + "\n" +
                        "Останній номер: " + pageUser.get(lastPage).get(pageUser.get(lastPage).size() - 1) + "\n" +
                        info.trim(),
                null);
    }
}
