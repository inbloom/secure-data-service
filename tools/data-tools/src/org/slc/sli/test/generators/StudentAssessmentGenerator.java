package org.slc.sli.test.generators;

import java.util.Random;

import org.slc.sli.test.edfi.entities.AdministrationEnvironmentType;
import org.slc.sli.test.edfi.entities.AssessmentReferenceType;
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
import org.slc.sli.test.edfi.entities.StudentReferenceType;
import org.slc.sli.test.edfi.entities.relations.StudentAssessmentMeta;

public class StudentAssessmentGenerator {
    private boolean optional = true;
    private static final Random RANDOM = new Random();

    private LanguageItemType[] lits = LanguageItemType.values();
    private AdministrationEnvironmentType[] aets = AdministrationEnvironmentType.values();
    private SpecialAccommodationItemType[] saits = SpecialAccommodationItemType.values();
    private LinguisticAccommodationItemType[] laits = LinguisticAccommodationItemType.values();

    private ReasonNotTestedType[] rntts = ReasonNotTestedType.values();
    private AssessmentReportingMethodType[] armts = AssessmentReportingMethodType.values();
    private GradeLevelType[] glts = GradeLevelType.values();

    public StudentAssessmentGenerator(boolean optional) {
        this.optional = optional;
    }

    public StudentAssessment generate(StudentReferenceType srt, AssessmentReferenceType art) {
        StudentAssessment sa = new StudentAssessment();

        sa.setAdministrationDate("2011-05-08");

        sa.setStudentReference(srt);

        sa.setAssessmentReference(art);

        if (optional) {
            sa.setAdministrationEndDate("2012-05-08");

            sa.setSerialNumber("Serial Number");

            sa.setAdministrationLanguage(lits[RANDOM.nextInt(lits.length)]);

            sa.setAdministrationEnvironment(aets[RANDOM.nextInt(aets.length)]);

            if (RANDOM.nextBoolean()) {
                int numberOfSpecialAccommodations = RANDOM.nextInt(5);
                for (int i = 0; i < numberOfSpecialAccommodations; i++) {
                    SpecialAccommodationsType sat = new SpecialAccommodationsType();
                    sat.getSpecialAccommodation().add(saits[RANDOM.nextInt(saits.length)]);
                    sa.getSpecialAccommodations().add(sat);
                }
            }

            if (RANDOM.nextBoolean()) {
                int numberOfLinguisticAccommodations = RANDOM.nextInt(5);
                for (int i = 0; i < numberOfLinguisticAccommodations; i++) {
                    LinguisticAccommodationsType lat = new LinguisticAccommodationsType();
                    lat.getLinguisticAccommodation().add(laits[RANDOM.nextInt(laits.length)]);
                    sa.getLinguisticAccommodations().add(lat);
                }
            }

            // TODO:missing RetestIndicatorType

            sa.setReasonNotTested(rntts[RANDOM.nextInt(rntts.length)]);

            if (RANDOM.nextBoolean()) {
                int numberOfScoreResults = RANDOM.nextInt(10);
                for (int i = 0; i < numberOfScoreResults; i++) {
                    ScoreResult sr = new ScoreResult();
                    sr.setAssessmentReportingMethod(armts[RANDOM.nextInt(armts.length)]);
                    sr.setResult("Assessment Result");
                    sa.getScoreResults().add(sr);
                }
            }

            sa.setGradeLevelWhenAssessed(glts[RANDOM.nextInt(glts.length)]);

            // TODO: add performanceLevels

        }

        return sa;
    }

    public static StudentAssessment generateLowFi(StudentAssessmentMeta studentAssessmentMeta) {
        StudentAssessment sa = new StudentAssessment();
        sa.setId(studentAssessmentMeta.xmlId);

        sa.setAdministrationDate("2011-05-08");
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
                        .add(SpecialAccommodationItemType.values()[RANDOM.nextInt(SpecialAccommodationItemType.values().length)]);
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

        int numberOfScoreResults = RANDOM.nextInt(3);
        for (int i = 0; i < numberOfScoreResults; i++) {
            ScoreResult sr = new ScoreResult();
            sr.setAssessmentReportingMethod(AssessmentReportingMethodType.values()[RANDOM
                    .nextInt(AssessmentReportingMethodType.values().length)]);
            sr.setResult("Assessment Result" + i);
            sa.getScoreResults().add(sr);
        }

        sa.setGradeLevelWhenAssessed(GradeLevelType.values()[RANDOM.nextInt(GradeLevelType.values().length)]);

        // TODO: add performanceLevels

        sa.setStudentReference(StudentGenerator.getStudentReferenceType(studentAssessmentMeta.studentId));

        sa.setAssessmentReference(AssessmentGenerator.getAssessmentReference(studentAssessmentMeta.assessmentId));

        return sa;
    }
}
