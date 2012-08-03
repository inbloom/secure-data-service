package org.sli.orient.importer;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import org.sli.orient.importer.importers.BaseImporter;
import org.sli.orient.importer.importers.EdOrgImporter;
import org.sli.orient.importer.importers.SectionImporter;
import org.sli.orient.importer.importers.TeacherImporter;
import org.sli.orient.importer.importers.TeacherSchoolEdgeImporter;

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
        
        importer = new TeacherImporter(db, g);
        importer.importCollection();
        
        importer = new TeacherSchoolEdgeImporter(db, g);
        importer.importCollection();

        g.shutdown();
    }
}
