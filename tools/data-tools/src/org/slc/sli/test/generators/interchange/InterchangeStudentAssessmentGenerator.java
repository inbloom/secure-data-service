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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import javax.xml.stream.XMLStreamException;

import org.slc.sli.test.edfi.entities.SLCAssessmentItem;
import org.slc.sli.test.edfi.entities.SLCAssessmentItemIdentityType;
import org.slc.sli.test.edfi.entities.SLCAssessmentItemReferenceType;
import org.slc.sli.test.edfi.entities.AssessmentReferenceType;
import org.slc.sli.test.edfi.entities.InterchangeStudentAssessment;
import org.slc.sli.test.edfi.entities.ReferenceType;
import org.slc.sli.test.edfi.entities.SLCStudentAssessment;
import org.slc.sli.test.edfi.entities.SLCStudentAssessmentItem;
import org.slc.sli.test.edfi.entities.SLCStudentObjectiveAssessment;
import org.slc.sli.test.edfi.entities.StudentReferenceType;
import org.slc.sli.test.edfi.entities.meta.AssessmentItemMeta;
import org.slc.sli.test.edfi.entities.meta.AssessmentMeta;
import org.slc.sli.test.edfi.entities.meta.StudentAssessmentMeta;
import org.slc.sli.test.edfi.entities.meta.StudentMeta;
import org.slc.sli.test.edfi.entities.meta.relations.AssessmentMetaRelations;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.AssessmentGenerator;
import org.slc.sli.test.generators.StudentAssessmentGenerator;
import org.slc.sli.test.generators.StudentAssessmentItemGenerator;
import org.slc.sli.test.generators.StudentGenerator;
import org.slc.sli.test.generators.StudentObjectiveAssessmentGenerator;
import org.slc.sli.test.utils.InterchangeWriter;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

/**
 *
 * @author ldalgado
 *
 */
public class InterchangeStudentAssessmentGenerator {

    public static void generate(InterchangeWriter<InterchangeStudentAssessment> writer) throws XMLStreamException {
        writeEntitiesToInterchange(writer);
    }

    private static void writeEntitiesToInterchange(InterchangeWriter<InterchangeStudentAssessment> writer) {

        // TODO: do we really want these 2 non-entity reference items?
        // generateStudentReference(MetaRelations.STUDENT_MAP.values(), writer);

        // generateAssessmentReference(AssessmentMetaRelations.ASSESSMENT_MAP.values(), writer);

        Collection<SLCStudentAssessment> studentAssessments = generateStudentAssessments(
                MetaRelations.STUDENT_ASSES_MAP.values(), writer);

        generateStudentObjectiveAssessments(studentAssessments, writer);
        
        // TODO: StudentAssessmentItem (post-alpha)
        generateStudentAssessmentItems(studentAssessments, writer);

    }

    private static void generateStudentReference(Collection<StudentMeta> studentMetas, 
            InterchangeWriter<InterchangeStudentAssessment> writer) {
        for (StudentMeta studentMeta : studentMetas) {
            StudentReferenceType studentReference = StudentGenerator.getEdFiStudentReferenceType(studentMeta.id);
            writer.marshal(studentReference);
        }
    }

    private static void generateAssessmentReference(Collection<AssessmentMeta> assessmentMetas, 
            InterchangeWriter<InterchangeStudentAssessment> writer) {
        for (AssessmentMeta assessmentMeta : assessmentMetas) {
            AssessmentReferenceType assessmentRef = AssessmentGenerator.getEdFiAssessmentReference(assessmentMeta.id);
            writer.marshal(assessmentRef);
        }
    }

    private static Collection<SLCStudentAssessment> generateStudentAssessments(
            Collection<StudentAssessmentMeta> studentAssessmentMetas, 
            InterchangeWriter<InterchangeStudentAssessment> writer) {
        long startTime = System.currentTimeMillis();

        Collection<SLCStudentAssessment> studentAssessments = new ArrayList<SLCStudentAssessment>();
        for (StudentAssessmentMeta studentAssessmentMeta : studentAssessmentMetas) {
            SLCStudentAssessment studentAssessment;
           
            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                studentAssessment = null;
            } else {
                studentAssessment = StudentAssessmentGenerator.generateLowFi(studentAssessmentMeta);
            }

            // consider nesting creation of StudentObjectiveAssessment here if too much memory is used
            studentAssessments.add(studentAssessment);
            writer.marshal(studentAssessment);
        }

        System.out.println("generated " + studentAssessmentMetas.size() + " StudentAssessment objects in: "
                + (System.currentTimeMillis() - startTime));
        return studentAssessments;
    }

    private static void generateStudentObjectiveAssessments(
            Collection<SLCStudentAssessment> studentAssessmentMetas, 
            InterchangeWriter<InterchangeStudentAssessment> writer) {
        long startTime = System.currentTimeMillis();
        long count = 0;
        Random random = new Random(31);
        
        // Don't generate any student objective assessments if the inverse probability is negative
        if (AssessmentMetaRelations.INV_PROBABILITY_STUDENTASSESSMENT_HAS_OBJECTIVEASSESSMENT >= 0) {

            for (SLCStudentAssessment studentAssessment : studentAssessmentMetas) {
                if ((int) (random.nextDouble() * AssessmentMetaRelations.INV_PROBABILITY_STUDENTASSESSMENT_HAS_OBJECTIVEASSESSMENT) == 0) {
                    SLCStudentObjectiveAssessment studentObjectiveAssessment;
                    
                    if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                        studentObjectiveAssessment = null;
                    } else {
                        studentObjectiveAssessment = StudentObjectiveAssessmentGenerator
                                .generateLowFi(studentAssessment);
                    }
                    
                    writer.marshal(studentObjectiveAssessment);
                    count++;
                }
            }

        }

        System.out.println("generated " + count + " StudentObjectiveAssessment objects in: "
                + (System.currentTimeMillis() - startTime));
    }
    
    private static void generateStudentAssessmentItems(
            Collection<SLCStudentAssessment> studentAssessmentMetas,
            InterchangeWriter<InterchangeStudentAssessment> writer) {

        long startTime = System.currentTimeMillis();
        long count = 0;
       
        
        //if (AssessmentMetaRelations.INV_PROBABILITY_STUDENTASSESSMENT_HAS_STUDENTASSESSMENTITEM > 0) {
            for (SLCStudentAssessment studentAssessmentMeta : studentAssessmentMetas) {
                AssessmentItemMeta random = AssessmentMetaRelations.getRandomAssessmentItemMeta();
                if ( Math.random()* 100  < AssessmentMetaRelations.INV_PROBABILITY_STUDENTASSESSMENT_HAS_STUDENTASSESSMENTITEM) {
                SLCStudentAssessmentItem studentAssessmentItem;
                    if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                        studentAssessmentItem = null;
                    } else {
                       
                        SLCAssessmentItemReferenceType airt = new SLCAssessmentItemReferenceType();
                        SLCAssessmentItemIdentityType aiit = new SLCAssessmentItemIdentityType();                    
                        aiit.setAssessmentItemIdentificationCode(random.id);
                        airt.setAssessmentItemIdentity(aiit);
                        studentAssessmentItem = StudentAssessmentItemGenerator.generateLowFi(
                                studentAssessmentMeta.getId() + "." + count, studentAssessmentMeta.getId(), airt);
                        writer.marshal(studentAssessmentItem);
                        count++;
                    }
                }
            
            }
            
        System.out.println("generated " + count + " StudentAssessmentItem objects in: "
                + (System.currentTimeMillis() - startTime));
    }

}
