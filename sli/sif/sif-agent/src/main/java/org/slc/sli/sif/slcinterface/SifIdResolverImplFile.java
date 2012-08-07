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

package org.slc.sli.sif.slcinterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.client.impl.BasicQuery;
import org.slc.sli.api.client.util.Query;

/**
 * Mapping between SIF id and SLI ids. File implementation
 */
public class SifIdResolverImplFile implements SifIdResolver {

    private static final Logger LOG = LoggerFactory.getLogger(SifIdResolverImplFile.class);

    @Value("${sli.sif-agent.idmap}")
    private String idmap; // TODO: This is temporary; when we have a clear and approved sif id
                          // resolution strategy this can get swapped out
    @Value("${sli.sif-agent.zonemap}")
    private String zonemap; // TODO: This is temporary; when we have a clear and approved sif id
                            // resolution strategy this can get swapped out

    @Autowired
    private SlcInterface slcInterface;

    /**
     * Helper class
     */
    class SliId {
        String type, id, field;

        SliId(String t, String i, String f) {
            type = t;
            id = i;
            field = f;
        }

        @Override
        public String toString() {
            return "type = " + type + " ; field = " + field + " ; value = " + id;
        }
    }

    private Map<String, SliId> sifToSliIdMap;
    private Map<String, SliId> zoneIdToSliIdMap;

    @PostConstruct
    public void init() {
        sifToSliIdMap = readIdMapFromFile(idmap);
        zoneIdToSliIdMap = readIdMapFromFile(zonemap);
    }

    @Override
    public String getSliGuid(String sifId) {
        // check if it is in the map
        if (!sifToSliIdMap.containsKey(sifId)) {
            return null;
        }
        SliId sliId = sifToSliIdMap.get(sifId);
        return digUpSliGuid(sliId);
    }

    @Override
    public Entity getSliEntity(String sifId) {
        // check if it is in the map
        if (!sifToSliIdMap.containsKey(sifId)) {
            return null;
        }
        SliId sliId = sifToSliIdMap.get(sifId);
        return digUpSliEntity(sliId);
    }

    @Override
    public String getZoneSea(String zoneId) {
        // check if it is in the map
        if (!zoneIdToSliIdMap.containsKey(zoneId)) {
            return null;
        }
        SliId sliId = zoneIdToSliIdMap.get(zoneId);
        return digUpSliGuid(sliId);
    }

    // dig up the SLI Guid from the api given a SliId.
    private String digUpSliGuid(SliId sliId) {
        Entity e = digUpSliEntity(sliId);
        return e == null ? null : e.getId();
    }

    // dig up the SLI Entity from the api given a SliId.
    // Returns null is none is found,.
    // throws runtime exception if more than one is found.
    private Entity digUpSliEntity(SliId sliId) {
        // dig up the SLI Id from the SLI database
        String id = sliId.id;
        String type = sliId.type;
        String queryField = sliId.field;
        List<Entity> retVal = new ArrayList<Entity>();
        try {
            Query query = BasicQuery.Builder.create().filterEqual(queryField, id).maxResults(1).build();
            slcInterface.read(retVal, type, query);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (retVal.size() == 0) {
            return null;
        }

        if (retVal.size() > 1) {
            throw new RuntimeException("  SIF Ref ID Resolution error: resolves to more than one entity: " + sliId);
        }
        return retVal.get(0);
    }

    // init function helper
    private Map<String, SliId> readIdMapFromFile(String filename) {
        Map<String, SliId> retVal = new HashMap<String, SliId>();
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream(filename);
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
                retVal.put(sifId, new SliId(sliType, sliValue, sliField));
            }
            mapFileReader.close();
        } catch (IOException e) {
            LOG.error("Error reading sif id resolver idMap file");
        }
        return retVal;
    }

    /**
     * Implements putSliGuid(). Does not persist.
     *
     * @param sifId
     * @param sliType
     * @param sliId
     */
    @Override
    public void putSliGuid(String sifId, String sliType, String sliId) {
        SliId id = new SliId(sliType, sliId, ParameterConstants.ID);
        sifToSliIdMap.put(sifId, id);
    }
}
