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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import openadk.library.ADKException;
import openadk.library.Event;
import openadk.library.EventAction;
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
    SifTranslationManager translationManager;

    @Autowired
    SlcInterface slcInterface;

    @Autowired
    SifIdResolver sifIdResolver;

    @Override
    public void onEvent(Event event, Zone zone, MessageInfo info) throws ADKException {

        String zoneId = zone.getZoneId();
        SIFDataObject sifData = event.getData().readDataObject();

        List<GenericEntity> translatedEntities = translationManager.translate(sifData, zoneId);

        if (event.getAction() == EventAction.ADD) {
            for (GenericEntity entity : translatedEntities) {
                String apiGuid = slcInterface.create(entity);
                if (apiGuid != null) {
                    sifIdResolver.putSliGuid(sifData.getRefId(), entity.getEntityType(), apiGuid, zoneId);
                }
            }
        } else {
            if (translatedEntities.size() > 0) {
                Entity matchedEntity = sifIdResolver.getSliEntity(sifData.getRefId(), zoneId);
                updateMap(matchedEntity.getData(), translatedEntities.get(0).getData());
                slcInterface.update(matchedEntity);
            } else {
                LOG.info(" Unable to map SIF object to SLI: " + sifData.getRefId());
            }
        }

    }

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
