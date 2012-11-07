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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slc.sli.test.edfi.entities.CalendarDateIdentityType;
import org.slc.sli.test.edfi.entities.CalendarDateReferenceType;
import org.slc.sli.test.edfi.entities.EducationOrgIdentificationCode;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.GradingPeriodReferenceType;
import org.slc.sli.test.edfi.entities.Ref;
import org.slc.sli.test.edfi.entities.ReferenceType;
import org.slc.sli.test.edfi.entities.SLCGradingPeriodIdentityType;
import org.slc.sli.test.edfi.entities.Session;
import org.slc.sli.test.edfi.entities.SessionIdentityType;
import org.slc.sli.test.edfi.entities.SessionReferenceType;
import org.slc.sli.test.edfi.entities.TermType;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;

public class SessionGenerator {

    private String beginDate = "2011-03-04";
    private String endDate = "2012-03-04";
    Random generator = new Random(31);

    public Session sessionGenerator(List<String> stateOrgIds) {
        Session session = new Session();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");

        session.setSessionName("2012 Spring");

        session.setSchoolYear("2011");

        session.setBeginDate(beginDate);
        session.setEndDate(endDate);
        session.setTerm(getTermType());
        Random random = new Random(31);
        int roll = 45 + (int) (random.nextDouble() * (150 - 45));
        session.setTotalInstructionalDays(roll);

        EducationalOrgIdentityType eoit = new EducationalOrgIdentityType();
        EducationOrgIdentificationCode eoic = new EducationOrgIdentificationCode();
        for (String stateOrgId : stateOrgIds)
            eoic.setID(stateOrgId);
            eoit.getEducationOrgIdentificationCode().add(eoic);
        EducationalOrgReferenceType eort = new EducationalOrgReferenceType();
        eort.setEducationalOrgIdentity(eoit);
        session.setEducationOrganizationReference(eort);

        SLCGradingPeriodIdentityType gpit = new SLCGradingPeriodIdentityType();
        gpit.setGradingPeriod(GradingPeriodGenerator.getGradingPeriodType(generator.nextInt(20) + 1));
        gpit.setBeginDate(beginDate);
        // System.out.println("this is grading period Type :" +
        // gpit.getGradingPeriod());
        // System.out.println("this is school year Type :" +
        // gpit.getSchoolYear());

        EducationalOrgIdentityType eoit2 = new EducationalOrgIdentityType();
        for (String stateOrgId : stateOrgIds) {
            eoit2.setStateOrganizationId(stateOrgId);
        }
        EducationalOrgReferenceType eort2 = new EducationalOrgReferenceType();
        eort.setEducationalOrgIdentity(eoit2);
        gpit.setEducationalOrgReference(eort2);
        // System.out.println("this is state org Id :" +
        // gpit.getStateOrganizationIdOrEducationOrgIdentificationCode().get(0)
        // );
        // System.out.println("this is state org Id :" +
        // gpit.getStateOrganizationIdOrEducationOrgIdentificationCode().get(1)
        // );
        GradingPeriodReferenceType gprt = new GradingPeriodReferenceType();

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

    public static Session generateLowFi(String id, String schoolId, List<String> calendarList, List<Integer> gradingPeriodNums) {
        Session session = new Session();
        Random random = new Random(31);
        int roll = 1;//random.nextInt(30) + 1;
        session.setSessionName(id);
        session.setSchoolYear("2011-2012");
        session.setTerm(TermType.SPRING_SEMESTER);
        String finalRoll = "0" ;
//        if (roll < 10 ) {
//        	finalRoll = finalRoll + roll;
//            session.setBeginDate("2012-01-" + finalRoll );
//        }
//        else
//        	session.setBeginDate("2012-01-" + roll );
//
//        if (roll < 10 ) {
//            session.setEndDate("2012-06-" + finalRoll );
//        }
//        else
//        	session.setEndDate("2012-06-" + + roll);

        //lina
        session.setBeginDate("2012-01-01");
        session.setEndDate("2012-12-31");


//        session.setBeginDate("2012-01-01");
//        session.setEndDate("2012-06-21");
        session.setTotalInstructionalDays(120);


        //session.getGradingPeriodReference().add(new GradingPeriodReferenceType());

        // construct and add the school reference
        EducationalOrgIdentityType edOrgIdentityType = new EducationalOrgIdentityType();
        edOrgIdentityType.setStateOrganizationId(schoolId);

        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentityType);

        //should really have gradingPeriod meta data to build this up from
        //restrict grading periods so that refs are unique

        for (Integer gradingPeriodNum : gradingPeriodNums) {
            if (MetaRelations.Session_Ref) {
                Ref gpRef = new Ref(calendarList.get(0) + "-" + gradingPeriodNum.intValue());
                GradingPeriodReferenceType gprt = new GradingPeriodReferenceType();
                gprt.setRef(gpRef);
                session.getGradingPeriodReference().add(gprt);
            } else {
                SLCGradingPeriodIdentityType gpit = new SLCGradingPeriodIdentityType();

                EducationalOrgIdentityType eoit = new EducationalOrgIdentityType();
                eoit.setStateOrganizationId(schoolId);
                EducationalOrgReferenceType eort = new EducationalOrgReferenceType();
                eort.setEducationalOrgIdentity(eoit);
                gpit.setEducationalOrgReference(eort);

                gpit.setGradingPeriod(GradingPeriodGenerator.getGradingPeriodType(gradingPeriodNum.intValue()));
                gpit.setBeginDate("2011-03-04");

                GradingPeriodReferenceType gprt = new GradingPeriodReferenceType();
                gprt.setGradingPeriodIdentity(gpit);
                //gprt.setBeginDate("2012-09-01");
                session.getGradingPeriodReference().add(gprt);
            }
        }

        session.setEducationOrganizationReference(schoolRef);
        return session;
    }

