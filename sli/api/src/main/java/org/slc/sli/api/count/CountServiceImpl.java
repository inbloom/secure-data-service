/*
 * Copyright 2012-2014 inBloom, Inc. and its affiliates.
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

/**
 * Service that is used to return counts of entities that are custom to
 * specific use cases. At this time, the use case for these counts are
 * in the Data Browser tables and counts next to the links
 * 
 * @author mbrush
 *
 */

@Service
public class CountServiceImpl implements CountService {

    @Autowired
    private EdOrgHelper edOrgHelper;

    @Autowired
    private Repository<Entity> mongoEntityRepository;

    /*
     * Find the counts related to the education organization that is directly associated
     * to the logged in user.
     */
	@Override
	public EducationOrganizationCount find() {
		Set<String> edOrgs = edOrgHelper.getDirectEdorgs();
		return getCountsForEdOrg(edOrgs, true);
	}

	/*
	 * Find the counts related to a particular edOrg
	 */
	@Override
	public EducationOrganizationCount findOne(String edOrgId) {
		Set<String> edOrgs = new HashSet<String>();
		edOrgs.add(edOrgId);
		return getCountsForEdOrg(edOrgs, true);
	}

	/*
	 * This is the main function. It gets all of the associations required and
	 * then filters the current and unique ones as required. If recursive is
	 * true then it will also get all of the children ed orgs and parse them
	 * for their associations as well.
	 */
	private EducationOrganizationCount getCountsForEdOrg(Set<String> edOrgs, boolean recursive) {
		EducationOrganizationCount count = new EducationOrganizationCount();

		Set<Entity> staffAssociations = new HashSet<Entity>();
		Set<Entity> teacherAssociations = new HashSet<Entity>();
		Set<Entity> studentAssociations = new HashSet<Entity>();
		Set<Entity> currentStaffAssociations = new HashSet<Entity>();
		Set<Entity> currentTeacherAssociations = new HashSet<Entity>();
		Set<Entity> currentStudentAssociations = new HashSet<Entity>();

		for (String edOrg : edOrgs) {
			count.addEducationOrganizationId(edOrg);
			
			// Get total associations for the specified edOrg
			staffAssociations.addAll(getAssociations(edOrg, "educationOrganizationReference", "staffEducationOrganizationAssociation"));
			teacherAssociations.addAll(getAssociations(edOrg, "schoolId", "teacherSchoolAssociation"));
			studentAssociations.addAll(getAssociations(edOrg, "schoolId", "studentSchoolAssociation"));
	
			// Filter out and return only the current associations
			currentStaffAssociations.addAll(filterCurrent(staffAssociations));
			currentTeacherAssociations.addAll(filterCurrent(teacherAssociations));
			currentStudentAssociations.addAll(filterCurrent(studentAssociations));
	
			// If recursive is true, get the child edOrgs and process them.
			if (recursive) {
				Set<String> childEdOrgs = edOrgHelper.getChildEdOrgs(edOrg);
				for (String childEdOrg : childEdOrgs) {
					// Add all of the child ed org associations to the sets
					staffAssociations.addAll(getAssociations(childEdOrg, "educationOrganizationReference", "staffEducationOrganizationAssociation"));
					teacherAssociations.addAll(getAssociations(childEdOrg, "schoolId", "teacherSchoolAssociation"));
					studentAssociations.addAll(getAssociations(childEdOrg, "schoolId", "studentSchoolAssociation"));
					
					// and again filter out the current
					currentStaffAssociations = filterCurrent(staffAssociations);
					currentTeacherAssociations = filterCurrent(teacherAssociations);
					currentStudentAssociations = filterCurrent(studentAssociations);
				}
			}
		}
		
		// Set the values of the unique students, teachers and staff. The
		//   nonTeachers are calculated by the difference between the number
		//   of staffAssociations and teacherAssociations
		count.setTotalStaff(getUniqueCount(staffAssociations, "staffReference"));
		count.setTotalTeacher(getUniqueCount(teacherAssociations, "teacherId"));
		count.setTotalStudent(getUniqueCount(studentAssociations, "studentId"));
		count.setTotalNonTeacher(staffAssociations.size() - teacherAssociations.size());

		// Dido for the current
		count.setCurrentStaff(getUniqueCount(currentStaffAssociations, "staffReference"));
		count.setCurrentTeacher(getUniqueCount(currentTeacherAssociations, "teacherId"));
		count.setCurrentStudent(getUniqueCount(currentStudentAssociations, "studentId"));
		count.setCurrentNonTeacher(currentStaffAssociations.size() - currentTeacherAssociations.size());

		return count;
	}

