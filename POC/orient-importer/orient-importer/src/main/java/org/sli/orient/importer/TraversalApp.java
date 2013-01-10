package org.sli.orient.importer;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import org.sli.orient.importer.traversal.BaseTraversal;
import org.sli.orient.importer.traversal.SimpleTraversal;
import org.sli.orient.importer.traversal.TeacherStudentTraversal;

public class TraversalApp {
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Mongo m = null;
        OrientGraph g = new OrientGraph("remote:localhost/security", "admin", "admin");
        
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
        
        BaseTraversal traversal = new SimpleTraversal(db, g);
        traversal.benchmark();

        traversal = new TeacherStudentTraversal(db, g);
        traversal.benchmark();
    }
    
}
