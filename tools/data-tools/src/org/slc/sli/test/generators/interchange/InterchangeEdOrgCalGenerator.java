package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.ComplexObjectType;
import org.slc.sli.test.edfi.entities.InterchangeEducationOrgCalendar;
import org.slc.sli.test.edfi.entities.Session;
import org.slc.sli.test.edfi.entities.relations.SessionMeta;
import org.slc.sli.test.generators.SessionGenerator;
import org.slc.sli.test.mappingGenerator.MetaRelations;

/**
 * Generates the Education Organization Calendar Interchange derived from the variable:
 *  - sessionMap
 *  as created by the call to MetaRelations.buildFromSea() in StateEdFiXmlGenerator
 * @author dduran
 *
 */
public class InterchangeEdOrgCalGenerator {

	/**
	 * Sets up a new Education Organization Calendar Interchange and populates it
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
     * @param interchangeObjects
     */
    private static void addEntitiesToInterchange(List<ComplexObjectType> interchangeObjects) {

        generateSessions(interchangeObjects, MetaRelations.sessionMap.values());

    }

    /**
     * Loops all sessions and, using an Session Generator, populates interchange data.
     * @param interchangeObjects
     * @param seaMetas
     */
    private static void generateSessions(List<ComplexObjectType> interchangeObjects,
            Collection<SessionMeta> sessionMetas) {

        for (SessionMeta sessionMeta : sessionMetas) {
            Session session = SessionGenerator.getFastSession(sessionMeta.id, sessionMeta.schoolId);
            interchangeObjects.add(session);
        }

    }
}
