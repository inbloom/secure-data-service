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

package org.slc.sli.api.security.pdp;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.resolver.SectionHelper;
import org.slc.sli.domain.Entity;
import org.springframework.stereotype.Component;

@Component
public class ContextInferranceHelper {

    // @Resource(name = "validationRepo")
    // private Repository<Entity> repo;

    @Resource
    private EdOrgHelper edorger;

    @Resource
    private SectionHelper sectionHelper;

    public String getInferredUri(String resource, Entity user) {
        String result = null;
        String actorId = user.getEntityId();
        if (isTeacher(user)) {
            if (ResourceNames.LEARNINGSTANDARDS.equals(resource)) {
                result = "/" + resource;
            } else if (ResourceNames.LEARNINGSTANDARDS.equals(resource)) {
                result = "/" + resource;
            } else if (ResourceNames.LEARNINGOBJECTIVES.equals(resource)) {
                result = "/" + resource;
            } else if (ResourceNames.HOME.equals(resource)) {
                result = "/" + resource;
            } else if (ResourceNames.COMPETENCY_LEVEL_DESCRIPTOR_TYPES.equals(resource)) {
                result = "/" + resource;
            } else if (ResourceNames.COMPETENCY_LEVEL_DESCRIPTORS.equals(resource)) {
                result = "/" + resource;
            } else if (ResourceNames.ASSESSMENTS.equals(resource)) {
                result = "/" + resource;
            } else if (ResourceNames.STUDENT_SECTION_ASSOCIATIONS.equals(resource)) {
                result = String.format("/sections/%s/studentSectionAssociations",
                        StringUtils.join(sectionHelper.getTeachersSections(user), ","));
            } else if (ResourceNames.TEACHERS.equals(resource)) {
                result = String.format("/schools/%s/teacherSchoolAssociations/teachers",
                        StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS.equals(resource)) {
                result = String.format("/teachers/%s/teacherSchoolAssociations", user.getEntityId());
            } else if (ResourceNames.TEACHER_SECTION_ASSOCIATIONS.equals(resource)) {
                result = String.format("/teachers/%s/teacherSectionAssociations", user.getEntityId());
            } else if (ResourceNames.SECTIONS.equals(resource)) {
                result = String.format("/teachers/%s/teacherSectionAssociations/sections", user.getEntityId());
            } else if (ResourceNames.COHORTS.equals(resource)) {
                result = String.format("/staff/%s/staffCohortAssociations/cohorts", actorId);
            }
            // 4035 or 4036 endpoints to be implemented
            /*
             * } else if (ResourceNames.ATTENDANCES.equals(resource)) {
             * result = String.format("/sections/%s/students/attendances",
             * StringUtils.join(sectionHelper.getTeachersSections(user)));
             * } else if (ResourceNames.COURSE_TRANSCRIPTS.equals(resource)) {
             * result = String.format("/sections/%s/students/courseTranscripts",
             * StringUtils.join(sectionHelper.getTeachersSections(user)));
             * } else if (ResourceNames.GRADES.equals(resource)) {
             * result = String.format("/sections/%s/studentSectionAssociations/students/grades",
             * StringUtils.join(sectionHelper.getTeachersSections(user)));
             * } else if (ResourceNames.PARENTS.equals(resource)) {
             * result = String.format("/sections/%s/students/grades",
             * StringUtils.join(sectionHelper.getTeachersSections(user)));
             * } else if (ResourceNames.REPORT_CARDS.equals(resource)) {
             * result = String.format("/sections/%s/students/reportCards",
             * StringUtils.join(sectionHelper.getTeachersSections(user)));
             * } else if (ResourceNames.STUDENT_ACADEMIC_RECORDS.equals(resource)) {
             * result = String.format("/sections/%s/students/studentAcademicRecords",
             * StringUtils.join(sectionHelper.getTeachersSections(user)));
             * } else if ("studentAssessments".equals(resource)) {
             * result = String.format("/sections/%s/students/studentAssessments",
             * StringUtils.join(sectionHelper.getTeachersSections(user)));
             * } else if (ResourceNames.STUDENT_COMPETENCIES.equals(resource)) {
             * result = String.format("/sections/%s/studentCompetencies",
             * StringUtils.join(sectionHelper.getTeachersSections(user)));
             * } else if (ResourceNames.STUDENT_GRADEBOOK_ENTRIES.equals(resource)) {
             * result = String.format("/sections/%s/students/studentGradebookEntries",
             * StringUtils.join(sectionHelper.getTeachersSections(user)));
             * } else if (ResourceNames.STUDENT_PARENT_ASSOCIATIONS.equals(resource)) {
             * result = String.format("/sections/%s/students/studentParentAssociations",
             * StringUtils.join(sectionHelper.getTeachersSections(user)));
             * }
             */else if (ResourceNames.STAFF_COHORT_ASSOCIATIONS.equals(resource)) {
                result = String.format("/staff/%s/staffCohortAssociations",
                        user.getEntityId());
            } else if (ResourceNames.COURSES.equals(resource)) {
                result = String.format("/schools/%s/courses",
                        StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.COURSE_OFFERINGS.equals(resource)) {
                result = String.format("/schools/%s/courseOfferings",
                        StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.DISCIPLINE_ACTIONS.equals(resource)) {
                result = String.format("/staff/%s/disciplineActions", user.getEntityId());
            } else if (ResourceNames.DISCIPLINE_INCIDENTS.equals(resource)) {
                result = String.format("/staff/%s/disciplineIncidents", user.getEntityId());
            } else if (ResourceNames.EDUCATION_ORGANIZATIONS.equals(resource)) {
                result = String.format("/teachers/%s/teacherSchoolAssociations/schools",
                        user.getEntityId());
            } else if (ResourceNames.GRADING_PERIODS.equals(resource)) {
                result = String.format("/schools/%s/sessions/gradingPeriods",
                        StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.GRADEBOOK_ENTRIES.equals(resource)) {
                result = String.format("/sections/%s/gradebookEntries",
                        StringUtils.join(sectionHelper.getTeachersSections(user), ","));
            } else if (ResourceNames.SESSIONS.equals(resource)) {
                result = String.format("/schools/%s/sessions",
                        StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS.equals(resource)) {
                result = String.format("/staff/%s/disciplineIncidents/studentDisciplineIncidentAssociations",
                        user.getEntityId());
            } else if (ResourceNames.STUDENT_COMPETENCY_OBJECTIVES.equals(resource)) {
                result = String.format("/educationOrganizations/%s/studentCompetencyObjectives",
                        StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
            }
            


        } else if (isStaff(user)) {
            if (ResourceNames.LEARNINGSTANDARDS.equals(resource)) {
                result = "/" + resource;
            } else if (ResourceNames.LEARNINGSTANDARDS.equals(resource)) {
                result = "/" + resource;
            } else if (ResourceNames.LEARNINGOBJECTIVES.equals(resource)) {
                result = "/" + resource;
            } else if (ResourceNames.HOME.equals(resource)) {
                result = "/" + resource;
            } else if (ResourceNames.COMPETENCY_LEVEL_DESCRIPTOR_TYPES.equals(resource)) {
                result = "/" + resource;
            } else if (ResourceNames.COMPETENCY_LEVEL_DESCRIPTORS.equals(resource)) {
                result = "/" + resource;
            } else if (ResourceNames.ASSESSMENTS.equals(resource)) {
                result = "/" + resource;
            } else if (ResourceNames.COHORTS.equals(resource)) {
                result = String.format("/staff/%s/staffCohortAssociations/cohorts", actorId);
            } else if (ResourceNames.TEACHERS.equals(resource)) {
                String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
                result = String.format("/schools/%s/teacherSchoolAssociations/teachers", ids);
            } else if (ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS.equals(resource)) {
                String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
                result = String.format("/schools/%s/teacherSchoolAssociations", ids);
            } else if (ResourceNames.SECTIONS.equals(resource)) {
                String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
                result = String.format("/schools/%s/sections", ids);
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
            } else if (ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS.equals(resource)) {
                String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
                result = String.format("/schools/%s/studentSchoolAssociations", ids);
            } else if (ResourceNames.PROGRAMS.equals(resource)) {
                result = String.format("/staff/%s/staffProgramAssociations/programs", actorId);
            }

            /*
             * 4035 or 4036 endpoints to be implemented
             * } else if (ResourceNames.TEACHER_SECTION_ASSOCIATIONS.equals(resource)) {
             * String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
             * result = String.format("/schools/%s/teacherSectionAssociations", ids);
             * } else if (ResourceNames.STUDENT_SECTION_ASSOCIATIONS.equals(resource)) {
             * String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
             * result =
             * String.format("/schools/%s/studentSchoolAssociations/students/studentSectionAssociations"
             * , ids);
             * } else if (ResourceNames.COURSE_OFFERINGS.equals(resource)) {
             * String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
             * result = String.format("/schools/%s/courseOfferings", ids);
             * } else if (ResourceNames.COURSES.equals(resource)) {
             * String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
             * result = String.format("/schools/%s/courses", ids);
             * } else if (ResourceNames.GRADEBOOK_ENTRIES.equals(resource)) {
             * String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
             * result = String.format("/schools/%s/section/gradebookEntries", ids);
             * } else if (ResourceNames.SESSIONS.equals(resource)) {
             * String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
             * result = String.format("/schools/%s/sessions", ids);
             * } else if (ResourceNames.ATTENDANCES.equals(resource)) {
             * String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
             * result = String.format("/schools/%s/students/attendances", ids);
             * } else if ("courseTranscripts".equals(resource)) {
             * String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
             * result = String.format("/schools/%s/students/courseTranscripts", ids);
             * } else if (ResourceNames.GRADES.equals(resource)) {
             * String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
             * result = String.format("/schools/%s/studentSchoolAssociations/students/grades", ids);
             * } else if (ResourceNames.PARENTS.equals(resource)) {
             * String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
             * result = String.format("/schools/%s/students/parents", ids);
             * } else if (ResourceNames.REPORT_CARDS.equals(resource)) {
             * String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
             * result = String.format("/schools/%s/students/reportCards", ids);
             * } else if (ResourceNames.STUDENT_ACADEMIC_RECORDS.equals(resource)) {
             * String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
             * result = String.format("/schools/%s/students/studentAcademicRecords", ids);
             * } else if (ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS.equals(resource)) {
             * String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
             * result = String.format("/schools/%s/students/studentAssessments ", ids);
             * } else if (ResourceNames.STUDENT_COMPETENCIES.equals(resource)) {
             * String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
             * result = String.format("/schools/%s/students/studentCompetencies", ids);
             * } else if (ResourceNames.STUDENT_GRADEBOOK_ENTRIES.equals(resource)) {
             * String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
             * result = String.format("/schools/%s/students/studentGradebookEntries ", ids);
             * } else if (ResourceNames.STUDENT_PARENT_ASSOCIATIONS.equals(resource)) {
             * String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
             * result = String.format("/schools/%s/students/studentParentAssociations", ids);
             * }
             */else if (ResourceNames.COURSE_OFFERINGS.equals(resource)) {
                result = String.format("/schools/%s/courseOfferings",
                        StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.COURSES.equals(resource)) {
                result = String.format("/schools/%s/courses",
                        StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.DISCIPLINE_ACTIONS.equals(resource)) {
                result = String.format("/staff/%s/disciplineActions", user.getEntityId());
            } else if (ResourceNames.DISCIPLINE_INCIDENTS.equals(resource)) {
                result = String.format("/staff/%s/disciplineIncidents", user.getEntityId());
            } else if (ResourceNames.EDUCATION_ORGANIZATIONS.equals(resource)) {
                result = String.format("/staff/%s/staffEducationOrgAssignmentAssociations/educationOrganizations",
                        user.getEntityId());
            } else if (ResourceNames.GRADING_PERIODS.equals(resource)) {
                result = String.format("/schools/%s/sessions/gradingPeriods",
                        StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.GRADEBOOK_ENTRIES.equals(resource)) {
                result = String.format("/schools/%s/sections/gradebookEntries",
                        StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.SESSIONS.equals(resource)) {
                result = String.format("/schools/%s/sessions",
                        StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS.equals(resource)) {
                result = String.format("/staff/%s/disciplineIncidents/studentDisciplineIncidentAssociations",
                        user.getEntityId());
            } else if (ResourceNames.STUDENT_COMPETENCY_OBJECTIVES.equals(resource)) {
                result = String.format("/educationOrganizations/%s/studentCompetencyObjectives",
                        StringUtils.join(edorger.getDirectEdOrgAssociations(user), ","));
            }

        }

        return result;
    }

    private boolean isTeacher(Entity principal) {
        return principal.getType().equals(EntityNames.TEACHER);
    }

    private boolean isStaff(Entity principal) {
        return principal.getType().equals(EntityNames.STAFF);
    }
}
