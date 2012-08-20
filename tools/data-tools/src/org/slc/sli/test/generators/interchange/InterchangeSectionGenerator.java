package org.slc.sli.test.generators.interchange;

import java.util.Collection;

import org.slc.sli.test.edfi.entities.meta.SectionMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.edfi.entitiesR1.InterchangeSection;
import org.slc.sli.test.edfi.entitiesR1.Section;
import org.slc.sli.test.generatorsR1.SectionGenerator;
import org.slc.sli.test.utils.InterchangeWriter;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

public class InterchangeSectionGenerator {
	
	
	  /**
     * Sets up a new Section Interchange and populates it
     *
     * @return
     */
    public static void generate(InterchangeWriter<InterchangeSection> iWriter) {

        writeEntitiesToInterchange(iWriter);

    }
    
    /**
     * Generates the individual entities that can generate a Section
     *
     * @param interchangeObjects
     */
    private static void writeEntitiesToInterchange(InterchangeWriter<InterchangeSection> iWriter) {

        generateSections(iWriter, MetaRelations.SECTION_MAP.values());

    }

    /**
     * Loops all sections and, using an Section Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param sectionMetas
     */
    private static void generateSections(InterchangeWriter<InterchangeSection> iWriter,
            Collection<SectionMeta> sectionMetas) {
        long startTime = System.currentTimeMillis();

        for (SectionMeta sectionMeta : sectionMetas) {

            Section section;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                section = null;
            } else {
            	section = SectionGenerator.generateMediumFiSliXsdRI(sectionMeta.id, sectionMeta.schoolId, sectionMeta.courseId,
                        sectionMeta.sessionId);
            	System.out.println("section has UniqueSectionCode ==============>" + section.getUniqueSectionCode());
            }

            iWriter.marshal(section);
        }

        System.out.println("generated " + sectionMetas.size() + " Section objects in: "
                + (System.currentTimeMillis() - startTime));
    }

}
