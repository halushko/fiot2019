package com.halushko.fiot2019.pk.bot.db;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.HashSet;
import java.util.Set;

public final class DBUtil {
    private static DBUtil INSTANCE;
    private MongoDatabase mongoDatabase;

    private <G extends DBClass> MongoCollection<Document> getTable(G object){
        if(mongoDatabase == null) {
            mongoDatabase = MongoClients.create().getDatabase("FIOT2019");
        }
        MongoCollection<Document> table = mongoDatabase.getCollection(object.tableName());
        object.generateIndexes(table);
        return table;
    }

    public <G extends DBClass> void insert(G object){
        getTable(object).insertOne(object.toDocument());
    }

    public <G extends DBClass> long getCount(BasicDBObject query, Class<G> clazz){
        return getTable(DBClass.getEmpty(clazz)).countDocuments(query);
    }

    private <G> MongoCollection<Document> getTable(G empty) {
        return null;
    }

    public <G extends DBClass> Set<G> get(BasicDBObject query, Class<G> clazz){
        Set<G> result = new HashSet<>();
        for(Document a: getTable(DBClass.getEmpty(clazz)).find(query)){
            result.add((G) DBClass.getEmpty(clazz).toJavaObject(a));
        }
        return result;
    }

    public <G extends DBClass> void update(BasicDBObject query, Class<G> clazz, Document doc){
        DBClass.getEmpty(clazz).toJavaObject(getTable(DBClass.getEmpty(clazz)).findOneAndReplace(query, doc));
    }

    public static DBUtil getInstance(){
        if(INSTANCE == null) {
            INSTANCE = new DBUtil();
        }
        return INSTANCE;
    }
}
