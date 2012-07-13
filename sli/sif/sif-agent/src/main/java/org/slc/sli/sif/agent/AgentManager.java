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

package org.slc.sli.sif.agent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.ADKFlags;
import openadk.library.ElementDef;
import openadk.library.SubscriptionOptions;
import openadk.library.Zone;
import openadk.library.student.StudentDTD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.sif.subscriber.SifSubscriber;

/**
 * Manages a SIFAgent and its SifSubscriber
 *
 */
@Component
public class AgentManager {

    private SifAgent agent;

    @Autowired
    private SifSubscriber subscriber;

    private String subscriberZoneName;

    private List<String> subscribeTypeList;

    private static final Logger LOG = LoggerFactory.getLogger(SifAgent.class);

    @PostConstruct
    public void setup() throws Exception {

        ADK.initialize();
        ADK.debug = ADK.DBG_ALL;

        agent.startAgent();

        subscribeToZone();
    }

    @PreDestroy
    public void cleanup() throws ADKException {
        agent.shutdown(ADKFlags.PROV_NONE);
    }

    /**
     * Create a subscriber and add it to the configured zone
     */
    private void subscribeToZone() throws ADKException {
        Map<String, ElementDef> dtdMap = new HashMap<String, ElementDef>();
        StudentDTD studentDTD = new StudentDTD();
        studentDTD.addElementMappings(dtdMap);

        Zone zone = agent.getZoneFactory().getZone(subscriberZoneName);

        for (String dataTypeString : subscribeTypeList) {
            ElementDef dataTypeDef = dtdMap.get(dataTypeString);
            if (dataTypeDef != null) {
                zone.setSubscriber(subscriber, dataTypeDef, new SubscriptionOptions());
                LOG.info("Subscribed zone " + subscriberZoneName +  " to SIF ADK datatype " + dataTypeString);
            } else {
                LOG.error("Unable to fine SIF ADK datatype " + dataTypeString);
            }
        }
    }

    public void setSubscribeTypeList(List<String> subscribeTypeList) {
        this.subscribeTypeList = subscribeTypeList;
    }

    public List<String> getSubscribeTypeList() {
        return this.subscribeTypeList;
    }

    public void setSubscriberZoneName(String subscriberZoneName) {
        this.subscriberZoneName = subscriberZoneName;
    }

    public String getSubscriberZoneName() {
        return this.subscriberZoneName;
    }

    public void setAgent(SifAgent agent) {
        this.agent = agent;
    }

    public SifAgent getAgent() {
        return this.agent;
    }
}
