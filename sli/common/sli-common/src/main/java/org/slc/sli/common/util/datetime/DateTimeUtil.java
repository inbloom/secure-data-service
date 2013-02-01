/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

    /**
     * Parses a date presently stored in the format yyyy-MM-dd and returns the corresponding DateTime object.
     *
     * @param dateToBeParsed String to be parsed into a DateTime object.
     * @return DateTime object.
     * @author shalka
     */
    public static DateTime parseDateTime(String dateToBeParsed) {
        String[] pieces = dateToBeParsed.split("-");
        int year = Integer.valueOf(pieces[0]);
        int month = Integer.valueOf(pieces[1]);
        int day = Integer.valueOf(pieces[2]);
        DateTime date = new DateTime().withDate(year, month, day);
        return date;
    }

    /**
     * Determines if the 1st date is before or equal to the 2nd date (comparing only year, month, day).
     *
     * @param date1 1st date object.
     * @param date2 2nd date object.
     * @return true if date1 is before or equal to date2, false if date1 is after date2.
     */
    public static boolean isLeftDateBeforeRightDate(DateTime date1, DateTime date2) {
        boolean less = false;
        if (date1.getYear() < date2.getYear()) {
            less = true;
        } else if (date1.getYear() == date2.getYear()) {
            if (date1.getMonthOfYear() < date2.getMonthOfYear()) {
                less = true;
            } else if (date1.getMonthOfYear() == date2.getMonthOfYear()) {
                if (date1.getDayOfMonth() <= date2.getDayOfMonth()) {
                    less = true;
                }
            }
        }
        return less;
    }
}