    public static void main(String args[]) {

        SessionGenerator sg = new SessionGenerator();

        List<String> stateOrgIdss = new ArrayList();
        stateOrgIdss.add("100100100");
        stateOrgIdss.add("200200200");
        List<String> stateOrgIdss2 = new ArrayList();
        stateOrgIdss2.add("300100100");
        stateOrgIdss2.add("400200200");

        Session s = sg.sessionGenerator(stateOrgIdss);
        String sessionString = "\n\n" + " 1" + ".\n" + "sessionName : "
                + s.getSessionName()
                + ",\n"
                + "schoolYear : "
                + s.getSchoolYear()
                + ",\n"
                + "totalInstructionDays : "
                + s.getTotalInstructionalDays()
                + ",\n"
                + "startDate : "
                + s.getBeginDate()
                + ",\n"
                + "endDate : "
                + s.getEndDate()
                + ",\n"
                + "term : "
                + s.getTerm()
                + ",\n"
                + "getGradingPeriodReference : "
                + s.getGradingPeriodReference().size()
                + ",\n"
                + "getEducationalOrgIdentity : "
                + s.getEducationOrganizationReference().getEducationalOrgIdentity()
                        .getEducationOrgIdentificationCode().size();
        System.out.println(sessionString + ",\n");

        Session s1 = sg.sessionGenerator(stateOrgIdss2);

        String sessionString1 = "\n\n" + " 2" + ".\n" + "sessionName : "
                + s1.getSessionName()
                + ",\n"
                + "schoolYear : "
                + s1.getSchoolYear()
                + ",\n"
                + "totalInstructionDays : "
                + s1.getTotalInstructionalDays()
                + ",\n"
                + "startDate : "
                + s1.getBeginDate()
                + ",\n"
                + "endDate : "
                + s1.getEndDate()
                + ",\n"
                + "term : "
                + s1.getTerm()
                + ",\n"
                + "getGradingPeriodReference : "
                + s1.getGradingPeriodReference().size()
                + ",\n"
                + "getEducationalOrgIdentity : "
                + s1.getEducationOrganizationReference().getEducationalOrgIdentity()
                        .getEducationOrgIdentificationCode().size();
        System.out.println(sessionString1);

        SessionReferenceType sessionRef = SessionGenerator.getSessionReferenceType("stateOrganizationId",
                "sessionName");
        System.out.println(sessionRef);

    }

    public static SessionReferenceType getSessinReferenceType(Session session) {
        SessionReferenceType ref = new SessionReferenceType();
        SessionIdentityType identity = new SessionIdentityType();
        ref.setSessionIdentity(identity);
        identity.setSessionName(session.getSessionName());

        EducationalOrgReferenceType edOrgRef = EducationAgencyGenerator.generateReference(session
                .getEducationOrganizationReference().getEducationalOrgIdentity().getStateOrganizationId());
        identity.setEducationalOrgReference(edOrgRef);
        return ref;
    }

    public static SessionReferenceType getSessionReferenceType(
            String stateOrganizationId,
            String sessionName
    )
    {
        SessionReferenceType  ref = new SessionReferenceType();
        SessionIdentityType sessionIdentity = new SessionIdentityType();
        ref.setSessionIdentity(sessionIdentity);

        if (stateOrganizationId != null) {
            EducationalOrgReferenceType edOrgRef = EducationAgencyGenerator.generateReference(stateOrganizationId);
            sessionIdentity.setEducationalOrgReference(edOrgRef);
        }

        if(sessionName != null) sessionIdentity.setSessionName(sessionName);
        return ref;
    }

}
