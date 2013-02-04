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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.SLIClientException;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.client.impl.BasicQuery;
import org.slc.sli.api.client.util.Query;

/**
 * Id resolver implementation using custom data
 *
 *
 */
public class SifIdResolverCustomData implements SifIdResolver {

    private static final Logger LOG = LoggerFactory.getLogger(SifIdResolverCustomData.class);

    @Autowired
    SlcInterface slcInterface;

    @Autowired
    ZoneMapProvider zoneMapProvider;

    @Autowired
    SeaCustomDataProvider customDataProvider;

    private final Object lock = new Object();

    @Override
    public String getSliGuid(String sifId, String zoneId) {

        synchronized (lock) {
            Entity entity = getSliEntity(sifId, zoneId);
            if (entity == null) {
                LOG.info("No sli id found for sifId(" + sifId + ")");
                return null;
            }
            return entity.getId();
        }
    }

    @Override
    public List<String> getSliGuidList(String sifId, String zoneId) {

        synchronized (lock) {
            List<Entity> entityList = getSliEntityList(sifId, zoneId);

            List<String> idList = new ArrayList<String>();
            for (Entity entity : entityList) {
                idList.add(entity.getId());
            }

            return idList;
        }

    }

    @Override
    public String getSliGuidByType(String sifId, String sliType, String zoneId) {

        synchronized (lock) {
            Entity entity = getSliEntityFromOtherSifId(sifId, sliType, zoneId);

            if (entity == null) {
                return null;
            }
            return entity.getId();
        }

    }

    @Override
    public List<String> getSliGuidListByType(String sifId, String sliType, String zoneId) {

        synchronized (lock) {
            List<Entity> entityList = getSliEntityListByType(sifId, sliType, zoneId);

            List<String> idList = new ArrayList<String>();
            for (Entity entity : entityList) {
                idList.add(entity.getId());
            }

            return idList;
        }

    }

    @Override
    public Entity getSliEntityByType(String sifId, String sliType, String zoneId) {

        synchronized (lock) {
            return getSliEntity(sifId + "-" + sliType, zoneId);
        }
    }

    @Override
    public List<Entity> getSliEntityListByType(String sifId, String sliType, String zoneId) {

        synchronized (lock) {
            return getSliEntityList(sifId + "-" + sliType, zoneId);
        }

    }

    @Override
    public Entity getSliEntity(String sifId, String zoneId) {

        synchronized (lock) {
            List<Entity> entities = getSliEntityList(sifId, zoneId);
            if (entities == null || entities.size() == 0) {
                LOG.warn("No entity found for sifId: " + sifId);
                return null;
            }
            return entities.get(0);
        }

    }

    @Override
    public List<Entity> getSliEntityList(String sifId, String zoneId) {

        synchronized (lock) {
            String seaId = getZoneSea(zoneId);

            Map<String, List<SliEntityLocator>> idMap = customDataProvider.getIdMap(seaId);
            List<SliEntityLocator> locators = idMap.get(sifId);
            if (locators == null || locators.size() == 0) {
                LOG.error("No sif-sli mapping found for sifId: " + sifId);
                return new ArrayList<Entity>();
            }

            List<Entity> entities = new ArrayList<Entity>();
            for (SliEntityLocator locator : locators) {
                Entity entity = fetchSliEntity(locator);
                entities.add(entity);
            }

            return entities;
        }

    }

    @Override
    public String getZoneSea(String zoneId) {

        synchronized (lock) {
            Map<String, SliEntityLocator> seaMap = zoneMapProvider.getZoneToSliIdMap();
            SliEntityLocator locator = seaMap.get(zoneId);

            Entity seaEntity = fetchSliEntity(locator);
            if (seaEntity == null) {
                return null;
            }
            return seaEntity.getId();
        }
    }

    @Override
    public void putSliGuid(String sifId, String sliType, String sliId, String zoneId) {

        synchronized (lock) {

            String seaGuid = getZoneSea(zoneId);

            // check if it is in the map
            Map<String, List<SliEntityLocator>> idMap = customDataProvider.getIdMap(seaGuid);
            SliEntityLocator id = new SliEntityLocator(sliType, sliId, ParameterConstants.ID);

            List<SliEntityLocator> existingIdList = idMap.get(sifId);
            if (existingIdList == null) {
                existingIdList = new ArrayList<SliEntityLocator>();
            } else {
                // if already exists, return early
                for (SliEntityLocator locator : existingIdList) {
                    if (locator.getField().equals(ParameterConstants.ID) && locator.getType().equals(sliType)
                            && locator.getValue().equals(sliId)) {
                        return;
                    }
                }
            }
            existingIdList.add(id);

            idMap.put(sifId, existingIdList);

            customDataProvider.storeIdMap(seaGuid, idMap);
        }
    }

    @Override
    public Entity getSliEntityFromOtherSifId(String sifId, String sliType, String zoneId) {
        synchronized (lock) {
            return getSliEntity(sifId + "-" + sliType, zoneId);
        }
    }

    @Override
    public void putSliGuidForOtherSifId(String sifId, String sliType, String sliId, String zoneId) {

        synchronized (lock) {
            String key = sifId + "-" + sliType;
            putSliGuid(key, sliType, sliId, zoneId);
        }
    }

    private Entity fetchSliEntity(SliEntityLocator locator) {
        Query query = BasicQuery.Builder.create().filterEqual(locator.getField(), locator.getValue()).maxResults(1)
                .build();
        List<Entity> list;
        try {
            list = slcInterface.read(locator.getType(), query);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SLIClientException e) {
            throw new RuntimeException(e);
        }
        if (list == null || list.size() == 0) {
            LOG.error("No SLI Entity found for the following query: " + locator);
            return null;
        }
        Entity entity = list.get(0);
        return entity;
    }

}
