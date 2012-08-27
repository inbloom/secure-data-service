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
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.client.impl.BasicQuery;
import org.slc.sli.api.client.impl.GenericEntity;
import org.slc.sli.api.client.util.Query;

/**
 * Id resolver implementation using custom data
 *
 */
public class SifIdResolverCustomData implements SifIdResolver {

    private static final Logger LOG = LoggerFactory.getLogger(SifIdResolverCustomData.class);

    @Autowired
    SlcInterface slcInterface;

    private final Object lock = new Object();

    // not the most configurable, but it should suit our needs
    // private final String STATE_ORG_ID = "IL";

    @Value("${sli.sif-agent.zonemap}")
    String zonemap; // TODO: This is temporary; when we have a clear and
                    // approved sif id
                    // resolution strategy this can get swapped out

    private Map<String, SliId> zoneIdToSliIdMap;

    /**
    * Helper class
    */
    private static class SliId {
        String type, id, field;

        SliId(String t, String i, String f) {
            type = t;
            id = i;
            field = f;
        }

        SliId(Map<String, String> map) {
            type = map.get("type");
            id = map.get("value");
            field = map.get("field");
        }

        public Map<String, String> toMap() {
            Map<String, String> map = new HashMap<String, String>();
            map.put("type", type);
            map.put("value", id);
            map.put("field", field);
            return map;
        }

        @Override
        public String toString() {
            return "type = " + type + " ; field = " + field + " ; value = " + id;
        }
    }

    @PostConstruct
    public void init() {
        zoneIdToSliIdMap = readIdMapFromFile(zonemap);
    }

    @Override
    public String getSliGuid(String sifId, String zoneId) {
        List<String> guidList = getSliGuidList(sifId, zoneId);
        if (guidList != null && !guidList.isEmpty()) {
            return guidList.get(0);
        }

        return null;
    }

    @Override
    public List<String> getSliGuidList(String sifId, String zoneId) {
        synchronized (lock) {

            // check if it is in the map
            Map<String, List<Map<String, String>>> idMap = getIdMap(zoneId);
            if (!idMap.containsKey(sifId)) {
                return null;
            }

            List<String> guidList = new ArrayList<String>();

            List<Map<String, String>> listOfSliIds = idMap.get(sifId);
            for (Map<String, String> sliIdMap : listOfSliIds) {
                SliId sliId = new SliId(sliIdMap);
                String guid = digUpSliGuid(sliId);
                if (guid != null) {
                    guidList.add(guid);
                }
            }

            return guidList;
        }
    }

    @Override
    public String getSliGuidByType(String sifId, String sliType, String zoneId) {
        List<String> guidList = getSliGuidListByType(sifId, sliType, zoneId);
        if (guidList != null && !guidList.isEmpty()) {
            return guidList.get(0);
        }

        return null;
    }

    @Override
    public List<String> getSliGuidListByType(String sifId, String sliType, String zoneId) {
        synchronized (lock) {

            Map<String, List<Map<String, String>>> idMap = getIdMap(zoneId);
            if (!idMap.containsKey(sifId + "-" + sliType)) {
                return null;
            }

            List<String> guidList = new ArrayList<String>();
            // Return the first guid you find (in case type is repeated)
            List<Map<String, String>> listOfSliIds = idMap.get(sifId + "-" + sliType);
            for (Map<String, String> sliIdMap : listOfSliIds) {
                SliId sliId = new SliId(sliIdMap);
                String guid = digUpSliGuid(sliId);
                if (guid != null) {
                    guidList.add(guid);
                }
            }

            return guidList;
        }
    }

    @Override
    public Entity getSliEntityByType(String sifId, String sliType, String zoneId) {
        List<Entity> entityList = getSliEntityListByType(sifId, sliType, zoneId);
        if (entityList != null && !entityList.isEmpty()) {
            return entityList.get(0);
        }

        return null;
    }

    @Override
    public List<Entity> getSliEntityListByType(String sifId, String sliType, String zoneId) {

        synchronized (lock) {

            Map<String, List<Map<String, String>>> idMap = getIdMap(zoneId);
            if (!idMap.containsKey(sifId + "-" + sliType)) {
                return null;
            }

            List<Entity> entityList = new ArrayList<Entity>();

            List<Map<String, String>> listOfSliIds = idMap.get(sifId + "-" + sliType);
            for (Map<String, String> sliIdMap : listOfSliIds) {
                SliId sliId = new SliId(sliIdMap);
                Entity entity = digUpSliEntity(sliId);
                if (entity != null) {
                    entityList.add(entity);
                }
            }

            return entityList;

        }
    }

