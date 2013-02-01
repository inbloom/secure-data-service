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

package org.slc.sli.sif.agent;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.ADKFlags;
import openadk.library.ElementDef;
import openadk.library.SubscriptionOptions;
import openadk.library.Zone;
import openadk.library.common.CommonDTD;
import openadk.library.datamodel.DatamodelDTD;
import openadk.library.hrfin.HrfinDTD;
import openadk.library.student.StudentDTD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.slc.sli.sif.subscriber.SifSubscriber;

/**
 * Manages a SIFAgent and its SifSubscriber
 *
 */
public class AgentManager {

    private SifAgent agent;

    @Autowired
    private SifSubscriber subscriber;

    @Value("${log.path}")
    private String logPath;

    @Value("${sli.sif-agent.adk.logFile}")
    private String adkLogFile;

    private String subscriberZoneName;

    private List<String> subscribeTypeList;

    private List<ElementDef> subscribeList;

    private static final Logger LOG = LoggerFactory.getLogger(SifAgent.class);

    /**
     * Schedules the delayed setup of the agent.  This is delayed so the the zone connect
     * will only happen after this(and every other) webapp has been completely initialized.
     * @throws Exception
     */
    @PostConstruct
    public void postConstruct() throws Exception {

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable run = new Runnable() {

            @Override
            public void run() {
                try {
                    setup();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        scheduler.schedule(run, 5, TimeUnit.SECONDS);

    }

    /**
     * Initializes the ADK, the agent, and the zone subscription
     * @throws Exception
     */
    public void setup() throws Exception {
        // set the adk.log.file property, which is used in ADK.initialize()
        System.setProperty("adk.log.file", logPath + File.separator + adkLogFile);
        ADK.initialize();
        ADK.debug = ADK.DBG_ALL;
        agent.startAgent();
        subscribeToZone();
    }

    /**
     * Unregisters the agent from the zone
     * @throws ADKException
     */
    @PreDestroy
    public void cleanup() throws ADKException {
        agent.shutdown(ADKFlags.PROV_NONE);
    }

    /**
     * Creates a subscriber and adds it to the configured zone
     */
    private void subscribeToZone() throws ADKException {
        Map<String, Map<String, ElementDef>> dtdMap = new HashMap<String, Map<String, ElementDef>>();

        Map<String, ElementDef> studentDtdMap = new HashMap<String, ElementDef>();
        new StudentDTD().addElementMappings(studentDtdMap);
        dtdMap.put("StudentDTD", studentDtdMap);

        Map<String, ElementDef> datamodelDtdMap = new HashMap<String, ElementDef>();
        new DatamodelDTD().addElementMappings(datamodelDtdMap);
        dtdMap.put("DatamodelDTD", datamodelDtdMap);

        Map<String, ElementDef> commonDtdMap = new HashMap<String, ElementDef>();
        new CommonDTD().addElementMappings(commonDtdMap);
        dtdMap.put("CommonDTD", commonDtdMap);

        Map<String, ElementDef> hrfinDtdMap = new HashMap<String, ElementDef>();
        new HrfinDTD().addElementMappings(hrfinDtdMap);
        dtdMap.put("HrfinDTD", hrfinDtdMap);

        Zone zone = agent.getZoneFactory().getZone(subscriberZoneName);

        for (String dataTypeString : subscribeTypeList) {
            String dtdType = dataTypeString.split("\\.", 2)[0];
            String subType = dataTypeString.split("\\.", 2)[1];
            ElementDef dataTypeDef = dtdMap.get(dtdType).get(subType);
            zone.setSubscriber(subscriber, dataTypeDef, new SubscriptionOptions());
            LOG.info("Subscribed zone " + subscriberZoneName +  " to SIF ADK datatype " + dataTypeString);
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

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public String getAdkLogFile() {
        return adkLogFile;
    }

    public void setAdkLogFile(String adkLogFile) {
        this.adkLogFile = adkLogFile;
    }

    public List<ElementDef> getSubscribeList() {
        return subscribeList;
    }

    public void setSubscribeList(List<ElementDef> subscribeList) {
        this.subscribeList = subscribeList;
    }

}
