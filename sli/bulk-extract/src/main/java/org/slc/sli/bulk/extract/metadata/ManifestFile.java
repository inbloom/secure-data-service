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

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tke
 *
 */
public class ManifestFile{

    private static final Logger LOG = LoggerFactory.getLogger(ManifestFile.class);
    private File metaFile;

    private final static String VERSION = "1.0";
    public final static String METADATA_VERSION = "metadata_version=";
    public final static String API_VERSION = "api_version=";
    public final static String TIME_STAMP = "timeStamp=";
    public final static String METADATA_FILE = "metadata.txt";

    private String apiVersion = null;

    public ManifestFile(String parentDirName) throws IOException{
        File parentDir = new File(parentDirName + "/");
        if (parentDir.isDirectory()) {
            metaFile = new File(parentDir, METADATA_FILE);
            metaFile.createNewFile();
        }
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

    public void generateMetaFile(Date startTime) throws IOException {

        String metaVersionEntry = METADATA_VERSION + VERSION;
        String timestampEntry = TIME_STAMP + startTime;
        if (apiVersion == null) {
            apiVersion = getApiVersion();
        }
        String apiVersionEntry = API_VERSION + apiVersion;

        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(metaFile);
            outputStream.write(metaVersionEntry.getBytes());
            outputStream.write('\n');
            outputStream.write(apiVersionEntry.getBytes());
            outputStream.write('\n');
            outputStream.write(timestampEntry.getBytes());
            outputStream.write('\n');
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    public File getFile() {
        return metaFile;
    }
}
