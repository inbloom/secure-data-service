package org.slc.sli.api.security.context.validator;

import java.util.ArrayList;
import java.util.HashSet;
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
     * @param type
     *            Type to check is 'below' student.
     * @return True if the entity hangs off of student, false otherwise.
     */
    protected boolean isSubEntityOfStudent(String type) {
        return EntityNames.ATTENDANCE.equals(type) || EntityNames.COURSE_TRANSCRIPT.equals(type)
                || EntityNames.DISCIPLINE_ACTION.equals(type) || EntityNames.STUDENT_ACADEMIC_RECORD.equals(type)
                || EntityNames.STUDENT_ASSESSMENT_ASSOCIATION.equals(type)
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
     * @param type
     *            Type to check is 'below' student section association.
     * @return True if the entity hangs off of student section association, false otherwise.
     */
    protected boolean isSubEntityOfStudentSectionAssociation(String type) {
        return EntityNames.GRADE.equals(type) || EntityNames.STUDENT_COMPETENCY.equals(type);
    }

    /**
     * Checks if the DateTime of the first parameter is earlier (or equal to) the second parameter,
     * comparing only the year, month, and day.
     *
     * @param lhs
     *            First DateTime.
     * @param rhs
     *            Second DateTime.
     * @return True if first DateTime is before (or equal to) to the second DateTime, false
     *         otherwise.
     */
    protected boolean isLhsBeforeRhs(DateTime lhs, DateTime rhs) {
        boolean before = false;
        if (lhs.getYear() < rhs.getYear()) {
            before = true;
        } else if (lhs.getYear() == rhs.getYear()) {
            if (lhs.getMonthOfYear() < rhs.getMonthOfYear()) {
                before = true;
            } else if (lhs.getMonthOfYear() == rhs.getMonthOfYear()) {
                if (lhs.getDayOfMonth() <= rhs.getDayOfMonth()) {
                    before = true;
                }
            }
        }
        return before;
    }

    /**
     * Parse the String representing a DateTime and return the corresponding DateTime.
     *
     * @param convert
     *            String to be converted (of format yyyy-MM-dd).
     * @return DateTime object.
     */
    protected DateTime getDateTime(String convert) {
        return DateTime.parse(convert, fmt);
    }

    /**
     * Convert the DateTime to a String representation.
     *
     * @param convert
     *            DateTime to be converted.
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

    protected Set<String> getChildEdOrgs(Set<String> edorg) {
        if (edorg.size() == 0) {
            return edorg;
        }
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE,
                NeutralCriteria.CRITERIA_IN, new ArrayList<String>(edorg)));
        Iterable<Entity> childrenIds = repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query);
        Set<String> children = new HashSet<String>();
        for (Entity child : childrenIds) {
            children.add(child.getEntityId());
        }
        edorg.addAll(getChildEdOrgs(children));
        return edorg;
    }

    /**
     * Will go through staffEdorgAssociations that are current and get the descendant
     * edorgs that you have.
     *
     * @return a set of the edorgs you are associated to and their children.
     */
    protected Set<String> getStaffEdorgLineage() {
        // Get my staffEdorg to get my edorg hierarchy
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_REFERENCE,
                NeutralCriteria.OPERATOR_EQUAL, SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
        Iterable<Entity> staffEdorgs = repo.findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, basicQuery);
        Set<String> edorgLineage = new HashSet<String>();
        for (Entity staffEdOrg : staffEdorgs) {
            if (!isFieldExpired(staffEdOrg.getBody(), ParameterConstants.END_DATE)) {
                edorgLineage
                        .add((String) staffEdOrg.getBody().get(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE));
            }
        }
        edorgLineage.addAll(getChildEdOrgs(edorgLineage));
        return edorgLineage;
    }

    /**
     * Performs a query for entities of type 'type' with _id contained in the List of 'ids'.
     * Iterates through result and peels off String value contained in body.<<field>>. Returns
     * unique set of values that were stored in body.<<field>>.
     *
     * @param type
     *            Entity type to query for.
     * @param ids
     *            List of _ids of entities to query.
     * @param field
     *            Field (contained in body) to peel off of entities.
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

    protected Repository<Entity> getRepo() {
        return this.repo;
    }

    protected void setRepo(PagingRepositoryDelegate<Entity> repo) {
        this.repo = repo;
    }
}
