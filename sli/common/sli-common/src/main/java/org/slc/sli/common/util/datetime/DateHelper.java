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

import java.util.Arrays;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

@Component
public class DateHelper {
    @Value("${sli.security.gracePeriod}")
    private String gracePeriod;
    private final static DateTimeFormatter FMT = DateTimeFormat.forPattern("yyyy-MM-dd");

    public String getFilterDate(boolean useGracePeriod) {
        DateTime date = null;
        if (useGracePeriod) {
            date = getNowMinusGracePeriod();
        } else {
            date = DateTime.now();

        }
        return date.toString(FMT);
    }

    public DateTime getNowMinusGracePeriod() {
        DateTime now = DateTime.now();
        int numDays = Integer.parseInt(gracePeriod);
        return now.minusDays(numDays);
    }

    public boolean isFieldExpired(Map<String, Object> body) {
        // silly edfi, refusing to have even one field be consistent...
        for (String key : Arrays.asList("endDate", "exitWithdrawDate")) {
            if (body.containsKey(key)) {
                return isFieldExpired(body, key, false);
            }
        }
        // no end date set, assume current
        return false;
    }

    public boolean isFieldExpired(Map<String, Object> body, String fieldName) {
        return isFieldExpired(body, fieldName, false);
    }

    public boolean isFieldExpired(Map<String, Object> body, String fieldName, boolean useGracePeriod) {
        boolean expired = false;
        if (null != body.get(fieldName)) {
            DateTime expire = DateTime.parse((String) body.get(fieldName), FMT);
            DateTime now = DateTime.now();

            if (useGracePeriod) {
                int numDays = Integer.parseInt(gracePeriod);
                now = now.minusDays(numDays);
            }

            expired = now.isAfter(expire);
        }

        return expired;
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
    public boolean isLhsBeforeRhs(DateTime lhs, DateTime rhs) {
        return !rhs.toLocalDate().isBefore(lhs.toLocalDate());
    }

    /**
     * Returns our internal format for dates.
     *
     * @return
     */
    public static DateTimeFormatter getDateTimeFormat() {
        return FMT;
    }

    public static Criteria getExpiredCriteria() {
        return Criteria.where("body.endDate").gte(DateTime.now().toString(getDateTimeFormat()));
    }

}
