package org.sli.orient.importer.traversal;

import java.util.logging.Level;

import com.mongodb.DB;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.gremlin.java.GremlinPipeline;

import org.sli.orient.importer.StopWatch;

public class TeacherStudentTraversal extends BaseTraversal {

    public TeacherStudentTraversal(DB mongo, OrientGraph graph) {
        super(mongo, graph);
    }
    
    public void benchmark() {
        watch = new StopWatch();
        benchmarkTeacherThroughSectionBlueprint();
        times.clear();
        // benchmarkTeacherThroughSection();
        // times.clear();
        benchmarkTeacherThroughCohort();
        times.clear();
        benchmarkTeacherThroughProgram();
        times.clear();
        watch.stop();
        logger.log(Level.INFO, "\t {0}", watch.inSeconds());
    }

    private void benchmarkTeacherThroughSectionBlueprint() {
        logger.log(Level.INFO, "Starting...");
        StopWatch bigWatch = new StopWatch();
        for (int i = 0; i < ITERATIONS; ++i) {
            StopWatch subWatch = new StopWatch();
            for (Vertex teacher : graph.getVertices("mongoid", "2012wl-f126bd7d-cfba-11e1-a172-024224a39f1b")) {
                for(Edge  e : teacher.getEdges(Direction.IN, "teacherSection")) {
                    for (Edge ss : e.getVertex(Direction.OUT).getEdges(Direction.OUT, "studentSection")) {
                        ss.getVertex(Direction.IN);
                    }
                }
            }
            subWatch.stop();
            times.add(subWatch.getEndTime());
        }
        bigWatch.stop();
        logger.log(Level.INFO, "Total {0} \t Avg {1} ms", new String[] { bigWatch.inSeconds(), "" + averageTime() });
        
    }

    void benchmarkTeacherThroughSection() {
        logger.log(Level.INFO, "Starting...");
        StopWatch bigWatch = new StopWatch();
        for (int i = 0; i < ITERATIONS; ++i) {
            StopWatch subWatch = new StopWatch();
            pipe = new GremlinPipeline();
            long students = pipe
                    .start(graph.getVertices("mongoid", "2012wl-f126bd7d-cfba-11e1-a172-024224a39f1b").iterator()
                            .next()).inE("teacherSection").outV().outE("studentSection").inV().count();
            subWatch.stop();
            logger.log(Level.INFO, "Found {0} students", "" + students);
            times.add(subWatch.getEndTime());
        }
        bigWatch.stop();
        logger.log(Level.INFO, "Total {0} \t Avg {1} ms", new String[] { bigWatch.inSeconds(), "" + averageTime() });
    }
    
    void benchmarkTeacherThroughCohort() {
        logger.log(Level.INFO, "Starting...");
        StopWatch bigWatch = new StopWatch();
        for (int i = 0; i < ITERATIONS; ++i) {
            StopWatch subWatch = new StopWatch();
            pipe = new GremlinPipeline();
            long students = pipe
                    .start(graph.getVertices("mongoid", "2012zc-a9b37634-cfbb-11e1-a172-024224a39f1b").iterator()
                            .next()).inE("staffCohort").count();
            subWatch.stop();
            logger.log(Level.INFO, "Found {0} students", "" + students);
            times.add(subWatch.getEndTime());
        }
        bigWatch.stop();
        logger.log(Level.INFO, "Total {0} \t Avg {1} ms", new String[] { bigWatch.inSeconds(), "" + averageTime() });
    }
    
    void benchmarkTeacherThroughProgram() {
        logger.log(Level.INFO, "Starting...");
        StopWatch bigWatch = new StopWatch();
        for (int i = 0; i < ITERATIONS; ++i) {
            StopWatch subWatch = new StopWatch();
            pipe = new GremlinPipeline();
            long students = pipe
                    .start(graph.getVertices("mongoid", "2012wo-a9d9e975-cfbb-11e1-a172-024224a39f1b").iterator()
                            .next()).inE("staffProgram").outV().outE("staffCohort").inV().count();
            subWatch.stop();
            logger.log(Level.INFO, "Found {0} students", "" + students);
            times.add(subWatch.getEndTime());
        }
        bigWatch.stop();
        logger.log(Level.INFO, "Total {0} \t Avg {1} ms", new String[] { bigWatch.inSeconds(), "" + averageTime() });
        
    }
}
