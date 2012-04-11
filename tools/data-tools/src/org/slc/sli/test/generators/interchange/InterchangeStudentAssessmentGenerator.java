package org.slc.sli.test.generators.interchange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.AssessmentItemReferenceType;
import org.slc.sli.test.edfi.entities.AssessmentReferenceType;
import org.slc.sli.test.edfi.entities.InterchangeStudentAssessment;
import org.slc.sli.test.edfi.entities.ObjectiveAssessmentReferenceType;
import org.slc.sli.test.edfi.entities.ReferenceType;
import org.slc.sli.test.edfi.entities.StudentAssessment;
import org.slc.sli.test.edfi.entities.StudentAssessmentItem;
import org.slc.sli.test.edfi.entities.StudentObjectiveAssessment;
import org.slc.sli.test.edfi.entities.StudentReferenceType;
import org.slc.sli.test.edfi.entities.relations.AssessmentItemMeta;
import org.slc.sli.test.edfi.entities.relations.AssessmentMeta;
import org.slc.sli.test.edfi.entities.relations.ObjectiveAssessmentMeta;
import org.slc.sli.test.edfi.entities.relations.StudentMeta;
import org.slc.sli.test.generators.AssessmentGenerator;
import org.slc.sli.test.generators.AssessmentItemGenerator;
import org.slc.sli.test.generators.ObjectiveAssessmentGenerator;
import org.slc.sli.test.generators.StudentAssessmentGenerator;
import org.slc.sli.test.generators.StudentAssessmentItemGenerator;
import org.slc.sli.test.generators.StudentGenerator;
import org.slc.sli.test.generators.StudentObjectiveAssessmentGenerator;
import org.slc.sli.test.mappingGenerator.MetaRelations;

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

        generateStudentReference(interchangeObjects, MetaRelations.STUDENT_MAP.values());

        generateAssessmentReference(interchangeObjects, MetaRelations.ASSESSMENT_MAP.values());

        generateStudentAssessmentParts(interchangeObjects, MetaRelations.STUDENT_MAP.values(),
                MetaRelations.ASSESSMENT_MAP.values(), MetaRelations.OBJECTIVE_ASSESSMENT_MAP.values(),
                MetaRelations.ASSESSMENT_ITEM_MAP.values());
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
            String assessmentTitle = null;
            AssessmentReferenceType assessmentRef = AssessmentGenerator.getAssessmentReference(assessmentTitle,
                    assessmentMeta.id);
            interchangeObjects.add(assessmentRef);
        }
    }

    private static void generateStudentAssessmentParts(List<Object> interchangeObjects,
            Collection<StudentMeta> studentMetas, Collection<AssessmentMeta> assessmentMetas,
            Collection<ObjectiveAssessmentMeta> objectiveAssessmentMetas,
            Collection<AssessmentItemMeta> assessmentItemMetas) {

        List<StudentAssessment> studentAssessments = new ArrayList<StudentAssessment>();
        List<StudentObjectiveAssessment> studentObjectiveAssessments = new ArrayList<StudentObjectiveAssessment>();
        List<StudentAssessmentItem> studentAssessmentItems = new ArrayList<StudentAssessmentItem>();

        boolean optional = true;
        StudentAssessmentGenerator studentAssessmentGen = new StudentAssessmentGenerator(optional);
        StudentObjectiveAssessmentGenerator studentObjectiveAssessmentGen = new StudentObjectiveAssessmentGenerator(
                optional);
        StudentAssessmentItemGenerator saig = new StudentAssessmentItemGenerator(optional);

        int studentId = 0;
        for (StudentMeta studentMeta : studentMetas) {
            studentId++;
            for (AssessmentMeta assessmentMeta : assessmentMetas) {
                StudentReferenceType studentRef = StudentGenerator.getStudentReferenceType(studentMeta.id);
                String assessmentTitle = null;
                AssessmentReferenceType assessmentRef = AssessmentGenerator.getAssessmentReference(assessmentTitle,
                        assessmentMeta.id);
                StudentAssessment studentAssessment = studentAssessmentGen.generate(studentRef, assessmentRef);
                studentAssessment.setId("SA_" + studentId);
                studentAssessments.add(studentAssessment);

                int studentObjectiveAssessmentId = 0;
                for (ObjectiveAssessmentMeta objectiveAssessmentMeta : objectiveAssessmentMetas) {
                    ObjectiveAssessmentReferenceType objectiveAssessmentRef = ObjectiveAssessmentGenerator
                            .getObjectiveAssessmentReferenceType(objectiveAssessmentMeta.id);
                    StudentObjectiveAssessment studentObjectiveAssessment = studentObjectiveAssessmentGen.generate(
                            "SOA_" + studentId + "_" + (studentObjectiveAssessmentId++), assessmentRef,
                            objectiveAssessmentRef);
                    studentObjectiveAssessments.add(studentObjectiveAssessment);
                }

                int studentAssessmentItemId = 0;
                for (AssessmentItemMeta assessmentItemMeta : assessmentItemMetas) {
                    AssessmentItemReferenceType assessmentItemRef = AssessmentItemGenerator
                            .getAssessmentItemReferenceType(assessmentItemMeta.id);
                    ReferenceType studentObjectiveAssessmentReference = null;
                    StudentAssessmentItem studentAssessmentItem = saig.generate("SAI_" + studentId + "_"
                            + (studentAssessmentItemId++), assessmentItemRef, assessmentRef,
                            studentObjectiveAssessmentReference);
                    studentAssessmentItems.add(studentAssessmentItem);
                }
            }
        }
        interchangeObjects.addAll(studentAssessments);
        interchangeObjects.addAll(studentObjectiveAssessments);
        interchangeObjects.addAll(studentAssessmentItems);
    }

}
