package org.slc.sli.test.generators.interchange;

import java.util.Collection;

import org.slc.sli.test.edfi.entities.InterchangeStudent;
import org.slc.sli.test.edfi.entities.Student;
import org.slc.sli.test.edfi.entities.meta.SectionMeta;
import org.slc.sli.test.edfi.entities.meta.StudentMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.edfi.entitiesR1.InterchangeSection;
import org.slc.sli.test.edfi.entitiesR1.Section;
import org.slc.sli.test.edfi.entitiesR1.meta.SuperSectionMeta;
import org.slc.sli.test.generators.FastStudentGenerator;
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

        generateSections(iWriter, MetaRelations.SUPERSECTION_MAP.values());
   
    }

    /**
     * Loops all sections and, using an Section Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param sectionMetas
     */
    private static void generateSections(InterchangeWriter<InterchangeSection> iWriter,
            Collection<SuperSectionMeta> SuperSectionMetas) {
        long startTime = System.currentTimeMillis();
      
        for (SuperSectionMeta superSectionMetas : SuperSectionMetas) {

            Section section;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                section = null;
            } else {
                
                section = SectionGenerator.generateMediumFiSliXsdRI(superSectionMetas.id, superSectionMetas.schoolId, superSectionMetas.courseOffering.courseMeta.id,
                        superSectionMetas.sessionId, superSectionMetas.programId, superSectionMetas.studentIds, superSectionMetas.teacherIds);
                 
                
            }
            iWriter.marshal(section);
           
        }

        System.out.println("generated " + SuperSectionMetas.size() + " Section objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    
}
