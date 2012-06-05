package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.GraduationPlan;
import org.slc.sli.test.edfi.entities.InterchangeStudentEnrollment;
import org.slc.sli.test.edfi.entities.StudentSchoolAssociation;
import org.slc.sli.test.edfi.entities.StudentSectionAssociation;
import org.slc.sli.test.edfi.entities.meta.GraduationPlanMeta;
import org.slc.sli.test.edfi.entities.meta.StudentMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.GraduationPlanGenerator;
import org.slc.sli.test.generators.StudentSchoolAssociationGenerator;
import org.slc.sli.test.generators.StudentSectionAssociationGenerator;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

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

        InterchangeStudentEnrollment interchange = new InterchangeStudentEnrollment();
        List<Object> interchangeObjects = interchange
                .getStudentSchoolAssociationOrStudentSectionAssociationOrGraduationPlan();

        addEntitiesToInterchange(interchangeObjects);

        return interchange;
    }

    
    private static void addEntitiesToInterchange(List<Object> interchangeObjects) {

        generateStudentSchoolAssoc(interchangeObjects, MetaRelations.STUDENT_MAP.values());

        generateStudentSectionAssoc(interchangeObjects, MetaRelations.STUDENT_MAP.values());
        
        generateGraduationPlan(interchangeObjects, MetaRelations.GRADUATION_PLAN_MAP.values());

    }

    /**
     * Generate the individual Graduation Plan entities.
     *
     * @param interchangeObjects
     */
    private static void generateGraduationPlan(List<Object> interchangeObjects, Collection<GraduationPlanMeta> graduationPlanMetas) {
    	
		long startTime = System.currentTimeMillis();

		int objGenCounter = 0;

		for (GraduationPlanMeta graduationPlanMeta : graduationPlanMetas) {

			GraduationPlan graduationPlan;

			if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
				graduationPlan = null;
			} else {
				graduationPlan = GraduationPlanGenerator
						.generateLowFi(graduationPlanMeta.id);
			}

			interchangeObjects.add(graduationPlan);

			objGenCounter++;
		}

		System.out.println("generated " + objGenCounter
				+ " GraduationPlan objects in: "
				+ (System.currentTimeMillis() - startTime));
   }
    
    /**
     * Generate the individual Student Association entities.
     *
     * @param interchangeObjects
     */
    
    private static void generateStudentSchoolAssoc(List<Object> interchangeObjects, Collection<StudentMeta> studentMetas) {
    	
        long startTime = System.currentTimeMillis();

        int objGenCounter = 0;
        for (StudentMeta studentMeta : studentMetas) {
            for (String schoolId : studentMeta.schoolIds) {

                StudentSchoolAssociation studentSchool;

                if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                    studentSchool = null;
                } else {
                    studentSchool = StudentSchoolAssociationGenerator.generateLowFi(studentMeta.id, schoolId);
                }

                interchangeObjects.add(studentSchool);

                objGenCounter++;
            }
        }

        System.out.println("generated " + objGenCounter + " StudentSchoolAssociation objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    /**
     * Generate the individual Student Section entities.
     *
     * @param interchangeObjects
     */
    private static void generateStudentSectionAssoc(List<Object> interchangeObjects,
            Collection<StudentMeta> studentMetas) {
        long startTime = System.currentTimeMillis();

        int objGenCounter = 0;
        for (StudentMeta studentMeta : studentMetas) {
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

                objGenCounter++;
            }
        }

        System.out.println("generated " + objGenCounter + " StudentSectionAssociation objects in: "
                + (System.currentTimeMillis() - startTime));
    }

}
