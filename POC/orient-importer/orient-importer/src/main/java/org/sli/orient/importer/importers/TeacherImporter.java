package org.sli.orient.importer.importers;

import com.mongodb.DB;
import com.tinkerpop.blueprints.Graph;

public class TeacherImporter extends BaseImporter {
    public TeacherImporter(DB m, Graph g) {
        super(m, g);
    }
    public void importCollection() {
        extractBasicNode("staff");
        // DBObject query = new BasicDBObject();
        // query.put("type", "teacher");
        // query.markAsPartialObject();
        // DBCursor cursor = mongo.getCollection("staff").find(query);
        // while (cursor.hasNext()) {
        // DBObject teacher = cursor.next();
        // Vertex v = graph.addVertex(null);
        // v.setProperty("mongoid", (String) teacher.get("_id"));
        // }
    }
    
}
