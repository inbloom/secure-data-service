package org.sli.orient.importer.importers;

import java.util.Map;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public class StudentCohortEdgeImporter extends BaseImporter {
    
    public StudentCohortEdgeImporter(DB mongo, Graph graph) {
        super(mongo, graph);
        // TODO Auto-generated constructor stub
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.sli.orient.importer.importers.BaseImporter#importCollection()
     */
    @Override
    public void importCollection() {
        DBCursor cursor = mongo.getCollection("studentCohortAssociation").find();
        while (cursor.hasNext()) {
            DBObject spa = cursor.next();
            Map<String, Object> body = (Map) spa.get("body");
            for (Vertex student : graph.getVertices("mongoid", body.get("studentId"))) {
                for (Vertex program : graph.getVertices("mongoid", body.get("cohortId"))) {
                    Edge e = graph.addEdge(null, program, student, "studentCohort");
                    e.setProperty("mongoid", spa.get("_id"));
                    // This may not be safe.
                    e.setProperty("endDate", body.get("endDate"));
                }
                
            }
            
        }
        
    }
    
}
