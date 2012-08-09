package org.sli.orient.importer;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.tinkerpop.blueprints.impls.orient.OrientBatchGraph;

import org.sli.orient.importer.importers.BaseImporter;

/**
 * Hello world!
 *
 */
public class App 
{
    private static Logger log = Logger.getLogger("Orient-Importer");
    private static int count = 0;
    private static long totalTime = 0;
    private static OrientBatchGraph g;
    public static void main( String[] args )
    {
        Mongo m = null;
        g = new OrientBatchGraph("remote:localhost/security", "admin", "admin");


        // // Clear the graph first.
        // for (Vertex v : g.getVertices()) {
        // g.removeVertex(v);
        // }
        // // g.stopTransaction(Conclusion.SUCCESS);
        // log.log(Level.INFO, "Verticies removed...");
        //
        // for (Edge e : g.getEdges()) {
        // g.removeEdge(e);
        // }
        // // g.stopTransaction(Conclusion.SUCCESS);
        // log.log(Level.INFO, "Edges removed...");
        //
        // g.createKeyIndex("mongoid", Vertex.class);
        // g.stopTransaction(Conclusion.SUCCESS);

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
        // BaseImporter importer = new EdOrgImporter(db, g);
        // benchmarkStamp(importer);
        //
        // // importer = new SectionImporter(db, g);
        // // benchmarkStamp(importer);
        // //
        // // importer = new TeacherImporter(db, g);
        // // benchmarkStamp(importer);
        //
        // importer = new CohortImporter(db, g);
        // benchmarkStamp(importer);
        //
        // importer = new ProgramImporter(db, g);
        // benchmarkStamp(importer);
        //
        // importer = new StudentImporter(db, g);
        // benchmarkStamp(importer);
        //
        // importer = new TeacherSchoolEdgeImporter(db, g);
        // benchmarkStamp(importer);
        //
        // importer = new StudentProgramEdgeImporter(db, g);
        // benchmarkStamp(importer);
        //
        // importer = new StudentCohortEdgeImporter(db, g);
        // benchmarkStamp(importer);
        //
        // importer = new StudentSectionEdgeImporter(db, g);
        // benchmarkStamp(importer);
        //
        // importer = new StaffProgramEdgeImporter(db, g);
        // benchmarkStamp(importer);
        //
        // importer = new StaffCohortEdgeImporter(db, g);
        // benchmarkStamp(importer);
        
        // importer = new TeacherSectionEdgeImporter(db, g);
        // benchmarkStamp(importer);

        

        if (totalTime > 0 && count > 0) {
            
            log.log(Level.INFO, "\t Avg {0}\tTotal {1}\tTotal Records: {2}", new String[] { "" + totalTime / count,
                    "" + totalTime, "" + count });
        }
        
        // OGremlinHelper.global().create();
        // List<Vertex> result = g.getRawGraph().command(new
        // OCommandGremlin("g.V[0..5]")).execute();
        
        g.shutdown();
        m.close();
    }
    
    private static void benchmarkStamp(BaseImporter stamper) {
        log.log(Level.INFO, "Starting to import {0}", stamper.getClass().getName());
        long startTime = System.currentTimeMillis();
        stamper.importCollection();
        // g.stopTransaction(Conclusion.SUCCESS);
        long endTime = System.currentTimeMillis() - startTime;
        log.log(Level.INFO, "{0} in {1} ms", new String[] { stamper.getClass().getName(), "" + endTime });
        totalTime += endTime;
        count++;
    }
}
