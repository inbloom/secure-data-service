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

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.CourseCode;
import org.slc.sli.test.edfi.entities.CourseCodeSystemType;
import org.slc.sli.test.edfi.entities.Credits;
import org.slc.sli.test.edfi.entities.CreditsByCourse;
import org.slc.sli.test.edfi.entities.CreditsBySubject;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.SLCGraduationPlan;
import org.slc.sli.test.edfi.entities.GraduationPlanType;

public class GraduationPlanGenerator {
    private static Random random = new Random(31);
    private AcademicSubjectType[] subjectTypes = AcademicSubjectType.values();
    private GradeLevelType[] gradeLevels = GradeLevelType.values();
    private CourseCodeSystemType[] courseCodeSystemTypes = CourseCodeSystemType.values();

    private boolean optional = true;

    private int numberOfCreditsBySubject = random.nextInt(10);

    //in order to be unique, a school can't have two graduationPlans of the same type, so iterate through
    private static int typeIndex = 0;

    public GraduationPlanGenerator(boolean generateOptionalFields) {
        this.optional = generateOptionalFields;
    }

    public GraduationPlanGenerator() {
        this(true);
    }

    public static SLCGraduationPlan generateLowFi(String graduationPlanId, String edOrg) {
           SLCGraduationPlan gp = new SLCGraduationPlan();

        gp.setId(graduationPlanId);

        gp.setGraduationPlanType(GraduationPlanGenerator.fromIndex(typeIndex++));

        Credits cs = new Credits();
        cs.setCredit(new BigDecimal(1 + random.nextInt(80)));
        gp.setTotalCreditsRequired(cs);

        SLCEducationalOrgIdentityType eoit = new SLCEducationalOrgIdentityType();
        //eoit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(edOrg);
        eoit.setStateOrganizationId(edOrg);
        SLCEducationalOrgReferenceType eort = new SLCEducationalOrgReferenceType();
        eort.setEducationalOrgIdentity(eoit);
        gp.getEducationOrganizationReference().add(eort);

        return gp;
   }

    public SLCGraduationPlan generate(String graduationPlanId, List<String> courses, String edOrg) {
        SLCGraduationPlan gp = generateLowFi(graduationPlanId, edOrg);

        if (null != gp && optional) {
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
        }

        return gp;
    }

    //in order to be unique, a school can't have two graduationPlans of the same type, so iterate through
    public static GraduationPlanType fromIndex(int i) {

        final int NUM_TYPES = 5;

        switch (i % NUM_TYPES) {
        case 0:
            return GraduationPlanType.CAREER_AND_TECHNICAL_EDUCATION;
        case 1:
            return GraduationPlanType.DISTINGUISHED;
        case 2:
            return GraduationPlanType.MINIMUM;
        case 3:
            return GraduationPlanType.RECOMMENDED;
        default:
            return GraduationPlanType.STANDARD;
        }
    }
}
