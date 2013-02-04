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

package org.slc.sli.api.security.context.validator;

import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DateHelper {
    @Value("${sli.security.gracePeriod}")
    private String gracePeriod;
    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

    public String getFilterDate(boolean useGracePeriod) {
        DateTime date = null;
        if (useGracePeriod) {
            date = getNowMinusGracePeriod();
        } else {
            date = DateTime.now();
            
        }
        return date.toString(fmt);
    }

    public DateTime getNowMinusGracePeriod() {
        DateTime now = DateTime.now();
        int numDays = Integer.parseInt(gracePeriod);
        return now.minusDays(numDays);
    }

    public boolean isFieldExpired(Map<String, Object> body, String fieldName) {
        return isFieldExpired(body, fieldName, false);
    }

    public boolean isFieldExpired(Map<String, Object> body, String fieldName, boolean useGracePeriod) {
        DateTime expirationDate = DateTime.now();
        if (useGracePeriod) {
            int numDays = Integer.parseInt(gracePeriod);
            expirationDate = expirationDate.minusDays(numDays);
        }
        if (body.containsKey(fieldName)) {
            String dateStringToCheck = (String) body.get(fieldName);

            if (dateStringToCheck == null) {
                return false;
            }

            DateTime dateToCheck = DateTime.parse(dateStringToCheck, fmt);
    
            return dateToCheck.isBefore(expirationDate);
        }
        return false;
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
    public boolean isLhsBeforeRhs(DateTime lhs, DateTime rhs) {
        return !rhs.toLocalDate().isBefore(lhs.toLocalDate());
    }
    
}
