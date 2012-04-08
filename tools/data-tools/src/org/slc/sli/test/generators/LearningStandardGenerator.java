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
    private ContentStandardType[] csts = ContentStandardType.values();
    private GradeLevelType[] glts = GradeLevelType.values();
    private AcademicSubjectType[] asts = AcademicSubjectType.values();

    private Random random = new Random();

    private boolean optional = true;

    public LearningStandardGenerator(boolean optional) {
        this.optional = optional;
    }

    public LearningStandard generate(String learningStandardCode) {
        LearningStandard ls = new LearningStandard();

        ls.setId(learningStandardCode);

        LearningStandardId lsid = new LearningStandardId();
        lsid.setContentStandardName(learningStandardCode + "Name");
        lsid.setIdentificationCode(learningStandardCode);
        ls.setLearningStandardId(lsid);

        ls.setDescription(learningStandardCode + " Descriptions");

        ls.setContentStandard(csts[random.nextInt(csts.length)]);

        ls.setGradeLevel(glts[random.nextInt(glts.length)]);

        ls.setSubjectArea(asts[random.nextInt(asts.length)]);

        if (optional) {
            ls.setCourseTitle(learningStandardCode + " CourseTitle");
        }

        return ls;
    }

    public static LearningStandardReferenceType getLearningStandardReferenceType(String learningStandardCode) {
        LearningStandardReferenceType lsrt = new LearningStandardReferenceType();

        lsrt.setRef(learningStandardCode);

        LearningStandardId lsid = new LearningStandardId();
        lsid.setContentStandardName(learningStandardCode + "Name");
        lsid.setIdentificationCode(learningStandardCode);
        LearningStandardIdentityType lsit = new LearningStandardIdentityType();
        lsit.setLearningStandardId(lsid);
        lsrt.setLearningStandardIdentity(lsit);

        return lsrt;
    }
}
