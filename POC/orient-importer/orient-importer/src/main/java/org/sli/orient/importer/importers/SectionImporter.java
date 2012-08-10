package org.sli.orient.importer.importers;

import com.mongodb.DB;
import com.tinkerpop.blueprints.Graph;

public class SectionImporter extends BaseImporter {
    
    public SectionImporter(DB mongo, Graph graph) {
        super(mongo, graph);

    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.sli.orient.importer.importers.BaseImporter#importCollection()
     */
    @Override
    public void importCollection() {
        extractBasicNode("section");
    }
    
}
