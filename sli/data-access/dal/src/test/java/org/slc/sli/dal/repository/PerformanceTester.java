package org.slc.sli.dal.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Hit MongoEntityRepository hard.
 *
 * @author dduran
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class PerformanceTester {

    @Autowired
    private Repository<Entity> repo;

    @Test
    @Ignore
    public void testPerformance() throws Exception {
        Map<String, Object> perfObject = new HashMap<String, Object>();
        perfObject.put("sectionId", "2012df-b2690f73-b5a0-11e1-890f-3c07544289e3");
        perfObject.put("studentId", "2012ma-b05aa25f-b5a0-11e1-890f-3c07544289e3");

        List<FutureTask<Entity>> futures = new ArrayList<FutureTask<Entity>>();

        long startTime = System.currentTimeMillis();
        for (int count = 0; count < 200000; count++) {
            futures.add(IngestionExecutor.execute(new InsertCallable(perfObject)));
        }

        for (FutureTask<Entity> futureEntity : futures) {
            futureEntity.get();
        }
        System.out.println("time taken:" + (System.currentTimeMillis() - startTime));
    }

    private class InsertCallable implements Callable<Entity> {
        private Map<String, Object> body;

        public InsertCallable(Map<String, Object> body) {
            this.body = body;
        }

        @Override
        public Entity call() throws Exception {
            return repo.create("studentSectionAssociation", body);
        }
    }

    private static class IngestionExecutor {

        private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
        private static final Executor EXECUTOR = Executors.newFixedThreadPool(NUM_THREADS);

        public static <T> FutureTask<T> execute(Callable<T> callable) {
            FutureTask<T> futureTask = new FutureTask<T>(callable);
            EXECUTOR.execute(futureTask);
            return futureTask;
        }
    }
}
