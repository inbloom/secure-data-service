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
package org.slc.sli.search.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;

public class AppLock {
    
    public AppLock(final String lockFile) throws IOException {
        final File lock = new File(lockFile);
        if (lock.exists()) {
            throw new IllegalStateException("Another instance of the application is running. " +
                    "Please delete lock file " + lock.getAbsolutePath() + " if not the case");
        }
        FileUtils.writeStringToFile(lock, String.format("File created at %1$tm/%1$te/%1$tY at %1$tH:%1$tM:%1$tS%n", new Date()));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (lock.exists() && !lock.delete()) {
                    throw new SearchIndexerException("Unable to delete lock file " + lockFile);
                }
            }
        });
    }   
}
