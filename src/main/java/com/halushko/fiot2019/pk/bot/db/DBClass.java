package com.halushko.fiot2019.pk.bot.db;

import com.halushko.fiot2019.pk.bot.actions.entities.AnswerInDB;
import com.halushko.fiot2019.pk.bot.actions.entities.Task;
import com.halushko.fiot2019.pk.bot.actions.entities.UserInfo;
import com.mongodb.client.MongoCollection;

import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public abstract class DBClass<T extends DBClass> {
    protected final static Map<Class<? extends DBClass>, DBClass> EMPTY = new HashMap<>();

    public static void init() {
        EMPTY.put(AnswerInDB.class, AnswerInDB.INSTANCE);
        EMPTY.put(Task.class, Task.INSTANCE);
        EMPTY.put(UserInfo.class, UserInfo.INSTANCE);
    }

    public static <G> G getEmpty(Class<G> clazz) {
        return (G) EMPTY.get(clazz);
    }

    final void generateIndexes(MongoCollection<T> table) {
        if (!isIndexesHasBeenGenerated()) {
            regenerateIndexes(table);
        }
    }

    public abstract String tableName();

    protected abstract void regenerateIndexes(MongoCollection<T> table);

    protected abstract boolean isIndexesHasBeenGenerated();

    public abstract Document toDocument();

    public abstract T toJavaObject(Document doc);
}
