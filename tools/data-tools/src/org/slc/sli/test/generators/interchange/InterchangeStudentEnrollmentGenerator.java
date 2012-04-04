package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.InterchangeStudentEnrollment;
import org.slc.sli.test.edfi.entities.StudentSchoolAssociation;
import org.slc.sli.test.edfi.entities.StudentSectionAssociation;
import org.slc.sli.test.edfi.entities.relations.StudentMeta;
import org.slc.sli.test.generators.StudentSchoolAssociationGenerator;
import org.slc.sli.test.generators.StudentSectionAssociationGenerator;
import org.slc.sli.test.mappingGenerator.MetaRelations;
import org.slc.sli.test.mappingGenerator.StateEdFiXmlGenerator;

/**
 * Generates the Student Enrollment Interchange as derived from the associations
 * determined during the call to MetaRelations.buildFromSea() in StateEdFiXmlGenerator.
 *
 * @author dduran
 *
 */
public class InterchangeStudentEnrollmentGenerator {

    /**
     * Sets up a new Student Enrollment Interchange and populates it.
     *
     * @return
     */
    public static InterchangeStudentEnrollment generate() {
        long startTime = System.currentTimeMillis();

        InterchangeStudentEnrollment interchange = new InterchangeStudentEnrollment();
        List<Object> interchangeObjects = interchange
                .getStudentSchoolAssociationOrStudentSectionAssociationOrGraduationPlan();

        addEntitiesToInterchange(interchangeObjects);

        System.out.println("generated InterchangeStudentEnrollment object in: "
                + (System.currentTimeMillis() - startTime));
        return interchange;
    }

    /**
     * Generate the individual Student Association entities.
     *
     * @param interchangeObjects
     */
    private static void addEntitiesToInterchange(List<Object> interchangeObjects) {

        generateStudentAssocs(interchangeObjects, MetaRelations.STUDENT_MAP.values());

    }

    /**
     * Loops student-school and student-section associations and populates
     * the interchange.
     *
     * @param interchangeObjects
     * @param studentMetas
     */
    private static void generateStudentAssocs(List<Object> interchangeObjects, Collection<StudentMeta> studentMetas) {

        for (StudentMeta studentMeta : studentMetas) {

            generateStudentSchoolAssoc(interchangeObjects, studentMeta);

            generateStudentSectionAssoc(interchangeObjects, studentMeta);
        }

    }

    private static void generateStudentSchoolAssoc(List<Object> interchangeObjects, StudentMeta studentMeta) {

        for (String schoolId : studentMeta.schoolIds) {

            StudentSchoolAssociation studentSchool;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                studentSchool = null;
            } else {
                studentSchool = StudentSchoolAssociationGenerator.generateLowFi(studentMeta.id, schoolId);
            }

            interchangeObjects.add(studentSchool);
        }
    }

    private static void generateStudentSectionAssoc(List<Object> interchangeObjects, StudentMeta studentMeta) {

        for (String sectionId : studentMeta.sectionIds) {

            // TODO: need to take another look at SectionIdentity and constructing it fully
            StudentSectionAssociation studentSection;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                studentSection = null;
            } else {
                studentSection = StudentSectionAssociationGenerator.generateLowFi(studentMeta.id,
                        studentMeta.schoolIds.get(0), sectionId);
            }

            interchangeObjects.add(studentSection);
        }
    }

}
