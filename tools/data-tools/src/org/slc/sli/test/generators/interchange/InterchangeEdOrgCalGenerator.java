package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.ComplexObjectType;
import org.slc.sli.test.edfi.entities.InterchangeEducationOrgCalendar;
import org.slc.sli.test.edfi.entities.Session;
import org.slc.sli.test.edfi.entities.relations.SessionMeta;
import org.slc.sli.test.generators.SessionGenerator;
import org.slc.sli.test.mappingGenerator.MetaRelations;

public class InterchangeEdOrgCalGenerator {

    public static InterchangeEducationOrgCalendar generate() {

        InterchangeEducationOrgCalendar interchange = new InterchangeEducationOrgCalendar();
        List<ComplexObjectType> interchangeObjects = interchange.getSessionOrGradingPeriodOrCalendarDate();

        addEntitiesToInterchange(interchangeObjects);

        return interchange;
    }

    private static void addEntitiesToInterchange(List<ComplexObjectType> interchangeObjects) {

        generateSessions(interchangeObjects, MetaRelations.sessionMap.values());

    }

    private static void generateSessions(List<ComplexObjectType> interchangeObjects,
            Collection<SessionMeta> sessionMetas) {

        for (SessionMeta sessionMeta : sessionMetas) {
            Session session = SessionGenerator.getFastSession(sessionMeta.id, sessionMeta.schoolId);
            interchangeObjects.add(session);
        }

    }
}
