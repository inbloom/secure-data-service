package org.slc.sli.api.security.pdp;

import org.apache.commons.lang3.StringUtils;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.resolver.SectionHelper;
import org.slc.sli.domain.Entity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ContextInferranceHelper {

	// @Resource(name = "validationRepo")
	// private Repository<Entity> repo;

	@Resource
	private EdOrgHelper edorger;

    @Resource
    private SectionHelper sectionHelper;


    public String getInferredUri(String resource, Entity user) {

        String result = "/" + resource;

        String actorId = user.getEntityId();
        if (isTeacher(user)) {
            if(ResourceNames.ATTENDANCES.equals(resource)) {
                result = String.format("/sections/%s/students/attendances",
                        StringUtils.join(sectionHelper.getTeachersSections(user)));
            } else if ("/courseTranscripts".equals(resource)) {
                result = String.format("/sections/%s/students/courseTranscripts",
                        StringUtils.join(sectionHelper.getTeachersSections(user)));
            } else if (ResourceNames.GRADES.equals(resource)) {
                result = String.format("/sections/%s/studentSectionAssociations/students/grades",
                        StringUtils.join(sectionHelper.getTeachersSections(user)));
            } else if (ResourceNames.PARENTS.equals(resource)) {
                result = String.format("/sections/%s/students/grades",
                        StringUtils.join(sectionHelper.getTeachersSections(user)));
            } else if (ResourceNames.REPORT_CARDS.equals(resource)) {
                result = String.format("/sections/%s/students/reportCards",
                        StringUtils.join(sectionHelper.getTeachersSections(user)));
            } else if (ResourceNames.STUDENT_ACADEMIC_RECORDS.equals(resource)) {
                result = String.format("/sections/%s/students/studentAcademicRecords",
                        StringUtils.join(sectionHelper.getTeachersSections(user)));
            } else if ("studentAssessments".equals(resource)) {
                result = String.format("/sections/%s/students/studentAssessments",
                        StringUtils.join(sectionHelper.getTeachersSections(user)));
            } else if (ResourceNames.STUDENT_COMPETENCIES.equals(resource)) {
                result = String.format("/sections/%s/studentCompetencies",
                        StringUtils.join(sectionHelper.getTeachersSections(user)));
            } else if (ResourceNames.STUDENT_GRADEBOOK_ENTRIES.equals(resource)) {
                result = String.format("/sections/%s/students/studentGradebookEntries",
                        StringUtils.join(sectionHelper.getTeachersSections(user)));
            } else if (ResourceNames.STUDENT_PARENT_ASSOCIATIONS.equals(resource)) {
                result = String.format("/sections/%s/students/studentParentAssociations",
                        StringUtils.join(sectionHelper.getTeachersSections(user)));
            }

		} else if(isStaff(user)) {
			if ("cohorts".equals(resource)) {
				result = String.format("/staff/%s/staffCohortAssociations/cohorts", actorId);
			} else if ("sections".equals(resource)) {
				String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
				result = String.format("/schools/%s/sections", ids);
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
