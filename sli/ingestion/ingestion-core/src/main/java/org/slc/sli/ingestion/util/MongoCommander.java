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
import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tke
 *
 */
public final class MongoCommander {

    private MongoCommander() { }

    protected static final Logger LOG = LoggerFactory.getLogger(MongoCommander.class);

    /**
     * Executes mongo command
     *
     * @param db
     *            the database name in mongo
     * @param script
     *            the name of the java script to be executed
     * @param jsContent
     *            javascript content to be evaluated before script execution
     * @return
     */
    public static void exec(String db, String script, String jsContent) {
        try {
            URL scriptFile = Thread.currentThread().getContextClassLoader().getResource(script);
            if (scriptFile != null) {
                String path = (new File(scriptFile.getFile())).getPath();
                String[] args = { "mongo", db, "--eval", jsContent, path };

                LOG.info("Running process with args: {} {} {} {} {}", args);
                ProcessBuilder pb = new ProcessBuilder(args);
                Process pr = pb.start();
                try {
                    pr.waitFor();
                } catch (InterruptedException e) {
                    LOG.error(e.getMessage());
                }
                if (pr.exitValue() != 0) {
                    LOG.error("Failed to execute the script " + script + " during tenant onboarding");
                    throw new RuntimeException("Failed to execute the script " + script + " during tenant onboarding");
                }
            } else {
                LOG.error("Failed to locate the script " + script + " during tenant onboarding");
                throw new RuntimeException("Failed to locate the script " + script + " during tenant onboarding");
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
