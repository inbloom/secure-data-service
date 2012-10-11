package org.slc.sli.search.process.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.activemq.transport.stomp.Stomp.Headers.Subscribe;
import org.apache.activemq.transport.stomp.StompConnection;
import org.apache.activemq.transport.stomp.StompFrame;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.process.IncrementalListener;
import org.slc.sli.search.process.Indexer;
import org.slc.sli.search.transform.IndexEntityConverter;
import org.slc.sli.search.util.NestedMapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens to ActiveMQ for Sarje messages. Filters the message and passes
 * data to the Indexer to be indexed in elasticsearch.
 * 
 * @author dwu
 *
 */
public class IncrementalListenerImpl extends Thread implements IncrementalListener {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private Indexer indexer;

    private IndexEntityConverter indexEntityConverter;
    
    private String mqHost = "localhost";
    
    private int mqPort= 61612;
    
    private String mqUsername = "system";
    
    private String mqPswd = "manager";
    
    private String queue = "/queue/search_events";
    
    private ObjectMapper mapper = new ObjectMapper();
    
    public void init() throws Exception {
        start();
    }
    
    public void run() {
        try {
            listen();
        } catch (Exception e) {
            logger.error("Listener exception", e);
        }
    }
    
    public void destroy() {
        
    }
    
    /* (non-Javadoc)
     * @see org.slc.sli.search.process.impl.IncrementalListener#listen()
     */
    public void listen() throws Exception {
    
        // connect to queue
        logger.info("Connecting to ActiveMQ");
        StompConnection connection = new StompConnection();
        connection.open(mqHost, mqPort);        
        connection.connect(mqUsername, mqPswd);
        logger.info("Connected to ActiveMQ");
        
        // listen for messages
        logger.info("Listening");
        connection.subscribe(queue, Subscribe.AckModeValues.AUTO);
        while (true) {
            StompFrame message = connection.receive(0);
            logger.info(message.getBody());
            process(message.getBody());
        }
    }
    
    /**
     * Process a message from the queue
     * 
     * @param opLog
     */
    public void process(String opLog) {
        
        try {
            logger.info("Processing message");
            IndexEntity entity = convertToEntity(opLog);
            sendToIndexer(entity);
            logger.info("Done processing message");
        } catch (Exception e) {
            logger.error("Error processing message", e);
        }
    }
    
    public IndexEntity convertToEntity(String opLog) throws Exception {
        
        IndexEntity entity = null;
        opLog = preProcess(opLog);
        
        // check action type and convert to an index entity
        if (isInsert(opLog)) {
            entity = convertInsertToEntity(opLog);
        } else if (isUpdate(opLog)) {
            entity = convertUpdateToEntity(opLog);
        } else if (isDelete(opLog)) {
            entity = convertDeleteToEntity(opLog);
        }
        
        return entity;
    }
    
    private IndexEntity convertInsertToEntity(String opLog) {
        
        logger.info("Action type: insert");
        
        // parse out entity 
        int index = opLog.indexOf("\"o\" : ") + 6;
        String entityStr = opLog.substring(index, opLog.length()-1);
        logger.info(entityStr);
        
        // convert to index entity object
        return indexEntityConverter.fromEntityJson(IndexEntity.Action.INDEX, entityStr);
    }
    
    private IndexEntity convertUpdateToEntity(String opLog) throws Exception {
        
        logger.info("Action type: update");
        
        // parse out entity data
        Map<String, Object> opLogMap = mapper.readValue(opLog, new TypeReference<Map<String, Object>>() {});
        Map<String, Object> o2 = (Map<String, Object>) opLogMap.get("o2");
        String id = (String) o2.get("_id");
        String type = (String) opLogMap.get("ns");
        type = type.substring(type.lastIndexOf('.')+1);
        logger.info(id);
        logger.info(type);
        
        //TODO - fix it - get tenant from the right place
        String tenant = "midgar";
        
        Map<String, Object> metadata = new HashMap<String, Object>();
        metadata.put("tenantId", tenant);
        
        Map<String, Object> o = (Map<String, Object>) opLogMap.get("o");
        Map<String, Object> updates = (Map<String, Object>) o.get("$set");

        
        // merge data into entity json (id, type, metadata.tenantId, body)
        Map<String, Object> entityMap = new HashMap<String, Object>();
        entityMap.put("_id", id);
        entityMap.put("type", type);
        entityMap.put("metaData", metadata);
        
        for (String updateField : updates.keySet()) {
            logger.info(updateField);
            List<String> fieldChain = NestedMapUtil.getPathLinkFromDotNotation(updateField);
            NestedMapUtil.put(fieldChain, updates.get(updateField), entityMap);
        }
        
        // convert to index entity object
        return indexEntityConverter.fromEntityJson(IndexEntity.Action.UPDATE, entityMap);
    }
    
    private IndexEntity convertDeleteToEntity(String opLog) throws Exception {
        
        logger.info("Action type: delete");
        
        // parse out entity data
        Map<String, Object> opLogMap = mapper.readValue(opLog, new TypeReference<Map<String, Object>>() {});
        Map<String, Object> o = (Map<String, Object>) opLogMap.get("o");
        String id = (String) o.get("_id");;
        
        // TODO - get the tenant for real
        String tenant = "midgar";
        String type = (String) opLogMap.get("ns");
        type = type.substring(type.lastIndexOf('.')+1);
        
        Map<String, Object> metadata = new HashMap<String, Object>();
        metadata.put("tenantId", tenant);
        
        // merge data into entity json (id, type, metadata.tenantId)
        Map<String, Object> entityMap = new HashMap<String, Object>();
        entityMap.put("_id", id);
        entityMap.put("type", type);
        entityMap.put("metaData", metadata);
        
        // convert to index entity object
        return indexEntityConverter.fromEntityJson(IndexEntity.Action.DELETE, entityMap);
    }
    
    
    // TODO : is there a better way to make the json valid?
    private String preProcess(String entityStr) {
        
        // handle ISODates and NumberLong
        entityStr = entityStr.replaceAll("ISODate\\((\".*?\")\\)", "$1");
        entityStr = entityStr.replaceAll("NumberLong\\((.*?)\\)", "$1");
        return entityStr;
    }
    
    private boolean isInsert(String opLog) {
        return opLog.contains("\"op\" : \"i\"");
    }
    
    private boolean isUpdate(String opLog) {
        return opLog.contains("\"op\" : \"u\"");
    }
    
    private boolean isDelete(String opLog) {
        return opLog.contains("\"op\" : \"d\"");
    }
    
    private void sendToIndexer(IndexEntity entity) {
        indexer.index(entity);
    }

    public void setIndexer(Indexer indexer) {
        this.indexer = indexer;
    }

    public void setIndexEntityConverter(IndexEntityConverter indexEntityConverter) {
        this.indexEntityConverter = indexEntityConverter;
    }
    
}