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

import org.slc.sli.sif.agent.SIFAgent;

import openadk.library.ADK;

import openadk.library.ADKException;
import openadk.library.DataObjectOutputStream;
import openadk.library.Event;
import openadk.library.MessageInfo;
import openadk.library.Publisher;
import openadk.library.Query;
import openadk.library.Zone;

import org.springframework.context.support.FileSystemXmlApplicationContext;

public class EventReporter implements Publisher {

    public static void main(String[] args) {
        try {
            FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(
                    "classpath:spring/applicationContext.xml");
            context.registerShutdownHook();

            SIFAgent agent = context.getBean(SIFAgent.class);

            ADK.initialize();
            if (args.length >= 3) {
                String zoneId = args[EventReporter.ZONE_ID];
                String zoneUrl = args[EventReporter.ZONE_URL];
                String messageFile = args[EventReporter.MESSAGE_FILE];
                Zone zone = agent.addZone(zoneId, zoneUrl);
                EventReporter reporter = new EventReporter(zone);
                reporter.reportEvent(messageFile);
            } else {
                Zone zone = agent.getZoneFactory().getZone("Zone1");
                EventReporter reporter = new EventReporter(zone);
                reporter.reportEvent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final int ZONE_ID = 0;
    public static final int ZONE_URL = 1;
    public static final int MESSAGE_FILE = 2;

    // TODO autowire these in?
    private Zone zone;
    private EventGenerator generator;

    public EventReporter(Zone zone) throws Exception {
        this(zone, null);
    }

    public EventReporter(Zone zone, String messageFile) throws Exception {
        this.zone = zone;
        this.zone.setPublisher(this);
        generator = new HCStudentPersonalGenerator();
    }

    public void setEventGenerator(EventGenerator generator) {
        this.generator = generator;
    }

    public void reportEvent() throws ADKException {
        Event event = generator.generateEvent(null);
        System.out.println(event.getObjectType().toString());
        zone.reportEvent(event);
    }

    public void reportEvent(String messageFile) throws ADKException {
        Properties props = new Properties();
        props.setProperty(CustomEventGenerator.MESSAGE_FILE, messageFile);
        Event event = generator.generateEvent(props);
        zone.reportEvent(event);
    }

    @Override
    public void onRequest(DataObjectOutputStream out, Query query, Zone zone,
            MessageInfo info) throws ADKException {
        System.out.println("Received request to publish data:\n"
                + "\tZone: " + zone.getZoneId() + "\n"
                + "\tQuery:\n" + query.toXML() + "\n"
                + "\tInfo: " + info.getMessage());
    }


}
