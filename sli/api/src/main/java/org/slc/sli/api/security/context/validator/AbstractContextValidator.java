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

package org.slc.sli.api.security.context.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Abstract class that all context validators must extend.
 */
public abstract class AbstractContextValidator implements IContextValidator {

    @Value("${sli.security.gracePeriod}")
    String gracePeriod;

    @Autowired
    protected DateHelper dateHelper;

    @Autowired
    public PagingRepositoryDelegate<Entity> repo;

    @Autowired
    private EdOrgHelper edorgHelper;

    protected String getFilterDate(boolean useGracePeriod) {
        return dateHelper.getFilterDate(useGracePeriod);
    }

    protected DateTime getNowMinusGracePeriod() {
        return dateHelper.getNowMinusGracePeriod();
    }
    
    protected static final Set<String> SUB_ENTITIES_OF_STUDENT = new HashSet<String>(Arrays.asList(
            EntityNames.ATTENDANCE,
            EntityNames.DISCIPLINE_ACTION, 
            EntityNames.STUDENT_ACADEMIC_RECORD,
            EntityNames.STUDENT_ASSESSMENT,
            EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION,
            EntityNames.STUDENT_GRADEBOOK_ENTRY,
            EntityNames.STUDENT_PARENT_ASSOCIATION,
            EntityNames.STUDENT_SCHOOL_ASSOCIATION,
            EntityNames.STUDENT_SECTION_ASSOCIATION,
            EntityNames.REPORT_CARD));

    /**
     * Determines if the specified type is a sub-entity of student.
     *
     * @param type Type to check is 'below' student.
     * @return True if the entity hangs off of student, false otherwise.
     */
    protected boolean isSubEntityOfStudent(String type) {
        return SUB_ENTITIES_OF_STUDENT.contains(type);
    }

    protected void addEndDateToQuery(NeutralQuery query, boolean useGracePeriod) {
        NeutralCriteria endDateCriteria = new NeutralCriteria(ParameterConstants.END_DATE, NeutralCriteria.CRITERIA_GTE, getFilterDate(useGracePeriod));
        query.addOrQuery(new NeutralQuery(new NeutralCriteria(ParameterConstants.END_DATE, NeutralCriteria.CRITERIA_EXISTS, false)));
        query.addOrQuery(new NeutralQuery(endDateCriteria));
    }
    
    protected static final Set<String> SUB_ENTITIES_OF_STUDENT_SECTION = new HashSet<String>(Arrays.asList(
            EntityNames.GRADE, 
            EntityNames.STUDENT_COMPETENCY));


    /**
     * Determines if the specified type is a sub-entity of student section association.
     *
     * @param type Type to check is 'below' student section association.
     * @return True if the entity hangs off of student section association, false otherwise.
     */
    protected boolean isSubEntityOfStudentSectionAssociation(String type) {
        return SUB_ENTITIES_OF_STUDENT_SECTION.contains(type);
    }

    /**
     * Checks if the DateTime of the first parameter is earlier (or equal to) the second parameter,
     * comparing only the year, month, and day.
     *
     * @param lhs First DateTime.
     * @param rhs Second DateTime.
     * @return True if first DateTime is before (or equal to) to the second DateTime, false
     *         otherwise.
     */
    protected boolean isLhsBeforeRhs(DateTime lhs, DateTime rhs) {
        return dateHelper.isLhsBeforeRhs(lhs, rhs);
    }

    /**
     * Parse the String representing a DateTime and return the corresponding DateTime.
     *
     * @param convert String to be converted (of format yyyy-MM-dd).
     * @return DateTime object.
     */
    protected DateTime getDateTime(String convert) {
        return DateTime.parse(convert, dateHelper.fmt);
    }

    /**
     * Convert the DateTime to a String representation.
     *
     * @param convert DateTime to be converted.
     * @return String representing DateTime (of format yyyy-MM-dd).
     */
    protected String getDateTimeString(DateTime convert) {
        return convert.toString(dateHelper.fmt);
    }

    /**
     * Determines if the user is of type 'staff'.
     *
     * @return True if user is of type 'staff', false otherwise.
     */
    protected boolean isStaff() {
        return EntityNames.STAFF.equals(SecurityUtil.getSLIPrincipal().getEntity().getType());
    }

    /**
     * Determines if the user is of type 'teacher'.
     *
     * @return True if user is of type 'teacher', false otherwise.
     */
    protected boolean isTeacher() {
        return EntityNames.TEACHER.equals(SecurityUtil.getSLIPrincipal().getEntity().getType());
    }
    
    public boolean isFieldExpired(Map<String, Object> body, String fieldName, boolean useGracePeriod) {
        return dateHelper.isFieldExpired(body, fieldName, useGracePeriod);
    }


    protected Repository<Entity> getRepo() {
        return this.repo;
    }


