/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import openadk.library.common.OtherId;
import openadk.library.common.OtherIdType;
import openadk.library.common.StudentLEARelationship;
import openadk.library.common.YesNo;
import openadk.library.common.YesNoUnknown;
import openadk.library.hrfin.EmployeeAssignment;
import openadk.library.hrfin.EmployeePersonal;
import openadk.library.hrfin.EmploymentRecord;
import openadk.library.hrfin.HrfinDTD;
import openadk.library.student.LEAInfo;
import openadk.library.student.SchoolInfo;
import openadk.library.student.StaffAssignment;
import openadk.library.student.StaffPersonal;
import openadk.library.student.StudentDTD;
import openadk.library.student.StudentPersonal;
import openadk.library.student.StudentSchoolEnrollment;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.slc.sli.sif.EventReporterAdkTest;
import org.slc.sli.sif.generator.SifEntityGenerator;

/**
 * Test class for EventReporter
 *
 * @author vmcglaughlin
 *
 */
public class EventReporterTest extends EventReporterAdkTest {

    private EventReporter eventReporter;

    @Mock
    private Zone zone;

    @Override
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
        ElementDef[] expectedPublishingTypes = {
                StudentDTD.SCHOOLINFO,
                StudentDTD.LEAINFO,
                StudentDTD.STUDENTPERSONAL,
                StudentDTD.STUDENTSCHOOLENROLLMENT,
                CommonDTD.STUDENTLEARELATIONSHIP,
                StudentDTD.STAFFPERSONAL,
                HrfinDTD.EMPLOYEEPERSONAL,
                StudentDTD.STAFFASSIGNMENT,
                HrfinDTD.EMPLOYEEASSIGNMENT,
                HrfinDTD.EMPLOYMENTRECORD
        };

        Mockito.verify(zone, Mockito.times(expectedPublishingTypes.length)).setPublisher(Mockito.eq(eventReporter),
                Mockito.any(ElementDef.class), Mockito.any(PublishingOptions.class));

