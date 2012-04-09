package org.slc.sli.test.generators;

import java.util.Random;

import org.slc.sli.test.edfi.entities.*;

public class StudentAssessmentGenerator {
    private boolean optional = true;
    private Random random = new Random();

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

            sa.setAdministrationLanguage(lits[random.nextInt(lits.length)]);

            sa.setAdministrationEnvironment(aets[random.nextInt(aets.length)]);

            if (random.nextBoolean()) {
                int numberOfSpecialAccommodations = random.nextInt(5);
                for (int i = 0; i < numberOfSpecialAccommodations; i++) {
                    SpecialAccommodationsType sat = new SpecialAccommodationsType();
                    sat.getSpecialAccommodation().add(saits[random.nextInt(saits.length)]);
                    sa.getSpecialAccommodations().add(sat);
                }
            }

            if (random.nextBoolean()) {
                int numberOfLinguisticAccommodations = random.nextInt(5);
                for (int i = 0 ; i < numberOfLinguisticAccommodations ; i++ ) {
                    LinguisticAccommodationsType lat = new LinguisticAccommodationsType();
                    lat.getLinguisticAccommodation().add(laits[random.nextInt(laits.length)]);
                    sa.getLinguisticAccommodations().add(lat);
                }
            }

            //TODO:missing RetestIndicatorType

            sa.setReasonNotTested(rntts[random.nextInt(rntts.length)]);

            if (random.nextBoolean()) {
                int numberOfScoreResults = random.nextInt(10);
                for (int i = 0; i < numberOfScoreResults ; i++) {
                    ScoreResult sr = new ScoreResult();
                    sr.setAssessmentReportingMethod(armts[random.nextInt(armts.length)]);
                    sr.setResult("Assessment Result");
                    sa.getScoreResults().add(sr);
                }
            }

            sa.setGradeLevelWhenAssessed(glts[random.nextInt(glts.length)]);

            //TODO: add performanceLevels

        }

        return sa;
    }
}
