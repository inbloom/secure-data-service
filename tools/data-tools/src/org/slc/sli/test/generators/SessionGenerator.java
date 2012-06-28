package org.slc.sli.test.generators;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slc.sli.test.edfi.entities.EducationOrgIdentificationCode;
import org.slc.sli.test.edfi.entities.EducationOrgIdentificationSystemType;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.GradingPeriodIdentityType;
import org.slc.sli.test.edfi.entities.GradingPeriodReferenceType;
import org.slc.sli.test.edfi.entities.GradingPeriodType;
import org.slc.sli.test.edfi.entities.Ref;
import org.slc.sli.test.edfi.entities.ReferenceType;
import org.slc.sli.test.edfi.entities.Session;
import org.slc.sli.test.edfi.entities.SessionIdentityType;
import org.slc.sli.test.edfi.entities.SessionReferenceType;
import org.slc.sli.test.edfi.entities.TermType;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;

public class SessionGenerator {

    private String beginDate = "2011-03-04";
    private String endDate = "2012-03-04";
    Random random = new Random();

    public Session sessionGenerator(List<String> stateOrgIds) {
        Session session = new Session();
        session.setSessionName("2012 Spring");

        session.setSchoolYear("2011");

        session.setBeginDate(beginDate);
        session.setEndDate(endDate);
        session.setTerm(getTermType());
        int roll = 45 + (int) (Math.random() * (150 - 45));
        session.setTotalInstructionalDays(roll);

        EducationalOrgIdentityType eoit = new EducationalOrgIdentityType();
        EducationOrgIdentificationCode eoic = new EducationOrgIdentificationCode();
        for (String stateOrgId : stateOrgIds)
            eoic.setID(stateOrgId);
        eoit.getEducationOrgIdentificationCode().add(eoic);
        EducationalOrgReferenceType eort = new EducationalOrgReferenceType();
        eort.setEducationalOrgIdentity(eoit);
        session.setEducationOrganizationReference(eort);

        GradingPeriodIdentityType gpit = new GradingPeriodIdentityType();
        gpit.setGradingPeriod(getGradingPeriodType());
        gpit.setSchoolYear("2011-2012");
        // System.out.println("this is grading period Type :" +
        // gpit.getGradingPeriod());
        // System.out.println("this is school year Type :" +
        // gpit.getSchoolYear());
        for (String stateOrgId : stateOrgIds)
            gpit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(stateOrgId);
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

    public GradingPeriodType getGradingPeriodType() {
        int index = random.nextInt(GradingPeriodType.values().length);
        return GradingPeriodType.values()[index];
    }

    public TermType getTermType() {
        int index = random.nextInt(TermType.values().length);
        return TermType.values()[index];
    }

    public static Session generateMidFi(String id, String schoolId, List<String> calendarList, List<String> gradingPeriodList) {
        return generateLowFi(id, schoolId, calendarList, gradingPeriodList);
    }

    public static Session generateLowFi(String id, String schoolId, List<String> calendarList, List<String> gradingPeriodList) {
        Session session = new Session();
        Random random = new Random();

        session.setSessionName(id);
        session.setSchoolYear("2011-2012");
        session.setTerm(TermType.SPRING_SEMESTER);

        // TODO: set session range big enough to make sure all AttendanceEvents can fall into
        // one of the session.
        session.setBeginDate("2002-01-" + String.format("%02d", random.nextInt(30)+1));
        session.setEndDate("2011-01-" + String.format("%02d", random.nextInt(30)+1));

        session.setTotalInstructionalDays(120);

        // session.getGradingPeriodReference().add(new GradingPeriodReferenceType());

        // construct and add the school reference
        EducationalOrgIdentityType edOrgIdentityType = new EducationalOrgIdentityType();
        edOrgIdentityType.setStateOrganizationId(schoolId);

        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentityType);

        for (String cal : calendarList) {
            Ref calRef = new Ref(cal);
            ReferenceType ref = new ReferenceType();
            ref.setRef(calRef);
            session.getCalendarDateReference().add(ref);
        }

        for (int i = 0; i < MetaRelations.GRADING_PERIOD_PER_SESSIONS && i < gradingPeriodList.size(); i++) {
            Ref gpRef = new Ref(gradingPeriodList.get(i));
            GradingPeriodReferenceType gprt = new GradingPeriodReferenceType();
            gprt.setRef(gpRef);
            session.getGradingPeriodReference().add(gprt);
            session.setEducationOrganizationReference(schoolRef);
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
                + s.getEducationOrganizationReference().getEducationalOrgIdentity().getEducationOrgIdentificationCode()
                        .size();
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
                "educationOrgIdentificationCode_ID", "educationOrgIdentificationCode_IdentificationSystem",
                "schoolYear", "sessionName");
        System.out.println(sessionRef);

    }

    public static SessionReferenceType getSessinReferenceType(Session session) {
        SessionReferenceType ref = new SessionReferenceType();
        SessionIdentityType identity = new SessionIdentityType();
        ref.setSessionIdentity(identity);
        identity.setSchoolYear(session.getSchoolYear());
        identity.setSessionName(session.getSessionName());
        identity.setTerm(session.getTerm());

        identity.getStateOrganizationIdOrEducationOrgIdentificationCode().addAll(
                session.getEducationOrganizationReference().getEducationalOrgIdentity()
                        .getEducationOrgIdentificationCode());
        return ref;
    }

    public static SessionReferenceType getSessionReferenceType(String stateOrganizationId,
            String educationOrgIdentificationCode_ID, String educationOrgIdentificationCode_IdentificationSystem,
            String schoolYear, String sessionName) {
        SessionReferenceType ref = new SessionReferenceType();
        SessionIdentityType sessionIdentity = new SessionIdentityType();
        ref.setSessionIdentity(sessionIdentity);

        if (stateOrganizationId != null)
            sessionIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(stateOrganizationId);

        if (educationOrgIdentificationCode_ID != null) {
            EducationOrgIdentificationCode edOrgCode = new EducationOrgIdentificationCode();
            edOrgCode.setID(educationOrgIdentificationCode_ID);
            edOrgCode.setIdentificationSystem(EducationOrgIdentificationSystemType.SCHOOL);
            sessionIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(edOrgCode);
        }
        if (schoolYear != null)
            sessionIdentity.setSchoolYear(schoolYear);
        sessionIdentity.setTerm(TermType.YEAR_ROUND);
        if (sessionName != null)
            sessionIdentity.setSessionName(sessionName);
        return ref;
    }

}
