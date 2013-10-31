package org.slc.sli.bulk.extract.date;

import junit.framework.Assert;

import org.junit.Test;

import org.slc.sli.common.constants.EntityNames;

public class ExtractVerifierFactoryTest {

    private ExtractVerifierFactory factory = new ExtractVerifierFactory();

    @SuppressWarnings("static-access")
    @Test
    public void test() {
        AttendanceExtractVerifier attendanceExtractVerifier = new AttendanceExtractVerifier();
        DatelessExtractVerifier datelessExtractVerifier = new DatelessExtractVerifier();
        PathExtractVerifier pathExtractVerifier = new PathExtractVerifier();
        SimpleExtractVerifier simpleExtractVerifier = new SimpleExtractVerifier();
        TeacherSchoolAssociationExtractVerifier teacherSchoolAssociationExtractVerifier = new TeacherSchoolAssociationExtractVerifier();

        factory.setAttendanceExtractVerifier(attendanceExtractVerifier);
        factory.setDatelessExtractVerifier(datelessExtractVerifier);
        factory.setPathExtractVerifier(pathExtractVerifier);
        factory.setSimpleExtractVerifier(simpleExtractVerifier);
        factory.setTeacherSchoolAssociationExtractVerifier(teacherSchoolAssociationExtractVerifier);

        Assert.assertEquals(attendanceExtractVerifier, factory.retrieveExtractVerifier(EntityNames.ATTENDANCE));
        Assert.assertEquals(datelessExtractVerifier, factory.retrieveExtractVerifier(EntityNames.STUDENT));
        Assert.assertEquals(pathExtractVerifier, factory.retrieveExtractVerifier(EntityNames.STUDENT_GRADEBOOK_ENTRY));
        Assert.assertEquals(simpleExtractVerifier, factory.retrieveExtractVerifier(EntityNames.GRADE));
        Assert.assertEquals(teacherSchoolAssociationExtractVerifier, factory.retrieveExtractVerifier(EntityNames.TEACHER_SCHOOL_ASSOCIATION));
   }

}
