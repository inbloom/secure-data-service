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


package org.slc.sli.util.transform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * @author dwilliams
 *
 */
public class Configuration {
    /**
     * 
     * @author dwilliams
     *
     */
    public enum OutputType {
        Fixture, EdFiXml
    }
    
    private static OutputType outputType = OutputType.Fixture;
    
    private static HashMap<String, String> xmlDestFiles = new HashMap<String, String>();
    static {
        xmlDestFiles.put(EducationalOrganization.class.getSimpleName(), "InterchangeSchool.xml");
        xmlDestFiles.put(School.class.getSimpleName(), "InterchangeSchool.xml");
        xmlDestFiles.put(Course.class.getSimpleName(), "InterchangeSchool.xml");
        
    }
    
    private static HashMap<String, OutputStreamWriter> streams = new HashMap<String, OutputStreamWriter>();
    
    public static String getOutputFileName(String key) {
        if (outputType.equals(OutputType.EdFiXml)) {
            return xmlDestFiles.get(key);
        } else {
            return null; // fixture file generation does this in a different way, should never call
                         // this method
        }
    }
    
    public static OutputStreamWriter getWriter(String className) {
        OutputStreamWriter answer = streams.get(className);
        if (answer == null) {
            String filename = xmlDestFiles.get(className);
            try {
                answer = new OutputStreamWriter(new FileOutputStream("." + File.separator + filename));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return answer;
    }
    
    public static OutputType getOutputType() {
        return outputType;
    }
    
    public void closeStreams() {
        Iterator<String> keys = streams.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            OutputStreamWriter osw = streams.get(key);
            try {
                osw.flush();
                osw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void setOutputType(OutputType type) {
        outputType = type;
    }
}
