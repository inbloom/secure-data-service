package org.slc.sli.ingestion.csv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import org.slc.sli.ingestion.NeutralRecord;

/**
 * POC code for Csv ingestion strategy
 * 
 * @author jtully
 *
 */
public class CsvCombine {
    
    private Mongo mongo;
    private DB db;

    public CsvCombine() {
        try {
            mongo = new Mongo();
            db = mongo.getDB("StagingDB");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // TODO Can be optimized 
    public List<NeutralRecord> getNeutralRecordsFromCollection(String entityName) {
        
        System.out.println("importing from collection " + entityName);
        
        ArrayList<NeutralRecord> records = new ArrayList<NeutralRecord>();
        
        DBCollection dbCollection = db.getCollection(entityName);
        DBCursor cursor = dbCollection.find();
        
        while (cursor.hasNext()) {
            records.add(createNeutralRecord(cursor.next(), entityName));
        }
        
        return records;
    }
    
    
    /**
     * Create a neutral record from a dbElement of a base collection.
     * Initiates recursive parsing of sub tables filling the neutralRecord map.
     */
    private NeutralRecord createNeutralRecord(DBObject dbElement, String entityName) {
        
        NeutralRecord record = new NeutralRecord();
        record.setRecordType(entityName);
        
        int joinKey = Integer.parseInt(dbElement.get("JoinKey").toString());
        try {
            Map<String, Object> attributes = parseDbElement(dbElement, joinKey);
            record.setAttributes(attributes);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("invalid collection format for entity type " + entityName);
        }
        
        return record;
    }
    
    /**
     * Parse an element (table row) representing a complex type and return the associated NR map.
     */
    private Map<String, Object> parseDbElement(DBObject dbElement, int joinKey) {
        Map<String, Object> result = new HashMap<String, Object>();
        
        Set<String> keySet = dbElement.keySet();

        // add all entries from this table row
        for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
            String curKey = it.next();
            if (curKey.equals("_id") || curKey.equals("JoinKey") || curKey.equals("ParentJoinKey")) {
                continue;
            } 
            String curVal = (String) dbElement.get(curKey);
            
            if (curVal.startsWith("#")) {
                String subColName = curVal.replaceAll("#", "");
                addMapEntry(curKey, getListFromCollection(subColName, joinKey), result);
            } else {
                addMapEntry(curKey, curVal, result);
            }
        }
        
        return result;
    }
    
    /**
     * Get a list from elements in collection "collectionName" with matching ParentJoinKeys.
     * Note that it is possible to have a list of simple types (strings) as opposed to a list
     * of complex types (maps).  This is defined by the column name "SimpleType".
     */
    private List<Object> getListFromCollection(String collectionName, int parentJoinKey) {
        List<Object> result = new ArrayList<Object>();
        
        DBCollection dbCollection = db.getCollection(collectionName);
        
        BasicDBObject query = new BasicDBObject();
        query.put("ParentJoinKey", parentJoinKey);
        DBCursor cursor = dbCollection.find(query);
        
        while (cursor.hasNext()) {
            DBObject dbElement = cursor.next();
            
            Object simpleTypeVal = dbElement.get("SimpleType");
            if (simpleTypeVal != null) {
                result.add(simpleTypeVal);
            } else {
                int newJoinKey = Integer.parseInt(dbElement.get("JoinKey").toString());
                result.add(parseDbElement(dbElement, newJoinKey));
            }
        }
        return result;
    }
    
    /**
     * Resolve and build the required map structure from the column names and adds the entry
     */
    @SuppressWarnings("unchecked")
    private void addMapEntry(String key, Object val, Map<String, Object> baseNode) {
        Map<String, Object> targetNode = baseNode;
        String[] mapNames = key.split("\\.");
        int numMaps = mapNames.length;
        
        for (int i = 0; i < numMaps; ++i) {
            if (i == numMaps - 1) {
                targetNode.put(mapNames[i], val);
            } else if (!targetNode.containsKey(mapNames[i])) {
                Map<String, Object> newNode = new HashMap<String, Object>();
                targetNode.put(mapNames[i], newNode);
                targetNode = newNode;
            } else {
                targetNode = (Map<String, Object>) targetNode.get(mapNames[i]);
            }
        }
    }
    
    /**
     * Main method to test run the combiner.  Note requires local mongodb instance to be running with
     * EducationalOrgReferenceType csv table collections loaded.
     * 
     */
    public static void main(String[] args) {
        CsvCombine combiner = new CsvCombine();
        
        List<NeutralRecord> records = combiner.getNeutralRecordsFromCollection("SimpleListTest");
        
        for (Iterator<NeutralRecord> it  = records.iterator(); it.hasNext();) {
            System.out.println(it.next());
        }
    }
}
