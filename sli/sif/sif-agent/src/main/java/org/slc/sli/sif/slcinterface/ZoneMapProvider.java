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
package org.slc.sli.sif.slcinterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Provides a map of zone->SEA
 * @author sashton
 *
 */
@Component
public class ZoneMapProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ZoneMapProvider.class);

    private Map<String, SliEntityLocator> zoneIdToSliIdMap = new HashMap<String, SliEntityLocator>();

    @Value("${sli.sif-agent.zonemap}")
    String zonemap; // TODO: This is temporary; when we have a clear and
                    // approved sif id
                    // resolution strategy this can get swapped out

    @PostConstruct
    public void init() {
        zoneIdToSliIdMap = readIdMapFromFile(zonemap);
    }

    /*
     * Returns a modifiable collection, but this is only a temporary solution anyway
     */
    public Map<String, SliEntityLocator> getZoneToSliIdMap() {
        return zoneIdToSliIdMap;
    }

    // init function helper
    private Map<String, SliEntityLocator> readIdMapFromFile(String filename) {
        Map<String, SliEntityLocator> retVal = new HashMap<String, SliEntityLocator>();
        try {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
            BufferedReader mapFileReader = new BufferedReader(new InputStreamReader(in));
            while (true) {
                String line = mapFileReader.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith("#")) {
                    continue;
                }
                String[] entry = line.split(",");
                if (entry.length != 4) {
                    continue;
                }
                String sifId = entry[0];
                String sliValue = entry[1];
                String sliType = entry[2];
                String sliField = entry[3];
                retVal.put(sifId, new SliEntityLocator(sliType, sliValue, sliField));
            }
            mapFileReader.close();
        } catch (IOException e) {
            LOG.error("Error reading sif id resolver idMap file");
        }
        return retVal;
    }

}
