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
package org.slc.sli.bulk.extract.files.metadata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for meta data in the output extracted file.
 *
 * @author tke
 *
 */
public class ManifestFile{

    private static final Logger LOG = LoggerFactory.getLogger(ManifestFile.class);
    private File metaFile;

    private static final  String VERSION = "1.0";
    private static final  String METADATA_VERSION = "metadata_version=";
    private static final String API_VERSION = "api_version=";
    private static  final String TIME_STAMP = "timeStamp=";
    private static  final String METADATA_FILE = "metadata.txt";

    private String apiVersion = null;

    /**
     * Parameterized constructor.
     *
     * @param parentDir
     *          Name of the parent directory
     */
    public ManifestFile(File parentDir) {
        metaFile = new File(parentDir, METADATA_FILE);
    }

    /**
     * Get the API version.
     * @return
     *      returns version of the API
     */
    public String getApiVersion(){
        InputStream is = getClass().getResourceAsStream("/api/resources.json");
        ObjectMapper mapper = new ObjectMapper();

        try {
            ApiNameSpace[] nameSpace = mapper.readValue(is, ApiNameSpace[].class);
            apiVersion = getLatestVersion(nameSpace);
        } catch (Exception e) {
            LOG.error("Failed to get the latest API version");
        }
        return apiVersion;

    }

    private String getLatestVersion(ApiNameSpace [] nameSpaces){
        String latest = "";
        for(ApiNameSpace nameSpace : nameSpaces){
            for(String version : nameSpace.getNameSpace()){
                if(version.compareToIgnoreCase(latest) > 0){
                    latest = version;
                }
            }
        }
        return latest;
    }

    /**
     * Generate the metadata file for the extract.
     *
     * @param startTime
     *          Timestamp of the time when extract started
     * @throws IOException
     *          throws IOException
     */
    public void generateMetaFile(DateTime startTime) throws IOException {
        String metaVersionEntry = METADATA_VERSION + VERSION;
        String timeStampEntry = TIME_STAMP + getTimeStamp(startTime);
        if (apiVersion == null) {
            apiVersion = getApiVersion();
        }
        String apiVersionEntry = API_VERSION + apiVersion;

        FileWriter fw = null;
        try {
            fw = new FileWriter(metaFile);

            fw.write(metaVersionEntry);
            fw.write('\n');
            fw.write(apiVersionEntry);
            fw.write('\n');
            fw.write(timeStampEntry);
            fw.write('\n');

            fw.flush();
        } finally {
            IOUtils.closeQuietly(fw);
        }
    }

    /**
     * Change the timestamp into our own format.
     * @param date
     *      Timestamp
     * @return
     *      returns the formatted timestamp
     */
    public static String getTimeStamp(DateTime date) {
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        String timeStamp = fmt.print(date);
        return timeStamp;
    }

    /**
     *Getter for the metadata file.
     * @return
     *      return File object for the metatdata file
     */
    public File getFile() {
        return metaFile;
    }
}
