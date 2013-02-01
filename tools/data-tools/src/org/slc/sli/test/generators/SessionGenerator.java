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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

import org.slc.sli.test.edfi.entities.SLCEducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.SLCGradingPeriodReferenceType;
import org.slc.sli.test.edfi.entities.GradingPeriodType;
import org.slc.sli.test.edfi.entities.SLCGradingPeriodIdentityType;
import org.slc.sli.test.edfi.entities.SLCSession;
import org.slc.sli.test.edfi.entities.SLCSessionIdentityType;
import org.slc.sli.test.edfi.entities.SLCSessionReferenceType;
import org.slc.sli.test.edfi.entities.TermType;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;

public class SessionGenerator {

    private String beginDate = "2011-03-01";
    private String endDate = "2012-03-01";
    Random generator = new Random(31);

    public SLCSession sessionGenerator(String stateOrgId) {
        SLCSession session = new SLCSession();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");

        session.setSessionName("2012 Spring");

        session.setSchoolYear("2011");

        session.setBeginDate(beginDate);
        session.setEndDate(endDate);
        session.setTerm(getTermType());
        Random random = new Random(31);
        int roll = 45 + (int) (random.nextDouble() * (150 - 45));
        session.setTotalInstructionalDays(roll);

        SLCEducationalOrgIdentityType eoit = new SLCEducationalOrgIdentityType();
        eoit.setStateOrganizationId(stateOrgId);
        SLCEducationalOrgReferenceType eort = new SLCEducationalOrgReferenceType();
        eort.setEducationalOrgIdentity(eoit);
        session.setEducationOrganizationReference(eort);

        SLCGradingPeriodIdentityType gpit = new SLCGradingPeriodIdentityType();
        gpit.setGradingPeriod(GradingPeriodType.FIRST_NINE_WEEKS);
        gpit.setBeginDate(beginDate);
        // System.out.println("this is grading period Type :" +
        // gpit.getGradingPeriod());
        // System.out.println("this is school year Type :" +
        // gpit.getSchoolYear());

        // SLCEducationalOrgIdentityType eoit2 = new SLCEducationalOrgIdentityType();
        // for (String stateOrgId : stateOrgIds) {
        // eoit2.setStateOrganizationId(stateOrgId);
        // }
        // SLCEducationalOrgReferenceType eort2 = new SLCEducationalOrgReferenceType();
        // eort.setEducationalOrgIdentity(eoit2);
        // gpit.setEducationalOrgReference(eort2);
        // // System.out.println("this is state org Id :" +
        // gpit.getStateOrganizationIdOrEducationOrgIdentificationCode().get(0)
        // );
        // System.out.println("this is state org Id :" +
        // gpit.getStateOrganizationIdOrEducationOrgIdentificationCode().get(1)
        // );
        SLCGradingPeriodReferenceType gprt = new SLCGradingPeriodReferenceType();

        gprt.setGradingPeriodIdentity(gpit);

        session.getGradingPeriodReference().add(gprt);
        // System.out.println("This is state org id by gradingPeriodReference: "
        // +
        // session.getGradingPeriodReference().get(0).getGradingPeriodIdentity().getStateOrganizationIdOrEducationOrgIdentificationCode().get(0));
        // System.out.println("This is state org id by gradingPeriodReference : "
        // +
        // session.getGradingPeriodReference().get(0).getGradingPeriodIdentity().getStateOrganizationIdOrEducationOrgIdentificationCode().get(1));

        return session;
    }

    public GradingPeriodType getGradingPeriodType() {
        return GradingPeriodType.FIRST_NINE_WEEKS;
    }

    public TermType getTermType() {
        int roll = generator.nextInt(8) + 1;
        switch (roll) {
            case 1:
                return TermType.FALL_SEMESTER;
            case 2:
                return TermType.FIRST_TRIMESTER;
            case 3:
                return TermType.MINI_TERM;
            case 4:
                return TermType.SECOND_TRIMESTER;
            case 5:
                return TermType.SPRING_SEMESTER;
            case 6:
                return TermType.SUMMER_SEMESTER;
            case 7:
                return TermType.THIRD_TRIMESTER;
            default:
                return TermType.YEAR_ROUND;
        }
    }

    public static SLCSession generateLowFi(String id, String schoolId, List<String> calendarList,
            List<Integer> gradingPeriodNums) {
        SLCSession session = new SLCSession();
        Random random = new Random(31);
        int roll = 1;// random.nextInt(30) + 1;
        session.setSessionName(id);
        session.setSchoolYear("2011-2012");
        session.setTerm(TermType.SPRING_SEMESTER);
        String finalRoll = "0";
        // if (roll < 10 ) {
        // finalRoll = finalRoll + roll;
        // session.setBeginDate("2012-01-" + finalRoll );
        // }
        // else
        // session.setBeginDate("2012-01-" + roll );
        //
        // if (roll < 10 ) {
        // session.setEndDate("2012-06-" + finalRoll );
        // }
        // else
        // session.setEndDate("2012-06-" + + roll);

        // lina
        session.setBeginDate("2012-01-01");
        session.setEndDate("2012-12-31");

        // session.setBeginDate("2012-01-01");
        // session.setEndDate("2012-06-21");
        session.setTotalInstructionalDays(120);

        // session.getGradingPeriodReference().add(new GradingPeriodReferenceType());

        // construct and add the school reference
        SLCEducationalOrgIdentityType edOrgIdentityType = new SLCEducationalOrgIdentityType();
        edOrgIdentityType.setStateOrganizationId(schoolId);

        SLCEducationalOrgReferenceType schoolRef = new SLCEducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentityType);