	/*
	 * Query mongo for the associations for the edOrg passed in.
	 */
	private Set<Entity> getAssociations(String edOrg, String edOrgField, String collectionName) {
		Set<Entity> associations = new HashSet<Entity>();
		
		// Set the NeutralQuery for the Mongo query
		NeutralQuery neutralQuery = new NeutralQuery();
		neutralQuery.addCriteria(new NeutralCriteria(edOrgField, NeutralCriteria.OPERATOR_EQUAL, edOrg));
		
		// Get the entities from Mongo and begin looping over them.
		Iterable<Entity> itEntities = mongoEntityRepository.findAll(collectionName, neutralQuery);
		Iterator<Entity> it = itEntities.iterator();
		while (it.hasNext()) {
			Entity entity = (Entity) it.next();
			// If we are dealing with a teacherSchoolAssociation, we also have to make sure that is has a
			//   staffEducationOrganizationAssociation at this edOrg or one of it's parents.
			if ("teacherSchoolAssociation".equals(entity.getType())) {
				// Get relevant staffAssociation based on this teacher association
				Entity staffAssociation = getStaffAssocForTeacherAssoc(entity, entity.getBody().get("schoolId").toString());
				// Add it if it isn't null
				if (null != staffAssociation) {
					// This is a little trickery for performance. Add the staffEducationOrganization
					//   begin and end dates to the teacherSchoolAssociation as if it belongs there.
					//   This will make getting the whether or not it is current easier.
					entity.getBody().put("beginDate", staffAssociation.getBody().get("beginDate"));
					entity.getBody().put("endDate", staffAssociation.getBody().get("endDate"));
					associations.add(entity);
				} else {
					// Else we need to loop up through the edorg hierarchy looking for a match at a higher level
					//	and add it if there is one.
					List<String> parentEdOrgs = edOrgHelper.getParentEdOrgs(mongoEntityRepository.findById("educationOrganization", edOrg));
					for (String parent : parentEdOrgs) {
						Entity staffParentAssociation = getStaffAssocForTeacherAssoc(entity, parent);
						if (null != staffParentAssociation) {
							// Trickery as above
							entity.getBody().put("beginDate", staffParentAssociation.getBody().get("beginDate"));
							entity.getBody().put("endDate", staffParentAssociation.getBody().get("endDate"));
							associations.add(entity);
							// Break out so the closest association to the ed org is the one that is used.
							break;
						}
					}
				}
			} else {
				// Not a teachSchoolAssociation so just add it.
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
			// Set the appropriate begin and end dates for student School Associations
			if (("studentSchoolAssociation").equals(entity.getType())) {
				beginDateField = ParameterConstants.STUDENT_SCHOOL_BEGIN_DATE;
				endDateField = ParameterConstants.STUDENT_SCHOOL_END_DATE;
			}

			// If it is current, add it
			if (isCurrent(entity, beginDateField, endDateField)) {
				current.add(entity);
			}
		}
		return current;
	}

	/*
	 * Get the staffEducationOrganization that matches the teacherSchoolAssociation if it exists otherwise null
	 */
	private Entity getStaffAssocForTeacherAssoc(Entity teacherSchoolAssociation, String edOrg) {
		// Go to Mongo and get a staffEducationOrganizationAssociation that matches the teacherSchoolAssociation
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

		// If the beginDate and endDate fields both exist and are both appropriate
		if (body.containsKey(beginDateField)
				&& body.containsKey(endDateField)
				&& body.get(beginDateField).toString().compareTo(today) <= 0
				&& body.get(endDateField).toString().compareTo(today) >= 0
				) {
			return true;
		// If the beginDate field exists and endDate does not and beginDate is today or before
		} else if (body.containsKey(beginDateField)
				&& !body.containsKey(endDateField)
				&& body.get(beginDateField).toString().compareTo(today) <= 0
				) {
			return true;
		// If the endDate field exists and beginDate does not and endDate is today or later
		} else if (!body.containsKey(beginDateField)
				&& body.containsKey(endDateField)
				&& body.get(endDateField).toString().compareTo(today) >= 0
				) {
			return true;
		// If neither field exists, then the entity will be current
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
