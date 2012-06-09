package org.slc.sli.test.generators;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.CourseCode;
import org.slc.sli.test.edfi.entities.CourseCodeSystemType;
import org.slc.sli.test.edfi.entities.Credits;
import org.slc.sli.test.edfi.entities.CreditsByCourse;
import org.slc.sli.test.edfi.entities.CreditsBySubject;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.GraduationPlan;
import org.slc.sli.test.edfi.entities.GraduationPlanType;

public class GraduationPlanGenerator {
    private Random random = new Random();
    private AcademicSubjectType[] subjectTypes = AcademicSubjectType.values();
    private GradeLevelType[] gradeLevels = GradeLevelType.values();
    private CourseCodeSystemType[] courseCodeSystemTypes = CourseCodeSystemType.values();

    private boolean optional = true;

    private int numberOfCreditsBySubject = random.nextInt(10);

    public GraduationPlanGenerator(boolean generateOptionalFields) {
        this.optional = generateOptionalFields;
    }

    public GraduationPlanGenerator() {
        this(true);
    }

    public GraduationPlan generate(String graduationPlanId, List<String> courses, List<String> edOrgs) {
        GraduationPlan gp = new GraduationPlan();

        gp.setId(graduationPlanId);

        gp.setGraduationPlanType(GraduationPlanType.STANDARD);

        Credits cs = new Credits();
        cs.setCredit(new BigDecimal(1 + random.nextInt(80)));
        gp.setTotalCreditsRequired(cs);

        if (optional) {
            gp.setIndividualPlan(random.nextBoolean());

            for (int i = 0 ; i < numberOfCreditsBySubject ; i++ ) {
                CreditsBySubject sbs = new CreditsBySubject();
                sbs.setSubjectArea(subjectTypes[random.nextInt(subjectTypes.length)]);
                Credits cs1 = new Credits();
                cs1.setCredit(new BigDecimal(1 + random.nextInt(5)));
                sbs.setCredits(cs1);
                gp.getCreditsBySubject().add(sbs);
            }

            for (String course : courses) {
                CreditsByCourse cbc = new CreditsByCourse();

                CourseCode cc = new CourseCode();
                cc.setID(course);
                cc.setIdentificationSystem(courseCodeSystemTypes[random.nextInt(courseCodeSystemTypes.length)]);
                cc.setAssigningOrganizationCode("someOrg");
                cbc.getCourseCode().add(cc);

                Credits cs1 = new Credits();
                cs1.setCredit(new BigDecimal(1 + random.nextInt(5)));
                cbc.setCredits(cs1);

                cbc.setGradeLevel(gradeLevels[random.nextInt(gradeLevels.length)]);

                gp.getCreditsByCourse().add(cbc);
            }

            for (String edOrg : edOrgs) {
                EducationalOrgIdentityType eoit = new EducationalOrgIdentityType();
                eoit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(edOrg);
                EducationalOrgReferenceType eort = new EducationalOrgReferenceType();
                eort.setEducationalOrgIdentity(eoit);
                gp.getEducationOrganizationReference().add(eort);
            }
        }

        return gp;
    }
}
