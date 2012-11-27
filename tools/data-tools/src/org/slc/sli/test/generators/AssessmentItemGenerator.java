/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import org.slc.sli.test.edfi.entities.AssessmentItem;
import org.slc.sli.test.edfi.entities.AssessmentItemIdentityType;
import org.slc.sli.test.edfi.entities.AssessmentItemReferenceType;
import org.slc.sli.test.edfi.entities.ItemCategoryType;
import org.slc.sli.test.edfi.entities.meta.AssessmentItemMeta;

public class AssessmentItemGenerator {

    public static AssessmentItem generateLowFi(final AssessmentItemMeta assessmentItemMeta) {
        AssessmentItem assessmentItem = new AssessmentItem();
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

    public static AssessmentItemReferenceType getAssessmentItemReferenceType(final String code) {
        AssessmentItemReferenceType ref = new AssessmentItemReferenceType();
        AssessmentItemIdentityType identity = new AssessmentItemIdentityType();
        ref.setAssessmentItemIdentity(identity);
        identity.setAssessmentItemIdentificationCode(code);
        return ref;
    }

    public static AssessmentItemReferenceType getAssessmentItemReferenceType(final AssessmentItem item) {
        AssessmentItemReferenceType ref = new AssessmentItemReferenceType();
        AssessmentItemIdentityType identity = new AssessmentItemIdentityType();
        ref.setAssessmentItemIdentity(identity);
        identity.setAssessmentItemIdentificationCode(item.getIdentificationCode());
        return ref;
    }
}
