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
