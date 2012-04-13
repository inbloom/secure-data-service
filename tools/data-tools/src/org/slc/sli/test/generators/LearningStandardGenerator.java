package org.slc.sli.test.generators;

import java.util.Random;

import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.ContentStandardType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.LearningStandard;
import org.slc.sli.test.edfi.entities.LearningStandardId;
import org.slc.sli.test.edfi.entities.LearningStandardIdentityType;
import org.slc.sli.test.edfi.entities.LearningStandardReferenceType;

public class LearningStandardGenerator {

    private static Random random = new Random();

    public static LearningStandard generateLowFi(String learningStandardCode) {
        LearningStandard ls = new LearningStandard();

        ls.setId(learningStandardCode);

        LearningStandardId lsid = new LearningStandardId();
        lsid.setContentStandardName(learningStandardCode + "Name");
        lsid.setIdentificationCode(learningStandardCode);
        ls.setLearningStandardId(lsid);

        ls.setDescription(learningStandardCode + " Descriptions");

        ls.setContentStandard(ContentStandardType.values()[random.nextInt(ContentStandardType.values().length)]);

        ls.setGradeLevel(GradeLevelType.values()[random.nextInt(GradeLevelType.values().length)]);

        ls.setSubjectArea(AcademicSubjectType.values()[random.nextInt(AcademicSubjectType.values().length)]);

        ls.setCourseTitle(learningStandardCode + " CourseTitle");

        return ls;
    }

    public static LearningStandardReferenceType getLearningStandardReferenceType(String learningStandardCode) {

        LearningStandardId lsid = new LearningStandardId();
        lsid.setContentStandardName(learningStandardCode + "Name");
        lsid.setIdentificationCode(learningStandardCode);

        LearningStandardIdentityType lsit = new LearningStandardIdentityType();
        lsit.setLearningStandardId(lsid);

        LearningStandardReferenceType lsrt = new LearningStandardReferenceType();
        lsrt.setLearningStandardIdentity(lsit);

        return lsrt;
    }
}
