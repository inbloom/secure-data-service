package org.sli.orient.importer.traversal;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.mongodb.DB;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.gremlin.java.GremlinPipeline;

import org.sli.orient.importer.StopWatch;

public class BaseTraversal {
    
    protected static Logger logger = Logger.getLogger("traversal");
    protected DB mongo;
    protected static final int ITERATIONS = 100;
    protected OrientGraph graph;
    protected StopWatch watch;
    protected List<Long> times;
    protected GremlinPipeline pipe;

    public BaseTraversal(DB mongo, OrientGraph graph) {
        this.mongo = mongo;
        this.graph = graph;
        pipe = new GremlinPipeline();
        times = new ArrayList<Long>();
    }
    
    public void benchmark() {
        
    }
    
    protected long averageTime() {
        long total = 0;
        for (Long time : times) {
            total += time;
        }
        return total / times.size();
    }

}
