/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.ingestion.csv;

import java.io.PrintStream;
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
 * @author jtully, dshaw
 *
 */
public class CsvCombine {

    private Mongo mongo;
    private DB db;

    private static final PrintStream PRINT_STREAM = System.out;

    public CsvCombine() {
        try {
            mongo = new Mongo();
            db = mongo.getDB("StagingDB");
        } catch (Exception e) {
        }
    }

    public List<NeutralRecord> getNeutralRecordsFromCollection(String entityName) {
        PRINT_STREAM.println("importing from collection " + entityName);

        List<NeutralRecord> records = new ArrayList<NeutralRecord>();

        // Get a list of all the collections in the staging DB
        Set<String> allCollections = db.getCollectionNames();
        List<DBCollection> dbSupportingCollections = new ArrayList<DBCollection>();
        Iterator<String> it = allCollections.iterator();
        while (it.hasNext()) {
            dbSupportingCollections.add(db.getCollection(it.next().toString()));
        }

        // Get the data in the primary (entityName) collection.
        DBCollection dbCollection = db.getCollection(entityName);
        DBCursor cursor = dbCollection.find();

        // Create the neutral record on a entry-by-entry basis
        while (cursor.hasNext()) {
            records.add(createNeutralRecord(cursor.next(), entityName, dbSupportingCollections));
        }

        return records;
    }

    /**
     * Create a neutral record from a dbElement of a base collection.
     * Initiates recursive parsing of sub tables filling the neutralRecord map.
     */
    private NeutralRecord createNeutralRecord(DBObject dbElement, String entityName,
            List<DBCollection> supportingCollections) {

        NeutralRecord record = new NeutralRecord();
        record.setRecordType(entityName);

        int joinKey = Integer.parseInt(dbElement.get("JoinKey").toString());
        try {
            Map<String, Object> attributes = parseDbElement(dbElement, joinKey, entityName, supportingCollections);
            record.setAttributes(attributes);
        } catch (Exception e) {
            e.printStackTrace();
            PRINT_STREAM.println("invalid collection format for entity type " + entityName);
        }

        return record;
    }

    /**
     * Parse an element (table row) representing a complex type and return the associated NR map.
     */
    private Map<String, Object> parseDbElement(DBObject dbElement, int joinKey, String entityName,
            List<DBCollection> supportingCollections) {
        Map<String, Object> result = new HashMap<String, Object>();

        Set<String> keySet = dbElement.keySet();

        // add all entries from this table rowprefix
        for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
            String curKey = it.next();
            if (curKey.equals("_id") || curKey.equals("JoinKey") || curKey.equals("ParentJoinKey")) {
                continue;
            }

            String curVal = "" + dbElement.get(curKey);
            addMapEntry(curKey, curVal, result);

            /**
             * Now pick up the supporting list of list files.
             * The outer 'if' statement ensures this is only called if
             * further levels of hierarchy exist
             */
            for (Iterator<DBCollection> iter = supportingCollections.iterator(); iter.hasNext();) {
                String collectionName = iter.next().toString();
                if (collectionName.lastIndexOf('_') == entityName.length()) {
                    String listName = collectionName.substring(entityName.length() + 1);
                    addMapEntry(listName,
                            getListFromCollection(collectionName, joinKey, supportingCollections), result);
                }
            }
        }
        return result;
    }

    /**
     * Get a list from elements in collection "collectionName" with matching ParentJoinKeys.
     * Note that it is possible to have a list of simple types (strings) as opposed to a list
     * of complex types (maps). This is defined by the column name "SimpleType".
     */
    private List<Object> getListFromCollection(String collectionName, int parentJoinKey,
            List<DBCollection> supportingCollections) {
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
                result.add(parseDbElement(dbElement, newJoinKey, collectionName, supportingCollections));
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
     * Main method to test run the combiner. Note requires local mongodb instance to be running with
     * Student csv table collections loaded.
     *
     */
    public static void main(String[] args) {
        /**
         * It would be nicer to auto-generate the entities and associations below using
         * the Ed-Fi schema, but I don't know if that's possible at this time. -dshaw 13MAR12
         */
        int numEntities = 53;
        numEntities = 1;
        String[] entities = new String[numEntities];
        entities[0] = "Student";

        int numAssociations = 16;
        numAssociations = 1;
        String[] associations = new String[numAssociations];

        associations[0] = "EducationalOrgReferenceType";

        CsvCombine combiner = new CsvCombine();

        /**
         * Currently, we are just looking at one entity type. One possibility for the
         * full solution is to simply loop over these top level files at this level.
         * This should be the same list of Entities and Associations as are used to
         * generate the CSV templates from the XSD file.
         */
        List<NeutralRecord> records = null;
        for (int iEntity = 0; iEntity < numEntities; iEntity++) {
            records = combiner.getNeutralRecordsFromCollection(entities[iEntity]);

            /**
             * Currently, we are just writing out the neutral record for debugging purposes.
             * TA2167 is the requirement to integrate this with our existing ingestion framework
             * and probably marries up here.
             */
            for (Iterator<NeutralRecord> it = records.iterator(); it.hasNext();) {
                PRINT_STREAM.println(it.next());
            }
        }

        for (int iAssociation = 0; iAssociation < numAssociations; iAssociation++) {
            records = combiner.getNeutralRecordsFromCollection(associations[iAssociation]);

            /**
             * Currently, we are just writing out the neutral record for debugging purposes.
             * TA2167 is the requirement to integrate this with our existing ingestion framework
             * and probably marries up here.
             */
            for (Iterator<NeutralRecord> it = records.iterator(); it.hasNext();) {
                PRINT_STREAM.println(it.next());
            }
        }

    }
}
