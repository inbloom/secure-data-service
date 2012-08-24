package org.mongo.performance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.stereotype.Component;

@Component
public class MongoProcessor<T> {

    public DataAccessWrapper da;
    public int size;
    public int chunkSize;
    public T dataRecord;
    public String operationsEnabled;
    public String dropCollectionFlag;
    
    private int totalExecutors;
    private CopyOnWriteArrayList<Pair<String, Integer>> opCounts;
    
    public void run(int executorCount, DataAccessWrapper da, int size, int chunkSize, T dataRecord, String operationsEnabled, String dropCollectionFlag) {
        this.da = da;
        this.size = size;
        this.chunkSize = chunkSize;
        this.dataRecord = dataRecord;
        this.operationsEnabled = operationsEnabled;
        this.totalExecutors = executorCount;
        
        if ("Y".equals(dropCollectionFlag)) {
            this.setup("profiledCollection");
        }
        
        List<FutureTask<Boolean>> futureTaskList = processOperationsInFuture(this.totalExecutors);
        boolean errors = false;
        
        try {
            runFutureTasks(futureTaskList, errors);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    
    public void writeStatistics() {
        
        System.out.println("-------------");
        
        Map<String, Integer> finalCounts = new HashMap<String, Integer>();
        
        Iterator<Pair<String, Integer>> iter = this.opCounts.iterator();
        Integer newValue;
        
        while (iter.hasNext()) {
            Pair<String, Integer> pair = iter.next();
            
            if (finalCounts.get(pair.getLeft()) != null) {
                newValue = finalCounts.get(pair.getLeft()) + pair.getRight();
            } else {
                newValue = pair.getRight();
            }
            finalCounts.put(pair.getLeft(), newValue);
        }
        
        Iterator<Entry<String, Integer>> it = finalCounts.entrySet().iterator();
        long totalNumberOfRecords = this.totalExecutors * this.size;
        
        while (it.hasNext()) {
            Entry<String, Integer> entry = it.next();
            System.out.println("OPERATION " + String.format("%1$16s", entry.getKey()) +
                    "          TOTAL COUNT OF RECORDS = " + String.format("%1$12s", totalNumberOfRecords) +
                    "          TOTAL ELAPSED MS = " + String.format("%1$10s", entry.getValue()) + 
                    "          RECORDS PER SECOND = " + ((float) totalNumberOfRecords / (float) ((float) entry.getValue() / (float) 1000))
            );
        }
        
        System.out.println("-------------");
        System.out.println("--- EXECUTION COMPLETED ---");
    }
    
    private List<FutureTask<Boolean>> processOperationsInFuture(int count) {
        List<FutureTask<Boolean>> futureTaskList = new ArrayList<FutureTask<Boolean>>(count);
        this.opCounts = new CopyOnWriteArrayList<Pair<String, Integer>>();

        for (int i = 0; i < count; i++) {
            Callable<Boolean> callable = new MongoCompositeTest(i, size, chunkSize, da, dataRecord, this.opCounts, this.operationsEnabled);
            FutureTask<Boolean> futureTask = MongoExecutor.execute(callable);
            futureTaskList.add(futureTask);
        }

        return futureTaskList;
    }   
    
    private void runFutureTasks(List<FutureTask<Boolean>> futureTaskList, boolean errors) throws InterruptedException, ExecutionException {
        for (FutureTask<Boolean> futureTask : futureTaskList) {
            // will block on FutureTask.get until task finishes
            if (futureTask.get()) {
                errors = true;
            }
        }
    }
    
    private void setup(String profiledCollectionName) {
        // setup
        da.mongoTemplate.dropCollection(profiledCollectionName);
        da.mongoTemplate.createCollection(profiledCollectionName);
        
        // index collection
        Map<String, String> seedObject = new HashMap<String, String>();
        seedObject.put("seed", "0");
        if(App.inputFromJsonFlag)
        {
        	List<String> indexes= getIndexes();
        	for(int i=0; i<indexes.size(); i++)
        	{
        		da.mongoTemplate.indexOps(profiledCollectionName).ensureIndex(new Index().on("body."+indexes.get(i), Order.ASCENDING)); 
        	}
        }
        else
        {
        	da.mongoTemplate.indexOps(profiledCollectionName).ensureIndex(new Index().on("body.studentUniqueStateId", Order.ASCENDING)); 
        //da.mongoTemplate.ensureIndex(new Index().on("body.studentUniqueStateId", Order.ASCENDING), profiledCollectionName);
        }
    }


	private List<String> getIndexes() {
		List<String> indexes = new ArrayList<String>();
		try {
			FileReader fr = new FileReader(new File(App.INDEX_PATH));
			BufferedReader br = new BufferedReader(fr);
			String curLine = null;
			while((curLine = br.readLine())!=null)
			{
				indexes.add(curLine);
			}
		} catch (FileNotFoundException e) {
			System.out.println("The specified index properties configuration file is not found.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return indexes;
	}
    
}
