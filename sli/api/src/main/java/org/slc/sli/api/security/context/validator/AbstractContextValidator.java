package org.slc.sli.api.security.context.validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.resolver.StaffEdOrgEdOrgIDNodeFilter;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Abstract class that all context validators must extend.
 */
public abstract class AbstractContextValidator implements IContextValidator {

    @Value("${sli.security.gracePeriod}")
    private String gracePeriod;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Autowired
    private StaffEdOrgEdOrgIDNodeFilter staffEdOrgEdOrgIDNodeFilter;

    @Autowired
    private EdOrgHelper edorgHelper;

    private DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

    protected String getFilterDate() {
        return getNowMinusGracePeriod().toString(fmt);
    }

    protected DateTime getNowMinusGracePeriod() {
        DateTime now = DateTime.now();
        int numDays = Integer.parseInt(gracePeriod);
        return now.minusDays(numDays);
    }

    /**
     * Determines if the specified type is a sub-entity of student.
     *
     * @param type Type to check is 'below' student.
     * @return True if the entity hangs off of student, false otherwise.
     */
    protected boolean isSubEntityOfStudent(String type) {
        return EntityNames.ATTENDANCE.equals(type) || EntityNames.COURSE_TRANSCRIPT.equals(type)
                || EntityNames.DISCIPLINE_ACTION.equals(type) || EntityNames.STUDENT_ACADEMIC_RECORD.equals(type)
                || EntityNames.STUDENT_ASSESSMENT.equals(type)
                || EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION.equals(type)
                || EntityNames.STUDENT_GRADEBOOK_ENTRY.equals(type)
                || EntityNames.STUDENT_PARENT_ASSOCIATION.equals(type)
                || EntityNames.STUDENT_SCHOOL_ASSOCIATION.equals(type)
                || EntityNames.STUDENT_SECTION_ASSOCIATION.equals(type)
                || EntityNames.REPORT_CARD.equals(type);
    }

    /**
     * Determines if the specified type is a sub-entity of student section association.
     *
     * @param type Type to check is 'below' student section association.
     * @return True if the entity hangs off of student section association, false otherwise.
     */
    protected boolean isSubEntityOfStudentSectionAssociation(String type) {
        return EntityNames.GRADE.equals(type) || EntityNames.STUDENT_COMPETENCY.equals(type);
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
        return !rhs.toLocalDate().isBefore(lhs.toLocalDate());
    }

    /**
     * Parse the String representing a DateTime and return the corresponding DateTime.
     *
     * @param convert String to be converted (of format yyyy-MM-dd).
     * @return DateTime object.
     */
    protected DateTime getDateTime(String convert) {
        return DateTime.parse(convert, fmt);
    }

    /**
     * Convert the DateTime to a String representation.
     *
     * @param convert DateTime to be converted.
     * @return String representing DateTime (of format yyyy-MM-dd).
     */
    protected String getDateTimeString(DateTime convert) {
        return convert.toString(fmt);
    }

    /**
     * Determines if the user is of type 'staff'.
     *
     * @return True if user is of type 'staff', false otherwise.
     */
    protected boolean isStaff() {
        return EntityNames.STAFF.equals(SecurityUtil.getSLIPrincipal().getEntity().getType());
    }

    protected boolean isFieldExpired(Map<String, Object> body, String fieldName) {
        DateTime expirationDate = DateTime.now();
        int numDays = Integer.parseInt(gracePeriod);
        expirationDate = expirationDate.minusDays(numDays);
        if (body.containsKey(fieldName)) {
            String dateStringToCheck = (String) body.get(fieldName);
            DateTime dateToCheck = DateTime.parse(dateStringToCheck, fmt);

            return dateToCheck.isBefore(expirationDate);
        }
        return false;
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
        Set<String> edOrgLineage = getStaffCurrentAssociatedEdOrgs();
        edOrgLineage.addAll(edorgHelper.getChildEdOrgs(edOrgLineage));
        return edOrgLineage;
    }

    protected Set<String> getStaffCurrentAssociatedEdOrgs() {
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_REFERENCE,
                NeutralCriteria.OPERATOR_EQUAL, SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
        Iterable<Entity> staffEdOrgs = repo.findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, basicQuery);
        List<Entity> staffEdOrgAssociations = new LinkedList<Entity>();
        if (staffEdOrgs != null) {
            for (Entity staffEdOrg : staffEdOrgs) {
                staffEdOrgAssociations.add(staffEdOrg);
            }
        }
        List<Entity> currentStaffEdOrgAssociations = staffEdOrgEdOrgIDNodeFilter.filterEntities(staffEdOrgAssociations, null);
        Set<String> edOrgIds = new HashSet<String>();
        for (Entity association : currentStaffEdOrgAssociations) {
            edOrgIds.add((String) association.getBody().get(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE));
        }
        return edOrgIds;
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
     * Determines if the entity type is public.
     *
     * @param type Entity type.
     * @return True if the entity is public, false otherwise.
     */
    protected boolean isPublic(String type) {
        return type.equals(EntityNames.ASSESSMENT) || type.equals(EntityNames.LEARNING_OBJECTIVE)
                || type.equals(EntityNames.LEARNING_STANDARD)
 || type.equals(EntityNames.COMPETENCY_LEVEL_DESCRIPTOR);
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

    public void setStaffEdOrgEdOrgIDNodeFilter(StaffEdOrgEdOrgIDNodeFilter staffEdOrgEdOrgIDNodeFilter) {
        this.staffEdOrgEdOrgIDNodeFilter = staffEdOrgEdOrgIDNodeFilter;
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
