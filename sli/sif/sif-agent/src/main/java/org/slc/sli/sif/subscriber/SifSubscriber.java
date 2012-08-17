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

import openadk.library.ADKException;
import openadk.library.Event;
import openadk.library.EventAction;
import openadk.library.MessageInfo;
import openadk.library.SIFDataObject;
import openadk.library.Subscriber;
import openadk.library.Zone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.impl.GenericEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;
import org.slc.sli.sif.slcinterface.SlcInterface;
import org.slc.sli.sif.translation.SifTranslationManager;

/**
 * Sif Subscriber implementation
 */
@Component
public class SifSubscriber implements Subscriber {

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
                slcInterface.update(translatedEntities.get(0));
            }
        }

    }

}
