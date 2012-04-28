package org.slc.sli.common.util.datetime;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Convenience utilities for common Joda time methods.
 *
 * @author Sean Melody <smelody@wgen.net>
 *
 */
public class DateTimeUtil {

    /**
     * Returns the current UTC time in ISO 8601 format.
     *
     * @return current UTC time
     */
    public static Date getNowInUTC() {

        return new DateTime(DateTimeZone.UTC).toDate();

    }
}
