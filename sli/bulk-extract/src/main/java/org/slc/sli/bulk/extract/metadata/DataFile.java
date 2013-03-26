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
package org.slc.sli.bulk.extract.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.bulk.extract.Launcher;
import org.slc.sli.bulk.extract.zip.OutstreamZipFile;

/**
 * Class for meta data in the output extracted file.
 *
 * @author tke
 *
 */
public class DataFile {

    private static final Logger LOG = LoggerFactory.getLogger(DataFile.class);

    private static final String VERSION = "1.0";

    public static final String METADATA_VERSION = "metadata_version=" + VERSION;
    public static final String API_VERSION = "api_version=";
    public static final String TIME_STAMP = "timeStamp=";

    public static final String METADATA_FILE = "metadata.txt";

    private String apiVersion = null;

    /**
     * Empty constructor.
     */
    public DataFile(){
        //Empty constructor
    }

    /**
     * get the API version.
     * @return
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
     * write the meta data file into the zip file.
     * @param zip
     * @param date
     * @throws IOException
     */
    public void writeToZip(OutstreamZipFile zip, Date date) throws IOException{
        zip.createArchiveEntry(METADATA_FILE);
        zip.writeData(METADATA_VERSION);
        if(apiVersion == null){
            apiVersion = getApiVersion();
        }
        zip.writeData(API_VERSION + apiVersion);
        zip.writeData(TIME_STAMP + Launcher.getTimeStamp(date));
    }
}
