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

import javax.ws.rs.core.Response.Status;


import org.slc.sli.api.resources.security.TenantResource.TenantResourceCreationException;
/**
 * @author tke
 *
 */
public class MongoCommander {
    /**
     * Executes mongo command
     *
     * @param db
     *          the database name in mongo
     * @param script
     *          the name of the java script to be executed
     * @param jsContent
     *          javascript content to be evaluated before script execution
     * @return
     */
    public static void exec(String db, String script, String jsContent) {
        
        MongoCommander tke = new MongoCommander();
        tke.localExec(db, script, jsContent);
    }
    
    private void localExec(String db, String script, String jsContent) {
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
                    error(e.getMessage());
                }
                if (pr.exitValue() != 0) {
                    error("Failed to execute the script " + script + " during tenant onboarding");
                    throw new TenantResourceCreationException(Status.INTERNAL_SERVER_ERROR,
                            "Failed to spin up new tenant");
                }
            } else {
                error("Failed to locate the script " + script + " during tenant onboarding");
                throw new TenantResourceCreationException(Status.INTERNAL_SERVER_ERROR, "Failed to load scripts for new tenant spin up");
            }
        } catch (IOException e) {
            error(e.getMessage());
            throw new TenantResourceCreationException(Status.INTERNAL_SERVER_ERROR, "Failed to spin up new tenant");
        }
    }
    
    

}
