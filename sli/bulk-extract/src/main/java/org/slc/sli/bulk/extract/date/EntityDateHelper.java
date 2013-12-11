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

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;

import org.slc.sli.common.constants.ContainerEntityNames;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;

/**
 * @author ablum tke
 */
public class EntityDateHelper {

    private static final List<String> YEAR_BASED_ENTITIES = Arrays.asList(EntityNames.ATTENDANCE, EntityNames.GRADE, EntityNames.REPORT_CARD,
                                                                          EntityNames.STUDENT_ACADEMIC_RECORD, EntityNames.COURSE_TRANSCRIPT,
                                                                          ContainerEntityNames.YEARLY_TRANSCRIPT);

    /**
     * Check if the input entity should be extracted.
     *
     * @param entity - Entity from which to retrieve the date
     * @param upToDate - Up to date
     *
     * @return - true if the entity should be extracted, false otherwise.
     */
    public static boolean shouldExtract(Entity entity, DateTime upToDate) {
        ExtractVerifier extractVerifier = retrieveExtractVerifier(entity.getType());
        return extractVerifier.shouldExtract(entity, upToDate);
    }

    public static String retrieveDate(Entity entity) {
        return (String) entity.getBody().get(EntityDates.ENTITY_DATE_FIELDS.get(entity.getType()));
    }

    private static boolean isBeforeOrEqualDate(String begin, DateTime upToDate) {
        DateTime beginDate = (begin == null) ? DateTime.now().toDateMidnight().toDateTime() : DateTime.parse(begin, DateHelper.getDateTimeFormat());
        return !beginDate.isAfter(upToDate.toDateMidnight().toDateTime());
    }

    private static boolean isBeforeOrEqualYear(String yearSpan, int upToYear) {
        int fromYear = Integer.parseInt(yearSpan.split("-")[0]);
        int toYear = Integer.parseInt(yearSpan.split("-")[1]);
        return ((upToYear >= toYear) && (upToYear > fromYear));
    }

    public static boolean isPastOrCurrentDate(String entityDate, DateTime upToDate, String type) {
       DateTime finalUpToDate = (upToDate == null) ? DateTime.now() : upToDate;

        if (YEAR_BASED_ENTITIES.contains(type)) {
            return isBeforeOrEqualYear(entityDate, finalUpToDate.year().get());
        } else {
            return isBeforeOrEqualDate(entityDate, finalUpToDate);
        }
    }

    protected static ExtractVerifier retrieveExtractVerifier(String entityType) {
        return ExtractVerifierFactory.retrieveExtractVerifier(entityType);
    }
}
