package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.ComplexObjectType;
import org.slc.sli.test.edfi.entities.InterchangeMasterSchedule;
import org.slc.sli.test.edfi.entities.Section;
import org.slc.sli.test.edfi.entities.relations.SectionMeta;
import org.slc.sli.test.generators.SectionGenerator;
import org.slc.sli.test.mappingGenerator.MetaRelations;

/**
 * Generates the Master Schedule Interchange as derived from the variable:
 *  - sectionMap
 *  as created by the call to MetaRelations.buildFromSea() in StateEdFiXmlGenerator
 * @author dduran
 *
 */
public class InterchangeMasterScheduleGenerator {

	/**
	 * Sets up a new Master Schedule Interchange and populates it
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
     * @param interchangeObjects
     */
    private static void addEntitiesToInterchange(List<ComplexObjectType> interchangeObjects) {

        generateSections(interchangeObjects, MetaRelations.sectionMap.values());

    }

    /**
     * Loops all sections and, using an Section Generator, populates interchange data.
     * @param interchangeObjects
     * @param seaMetas
     */
    private static void generateSections(List<ComplexObjectType> interchangeObjects,
            Collection<SectionMeta> sectionMetas) {

        for (SectionMeta sectionMeta : sectionMetas) {

            Section section = SectionGenerator.getFastSection(sectionMeta.id, sectionMeta.schoolId,
                    sectionMeta.courseId, sectionMeta.sessionId);

            interchangeObjects.add(section);
        }
    }

}
