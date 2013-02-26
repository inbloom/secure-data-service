/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates
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

package org.slc.sli.api.config;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.representation.EntityBody;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.when;

/**
 * Unit test for AttendanceTreatment.
 * @author chung
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class AttendanceTreatmentTest {

    @InjectMocks
    private AttendanceTreatment treatment;

    @Mock
    private EntityDefinition definition;

    // Fields
    private final String ATTENDANCE = "attendance";
    private final String ATTENDANCE_EVENT = "attendanceEvent";
    private final String SCHOOL_YEAR = "schoolYear";
    private final String SCHOOL_YEAR_ATTENDANCE = "schoolYearAttendance";
    private final String STUDENT_ID = "studentId";
    private final String SCHOOL_ID = "schoolId";
    private final String EVENT = "event";
    private final String DATE = "date";

    @Before
    public void setup() {
        treatment = new AttendanceTreatment();
        MockitoAnnotations.initMocks(this);
        when(definition.getType()).thenReturn(ATTENDANCE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testToStored() {
        EntityBody entityBody = treatment.toStored(new ArrayList<EntityBody>()
            {{ add(getAPIBody()); }}, definition).get(0);
        assertTrue(entityBody.containsKey(ATTENDANCE_EVENT));
        assertTrue(entityBody.containsKey(SCHOOL_YEAR));
        assertFalse(entityBody.containsKey(SCHOOL_YEAR_ATTENDANCE));
        assertTrue(entityBody.containsKey(SCHOOL_ID));
        assertTrue(entityBody.containsKey(STUDENT_ID));
        assertEquals("1234_id", entityBody.get(SCHOOL_ID));
        assertEquals("2345_id", entityBody.get(STUDENT_ID));
        assertEquals("1987-1988", entityBody.get(SCHOOL_YEAR));

        List<EntityBody> attendanceEvents = (List<EntityBody>) entityBody.get(ATTENDANCE_EVENT);
        assertEquals(2, attendanceEvents.size());
        for (EntityBody attendanceEvent : attendanceEvents) {
            assertTrue(attendanceEvent.containsKey(EVENT));
            assertTrue(attendanceEvent.containsKey(DATE));
            if (attendanceEvent.get(DATE).equals("1988-01-01")) {
                assertEquals("Tardy", attendanceEvent.get(EVENT));
            } else if (attendanceEvent.get(DATE).equals("1988-01-02")) {
                assertEquals("Unexcused Absence", attendanceEvent.get(EVENT));
            } else {
                fail(String.format("'%s' does not match any of the expected dates.", attendanceEvent.get(DATE)));
            }
        }
    }

    @Test
    public void testToStoredMultipleSchoolYearAttendances() {
        List<EntityBody> entityBodies = treatment.toStored(new ArrayList<EntityBody>()
            {{ add(getAPIBodyMultipleSYAs()); }}, definition);
        assertEquals(2, entityBodies.size());
    }

    @Test
    public void testToStoredSameSchoolYear() {
        List<EntityBody> entityBodies = treatment.toStored(new ArrayList<EntityBody>()
            {{ add(getAPIBodySameSchoolYear()); }}, definition);
        assertEquals(1, entityBodies.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testToExposed() {
        EntityBody entityBody = treatment.toExposed(getMongoBody(), definition, null);
        assertTrue(entityBody.containsKey(SCHOOL_YEAR_ATTENDANCE));
        assertFalse(entityBody.containsKey(ATTENDANCE_EVENT));
        assertFalse(entityBody.containsKey(SCHOOL_YEAR));
        assertTrue(entityBody.containsKey(SCHOOL_ID));
        assertTrue(entityBody.containsKey(STUDENT_ID));
        assertEquals("1234_id", entityBody.get(SCHOOL_ID));
        assertEquals("2345_id", entityBody.get(STUDENT_ID));

        List<EntityBody> schoolYearAttendances = (List<EntityBody>) entityBody.get(SCHOOL_YEAR_ATTENDANCE);
        assertEquals(1, schoolYearAttendances.size());

        EntityBody schoolYearAttendance = schoolYearAttendances.get(0);
        assertTrue(schoolYearAttendance.containsKey(SCHOOL_YEAR));
        assertTrue(schoolYearAttendance.containsKey(ATTENDANCE_EVENT));
        assertEquals("2011-2012", schoolYearAttendance.get(SCHOOL_YEAR));

        List<EntityBody> attendanceEvent = (List<EntityBody>) schoolYearAttendance.get(ATTENDANCE_EVENT);
        assertEquals(1, attendanceEvent.size());
        assertTrue(attendanceEvent.get(0).containsKey(EVENT));
        assertTrue(attendanceEvent.get(0).containsKey(DATE));
        assertEquals("In Attendance", attendanceEvent.get(0).get(EVENT));
        assertEquals("2011-11-11", attendanceEvent.get(0).get(DATE));
    }

    // It no longer sets an empty list
    @Ignore
    @SuppressWarnings("unchecked")
    public void testEmptyAttendanceEvent() {
        EntityBody mongoBody = getMongoBody();
        mongoBody.remove(ATTENDANCE_EVENT);
        EntityBody entityBody = treatment.toExposed(mongoBody, definition, null);
        List<EntityBody> schoolYearAttendances = (List<EntityBody>) entityBody.get(SCHOOL_YEAR_ATTENDANCE);
        List<EntityBody> attendanceEvents = (List<EntityBody>) schoolYearAttendances.get(0).get(ATTENDANCE_EVENT);
        assertNotNull(attendanceEvents);
        assertEquals(0, attendanceEvents.size());
    }

    private EntityBody getAPIBody() {
        EntityBody entityBody = new EntityBody();

        final List<EntityBody> attendanceEvents = new ArrayList<EntityBody>() {{
            add(new EntityBody() {{
                put(EVENT, "Tardy");
                put(DATE, "1988-01-01");
            }});
            add(new EntityBody() {{
                put(EVENT, "Unexcused Absence");
                put(DATE, "1988-01-02");
            }});
        }};
        List<EntityBody> schoolYearAttendances = new ArrayList<EntityBody>() {{
            add(new EntityBody() {{
                put(SCHOOL_YEAR, "1987-1988");
                put(ATTENDANCE_EVENT, attendanceEvents);
            }});
        }};

        entityBody.put(SCHOOL_ID, "1234_id");
        entityBody.put(STUDENT_ID, "2345_id");
        entityBody.put(SCHOOL_YEAR_ATTENDANCE, schoolYearAttendances);

        return entityBody;
    }

    private EntityBody getAPIBodyMultipleSYAs() {
        EntityBody entityBody = getAPIBody();
        @SuppressWarnings("unchecked")
        List<EntityBody> schoolYearAttendances = (List<EntityBody>) entityBody.get(SCHOOL_YEAR_ATTENDANCE);
        EntityBody schoolYearAttendance = schoolYearAttendances.get(0);
        EntityBody newSchoolYearAttendance = new EntityBody(schoolYearAttendance);
        newSchoolYearAttendance.put(SCHOOL_YEAR, "1988-1989");
        newSchoolYearAttendance.put(ATTENDANCE_EVENT, schoolYearAttendance.get(ATTENDANCE_EVENT));
        schoolYearAttendances.add(newSchoolYearAttendance);
        entityBody.put(SCHOOL_YEAR_ATTENDANCE, schoolYearAttendances);

        return entityBody;
    }

    private EntityBody getAPIBodySameSchoolYear() {
        EntityBody entityBody = getAPIBody();
        @SuppressWarnings("unchecked")
        List<EntityBody> schoolYearAttendances = (List<EntityBody>) entityBody.get(SCHOOL_YEAR_ATTENDANCE);
        EntityBody schoolYearAttendance = schoolYearAttendances.get(0);
        EntityBody newSchoolYearAttendance = new EntityBody(schoolYearAttendance);
        newSchoolYearAttendance.put(SCHOOL_YEAR, schoolYearAttendance.get(SCHOOL_YEAR));
        newSchoolYearAttendance.put(ATTENDANCE_EVENT, schoolYearAttendance.get(ATTENDANCE_EVENT));
        schoolYearAttendances.add(newSchoolYearAttendance);
        entityBody.put(SCHOOL_YEAR_ATTENDANCE, schoolYearAttendances);

        return entityBody;
    }

    private EntityBody getMongoBody() {
        EntityBody entityBody = new EntityBody();

        List<EntityBody> attendanceEvents = new ArrayList<EntityBody>() {{
            EntityBody event = new EntityBody() {{
                put(EVENT, "In Attendance");
                put(DATE, "2011-11-11");
            }};
            add(event);
        }};

        entityBody.put(SCHOOL_ID, "1234_id");
        entityBody.put(STUDENT_ID, "2345_id");
        entityBody.put(SCHOOL_YEAR, "2011-2012");
        entityBody.put(ATTENDANCE_EVENT, attendanceEvents);

        return entityBody;
    }
}
