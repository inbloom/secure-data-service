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

package org.slc.sli.api.config;

import org.junit.Before;
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
        EntityBody entityBody = treatment.toStored(getAPIBody(), definition);
        assertTrue(entityBody.containsKey(ATTENDANCE_EVENT));
        assertTrue(entityBody.containsKey(SCHOOL_YEAR));
        assertFalse(entityBody.containsKey(SCHOOL_YEAR_ATTENDANCE));
        assertTrue(entityBody.containsKey(SCHOOL_ID));
        assertTrue(entityBody.containsKey(STUDENT_ID));
        assertEquals(entityBody.get(SCHOOL_ID), "1234_id");
        assertEquals(entityBody.get(STUDENT_ID), "2345_id");
        assertEquals(entityBody.get(SCHOOL_YEAR), "1987-1988");

        List<EntityBody> attendanceEvents = (List<EntityBody>) entityBody.get(ATTENDANCE_EVENT);
        assertEquals(attendanceEvents.size(), 2);
        for (EntityBody attendanceEvent : attendanceEvents) {
            assertTrue(attendanceEvent.containsKey(EVENT));
            assertTrue(attendanceEvent.containsKey(DATE));
            if (attendanceEvent.get(DATE).equals("1988-01-01")) {
                assertEquals(attendanceEvent.get(EVENT), "Tardy");
            } else if (attendanceEvent.get(DATE).equals("1988-01-02")) {
                assertEquals(attendanceEvent.get(EVENT), "Unexcused Absence");
            } else {
                fail(String.format("'%s' does not match any of the expected dates.", attendanceEvent.get(DATE)));
            }
        }
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
        assertEquals(entityBody.get(SCHOOL_ID), "1234_id");
        assertEquals(entityBody.get(STUDENT_ID), "2345_id");

        List<EntityBody> schoolYearAttendances = (List<EntityBody>) entityBody.get(SCHOOL_YEAR_ATTENDANCE);
        assertEquals(schoolYearAttendances.size(), 1);

        EntityBody schoolYearAttendance = schoolYearAttendances.get(0);
        assertTrue(schoolYearAttendance.containsKey(SCHOOL_YEAR));
        assertTrue(schoolYearAttendance.containsKey(ATTENDANCE_EVENT));
        assertEquals(schoolYearAttendance.get(SCHOOL_YEAR), "2011-2012");

        List<EntityBody> attendanceEvent = (List<EntityBody>) schoolYearAttendance.get(ATTENDANCE_EVENT);
        assertEquals(attendanceEvent.size(), 1);
        assertTrue(attendanceEvent.get(0).containsKey(EVENT));
        assertTrue(attendanceEvent.get(0).containsKey(DATE));
        assertEquals(attendanceEvent.get(0).get(EVENT), "In Attendance");
        assertEquals(attendanceEvent.get(0).get(DATE), "2011-11-11");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEmptyAttendanceEvent() {
        EntityBody mongoBody = getMongoBody();
        mongoBody.remove(ATTENDANCE_EVENT);
        EntityBody entityBody = treatment.toExposed(mongoBody, definition, null);
        List<EntityBody> schoolYearAttendances = (List<EntityBody>) entityBody.get(SCHOOL_YEAR_ATTENDANCE);
        List<EntityBody> attendanceEvents = (List<EntityBody>) schoolYearAttendances.get(0).get(ATTENDANCE_EVENT);
        assertNotNull(attendanceEvents);
        assertEquals(attendanceEvents.size(), 0);
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
