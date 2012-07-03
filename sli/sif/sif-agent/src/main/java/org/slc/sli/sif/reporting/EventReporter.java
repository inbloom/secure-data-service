package org.slc.sli.sif.reporting;

import org.slc.sli.sif.agent.SIFAgent;

import openadk.library.ADKException;
import openadk.library.Event;
import openadk.library.Zone;

public class EventReporter {

    public static void main(String[] args) {
        try {
            EventReporter reporter = new EventReporter();
            reporter.reportEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Zone zone;
    private SIFAgent agent;

    public EventReporter() throws Exception {
        agent = new SIFAgent("TestEventGenerator");
        agent.initialize();
        zone = agent.getZoneFactory().getZone("DistrictZone");
    }

    public EventReporter(String zoneId, String zoneUrl) throws Exception {
        agent = new SIFAgent("TestEventGenerator");
        agent.initialize();
        zone = agent.addZone("TestZone", "http://localhost:1337/TestZone");
    }

    public void reportEvent() throws ADKException {
        EventGenerator generator = new HCStudentPersonalGenerator();
        Event event = generator.generateEvent();
        zone.reportEvent(event);
    }

}
