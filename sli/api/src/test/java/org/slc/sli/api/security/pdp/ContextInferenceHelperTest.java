package org.slc.sli.api.security.pdp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.resolver.SectionHelper;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;

/**
 * Tests for ContextInferenceHelper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class ContextInferenceHelperTest {

    @Autowired
    ContextInferenceHelper contextInferranceHelper;

    private SectionHelper sectionHelper;
    private EdOrgHelper edOrgHelper;

    private Entity teacher;
    private Entity staff;

    @Before
    public void setup() throws Exception {
        teacher = mock(Entity.class);
        when(teacher.getType()).thenReturn(EntityNames.TEACHER);
        when(teacher.getEntityId()).thenReturn("teacher123");

        staff = mock(Entity.class);
        when(staff.getType()).thenReturn(EntityNames.STAFF);
        when(staff.getEntityId()).thenReturn("staff123");

        sectionHelper = mock(SectionHelper.class);
        when(sectionHelper.getTeachersSections(teacher)).thenReturn(Arrays.asList("section123"));

        edOrgHelper = mock(EdOrgHelper.class);
        when(edOrgHelper.getDirectEdOrgAssociations(teacher)).thenReturn(Arrays.asList("school123"));
        when(edOrgHelper.getDirectEdOrgAssociations(staff)).thenReturn(Arrays.asList("edOrg123"));

        contextInferranceHelper.setSectionHelper(sectionHelper);
        contextInferranceHelper.setEdOrgHelper(edOrgHelper);
    }

    @Test
    public void testGetInferredUrisForTeacher() throws Exception {
        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.ASSESSMENTS + " is incorrect.",
                "/assessments",
                contextInferranceHelper.getInferredUri(ResourceNames.ASSESSMENTS, teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.ATTENDANCES + " is incorrect.",
                "/sections/section123/studentSectionAssociations/students/attendances",
                contextInferranceHelper.getInferredUri(ResourceNames.ATTENDANCES, teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.COMPETENCY_LEVEL_DESCRIPTORS + " is incorrect.",
                "/competencyLevelDescriptor",
                contextInferranceHelper.getInferredUri(ResourceNames.COMPETENCY_LEVEL_DESCRIPTORS, teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.COMPETENCY_LEVEL_DESCRIPTOR_TYPES + " is incorrect.",
                "/competencyLevelDescriptorTypes",
                contextInferranceHelper.getInferredUri(ResourceNames.COMPETENCY_LEVEL_DESCRIPTOR_TYPES, teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.HOME + " is incorrect.",
                "/home",
                contextInferranceHelper.getInferredUri(ResourceNames.HOME, teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.LEARNINGSTANDARDS + " is incorrect.",
                "/learningStandards",
                contextInferranceHelper.getInferredUri(ResourceNames.LEARNINGSTANDARDS, teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.LEARNINGOBJECTIVES + " is incorrect.",
                "/learningObjectives",
                contextInferranceHelper.getInferredUri(ResourceNames.LEARNINGOBJECTIVES, teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.SECTIONS + " is incorrect.",
                "/teachers/teacher123/teacherSectionAssociations/sections",
                contextInferranceHelper.getInferredUri(ResourceNames.SECTIONS, teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.STUDENT_SECTION_ASSOCIATIONS + " is incorrect.",
                "/sections/section123/studentSectionAssociations",
                contextInferranceHelper.getInferredUri(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.TEACHERS + " is incorrect.",
                "/schools/school123/teacherSchoolAssociations/teachers",
                contextInferranceHelper.getInferredUri(ResourceNames.TEACHERS, teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS + " is incorrect.",
                "/teachers/teacher123/teacherSchoolAssociations",
                contextInferranceHelper.getInferredUri(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS, teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.TEACHER_SECTION_ASSOCIATIONS + " is incorrect.",
                "/teachers/teacher123/teacherSectionAssociations",
                contextInferranceHelper.getInferredUri(ResourceNames.TEACHER_SECTION_ASSOCIATIONS, teacher));
    }

    @Test
    public void testGetInferredUrisForStaff() throws Exception {
        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.ASSESSMENTS + " is incorrect.",
                "/assessments",
                contextInferranceHelper.getInferredUri(ResourceNames.ASSESSMENTS, staff));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.ATTENDANCES + " is incorrect.",
                "/schools/edOrg123/studentSchoolAssociations/students/attendances",
                contextInferranceHelper.getInferredUri(ResourceNames.ATTENDANCES, staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.COHORTS + " is incorrect.",
                "/staff/staff123/staffCohortAssociations/cohorts",
                contextInferranceHelper.getInferredUri(ResourceNames.COHORTS, staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.COMPETENCY_LEVEL_DESCRIPTORS + " is incorrect.",
                "/competencyLevelDescriptor",
                contextInferranceHelper.getInferredUri(ResourceNames.COMPETENCY_LEVEL_DESCRIPTORS, staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.COMPETENCY_LEVEL_DESCRIPTOR_TYPES + " is incorrect.",
                "/competencyLevelDescriptorTypes",
                contextInferranceHelper.getInferredUri(ResourceNames.COMPETENCY_LEVEL_DESCRIPTOR_TYPES, staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.HOME + " is incorrect.",
                "/home",
                contextInferranceHelper.getInferredUri(ResourceNames.HOME, staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.LEARNINGSTANDARDS + " is incorrect.",
                "/learningStandards",
                contextInferranceHelper.getInferredUri(ResourceNames.LEARNINGSTANDARDS, staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.LEARNINGOBJECTIVES + " is incorrect.",
                "/learningObjectives",
                contextInferranceHelper.getInferredUri(ResourceNames.LEARNINGOBJECTIVES, staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.PROGRAMS + " is incorrect.",
                "/staff/staff123/staffProgramAssociations/programs",
                contextInferranceHelper.getInferredUri(ResourceNames.PROGRAMS, staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.SECTIONS + " is incorrect.",
                "/schools/edOrg123/sections",
                contextInferranceHelper.getInferredUri(ResourceNames.SECTIONS, staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.STAFF + " is incorrect.",
                "/educationOrganizations/edOrg123/staffEducationOrgAssignmentAssociations/staff",
                contextInferranceHelper.getInferredUri(ResourceNames.STAFF, staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.STAFF_COHORT_ASSOCIATIONS + " is incorrect.",
                "/staff/staff123/staffCohortAssociations",
                contextInferranceHelper.getInferredUri(ResourceNames.STAFF_COHORT_ASSOCIATIONS, staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS + " is incorrect.",
                "/staff/staff123/staffEducationOrgAssignmentAssociations",
                contextInferranceHelper.getInferredUri(ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS, staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.STAFF_PROGRAM_ASSOCIATIONS + " is incorrect.",
                "/staff/staff123/staffProgramAssociations",
                contextInferranceHelper.getInferredUri(ResourceNames.STAFF_PROGRAM_ASSOCIATIONS, staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.STUDENTS + " is incorrect.",
                "/schools/edOrg123/studentSchoolAssociations/students",
                contextInferranceHelper.getInferredUri(ResourceNames.STUDENTS, staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS + " is incorrect.",
                "/schools/edOrg123/studentSchoolAssociations",
                contextInferranceHelper.getInferredUri(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.TEACHERS + " is incorrect.",
                "/schools/edOrg123/teacherSchoolAssociations/teachers",
                contextInferranceHelper.getInferredUri(ResourceNames.TEACHERS, staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS + " is incorrect.",
                "/schools/edOrg123/teacherSchoolAssociations",
                contextInferranceHelper.getInferredUri(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS, staff));
    }
}
