package org.slc.sli.api.count;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountServiceImpl implements CountService {

    @Autowired
    private EdOrgHelper edOrgHelper;

    @Autowired
    private Repository<Entity> mongoEntityRepository;

	@Override
	public List<EducationOrganizationCount> find() {
		List<EducationOrganizationCount> counts = new ArrayList<EducationOrganizationCount>();
		Set<String> edOrgs = edOrgHelper.getDirectEdorgs();
		
		for (String edOrg : edOrgs) {
			counts.add(getCountsForEdOrg(edOrg));
		}
		
		return counts;
	}

	public EducationOrganizationCount findOne(String edOrgId) {
		return getCountsForEdOrg(edOrgId);
	}

	private EducationOrganizationCount getCountsForEdOrg(String edOrg) {
		EducationOrganizationCount count = new EducationOrganizationCount();
		Set<Entity> staffAssociations = getAssociations(edOrg, "educationOrganizationReference", "staffEducationOrganizationAssociation");
		Set<Entity> teacherAssociations = getAssociations(edOrg, "schoolId", "teacherSchoolAssociation");
		Set<Entity> studentAssociations = getAssociations(edOrg, "schoolId", "studentSchoolAssociation");
		
		Set<Entity> currentStaffAssociations = filterCurrent(staffAssociations);
		Set<Entity> currentTeacherAssociations = filterCurrent(teacherAssociations);
		Set<Entity> currentStudentAssociations = filterCurrent(studentAssociations);

		count.setTotalStaff(getUniqueCount(staffAssociations, "staffReference"));
		count.setTotalTeacher(getUniqueCount(teacherAssociations, "teacherId"));
		count.setTotalStudent(getUniqueCount(studentAssociations, "studentId"));
		count.setTotalNonTeacher(count.getTotalStaff() - count.getTotalTeacher());

		count.setCurrentStaff(getUniqueCount(currentStaffAssociations, "staffReference"));
		count.setCurrentTeacher(getUniqueCount(currentTeacherAssociations, "teacherId"));
		count.setCurrentStudent(getUniqueCount(currentStudentAssociations, "studentId"));
		count.setCurrentNonTeacher(count.getCurrentStaff() - count.getCurrentTeacher());

		return count;
	}

	private Set<Entity> getAssociations(String edOrg, String edOrgField, String collectionName) {
		Set<Entity> associations = new HashSet<Entity>();
		NeutralQuery neutralQuery = new NeutralQuery();
		neutralQuery.addCriteria(new NeutralCriteria(edOrgField, NeutralCriteria.OPERATOR_EQUAL, edOrg));
		Iterable<Entity> itEntities = mongoEntityRepository.findAll(collectionName, neutralQuery);
		Iterator<Entity> it = itEntities.iterator();
		while (it.hasNext()) {
			Entity entity = (Entity) it.next();
			if ("teacherSchoolAssociation".equals(entity.getType())) {
				// Check if there is a matching staff Ed Org association before adding
				NeutralQuery staffNeutralQuery = new NeutralQuery();
				staffNeutralQuery.addCriteria(new NeutralCriteria("educationOrganizationReference", NeutralCriteria.OPERATOR_EQUAL, edOrg));
				staffNeutralQuery.addCriteria(new NeutralCriteria("staffReference", NeutralCriteria.OPERATOR_EQUAL, entity.getBody().get("teacherId")));
				Entity staffAssociation = mongoEntityRepository.findOne("staffEducationOrganizationAssociation", staffNeutralQuery);
				if (null != staffAssociation) {
					associations.add(entity);
				}
			} else {
				associations.add(entity);
			}
		}
		return associations;
	}

	private Set<Entity> filterCurrent(Set<Entity> associations) {
		Set<Entity> current = new HashSet<Entity>();

		String beginDateField = ParameterConstants.DEFAULT_BEGIN_DATE;
		String endDateField = ParameterConstants.DEFAULT_END_DATE;
		
		for (Entity entity : associations) {
			if (("studentSchoolAssociation").equals(entity.getType())) {
				beginDateField = ParameterConstants.STUDENT_SCHOOL_BEGIN_DATE;
				endDateField = ParameterConstants.STUDENT_SCHOOL_END_DATE;
			}

			if ("teacherSchoolAssociation".equals(entity.getType())) {
				// Check if there is a matching staff Ed Org association before adding
				NeutralQuery staffNeutralQuery = new NeutralQuery();
				staffNeutralQuery.addCriteria(new NeutralCriteria("educationOrganizationReference", NeutralCriteria.OPERATOR_EQUAL, entity.getBody().get("schoolId")));
				staffNeutralQuery.addCriteria(new NeutralCriteria("staffReference", NeutralCriteria.OPERATOR_EQUAL, entity.getBody().get("teacherId")));
				Entity staffAssociation = mongoEntityRepository.findOne("staffEducationOrganizationAssociation", staffNeutralQuery);
				if (null != staffAssociation && isCurrent(staffAssociation, beginDateField, endDateField)) {
					current.add(entity);
				}
			} else if (isCurrent(entity, beginDateField, endDateField)) {
				current.add(entity);
			}
		}
		return current;
	}

	private boolean isCurrent(Entity entity, String beginDateField, String endDateField) {
		boolean result = false;

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String today = df.format(new Date());

		Map<String, Object> body = entity.getBody();

		if (body.containsKey(beginDateField)
				&& body.containsKey(endDateField)
				&& body.get(beginDateField).toString().compareTo(today) <= 0
				&& body.get(endDateField).toString().compareTo(today) >= 0
				) {
			return true;
		} else if (body.containsKey(beginDateField)
				&& !body.containsKey(endDateField)
				&& body.get(beginDateField).toString().compareTo(today) <= 0
				) {
			return true;
		} else if (!body.containsKey(beginDateField)
				&& body.containsKey(endDateField)
				&& body.get(endDateField).toString().compareTo(today) >= 0
				) {
			return true;
		} else if (!body.containsKey(beginDateField)
				&& !body.containsKey(endDateField)
				) {
			return true;
		}

		return result;
	}

	private int getUniqueCount(Set<Entity> associations, String referenceField) {
		Set<String> unique = new HashSet<String>();
		
		for (Entity entity : associations) {
			unique.add((String) entity.getBody().get(referenceField));
		}

		return unique.size();
	}
}
