package org.slc.sli.test.generators.interchange;

import java.util.Collection;

import org.slc.sli.test.edfi.entities.InterchangeStudent;
import org.slc.sli.test.edfi.entities.Student;
import org.slc.sli.test.edfi.entities.meta.SectionMeta;
import org.slc.sli.test.edfi.entities.meta.StudentMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.edfi.entitiesR1.InterchangeSection;
import org.slc.sli.test.edfi.entitiesR1.Section;
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

        generateSections(iWriter, MetaRelations.SECTION_MAP.values());
        //generateStudents(iWriter, MetaRelations.STUDENT_MAP.values());

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
                        sectionMeta.sessionId, sectionMeta.programId);
            	System.out.println("section has UniqueSectionCode ==============>" + section.getUniqueSectionCode());
            }

            iWriter.marshal(section);
        }

        System.out.println("generated " + sectionMetas.size() + " Section objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    private static void generateStudents(InterchangeWriter<InterchangeStudent> iWriter, Collection<StudentMeta> studentMetas) {
        long startTime = System.currentTimeMillis();

        for (StudentMeta studentMeta : studentMetas) {

            Student student;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                student = null;
            } else {
                student = FastStudentGenerator.generateLowFi(studentMeta.id);
            }


            iWriter.marshal(student);

        }

        System.out.println("generated " + studentMetas.size() + " Student objects in: "
                + (System.currentTimeMillis() - startTime));
    }
    
}
