package org.sli.orient.importer;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import org.sli.orient.importer.importers.BaseImporter;
import org.sli.orient.importer.importers.CohortImporter;
import org.sli.orient.importer.importers.EdOrgImporter;
import org.sli.orient.importer.importers.ProgramImporter;
import org.sli.orient.importer.importers.SectionImporter;
import org.sli.orient.importer.importers.StaffCohortEdgeImporter;
import org.sli.orient.importer.importers.StaffProgramEdgeImporter;
import org.sli.orient.importer.importers.StudentCohortEdgeImporter;
import org.sli.orient.importer.importers.StudentImporter;
import org.sli.orient.importer.importers.StudentProgramEdgeImporter;
import org.sli.orient.importer.importers.StudentSectionEdgeImporter;
import org.sli.orient.importer.importers.TeacherImporter;
import org.sli.orient.importer.importers.TeacherSchoolEdgeImporter;
import org.sli.orient.importer.importers.TeacherSectionEdgeImporter;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Mongo m = null;
        Graph g = new OrientGraph("local:~/Documents/orient");
        // Clear the graph first.
        try {
            m = new Mongo("127.0.0.1", 27017);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MongoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        DB db = m.getDB("sli");
        BaseImporter importer = new EdOrgImporter(db, g);
        importer.importCollection();

        importer = new SectionImporter(db, g);
        importer.importCollection();
        
        importer = new CohortImporter(db, g);
        importer.importCollection();
        
        importer = new ProgramImporter(db, g);
        importer.importCollection();
        
        importer = new StudentImporter(db, g);
        importer.importCollection();

        importer = new TeacherImporter(db, g);
        importer.importCollection();
        
        importer = new TeacherSchoolEdgeImporter(db, g);
        importer.importCollection();
        
        importer = new StudentProgramEdgeImporter(db, g);
        importer.importCollection();
        
        importer = new StudentCohortEdgeImporter(db, g);
        importer.importCollection();
        
        importer = new StudentSectionEdgeImporter(db, g);
        importer.importCollection();
        
        importer = new StaffProgramEdgeImporter(db, g);
        importer.importCollection();
        
        importer = new StaffCohortEdgeImporter(db, g);
        importer.importCollection();
        
        importer = new TeacherSectionEdgeImporter(db, g);
        importer.importCollection();

        g.shutdown();
        m.close();
    }
}
