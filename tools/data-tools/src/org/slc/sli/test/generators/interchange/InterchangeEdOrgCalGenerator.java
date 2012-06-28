package org.slc.sli.test.generators.interchange;

import static org.slc.sli.test.utils.InterchangeStatisticsWriterUtils.writeInterchangeEntityStatistic;
import static org.slc.sli.test.utils.InterchangeStatisticsWriterUtils.writeInterchangeStatisticEnd;
import static org.slc.sli.test.utils.InterchangeStatisticsWriterUtils.writeInterchangeStatisticStart;
import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.CalendarDate;
import org.slc.sli.test.edfi.entities.ComplexObjectType;
import org.slc.sli.test.edfi.entities.GradingPeriod;
import org.slc.sli.test.edfi.entities.InterchangeEducationOrgCalendar;
import org.slc.sli.test.edfi.entities.Ref;
import org.slc.sli.test.edfi.entities.ReferenceType;
import org.slc.sli.test.edfi.entities.Session;
import org.slc.sli.test.edfi.entities.meta.CalendarMeta;
import org.slc.sli.test.edfi.entities.meta.GradingPeriodMeta;
import org.slc.sli.test.edfi.entities.meta.SessionMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.CalendarDateGenerator;
import org.slc.sli.test.generators.GradingPeriodGenerator;
import org.slc.sli.test.generators.SessionGenerator;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

/**
 * Generates the Education Organization Calendar Interchange derived from the variable:
 * - sessionMap
 * as created by the call to MetaRelations.buildFromSea() in StateEdFiXmlGenerator
 *
 * @author dduran
 *
 */
public class InterchangeEdOrgCalGenerator {

    private static GradingPeriodGenerator gpg = new GradingPeriodGenerator();

    /**
     * Sets up a new Education Organization Calendar Interchange and populates it
     *
     * @return
     */
    public static InterchangeEducationOrgCalendar generate() {
        long startTime = System.currentTimeMillis();

        InterchangeEducationOrgCalendar interchange = new InterchangeEducationOrgCalendar();
        List<ComplexObjectType> interchangeObjects = interchange.getSessionOrGradingPeriodOrCalendarDate();

        writeInterchangeStatisticStart(interchange.getClass().getSimpleName());

        addEntitiesToInterchange(interchangeObjects);

        writeInterchangeStatisticEnd(interchangeObjects.size(), System.currentTimeMillis() - startTime);
        return interchange;
    }

    /**
     * Generates the individual entities that can be Educational Organization Calendars
     *
     * @param interchangeObjects
     */
    private static void addEntitiesToInterchange(List<ComplexObjectType> interchangeObjects) {

        generateSessions(interchangeObjects, MetaRelations.SESSION_MAP.values());

        generateGradingPeriod(interchangeObjects, MetaRelations.GRADINGPERIOD_MAP.values());

        generateCalendar(interchangeObjects, MetaRelations.CALENDAR_MAP.values());

    }

    private static void generateGradingPeriod(List<ComplexObjectType> interchangeObjects,
            Collection<GradingPeriodMeta> gradingPeriodMetas) {
        long startTime = System.currentTimeMillis();

        int count = 1;
        String prevOrgId = "";
        for (GradingPeriodMeta gradingPeriodMeta : gradingPeriodMetas) {

            GradingPeriod gradingPeriod = null;

            // calendar = CalendarGenerator.generateLowFi(calendarMeta.id);
            // gradingPeriod = gpg.getGradingPeriod();
            for (String calendarId : gradingPeriodMeta.calendars) {
                String orgId = calendarId.substring(0, calendarId.lastIndexOf("-"));
                orgId = orgId.substring(0, orgId.lastIndexOf("-"));
                if (!prevOrgId.equalsIgnoreCase(orgId))
                    count = 1;
                prevOrgId = orgId;

                if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                    // TODO update this to use its own generator if medFi requirements change
                    gradingPeriod = gpg.getGradingPeriod(orgId, count);
                } else {
                    gradingPeriod = gpg.getGradingPeriod(orgId, count);
                }

                gradingPeriod.setId(gradingPeriodMeta.id);
                ReferenceType calRef = new ReferenceType();
                calRef.setRef(new Ref(calendarId));
                gradingPeriod.getCalendarDateReference().add(calRef);
                count++;
            }

            interchangeObjects.add(gradingPeriod);
        }

        writeInterchangeEntityStatistic("GradingPeriod", gradingPeriodMetas.size(), 
                System.currentTimeMillis() - startTime);
	}

    private static void generateCalendar(List<ComplexObjectType> interchangeObjects,
            Collection<CalendarMeta> calendarMetas) {

        long startTime = System.currentTimeMillis();
        for (CalendarMeta calendarMeta : calendarMetas) {

            CalendarDate calendar;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                calendar = CalendarDateGenerator.generateMidFi(calendarMeta.id);
            } else {
                calendar = CalendarDateGenerator.generateLowFi(calendarMeta.id);
            }

            interchangeObjects.add(calendar);
        }

         writeInterchangeEntityStatistic("CalendarDate", calendarMetas.size(), 
                 System.currentTimeMillis() - startTime);
     }

    /**
     * Loops all sessions and, using an Session Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param seaMetas
     */
    private static void generateSessions(List<ComplexObjectType> interchangeObjects,
            Collection<SessionMeta> sessionMetas) {
        long startTime = System.currentTimeMillis();

        for (SessionMeta sessionMeta : sessionMetas) {

            Session session;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                session = SessionGenerator.generateMidFi(sessionMeta.id, sessionMeta.schoolId,
                        sessionMeta.calendarList, sessionMeta.gradingPeriodList);
            } else {
                session = SessionGenerator.generateLowFi(sessionMeta.id, sessionMeta.schoolId,
                        sessionMeta.calendarList, sessionMeta.gradingPeriodList);
            }

            interchangeObjects.add(session);
        }

        writeInterchangeEntityStatistic("Session", sessionMetas.size(), 
                System.currentTimeMillis() - startTime);
    }
}
