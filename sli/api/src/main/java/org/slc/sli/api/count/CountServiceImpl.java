package org.slc.sli.api.count;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		return getCountsForEdOrg(edOrg, true);
	}

	private EducationOrganizationCount getCountsForEdOrg(String edOrg, boolean recursive) {
		EducationOrganizationCount count = new EducationOrganizationCount();
		Set<Entity> staffAssociations = getAssociations(edOrg, "educationOrganizationReference", "staffEducationOrganizationAssociation");
		Set<Entity> teacherAssociations = getAssociations(edOrg, "schoolId", "teacherSchoolAssociation");
		Set<Entity> studentAssociations = getAssociations(edOrg, "schoolId", "studentSchoolAssociation");
		
		Set<Entity> currentStaffAssociations = filterCurrent(staffAssociations);
		Set<Entity> currentTeacherAssociations = filterCurrent(teacherAssociations);
		Set<Entity> currentStudentAssociations = filterCurrent(studentAssociations);

		if (recursive) {
			Set<String> childEdOrgs = edOrgHelper.getChildEdOrgs(edOrg);
			for (String childEdOrg : childEdOrgs) {
				staffAssociations.addAll(getAssociations(childEdOrg, "educationOrganizationReference", "staffEducationOrganizationAssociation"));
				teacherAssociations.addAll(getAssociations(childEdOrg, "schoolId", "teacherSchoolAssociation"));
				studentAssociations.addAll(getAssociations(childEdOrg, "schoolId", "studentSchoolAssociation"));
				currentStaffAssociations = filterCurrent(staffAssociations);
				currentTeacherAssociations = filterCurrent(teacherAssociations);
				currentStudentAssociations = filterCurrent(studentAssociations);
			}
		}
		
		count.setEducationOrganizationId(edOrg);

		count.setTotalStaff(getUniqueCount(staffAssociations, "staffReference"));
		count.setTotalTeacher(getUniqueCount(teacherAssociations, "teacherId"));
		count.setTotalStudent(getUniqueCount(studentAssociations, "studentId"));
		count.setTotalNonTeacher(staffAssociations.size() - teacherAssociations.size());

		count.setCurrentStaff(getUniqueCount(currentStaffAssociations, "staffReference"));
		count.setCurrentTeacher(getUniqueCount(currentTeacherAssociations, "teacherId"));
		count.setCurrentStudent(getUniqueCount(currentStudentAssociations, "studentId"));
		count.setCurrentNonTeacher(currentStaffAssociations.size() - currentTeacherAssociations.size());

		return count;
	}

	/*
	 * Get Associations for the Ed Org passed in
	 */
	private Set<Entity> getAssociations(String edOrg, String edOrgField, String collectionName) {
		Set<Entity> associations = new HashSet<Entity>();
		NeutralQuery neutralQuery = new NeutralQuery();
		neutralQuery.addCriteria(new NeutralCriteria(edOrgField, NeutralCriteria.OPERATOR_EQUAL, edOrg));
		Iterable<Entity> itEntities = mongoEntityRepository.findAll(collectionName, neutralQuery);
		Iterator<Entity> it = itEntities.iterator();
		while (it.hasNext()) {
			Entity entity = (Entity) it.next();
			if ("teacherSchoolAssociation".equals(entity.getType())) {
				Entity staffAssociation = getStaffAssocForTeacherAssoc(entity, entity.getBody().get("schoolId").toString());
				if (null != staffAssociation) {
					associations.add(entity);
				} else {
					List<String> parentEdOrgs = edOrgHelper.getParentEdOrgs(mongoEntityRepository.findById("educationOrganization", edOrg));
					for (String parent : parentEdOrgs) {
						Entity staffParentAssociation = getStaffAssocForTeacherAssoc(entity, parent);
						if (null != staffParentAssociation) {
							associations.add(entity);
						}
					}
				}
			} else {
				associations.add(entity);
			}
		}
		return associations;
	}

	/*
	 * Filter out entities that are not current
	 */
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
				Entity staffAssociation = getStaffAssocForTeacherAssoc(entity, entity.getBody().get("schoolId").toString());
				if (null != staffAssociation && isCurrent(staffAssociation, beginDateField, endDateField)) {
					current.add(entity);
				} else if (null == staffAssociation) {
					List<String> parentEdOrgs = edOrgHelper.getParentEdOrgs(mongoEntityRepository.findById("educationOrganization", entity.getBody().get("schoolId").toString()));
					for (String parent : parentEdOrgs) {
						Entity staffParentAssociation = getStaffAssocForTeacherAssoc(entity, parent);
						if (null != staffParentAssociation) {
							current.add(entity);
						}
					}
				}
			} else if (isCurrent(entity, beginDateField, endDateField)) {
				current.add(entity);
			}
		}
		return current;
	}

	/*
	 * Get the staffEducationOrganization that matches the teacherSchoolAssociation if it exists otherwise null
	 */
	private Entity getStaffAssocForTeacherAssoc(Entity teacherSchoolAssociation, String edOrg) {
		NeutralQuery staffNeutralQuery = new NeutralQuery();
		staffNeutralQuery.addCriteria(new NeutralCriteria("educationOrganizationReference", NeutralCriteria.OPERATOR_EQUAL, edOrg));
		staffNeutralQuery.addCriteria(new NeutralCriteria("staffReference", NeutralCriteria.OPERATOR_EQUAL, teacherSchoolAssociation.getBody().get("teacherId")));
		return mongoEntityRepository.findOne("staffEducationOrganizationAssociation", staffNeutralQuery);
	}

	/*
	 * Check to see if the entity is current or not.
	 */
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

	/*
	 * Return a count of unique entities from a set based on the reference Field
	 */
	private int getUniqueCount(Set<Entity> associations, String referenceField) {
		Set<String> unique = new HashSet<String>();
		
		for (Entity entity : associations) {
			unique.add((String) entity.getBody().get(referenceField));
		}

		return unique.size();
	}
}