    /**
     * Will go through staffEdorgAssociations that are current and get the descendant
     * edorgs that you have.
     *
     * @return a set of the edorgs you are associated to and their children.
     */
    protected Set<String> getStaffEdOrgLineage() {
        return edorgHelper.getStaffEdOrgLineage();
    }

    protected Set<String> getEdorgDescendents(Set<String> edOrgLineage) {
        edOrgLineage.addAll(edorgHelper.getChildEdOrgs(edOrgLineage));
        return edOrgLineage;
    }

    protected Set<String> getEdorgLineage(Set<String> directEdorgs) {
        Set<String> ancestors = new HashSet<String>();
        Set<String> descendants = new HashSet<String>(directEdorgs);
        for (String edorg : directEdorgs) {
            ancestors.addAll(fetchParentEdorgs(edorg));
        }
        descendants = getEdorgDescendents(descendants);
        descendants.addAll(ancestors);
        return descendants;

    }

    protected  Set<String> getStaffCurrentAssociatedEdOrgs() {
        return edorgHelper.getStaffCurrentAssociatedEdOrgs();
    }

    protected Set<String> getStaffEdOrgParents() {
        Set<String> edorgHiearchy = new HashSet<String>();
        Set<String> directEdorgs = getStaffCurrentAssociatedEdOrgs();
        edorgHiearchy.addAll(directEdorgs);
        for (String edorg : directEdorgs) {
            edorgHiearchy.addAll(fetchParentEdorgs(edorg));
        }
        return edorgHiearchy;
    }

    private Set<String> fetchParentEdorgs(String id) {
        Set<String> parents = new HashSet<String>();
        Entity edOrg = getRepo().findOne(EntityNames.EDUCATION_ORGANIZATION, new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.OPERATOR_EQUAL, id, false)));
        if (edOrg != null) {
            parents.addAll(edorgHelper.getParentEdOrgs(edOrg));
        }
        return parents;
    }


    /**
     * Performs a query for entities of type 'type' with _id contained in the List of 'ids'.
     * Iterates through result and peels off String value contained in body.<<field>>. Returns
     * unique set of values that were stored in body.<<field>>.
     *
     * @param type  Entity type to query for.
     * @param ids   List of _ids of entities to query.
     * @param field Field (contained in body) to peel off of entities.
     * @return List of Strings representing unique Set of values stored in entities' body.<<field>>.
     */
    protected List<String> getIdsContainedInFieldOnEntities(String type, List<String> ids, String field) {
        Set<String> matching = new HashSet<String>();

        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID,
                NeutralCriteria.OPERATOR_EQUAL, ids));
        Iterable<Entity> entities = getRepo().findAll(type, query);
        if (entities != null) {
            for (Entity entity : entities) {
                Map<String, Object> body = entity.getBody();
                if (body.containsKey(field)) {
                    matching.add((String) body.get(field));
                }
            }
        }

        return new ArrayList<String>(matching);
    }

    protected void setRepo(PagingRepositoryDelegate<Entity> repo) {
        this.repo = repo;
    }

    /**
     * Validate that the id list isn't null, contains at least one id, and that the entity types match.
     * 
     * @param correctEntityType
     * @param inputEntityType
     * @param ids
     * @return true if the parameters are valid, false otherwise
     * @throws IllegalArgumentException if the types don't match
     */
    protected boolean areParametersValid(String correctEntityType, String inputEntityType, Set<String> ids) {
        return areParametersValid(Arrays.asList(correctEntityType), inputEntityType, ids);
    }
    
    protected boolean areParametersValid(Collection<String> correctEntityTypes, String inputEntityType, Set<String> ids) {
        if (!correctEntityTypes.contains(inputEntityType)) {
            throw new IllegalArgumentException(this.getClass() + " cannot validate type " + inputEntityType);
        }
        
        if (ids == null || ids.size() == 0) {
            return false;
        }
        return true;
    }

    protected Set<String> getTeacherEdorgLineage() {
        Set<String> edorgs = getDirectEdorgs();
        edorgs = getEdorgLineage(edorgs);
        return edorgs;
    }

    protected Set<String> getDirectEdorgs() {
        return edorgHelper.getDirectEdorgs();
    }

    /**
     * Gets all staff -> education organization assignment associations for the teacher.
     *
     * @return Iterable set of Entities representing StaffEducationOrgAssociation.
     */
    protected Iterable<Entity> getTeacherSchoolAssociations() {
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_REFERENCE,
                NeutralCriteria.OPERATOR_EQUAL, SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
        return repo.findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, basicQuery);
    }

    @Override
    public Set<String> getValid(String entityType, Set<String> ids) {
        // Default "fallback" implementation where ids are validated one by one
        Set<String> validated = new HashSet<String>();
        Set<String> tmp = new HashSet<String>();
        for (String id : ids) {
            tmp.add(id);
            if (validate(entityType, tmp)) {
                validated.add(id);
            }
            tmp.clear();
        }
        return validated;
    }
}
