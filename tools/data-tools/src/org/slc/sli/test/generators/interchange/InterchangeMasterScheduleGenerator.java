package org.slc.sli.test.generators.interchange;

import static org.slc.sli.test.utils.InterchangeWriter.REPORT_INDENTATION;

import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.ComplexObjectType;
import org.slc.sli.test.edfi.entities.CourseOffering;
import org.slc.sli.test.edfi.entities.InterchangeMasterSchedule;
import org.slc.sli.test.edfi.entities.Section;
import org.slc.sli.test.edfi.entities.meta.CourseOfferingMeta;
import org.slc.sli.test.edfi.entities.meta.SectionMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.CourseOfferingGenerator;
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
        long startTime = System.currentTimeMillis();

        InterchangeMasterSchedule interchange = new InterchangeMasterSchedule();
        System.out.println(interchange.getClass().getSimpleName() + ": started");
        List<ComplexObjectType> interchangeObjects = interchange.getCourseOfferingOrSectionOrBellSchedule();

        addEntitiesToInterchange(interchangeObjects);

        System.out.println(interchange.getClass().getSimpleName() + ": generated " + interchangeObjects.size() + 
                " entries in " + (System.currentTimeMillis() - startTime) + "\n");
        return interchange;
    }

    /**
     * Generates the individual entities that can generate a Master Schedule
     *
     * @param interchangeObjects
     */
    private static void addEntitiesToInterchange(List<ComplexObjectType> interchangeObjects) {

        generateCourseOffering(interchangeObjects, MetaRelations.COURSEOFFERING_MAP.values());
        generateSections(interchangeObjects, MetaRelations.SECTION_MAP.values());

    }

    private static void generateCourseOffering(List<ComplexObjectType> interchangeObjects,
            Collection<CourseOfferingMeta> courseOfferingMetas) {
        long startTime = System.currentTimeMillis();

        for (CourseOfferingMeta courseOfferingMeta : courseOfferingMetas) {
            CourseOffering courseOffering;
            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                courseOffering = CourseOfferingGenerator.generate(courseOfferingMeta);
            } else {
                courseOffering = CourseOfferingGenerator.generateLowFi(courseOfferingMeta);
            }
            interchangeObjects.add(courseOffering);
        }
        System.out.println(REPORT_INDENTATION + "generated " + courseOfferingMetas.size() + " CourseOfferings in: "
                + (System.currentTimeMillis() - startTime));
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
                section = SectionGenerator.generateMediumFi(sectionMeta.id, sectionMeta.schoolId, sectionMeta.courseId,
                        sectionMeta.sessionId);
            } else {
            	section = SectionGenerator.generateLowFi(sectionMeta.id, sectionMeta.schoolId, sectionMeta.courseId,
                        sectionMeta.sessionId);
            }

            interchangeObjects.add(section);
        }

        System.out.println(REPORT_INDENTATION + "generated " + sectionMetas.size() + " Section objects in: "
                + (System.currentTimeMillis() - startTime));
    }

}
