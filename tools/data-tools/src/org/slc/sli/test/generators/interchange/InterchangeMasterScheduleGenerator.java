package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.ComplexObjectType;
import org.slc.sli.test.edfi.entities.InterchangeMasterSchedule;
import org.slc.sli.test.edfi.entities.Section;
import org.slc.sli.test.edfi.entities.meta.SectionMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.SectionGenerator;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

/**
 * Generates the Master Schedule Interchange as derived from the variable:
 * - sectionMap
 * as created by the call to MetaRelations.buildFromSea() in StateEdFiXmlGenerator
 *
 * @author dduran
 *
 */
public class InterchangeMasterScheduleGenerator {

    /**
     * Sets up a new Master Schedule Interchange and populates it
     *
     * @return
     */
    public static InterchangeMasterSchedule generate() {

        InterchangeMasterSchedule interchange = new InterchangeMasterSchedule();
        List<ComplexObjectType> interchangeObjects = interchange.getCourseOfferingOrSectionOrBellSchedule();

        addEntitiesToInterchange(interchangeObjects);

        return interchange;
    }

    /**
     * Generates the individual entities that can generate a Master Schedule
     *
     * @param interchangeObjects
     */
    private static void addEntitiesToInterchange(List<ComplexObjectType> interchangeObjects) {

        generateSections(interchangeObjects, MetaRelations.SECTION_MAP.values());

    }

    /**
     * Loops all sections and, using an Section Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param sectionMetas
     */
    private static void generateSections(List<ComplexObjectType> interchangeObjects,
            Collection<SectionMeta> sectionMetas) {
        long startTime = System.currentTimeMillis();

        for (SectionMeta sectionMeta : sectionMetas) {

            Section section;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                section = null;
            } else {
                section = SectionGenerator.generateLowFi(sectionMeta.id, sectionMeta.schoolId, sectionMeta.courseId,
                        sectionMeta.sessionId);
            }

            interchangeObjects.add(section);
        }

        System.out.println("generated " + sectionMetas.size() + " Section objects in: "
                + (System.currentTimeMillis() - startTime));
    }

}
