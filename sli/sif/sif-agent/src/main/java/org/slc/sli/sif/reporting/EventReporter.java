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

import java.util.Properties;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.DataObjectOutputStream;
import openadk.library.Event;
import openadk.library.MessageInfo;
import openadk.library.Publisher;
import openadk.library.PublishingOptions;
import openadk.library.Query;
import openadk.library.SIFDataObject;
import openadk.library.Zone;
import openadk.library.student.StudentDTD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.sif.agent.SifAgent;
import org.slc.sli.sif.zone.PublishZoneConfigurator;

/**
 * Test agent to trigger event reports
 *
 */
public class EventReporter implements Publisher {

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

    public static void main(String[] args) throws ADKException {
        Logger logger = LoggerFactory.getLogger(EventReporter.class);

        ADK.initialize();
        ADK.debug = ADK.DBG_ALL;

        try {
            SifAgent agent = new SifAgent("PublisherAgent",
                    "src/main/resources/sif/agent-publish-config.xml",
                    new PublishZoneConfigurator());

            agent.startAgent();

            if (args.length >= 2) {
                String zoneId = args[EventReporter.ZONE_ID];
                String messageFile = args[EventReporter.MESSAGE_FILE];
                Zone zone = agent.getZoneFactory().getZone(zoneId);
                EventReporter reporter = new EventReporter(zone);

                reporter.setEventGenerator(new CustomEventGenerator());
                reporter.reportEvent(messageFile);
            } else {
                Zone zone = agent.getZoneFactory().getZone("TestZone");
                EventReporter reporter = new EventReporter(zone);
                reporter.reportEvent();
            }
        } catch (Exception e) {
            logger.error("Exception trying to report event", e);
        }
    }

    public static final int ZONE_ID = 0;
    public static final int MESSAGE_FILE = 1;

    private static final Logger LOG = LoggerFactory.getLogger(EventReporter.class);

    private Zone zone;
    private EventGenerator generator;

    public EventReporter(Zone zone) throws Exception {
        this.zone = zone;
        //this.zone.setPublisher(this);
        this.zone.setPublisher(this, StudentDTD.SCHOOLINFO, new PublishingOptions(true));
        this.zone.setPublisher(this, StudentDTD.LEAINFO, new PublishingOptions(true));
        this.zone.setPublisher(this, StudentDTD.STUDENTPERSONAL, new PublishingOptions(true));
        generator = new HCStudentPersonalGenerator();
    }

    public void setEventGenerator(EventGenerator generator) {
        this.generator = generator;
    }

    public void reportEvent() throws ADKException {
        Event event = generator.generateEvent(null);
        if (zone.isConnected()) {
            zone.reportEvent(event);
        } else {
            LOG.error("Zone is not connected");
        }
    }

    public void reportEvent(String messageFile) throws ADKException {
        Properties props = new Properties();
        props.setProperty(CustomEventGenerator.MESSAGE_FILE, messageFile);
        Event event = generator.generateEvent(props);
        if (zone.isConnected()) {
            zone.reportEvent(event);
        } else {
            LOG.error("Zone is not connected");
        }
    }

    @Override
    public void onRequest(DataObjectOutputStream out, Query query, Zone zone,
            MessageInfo info) throws ADKException {
        LOG.info("Received request to publish data:\n"
                + "\tQuery:\n" + query.toXML() + "\n"
                + "\tZone: " + zone.getZoneId() + "\n"
                + "\tInfo: " + info.getMessage());
    }

    @SuppressWarnings("unused")
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
}
