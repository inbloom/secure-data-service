package org.sli.orient.importer.importers;

import java.util.Map;
import java.util.logging.Level;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public class StudentSchoolEdgeImporter extends BaseImporter {

    private static final String STUDENT_SCHOOL_ASSOCIATION = "studentSchoolAssociation";
    
    public StudentSchoolEdgeImporter(DB m, Graph g) {
        super(m, g);
    }

    /**
     * Imports student school associations (as edges) into the graph structure.
     */
    @Override
    public void importCollection() {
        logger.log(Level.INFO, "Importing collection into graph: " + STUDENT_SCHOOL_ASSOCIATION);
        
        DBCursor cursor = mongo.getCollection(STUDENT_SCHOOL_ASSOCIATION).find();
        cursor.batchSize(BATCH_SIZE);
        while (cursor.hasNext()) {
            DBObject association = cursor.next();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> body = (Map<String, Object>) association.get("body");
            String studentId = (String) body.get("studentId");
            String schoolId = (String) body.get("schoolId");
            for (Vertex student : graph.getVertices("mongoid", studentId)) {
                for (Vertex school : graph.getVertices("mongoid", schoolId)) {
                    Edge e = graph.addEdge(null, school, student, "studentSchoolAssociation");
                    e.setProperty("mongoid", association.get("_id"));
                    
                    if (body.containsKey("exitWithdrawDate")) {
                        e.setProperty("exitWithdrawDate", body.get("exitWithdrawDate"));
                    }
                    
                    logger.log(Level.INFO, "Adding an edge between school: " + schoolId + " --> student: " + studentId);
                }
            }            
        }
    }
}
