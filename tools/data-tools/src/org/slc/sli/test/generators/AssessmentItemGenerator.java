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


package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.SLCAssessmentItem;
import org.slc.sli.test.edfi.entities.SLCAssessmentItemIdentityType;
import org.slc.sli.test.edfi.entities.SLCAssessmentItemReferenceType;
import org.slc.sli.test.edfi.entities.ItemCategoryType;
import org.slc.sli.test.edfi.entities.meta.AssessmentItemMeta;

public class AssessmentItemGenerator {

    public static SLCAssessmentItem generateLowFi(final AssessmentItemMeta assessmentItemMeta) {
        SLCAssessmentItem assessmentItem = new SLCAssessmentItem();
        assessmentItem.setIdentificationCode(assessmentItemMeta.id);
        assessmentItem.setItemCategory(ItemCategoryType.ANALYTIC);
        assessmentItem.setMaxRawScore(100);
        assessmentItem.setCorrectResponse("CorrectResponse");

        for (String learningStandardRef : assessmentItemMeta.learningStandardIds) {
            assessmentItem.getLearningStandardReference().add(
                    LearningStandardGenerator.getLearningStandardReferenceType(learningStandardRef));
        }

        assessmentItem.setAssessmentReference(AssessmentGenerator.getAssessmentReference(assessmentItemMeta.getAssessmentId()));
        assessmentItem.setNomenclature("Nomenclature");
        return assessmentItem;
    }

    public static SLCAssessmentItemReferenceType getAssessmentItemReferenceType(final String code) {
        SLCAssessmentItemReferenceType ref = new SLCAssessmentItemReferenceType();
        SLCAssessmentItemIdentityType identity = new SLCAssessmentItemIdentityType();
        ref.setAssessmentItemIdentity(identity);
        identity.setAssessmentItemIdentificationCode(code);
        return ref;
    }

    public static SLCAssessmentItemReferenceType getAssessmentItemReferenceType(final SLCAssessmentItem item) {
        SLCAssessmentItemReferenceType ref = new SLCAssessmentItemReferenceType();
        SLCAssessmentItemIdentityType identity = new SLCAssessmentItemIdentityType();
        ref.setAssessmentItemIdentity(identity);
        identity.setAssessmentItemIdentificationCode(item.getIdentificationCode());
        return ref;
    }
}
