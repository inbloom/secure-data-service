package org.sli.orient.importer.importers;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public class TeacherImporter extends BaseImporter {
    public TeacherImporter(DB m, Graph g) {
        super(m, g);
    }
    public void importCollection() {
        DBObject query = new BasicDBObject();
        query.put("type", "teacher");
        query.markAsPartialObject();
        DBCursor cursor = mongo.getCollection("staff").find(query);
        while (cursor.hasNext()) {
            DBObject teacher = cursor.next();
            Vertex v = graph.addVertex(null);
            v.setProperty("mongoid", teacher.get("_id"));
        }
    }
    
}
