package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.AssessmentPeriodDescriptor;
import org.slc.sli.test.edfi.entities.AssessmentPeriodDescriptorType;
import org.slc.sli.test.edfi.entities.ObjectFactory;
import org.slc.sli.test.edfi.entities.meta.AssessmentPeriodDescriptorMeta;

public class AssessmentPeriodDescriptorGenerator {

    // TODO: maybe make public and move to one common generator class for use by all
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    public static AssessmentPeriodDescriptor generateLowFi(AssessmentPeriodDescriptorMeta assessPeriodDescMeta) {
        AssessmentPeriodDescriptor assessmentPeriodDescriptor = new AssessmentPeriodDescriptor();
        assessmentPeriodDescriptor.setCodeValue(assessPeriodDescMeta.id);
        assessmentPeriodDescriptor.setDescription("Description");
        assessmentPeriodDescriptor.setShortDescription("ShortDescription");
        assessmentPeriodDescriptor.setBeginDate("2012-01-12");
        assessmentPeriodDescriptor.setEndDate("2012-11-12");
        return assessmentPeriodDescriptor;
    }

    public static AssessmentPeriodDescriptorType getAssessmentPeriodDescriptorType(String assessPeriodDescIdString) {
        AssessmentPeriodDescriptorType assessPeriodDescType = new AssessmentPeriodDescriptorType();
        assessPeriodDescType.getCodeValueOrShortDescriptionOrDescription().add(
                OBJECT_FACTORY.createAssessmentPeriodDescriptorTypeCodeValue(assessPeriodDescIdString));
        return assessPeriodDescType;
    }
}
