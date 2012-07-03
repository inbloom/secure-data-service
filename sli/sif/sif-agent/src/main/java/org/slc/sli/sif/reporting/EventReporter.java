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
