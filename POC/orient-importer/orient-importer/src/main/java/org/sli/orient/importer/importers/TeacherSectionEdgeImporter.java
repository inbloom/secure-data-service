package org.sli.orient.importer.importers;

import java.util.Map;
import java.util.logging.Level;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public class TeacherSectionEdgeImporter extends BaseImporter {
    
    public TeacherSectionEdgeImporter(DB mongo, Graph graph) {
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
        DBCursor cursor = mongo.getCollection("teacherSectionAssociation").find();
        while (cursor.hasNext()) {
            DBObject spa = cursor.next();
            @SuppressWarnings("unchecked")
            Map<String, Object> body = (Map<String, Object>) spa.get("body");
            String teacherId = (String) body.get("teacherId");
            String sectionId = (String) body.get("sectionId");
            for (Vertex student : graph.getVertices("mongoid", teacherId)) {
                logger.log(Level.INFO, "Found teacherId {0}", body.get("teacherId"));
                for (Vertex program : graph.getVertices("mongoid", body.get("sectionId"))) {
                    logger.log(Level.INFO, "Found sectionId {0}", body.get("sectionId"));
                    Edge e = graph.addEdge(null, program, student, "teacherSection");
                    e.setProperty("mongoid", spa.get("_id"));
                    // This may not be safe.
                    e.setProperty("endDate", body.get("endDate"));
                }
                
            }
            
        }
    }
}
