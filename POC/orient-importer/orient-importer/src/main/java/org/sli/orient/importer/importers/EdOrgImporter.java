package org.sli.orient.importer.importers;

import java.util.Map;
import java.util.logging.Level;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public class EdOrgImporter extends BaseImporter {
    
    private static final String EDUCATION_ORGANIZATION = "educationOrganization";
    private static final String EDUCATION_ORGANIZATION_ASSOCIATION = "educationOrganizationAssociation";
    
    public EdOrgImporter(DB m, Graph g) {
        super(m, g);
    }
    
    /**
     * Imports education organizations into the graph structure. Then coalesces hierarchy by
     * 'parentEducationAgencyReference' contained in the 'body' of each education organization.
     */
    @Override
    public void importCollection() {
        DBCursor cursor = mongo.getCollection(EDUCATION_ORGANIZATION).find();
        while (cursor.hasNext()) {
            DBObject edOrg = cursor.next();
            String currentEdOrgId = (String) edOrg.get("_id");
            
            Vertex v = graph.addVertex(null);
            logger.log(Level.INFO, "Adding vertex for {0}#{1} \t {2}", new String[] { EDUCATION_ORGANIZATION,
                    currentEdOrgId, v.getId().toString() });
            
            v.setProperty("mongoid", currentEdOrgId);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> body = (Map<String, Object>) edOrg.get("body");
            if (body.containsKey("parentEducationAgencyReference")) {
                String parentId = (String) body.get("parentEducationAgencyReference");
                for (Vertex child : graph.getVertices("mongoid", currentEdOrgId)) {
                    for (Vertex parent : graph.getVertices("mongoid", parentId)) {
                        graph.addEdge(null, parent, child, EDUCATION_ORGANIZATION_ASSOCIATION);
                        logger.log(Level.INFO, "Adding an edge between ed org: " + parentId + " --> " + currentEdOrgId);
                    }
                }
            }
        }
    }
}
