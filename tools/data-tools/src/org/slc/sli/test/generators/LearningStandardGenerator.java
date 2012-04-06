package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.ContentStandardType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.LearningStandard;
import org.slc.sli.test.edfi.entities.LearningStandardId;
import org.slc.sli.test.edfi.entities.LearningStandardIdentityType;
import org.slc.sli.test.edfi.entities.LearningStandardReferenceType;

public class LearningStandardGenerator {

    public static LearningStandard getLearningStandard() {
        LearningStandard learningStandard = new LearningStandard();
        LearningStandardId id = new LearningStandardId();
        learningStandard.setLearningStandardId(id);
        id.setIdentificationCode("IdentificationCode");
        id.setContentStandardName("Content Standard Name");
        learningStandard.setDescription("Description");
        learningStandard.setContentStandard(ContentStandardType.ACT);
        learningStandard.setGradeLevel(GradeLevelType.EIGHTH_GRADE);
        learningStandard.setSubjectArea(AcademicSubjectType.ENGLISH);
        learningStandard.setCourseTitle("CourseTitle");
        return learningStandard;
    }

    public static LearningStandardReferenceType LearningStandardReferenceType(
            final String learningStandardCode,
            final String contentStandardName) {
        LearningStandardReferenceType learningStandardRef =
                new LearningStandardReferenceType();
        LearningStandardIdentityType identity =
                new LearningStandardIdentityType();
        learningStandardRef.setLearningStandardIdentity(identity);
        LearningStandardId id = new LearningStandardId();
        identity.setLearningStandardId(id);
        if (learningStandardCode != null) {
            id.setIdentificationCode(learningStandardCode);
        }
        if (contentStandardName != null) {
            id.setContentStandardName(contentStandardName);
        }
        return learningStandardRef;
    }

    public static LearningStandardReferenceType LearningStandardReferenceType(
            final LearningStandard learningStandard) {
        LearningStandardReferenceType learningStandardRef =
                new LearningStandardReferenceType();
        LearningStandardIdentityType identity =
                new LearningStandardIdentityType();
        learningStandardRef.setLearningStandardIdentity(identity);
        LearningStandardId id = new LearningStandardId();
        identity.setLearningStandardId(id);
        id.setIdentificationCode(learningStandard.getLearningStandardId().
                getIdentificationCode());
        id.setContentStandardName(learningStandard.getLearningStandardId().
                getContentStandardName());
        return learningStandardRef;
    }
}