    @Override
    public Entity getSliEntity(String sifId, String zoneId) {
        List<Entity> entityList = getSliEntityList(sifId, zoneId);
        if (entityList != null && !entityList.isEmpty()) {
            return entityList.get(0);
        }

        return null;
    }

    @Override
    public List<Entity> getSliEntityList(String sifId, String zoneId) {
        synchronized (lock) {

            // check if it is in the map
            Map<String, List<Map<String, String>>> idMap = getIdMap(zoneId);
            if (!idMap.containsKey(sifId)) {
                return null;
            }

            List<Entity> entityList = new ArrayList<Entity>();

            List<Map<String, String>> listOfSliIds = idMap.get(sifId);
            for (Map<String, String> sliIdMap : listOfSliIds) {
                SliId sliId = new SliId(sliIdMap);
                Entity entity = digUpSliEntity(sliId);
                if (entity != null) {
                    entityList.add(entity);
                }
            }

            return entityList;
        }
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

    @Override
    public void putSliGuid(String sifId, String sliType, String sliId, String zoneId) {

        synchronized (lock) {

            String seaGuid = getZoneSea(zoneId);

            // check if it is in the map
            Map<String, List<Map<String, String>>> idMap = getIdMap(zoneId);
            SliId id = new SliId(sliType, sliId, ParameterConstants.ID);

            List<Map<String, String>> existingIdList = idMap.get(sifId);
            if (existingIdList == null) {
                existingIdList = new ArrayList<Map<String, String>>();
            }
            existingIdList.add(id.toMap());

            idMap.put(sifId, existingIdList);

            GenericEntity entity = new GenericEntity("custom", toGenericMap(idMap));
            slcInterface.create(entity, "/educationOrganizations/" + seaGuid + "/custom");
        }
    }

    @Override
    public void putSliGuidForOtherSifId(String sifId, String sliType, String sliId, String zoneId) {

        synchronized (lock) {

            String seaGuid = getZoneSea(zoneId);

            // check if it is in the map
            Map<String, List<Map<String, String>>> idMap = getIdMap(zoneId);
            SliId id = new SliId(sliType, sliId, ParameterConstants.ID);

            List<Map<String, String>> existingIdList = idMap.get(sifId);
            if (existingIdList == null) {
                existingIdList = new ArrayList<Map<String, String>>();
            }
            existingIdList.add(id.toMap());

            idMap.put(sifId + "-" + sliType, existingIdList);

            GenericEntity entity = new GenericEntity("custom", toGenericMap(idMap));
            slcInterface.create(entity, "/educationOrganizations/" + seaGuid + "/custom");
        }
    }

    // /**
    // * Ensures that an edorg has been found that can be used to store custom
    // data
    // */
    // private void ensureEdOrgId() {
    // if (customDataEdOrgId == null) {
    // List<Entity> list = slcInterface.read("/educationOrganizations");
    // for (Entity e : list) {
    // if (STATE_ORG_ID.equals(e.getData().get("stateOrganizationId"))) {
    // customDataEdOrgId = e.getId();
    // break;
    // }
    // }
    // if (customDataEdOrgId == null) {
    // throw new
    // RuntimeException("No EdOrg was found with a stateOrganizationId of '" +
    // STATE_ORG_ID
    // + "'. One is required to support SifIdResolver using custom data.");
    // }
    // }
    // }

    private Map<String, List<Map<String, String>>> getIdMap(String zoneId) {

        String seaGuid = getZoneSea(zoneId);

        LOG.info("Attempting to pull id map from SEA custom data, will cause exception if doesn't exist");
        List<Entity> list = slcInterface.read("/educationOrganizations/" + seaGuid + "/custom");
        if (list.size() > 0) {
            Map<String, Object> rawMap = list.get(0).getData();
            return toSpecificMap(rawMap);
        }
        return new HashMap<String, List<Map<String, String>>>();
    }

    private Map<String, Object> toGenericMap(Map<String, List<Map<String, String>>> map) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (Entry<String, List<Map<String, String>>> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<Map<String, String>>> toSpecificMap(Map<String, Object> map) {
        Map<String, List<Map<String, String>>> result = new HashMap<String, List<Map<String, String>>>();
        for (Entry<String, Object> entry : map.entrySet()) {
            List<Map<String, String>> idList = (List<Map<String, String>>) entry.getValue();
            result.put(entry.getKey(), idList);
        }
        return result;
    }

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

        Entity entity = retVal.get(0);
        if ("staffEducationOrganizationAssociation".equals(entity.getEntityType())) {
            entity = new GenericEntity("staffEducationOrgAssignmentAssociation", entity.getData());
        }

        return entity;
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

}
