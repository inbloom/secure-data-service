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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.DataObjectOutputStream;
import openadk.library.Event;
import openadk.library.EventAction;
import openadk.library.MessageInfo;
import openadk.library.Publisher;
import openadk.library.PublishingOptions;
import openadk.library.Query;
import openadk.library.SIFDataObject;
import openadk.library.SIFVersion;
import openadk.library.Zone;
import openadk.library.common.CommonDTD;
import openadk.library.common.Email;
import openadk.library.common.EmailList;
import openadk.library.common.EmailType;
import openadk.library.common.ExitTypeCode;
import openadk.library.common.GradeLevelCode;
import openadk.library.common.OtherId;
import openadk.library.common.OtherIdType;
import openadk.library.common.StudentLEARelationship;
import openadk.library.common.YesNoUnknown;
import openadk.library.hrfin.EmployeePersonal;
import openadk.library.hrfin.HrOtherIdList;
import openadk.library.hrfin.HrfinDTD;
import openadk.library.student.LEAInfo;
import openadk.library.student.SchoolInfo;
import openadk.library.student.StaffPersonal;
import openadk.library.student.StudentDTD;
import openadk.library.student.StudentPersonal;
import openadk.library.student.StudentSchoolEnrollment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.sif.generator.CustomEventGenerator;
import org.slc.sli.sif.generator.GeneratorScriptEvent;
import org.slc.sli.sif.generator.SifEntityGenerator;
import org.slc.sli.sif.zone.PublishZoneConfigurator;

/**
 * Test agent to trigger event reports
 *
 */
public class EventReporter implements Publisher {

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
        Logger logger = LoggerFactory.getLogger(EventReporter.class);

        ADK.initialize();
        ADK.debug = ADK.DBG_ALL;

        try {
            Properties props = PropertyUtils.getProperties(args);
            String agentId = props.getProperty(PropertyUtils.KEY_AGENT_ID);
            String zoneUrl = props.getProperty(PropertyUtils.KEY_ZONE_URL);
            String localZoneId = props.getProperty(PropertyUtils.KEY_ZONE_ID);
            String script = props.getProperty(PropertyUtils.KEY_SCRIPT);
            long waitTime = ((Long) props.get(PropertyUtils.KEY_WAIT_TIME)).longValue();

            String messageFile = props.getProperty(PropertyUtils.KEY_MESSAGE_FILE);
            String eventAction = props.getProperty(PropertyUtils.KEY_EVENT_ACTION);

            EventReporterAgent agent = createReporterAgent(agentId, zoneUrl);
            agent.startAgent();
            Zone zone = agent.getZoneFactory().getZone(localZoneId);

            EventReporter reporter = new EventReporter(zone);

            if (!messageFile.isEmpty()) {
                reporter.reportEvent(messageFile, eventAction);
            } else  {
                reporter.runReportScript(script, waitTime);
            }
        } catch (Exception e) { // Have to catch top-level Exception due to agent.startAgent()
            logger.error("Exception trying to report event", e);
        }

