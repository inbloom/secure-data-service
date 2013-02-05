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


package org.slc.sli.ingestion.service;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Fixed thread pool Executor for Ingestion
 *
 * @author dduran
 *
 */
public final class IngestionExecutor {

    private IngestionExecutor() { }

    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    private static final Executor EXECUTOR = Executors.newFixedThreadPool(NUM_THREADS);

    public static <T> FutureTask<T> execute(Callable<T> callable) {
        FutureTask<T> futureTask = new FutureTask<T>(callable);
        EXECUTOR.execute(futureTask);
        return futureTask;
    }
}
