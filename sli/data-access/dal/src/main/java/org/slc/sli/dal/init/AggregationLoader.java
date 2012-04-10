package org.slc.sli.dal.init;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * On startup loads all javascript function definitions into Mongo's memory.
 * These functions are used during map/reduce (aggregation) calculations.
 *
 * @author Kevin Myers kmyers@wgen.net
 *
 */
@Component
public class AggregationLoader {

    // ability to write to DEBUG output
    private static final Logger LOG = LoggerFactory.getLogger(AggregationLoader.class);

    private static final String PATH_PREFIX = "/aggregationDefinitions/";

    @Autowired
    private MongoTemplate template;

    /**
     * Constructor. Loads all definitions from file system into Mongo.
     */
    public AggregationLoader() {
    }

    @PostConstruct
    public void init() {
        try {
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

            if (loadJavascriptFiles(files)) {
                LOG.info("All aggregation definitions loaded.");
            } else {
                LOG.warn("All aggregation definitions not loaded.");
            }
        } catch (URISyntaxException e) {
            LOG.warn("Could not load aggregation definitions {}", new Object[] {e});
        }
    }

    /**
     * Loads the javascript files from the given list
     *
     * @return true if all definition folders process without producing any errors
     */
    private boolean loadJavascriptFiles(List<String> files) throws URISyntaxException {

        boolean allFilesLoaded = true;

        //go through and load the files
        for (String file : files) {
            if (!loadJavascriptFile(file)) {
                allFilesLoaded = false;
            }
        }

        return allFilesLoaded;
    }

    /**
     * Loads a specific javascript file into Mongo's javascript shell.
     *
     * @param fileName
     *            name of the file to be loaded
     * @return true if successful, false otherwise
     */
    private boolean loadJavascriptFile(String fileName) {

        // file IO can throw IOException(s)
        try {
            InputStream in = getClass().getResourceAsStream(PATH_PREFIX + fileName);

            StringBuffer fileData = new StringBuffer();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String temp = br.readLine();
            while (temp != null) {
                fileData.append(temp + "\n");
                temp = br.readLine();
            }
            br.close();
            fileData.append("\n");

            // tell mongo to execute what was just read from the file
            // return OK status from command execution
            return template.executeCommand("{\"$eval\":\"" + StringEscapeUtils.escapeJava(fileData.toString()) + "\"}")
                    .ok();
        } catch (IOException ioe) {
            LOG.debug("Failed to load definition file: {}", fileName);
            return false;
        }
    }
}
