package org.sli.orient.importer.importers;

import java.util.Map;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public class TeacherSchoolEdgeImporter extends BaseImporter {
    
    public TeacherSchoolEdgeImporter(DB m, Graph g) {
        super(m, g);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.sli.orient.importer.importers.BaseImporter#importCollection()
     */
    @Override
    public void importCollection() {
        DBCursor cursor = mongo.getCollection("teacherSchoolAssociation").find();
        while (cursor.hasNext()) {
            DBObject tsa = cursor.next();
            Map<String, Object> body = (Map) tsa.get("body");
            String endDate = body.containsKey("endDate") ? (String) body.get("endDate") : "";
            for (Vertex teacher : graph.getVertices("id", body.get("teacherId"))) {
                for (Vertex school : graph.getVertices("id", body.get("schoolId"))) {
                    Edge e = graph.addEdge(null, teacher, school, "teacherSchoolAssociation");
                    e.setProperty("endDate", endDate);
                }
            }
        }
    }
    
}