        System.exit(0);
    }

    private static EventReporterAgent createReporterAgent(String agentId, String zoneUrl) {
        Properties agentProperties = new Properties();
        agentProperties.put("adk.messaging.mode", "Push");
        agentProperties.put("adk.messaging.transport", "http");
        agentProperties.put("adk.messaging.pullFrequency", "30000");
        agentProperties.put("adk.messaging.maxBufferSize", "32000");

        Properties httpProperties = new Properties();
        httpProperties.put("port", "25102");

        Properties httpsProperties = new Properties();

        return new EventReporterAgent(agentId, new PublishZoneConfigurator(), agentProperties, httpProperties, httpsProperties,
                "TestZone", zoneUrl, SIFVersion.SIF23);
    }

    private static final Logger LOG = LoggerFactory.getLogger(EventReporter.class);

    private Zone zone;
    private Map<String, ScriptMethod> scriptMethodMap = new HashMap<String, ScriptMethod>();

    /**
     * Helper class to map script identifiers to Java methods
     *
     * @author vmcglaughlin
     *
     */
    static class ScriptMethod {
        private Object executingClass;
        private Object[] args;
        private Method method;

        public ScriptMethod(Object executingClass, Method method, Object... args) {
            this.executingClass = executingClass;
            this.method = method;
            this.args = args;
        }

        public Event execute() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
            return (Event) method.invoke(executingClass, args);
        }

        public String toString() {
            return "Class: " + executingClass + "\tmethod: " + method.getName() + "\targs: " + args;
        }
    }

    public EventReporter(Zone zone) throws ADKException, SecurityException, NoSuchMethodException {
        this.zone = zone;
        this.zone.setPublisher(this, StudentDTD.SCHOOLINFO, new PublishingOptions(true));
        this.zone.setPublisher(this, StudentDTD.LEAINFO, new PublishingOptions(true));
        this.zone.setPublisher(this, StudentDTD.STUDENTPERSONAL, new PublishingOptions(true));
        this.zone.setPublisher(this, StudentDTD.STUDENTSCHOOLENROLLMENT, new PublishingOptions(true));
        this.zone.setPublisher(this, CommonDTD.STUDENTLEARELATIONSHIP, new PublishingOptions(true));
        this.zone.setPublisher(this, StudentDTD.STAFFPERSONAL, new PublishingOptions(true));
        this.zone.setPublisher(this, HrfinDTD.EMPLOYEEPERSONAL, new PublishingOptions(true));

        populateMethodMap();
    }

    private void populateMethodMap() throws SecurityException, NoSuchMethodException {
        ScriptMethod leaInfoAddMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportLeaInfoEvent", EventAction.class), EventAction.ADD);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_LEA_INFO_ADD, leaInfoAddMethod);
        ScriptMethod leaInfoChangeMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportLeaInfoEvent", EventAction.class), EventAction.CHANGE);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_LEA_INFO_CHANGE, leaInfoChangeMethod);
        ScriptMethod leaInfoDeleteMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportLeaInfoEvent", EventAction.class), EventAction.DELETE);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_LEA_INFO_DELETE, leaInfoDeleteMethod);

        ScriptMethod schoolInfoAddMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportSchoolInfoEvent", EventAction.class), EventAction.ADD);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_SCHOOL_INFO_ADD, schoolInfoAddMethod);
        ScriptMethod schoolInfoChangeMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportSchoolInfoEvent", EventAction.class), EventAction.CHANGE);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_SCHOOL_INFO_CHANGE, schoolInfoChangeMethod);
        ScriptMethod schoolInfoDeleteMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportSchoolInfoEvent", EventAction.class), EventAction.DELETE);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_SCHOOL_INFO_DELETE, schoolInfoDeleteMethod);

        ScriptMethod studentPersonalAddMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportStudentPersonalEvent", EventAction.class), EventAction.ADD);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_STUDENT_PERSONAL_ADD, studentPersonalAddMethod);
        ScriptMethod studentPersonalChangeMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportStudentPersonalEvent", EventAction.class), EventAction.CHANGE);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_STUDENT_PERSONAL_CHANGE, studentPersonalChangeMethod);
        ScriptMethod studentPersonalDeleteMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportStudentPersonalEvent", EventAction.class), EventAction.DELETE);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_STUDENT_PERSONAL_DELETE, studentPersonalDeleteMethod);

        ScriptMethod studentLeaRelationshipAddMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportStudentLeaRelationshipEvent", EventAction.class), EventAction.ADD);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_STUDENT_LEA_RELATIONSHIP_ADD, studentLeaRelationshipAddMethod);
        ScriptMethod studentLeaRelationshipChangeMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportStudentLeaRelationshipEvent", EventAction.class), EventAction.CHANGE);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_STUDENT_LEA_RELATIONSHIP_CHANGE, studentLeaRelationshipChangeMethod);
        ScriptMethod studentLeaRelationshipDeleteMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportStudentLeaRelationshipEvent", EventAction.class), EventAction.DELETE);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_STUDENT_LEA_RELATIONSHIP_DELETE, studentLeaRelationshipDeleteMethod);

        ScriptMethod studentSchoolEnrollmentAddMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportStudentSchoolEnrollmentEvent", EventAction.class), EventAction.ADD);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_STUDENT_SCHOOL_ENROLLMENT_ADD, studentSchoolEnrollmentAddMethod);
        ScriptMethod studentSchoolEnrollmentChangeMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportStudentSchoolEnrollmentEvent", EventAction.class), EventAction.CHANGE);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_STUDENT_SCHOOL_ENROLLMENT_CHANGE, studentSchoolEnrollmentChangeMethod);
        ScriptMethod studentSchoolEnrollmentDeleteMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportStudentSchoolEnrollmentEvent", EventAction.class), EventAction.DELETE);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_STUDENT_SCHOOL_ENROLLMENT_DELETE, studentSchoolEnrollmentDeleteMethod);

        ScriptMethod staffPersonalAddMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportStaffPersonalEvent", EventAction.class), EventAction.ADD);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_STAFF_PERSONAL_ADD, staffPersonalAddMethod);
        ScriptMethod staffPersonalChangeMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportStaffPersonalEvent", EventAction.class), EventAction.CHANGE);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_STAFF_PERSONAL_CHANGE, staffPersonalChangeMethod);
        ScriptMethod staffPersonalDeleteMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportStaffPersonalEvent", EventAction.class), EventAction.DELETE);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_STAFF_PERSONAL_DELETE, staffPersonalDeleteMethod);

        ScriptMethod employeePersonalAddMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportEmployeePersonalEvent", EventAction.class), EventAction.ADD);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_EMPLOYEE_PERSONAL_ADD, employeePersonalAddMethod);
        ScriptMethod employeePersonalChangeMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportEmployeePersonalEvent", EventAction.class), EventAction.CHANGE);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_EMPLOYEE_PERSONAL_CHANGE, employeePersonalChangeMethod);
        ScriptMethod employeePersonalDeleteMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportEmployeePersonalEvent", EventAction.class), EventAction.DELETE);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_EMPLOYEE_PERSONAL_DELETE, employeePersonalDeleteMethod);
    }

    public List<Event> runReportScript(String script, long waitTime) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        List<Event> eventsSent = new ArrayList<Event>();
        LOG.info("Wait time (ms): " + waitTime);
        String[] eventDescriptors = script.split(",");
        for (String descriptor : eventDescriptors) {
            ScriptMethod scriptMethod = scriptMethodMap.get(descriptor);
            LOG.info("Executing script method - " + scriptMethod.toString());
            Event eventSent = scriptMethod.execute();
            eventsSent.add(eventSent);
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                LOG.error("Exception while sleeping", e);
            }
        }
        return eventsSent;
    }

    public Event reportLeaInfoEvent(EventAction action) throws ADKException {
        LOG.info("LeaInfo " + action.toString());
        LEAInfo leaInfo = SifEntityGenerator.generateTestLEAInfo();
        if (action == EventAction.CHANGE) {
            leaInfo.setChanged();
            leaInfo.setLEAURL("http://www.example.com");
        }
        Event event = new Event(leaInfo, action);
        zone.reportEvent(event);
        return event;
    }

    public Event reportSchoolInfoEvent(EventAction action) throws ADKException {
        LOG.info("SchoolInfo " + action.toString());
        SchoolInfo schoolInfo = SifEntityGenerator.generateTestSchoolInfo();
        if (action == EventAction.CHANGE) {
            schoolInfo.setChanged();
            schoolInfo.setSchoolURL("http://www.IL-DAYBREAK.edu");
        }
        Event event = new Event(schoolInfo, action);
        zone.reportEvent(event);
        return event;
    }

    public Event reportStudentPersonalEvent(EventAction action) throws ADKException {
        LOG.info("StudentPersonal " + action.toString());
        StudentPersonal studentPersonal = SifEntityGenerator.generateTestStudentPersonal();
        if (action == EventAction.CHANGE) {
            studentPersonal.setChanged();
            studentPersonal.setMigrant(YesNoUnknown.UNKNOWN);
        }
        Event event = new Event(studentPersonal, action);
        zone.reportEvent(event);
        return event;
    }

    public Event reportStudentLeaRelationshipEvent(EventAction action) throws ADKException {
        LOG.info("StudentLeaRelationship " + action.toString());
        StudentLEARelationship studentLeaRelationship = SifEntityGenerator.generateTestStudentLeaRelationship();
        if (action == EventAction.CHANGE) {
            studentLeaRelationship.setChanged();
            studentLeaRelationship.setGradeLevel(GradeLevelCode._09);
        }
        Event event = new Event(studentLeaRelationship, action);
        zone.reportEvent(event);
        return event;
    }

    public Event reportStudentSchoolEnrollmentEvent(EventAction action) throws ADKException {
        LOG.info("StudentSchoolEnrollment " + action.toString());
        StudentSchoolEnrollment studentSchoolEnrollment = SifEntityGenerator.generateTestStudentSchoolEnrollment();
        if (action == EventAction.CHANGE) {
            studentSchoolEnrollment.setChanged();
            studentSchoolEnrollment.setExitType(ExitTypeCode._1923_DIED_OR_INCAPACITATED);
        }
        Event event = new Event(studentSchoolEnrollment, action);
        zone.reportEvent(event);
        return event;
    }

    public Event reportStaffPersonalEvent(EventAction action) throws ADKException {
        LOG.info("StaffPersonal " + action.toString());
        StaffPersonal staffPersonal = SifEntityGenerator.generateTestStaffPersonal();
        if (action == EventAction.CHANGE) {
            staffPersonal.setChanged();
            staffPersonal.setEmailList(new EmailList(new Email(EmailType.PRIMARY, "chuckyw@imginc.com")));
        }
        Event event = new Event(staffPersonal, action);
        zone.reportEvent(event);
        return event;
    }

    public Event reportEmployeePersonalEvent(EventAction action) throws ADKException {
        LOG.info("EmployeePersonal " + action.toString());
        EmployeePersonal employeePersonal = SifEntityGenerator.generateTestEmployeePersonal();
        if (action == EventAction.CHANGE) {
            employeePersonal.setChanged();
            employeePersonal.setOtherIdList(new HrOtherIdList(new OtherId(OtherIdType.CERTIFICATE, "certificate")));
        }
        Event event = new Event(employeePersonal, action);
        zone.reportEvent(event);
        return event;
    }

    public Event reportEvent(String messageFile, String eventAction) throws ADKException {
        Event event = CustomEventGenerator.generateEvent(messageFile, EventAction.valueOf(eventAction));
        if (event == null) {
            LOG.error("Null event can not be reported");
            return null;
        }
        if (zone.isConnected()) {
            zone.reportEvent(event);
        } else {
            LOG.error("Zone is not connected");
            return null;
        }
        return event;
    }

    @Override
    public void onRequest(DataObjectOutputStream out, Query query, Zone zone, MessageInfo info) throws ADKException {
        LOG.info("Received request to publish data:\n" + "\tQuery:\n" + query.toXML() + "\n" + "\tZone: "
                + zone.getZoneId() + "\n" + "\tInfo: " + info.getMessage());
    }

    @SuppressWarnings("unused")
    private void inspectAndDestroyEvent(Event e) {
        try {
            SIFDataObject dataObj = e.getData().readDataObject();
            LOG.info(dataObj.toString());
        } catch (ADKException e1) {
            LOG.error("Error trying to inspect event", e1);
        }
    }
}
