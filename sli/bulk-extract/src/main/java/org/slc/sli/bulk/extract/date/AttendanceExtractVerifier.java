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
package org.slc.sli.bulk.extract.date;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;

/**
 * Verifies whether or not an Attendance entity should be extracted.
 *
 * @author tke tshewchuk
 */
@Component
public class AttendanceExtractVerifier implements ExtractVerifier {

    private final ThreadLocal <String> schoolYear = new ThreadLocal<String> ();

    @Override
    public boolean shouldExtract(Entity entity, DateTime upToDate) {
        String schoolYear = EntityDateHelper.retrieveDate(entity);
        if (schoolYear != null) {
            this.schoolYear.set(schoolYear);
        }

        DateTime finalUpToDate = (upToDate == null) ? DateTime.now() : upToDate;
        return isBeforeOrEqualYear(this.schoolYear.get(), finalUpToDate.year().get());
    }

    /**
     * Check if year span is not beyond up to year.
     *
     * @param yearSpan - Year span
     * @param upToYear - Up to year
     *
     * @return - true if year span is not beyond up to year
     */
    protected boolean isBeforeOrEqualYear(String yearSpan, int upToYear) {
        int fromYear = Integer.parseInt(yearSpan.split("-")[0]);
        int toYear = Integer.parseInt(yearSpan.split("-")[1]);
        return ((upToYear >= toYear) && (upToYear > fromYear));
    }

}
