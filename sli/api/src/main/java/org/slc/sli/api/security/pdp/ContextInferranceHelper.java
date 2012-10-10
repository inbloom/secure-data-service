package org.slc.sli.api.security.pdp;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.resolver.SectionHelper;
import org.slc.sli.domain.Entity;

@Component
public class ContextInferranceHelper {

	@Resource
	private EdOrgHelper edorger;

	@Resource
	private SectionHelper sectionHelper;

	public String getInferredUri(String resource, Entity user) {
		String result = null;
		String actorId = user.getEntityId();
		if (isTeacher(user)) {
			if (ResourceNames.ASSESSMENTS.equals(resource)) {
				result = "/" + resource;
			} else if (ResourceNames.ATTENDANCES.equals(resource)) {
				result = String.format("/sections/%s/studentSectionAssociations/students/attendances", StringUtils.join(sectionHelper.getTeachersSections(user), ","));
			} else if (ResourceNames.COHORTS.equals(resource)) {
				result = String.format("/staff/%s/staffCohortAssociations/cohorts", actorId);
			} else if (ResourceNames.COMPETENCY_LEVEL_DESCRIPTORS.equals(resource)) {
				result = "/" + resource;
			} else if (ResourceNames.PARENTS.equals(resource)) {
				result = String.format("/sections/%s/studentSectionAssociations/students/studentParentAssociations/parents", StringUtils.join(sectionHelper.getTeachersSections(user), ","));
			} else if (ResourceNames.COMPETENCY_LEVEL_DESCRIPTOR_TYPES.equals(resource)) {
				result = "/" + resource;
			} else if (ResourceNames.COURSES.equals(resource)) {
				result = String.format("/schools/%s/courses", StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
			} else if (ResourceNames.COURSE_OFFERINGS.equals(resource)) {
				result = String.format("/schools/%s/courseOfferings", StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
			} else if (ResourceNames.COURSE_TRANSCRIPTS.equals(resource)) {
				String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
				result = String.format("/sections/%s/studentSectionAssociations/students/courseTranscripts", ids);
			} else if (ResourceNames.DISCIPLINE_ACTIONS.equals(resource)) {
				result = String.format("/staff/%s/disciplineActions", actorId);
			} else if (ResourceNames.DISCIPLINE_INCIDENTS.equals(resource)) {
				result = String.format("/staff/%s/disciplineIncidents", actorId);
			} else if (ResourceNames.EDUCATION_ORGANIZATIONS.equals(resource)) {
				result = String.format("/teachers/%s/teacherSchoolAssociations/schools", actorId);
			} else if (ResourceNames.GRADES.equals(resource)) {
				String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
				result = String.format("/sections/%s/studentSectionAssociations/students/grades", ids);
			} else if (ResourceNames.GRADING_PERIODS.equals(resource)) {
				result = String.format("/schools/%s/sessions/gradingPeriods", StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
			} else if (ResourceNames.GRADEBOOK_ENTRIES.equals(resource)) {
				result = String.format("/sections/%s/gradebookEntries", StringUtils.join(sectionHelper.getTeachersSections(user), ","));
			} else if (ResourceNames.HOME.equals(resource)) {
				result = "/" + resource;
			} else if (ResourceNames.LEARNINGOBJECTIVES.equals(resource)) {
				result = "/" + resource;
			} else if (ResourceNames.LEARNINGSTANDARDS.equals(resource)) {
				result = "/" + resource;
			} else if (ResourceNames.REPORT_CARDS.equals(resource)) {
				String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
				result = String.format("/sections/%s/studentSectionAssociations/students/reportCards", ids);
			} else if (ResourceNames.SECTIONS.equals(resource)) {
				result = String.format("/teachers/%s/teacherSectionAssociations/sections", actorId);
			} else if (ResourceNames.SESSIONS.equals(resource)) {
				result = String.format("/schools/%s/sessions", StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
			} else if (ResourceNames.STAFF_COHORT_ASSOCIATIONS.equals(resource)) {
				result = String.format("/staff/%s/staffCohortAssociations", actorId);
			} else if (ResourceNames.STUDENT_ACADEMIC_RECORDS.equals(resource)) {
				String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
				result = String.format("/sections/%s/studentSectionAssociations/students/studentAcademicRecords", ids);
			} else if (ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS.equals(resource)) {
				String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
				result = String.format("/sections/%s/studentSectionAssociations/students/studentAssessments ", ids);
			} else if (ResourceNames.STUDENT_COHORT_ASSOCIATIONS.equals(resource)) {
				result = String.format("/staff/%s/staffCohortAssociations/cohorts/studentCohortAssociations", actorId);
			} else if (ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS.equals(resource)) {
				result = String.format("/staff/%s/disciplineIncidents/studentDisciplineIncidentAssociations", actorId);
			} else if (ResourceNames.STUDENT_COMPETENCIES.equals(resource)) {
				String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
				result = String.format("/sections/%s/studentSectionAssociations/students/studentCompetencies", ids);
			} else if (ResourceNames.STUDENT_COMPETENCY_OBJECTIVES.equals(resource)) {
				result = String.format("/educationOrganizations/%s/studentCompetencyObjectives", StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
			} else if (ResourceNames.STUDENT_GRADEBOOK_ENTRIES.equals(resource)) {
				String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
				result = String.format("/sections/%s/studentSectionAssociations/students/studentGradebookEntries ", ids);
			} else if (ResourceNames.STUDENT_PARENT_ASSOCIATIONS.equals(resource)) {
				String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
				result = String.format("/ections/%s/studentSectionAssociations/students/studentParentAssociations", ids);
			} else if (ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS.equals(resource)) {
				result = String.format("/staff/%s/staffProgramAssociations/programs/studentParentAssociations", actorId);
			} else if (ResourceNames.STUDENT_SECTION_ASSOCIATIONS.equals(resource)) {
				result = String.format("/sections/%s/studentSectionAssociations", StringUtils.join(sectionHelper.getTeachersSections(user), ","));
			} else if (ResourceNames.TEACHERS.equals(resource)) {
				result = String.format("/schools/%s/teacherSchoolAssociations/teachers", StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
			} else if (ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS.equals(resource)) {
				result = String.format("/teachers/%s/teacherSchoolAssociations", actorId);
			} else if (ResourceNames.TEACHER_SECTION_ASSOCIATIONS.equals(resource)) {
				result = String.format("/teachers/%s/teacherSectionAssociations", actorId);
			}
		} else if (isStaff(user)) {
			if (ResourceNames.ASSESSMENTS.equals(resource)) {
				result = "/" + resource;
			} else if (ResourceNames.ATTENDANCES.equals(resource)) {
				String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
				result = String.format("/schools/%s/studentSchoolAssociations/students/attendances", ids);
			} else if (ResourceNames.COMPETENCY_LEVEL_DESCRIPTORS.equals(resource)) {
				result = "/" + resource;
			} else if (ResourceNames.COMPETENCY_LEVEL_DESCRIPTOR_TYPES.equals(resource)) {
				result = "/" + resource;
			} else if (ResourceNames.COHORTS.equals(resource)) {
				result = String.format("/staff/%s/staffCohortAssociations/cohorts", actorId);
			} else if (ResourceNames.COURSES.equals(resource)) {
				result = String.format("/schools/%s/courses", StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
			} else if (ResourceNames.COURSE_OFFERINGS.equals(resource)) {
				result = String.format("/schools/%s/courseOfferings", StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
			} else if (ResourceNames.COURSE_TRANSCRIPTS.equals(resource)) {
				String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
				result = String.format("/schools/%s/studentSchoolAssociations/students/courseTranscripts", ids);
			} else if (ResourceNames.DISCIPLINE_ACTIONS.equals(resource)) {
				result = String.format("/staff/%s/disciplineActions", actorId);
			} else if (ResourceNames.DISCIPLINE_INCIDENTS.equals(resource)) {
				result = String.format("/staff/%s/disciplineIncidents", actorId);
			} else if (ResourceNames.EDUCATION_ORGANIZATIONS.equals(resource)) {
				result = String.format("/staff/%s/staffEducationOrgAssignmentAssociations/educationOrganizations", actorId);
			} else if (ResourceNames.GRADES.equals(resource)) {
				String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
				result = String.format("/schools/%s/studentSchoolAssociations/students/grades", ids);
			} else if (ResourceNames.GRADING_PERIODS.equals(resource)) {
				result = String.format("/schools/%s/sessions/gradingPeriods", StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
			} else if (ResourceNames.GRADEBOOK_ENTRIES.equals(resource)) {
				result = String.format("/schools/%s/sections/gradebookEntries", StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
			} else if (ResourceNames.HOME.equals(resource)) {
				result = "/" + resource;
			} else if (ResourceNames.LEARNINGOBJECTIVES.equals(resource)) {
				result = "/" + resource;
			} else if (ResourceNames.LEARNINGSTANDARDS.equals(resource)) {
				result = "/" + resource;
			} else if (ResourceNames.PARENTS.equals(resource)) {
				String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
				result = String.format("/schools/%s/students/parents", ids);
			} else if (ResourceNames.PROGRAMS.equals(resource)) {
				result = String.format("/staff/%s/staffProgramAssociations/programs", actorId);
			} else if (ResourceNames.REPORT_CARDS.equals(resource)) {
				String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
				result = String.format("/schools/%s/studentSchoolAssociations/students/reportCards", ids);
			} else if (ResourceNames.SECTIONS.equals(resource)) {
				String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
				result = String.format("/schools/%s/sections", ids);
			} else if (ResourceNames.SESSIONS.equals(resource)) {
				result = String.format("/schools/%s/sessions", StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
			} else if (ResourceNames.STAFF.equals(resource)) {
				String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
				result = String.format("/educationOrganizations/%s/staffEducationOrgAssignmentAssociations/staff", ids);
			} else if (ResourceNames.STAFF_COHORT_ASSOCIATIONS.equals(resource)) {
				result = String.format("/staff/%s/staffCohortAssociations", actorId);
			} else if (ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS.equals(resource)) {
				result = String.format("/staff/%s/staffEducationOrgAssignmentAssociations", actorId);
			} else if (ResourceNames.STAFF_PROGRAM_ASSOCIATIONS.equals(resource)) {
				result = String.format("/staff/%s/staffProgramAssociations", actorId);
			} else if (ResourceNames.STUDENTS.equals(resource)) {
				String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
				result = String.format("/schools/%s/studentSchoolAssociations/students", ids);
			} else if (ResourceNames.STUDENT_ACADEMIC_RECORDS.equals(resource)) {
				String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
				result = String.format("/schools/%s/studentSchoolAssociations/students/studentAcademicRecords", ids);
			} else if (ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS.equals(resource)) {
				String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
				result = String.format("/schools/%s/studentSchoolAssociations/students/studentAssessments ", ids);
			} else if (ResourceNames.STUDENT_COHORT_ASSOCIATIONS.equals(resource)) {
				String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
				result = String.format("/staff/%s/staffCohortAssociations/cohorts/studentCohortAssociations", ids);
			} else if (ResourceNames.STUDENT_COMPETENCIES.equals(resource)) {
				String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
				result = String.format("/schools/%s/studentSchoolAssociations/students/studentCompetencies", ids);
			} else if (ResourceNames.STUDENT_COMPETENCY_OBJECTIVES.equals(resource)) {
				result = String.format("/educationOrganizations/%s/studentCompetencyObjectives", StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
			} else if (ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS.equals(resource)) {
				result = String.format("/staff/%s/disciplineIncidents/studentDisciplineIncidentAssociations", actorId);
			} else if (ResourceNames.STUDENT_GRADEBOOK_ENTRIES.equals(resource)) {
				String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
				result = String.format("/schools/%s/studentSchoolAssociations/students/studentGradebookEntries ", ids);
			} else if (ResourceNames.STUDENT_PARENT_ASSOCIATIONS.equals(resource)) {
				String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
				result = String.format("/schools/%s/studentSchoolAssociations/students/studentParentAssociations", ids);
			} else if (ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS.equals(resource)) {
				result = String.format("/staff/%s/staffProgramAssociations/programs/studentParentAssociations", actorId);
			} else if (ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS.equals(resource)) {
				String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
				result = String.format("/schools/%s/studentSchoolAssociations", ids);
			} else if (ResourceNames.TEACHERS.equals(resource)) {
				String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
				result = String.format("/schools/%s/teacherSchoolAssociations/teachers", ids);
			} else if (ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS.equals(resource)) {
				String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
				result = String.format("/schools/%s/teacherSchoolAssociations", ids);
			} else if (ResourceNames.PROGRAMS.equals(resource)) {
				result = String.format("/staff/%s/staffProgramAssociations/programs", actorId);
			} else if (ResourceNames.PARENTS.equals(resource)) {
				result = String.format("/schools/%s/studentSchoolAssociations/students/studentParentAssociations/parents", StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
			}

			/*
			 * Endpoints to be implemented: } else if (ResourceNames.TEACHER_SECTION_ASSOCIATIONS.equals(resource)) {
			 * String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","); result =
			 * String.format("/schools/%s/teacherSectionAssociations", ids); } else if
			 * (ResourceNames.STUDENT_SECTION_ASSOCIATIONS.equals(resource)) { String ids =
			 * StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","); result =
			 * String.format("/schools/%s/studentSchoolAssociations/students/studentSectionAssociations" , ids); }
			 */
		}
		return result;
	}

	private boolean isTeacher(Entity principal) {
		return principal.getType().equals(EntityNames.TEACHER);
	}

	private boolean isStaff(Entity principal) {
		return principal.getType().equals(EntityNames.STAFF);
	}

	protected void setSectionHelper(SectionHelper sectionHelper) {
		this.sectionHelper = sectionHelper;
	}

	protected void setEdOrgHelper(EdOrgHelper edOrgHelper) {
		this.edorger = edOrgHelper;
	}
}
