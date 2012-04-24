package org.slc.sli.common.util.threadutil;

import junit.framework.Assert;

import org.junit.Test;

/**
 *
 * ThreadLocalStorage Tests
 *
 * @author ifaybyshev
 *
 */
public class ThreadLocalStorageTest {

    @Test
    public void simpleTestThreadLocalStorage() {
        ThreadLocalStorage tls = new ThreadLocalStorage();
        tls.put("apple", "good");
        Assert.assertEquals("Failed to retrieve ThreadLocalStore value from same thread", "good", tls.get("apple"));
    }


    @Test
    public void multiThreadedTestThreadLocalStorage() throws InterruptedException {
        ThreadWorker tw1 = new ThreadWorker("123");
        ThreadWorker tw2 = new ThreadWorker("234");

        Thread t1 = new Thread(tw1);
        Thread t2 = new Thread(tw2);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        Assert.assertEquals("Failed to retrieve ThreadLocalStore value", "123", tw1.getValue());
        Assert.assertEquals("Failed to retrieve ThreadLocalStore value", "234", tw2.getValue());

    }


    /**
     * Thread Object used for testing
     *
     * @author ifaybyshev
     *
     */
    class ThreadWorker implements Runnable {

        private Object value;

        public ThreadWorker(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return this.value;
        }

        @Override
        public void run() {
            ThreadLocalStorage tls = new ThreadLocalStorage();
            tls.put("time", this.value);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.value = tls.get("time");
        }

    }

}