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
package org.slc.sli.bulk.extract.metadata;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.map.ObjectMapper;
import org.slc.sli.bulk.extract.zip.OutstreamZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tke
 *
 */
public class DataFile {

    private static final Logger LOG = LoggerFactory.getLogger(DataFile.class);

    private final static String VERSION = "1.0";

    public final static String METADATA_VERSION = "metadata_version=" + VERSION;
    public final static String API_VERSION = "api_version=";
    public final static String TIME_STAMP = "timeStamp=";

    public final static String METADATA_FILE = "metadata.txt";

    private String apiVersion = null;

    public DataFile(){
        //Empty constructor
    }

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

    public void writeToZip(OutstreamZipFile zip, String time) throws IOException{
        zip.createArchiveEntry(METADATA_FILE);
        zip.writeData(METADATA_VERSION);
        if(apiVersion == null){
            apiVersion = getApiVersion();
        }
        zip.writeData(API_VERSION + apiVersion);
        zip.writeData(TIME_STAMP + time);
    }
}
