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

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;

import openadk.library.ADKException;
import openadk.library.ElementDef;
import openadk.library.Event;
import openadk.library.EventAction;
import openadk.library.PublishingOptions;
import openadk.library.SIFDataObject;
import openadk.library.Zone;
import openadk.library.common.CommonDTD;
import openadk.library.common.ExitTypeCode;
import openadk.library.common.GradeLevelCode;
import openadk.library.common.StudentLEARelationship;
import openadk.library.common.YesNoUnknown;
import openadk.library.student.LEAInfo;
import openadk.library.student.SchoolInfo;
import openadk.library.student.StudentDTD;
import openadk.library.student.StudentPersonal;
import openadk.library.student.StudentSchoolEnrollment;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slc.sli.sif.ADKTest;
import org.slc.sli.sif.generator.EventGenerator;
import org.slc.sli.sif.generator.GeneratorScriptEvent;
import org.slc.sli.sif.generator.SifEntityGenerator;

/**
 * Test class for EventReporter
 *
 * @author vmcglaughlin
 *
 */
public class EventReporterTest extends ADKTest {

    private EventReporter eventReporter;

    @Mock
    private Zone zone;

    @Before
    public void setup() {
        super.setup();
        zone = Mockito.mock(Zone.class);
        Mockito.when(zone.isConnected()).thenReturn(true);
        try {
            eventReporter = new EventReporter(zone);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (ADKException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testConstructor() throws ADKException {
        Mockito.verify(zone, Mockito.times(5)).setPublisher(Mockito.eq(eventReporter), Mockito.any(ElementDef.class), Mockito.any(PublishingOptions.class));

        Mockito.verify(zone, Mockito.times(1)).setPublisher(Mockito.eq(eventReporter), Mockito.eq(StudentDTD.SCHOOLINFO), Mockito.any(PublishingOptions.class));
        Mockito.verify(zone, Mockito.times(1)).setPublisher(Mockito.eq(eventReporter), Mockito.eq(StudentDTD.LEAINFO), Mockito.any(PublishingOptions.class));
        Mockito.verify(zone, Mockito.times(1)).setPublisher(Mockito.eq(eventReporter), Mockito.eq(StudentDTD.STUDENTPERSONAL), Mockito.any(PublishingOptions.class));
        Mockito.verify(zone, Mockito.times(1)).setPublisher(Mockito.eq(eventReporter), Mockito.eq(StudentDTD.STUDENTSCHOOLENROLLMENT), Mockito.any(PublishingOptions.class));
        Mockito.verify(zone, Mockito.times(1)).setPublisher(Mockito.eq(eventReporter), Mockito.eq(CommonDTD.STUDENTLEARELATIONSHIP), Mockito.any(PublishingOptions.class));
    }

    @Test
    public void testRunReportScript() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ADKException {
        Map<Class<? extends SIFDataObject>, List<EventAction>> testMap;
        testMap = new LinkedHashMap<Class<? extends SIFDataObject>, List<EventAction>>();

        List<EventAction> addChangeDeleteEventActionList = new ArrayList<EventAction>();
        addChangeDeleteEventActionList.add(EventAction.ADD);
        addChangeDeleteEventActionList.add(EventAction.CHANGE);
        addChangeDeleteEventActionList.add(EventAction.DELETE);

        testMap.put(LEAInfo.class, addChangeDeleteEventActionList);
        testMap.put(SchoolInfo.class, addChangeDeleteEventActionList);
        testMap.put(StudentPersonal.class, addChangeDeleteEventActionList);
        testMap.put(StudentLEARelationship.class, addChangeDeleteEventActionList);
        testMap.put(StudentSchoolEnrollment.class, addChangeDeleteEventActionList);


        String script = GeneratorScriptEvent.KEY_LEA_INFO_ADD + "," +
            GeneratorScriptEvent.KEY_LEA_INFO_CHANGE + "," +
            GeneratorScriptEvent.KEY_LEA_INFO_DELETE + "," +
            GeneratorScriptEvent.KEY_SCHOOL_INFO_ADD + "," +
            GeneratorScriptEvent.KEY_SCHOOL_INFO_CHANGE + "," +
            GeneratorScriptEvent.KEY_SCHOOL_INFO_DELETE + "," +
            GeneratorScriptEvent.KEY_STUDENT_PERSONAL_ADD + "," +
            GeneratorScriptEvent.KEY_STUDENT_PERSONAL_CHANGE + "," +
            GeneratorScriptEvent.KEY_STUDENT_PERSONAL_DELETE + "," +
            GeneratorScriptEvent.KEY_STUDENT_LEA_RELATIONSHIP_ADD + "," +
            GeneratorScriptEvent.KEY_STUDENT_LEA_RELATIONSHIP_CHANGE + "," +
            GeneratorScriptEvent.KEY_STUDENT_LEA_RELATIONSHIP_DELETE + "," +
            GeneratorScriptEvent.KEY_STUDENT_SCHOOL_ENROLLMENT_ADD + "," +
            GeneratorScriptEvent.KEY_STUDENT_SCHOOL_ENROLLMENT_CHANGE + "," +
            GeneratorScriptEvent.KEY_STUDENT_SCHOOL_ENROLLMENT_DELETE;
        long waitTime = 0;

        List<Event> eventsSent = eventReporter.runReportScript(script, waitTime);
        Mockito.verify(zone, Mockito.times(15)).reportEvent(Mockito.any(Event.class));

        Assert.assertEquals(15, eventsSent.size());

        int index = 0;
        for (Class<? extends SIFDataObject> expectedClass : testMap.keySet()) {
            for (EventAction expectedAction : testMap.get(expectedClass)) {
                Event event = eventsSent.get(index++);
                checkScriptedEvent(event, expectedClass, expectedAction);
            }
        }
    }

    private void checkScriptedEvent(Event event, Class<? extends SIFDataObject> expectedClass, EventAction expectedAction) throws ADKException {
        SIFDataObject dataObject = event.getData().readDataObject();
        Assert.assertTrue(dataObject.getClass().isAssignableFrom(expectedClass));
        Assert.assertEquals(event.getAction(), expectedAction);
    }

    @Test
    public void runReportLeaInfoEventTests() throws ADKException {
        Class<? extends SIFDataObject> expectedClass = LEAInfo.class;
        String expectedId = SifEntityGenerator.TEST_LEAINFO_REFID;

        EventAction eventAction = EventAction.ADD;
        Event sentEvent = eventReporter.reportLeaInfoEvent(eventAction);
        LEAInfo dataObject = (LEAInfo) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, false);
        Assert.assertEquals("http://IL-DAYBREAK.edu", dataObject.getLEAURL());

        eventAction = EventAction.CHANGE;
        sentEvent = eventReporter.reportLeaInfoEvent(eventAction);
        dataObject = (LEAInfo) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, true);
        Assert.assertEquals("http://www.example.com", dataObject.getLEAURL());

        eventAction = EventAction.DELETE;
        sentEvent = eventReporter.reportLeaInfoEvent(eventAction);
        dataObject = (LEAInfo) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, false);
        Assert.assertEquals("http://IL-DAYBREAK.edu", dataObject.getLEAURL());
    }

