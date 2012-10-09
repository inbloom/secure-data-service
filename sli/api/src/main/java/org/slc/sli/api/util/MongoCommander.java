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

    static public void exec(String cmd, CmdStreamGobbler outputGobber){
        try {
        Process p = Runtime.getRuntime().exec(cmd);
        CmdStreamGobbler errorGobbler = new CmdStreamGobbler(p.getErrorStream(), "ERROR");

        errorGobbler.start();

            try {
                p.waitFor();
            } catch(InterruptedException e) {
                LOG.error(e.getMessage());
            }
        }catch(IOException e) {
            LOG.error(e.getMessage());
        }
    }

    static public void exec(String db, String script, String vars){
        try {
            URL scriptFile = Thread.currentThread().getContextClassLoader().getResource(script);
            if(scriptFile != null) {
                Runtime rt = Runtime.getRuntime();
                Process p = rt.exec(new String[] {"mongo",db,"--eval",vars,(new File(scriptFile.getFile())).getPath()});
                CmdStreamGobbler errorGobbler = new CmdStreamGobbler(p.getErrorStream(), "ERROR");

                errorGobbler.start();

            try {
                p.waitFor();
            } catch(InterruptedException e) {
                LOG.error(e.getMessage());
            }
            }
        }catch(IOException e) {
            LOG.error(e.getMessage());
        }
    }

}
