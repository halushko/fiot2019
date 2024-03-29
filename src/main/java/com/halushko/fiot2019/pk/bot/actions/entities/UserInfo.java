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
    private Integer number = Integer.MAX_VALUE;
    private String name = "";
    private static boolean isIndexesHasBeenGenerated = false;

    public UserInfo(Document doc) {
        this.userId = doc.getLong(USER_ID_FIELD);
        this.number = doc.getInteger(NUMBER_FIELD);
        this.name = doc.getString(NAME_FIELD);
    }

    public UserInfo(Long userId) {
        this.userId = userId;
    }

    public UserInfo setNumber(int number){
        BasicDBObject query = new BasicDBObject();
        query.put(USER_ID_FIELD, userId);

        this.number = number;
        DBUtil.getInstance().update(query, UserInfo.class, toDocument());
        return this;
    }

    public Integer getNumber(){
        return number;
    }

    public String getName() {
        return name;
    }

    public UserInfo setName(String name) {
        this.name = name;
        return this;
    }

    public static UserInfo getById(Long userId){
        BasicDBObject query = new BasicDBObject();
        query.put(USER_ID_FIELD, userId);
        return DBUtil.getInstance().get(query, UserInfo.class).stream().findFirst().orElse(null);
    }

    public static UserInfo getByNumber(Integer number){
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
//        DBUtil.getInstance().getTable(INSTANCE, UserInfo.class).insertOne(INSTANCE);
        table.createIndex(Indexes.text("userId"), new IndexOptions().unique(true).background(true));
        table.createIndex(Indexes.ascending("number"), new IndexOptions().unique(true).background(true));
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
        return doc;
    }

    @Override
    public UserInfo toJavaObject(Document doc) {
        return new UserInfo(doc);
    }

    private final static String USER_ID_FIELD = "userId";
    private final static String NUMBER_FIELD = "number";
    private final static String NAME_FIELD = "name";

}
