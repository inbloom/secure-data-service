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


package org.slc.sli.lander;

import java.io.File;

import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.PosixParser;
import org.slc.sli.lander.config.UploadProperties;
import org.slc.sli.lander.exception.MissingConfigException;
import org.slc.sli.lander.util.FileUtils;
import org.slc.sli.lander.util.PropertyUtils;
import org.slc.sli.lander.util.SftpUtils;

public class FileLander {
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        new FileLander().run(args);
    }
    
    private UploadProperties properties = null;
    
    private void run(String[] args) {
        try {
            initialize(args);
            landData();
        } catch (MissingConfigException e) {
            System.out.println("Missing argument: -" + e.getFieldName());
            printUsage();
            System.exit(-1);
        } catch (MissingArgumentException e) {
            System.out.println(e.getMessage());
            printUsage();
            System.exit(-1);
        } catch (Exception e) {
            System.out.println("Unable to complete: " + e.getMessage());
            System.exit(-1);
        }
    }
    
    private void printUsage() {
        System.out
                .println("Required parameters: -u username -pass password -d localDirectory -s sftpServer -port port");
    }
    
    public void initialize(String[] args) throws Exception {
        properties = new PropertyUtils(new PosixParser()).getUploadProperties(args);
    }
    
    public void landData() throws Exception {
        if (properties == null) {
            return;
        }
        
        File fileToLand = FileUtils.zipIngestionData(properties.getLocalDir());
        SftpUtils.landFile(fileToLand, properties);
    }
}
