package org.dataprocessing.zipstreaming;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class ZipStreamerExecutor {

    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    private static final Executor EXECUTOR = Executors.newFixedThreadPool(NUM_THREADS);

    public static <T> FutureTask<T> execute(Callable<T> callable) {
        FutureTask<T> futureTask = new FutureTask<T>(callable);
        EXECUTOR.execute(futureTask);
        return futureTask;
    }
}
