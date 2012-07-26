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

import java.util.HashMap;
import java.util.Map;

import openadk.library.ADKException;
import openadk.library.Event;
import openadk.library.MessageInfo;
import openadk.library.SIFDataObject;
import openadk.library.Subscriber;
import openadk.library.Zone;
import openadk.library.student.LEAInfo;
import openadk.library.student.SchoolInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.impl.GenericEntity;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.sif.domain.Sif2SliTransformer;
import org.slc.sli.sif.slcinterface.SifIdResolver;
import org.slc.sli.sif.slcinterface.SlcInterface;

@Component
public class SifSubscriber implements Subscriber {

    private static final Logger LOG = LoggerFactory.getLogger(SifSubscriber.class);
    
    private static final String PARENT_EDORG_FIELD = "parentEducationAgencyReference";

    @Autowired
    private Sif2SliTransformer xformer;

    @Autowired
    private SlcInterface slcInterface;
    
    @Autowired
    SifIdResolver sifIdResolver;

    private SIFDataObject inspectAndDestroyEvent(Event e) {
        SIFDataObject sdo = null;
        LOG.info("###########################################################################");
        try {
            sdo = e.getData().readDataObject();
            LOG.info("\n" + "\tObjectType: " + sdo.getObjectType());
            LOG.info(""+sdo.toString());
        } catch (ADKException e1) {
            LOG.error("Error trying to inspect event", e1);
        }
        LOG.info("###########################################################################");
        return sdo;
    }

    @Override
    public void onEvent(Event event, Zone zone, MessageInfo info) throws ADKException {
        LOG.info("Received event:\n" + "\tEvent:      " + event.getActionString() + "\n" + "\tZone:       " + zone.getZoneId()
                + "\n" + "\tInfo:       " + info.getMessage());

        SIFDataObject sdo = inspectAndDestroyEvent(event);

        // execute a call to the SDK
        boolean tokenChecked = false;
        String token = slcInterface.sessionCheck();
        if (null != token && 0 < token.length()) {
            tokenChecked = true;
            LOG.info("Successfully executed session check with token " + token);
        } else {
            LOG.info("Session check failed");
        }
        
        if (sdo!=null && tokenChecked && event.getAction()!=null) {

            // Testing id map
            String sliGuid = sifIdResolver.getSLIGuid(sdo.getRefId());
            String seaGuid = sifIdResolver.getZoneSEA(zone.getZoneId());
            LOG.info("received action: " + event.getAction().name() + " on " + 
                sdo.getRefId() + "(sliID: " + (sliGuid == null ? " none " : sliGuid) + "," +
                " seaId: " + (seaGuid == null ? " none " : seaGuid) + ")"
            );

            switch(event.getAction()) {
            case ADD:
                addEntity(sdo);
                break;
            case CHANGE:
                changeEntity(sdo);
                break;
            case UNDEFINED:
            default:
                LOG.error("Wrong SIF Action.");
                break;
            }
        }
     }
    
    private void addEntity(SIFDataObject sdo) { 
        String guid = null;
        Map<String, Object> body = null;
        String entityType = null;
        if (sdo instanceof SchoolInfo) {
            body = xformer.transform((SchoolInfo)sdo);
            entityType = ResourceNames.SCHOOLS;
        } else if (sdo instanceof LEAInfo) {
            body = xformer.transform((LEAInfo)sdo);
            entityType = ResourceNames.EDUCATION_ORGANIZATIONS;
        } else {
            LOG.info("Unsupported SIF Entity");
        }
        
        if (body!=null) {
            GenericEntity entity = new GenericEntity(entityType, body);
            guid = slcInterface.create(entity);
        }
        
    }
    
    //used to keep mapping between Entity Type and its sif RefId-to-guid map
    //may need refactor for persistency consideration
    private Map<String, Map<String, String>> sifRefId2guidMap = new HashMap<String, Map<String, String>>();

    private void changeEntity(SIFDataObject sdo) {
        Entity entity = sifIdResolver.getSLIEntity(sdo.getRefId());
        if (entity == null) {
            LOG.info(" Unable to map SIF object to SLI: " + sdo.getRefId());
            return;
        }
        Map<String, Object> updateBody = null;
        String entityType = null;
        if (sdo instanceof SchoolInfo) {
            updateBody = xformer.transform((SchoolInfo) sdo);
        }
        if (sdo instanceof LEAInfo) {
            updateBody = xformer.transform((LEAInfo)sdo);
        }
        updateMap(entity.getData(), updateBody);
        slcInterface.update(entity);
    }




    ///-======================== HELPER UTILs ======
    /**
     * Takes an entity and fills in the missing values from it recursively
     * @param m
     */
    // applies map2 to map1 recursively
    private static void updateMap (Map map, Map u) {
        for (Object k : u.keySet()) {
            if (!map.containsKey(k)) {
                map.put(k, u.get(k));
            } else {
                Object o1 = map.get(k);
                Object o2 = u.get(k);
                // recursive update collections
                if (o1 instanceof Map && o2 instanceof Map) {
                	updateMap((Map) o1, (Map) o2);
                } else {
                    map.put(k,  o2);
                }
            }
        }
    }
}
