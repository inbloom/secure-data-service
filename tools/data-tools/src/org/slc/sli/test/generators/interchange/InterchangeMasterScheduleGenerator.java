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

        InterchangeMasterSchedule interchange = new InterchangeMasterSchedule();
        List<ComplexObjectType> interchangeObjects = interchange.getCourseOfferingOrSectionOrBellSchedule();

        generateSections(interchangeObjects, MetaRelations.sectionMap.values());

        return interchange;
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
