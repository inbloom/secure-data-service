package org.slc.sli.api.security.context.validator;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractContextValidator implements IContextValidator {

    @Value("${sli.security.gracePeriod}")
    private String gracePeriod;

    private DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

    protected String getFilterDate() {
        DateTime now = DateTime.now();
        int numDays = Integer.parseInt(gracePeriod);
        now = now.minusDays(numDays);
        return now.toString(fmt);
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
