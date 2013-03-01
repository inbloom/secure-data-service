package org.mongo.performance;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;


public class MongoCompositeTest<T> implements Callable<Boolean> {

    public DataAccessWrapper da;
    
    public int id;
    public int operationCount;
    public int chunkSize;
    public String operationsEnabled;
    public CopyOnWriteArrayList<Pair<String, Integer>> opCounts;
    public String profiledCollectionName;
    
    public T dataRecord;
    
    public MongoCompositeTest() {
        
    }
    
    public MongoCompositeTest(int id, int operationCount, int chunkSize, DataAccessWrapper da, T dataRecord, CopyOnWriteArrayList<Pair<String, Integer>> opCounts, String operationsEnabled, String profiledCollectionName) {
        this.id = id;
        this.operationCount = operationCount;
        this.chunkSize = chunkSize;
        this.da = da;
        this.dataRecord = dataRecord;
        this.opCounts = opCounts;
        this.operationsEnabled = operationsEnabled;
        this.profiledCollectionName = profiledCollectionName;
    }

    @Override
    public Boolean call() throws Exception {
        //System.out.println("CALLING THREAD " + this.id);
        execute();
        
        return false;
    }

    public void execute() {
        
        int iterations = operationCount / this.chunkSize;
        
        for (int i = 0; i < iterations; i++) {

                if (this.operationsEnabled.contains("D")) {
                  this.profileBatchedInsertsDriver(operationCount, profiledCollectionName, this.chunkSize, i);
                }
              
                if (this.operationsEnabled.contains("B")) {
                    this.profileBatchedInserts(operationCount, profiledCollectionName, this.chunkSize, i);
                }
              
                if (this.operationsEnabled.contains("W")) {
                    this.profileInsert(operationCount, profiledCollectionName, this.chunkSize, i);
                }
              
                if (this.operationsEnabled.contains("T")) {
                    this.profileBatchedSelects(operationCount, profiledCollectionName, this.chunkSize, i);
                }
              
                if (this.operationsEnabled.contains("R")) {
                    this.profileSelects(operationCount, profiledCollectionName, this.chunkSize, i);
                }              
        }
    
    }   

    private void profileBatchedInsertsDriver(int operationCount, String profiledCollectionName, int chunkSize, int iterationNumber) {
        List<DBObject> records = new ArrayList<DBObject>();
        
        for (int i = 0; i < chunkSize; i++) {
            Map<String, Object> innerObject = new HashMap<String, Object>((HashMap<String, Object>) this.dataRecord);
            
            innerObject.put(App.entityType+"UniqId", "" + this.id + "-" + iterationNumber + "-" + i);        
            
            BasicDBObject dbObj = new BasicDBObject();
            dbObj.put("_id", UUID.randomUUID().toString());
            dbObj.put("body", innerObject);
            dbObj.put("metaData", "");

            records.add(dbObj);
        }

        try {
            Mongo m = new Mongo("localhost", 27017);
            DB db = m.getDB("sli");
            db.setWriteConcern(WriteConcern.SAFE);
            
            DBCollection profiledCollection = db.getCollection(this.profiledCollectionName);

            long startTime = System.currentTimeMillis();
            profiledCollection.insert(records);
            long elapsed = System.currentTimeMillis() - startTime;
            
            System.out.println("ID = " + this.id +
                    " INSERTS BATCHD  " + chunkSize + " = " + String.format("%1$6s", elapsed) + " ms." + 
                    "          Average = " + String.format("%1$10s", (float) ((float) (elapsed) / (float) chunkSize)) + " ms/record." + 
                    "          RPS = " + Math.floor((float) ((float) chunkSize / (float)(elapsed)) * 1000));
            
            this.opCounts.add(Pair.of("INSERT_BATCHD", new Integer((int) elapsed)));
            
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        }

    }     
    
