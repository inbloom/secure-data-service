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

package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import org.slc.sli.test.edfi.entities.SLCCalendarDate;
import org.slc.sli.test.edfi.entities.SLCCalendarDateIdentityType;
import org.slc.sli.test.edfi.entities.SLCCalendarDateReferenceType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.SLCGradingPeriod;
import org.slc.sli.test.edfi.entities.InterchangeEducationOrgCalendar;
import org.slc.sli.test.edfi.entities.SLCSession;
import org.slc.sli.test.edfi.entities.meta.CalendarMeta;
import org.slc.sli.test.edfi.entities.meta.GradingPeriodMeta;
import org.slc.sli.test.edfi.entities.meta.SessionMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.CalendarDateGenerator;
import org.slc.sli.test.generators.GradingPeriodGenerator;
import org.slc.sli.test.generators.SessionGenerator;
import org.slc.sli.test.utils.InterchangeWriter;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

/**
 * Generates the Education Organization Calendar Interchange derived from the
 * variable: - sessionMap as created by the call to MetaRelations.buildFromSea()
 * in StateEdFiXmlGenerator
 * 
 * @author dduran
 * 
 */
public class InterchangeEdOrgCalGenerator {
    public static final int MAX_GRADING_PERIODS = 20;

    private static GradingPeriodGenerator gpg = new GradingPeriodGenerator();

    /**
     * Sets up a new Education Organization Calendar Interchange and populates
     * it
     * 
     * @return
     */
    public static void generate(
            InterchangeWriter<InterchangeEducationOrgCalendar> iWriter) {

        // InterchangeEducationOrgCalendar interchange = new
        // InterchangeEducationOrgCalendar();
        // List<ComplexObjectType> interchangeObjects =
        // interchange.getSessionOrGradingPeriodOrCalendarDate();

        writeEntitiesToInterchange(iWriter);

        // return interchange;
    }

    /**
     * Generates the individual entities that can be Educational Organization
     * Calendars
     * 
     * @param interchangeObjects
     */
    private static void writeEntitiesToInterchange(
            InterchangeWriter<InterchangeEducationOrgCalendar> iWriter) {

        generateSessions(iWriter, MetaRelations.SESSION_MAP.values());

        generateGradingPeriod(iWriter, MetaRelations.GRADINGPERIOD_MAP.values());

        generateCalendar(iWriter, MetaRelations.CALENDAR_MAP.values());

    }

    private static void generateGradingPeriod(
            InterchangeWriter<InterchangeEducationOrgCalendar> iWriter,
            Collection<GradingPeriodMeta> gradingPeriodMetas) {
        long startTime = System.currentTimeMillis();

        int count = 1;
        String prevOrgId = "";
        for (GradingPeriodMeta gradingPeriodMeta : gradingPeriodMetas) {

            SLCGradingPeriod gradingPeriod = null;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                gradingPeriod = null;
            } else {
                
                for (CalendarMeta calendar : gradingPeriodMeta.calendars) {
                    if (count > MAX_GRADING_PERIODS)
                        break;
                    String calendarId = calendar.id;
                    String orgId = calendarId.substring(0,
                            calendarId.lastIndexOf("-"));
                    orgId = orgId.substring(0, orgId.lastIndexOf("-"));
                    if (!prevOrgId.equalsIgnoreCase(orgId))
                        count = 1;
                    prevOrgId = orgId;

                    gradingPeriod = gpg.getGradingPeriod(orgId,
                            gradingPeriodMeta.getGradingPeriodNum());
                    gradingPeriod.setId(gradingPeriodMeta.id);

                    SLCCalendarDateReferenceType calRef = new SLCCalendarDateReferenceType();
                    SLCCalendarDateIdentityType cit = new SLCCalendarDateIdentityType();
                    
                    SLCEducationalOrgReferenceType edOrgRef = new SLCEducationalOrgReferenceType();
                    SLCEducationalOrgIdentityType edOrgId = new SLCEducationalOrgIdentityType();
                    edOrgId.setStateOrganizationId(orgId);
                    edOrgRef.setEducationalOrgIdentity(edOrgId);                    
                    cit.setEducationOrgReference(edOrgRef);
                    
                    cit.setDate(calendar.date);
                    calRef.setCalendarDateIdentity(cit);
                    gradingPeriod.getCalendarDateReference().add(calRef);

                    count++;
                }
            }

            iWriter.marshal(gradingPeriod);

        }

        System.out.println("generated " + gradingPeriodMetas.size()
                + " GradingPeriod objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    private static void generateCalendar(
            InterchangeWriter<InterchangeEducationOrgCalendar> iWriter,
            Collection<CalendarMeta> calendarMetas) {

        long startTime = System.currentTimeMillis();
        for (CalendarMeta calendarMeta : calendarMetas) {

            //this has to be fixed!
            String calendarId = calendarMeta.id;
            String orgId = calendarId.substring(0,
                    calendarId.lastIndexOf("-"));
            orgId = orgId.substring(0, orgId.lastIndexOf("-"));
            SLCEducationalOrgReferenceType edOrgRef = new SLCEducationalOrgReferenceType();
            SLCEducationalOrgIdentityType edOrgId = new SLCEducationalOrgIdentityType();
            edOrgId.setStateOrganizationId(orgId);
            edOrgRef.setEducationalOrgIdentity(edOrgId);
            
            SLCCalendarDate calendar;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                calendar = null;
            } else {
                calendar = new SLCCalendarDate();
                calendar.setDate(calendarMeta.date);
                calendar.setCalendarEvent(CalendarDateGenerator.getCalendarEventType());
                calendar.setEducationOrgReference(edOrgRef);
            }

            iWriter.marshal(calendar);
        }
        System.out.println("generated " + calendarMetas.size()
                + " Calendar objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    /**
     * Loops all sessions and, using an Session Generator, populates interchange
     * data.
     * 
     * @param interchangeObjects
     * @param seaMetas
     */
    private static void generateSessions(
            InterchangeWriter<InterchangeEducationOrgCalendar> iWriter,
            Collection<SessionMeta> sessionMetas) {
        long startTime = System.currentTimeMillis();

        for (SessionMeta sessionMeta : sessionMetas) {

            SLCSession session;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                session = null;
            } else {
                session = SessionGenerator.generateLowFi(sessionMeta.id,
                        sessionMeta.schoolId, sessionMeta.calendarList,
                        sessionMeta.gradingPeriodNumList);
                // session = SessionGenerator.generateLowFi(sessionMeta.id,
                // sessionMeta.schoolId, sessionMeta.calendarList,
                // sessionMeta.gradingPeriodList);
            }

            iWriter.marshal(session);
            // interchangeObjects.add(session);
        }

        System.out.println("generated " + sessionMetas.size()
                + " Session objects in: "
                + (System.currentTimeMillis() - startTime));
    }
}
