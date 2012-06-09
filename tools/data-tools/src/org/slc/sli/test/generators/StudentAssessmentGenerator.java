package org.slc.sli.test.generators;

import java.util.Random;

import org.slc.sli.test.edfi.entities.AdministrationEnvironmentType;
import org.slc.sli.test.edfi.entities.AssessmentReportingMethodType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.LanguageItemType;
import org.slc.sli.test.edfi.entities.LinguisticAccommodationItemType;
import org.slc.sli.test.edfi.entities.LinguisticAccommodationsType;
import org.slc.sli.test.edfi.entities.ReasonNotTestedType;
import org.slc.sli.test.edfi.entities.ScoreResult;
import org.slc.sli.test.edfi.entities.SpecialAccommodationItemType;
import org.slc.sli.test.edfi.entities.SpecialAccommodationsType;
import org.slc.sli.test.edfi.entities.StudentAssessment;
import org.slc.sli.test.edfi.entities.meta.StudentAssessmentMeta;
import org.slc.sli.test.edfi.entities.meta.relations.AssessmentMetaRelations;

public class StudentAssessmentGenerator {
    private static final boolean INCLUDE_OPTIONAL_DATA = true;
    private static final Random RANDOM = new Random();

    public static StudentAssessment generateLowFi(StudentAssessmentMeta studentAssessmentMeta) {
        StudentAssessment sa = new StudentAssessment();
        sa.setId(studentAssessmentMeta.xmlId);

        sa.setAdministrationDate("2011-05-08");

        // student reference
        sa.setStudentReference(StudentGenerator.getStudentReferenceType(studentAssessmentMeta.studentId));

        // assessment reference
        sa.setAssessmentReference(AssessmentGenerator.getAssessmentReference(studentAssessmentMeta.assessmentId));

        if (INCLUDE_OPTIONAL_DATA) {
            sa.setAdministrationEndDate("2012-05-08");
            sa.setSerialNumber("Serial Number");

            sa.setAdministrationLanguage(LanguageItemType.values()[RANDOM.nextInt(LanguageItemType.values().length)]);

            sa.setAdministrationEnvironment(AdministrationEnvironmentType.values()[RANDOM
                    .nextInt(AdministrationEnvironmentType.values().length)]);

            int numberOfSpecialAccommodations = RANDOM.nextInt(3);
            if (numberOfSpecialAccommodations > 0) {
                SpecialAccommodationsType sat = new SpecialAccommodationsType();
                for (int i = 0; i < numberOfSpecialAccommodations; i++) {
                    sat.getSpecialAccommodation()
                            .add(SpecialAccommodationItemType.values()[RANDOM.nextInt(SpecialAccommodationItemType
                                    .values().length)]);
                }
                sa.getSpecialAccommodations().add(sat);
            }

            int numberOfLinguisticAccommodations = RANDOM.nextInt(3);
            if (numberOfLinguisticAccommodations > 0) {
                LinguisticAccommodationsType lat = new LinguisticAccommodationsType();
                for (int i = 0; i < numberOfLinguisticAccommodations; i++) {
                    lat.getLinguisticAccommodation().add(
                            LinguisticAccommodationItemType.values()[RANDOM.nextInt(LinguisticAccommodationItemType
                                    .values().length)]);
                }
                sa.getLinguisticAccommodations().add(lat);
            }

            // TODO:missing RetestIndicatorType JAXB generated class
            sa.setRetestIndicator("Primary Administration");

            sa.setReasonNotTested(ReasonNotTestedType.values()[RANDOM.nextInt(ReasonNotTestedType.values().length)]);

//            int numberOfScoreResults = RANDOM.nextInt(3);
//            for (int i = 0; i < numberOfScoreResults; i++) {
//                ScoreResult sr = new ScoreResult();
//                sr.setAssessmentReportingMethod(AssessmentReportingMethodType.values()[RANDOM
//                        .nextInt(AssessmentReportingMethodType.values().length)]);
//                sr.setResult(""+RANDOM.nextInt(100));
//                sa.getScoreResults().add(sr);
//            }
          ScoreResult sr = new ScoreResult();
          sr.setAssessmentReportingMethod(AssessmentReportingMethodType.SCALE_SCORE);
          sr.setResult(""+RANDOM.nextInt(100));
          sa.getScoreResults().add(sr);

            sa.setGradeLevelWhenAssessed(GradeLevelType.values()[RANDOM.nextInt(GradeLevelType.values().length)]);

            // performanceLevels
            String randomPerfLevelDescId = AssessmentMetaRelations.getRandomPerfLevelDescMeta().id;
            sa.getPerformanceLevels().add(
                    PerformanceLevelDescriptorGenerator.getPerformanceLevelDescriptorType(randomPerfLevelDescId));
        }

        return sa;
    }
}
