package com.openread.tools;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * mongodb客户端帮助类
 * 
 * @author chenke
 * @date 2016年5月10日 下午6:15:18
 */
public class MongoHelper {
    private MongoClient mongoClient = null;

    public MongoHelper() {
        createClient();
    }

    private void createClient() {
        if (mongoClient == null) {
            MongoClientOptions.Builder build = new MongoClientOptions.Builder();
            build.connectionsPerHost(50);
            build.threadsAllowedToBlockForConnectionMultiplier(50);
            build.maxWaitTime(5000);
            build.connectTimeout(5000);
            MongoClientOptions myOptions = build.build();
            try {
                //数据库连接实例  
                mongoClient = new MongoClient("127.0.0.1", myOptions);
            } catch (MongoException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void insert(String dbName, String collectionName, Map<String, Object> objMap) {
        MongoDatabase db = mongoClient.getDatabase(dbName);
        MongoCollection<Document> dbCollection = db.getCollection(collectionName);
        Document doc = new Document(objMap);
        dbCollection.insertOne(doc);
    }

    public void delete(String dbName, String collectionName, Map<String, Object> objMap) {
        MongoDatabase db = mongoClient.getDatabase(dbName);
        MongoCollection<Document> dbCollection = db.getCollection(collectionName);
        Document doc = new Document(objMap);
        dbCollection.deleteMany(doc);
    }

    public static void main(String args[]) {
        MongoHelper helper = new MongoHelper();
        Map<String, Object> objMap = new HashMap<String, Object>();
        objMap.put("type", "1");
        objMap.put("name", "朝阳区");
        objMap.put("sub_name", "三元桥");
//        helper.insert("test", "business_area", objMap);
//        helper.insert("test", "business_area", objMap);
//        helper.insert("test", "business_area", objMap);
        helper.delete("test", "business_area", objMap);
    }
}
