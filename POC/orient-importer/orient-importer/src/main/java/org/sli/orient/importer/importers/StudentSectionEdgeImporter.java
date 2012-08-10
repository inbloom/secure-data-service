package org.sli.orient.importer.importers;

import java.util.Map;
import java.util.logging.Level;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public class StudentSectionEdgeImporter extends BaseImporter {
    
    public StudentSectionEdgeImporter(DB mongo, Graph graph) {
        super(mongo, graph);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.sli.orient.importer.importers.BaseImporter#importCollection()
     */
    @Override
    public void importCollection() {
        DBCursor cursor = mongo.getCollection("studentSectionAssociation").find();
        cursor.batchSize(BATCH_SIZE);
        while (cursor.hasNext()) {
            DBObject spa = cursor.next();
            @SuppressWarnings("unchecked")
            Map<String, Object> body = (Map<String, Object>) spa.get("body");
            String studentId = (String) body.get("studentId");
            String sectionId = (String) body.get("sectionId");
            for (Vertex student : graph.getVertices("mongoid", studentId)) {
                for (Vertex section : graph.getVertices("mongoid", sectionId)) {
                    Edge e = graph.addEdge(null, section, student, "studentSection");
                    e.setProperty("mongoid", spa.get("_id"));

                    if (body.containsKey("endDate")) {
                        e.setProperty("endDate", body.get("endDate"));
                    }
                    
                    logger.log(Level.INFO, "Adding an edge between section: " + sectionId + " --> student: " + studentId);
                }
            }            
        }        
    }
}
