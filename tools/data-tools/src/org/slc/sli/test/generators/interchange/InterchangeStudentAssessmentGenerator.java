package org.slc.sli.test.generators.interchange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.AssessmentReferenceType;
import org.slc.sli.test.edfi.entities.InterchangeStudentAssessment;
import org.slc.sli.test.edfi.entities.StudentAssessment;
import org.slc.sli.test.edfi.entities.StudentObjectiveAssessment;
import org.slc.sli.test.edfi.entities.StudentReferenceType;
import org.slc.sli.test.edfi.entities.meta.AssessmentMeta;
import org.slc.sli.test.edfi.entities.meta.StudentAssessmentMeta;
import org.slc.sli.test.edfi.entities.meta.StudentMeta;
import org.slc.sli.test.edfi.entities.meta.relations.AssessmentMetaRelations;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.AssessmentGenerator;
import org.slc.sli.test.generators.StudentAssessmentGenerator;
import org.slc.sli.test.generators.StudentGenerator;
import org.slc.sli.test.generators.StudentObjectiveAssessmentGenerator;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

/**
 *
 * @author ldalgado
 *
 */
public class InterchangeStudentAssessmentGenerator {

    public static InterchangeStudentAssessment generate() {
        InterchangeStudentAssessment interchange = new InterchangeStudentAssessment();
        List<Object> interchangeObjects = interchange.getStudentReferenceOrAssessmentReferenceOrStudentAssessment();
        addEntitiesToInterchange(interchangeObjects);
        return interchange;
    }

    private static void addEntitiesToInterchange(List<Object> interchangeObjects) {

        // TODO: do we really want these 2 non-entity reference items?
        // generateStudentReference(interchangeObjects, MetaRelations.STUDENT_MAP.values());

        // generateAssessmentReference(interchangeObjects,
        // AssessmentMetaRelations.ASSESSMENT_MAP.values());

        Collection<StudentAssessment> studentAssessments = generateStudentAssessments(interchangeObjects,
                MetaRelations.STUDENT_ASSES_MAP.values());

        generateStudentObjectiveAssessments(interchangeObjects, studentAssessments);

        // TODO: StudentAssessmentItem (post-alpha)
    }

    private static void generateStudentReference(List<Object> interchangeObjects, Collection<StudentMeta> studentMetas) {
        for (StudentMeta studentMeta : studentMetas) {
            StudentReferenceType studentReference = StudentGenerator.getStudentReferenceType(studentMeta.id);
            interchangeObjects.add(studentReference);
        }
    }

    private static void generateAssessmentReference(List<Object> interchangeObjects,
            Collection<AssessmentMeta> assessmentMetas) {
        for (AssessmentMeta assessmentMeta : assessmentMetas) {
            AssessmentReferenceType assessmentRef = AssessmentGenerator.getAssessmentReference(assessmentMeta.id);
            interchangeObjects.add(assessmentRef);
        }
    }

    private static Collection<StudentAssessment> generateStudentAssessments(List<Object> interchangeObjects,
            Collection<StudentAssessmentMeta> studentAssessmentMetas) {
        long startTime = System.currentTimeMillis();

        Collection<StudentAssessment> studentAssessments = new ArrayList<StudentAssessment>();
        for (StudentAssessmentMeta studentAssessmentMeta : studentAssessmentMetas) {
            StudentAssessment studentAssessment;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                studentAssessment = null;
            } else {
                studentAssessment = StudentAssessmentGenerator.generateLowFi(studentAssessmentMeta);
            }

            studentAssessments.add(studentAssessment);
            interchangeObjects.add(studentAssessment);
        }

        System.out.println("generated " + studentAssessmentMetas.size() + " StudentAssessment objects in: "
                + (System.currentTimeMillis() - startTime));
        return studentAssessments;
    }

    private static void generateStudentObjectiveAssessments(List<Object> interchangeObjects,
            Collection<StudentAssessment> studentAssessmentMetas) {
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
                
                interchangeObjects.add(studentObjectiveAssessment);
                count++;
            }
        }

        System.out.println("generated " + count + " StudentObjectiveAssessment objects in: "
                + (System.currentTimeMillis() - startTime));
    }

}
