package com.halushko.fiot2019.pk.bot.actions.entities;

import com.halushko.fiot2019.pk.bot.db.DBClass;
import com.halushko.fiot2019.pk.bot.db.DBUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Set;
import java.util.TreeSet;

public final class AnswerInDB extends DBClass<AnswerInDB> {
    private static boolean isIndexesHasBeenGenerated = false;
    public static final AnswerInDB INSTANCE = new AnswerInDB(new Document());

    public final Long userId;
    public final Integer messageId;
    public final String text;
    public final String document;
    public final String key;

    public AnswerInDB(Document doc) {
        this.userId = doc.getLong(USER_ID_FIELD);
        this.text = doc.getString(TEXT_FIELD);
        this.document = doc.getString(DOCUMENT_FIELD);
        this.key = doc.getString(KEY_FIELD);
        this.messageId = doc.getInteger(MESSAGE_ID_FIELD);
    }

    public AnswerInDB(Message message, String key) {
        if (message == null) {
            this.text = "";
            this.document = "";
            this.userId = Long.MAX_VALUE;
            this.messageId = -1;
        } else {
            this.text = message.getText();
            this.document = message.getDocument() != null ? message.getDocument().getFileId() : "";
            this.userId = message.getChatId();
            this.messageId = message.getMessageId();
        }
        this.key = key;
    }

    @Override
    public Document toDocument() {
        Document doc = new Document();
        doc.append(USER_ID_FIELD, userId);
        doc.append(TEXT_FIELD, text);
        doc.append(DOCUMENT_FIELD, document);
        doc.append(KEY_FIELD, key);
        doc.append(MESSAGE_ID_FIELD, messageId);
        return doc;
    }

    @Override
    public AnswerInDB toJavaObject(Document doc) {
        return new AnswerInDB(doc);
    }

    @Override
    public String tableName() {
        return "Answers";
    }

    @Override
    protected void regenerateIndexes(MongoCollection<AnswerInDB> table) {
        table.createIndex(Indexes.ascending("usrId"), new IndexOptions().background(true));
        table.createIndex(Indexes.text("key"), new IndexOptions().background(true));
        isIndexesHasBeenGenerated = true;
    }

    @Override
    protected boolean isIndexesHasBeenGenerated() {
        return isIndexesHasBeenGenerated;
    }

    public static Set<AnswerInDB> getAnswersToUserByKey(Long userId, String keyOfTask) {
        Set<AnswerInDB> result = new TreeSet<>((o1, o2) -> {
            if (o1 == null && o2 == null) return 0;
            if (o1 == null || o1.messageId == null) return 1;
            if (o2 == null || o2.messageId == null) return -1;
            return o1.messageId.compareTo(o2.messageId);
        });

        BasicDBObject query = new BasicDBObject();
        query.put(USER_ID_FIELD, userId);
        query.put(KEY_FIELD, keyOfTask);

        result.addAll(DBUtil.getInstance().get(query, AnswerInDB.class));
        return result;
    }

    private final static String USER_ID_FIELD = "userId";
    private final static String TEXT_FIELD = "text";
    private final static String DOCUMENT_FIELD = "document";
    private final static String KEY_FIELD = "key";
    private final static String MESSAGE_ID_FIELD = "messageId";
}
