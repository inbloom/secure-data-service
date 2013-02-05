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


package org.slc.sli.dal.init;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * On startup loads all javascript function definitions into Mongo's memory.
 * These functions are used during map/reduce (aggregation) calculations.
 *
 * @author Kevin Myers kmyers@wgen.net
 *
 */

public class AggregationLoader {

    // ability to write to DEBUG output
    private static final Logger LOG = LoggerFactory.getLogger(AggregationLoader.class);

    private static final String PATH_PREFIX = "/aggregationDefinitions/";

    @Qualifier("mongoTemplate")
    @Autowired(required = false)
    private MongoTemplate template;

/*    *//**
     * Constructor. Loads all definitions from file system into Mongo.
     *//*
    public AggregationLoader() {
    }*/


    public void init() {
        if (template != null) {
            if (loadJavascriptFiles(getFiles(), PATH_PREFIX)) {
                LOG.info("All aggregation definitions loaded.");
            } else {
                LOG.warn("All aggregation definitions not loaded.");
            }
        }
    }

    /**
     * Retrieves the list of files to be loaded
     * @return list of files to load
     */
    protected List<String> getFiles() {
        List<String> files = new ArrayList<String>();

        files.add("cleanupFunctions/cleanupBodyAndId.js");
        files.add("finalizeFunctions/finalizePerf1to4.js");

        files.add("mapFunctions/mapDistrictPerf1to4.js");
        files.add("mapFunctions/mapSchoolPerf1to4.js");
        files.add("mapFunctions/mapSectionAttendance.js");
        files.add("mapFunctions/mapTeacherPerf1to4.js");

        files.add("other/uuid.js");
        files.add("other/uuidhelpers.js");

        files.add("reduceFunctions/reducePerf1to4.js");
        files.add("reduceFunctions/reduceSectionAttendance.js");

        return files;
    }

    /**
     * Loads the javascript files from the given list
     * @param pathPrefix
     *            location of files to load
     * @param files
     *            list of files to load
     *
     * @return true if all definition folders process without producing any errors
     */
    private boolean loadJavascriptFiles(List<String> files, String pathPrefix) {

        boolean allFilesLoaded = true;

        //go through and load the files
        for (String file : files) {
            InputStream in = getClass().getResourceAsStream(pathPrefix + file);
            String command = loadJavascriptFile(in);

            if (!executeString(command)) {
                allFilesLoaded = false;
            }
        }

        return allFilesLoaded;
    }

    /**
     * Loads a specific javascript file into Mongo's javascript shell.
     *
     * @param in
     *          The input stream to read from
     * @return the file loaded as a string
     */
    protected String loadJavascriptFile(InputStream in) {

        // file IO can throw IOException(s)
        try {

            if (in == null) {
                return "";
            }

            StringBuffer fileData = new StringBuffer();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String temp = br.readLine();
            while (temp != null) {
                fileData.append(temp);
                fileData.append("\n");
                temp = br.readLine();
            }
            br.close();
            fileData.append("\n");

            return fileData.toString();

        } catch (IOException ioe) {
            LOG.debug("Failed to load definition file");
            return "";
        }
    }

    /**
     * Executes a command given in a string
     * @param command The command to execute against the mongo template
     * @return true if command was executed successfully, false otherwise
     */
    private boolean executeString(String command) {
        return template.executeCommand("{\"$eval\":\"" + StringEscapeUtils.escapeJava(command) + "\"}").ok();
    }
}
