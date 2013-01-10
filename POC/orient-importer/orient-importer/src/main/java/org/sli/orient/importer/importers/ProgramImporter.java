package org.sli.orient.importer.importers;

import com.mongodb.DB;
import com.tinkerpop.blueprints.Graph;

public class ProgramImporter extends BaseImporter {
    
    public ProgramImporter(DB mongo, Graph graph) {
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
        extractBasicNode("program");
    }
}
