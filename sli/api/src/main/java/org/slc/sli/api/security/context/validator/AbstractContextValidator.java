package org.slc.sli.api.security.context.validator;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;

import org.slc.sli.api.constants.EntityNames;

public abstract class AbstractContextValidator implements IContextValidator {

    @Value("${sli.security.gracePeriod}")
    private String gracePeriod;

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
                || EntityNames.STUDENT_SCHOOL_ASSOCIATION.equals(type)
                || EntityNames.STUDENT_SECTION_ASSOCIATION.equals(type);
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
}
