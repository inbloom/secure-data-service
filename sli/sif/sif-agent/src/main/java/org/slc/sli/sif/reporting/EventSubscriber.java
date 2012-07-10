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

package org.slc.sli.sif.reporting;

import java.io.File;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.DataObjectInputStream;
import openadk.library.Event;
import openadk.library.MessageInfo;
import openadk.library.Query;
import openadk.library.QueryResults;
import openadk.library.SIFDataObject;
import openadk.library.SIFVersion;
import openadk.library.Subscriber;
import openadk.library.SubscriptionOptions;
import openadk.library.Zone;
import openadk.library.infra.SIF_Error;
import openadk.library.student.StudentDTD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import org.slc.sli.sif.agent.SifAgent;
import org.slc.sli.sif.zone.SubscribeZoneConfigurator;

public class EventSubscriber implements Subscriber, QueryResults {

    static {
        // Simple workaround to get logging paths set up correctly when run from the command line
        String catalinaHome = System.getProperty("catalina.home");
        if (catalinaHome == null) {
            System.setProperty("catalina.home", "target");
        }
        String sliConf = System.getProperty("sli.conf");
        if (sliConf == null) {
            System.setProperty("sli.conf", "../../config/properties/sli.properties");
        }
    }

    public static void main(String[] args) throws ADKException {
        Logger logger = LoggerFactory.getLogger(EventSubscriber.class);

        ADK.initialize();
        ADK.debug = ADK.DBG_ALL;

        try {
            Resource configFile = new FileSystemResource(new File("src/main/resources/sif/agent-subscribe-config.xml"));
            SifAgent agent = new SifAgent("PublisherAgent", configFile, new SubscribeZoneConfigurator());

            agent.startAgent();

            if (args.length >= 1) {
                String zoneId = args[EventSubscriber.ZONE_ID];
                Zone zone = agent.getZoneFactory().getZone(zoneId);
                new EventSubscriber(zone);
            } else {
                Zone zone = agent.getZoneFactory().getZone("TestZone");
                EventSubscriber subscriber = new EventSubscriber(zone);
                subscriber.sendQuery();
            }
        } catch (Exception e) {
            logger.error("Exception trying to subscriber to event", e);
        }

        // Wait for Ctrl-C to be pressed
        Object semaphore = new Object();
        System.out.println("Agent is running (Press Ctrl-C to stop)");
        synchronized (semaphore) {
            try {
                semaphore.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static final int ZONE_ID = 0;

    private static final Logger LOG = LoggerFactory.getLogger(EventSubscriber.class);

    private Zone zone;

    public EventSubscriber(Zone zone) throws Exception {
        this.zone = zone;
        this.zone.setSubscriber(this, StudentDTD.SCHOOLINFO, new SubscriptionOptions());
        this.zone.setSubscriber(this, StudentDTD.STUDENTPERSONAL, new SubscriptionOptions());
    }

    public void sendQuery() throws ADKException {
        Query query = new Query(StudentDTD.STUDENTPERSONAL);
        query.setSIFVersions(SIFVersion.SIF23);
        zone.query(query);
    }

    private void inspectAndDestroyEvent(Event e) {
        LOG.info("###########################################################################");
        try {
            SIFDataObject dataObj = e.getData().readDataObject();
            LOG.info(dataObj.toString());
        } catch (ADKException e1) {
            LOG.error("Error trying to inspect event", e1);
        }
        LOG.info("###########################################################################");
    }

    @Override
    public void onEvent(Event event, Zone zone, MessageInfo info) throws ADKException {
        LOG.info("Received event:\n" + "\tEvent: " + event.getActionString() + "\n" + "\tZone: " + zone.getZoneId()
                + "\n" + "\tInfo: " + info.getMessage());
        inspectAndDestroyEvent(event);
    }

    @Override
    public void onQueryPending(MessageInfo info, Zone zone) throws ADKException {
        LOG.info("Query pending");
    }

    @Override
    public void onQueryResults(DataObjectInputStream data, SIF_Error error, Zone zone, MessageInfo info)
            throws ADKException {
        LOG.info("Received query results");
    }
}
