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
import java.util.HashMap;
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
import openadk.library.common.ExitTypeCode;
import openadk.library.common.GradeLevelCode;
import openadk.library.common.StudentLEARelationship;
import openadk.library.student.LEAInfo;
import openadk.library.student.SchoolInfo;
import openadk.library.student.StudentDTD;
import openadk.library.student.StudentPersonal;
import openadk.library.student.StudentSchoolEnrollment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.sif.generator.CustomEventGenerator;
import org.slc.sli.sif.generator.EventGenerator;
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

            EventReporterAgent agent = createReporterAgent(agentId, zoneUrl);
            agent.startAgent();
            Zone zone = agent.getZoneFactory().getZone(localZoneId);

            EventReporter reporter = new EventReporter(zone, props);
            reporter.runReportScript(script);
        } catch (Exception e) { // Have to catch top-level Exception due to agent.startAgent()
            logger.error("Exception trying to report event", e);
        }
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
    private Properties props;
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

        public void execute() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
            method.invoke(executingClass, args);
        }
    }

    public EventReporter(Zone zone, Properties props) throws Exception {
        this.zone = zone;
        this.zone.setPublisher(this, StudentDTD.SCHOOLINFO, new PublishingOptions(true));
        this.zone.setPublisher(this, StudentDTD.LEAINFO, new PublishingOptions(true));
        this.zone.setPublisher(this, StudentDTD.STUDENTPERSONAL, new PublishingOptions(true));
        this.zone.setPublisher(this, StudentDTD.STUDENTSCHOOLENROLLMENT, new PublishingOptions(true));
        this.zone.setPublisher(this, CommonDTD.STUDENTLEARELATIONSHIP, new PublishingOptions(true));
        this.props = props;

        populateMethodMap();
    }

    private void populateMethodMap() throws SecurityException, NoSuchMethodException {
        ScriptMethod leaInfoAddMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportLeaInfoEvent", EventAction.class), EventAction.ADD);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_LEA_INFO_ADD, leaInfoAddMethod);
        ScriptMethod leaInfoChangeMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportLeaInfoEvent", EventAction.class), EventAction.CHANGE);
        scriptMethodMap.put(GeneratorScriptEvent.KEY_LEA_INFO_CHANGE, leaInfoChangeMethod);
        ScriptMethod leaInfoDeleteMethod = new ScriptMethod(this, EventReporter.class.getMethod("reportSchoolInfoEvent", EventAction.class), EventAction.DELETE);
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
    }

    public void runReportScript(String script) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        long waitTime = ((Long) props.get(PropertyUtils.KEY_WAIT_TIME)).longValue();
        LOG.info("Wait time (ms): " + waitTime);
        String[] eventDescriptors = script.split(",");
        for (String descriptor : eventDescriptors) {
            ScriptMethod scriptMethod = scriptMethodMap.get(descriptor);
            scriptMethod.execute();
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                LOG.error("Exception while sleeping", e);
            }
        }
    }

    public void reportLeaInfoEvent(EventAction action) throws ADKException {
        LOG.info("LeaInfo " + action.toString());
        LEAInfo leaInfo = SifEntityGenerator.generateTestLEAInfo();
        if (action == EventAction.CHANGE) {
            throw new RuntimeException("LEAInfo change not supported");
        }
        zone.reportEvent(leaInfo, action);
    }

    public void reportSchoolInfoEvent(EventAction action) throws ADKException {
        LOG.info("SchoolInfo " + action.toString());
        SchoolInfo schoolInfo = SifEntityGenerator.generateTestSchoolInfo();
        if (action == EventAction.CHANGE) {
            schoolInfo.setChanged();
            schoolInfo.setSchoolURL("http://www.IL-DAYBREAK.edu");
        }
        zone.reportEvent(schoolInfo, action);
    }

    public void reportStudentPersonalEvent(EventAction action) throws ADKException {
        LOG.info("StudentPersonal " + action.toString());
        StudentPersonal studentPersonal = SifEntityGenerator.generateTestStudentPersonal();
        if (action == EventAction.CHANGE) {
            throw new RuntimeException("StudentPersonal change not supported");
        }
        zone.reportEvent(studentPersonal, action);
    }

    public void reportStudentLeaRelationshipEvent(EventAction action) throws ADKException {
        LOG.info("StudentLeaRelationship " + action.toString());
        StudentLEARelationship studentLeaRelationship = SifEntityGenerator.generateTestStudentLeaRelationship();
        if (action == EventAction.CHANGE) {
            studentLeaRelationship.setChanged();
            studentLeaRelationship.setGradeLevel(GradeLevelCode._09);
        }
        zone.reportEvent(studentLeaRelationship, action);
    }

    public void reportStudentSchoolEnrollmentEvent(EventAction action) throws ADKException {
        LOG.info("StudentSchoolEnrollment " + action.toString());
        StudentSchoolEnrollment studentSchoolEnrollment = SifEntityGenerator.generateTestStudentSchoolEnrollment();
        if (action == EventAction.CHANGE) {
            studentSchoolEnrollment.setChanged();
            studentSchoolEnrollment.setExitType(ExitTypeCode._1923_DIED_OR_INCAPACITATED);
        }
        zone.reportEvent(studentSchoolEnrollment, action);
    }

    public void reportEvent(String messageFile) throws ADKException {
        Properties props = new Properties();
        props.setProperty(CustomEventGenerator.MESSAGE_FILE, messageFile);
        EventGenerator generator = new CustomEventGenerator();
        Event event = generator.generateEvent(props);
        if (zone.isConnected()) {
            zone.reportEvent(event);
        } else {
            LOG.error("Zone is not connected");
        }
    }

    @Override
    public void onRequest(DataObjectOutputStream out, Query query, Zone zone, MessageInfo info) throws ADKException {
        LOG.info("Received request to publish data:\n" + "\tQuery:\n" + query.toXML() + "\n" + "\tZone: "
                + zone.getZoneId() + "\n" + "\tInfo: " + info.getMessage());
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