    private void profileBatchedInserts(int operationCount, String profiledCollectionName, int chunkSize, int iterationNumber) {
        List<Object> records = new ArrayList<Object>();
        
        for (int i = 0; i < chunkSize; i++) {
            Map<String, Object> innerObject = new HashMap<String, Object>((HashMap<String, Object>) this.dataRecord);

            innerObject.put(App.entityType+"UniqId", "" + this.id + "-" + iterationNumber + "-" + i);
            
            BasicDBObject dbObj = new BasicDBObject();
            dbObj.put("_id", UUID.randomUUID().toString());
            dbObj.put("body", innerObject);
            dbObj.put("metaData", "");

            records.add(dbObj);
        }
        
        long startTime = System.currentTimeMillis();
        da.mongoTemplate.insert(records, profiledCollectionName);
        long elapsed = System.currentTimeMillis() - startTime;
        
        System.out.println("ID = " + this.id +
                " INSERTS BATCH   " + chunkSize + " = " + String.format("%1$6s", elapsed) + " ms." + 
                "          Average = " + String.format("%1$10s", (float) ((float) (elapsed) / (float) chunkSize)) + " ms/record." + 
                "          RPS = " + Math.floor((float) ((float) chunkSize / (float)(elapsed)) * 1000));
        
        this.opCounts.add(Pair.of("INSERT_BATCH", new Integer((int) elapsed)));
    }
    
    
    private void profileInsert(int operationCount, String profiledCollectionName, int chunkSize, int iterationNumber) {
        Map<String, Object> innerObject = new HashMap<String, Object>((HashMap<String, Object>) this.dataRecord);
        
        long elapsed = 0;

        for (int i = 0; i < chunkSize; i++) {

            innerObject.put(App.entityType+"UniqId", "" + this.id + "-" + iterationNumber + "-" + i);
    
            BasicDBObject dbObj = new BasicDBObject();
            dbObj.put("_id", UUID.randomUUID().toString());
            dbObj.put("body", innerObject);
            dbObj.put("metaData", "");

            long startTime = System.currentTimeMillis();
            da.mongoTemplate.insert(dbObj, 
                    profiledCollectionName);
            elapsed += System.currentTimeMillis() - startTime;
        }
        
        System.out.println("ID = " + this.id +
                " INSERTS         " + chunkSize + " = " + String.format("%1$6s", elapsed) + " ms." + 
                "          Average = " + String.format("%1$10s", (float) ((float) (elapsed) / (float) chunkSize)) + " ms/record." + 
                "          RPS = " + Math.floor((float) ((float) chunkSize / (float)(elapsed)) * 1000));
        
        this.opCounts.add(Pair.of("INSERT", new Integer((int) elapsed)));
    }   
    
    @SuppressWarnings("unused")
    private void profileBatchedSelects(int operationCount, String profiledCollectionName, int chunkSize, int iterationNumber) {
        List<Object> selectResult;
        
        List<String> searchKeys = new ArrayList<String>();
        
        long elapsed = 0;
        long startTime = 0;
        
        for (int i = 0; i < chunkSize; i++) {
            searchKeys.add("" + this.id + "-" + iterationNumber + "-" + i);
            
            if (searchKeys.size() >= 100) {
                startTime = System.currentTimeMillis();

                selectResult = da.mongoTemplate.find(new Query(Criteria.where("body."+App.entityType+"UniqId").in(searchKeys)), Object.class, profiledCollectionName);

                elapsed += System.currentTimeMillis() - startTime;
                
                searchKeys = new ArrayList<String>();
            }
            
        }

        System.out.println("ID = " + this.id +
                " SELECTS BATCH   " + chunkSize + " = " + String.format("%1$6s", elapsed) + " ms." + 
                "          Average = " + String.format("%1$10s", (float) ((float) (elapsed) / (float) chunkSize)) + " ms/record." + 
                "          RPS = " + Math.floor((float) ((float) chunkSize / (float)(elapsed)) * 1000));
        
        this.opCounts.add(Pair.of("SELECT_BATCH", new Integer((int) elapsed)));
    }
    
    
    @SuppressWarnings("unused")
    private void profileSelects(int operationCount, String profiledCollectionName, int chunkSize, int iterationNumber) {
        List<Object> selectResult;
        
        long elapsed = 0;
        long startTime = 0;
        
        for (int i = 0; i < chunkSize; i++) {
            startTime = System.currentTimeMillis();

            selectResult = da.mongoTemplate.find(new Query(Criteria.where("body."+App.entityType+"UniqId").is("" + this.id + "-" + iterationNumber + "-" + i)), Object.class, profiledCollectionName);

            elapsed += System.currentTimeMillis() - startTime;
        }

        System.out.println("ID = " + this.id +
                " SELECTS         " + chunkSize + " = " + String.format("%1$6s", elapsed) + " ms." + 
                "          Average = " + String.format("%1$10s", (float) ((float) (elapsed) / (float) chunkSize)) + " ms/record." + 
                "          RPS = " + Math.floor((float) ((float) chunkSize / (float)(elapsed)) * 1000));
        
        this.opCounts.add(Pair.of("SELECT", new Integer((int) elapsed)));
    }

    
}
