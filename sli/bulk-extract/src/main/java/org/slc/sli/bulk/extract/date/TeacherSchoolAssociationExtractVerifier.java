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
package org.slc.sli.bulk.extract.date;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;

/**
 * Verifies whether or not a TeacherSchoolAssociation entity should be extracted.
 *
 * @author tke tshewchuk
 */
@Component
public class TeacherSchoolAssociationExtractVerifier implements ExtractVerifier {

    @Autowired
    private EdOrgExtractHelper edOrgExtractHelper;

    @Override
    public boolean shouldExtract(Entity entity, DateTime upToDate) {
        Iterable<Entity> seaos = edOrgExtractHelper.retrieveSEOAS((String) entity.getBody().get(ParameterConstants.TEACHER_ID),
                (String) entity.getBody().get(ParameterConstants.SCHOOL_ID));

        return shouldExtract(seaos, upToDate);
    }

    /**
     * Check if any of the input entities should be extracted.
     *
     * @param entities    List of entities to check
     * @param upToDate    Up to date
     *
     * @return   true if any of the entity should be extracted, false otherwise.
     */
    protected boolean shouldExtract(Iterable<Entity> entities, DateTime upToDate) {
        ExtractVerifier extractVerifier = getExtractVerifer();
        for (Entity entity : entities) {
            if (extractVerifier.shouldExtract(entity, upToDate)) {
                return true;
            }
        }
        return false;
    }

    protected ExtractVerifier getExtractVerifer() {
        return ExtractVerifierFactory.retrieveExtractVerifier(EntityNames.STAFF_ED_ORG_ASSOCIATION);
    }
}
