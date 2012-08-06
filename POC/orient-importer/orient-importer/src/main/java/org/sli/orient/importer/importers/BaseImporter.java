package org.sli.orient.importer.importers;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public class BaseImporter implements Importer {
    protected static Logger logger = Logger.getLogger("Orient-Importer");
    protected DB mongo;
    protected Graph graph;
    
    protected static final Integer BATCH_SIZE = 1000;
    
    public BaseImporter(DB mongo, Graph graph) {
        this.graph = graph;
        this.mongo = mongo;
    }
    
    public void importCollection() {
    }

    protected final boolean vertexExists(String id) {
        boolean hasVertex = false;
        for (@SuppressWarnings("unused") Vertex v : graph.getVertices("mongoid", id)) {
            hasVertex = true;
        }
        return hasVertex;
    }
    
    protected final void extractBasicNode(String collectionName) {
        logger.log(Level.INFO, "Building basic node for type: " + collectionName);
        DBCursor cursor = mongo.getCollection(collectionName).find();
        while (cursor.hasNext()) {
            DBObject item = cursor.next();
            Vertex v = graph.addVertex(null);
            logger.log(Level.INFO, "Adding vertex for {0}#{1} \t {2}",
                    new String[] { collectionName, (String) item.get("_id"), v.getId().toString() });

            v.setProperty("mongoid", item.get("id"));
        }
    }
}
