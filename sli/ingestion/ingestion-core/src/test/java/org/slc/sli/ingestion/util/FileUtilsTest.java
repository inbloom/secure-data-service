/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.ingestion.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bsuzuki
 *
 */
public class FileUtilsTest {

    private static final Logger LOG = LoggerFactory.getLogger(FileUtilsTest.class);

    private static final String testFileName = "FileUtilsTest.changetestfile.txt";

    private static class TestFileWriter implements Runnable {

        private static final long WRITE_TIME = 3000; // 3 secs
        private static final long WRITE_INTERVAL = 250;

        @Override
        public void run() {
            long stopTime = System.currentTimeMillis() + WRITE_TIME;

            FileWriter outputStream = null;

            try {
                try {
                    outputStream = new FileWriter(testFileName);

                    System.out.println("Writing test file");
                    System.out.flush();

                    while (System.currentTimeMillis() < stopTime) {

                        // modify file size
                        outputStream.write("test");
                        outputStream.flush();

                        System.out.println("Modified test file");
                        System.out.flush();

                        Thread.sleep(WRITE_INTERVAL);
                    }

                } catch (InterruptedException e) {
                    System.out.println("Interrupted!");
                    e.printStackTrace();
                } finally {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            System.out.println("Done writing test file");
            System.out.flush();
        }
    }

    /**
     * Happy path - file changes in size within the interval period and completes before the timeout
     * is hit
     */
    @Test
    public void testIsFileDoneChangingTrue() throws InterruptedException {

        boolean result = false;

        Thread t = new Thread(new TestFileWriter());
        t.start();

        File testFile = new File(testFileName);
        testFile.deleteOnExit();

        result = FileUtils.isFileDoneChanging(testFile, 500, 5000);

        t.join();

        Assert.assertEquals(true, result);
    }

    /**
     * Sad path 1 - file changes in size within the interval period, but the timeout is hit
     */
    @Test
    public void testIsFileDoneChangingFalseTimeout() throws InterruptedException {
        boolean result = false;

        Thread t = new Thread(new TestFileWriter());
        t.start();

        File testFile = new File(testFileName);
        testFile.deleteOnExit();

        result = FileUtils.isFileDoneChanging(testFile, 500, 2000);

        t.join();

        Assert.assertEquals(false, result);
    }

    /**
     * Sad path 2 - file does NOT change in size within the interval period
     */
    @Test
    public void testIsFileDoneChangingFalseInterval() throws InterruptedException {
        boolean result = false;

        Thread t = new Thread(new TestFileWriter());
        t.start();

        File testFile = new File(testFileName);
        testFile.deleteOnExit();

        result = FileUtils.isFileDoneChanging(testFile, 200, 5000);

        t.join();

        Assert.assertEquals(true, result);
    }

}
