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

import openadk.library.ADKException;
import openadk.library.Event;
import openadk.library.EventAction;
import openadk.library.MessageInfo;
import openadk.library.SIFDataObject;
import openadk.library.Subscriber;
import openadk.library.Zone;
import openadk.library.student.SchoolInfo;
import openadk.library.student.LEAInfo;

import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.sif.domain.Sif2SliTransformer;
import org.slc.sli.sif.domain.slientity.LEAEntity;
import org.slc.sli.sif.domain.slientity.SchoolEntity;
import org.slc.sli.sif.slcinterface.SlcInterface;

@Component
public class SifSubscriber implements Subscriber {

    private static final Logger LOG = LoggerFactory.getLogger(SifSubscriber.class);

    @Autowired
    private Sif2SliTransformer xformer;

    @Autowired
    private SlcInterface slcInterface;

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
            switch(event.getAction()) {
            case ADD:
                addEntity(sdo);
                break;
            case CHANGE:
                break;
            case DELETE:
                break;
            case UNDEFINED:
            default:
                LOG.error("Wrong SIF Action.");
                break;
            }
        }
    }
    
    private void addEntity(SIFDataObject sdo) {
        
        if (sdo instanceof SchoolInfo) {
            SchoolEntity entity = xformer.transform((SchoolInfo)sdo);
            LOG.info("addEntity: "+entity);
            LOG.info(""+entity.getData());
            String result = slcInterface.create(entity);
            LOG.info(result);
        }
        
        if (sdo instanceof LEAInfo) {
            LEAEntity entity = xformer.transform((LEAInfo)sdo);
            LOG.info("addEntity: "+entity);
            LOG.info(""+entity.getData());
        }
        
    }

}
