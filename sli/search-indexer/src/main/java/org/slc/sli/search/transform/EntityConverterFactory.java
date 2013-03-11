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
