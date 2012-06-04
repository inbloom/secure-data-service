package org.slc.sli.test.generators.interchange;

import java.util.ArrayList;
import java.util.Collection;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slc.sli.test.edfi.entities.AssessmentItemIdentityType;
import org.slc.sli.test.edfi.entities.AssessmentItemReferenceType;
import org.slc.sli.test.edfi.entities.AssessmentReferenceType;
import org.slc.sli.test.edfi.entities.StudentAssessment;
import org.slc.sli.test.edfi.entities.StudentAssessmentItem;
import org.slc.sli.test.edfi.entities.StudentObjectiveAssessment;
import org.slc.sli.test.edfi.entities.StudentReferenceType;
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
import org.slc.sli.test.utils.JaxbUtils;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

/**
 *
 * @author ldalgado
 *
 */
public class InterchangeStudentAssessmentGenerator {

    public static void generate(XMLStreamWriter writer) throws XMLStreamException {
        // Manually create the interchange level tags
        writer.writeStartElement("InterchangeStudentAssessment");
        writer.writeNamespace(null, "http://ed-fi.org/0100");
        
        writeEntitiesToInterchange(writer);

        writer.writeEndElement();
    }

    private static void writeEntitiesToInterchange(XMLStreamWriter writer) {

        // TODO: do we really want these 2 non-entity reference items?
        // generateStudentReference(interchangeObjects, MetaRelations.STUDENT_MAP.values());

        // generateAssessmentReference(interchangeObjects,
        // AssessmentMetaRelations.ASSESSMENT_MAP.values());

        Collection<StudentAssessment> studentAssessments = generateStudentAssessments(
                MetaRelations.STUDENT_ASSES_MAP.values(), writer);

        generateStudentObjectiveAssessments(studentAssessments, writer);
        
        // TODO: StudentAssessmentItem (post-alpha)
        generateStudentAssessmentItems(studentAssessments, writer);

    }

    private static void generateStudentReference(Collection<StudentMeta> studentMetas, XMLStreamWriter writer) {
        for (StudentMeta studentMeta : studentMetas) {
            StudentReferenceType studentReference = StudentGenerator.getStudentReferenceType(studentMeta.id);
            JaxbUtils.marshal(studentReference, writer);
        }
    }

    private static void generateAssessmentReference(Collection<AssessmentMeta> assessmentMetas, 
            XMLStreamWriter writer) {
        for (AssessmentMeta assessmentMeta : assessmentMetas) {
            AssessmentReferenceType assessmentRef = AssessmentGenerator.getAssessmentReference(assessmentMeta.id);
            JaxbUtils.marshal(assessmentRef, writer);
        }
    }

    private static Collection<StudentAssessment> generateStudentAssessments(
            Collection<StudentAssessmentMeta> studentAssessmentMetas, XMLStreamWriter writer) {
        long startTime = System.currentTimeMillis();

        Collection<StudentAssessment> studentAssessments = new ArrayList<StudentAssessment>();
        for (StudentAssessmentMeta studentAssessmentMeta : studentAssessmentMetas) {
            StudentAssessment studentAssessment;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                studentAssessment = null;
            } else {
                studentAssessment = StudentAssessmentGenerator.generateLowFi(studentAssessmentMeta);
            }

            // consider nesting creation of StudentObjectiveAssessment here if too much memory is used
            studentAssessments.add(studentAssessment);
            JaxbUtils.marshal(studentAssessment, writer);
        }

        System.out.println("generated " + studentAssessmentMetas.size() + " StudentAssessment objects in: "
                + (System.currentTimeMillis() - startTime));
        return studentAssessments;
    }

    private static void generateStudentObjectiveAssessments(
            Collection<StudentAssessment> studentAssessmentMetas, XMLStreamWriter writer) {
        long startTime = System.currentTimeMillis();
        long count = 0;
        
        for (StudentAssessment studentAssessment : studentAssessmentMetas) {
            if ((int) (Math.random() * AssessmentMetaRelations.INV_PROBABILITY_STUDENTASSESSMENT_HAS_OBJECTIVEASSESSMENT) == 0) {
                StudentObjectiveAssessment studentObjectiveAssessment;
                
                if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                    studentObjectiveAssessment = null;
                } else {
                    studentObjectiveAssessment = StudentObjectiveAssessmentGenerator.generateLowFi(studentAssessment);
                }
                
                JaxbUtils.marshal(studentObjectiveAssessment, writer);
                count++;
            }
        }

        System.out.println("generated " + count + " StudentObjectiveAssessment objects in: "
                + (System.currentTimeMillis() - startTime));
    }
    
    private static void generateStudentAssessmentItems(
            Collection<StudentAssessment> studentAssessmentMetas, XMLStreamWriter writer) {

        long startTime = System.currentTimeMillis();
        long count = 0;

        for (StudentAssessment studentAssessmentMeta : studentAssessmentMetas) {
            StudentAssessmentItem studentAssessmentItem;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                studentAssessmentItem = null;
            } else {
                AssessmentItemReferenceType airt = new AssessmentItemReferenceType();
                AssessmentItemIdentityType aiit = new AssessmentItemIdentityType();
                aiit.setAssessmentItemIdentificationCode("AssessmentItemReference");
                airt.setAssessmentItemIdentity(aiit);
                studentAssessmentItem = StudentAssessmentItemGenerator.generateLowFi(studentAssessmentMeta.getId()+".", airt);
            }

            JaxbUtils.marshal(studentAssessmentItem, writer);
            count++;
        }

        System.out.println("generated " + count + " StudentAssessmentItem objects in: "
                + (System.currentTimeMillis() - startTime));
    }

}
