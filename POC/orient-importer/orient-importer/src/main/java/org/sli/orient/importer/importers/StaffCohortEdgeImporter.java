package org.sli.orient.importer.importers;

import java.util.List;
import java.util.Map;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public class StaffCohortEdgeImporter extends BaseImporter {
    
    public StaffCohortEdgeImporter(DB mongo, Graph graph) {
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
        DBCursor cursor = mongo.getCollection("staffCohortAssociation").find();
        while (cursor.hasNext()) {
            DBObject spa = cursor.next();
            Map<String, Object> body = (Map) spa.get("body");
            List<String> staffIds = (List) body.get("staffId");
            for (String staffId : staffIds) {
                for (Vertex staff : graph.getVertices("mongoid", staffId)) {
                    List<String> cohortIds = (List) body.get("cohortId");
                    for (String cohortId : cohortIds)
                        for (Vertex program : graph.getVertices("mongoid", cohortId)) {
                            Edge e = graph.addEdge(null, program, staff, "staffCohort");
                            e.setProperty("mongoid", spa.get("_id"));
                            // This may not be safe.
                            e.setProperty("endDate", body.containsKey("endDate") ? body.get("endDate") : "");
                            e.setProperty("studentRecordAccess", body.get("studentRecordAccess"));
                        }

                }
            }
            
        }
        
    }
    
}
