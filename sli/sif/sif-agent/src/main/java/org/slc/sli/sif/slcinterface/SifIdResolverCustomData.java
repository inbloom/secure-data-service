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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.client.impl.BasicQuery;
import org.slc.sli.api.client.impl.GenericEntity;
import org.slc.sli.api.client.util.Query;

public class SifIdResolverCustomData implements SifIdResolver {

    @Autowired
    SlcInterface slcInterface;

    private final Object lock = new Object();

    // not the most configurable, but it should suit our needs
    private final String STATE_ORG_ID = "IL";

    private String customDataEdOrgId = null;

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

    @Override
    public String getSliGuid(String sifId) {

        synchronized (lock) {
            ensureEdOrgId();

            Map<String, Map<String, String>> idMap = getIdMap();
            if (!idMap.containsKey(sifId)) {
                return null;
            }

            SliId sliId = new SliId(idMap.get(sifId));
            return digUpSliGuid(sliId);
        }
    }

    @Override
    public Entity getSliEntity(String sifId) {

        synchronized (lock) {
            ensureEdOrgId();

            // check if it is in the map
            Map<String, Map<String, String>> idMap = getIdMap();
            if (!idMap.containsKey(sifId)) {
                return null;
            }

            SliId sliId = new SliId(idMap.get(sifId));
            return digUpSliEntity(sliId);
        }

    }

    // @Override
    // public String getZoneSea(String sifZoneId) {
    // // TODO Auto-generated method stub
    // return null;
    // }

    @Override
    public void putSliGuid(String sifId, String sliType, String sliId) {

        synchronized (lock) {
            ensureEdOrgId();

            // check if it is in the map
            Map<String, Map<String, String>> idMap = getIdMap();
            SliId id = new SliId(sliType, sliId, ParameterConstants.ID);

            idMap.put(sifId, id.toMap());

            GenericEntity entity = new GenericEntity("custom", toGenericMap(idMap));
            String guid = slcInterface.create(entity,
                    "/educationOrganizations/2012at-6dc60eb7-dcc5-11e1-95f6-0021701f543f/custom");
        }
    }

    /**
     * Ensures that an edorg has been found that can be used to store custom data
     */
    private void ensureEdOrgId() {
        if (customDataEdOrgId == null) {
            List<Entity> list = slcInterface.read("/educationOrganizations");
            for (Entity e : list) {
                if (STATE_ORG_ID.equals(e.getData().get("stateOrganizationId"))) {
                    customDataEdOrgId = e.getId();
                    break;
                }
            }
            if (customDataEdOrgId == null) {
                throw new RuntimeException("No EdOrg was found with a stateOrganizationId of '" + STATE_ORG_ID
                        + "'. One is required to support SifIdResolver using custom data.");
            }
        }
    }

    private Map<String, Map<String, String>> getIdMap() {

        List<Entity> list = slcInterface.read("/educationOrganizations/" + customDataEdOrgId + "/custom");
        if (list.size() > 0) {
            Map<String, Object> rawMap = list.get(0).getData();
            return toSpecificMap(rawMap);
        }
        return new HashMap<String, Map<String, String>>();
    }

    private Map<String, Object> toGenericMap(Map<String, Map<String, String>> map) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (Entry<String, Map<String, String>> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Map<String, String>> toSpecificMap(Map<String, Object> map) {
        Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
        for (Entry<String, Object> entry : map.entrySet()) {
            result.put(entry.getKey(), (Map<String, String>) entry.getValue());
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
        return retVal.get(0);
    }

}
