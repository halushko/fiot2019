package com.halushko.fiot2019.pk.bot.actions.questions;

import com.halushko.fiot2019.pk.bot.Bot;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public class IsPDF extends Question<java.io.File> {

    @Override
    protected boolean validateRealisation(java.io.File input) {
        try {
            return input != null && PDDocument.load(input) != null;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected java.io.File getInput(Update update) {
        if (update == null || update.getMessage() == null || update.getMessage().getDocument() == null) return null;

        return Bot.getDocument(
                update.getMessage().getDocument().getFileId(),
                update.getMessage().getDocument().getFileName()
        );
    }
}
