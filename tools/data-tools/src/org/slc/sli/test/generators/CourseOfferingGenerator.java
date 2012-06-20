package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.CourseIdentityType;
import org.slc.sli.test.edfi.entities.CourseOffering;
import org.slc.sli.test.edfi.entities.CourseReferenceType;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.SessionIdentityType;
import org.slc.sli.test.edfi.entities.SessionReferenceType;
import org.slc.sli.test.edfi.entities.meta.CourseOfferingMeta;

public class CourseOfferingGenerator {
    public static CourseOffering generate(CourseOfferingMeta courseOfferingMeta) {
        CourseOffering courseOffering = new CourseOffering();
        courseOffering.setLocalCourseCode(courseOfferingMeta.id);

        courseOffering.setLocalCourseTitle(courseOfferingMeta.id + "-title");

        EducationalOrgIdentityType eoit = new EducationalOrgIdentityType();
        eoit.setStateOrganizationId(courseOfferingMeta.schoolId.id);
        EducationalOrgReferenceType eort = new EducationalOrgReferenceType();
        eort.setEducationalOrgIdentity(eoit);
        courseOffering.setSchoolReference(eort);

        SessionIdentityType sit = new SessionIdentityType();
        sit.setSessionName(courseOfferingMeta.sessionMeta.id);
        SessionReferenceType srt = new SessionReferenceType();
        srt.setSessionIdentity(sit);
        courseOffering.setSessionReference(srt);

        CourseIdentityType cit = new CourseIdentityType();
        cit.getCourseCode().addAll(courseOfferingMeta.courseMeta.courseCodes);
        CourseReferenceType crt = new CourseReferenceType();
        crt.setCourseIdentity(cit);
        courseOffering.setCourseReference(crt);

        return courseOffering;
    }

    public static CourseOffering generateLowFi(CourseOfferingMeta courseOfferingMeta) {
        return generate(courseOfferingMeta);
    }
}
