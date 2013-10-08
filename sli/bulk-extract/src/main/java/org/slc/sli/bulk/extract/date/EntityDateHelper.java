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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;

/**
 * @author ablum tke
 */
@Component
public class EntityDateHelper {

    private static final Logger LOG = LoggerFactory.getLogger(EntityDateHelper.class);

    private static DateRetriever simpleDateRetriever;

    private static DateRetriever pathDateRetriever;

    private static final List<String> YEAR_BASED_ENTITIES = Arrays.asList(EntityNames.ATTENDANCE, EntityNames.GRADE, EntityNames.REPORT_CARD,
                                                                          EntityNames.STUDENT_ACADEMIC_RECORD, EntityNames.COURSE_TRANSCRIPT,
                                                                          "yearlyTranscript");

    public static boolean shouldExtract(Entity entity, DateTime upToDate) {
        String entityDate = retrieveDate(entity);

        return compareDates(upToDate, entityDate, entity.getType());
    }

    public static boolean shouldExtract(String entityDate, DateTime upToDate, String entityType) {

        return compareDates(upToDate, entityDate, entityType);
    }

    public static String retrieveDate(Entity entity) {
        String date = "";

        if (EntityDates.ENTITY_DATE_FIELDS.containsKey(entity.getType())) {
            date = simpleDateRetriever.retrieve(entity);
        } else if (EntityDates.ENTITY_PATH_FIELDS.containsKey(entity.getType())) {
            date = pathDateRetriever.retrieve(entity);
        }
        return date;
    }

    protected static boolean isBeforeOrEqualDate(String begin, DateTime upToDate) {
        DateTime beginDate = DateTime.parse(begin, DateHelper.getDateTimeFormat());
        return !beginDate.isAfter(upToDate);
    }

    protected static boolean isBeforeOrEqualYear(String yearSpan, String upToYear) {
        String fromYear = yearSpan.split("-")[0];
        String toYear = yearSpan.split("-")[1];
        return ((upToYear.compareTo(toYear) >= 0) && (upToYear.compareTo(fromYear) > 0));
    }

    private static boolean compareDates(DateTime upToDate, String entityDate, String type) {
        DateTime finalUpToDate = (upToDate == null) ? DateTime.now() : upToDate;

        if (YEAR_BASED_ENTITIES.contains(type)) {
            return isBeforeOrEqualYear(entityDate, finalUpToDate.year().getAsString());
        } else {
            return isBeforeOrEqualDate(entityDate, finalUpToDate);
        }
    }

    @Autowired(required = true)
    @Qualifier("simpleDateRetriever")
    public void setSimpleDateRetriever(DateRetriever simpleDateRetriever) {
        EntityDateHelper.simpleDateRetriever = simpleDateRetriever;
    }

    @Autowired(required = true)
    @Qualifier("pathDateRetriever")
    public void setPathDateRetriever(DateRetriever pathDateRetriever) {
        EntityDateHelper.pathDateRetriever = pathDateRetriever;
    }

}
