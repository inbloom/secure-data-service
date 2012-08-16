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

package org.slc.sli.sif.subscriber;

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

    private static final String PARENT_EDORG_FIELD = "parentEducationAgencyReference";

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
                + zone.getZoneId() + "\n" + "\tInfo:       " + info.getMessage());

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
            switch (event.getAction()) {
                case ADD:
                    addEntities(sifData, zone.getZoneId());
                    break;
                case CHANGE:
                    changeEntities(sifData, zone.getZoneId());
                    break;
                case UNDEFINED:
                default:
                    LOG.error("Unsupported SIF Action: " + event.getAction());
                    break;
            }
        }
    }

    private void addEntities(SIFDataObject sifData, String zoneId) {
        for (SliEntity sliEntity : translationManager.translate(sifData, zoneId)) {
            GenericEntity entity = sliEntity.createGenericEntity();
            String guid = slcInterface.create(entity);
            LOG.info("addEntities " + entity.getEntityType() + ": RefId=" + sifData.getRefId() + " guid=" + guid);
            if (guid != null) {
                sifIdResolver.putSliGuid(sifData.getRefId(), sliEntity.entityType(), guid, zoneId);
            }
        }
    }

    private void changeEntities(SIFDataObject sifData, String zoneId) {
        // TODO, we can potentially get multiple matched entities
        Entity matchedEntity = sifIdResolver.getSliEntity(sifData.getRefId(), zoneId);

        if (matchedEntity == null) {
            LOG.info(" Unable to map SIF object to SLI: " + sifData.getRefId());
            return;
        }
        for (SliEntity sliEntity : translationManager.translate(sifData, zoneId)) {
            updateMap(matchedEntity.getData(), sliEntity.createBody());
            slcInterface.update(matchedEntity);
            LOG.info("changeEntities " + sliEntity.entityType() + ": RefId=" + sifData.getRefId());
        }
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
