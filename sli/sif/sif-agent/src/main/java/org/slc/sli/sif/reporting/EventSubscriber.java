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
import openadk.library.Event;
import openadk.library.MessageInfo;
import openadk.library.SIFDataObject;
import openadk.library.Subscriber;
import openadk.library.SubscriptionOptions;
import openadk.library.Zone;
import openadk.library.student.StudentDTD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import org.slc.sli.sif.agent.SifAgent;

public class EventSubscriber implements Subscriber {

    static {
        // Simple workaround to get logging paths set up correctly when run from the command line
        String catalinaHome = System.getProperty("catalina.home");
        if( catalinaHome == null ){
            System.setProperty("catalina.home", "target");
        }
        String sliConf = System.getProperty("sli.conf");
        if( sliConf == null ){
            System.setProperty("sli.conf", "../../config/properties/sli.properties");
        }
    }

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(EventSubscriber.class);

        try {
            FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(
                    "classpath:spring/applicationContext.xml");
            context.registerShutdownHook();

            //SifAgent agent = context.getBean(SifAgent.class);

            SifAgent agent = new SifAgent("SubscriberAgent");
            ADK.initialize();
            ADK.debug = ADK.DBG_ALL;

            agent.startAgentWithConfig(new File("src/main/resources/sif/subscriber-agent-config.xml"));

            if (args.length >= 1) {
                String zoneId = args[EventSubscriber.ZONE_ID];
                Zone zone = agent.getZoneFactory().getZone(zoneId);
                new EventSubscriber(zone);
            } else {
                Zone zone = agent.getZoneFactory().getZone("TestZone");
                new EventSubscriber(zone);
            }
        } catch (Exception e) {
            logger.error("Exception trying to subscriber to event", e);
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
    public void onEvent(Event event, Zone zone, MessageInfo info)
            throws ADKException {
        LOG.info("Received event:\n"
                + "\tEvent: " + event.getActionString() + "\n"
                + "\tZone: " + zone.getZoneId() + "\n"
                + "\tInfo: " + info.getMessage());
        inspectAndDestroyEvent(event);
    }
}
