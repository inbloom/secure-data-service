package org.sli.orient.importer.importers;

import java.util.logging.Level;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.tinkerpop.blueprints.Graph;

public class EdOrgImporter extends BaseImporter {
    
    public EdOrgImporter(DB m, Graph g) {
        super(m, g);
    }
    public void importCollection() {
        DBCursor cursor = mongo.getCollection("educationOrganization").find();
        cursor.batchSize(100);
        while (cursor.hasNext()) {

            DBObject edOrg = cursor.next();
            logger.log(Level.INFO, "Adding edOrg#" + edOrg.get("_id") + " to the graph");

        }
    }
    
}
