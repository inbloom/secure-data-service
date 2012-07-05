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

import openadk.library.ADKException;
import openadk.library.Event;
import openadk.library.Zone;

import org.springframework.context.support.FileSystemXmlApplicationContext;

import org.slc.sli.sif.agent.SIFAgent;

public class EventReporter {

    public static void main(String[] args) {
        try {

            FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(
                    "classpath:spring/applicationContext.xml");
            context.registerShutdownHook();

            SIFAgent agent = context.getBean(SIFAgent.class);

            EventReporter reporter = new EventReporter(agent);
            reporter.reportEvent();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Zone zone;
    private SIFAgent agent;

    public EventReporter(SIFAgent agent) throws Exception {
        zone = agent.getZoneFactory().getZone("Zone1");
    }


    public void reportEvent() throws ADKException {
        EventGenerator generator = new HCStudentPersonalGenerator();
        Event event = generator.generateEvent();
        zone.reportEvent(event);
    }


}
