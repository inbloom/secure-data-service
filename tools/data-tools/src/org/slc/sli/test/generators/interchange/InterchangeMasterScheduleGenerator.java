package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.ComplexObjectType;
import org.slc.sli.test.edfi.entities.InterchangeMasterSchedule;
import org.slc.sli.test.edfi.entities.Section;
import org.slc.sli.test.edfi.entities.relations.SectionMeta;
import org.slc.sli.test.generators.SectionGenerator;
import org.slc.sli.test.mappingGenerator.MetaRelations;

public class InterchangeMasterScheduleGenerator {

    public static InterchangeMasterSchedule generate() {
        long startTime = System.currentTimeMillis();

        InterchangeMasterSchedule interchange = new InterchangeMasterSchedule();
        List<ComplexObjectType> interchangeObjects = interchange.getCourseOfferingOrSectionOrBellSchedule();

        addEntitiesToInterchange(interchangeObjects);

        System.out.println("generated InterchangeMasterSchedule object in: "
                + (System.currentTimeMillis() - startTime));
        return interchange;
    }

    private static void addEntitiesToInterchange(List<ComplexObjectType> interchangeObjects) {

        generateSections(interchangeObjects, MetaRelations.sectionMap.values());

    }

    private static void generateSections(List<ComplexObjectType> interchangeObjects,
            Collection<SectionMeta> sectionMetas) {

        for (SectionMeta sectionMeta : sectionMetas) {

            Section section = SectionGenerator.getFastSection(sectionMeta.id, sectionMeta.schoolId,
                    sectionMeta.courseId, sectionMeta.sessionId);

            interchangeObjects.add(section);
        }
    }

}
