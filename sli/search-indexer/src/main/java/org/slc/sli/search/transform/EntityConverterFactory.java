package org.slc.sli.search.transform;

import org.slc.sli.search.transform.impl.AssessmentEntityConverter;
import org.slc.sli.search.transform.impl.AssessmentPeriodDescriptorEntityConverter;
import org.slc.sli.search.transform.impl.GenericEntityConverter;

public class EntityConverterFactory {

    private GenericEntityConverter genericEntityConverter;
    private AssessmentEntityConverter assessmentEntityConverter;
    private AssessmentPeriodDescriptorEntityConverter assessmentPeriodDescriptorEntityConverter;
    
    public EntityConverter getConverter(String type) {
        if ("assessment".equals(type)) {
            return assessmentEntityConverter;
        }

        if ("assessmentPeriodDescriptor".equals(type)) {
            return assessmentPeriodDescriptorEntityConverter;
        }

        return genericEntityConverter;
    }

    /* only for unit testing */
    public void setGenericEntityConverter(GenericEntityConverter genericEntityConverter) {
        this.genericEntityConverter = genericEntityConverter;
    }
    
    public void setAssessmentEntityConverter(AssessmentEntityConverter assessmentEntityConverter) {
        this.assessmentEntityConverter = assessmentEntityConverter;
    }
    
    public void setAssessmentPeriodDescriptorEntityConverter(
            AssessmentPeriodDescriptorEntityConverter assessmentPeriodDescriptorEntityConverter) {
        this.assessmentPeriodDescriptorEntityConverter = assessmentPeriodDescriptorEntityConverter;
    }

}
