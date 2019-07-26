package com.halushko.fiot2019.pk.bot.actions.entities;

import com.halushko.fiot2019.pk.bot.db.DBClass;
import com.halushko.fiot2019.pk.bot.db.DBUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;

public final class UserInfo extends DBClass<UserInfo> {
    public static final UserInfo INSTANCE = new UserInfo(new Document());

    public final Long userId;
    private String number = "";
    private String name = "";
    private String specialisation = "";
    private static boolean isIndexesHasBeenGenerated = false;

    public UserInfo(Document doc) {
        if (doc == null) {
            this.userId = Long.MAX_VALUE;
            return;
        } else {
            this.userId = doc.getLong(USER_ID_FIELD);
        }
        this.number = doc.getString(NUMBER_FIELD);
        this.name = doc.getString(NAME_FIELD);
        this.specialisation = doc.getString(SPEC_FIELD);
    }

    public UserInfo(Long userId) {
        this.userId = userId;
        save();
    }

    public UserInfo setNumber(String number) {
        this.number = number;
        save();
        return this;
    }

    public String getNumber() {
        return number;
    }

    public String getSpecialisation() {
        return specialisation;
    }

    public void setSpecialisation(String specialisation) {
        this.specialisation = specialisation;
        save();
    }

    public String getName() {
        return name;
    }

    public UserInfo setName(String name) {
        this.name = name;
        save();
        return this;
    }

    public static UserInfo getById(Long userId) {
        return DBUtil.getInstance().get(getQuery(userId), UserInfo.class).stream().findFirst().orElse(null);
    }

    public static UserInfo getByNumber(String number) {
        BasicDBObject query = new BasicDBObject();
        query.put(NUMBER_FIELD, number);

        return DBUtil.getInstance().get(query, UserInfo.class).stream().findFirst().orElse(null);
    }


    @Override
    public String tableName() {
        return "Users";
    }

    @Override
    protected void regenerateIndexes(MongoCollection<UserInfo> table) {
        table.createIndex(Indexes.text("userId"), new IndexOptions().unique(true).background(true));
        table.createIndex(Indexes.ascending("number"), new IndexOptions().background(true));
        isIndexesHasBeenGenerated = true;
    }

    @Override
    protected boolean isIndexesHasBeenGenerated() {
        return isIndexesHasBeenGenerated;
    }

    @Override
    public Document toDocument() {
        Document doc = new Document();
        doc.append(USER_ID_FIELD, userId);
        doc.append(NUMBER_FIELD, number);
        doc.append(NAME_FIELD, name);
        doc.append(SPEC_FIELD, specialisation);
        return doc;
    }

    @Override
    public UserInfo toJavaObject(Document doc) {
        return new UserInfo(doc);
    }

    private void save() {
        UserInfo u = getById(userId);
        if(u == null) {
            DBUtil.getInstance().insert(this);
        } else {
            DBUtil.getInstance().update(getQuery(userId), UserInfo.class, toDocument());
        }
    }

    private static BasicDBObject getQuery(long userId) {
        BasicDBObject query = new BasicDBObject();
        query.put(USER_ID_FIELD, userId);
        return query;
    }

    private final static String USER_ID_FIELD = "userId";
    private final static String NUMBER_FIELD = "number";
    private final static String NAME_FIELD = "name";
    private final static String SPEC_FIELD = "spec";
}
