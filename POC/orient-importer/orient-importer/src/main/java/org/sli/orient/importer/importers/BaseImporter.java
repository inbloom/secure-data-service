package org.sli.orient.importer.importers;

import java.util.logging.Logger;

import com.mongodb.DB;
import com.tinkerpop.blueprints.Graph;

public class BaseImporter {
    private static Logger logger = Logger.getLogger("BaseImporter");
    protected String collectionName;
    protected DB mongo;
    protected Graph graph;
    
    public BaseImporter() {
        
    }
    
    public BaseImporter(DB mongo, Graph graph) {
        this.graph = graph;
        this.mongo = mongo;
    }
}