    @Test
    public void runReportSchoolInfoEventTests() throws ADKException {
        Class<? extends SIFDataObject> expectedClass = SchoolInfo.class;
        String expectedId = SifEntityGenerator.TEST_SCHOOLINFO_REFID;

        EventAction eventAction = EventAction.ADD;
        Event sentEvent = eventReporter.reportSchoolInfoEvent(eventAction);
        SchoolInfo dataObject = (SchoolInfo) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, false);
        Assert.assertEquals("http://IL-DAYBREAK.edu", dataObject.getSchoolURL());

        eventAction = EventAction.CHANGE;
        sentEvent = eventReporter.reportSchoolInfoEvent(eventAction);
        dataObject = (SchoolInfo) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, true);
        Assert.assertEquals("http://www.IL-DAYBREAK.edu", dataObject.getSchoolURL());

        eventAction = EventAction.DELETE;
        sentEvent = eventReporter.reportSchoolInfoEvent(eventAction);
        dataObject = (SchoolInfo) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, false);
        Assert.assertEquals("http://IL-DAYBREAK.edu", dataObject.getSchoolURL());
    }

    @Test
    public void runReportStudentPersonalEventTests() throws ADKException {
        Class<? extends SIFDataObject> expectedClass = StudentPersonal.class;
        String expectedId = SifEntityGenerator.TEST_STUDENTPERSONAL_REFID;

        EventAction eventAction = EventAction.ADD;
        Event sentEvent = eventReporter.reportStudentPersonalEvent(eventAction);
        StudentPersonal dataObject = (StudentPersonal) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, false);
        Assert.assertEquals(YesNoUnknown.NO.getValue(), dataObject.getMigrant());

        eventAction = EventAction.CHANGE;
        sentEvent = eventReporter.reportStudentPersonalEvent(eventAction);
        dataObject = (StudentPersonal) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, true);
        Assert.assertEquals(YesNoUnknown.UNKNOWN.getValue(), dataObject.getMigrant());

        eventAction = EventAction.DELETE;
        sentEvent = eventReporter.reportStudentPersonalEvent(eventAction);
        dataObject = (StudentPersonal) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, false);
        Assert.assertEquals(YesNoUnknown.NO.getValue(), dataObject.getMigrant());
    }

    @Test
    public void runReportStudentLeaRelationshipEventTests() throws ADKException {
        Class<? extends SIFDataObject> expectedClass = StudentLEARelationship.class;
        String expectedId = SifEntityGenerator.TEST_STUDENTLEARELATIONSHIP_REFID;

        EventAction eventAction = EventAction.ADD;
        Event sentEvent = eventReporter.reportStudentLeaRelationshipEvent(eventAction);
        StudentLEARelationship dataObject = (StudentLEARelationship) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, false);
        Assert.assertEquals(GradeLevelCode._10.getValue(), dataObject.getGradeLevel().getCode());

        eventAction = EventAction.CHANGE;
        sentEvent = eventReporter.reportStudentLeaRelationshipEvent(eventAction);
        dataObject = (StudentLEARelationship) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, true);
        Assert.assertEquals(GradeLevelCode._09.getValue(), dataObject.getGradeLevel().getCode());

        eventAction = EventAction.DELETE;
        sentEvent = eventReporter.reportStudentLeaRelationshipEvent(eventAction);
        dataObject = (StudentLEARelationship) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, false);
        Assert.assertEquals(GradeLevelCode._10.getValue(), dataObject.getGradeLevel().getCode());
    }

    @Test
    public void runReportStudentSchoolEnrollmentEventTests() throws ADKException {
        Class<? extends SIFDataObject> expectedClass = StudentSchoolEnrollment.class;
        String expectedId = SifEntityGenerator.TEST_STUDENTSCHOOLENROLLMENT_REFID;

        EventAction eventAction = EventAction.ADD;
        Event sentEvent = eventReporter.reportStudentSchoolEnrollmentEvent(eventAction);
        StudentSchoolEnrollment dataObject = (StudentSchoolEnrollment) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, false);
        Assert.assertEquals(ExitTypeCode._3502_NOT_ENROLLED_ELIGIBLE_TO.getValue(), dataObject.getExitType().getCode());

        eventAction = EventAction.CHANGE;
        sentEvent = eventReporter.reportStudentSchoolEnrollmentEvent(eventAction);
        dataObject = (StudentSchoolEnrollment) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, true);
        Assert.assertEquals(ExitTypeCode._1923_DIED_OR_INCAPACITATED.getValue(), dataObject.getExitType().getCode());

        eventAction = EventAction.DELETE;
        sentEvent = eventReporter.reportStudentSchoolEnrollmentEvent(eventAction);
        dataObject = (StudentSchoolEnrollment) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, false);
        Assert.assertEquals(ExitTypeCode._3502_NOT_ENROLLED_ELIGIBLE_TO.getValue(), dataObject.getExitType().getCode());
    }

    private SIFDataObject runDataObjectEventTest(Event sentEvent, EventAction action, Class<? extends SIFDataObject> expectedClass, String expectedId, boolean expectedChanged) throws ADKException {
        Mockito.verify(zone, Mockito.times(1)).reportEvent(Mockito.eq(sentEvent));

        Assert.assertEquals(action, sentEvent.getAction());
        SIFDataObject dataObject = sentEvent.getData().readDataObject();
        Assert.assertTrue(dataObject.getClass().isAssignableFrom(expectedClass));
        Assert.assertEquals(expectedId, dataObject.getRefId());
        Assert.assertEquals(expectedChanged, dataObject.isChanged());

        return dataObject;
    }

    @Test
    public void testReportEventValidFile() throws ADKException {
        URL url = getClass().getResource("/element_xml/StudentPersonal.xml");
        String validFilename = url.getPath();

        Event event = eventReporter.reportEvent(validFilename);
        Assert.assertNotNull(event);
        Mockito.verify(zone, Mockito.times(1)).reportEvent(Mockito.eq(event));

        Mockito.when(zone.isConnected()).thenReturn(false);
        event = eventReporter.reportEvent(validFilename);
        Assert.assertNull(event);
        Mockito.verify(zone, Mockito.times(0)).reportEvent(Mockito.eq(event));
    }

    @Test
    public void testReportEventInvalidFile() throws ADKException {
        String invalidFilename = "doesntexist.xml";
        Event event = eventReporter.reportEvent(invalidFilename);
        Assert.assertNull(event);
        Mockito.verify(zone, Mockito.times(0)).reportEvent(Mockito.eq(event));
    }

}
