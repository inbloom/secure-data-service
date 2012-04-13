package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.ComplexObjectType;
import org.slc.sli.test.edfi.entities.InterchangeEducationOrgCalendar;
import org.slc.sli.test.edfi.entities.Session;
import org.slc.sli.test.edfi.entities.meta.SessionMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
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
                session = SessionGenerator.generateLowFi(sessionMeta.id, sessionMeta.schoolId);
            }

            interchangeObjects.add(session);
        }

        System.out.println("generated " + sessionMetas.size() + " Session objects in: "
                + (System.currentTimeMillis() - startTime));
    }
}
