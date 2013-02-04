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

package org.slc.sli.sif.subscriber;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import openadk.library.ADKException;
import openadk.library.Event;
import openadk.library.MessageInfo;
import openadk.library.SIFDataObject;
import openadk.library.Subscriber;
import openadk.library.Zone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.impl.GenericEntity;
import org.slc.sli.sif.domain.slientity.SliEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;
import org.slc.sli.sif.slcinterface.SlcInterface;
import org.slc.sli.sif.translation.SifTranslationManager;

/**
 * Sif Subscriber implementation
 */
@Component
public class SifSubscriber implements Subscriber {

    private static final Logger LOG = LoggerFactory.getLogger(SifSubscriber.class);

    @Autowired
    private SifTranslationManager translationManager;

    @Autowired
    private SlcInterface slcInterface;

    @Autowired
    SifIdResolver sifIdResolver;

    private SIFDataObject inspectAndDestroyEvent(Event e) {
        SIFDataObject sdo = null;
        try {
            sdo = e.getData().readDataObject();
            LOG.info(sdo.toString());
        } catch (ADKException e1) {
            LOG.error("Error trying to inspect event", e1);
        }
        return sdo;
    }

    @Override
    public void onEvent(Event event, Zone zone, MessageInfo info) throws ADKException {
        LOG.info("Received event:\n" + "\tEvent:      " + event.getActionString() + "\n" + "\tZone:       "
                + zone.getZoneId() + "\n" + "\tInfo:       " + info);

        SIFDataObject sifData = inspectAndDestroyEvent(event);

        // execute a call to the SDK
        boolean tokenChecked = false;
        String token = slcInterface.sessionCheck();
        if (token != null && token.length() > 0) {
            tokenChecked = true;
            LOG.info("Successfully executed session check with token " + token);
        } else {
            LOG.info("Session check failed");
        }

        if (sifData != null && tokenChecked && event.getAction() != null) {
            List<SliEntity> entities = translationManager.translate(sifData, zone.getZoneId());
            Entity matchedEntity;

            switch (event.getAction()) {
            case ADD:
                for (SliEntity sliEntity : entities) {
                    // Handle cases when two or more SIF entities map into one.
                    matchedEntity = findCombiningEntity(sliEntity, zone.getZoneId());

                    if (matchedEntity != null) {
                        changeEntity(sifData, sliEntity, zone.getZoneId(), matchedEntity);
                        sifIdResolver.putSliGuid(sifData.getRefId(), matchedEntity.getEntityType(),
                                matchedEntity.getId(), zone.getZoneId());
                    } else {
                        addEntity(sifData, zone.getZoneId(), sliEntity);
                    }
                }
                break;
            case CHANGE:
                // TODO, we can potentially get multiple matched entities
                List<Entity> matchedEntities = sifIdResolver.getSliEntityList(sifData.getRefId(), zone.getZoneId());
                if (entities == null || entities.isEmpty()) {
                    LOG.warn("Null or empty translated SIF entities: " + entities);
                } else if (matchedEntities == null || matchedEntities.isEmpty()) {
                    LOG.warn("Null or empty SIF entities (no entity found to update): " + matchedEntities);
                } else {
                    for (SliEntity sliEntity : entities) {
                        for (Entity e : matchedEntities) {
                            if (sliEntity.entityType().equals(e.getEntityType())) {
                                changeEntity(sifData, sliEntity, zone.getZoneId(), e);
                            }
                        }
                    }
                }
                break;
            case UNDEFINED:
            default:
                LOG.error("Unsupported SIF Action: " + event.getAction());
                break;
            }
        }
    }

    private void addEntity(SIFDataObject sifData, String zoneId, SliEntity sliEntity) {
        GenericEntity entity = sliEntity.createGenericEntity();
        String guid = slcInterface.create(entity);
        LOG.info("addEntity " + entity.getEntityType() + ": RefId=" + sifData.getRefId() + " guid=" + guid);
        if (guid != null) {
            sifIdResolver.putSliGuid(sifData.getRefId(), sliEntity.entityType(), guid, zoneId);
            if (sliEntity.hasOtherSifRefId()) {
                sifIdResolver.putSliGuidForOtherSifId(sliEntity.getOtherSifRefId(), sliEntity.entityType(), guid,
                        zoneId);
            }
        }
    }

    private void changeEntity(SIFDataObject sifData, SliEntity sliEntity, String zoneId, Entity matchedEntity) {
        if (matchedEntity == null) {
            LOG.info(" Unable to map SIF object to SLI: " + sifData.getRefId());
            return;
        }
        updateMap(matchedEntity.getData(), sliEntity.createBody());
        slcInterface.update(matchedEntity);

        LOG.info("changeEntity " + sliEntity.entityType() + ": RefId=" + sifData.getRefId());
    }

    /**
     * Attempt to match an sli entity with another partial entity that was created by a related sif object.
     */
    private Entity findCombiningEntity(SliEntity sliEntity, String zoneId) {
        Entity matched = null;
        // Two combining cases to handle:
        // 1) match by a reference
        if (sliEntity.isCreatedByOthers()) {
          //TODO this should probably include type but should be fine for PI5
            matched = sifIdResolver.getSliEntity(sliEntity.getCreatorRefId(), zoneId);
            if (matched != null) {
                LOG.info("Combining through common SIF creator with a " + matched.getEntityType()
                        + " entity with id " + matched.getId());
            }
        }
        // 2) match by a common other entity
        if (sliEntity.hasOtherSifRefId()) {

            matched = sifIdResolver.getSliEntityFromOtherSifId(sliEntity.getOtherSifRefId(),
                    sliEntity.entityType(), zoneId);
            if (matched != null) {
                LOG.info("Combining through common SIF reference with a " + matched.getEntityType()
                        + " entity with id " + matched.getId());
            }
        }

        return matched;
    }

    // /-======================== HELPER UTILs ======
    /**
    * Applies the values from map u to the keys in map m, recursively
    *
    * @param map
    *            : the map to be updated
    * @param u
    *            : the map containing the updates
    */
    private static void updateMap(Map<String, Object> map, Map<String, Object> u) {
        for (Entry<String, Object> uEntry : u.entrySet()) {
            if (!map.containsKey(uEntry.getKey())) {
                map.put(uEntry.getKey(), uEntry.getValue());
            } else {
                Object o1 = map.get(uEntry.getKey());
                Object o2 = uEntry.getValue();
                // recursive update collections
                if (o1 instanceof Map && o2 instanceof Map) {
                    updateMap((Map<String, Object>) o1, (Map<String, Object>) o2);
                } else {
                    map.put(uEntry.getKey(), o2);
                }
            }
        }
    }
}
