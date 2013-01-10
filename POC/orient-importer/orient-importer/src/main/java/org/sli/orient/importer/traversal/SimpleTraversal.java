package org.sli.orient.importer.traversal;

import java.util.logging.Level;

import com.mongodb.DB;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import org.sli.orient.importer.StopWatch;

public class SimpleTraversal extends BaseTraversal {
    
    public SimpleTraversal(DB mongo, OrientGraph graph) {
        super(mongo, graph);
        // TODO Auto-generated constructor stub
    }
    
    public void benchmark() {
        watch = new StopWatch();
        // benchmarkGetVertexById();
        times.clear();
        watch.stop();
        logger.log(Level.INFO, "Total time: {0}", watch.inSeconds());
    }
    
    private void benchmarkGetVertexById() {
        logger.log(Level.INFO, "Starting...");
        StopWatch bigWatch = new StopWatch();
        for (int i = 0; i < ITERATIONS; ++i) {
            StopWatch subWatch = new StopWatch();
            pipe.start(graph.getVertices("mongoid", "2012wl-f126bd7d-cfba-11e1-a172-024224a39f1b").iterator().next())
                    .outE("teacherSection");
            subWatch.stop();
            times.add(subWatch.getEndTime());
        }
        logger.log(Level.INFO, "Total {0} \t Avg {1} ms", new String[] { bigWatch.inSeconds(), "" + averageTime() });
    }

}
