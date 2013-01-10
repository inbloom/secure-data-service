package org.mongo.performance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.util.JSON;

public class App {
    public static String entityType;
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        System.out.println("Bootstrapping Mongo Performance");
        
        if (args.length < 7 || args.length > 8) {
            System.out.println("INVALID NUMBER OF INPUTS");
            System.out.println("1. MODE (SAFE / NONE / NORMAL)");
            System.out.println("2. NUMBER OF CONCURRENT PROCESSORS");
            System.out.println("3. NUMBER OF TOTAL RECORDS OPERATED ON BY EACH TYPE OF OPERATION");
            System.out.println("4. CHUNK SIZE (FOR READS / WRITES)");
            System.out.println("5. TYPE OF OPERATIONS (W - WRITE VIA SPRING TEMPLATE / B - BATCHED WRITE VIA SPRING TEMPLATE / D - BATCHED WRITE VIA DRIVER / R - READ / T - BATCHED READ");
            System.out.println("6. SETUP COLLECTION OPERATIONS (profiledCollection) PRIOR TO RUN (D - DROP COLLECTION + CREATE + INDEX / R - REMOVE DATA / I - REMOVE DATA + INDEX / N - NONE).");
            System.out.println("7. PREFIX OF INPUT JSON FILE NAME.(PLEASE PUT THE JSON FILE UNDER DIR 'resources/JsonFiles/'. ALSO CONFIG THE index.properties FILE UNDER 'resources/indexes/')");
            System.out.println("8. COLLECTION NAME");
            System.exit(0);
        }
        
        ConfigurableApplicationContext context = null;
        context = new ClassPathXmlApplicationContext("META-INF/spring/bootstrap.xml");
        context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
        
        DataAccessWrapper da = context.getBean(DataAccessWrapper.class);
        
        if ("SAFE".equals(args[0])) {
            da.mongoTemplate.setWriteConcern(WriteConcern.SAFE);
            System.out.println("WRITE CONCERN = SAFE");
        } else if ("NONE".equals(args[0])) {
            da.mongoTemplate.setWriteConcern(WriteConcern.NONE);
            System.out.println("WRITE CONCERN = NONE");
        } else {
            da.mongoTemplate.setWriteConcern(WriteConcern.NORMAL);
            System.out.println("WRITE CONCERN = NORMAL");
        }
        
        int numberOfProcessors = new Integer(args[1]).intValue();
        int numberOfRecords = new Integer(args[2]).intValue();
        int chunkSize = new Integer(args[3]).intValue();
        
        String concurrentOperationsEnabled = args[4];
    
        String setupOperationFlag = args[5];

        String [] tmp =  args[6].split("/");
        String temp = tmp[tmp.length-1];
        String [] tmp2 = temp.split("-");
        entityType = tmp2[0];
        
        String profiledCollectionName = args[7];
        
        System.out.println("ENTITY TO BE TESTED = "+entityType);
        System.out.println("NUMBER OF PROCESSORS = " + numberOfProcessors);
        System.out.println("NUMBER OF RECORDS = " + numberOfRecords);
        System.out.println("CHUNK SIZE = " + chunkSize);
        System.out.println("TYPES OF CONCURRENT OPERATIONS ENABLED = " + concurrentOperationsEnabled);
        System.out.println("COLLECTION SETUP FLAG = " + setupOperationFlag);
        
        long startTime = System.currentTimeMillis();
        
        MongoProcessor<DBObject> mongoProcessor = context.getBean(MongoProcessor.class);
        
        mongoProcessor.run(numberOfProcessors, da, numberOfRecords / numberOfProcessors, chunkSize,generateRecordJson(args[6]), concurrentOperationsEnabled, setupOperationFlag, args[6].substring(0, args[6].indexOf('-')) + ".indexes", profiledCollectionName);
        
        mongoProcessor.writeStatistics();
        
        long endTime = System.currentTimeMillis();
        long elapsed = endTime - startTime;
        
        System.out.println();
        System.out.println("START TIME = " + startTime +
                "           END TIME = " + endTime +
                "           ELAPSED TIME MS = " + elapsed +
                "           RPS = " + Math.floor((float) ((float) numberOfRecords / (float)(elapsed)) * 1000));

        System.out.println("-------------");
        
        System.exit(0);
        
    }
    
    private static DBObject generateRecordJson(String inputFile) {
        
        DBObject dbObject = null;
        File file = new File(inputFile + ".json");
        FileReader fr;
        try {
            fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String curLine;
            curLine = br.readLine();
            dbObject = (DBObject) JSON.parse(curLine);
            
        } catch (FileNotFoundException e) {
            System.out.println("The specified " + inputFile + ".json file is not found.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dbObject;
        
    }

}
