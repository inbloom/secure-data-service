package org.slc.sli.dal.repository;

import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

public class MongoDriverTest {

    private static final int NUM_TO_WRITE = 100000;

    private static final int NUM_CONSUMERS = 1;

    public static void main(String[] args) {

        test();
    }

    public static void test() {
        try {

            // setup
            Mongo mongo = new Mongo("localhost", 27017);
            mongo.getDB("rel").dropDatabase();

            BlockingQueue<DBObject> queue = new LinkedBlockingQueue<DBObject>();

            long startTime = System.nanoTime();

            // create and kick off queue consumers (mongo writers)
            consume(queue, mongo.getDB("rel").getCollection("node"));

            // construct and put DBObjects to queue
            produce(queue);

            waitForFinish(queue);

            double totalTime = (System.nanoTime() - startTime) / 1000000000D;

            System.out.println("\n" + NUM_TO_WRITE + " documents in " + totalTime + " seconds: " + NUM_TO_WRITE
                    / totalTime + " RPS");

            System.exit(1);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    private static void consume(BlockingQueue<DBObject> queue, DBCollection collection) {
        for (int n = 0; n < NUM_CONSUMERS; n++) {
            new Thread(new MongoWriteConsumer(queue, collection)).start();
        }
    }

    private static void produce(BlockingQueue<DBObject> queue) {

        String[] links = { "school|01234567891234567", "assessment|0123456789123456", "grade|01234567891234567" };

        try {
            for (int i = 0; i < NUM_TO_WRITE; i++) {
                DBObject dbObj = new BasicDBObject("_id", "student|" + i);
                dbObj.put("l", links);
                queue.put(dbObj);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void waitForFinish(BlockingQueue<DBObject> queue) {
        while (!queue.isEmpty()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static final class MongoWriteConsumer implements Runnable {

        private final BlockingQueue<DBObject> queue;
        private final DBCollection collection;

        public MongoWriteConsumer(BlockingQueue<DBObject> queue, DBCollection collection) {
            this.queue = queue;
            this.collection = collection;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    DBObject dbObj = queue.take();
                    dbObj.put("t", System.currentTimeMillis());

                    // collection.save(dbObj, WriteConcern.NORMAL);
                    collection.insert(dbObj, WriteConcern.NORMAL);

                } catch (MongoException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
