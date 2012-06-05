package org.slc.sli.test.generators.interchange;

import java.util.Calendar;
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

        InterchangeEducationOrgCalendar interchange = new InterchangeEducationOrgCalendar();
        List<ComplexObjectType> interchangeObjects = interchange.getSessionOrGradingPeriodOrCalendarDate();

        addEntitiesToInterchange(interchangeObjects);

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
    
    
	private static void generateGradingPeriod(
			List<ComplexObjectType> interchangeObjects,
			Collection<GradingPeriodMeta> gradingPeriodMetas) {
		long startTime = System.currentTimeMillis();
		
		for (GradingPeriodMeta gradingPeriodMeta : gradingPeriodMetas) {

			GradingPeriod gradingPeriod = null;

			if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
				gradingPeriod = null;
			} else {
				// calendar = CalendarGenerator.generateLowFi(calendarMeta.id);
				//gradingPeriod = gpg.getGradingPeriod();
				
				for (String calendarId : gradingPeriodMeta.calendars) {
                    String orgId = calendarId.substring(0, calendarId.lastIndexOf("-"));
                    orgId = orgId.substring(0, orgId.lastIndexOf("-"));
				    gradingPeriod = gpg.getGradingPeriod(orgId);
				    gradingPeriod.setId(gradingPeriodMeta.id);
					ReferenceType calRef = new ReferenceType();
					calRef.setRef(new Ref(calendarId));
					gradingPeriod.getCalendarDateReference().add(calRef);
				}
			}

			interchangeObjects.add(gradingPeriod);
		}

		System.out.println("generated " + gradingPeriodMetas.size()
				+ " GradingPeriod objects in: "
				+ (System.currentTimeMillis() - startTime));
	}

	private static void generateCalendar(List<ComplexObjectType> interchangeObjects, Collection<CalendarMeta> calendarMetas) {
    	
    	 long startTime = System.currentTimeMillis();
         for (CalendarMeta calendarMeta : calendarMetas) {

        	 CalendarDate calendar;

             if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
            	 calendar = null;
             } else {
            	 calendar = CalendarDateGenerator.getCalendarDate(calendarMeta.id);
             }

             interchangeObjects.add(calendar);
         }

         System.out.println("generated " + calendarMetas.size() + " Calendar objects in: "
                 + (System.currentTimeMillis() - startTime));
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
                session = null;
            } else {
                session = SessionGenerator.generateLowFi(sessionMeta.id, sessionMeta.schoolId, sessionMeta.calendarList);
            	//session = SessionGenerator.generateLowFi(sessionMeta.id, sessionMeta.schoolId, sessionMeta.calendarList, sessionMeta.gradingPeriodList);
            }

            interchangeObjects.add(session);
        }

        System.out.println("generated " + sessionMetas.size() + " Session objects in: "
                + (System.currentTimeMillis() - startTime));
    }
}
