/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.slc.sli.test.edfi.entities.SLCGraduationPlan;
import org.slc.sli.test.edfi.entities.InterchangeStudentEnrollment;
import org.slc.sli.test.edfi.entities.LearningStandard;
import org.slc.sli.test.edfi.entities.SLCStudentSchoolAssociation;
import org.slc.sli.test.edfi.entities.SLCStudentSectionAssociation;
import org.slc.sli.test.edfi.entities.meta.GraduationPlanMeta;
import org.slc.sli.test.edfi.entities.meta.StudentMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.GraduationPlanGenerator;
import org.slc.sli.test.generators.StudentSchoolAssociationGenerator;
import org.slc.sli.test.generators.StudentSectionAssociationGenerator;
import org.slc.sli.test.utils.InterchangeWriter;
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
    public static void generate(InterchangeWriter<InterchangeStudentEnrollment> iWriter) {

//        InterchangeStudentEnrollment interchange = new InterchangeStudentEnrollment();
//        List<Object> interchangeObjects = interchange
//                .getStudentSchoolAssociationOrStudentSectionAssociationOrGraduationPlan();

        writeEntitiesToInterchange(iWriter);

//        return interchange;
    }


    private static void writeEntitiesToInterchange(InterchangeWriter<InterchangeStudentEnrollment> iWriter) {

        generateStudentSchoolAssoc(iWriter, MetaRelations.STUDENT_MAP.values());

        generateStudentSectionAssoc(iWriter, MetaRelations.STUDENT_MAP.values());

		generateGraduationPlan(iWriter,
				MetaRelations.GRADUATION_PLAN_MAP.values());


    }

    /**
     * Generate the individual Graduation Plan entities.
     *
     * @param interchangeObjects
     */
    private static void generateGraduationPlan(InterchangeWriter<InterchangeStudentEnrollment> iWriter, Collection<GraduationPlanMeta> graduationPlanMetas) {

		long startTime = System.currentTimeMillis();

		int objGenCounter = 0;

		for (GraduationPlanMeta graduationPlanMeta : graduationPlanMetas) {

			SLCGraduationPlan graduationPlan;

			if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
				graduationPlan = null;
			} else {
				graduationPlan = GraduationPlanGenerator
						.generateLowFi(graduationPlanMeta.id, graduationPlanMeta.schoolId);
			}


	        if (graduationPlan != null) {
	            iWriter.marshal(graduationPlan);
	            objGenCounter++;
	        }
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

    private static void generateStudentSchoolAssoc(InterchangeWriter<InterchangeStudentEnrollment> iWriter, Collection<StudentMeta> studentMetas) {

        long startTime = System.currentTimeMillis();

        int objGenCounter = 0;
        for (StudentMeta studentMeta : studentMetas) {
            for (String schoolId : studentMeta.schoolIds) {

                SLCStudentSchoolAssociation studentSchool;

                if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                    studentSchool = null;
                } else {
                    studentSchool = StudentSchoolAssociationGenerator.generateLowFi(studentMeta.id, schoolId);
                }



   	         iWriter.marshal(studentSchool);
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
    private static void generateStudentSectionAssoc(InterchangeWriter<InterchangeStudentEnrollment> iWriter,
            Collection<StudentMeta> studentMetas) {
        long startTime = System.currentTimeMillis();

        int objGenCounter = 0;
        for (StudentMeta studentMeta : studentMetas) {
            for (String sectionId : studentMeta.sectionIds) {

                // TODO: need to take another look at SectionIdentity and constructing it fully
                SLCStudentSectionAssociation studentSection;

                if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                    studentSection = null;
                } else {
                    studentSection = StudentSectionAssociationGenerator.generateLowFi(studentMeta.id,
                            studentMeta.schoolIds.get(0), sectionId);
                }



      	        iWriter.marshal(studentSection);

                objGenCounter++;
            }
        }

        System.out.println("generated " + objGenCounter + " StudentSectionAssociation objects in: "
                + (System.currentTimeMillis() - startTime));
    }

}
