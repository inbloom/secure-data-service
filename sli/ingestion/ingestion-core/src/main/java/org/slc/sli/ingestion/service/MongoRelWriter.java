package org.slc.sli.ingestion.service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.transformation.normalization.ComplexRefDef;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.transformation.normalization.RefDef;

/**
 * Async mongo writer (consumer of producer/consumer)
 *
 * @author dduran
 *
 */
public final class MongoRelWriter {

    private static final BlockingQueue<DBObject> QUEUE = new LinkedBlockingQueue<DBObject>();

    static {
        try {
            Mongo mongo = new Mongo("localhost", 27017);

            new Thread(new MongoWriteConsumer(QUEUE, mongo.getDB("rel").getCollection("node"))).start();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    public static void writeAsync(Entity entity, EntityConfig entityConfig, String collectionName) {

        DBObject dbObj = createDBObject(entity, entityConfig, collectionName);

        try {

            QUEUE.put(dbObj);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static DBObject createDBObject(Entity entity, EntityConfig entityConfig, String collectionName) {

        DBObject dbObj = new BasicDBObject("_id", collectionName + "|" + entity.getEntityId());

        List<String> links = getLinksForEntity(entity, entityConfig);
        dbObj.put("l", links.toArray(new String[links.size()]));

        dbObj.put("t", entity.getMetaData().get("updated"));

        return dbObj;
    }

    private static List<String> getLinksForEntity(Entity entity, EntityConfig entityConfig) {
        List<String> links = new ArrayList<String>();

        ComplexRefDef complexRefDef = entityConfig.getComplexReference();
        if (complexRefDef != null) {
            addLinks(entity, links, complexRefDef.getFieldPath(), complexRefDef.getEntityType());
        }

        if (entityConfig.getReferences() != null) {
            for (RefDef refDef : entityConfig.getReferences()) {
                addLinks(entity, links, refDef.getFieldPath(), refDef.getRef().getEntityType());
            }
        }
        return links;
    }

    @SuppressWarnings("unchecked")
    private static void addLinks(Entity entity, List<String> links, String fieldPath, String entityType) {

        String bodyPath = fieldPath.replaceFirst("body\\.", "");

        Object val = entity.getBody().get(bodyPath);
        if (val instanceof String) {

            links.add(entityType + "|" + val);

        } else if (val instanceof List) {
            for (String listVal : (List<String>) val) {

                links.add(entityType + "|" + listVal);
            }
        }
    }

    private static final class MongoWriteConsumer implements Runnable {

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

                    collection.save(dbObj, WriteConcern.NORMAL);
                    // collection.insert(dbObj, WriteConcern.NORMAL);

                } catch (MongoException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
