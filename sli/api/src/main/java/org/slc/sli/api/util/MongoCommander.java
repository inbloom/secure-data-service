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
package org.slc.sli.api.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author tke
 *
 */
public class MongoCommander {
    protected static final Logger LOG = LoggerFactory.getLogger(MongoCommander.class);

    public static void exec(String cmd, CmdStreamGobbler outputGobber) {
        try {
        Process p = Runtime.getRuntime().exec(cmd);
        CmdStreamGobbler errorGobbler = new CmdStreamGobbler(p.getErrorStream(), "ERROR");

        errorGobbler.start();

            try {
                p.waitFor();
            } catch (InterruptedException e) {
                LOG.error(e.getMessage());
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }

    public static void exec(String db, String script, String jsContent) {
        try {
            URL scriptFile = Thread.currentThread().getContextClassLoader().getResource(script);
            if (scriptFile != null) {
                String path = (new File(scriptFile.getFile())).getPath();
                ProcessBuilder pb = new ProcessBuilder(new String[] { "mongo", db, "--eval", jsContent, path });
                Process pr = pb.start();
                CmdStreamGobbler errorGobbler = new CmdStreamGobbler(pr.getErrorStream(), "ERROR");
                errorGobbler.start();
                try {
                    pr.waitFor();
                } catch (InterruptedException e) {
                    LOG.error(e.getMessage());
                }
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }

}
