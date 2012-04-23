package org.slc.sli.test.generators;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import org.slc.sli.test.edfi.entities.AttendanceEvent;
import org.slc.sli.test.edfi.entities.AttendanceEventCategoryType;
import org.slc.sli.test.edfi.entities.AttendanceEventType;
import org.slc.sli.test.edfi.entities.EducationalEnvironmentType;
import org.slc.sli.test.edfi.entities.SectionIdentityType;
import org.slc.sli.test.edfi.entities.SectionReferenceType;

public class AttendanceEventGenerator {

    private static final boolean INCLUDE_OPTIONAL_DATA = false;

    private static final Random RANDOM = new Random();
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    private static Calendar calendar = new GregorianCalendar(2012, 0, 1);

    public static AttendanceEvent generateLowFi(String studentID, String schoolID, String sectionCode) {
        AttendanceEvent ae = new AttendanceEvent();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        ae.setEventDate(DATE_FORMATTER.format(calendar.getTime()));
        ae.setAttendanceEventCategory(getAttendanceEventCategoryType());
        ae.setStudentReference(StudentGenerator.getStudentReferenceType(studentID));

        if (INCLUDE_OPTIONAL_DATA) {
            ae.setAttendanceEventType(getAttendanceEventType());
            ae.setAttendanceEventReason("My dog ate my homework.");
            ae.setEducationalEnvironment(getEducationalEnvironmentType());

            SectionReferenceType sectionReferenceType = new SectionReferenceType();
            SectionIdentityType sectionIdentityType = new SectionIdentityType();
            sectionIdentityType.setUniqueSectionCode(sectionCode);
            sectionIdentityType.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolID);
            sectionReferenceType.setSectionIdentity(sectionIdentityType);
            ae.setSectionReference(sectionReferenceType);
        }

        return ae;
    }

    public static AttendanceEventType getAttendanceEventType() {
        return AttendanceEventType.values()[RANDOM.nextInt(AttendanceEventType.values().length)];
    }

    public static AttendanceEventCategoryType getAttendanceEventCategoryType() {
        return AttendanceEventCategoryType.values()[RANDOM.nextInt(AttendanceEventCategoryType.values().length)];
    }

    public static EducationalEnvironmentType getEducationalEnvironmentType() {
        return EducationalEnvironmentType.values()[RANDOM.nextInt(EducationalEnvironmentType.values().length)];
    }

    public static void resetCalendar() {
        calendar = new GregorianCalendar(2012, 0, 1);
    }

}