        // should really have gradingPeriod meta data to build this up from
        // restrict grading periods so that refs are unique

        for (Integer gradingPeriodNum : gradingPeriodNums) {

            SLCGradingPeriodIdentityType gpit = new SLCGradingPeriodIdentityType();

            SLCEducationalOrgIdentityType eoit = new SLCEducationalOrgIdentityType();
            eoit.setStateOrganizationId(schoolId);
            SLCEducationalOrgReferenceType eort = new SLCEducationalOrgReferenceType();
            eort.setEducationalOrgIdentity(eoit);
            gpit.setEducationalOrgReference(eort);

            gpit.setGradingPeriod(GradingPeriodType.FIRST_NINE_WEEKS);
            gpit.setBeginDate("2011-03-04");

            SLCGradingPeriodReferenceType gprt = new SLCGradingPeriodReferenceType();
            gprt.setGradingPeriodIdentity(gpit);
            // gprt.setBeginDate("2012-09-01");
            session.getGradingPeriodReference().add(gprt);

        }

        session.setEducationOrganizationReference(schoolRef);
        return session;
    }

    public static void main(String args[]) {

        SessionGenerator sg = new SessionGenerator();

        SLCSession s = sg.sessionGenerator("100200200");
        String sessionString = "\n\n" + " 1" + ".\n" + "sessionName : " + s.getSessionName() + ",\n" + "schoolYear : "
                + s.getSchoolYear() + ",\n" + "totalInstructionDays : " + s.getTotalInstructionalDays() + ",\n"
                + "startDate : " + s.getBeginDate() + ",\n" + "endDate : " + s.getEndDate() + ",\n" + "term : "
                + s.getTerm() + ",\n" + "getGradingPeriodReference : " + s.getGradingPeriodReference().size() + ",\n"
                + "getEducationalOrgIdentity : "
                + s.getEducationOrganizationReference().getEducationalOrgIdentity().getStateOrganizationId();
        System.out.println(sessionString + ",\n");

        SLCSession s1 = sg.sessionGenerator("400200200");

        String sessionString1 = "\n\n" + " 2" + ".\n" + "sessionName : " + s1.getSessionName() + ",\n"
                + "schoolYear : " + s1.getSchoolYear() + ",\n" + "totalInstructionDays : "
                + s1.getTotalInstructionalDays() + ",\n" + "startDate : " + s1.getBeginDate() + ",\n" + "endDate : "
                + s1.getEndDate() + ",\n" + "term : " + s1.getTerm() + ",\n" + "getGradingPeriodReference : "
                + s1.getGradingPeriodReference().size() + ",\n" + "getEducationalOrgIdentity : "
                + s1.getEducationOrganizationReference().getEducationalOrgIdentity().getStateOrganizationId();
        System.out.println(sessionString1);

        SLCSessionReferenceType sessionRef = SessionGenerator.getSessionReferenceType("stateOrganizationId",
                "sessionName");
        System.out.println(sessionRef);

    }

    public static SLCSessionReferenceType getSessinReferenceType(SLCSession session) {
        SLCSessionReferenceType ref = new SLCSessionReferenceType();
        SLCSessionIdentityType identity = new SLCSessionIdentityType();
        ref.setSessionIdentity(identity);
        identity.setSessionName(session.getSessionName());

        SLCEducationalOrgReferenceType edOrgRef = EducationAgencyGenerator.generateReference(session
                .getEducationOrganizationReference().getEducationalOrgIdentity().getStateOrganizationId());
        identity.setEducationalOrgReference(edOrgRef);
        return ref;
    }

    public static SLCSessionReferenceType getSessionReferenceType(String stateOrganizationId, String sessionName) {
        SLCSessionReferenceType ref = new SLCSessionReferenceType();
        SLCSessionIdentityType sessionIdentity = new SLCSessionIdentityType();
        ref.setSessionIdentity(sessionIdentity);

        if (stateOrganizationId != null) {
            SLCEducationalOrgReferenceType edOrgRef = EducationAgencyGenerator.generateReference(stateOrganizationId);
            sessionIdentity.setEducationalOrgReference(edOrgRef);
        }

        if (sessionName != null)
            sessionIdentity.setSessionName(sessionName);
        return ref;
    }

}
