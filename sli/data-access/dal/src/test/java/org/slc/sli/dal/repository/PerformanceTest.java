/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.dal.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
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
public class PerformanceTest {

    @Autowired
    private Repository<Entity> mongoEntityRepository;

    @Test
    @Ignore
    public void testSafePerformance() throws Exception {

        runPerfTest(100000, "SAFE");
    }

    @Test
    @Ignore
    public void testNormalPerformance() throws Exception {

        runPerfTest(100000, "NORMAL");
    }

    private void runPerfTest(int numToProcess, String writeConcern) throws InterruptedException, ExecutionException {
        mongoEntityRepository.setWriteConcern(writeConcern);

        Map<String, Object> perfObject = createObjectToInsert();

        List<FutureTask<Entity>> futures = new ArrayList<FutureTask<Entity>>();

        long startTime = System.currentTimeMillis();
        for (int count = 0; count < numToProcess; count++) {
            futures.add(IngestionExecutor.execute(new InsertCallable(perfObject, mongoEntityRepository)));
        }
        for (FutureTask<Entity> futureEntity : futures) {
            futureEntity.get();
        }
        double timeTaken = (System.currentTimeMillis() - startTime) / 1000.0;

//        System.out.println("time taken to insert " + numToProcess + " records with " + writeConcern
//                + " write concern: " + timeTaken + "s RPS: " + (numToProcess / timeTaken));
    }

    private Map<String, Object> createObjectToInsert() {
        Map<String, Object> perfObject = new HashMap<String, Object>();
        perfObject.put("sectionId", "2012df-b2690f73-b5a0-11e1-890f-3c07544289e3");
        perfObject.put("studentId", "2012ma-b05aa25f-b5a0-11e1-890f-3c07544289e3");
        return perfObject;
    }

    private static class InsertCallable implements Callable<Entity> {
        private Map<String, Object> body;
        private Repository<Entity> repo;

        public InsertCallable(Map<String, Object> body, Repository<Entity> repo) {
            this.body = body;
            this.repo = repo;
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
