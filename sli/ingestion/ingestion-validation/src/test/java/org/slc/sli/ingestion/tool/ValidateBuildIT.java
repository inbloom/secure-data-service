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

package org.slc.sli.ingestion.tool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import org.slc.sli.ingestion.landingzone.ZipFileUtil;


/**
 * @author tke
 *
 */
public class ValidateBuildIT {

    static final String MAVEN_TARGET_DIR = "target";
    static final String TARGET_DIR = "OfflineValidationTool";
    static final String SRC_FILE = "OfflineValidationTool-src.zip";

    @Test
    public void testMvnBuild() throws Exception {
        File srcZip = new File(MAVEN_TARGET_DIR, SRC_FILE);
        Assert.assertTrue(srcZip.exists() && srcZip.isFile());

        File mvnTargetDir = new File(MAVEN_TARGET_DIR);

        Assert.assertTrue("Maven Target directory is not found", mvnTargetDir.isDirectory());

        File targetDir = new File(MAVEN_TARGET_DIR, TARGET_DIR);

        PrintStream ps = System.out;

        try {
            ps.println();
            ps.println("================== Verifying the SLC Offline Validation Tool package ==================");
            ps.println();

            ZipFileUtil.extract(srcZip, mvnTargetDir, true);

            int ret = executeMvnBuild(targetDir);

            Assert.assertEquals(0, ret);
        } finally {
            ps.println();
            ps.println("================== End of verification of the SLC Offline Validation Tool package ==================");
            ps.println();
        }
    }

    private static int executeMvnBuild(File dir) throws InterruptedException, IOException {
        String osName = System.getProperty("os.name").toLowerCase();
        ArrayList<String> args = new ArrayList<String>();
        if (osName.contains("windows")) {
            args.add("cmd.exe");
            args.add("/C");
        }

        args.add("mvn");
        args.add("clean");
        args.add("package");

        ProcessBuilder pb = new ProcessBuilder(args);
        pb.redirectErrorStream(true);
        pb.directory(dir);

        Process p = pb.start();
        StreamRelay sg = new StreamRelay(p.getInputStream(), System.out);

        sg.start();

        return p.waitFor();
    }

    /**
     * Relays the InputStream into the OutputStream.
     *
     * @author okrook
     *
     */
    static class StreamRelay extends Thread {
        private static final int BUF_SIZE = 1024;

        private InputStream in;
        private OutputStream out;

        public StreamRelay(InputStream in, OutputStream out) {
            this.in = in;
            this.out = out;
        }

        @Override
        public void run() {
            final byte[] buffer = new byte[BUF_SIZE];

            try {
                int bytes = 0;

                while ((bytes = in.read(buffer, 0, BUF_SIZE)) != -1) {
                    out.write(buffer, 0, bytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
