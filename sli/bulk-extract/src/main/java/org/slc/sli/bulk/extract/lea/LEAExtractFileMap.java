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

package org.slc.sli.bulk.extract.lea;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LEAExtractFileMap {
    private Map<String, ExtractFile> edOrgToLEAExtract;
    private static final Logger LOG = LoggerFactory.getLogger(LEAExtractFileMap.class);
    
    public LEAExtractFileMap(Map<String, ExtractFile> initialMap) {
        edOrgToLEAExtract = initialMap;
    }
    
    public ExtractFile getExtractFileForLea(String lea) {
        return edOrgToLEAExtract.get(lea);
    }
    
    public void setLeaToExtractMap(Map<String, ExtractFile> map) {
        this.edOrgToLEAExtract = map;
    }

    public void closeFiles() {
        for (ExtractFile file : edOrgToLEAExtract.values()) {
            file.closeWriters();
        }
        
    }
    
    public Set<String> getLeas() {
        return edOrgToLEAExtract.keySet();
    }

    public void buildManifestFiles(DateTime startTime) {
        for (ExtractFile file : edOrgToLEAExtract.values()) {
            try {
                file.getManifestFile().generateMetaFile(startTime);
            } catch (IOException e) {
                LOG.error("Error creating metadata file: {}", e.getMessage());
            }
        }
    }

    public void archiveFiles() {
        for (ExtractFile file : new HashSet<ExtractFile>(edOrgToLEAExtract.values())) {
            if(!file.generateArchive()) {
                LOG.warn("Unable to create archive: {}", file.getEdorg());
            }
        }
        
    }

}