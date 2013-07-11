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
package org.slc.sli.bulk.extract.treatment;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.dal.convert.SuperdocConverter;
import org.slc.sli.domain.Entity;

/** Embed subdocs into Assessment and StudentAssessment entities.
 *
 * @author tshewchuk
 *
 */
public class AssessmentTreatment implements Treatment{

    @Autowired
    private SuperdocConverter assessmentConverter;

    @Override
    public Entity apply(Entity entity) {
        return assessmentConverter.subdocToBodyField(entity);
    }

    /**
     * Get assessment converter.
     * @return the assessmentConverter
     */
    public SuperdocConverter getAssessmentConverter() {
        return assessmentConverter;
    }

    /**
     * Set assessment converter.
     * @param assessmentConverter the assessmentConverter to set
     */
    public void setAssessmentConverter(SuperdocConverter assessmentConverter) {
        this.assessmentConverter = assessmentConverter;
    }

}