        for (ElementDef publishingType : expectedPublishingTypes) {
            Mockito.verify(zone, Mockito.times(1)).setPublisher(Mockito.eq(eventReporter), Mockito.eq(publishingType),
                    Mockito.any(PublishingOptions.class));
        }
    }

    @Test
    public void testRunReportScript() throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, ADKException {
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

        String[] scriptArray = {
                "LEAInfoAdd",
                "LEAInfoChange",
                "LEAInfoDelete",
                "SchoolInfoAdd",
                "SchoolInfoChange",
                "SchoolInfoDelete",
                "StudentPersonalAdd",
                "StudentPersonalChange",
                "StudentPersonalDelete",
                "StudentLEARelationshipAdd",
                "StudentLEARelationshipChange",
                "StudentLEARelationshipDelete",
                "StudentSchoolEnrollmentAdd",
                "StudentSchoolEnrollmentChange",
                "StudentSchoolEnrollmentDelete",
                "StaffPersonalAdd",
                "StaffPersonalChange",
                "StaffPersonalDelete",
                "EmployeePersonalAdd",
                "EmployeePersonalChange",
                "EmployeePersonalDelete",
                "StaffAssignmentAdd",
                "StaffAssignmentChange",
                "StaffAssignmentDelete",
                "EmploymentRecordAdd",
                "EmploymentRecordChange",
                "EmploymentRecordDelete",
                "EmployeeAssignmentAdd",
                "EmployeeAssignmentChange",
                "EmployeeAssignmentDelete"
        };

        String script = "";
        for (String item : scriptArray) {
            script += item + ",";
        }
        long waitTime = 0;

        List<Event> eventsSent = eventReporter.runReportScript(script, waitTime);
        Mockito.verify(zone, Mockito.times(scriptArray.length)).reportEvent(Mockito.any(Event.class));

        Assert.assertEquals(scriptArray.length, eventsSent.size());

        int index = 0;
        for (Class<? extends SIFDataObject> expectedClass : testMap.keySet()) {
            for (EventAction expectedAction : testMap.get(expectedClass)) {
                Event event = eventsSent.get(index++);
                checkScriptedEvent(event, expectedClass, expectedAction);
            }
        }
    }

    private void checkScriptedEvent(Event event, Class<? extends SIFDataObject> expectedClass,
            EventAction expectedAction) throws ADKException {
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
        SchoolInfo dataObject = (SchoolInfo) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId,
                false);
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
        StudentPersonal dataObject = (StudentPersonal) runDataObjectEventTest(sentEvent, eventAction, expectedClass,
                expectedId, false);
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
        StudentLEARelationship dataObject = (StudentLEARelationship) runDataObjectEventTest(sentEvent, eventAction,
                expectedClass, expectedId, false);
        Assert.assertEquals(GradeLevelCode._10.getValue(), dataObject.getGradeLevel().getCode());

        eventAction = EventAction.CHANGE;
        sentEvent = eventReporter.reportStudentLeaRelationshipEvent(eventAction);
        dataObject = (StudentLEARelationship) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId,
                true);
        Assert.assertEquals(GradeLevelCode._09.getValue(), dataObject.getGradeLevel().getCode());

        eventAction = EventAction.DELETE;
        sentEvent = eventReporter.reportStudentLeaRelationshipEvent(eventAction);
        dataObject = (StudentLEARelationship) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId,
                false);
        Assert.assertEquals(GradeLevelCode._10.getValue(), dataObject.getGradeLevel().getCode());
    }

    @Test
    public void runReportStudentSchoolEnrollmentEventTests() throws ADKException {
        Class<? extends SIFDataObject> expectedClass = StudentSchoolEnrollment.class;
        String expectedId = SifEntityGenerator.TEST_STUDENTSCHOOLENROLLMENT_REFID;

        EventAction eventAction = EventAction.ADD;
        Event sentEvent = eventReporter.reportStudentSchoolEnrollmentEvent(eventAction);
        StudentSchoolEnrollment dataObject = (StudentSchoolEnrollment) runDataObjectEventTest(sentEvent, eventAction,
                expectedClass, expectedId, false);
        Assert.assertEquals(ExitTypeCode._3502_NOT_ENROLLED_ELIGIBLE_TO.getValue(), dataObject.getExitType().getCode());

        eventAction = EventAction.CHANGE;
        sentEvent = eventReporter.reportStudentSchoolEnrollmentEvent(eventAction);
        dataObject = (StudentSchoolEnrollment) runDataObjectEventTest(sentEvent, eventAction, expectedClass,
                expectedId, true);
        Assert.assertEquals(ExitTypeCode._1923_DIED_OR_INCAPACITATED.getValue(), dataObject.getExitType().getCode());

        eventAction = EventAction.DELETE;
        sentEvent = eventReporter.reportStudentSchoolEnrollmentEvent(eventAction);
        dataObject = (StudentSchoolEnrollment) runDataObjectEventTest(sentEvent, eventAction, expectedClass,
                expectedId, false);
        Assert.assertEquals(ExitTypeCode._3502_NOT_ENROLLED_ELIGIBLE_TO.getValue(), dataObject.getExitType().getCode());
    }

    @Test
    public void runReportStaffPersonalEventTests() throws ADKException {
        Class<? extends SIFDataObject> expectedClass = StaffPersonal.class;
        String expectedId = SifEntityGenerator.TEST_STAFFPERSONAL_REFID;

        EventAction eventAction = EventAction.ADD;
        Event sentEvent = eventReporter.reportStaffPersonalEvent(eventAction);
        StaffPersonal dataObject = (StaffPersonal) runDataObjectEventTest(sentEvent, eventAction, expectedClass,
                expectedId, false);
        String emailAddress = dataObject.getEmailList().get(0).getValue();
        Assert.assertEquals("chuckw@imginc.com", emailAddress);

        eventAction = EventAction.CHANGE;
        sentEvent = eventReporter.reportStaffPersonalEvent(eventAction);
        dataObject = (StaffPersonal) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, true);
        emailAddress = dataObject.getEmailList().get(0).getValue();
        Assert.assertEquals("chuckyw@imginc.com", emailAddress);

        eventAction = EventAction.DELETE;
        sentEvent = eventReporter.reportStaffPersonalEvent(eventAction);
        dataObject = (StaffPersonal) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, false);
        emailAddress = dataObject.getEmailList().get(0).getValue();
        Assert.assertEquals("chuckw@imginc.com", emailAddress);
    }

    @Test
    public void runReportEmployeePersonalEventTests() throws ADKException {
        Class<? extends SIFDataObject> expectedClass = EmployeePersonal.class;
        String expectedId = SifEntityGenerator.TEST_EMPLOYEEPERSONAL_REFID;

        EventAction eventAction = EventAction.ADD;
        Event sentEvent = eventReporter.reportEmployeePersonalEvent(eventAction);
        EmployeePersonal dataObject = (EmployeePersonal) runDataObjectEventTest(sentEvent, eventAction, expectedClass,
                expectedId, false);
        OtherId otherId = dataObject.getOtherIdList().get(0);
        Assert.assertEquals(OtherIdType.SOCIALSECURITY.getValue(), otherId.getType());
        Assert.assertEquals("333333333", otherId.getValue());

        eventAction = EventAction.CHANGE;
        sentEvent = eventReporter.reportEmployeePersonalEvent(eventAction);
        dataObject = (EmployeePersonal) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, true);
        otherId = dataObject.getOtherIdList().get(0);
        Assert.assertEquals(OtherIdType.CERTIFICATE.getValue(), otherId.getType());
        Assert.assertEquals("certificate", otherId.getValue());

        eventAction = EventAction.DELETE;
        sentEvent = eventReporter.reportEmployeePersonalEvent(eventAction);
        dataObject = (EmployeePersonal) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, false);
        otherId = dataObject.getOtherIdList().get(0);
        Assert.assertEquals(OtherIdType.SOCIALSECURITY.getValue(), otherId.getType());
        Assert.assertEquals("333333333", otherId.getValue());
    }

    @Test
    public void runReportStaffAssignmentEventTests() throws ADKException {
        Class<? extends SIFDataObject> expectedClass = StaffAssignment.class;
        String expectedId = SifEntityGenerator.TEST_STAFFASSIGNMENT_REFID;

        EventAction eventAction = EventAction.ADD;
        Event sentEvent = eventReporter.reportStaffAssignmentEvent(eventAction);
        StaffAssignment dataObject = (StaffAssignment) runDataObjectEventTest(sentEvent, eventAction, expectedClass,
                expectedId, false);
        Assert.assertEquals(YesNo.YES.getValue(), dataObject.getPrimaryAssignment());

        eventAction = EventAction.CHANGE;
        sentEvent = eventReporter.reportStaffAssignmentEvent(eventAction);
        dataObject = (StaffAssignment) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, true);
        Assert.assertEquals(YesNo.NO.getValue(), dataObject.getPrimaryAssignment());

        eventAction = EventAction.DELETE;
        sentEvent = eventReporter.reportStaffAssignmentEvent(eventAction);
        dataObject = (StaffAssignment) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, false);
        Assert.assertEquals(YesNo.YES.getValue(), dataObject.getPrimaryAssignment());
    }

    @Test
    public void runReportEmploymentRecordEventTests() throws ADKException {
        Class<? extends SIFDataObject> expectedClass = EmploymentRecord.class;
        String expectedId = SifEntityGenerator.TEST_EMPLOYMENTRECORD_REFID;

        EventAction eventAction = EventAction.ADD;
        Event sentEvent = eventReporter.reportEmploymentRecordEvent(eventAction);
        EmploymentRecord dataObject = (EmploymentRecord) runDataObjectEventTest(sentEvent, eventAction, expectedClass,
                expectedId, false);
        Assert.assertEquals("10", dataObject.getPositionNumber());

        eventAction = EventAction.CHANGE;
        sentEvent = eventReporter.reportEmploymentRecordEvent(eventAction);
        dataObject = (EmploymentRecord) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, true);
        Assert.assertEquals("15", dataObject.getPositionNumber());

        eventAction = EventAction.DELETE;
        sentEvent = eventReporter.reportEmploymentRecordEvent(eventAction);
        dataObject = (EmploymentRecord) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, false);
        Assert.assertEquals("10", dataObject.getPositionNumber());
    }

    @Test
    public void runReportEmployeeAssignmentEventTests() throws ADKException {
        Class<? extends SIFDataObject> expectedClass = EmployeeAssignment.class;
        String expectedId = SifEntityGenerator.TEST_EMPLOYEEASSIGNMENT_REFID;

        EventAction eventAction = EventAction.ADD;
        Event sentEvent = eventReporter.reportEmployeeAssignmentEvent(eventAction);
        EmployeeAssignment dataObject = (EmployeeAssignment) runDataObjectEventTest(sentEvent, eventAction, expectedClass,
                expectedId, false);
        Assert.assertEquals(YesNo.YES.getValue(), dataObject.getPrimaryAssignment());

        eventAction = EventAction.CHANGE;
        sentEvent = eventReporter.reportEmployeeAssignmentEvent(eventAction);
        dataObject = (EmployeeAssignment) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, true);
        Assert.assertEquals(YesNo.NO.getValue(), dataObject.getPrimaryAssignment());

        eventAction = EventAction.DELETE;
        sentEvent = eventReporter.reportEmployeeAssignmentEvent(eventAction);
        dataObject = (EmployeeAssignment) runDataObjectEventTest(sentEvent, eventAction, expectedClass, expectedId, false);
        Assert.assertEquals(YesNo.YES.getValue(), dataObject.getPrimaryAssignment());
    }

    private SIFDataObject runDataObjectEventTest(Event sentEvent, EventAction action,
            Class<? extends SIFDataObject> expectedClass, String expectedId, boolean expectedChanged)
            throws ADKException {
        Mockito.verify(zone, Mockito.times(1)).reportEvent(Mockito.eq(sentEvent));

        Assert.assertEquals(action, sentEvent.getAction());
        SIFDataObject dataObject = sentEvent.getData().readDataObject();
        Assert.assertTrue(dataObject.getClass().isAssignableFrom(expectedClass));
        Assert.assertEquals(expectedId, dataObject.getRefId());
        // Assert.assertEquals(expectedChanged, dataObject.isChanged());

        return dataObject;
    }

    @Test
    public void testReportEventValidFile() throws ADKException {
        URL url = getClass().getResource("/element_xml/StudentPersonal.xml");
        String validFilename = url.getPath();

        // Test ADD
        String eventActionStr = EventAction.ADD.toString();
        Event event = eventReporter.reportEvent(validFilename, eventActionStr);
        Assert.assertNotNull(event);
        EventAction eventAction = event.getAction();
        Assert.assertEquals(eventActionStr, eventAction.toString());
        Mockito.verify(zone, Mockito.times(1)).reportEvent(Mockito.eq(event));

        // Test CHANGE
        eventActionStr = EventAction.CHANGE.toString();
        event = eventReporter.reportEvent(validFilename, eventActionStr);
        Assert.assertNotNull(event);
        eventAction = event.getAction();
        Assert.assertEquals(eventActionStr, eventAction.toString());
        Mockito.verify(zone, Mockito.times(1)).reportEvent(Mockito.eq(event));

        // Test DELETE
        eventActionStr = EventAction.DELETE.toString();
        event = eventReporter.reportEvent(validFilename, eventActionStr);
        Assert.assertNotNull(event);
        eventAction = event.getAction();
        Assert.assertEquals(eventActionStr, eventAction.toString());
        Mockito.verify(zone, Mockito.times(1)).reportEvent(Mockito.eq(event));

        Mockito.when(zone.isConnected()).thenReturn(false);
        event = eventReporter.reportEvent(validFilename, EventAction.ADD.toString());
        Assert.assertNull(event);
        Mockito.verify(zone, Mockito.times(0)).reportEvent(Mockito.eq(event));
    }

    @Test
    public void testReportEventInvalidFile() throws ADKException {
        String invalidFilename = "doesntexist.xml";
        Event event = eventReporter.reportEvent(invalidFilename, EventAction.ADD.toString());
        Assert.assertNull(event);
        Mockito.verify(zone, Mockito.times(0)).reportEvent(Mockito.eq(event));
    }

}
