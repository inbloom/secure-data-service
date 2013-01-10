package org.sli.orient.importer.importers;

import java.util.List;
import java.util.Map;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public class StaffProgramEdgeImporter extends BaseImporter {
    
    /*
     * (non-Javadoc)
     * 
     * @see org.sli.orient.importer.importers.BaseImporter#importCollection()
     */
    @Override
    public void importCollection() {
        DBCursor cursor = mongo.getCollection("staffProgramAssociation").find();
        while (cursor.hasNext()) {
            DBObject spa = cursor.next();
            Map<String, Object> body = (Map) spa.get("body");
            List<String> staffIds = (List) body.get("staffId");
            for (String staffId : staffIds) {
                for (Vertex staff : graph.getVertices("mongoid", staffId)) {
                    List<String> cohortIds = (List) body.get("programId");
                    for (String cohortId : cohortIds)
                        for (Vertex program : graph.getVertices("mongoid", cohortId)) {
                            Edge e = graph.addEdge(null, program, staff, "staffProgram");
                            e.setProperty("mongoid", spa.get("_id"));
                            // This may not be safe.
                            e.setProperty("endDate", body.containsKey("endDate") ? body.get("endDate") : "");
                            e.setProperty("studentRecordAccess", body.get("studentRecordAccess"));
                        }
                    
                }
            }
            
        }
    }
    
    public StaffProgramEdgeImporter(DB mongo, Graph graph) {
        super(mongo, graph);
    }
    
}
