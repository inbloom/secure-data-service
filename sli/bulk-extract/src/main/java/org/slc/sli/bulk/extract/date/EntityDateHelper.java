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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.ContainerEntityNames;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;

/**
 * @author ablum tke
 */
@Component
public class EntityDateHelper {

    private static DateRetriever simpleDateRetriever;
    private static DateRetriever pathDateRetriever;
    private static EdOrgExtractHelper edOrgExtractHelper;

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
        // TeacherSchoolAssociation is a special case, as it is dated from it's corresponding SEOAs.
        if (EntityNames.TEACHER_SCHOOL_ASSOCIATION.equals(entity.getType())) {
            Iterable<Entity> seaos = edOrgExtractHelper.retrieveSEOAS((String) entity.getBody().get(ParameterConstants.TEACHER_ID),
                    (String) entity.getBody().get(ParameterConstants.SCHOOL_ID));
            return shouldExtract(seaos, upToDate);
        }

        String entityDate = retrieveDate(entity);

        return isPastOrCurrentDate(entityDate, upToDate, entity.getType());
    }

    /**
     * Check if the input entity should be extracted.
     *
     * @param entityDate - Entity date
     * @param upToDate - Up to date
     * @param entityType - Entity type
     *
     * @return - true if the entity should be extracted, false otherwise.
     */
    public static boolean shouldExtract(String entityDate, DateTime upToDate, String entityType) {
        return isPastOrCurrentDate(entityDate, upToDate, entityType);
    }

    /**
     * Check if any of the input entities should be extracted.
     *
     * @param entities    List of entities to check
     * @param upToDate    Up to date
     *
     * @return   true if any of the entity should be extracted, false otherwise.
     */
    protected static boolean shouldExtract(Iterable<Entity> entities, DateTime upToDate) {
        for (Entity entity : entities) {
            if (shouldExtract(entity, upToDate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get entity date.
     *
     * @param entity - Entity from which to retrieve the date
     *
     * @return - Entity's date
     */
    public static String retrieveDate(Entity entity) {
        String date = "";

        if (EntityDates.ENTITY_DATE_FIELDS.containsKey(entity.getType())) {
            date = simpleDateRetriever.retrieve(entity);
        } else if (EntityDates.ENTITY_PATH_FIELDS.containsKey(entity.getType())) {
            date = pathDateRetriever.retrieve(entity);
        }

        return date;
    }

    /**
     * Check if begin date is not past up to date.
     *
     * @param begin - Begin date
     * @param upToDate - Up to date
     *
     * @return - true if begin date is not past up to date
     */
    protected static boolean isBeforeOrEqualDate(String begin, DateTime upToDate) {
        DateTime beginDate = (begin == null) ? DateTime.now() : DateTime.parse(begin, DateHelper.getDateTimeFormat());
        return !beginDate.isAfter(upToDate);
    }

    /**
     * Check if year span is not beyond up to year.
     *
     * @param yearSpan - Year span
     * @param upToYear - Up to year
     *
     * @return - true if year span is not beyond up to year
     */
    protected static boolean isBeforeOrEqualYear(String yearSpan, int upToYear) {
        int fromYear = Integer.parseInt(yearSpan.split("-")[0]);
        int toYear = Integer.parseInt(yearSpan.split("-")[1]);
        return ((upToYear >= toYear) && (upToYear > fromYear));
    }

    private static boolean isPastOrCurrentDate(String entityDate, DateTime upToDate, String type) {
        DateTime finalUpToDate = (upToDate == null) ? DateTime.now() : upToDate;

        if (YEAR_BASED_ENTITIES.contains(type)) {
            return isBeforeOrEqualYear(entityDate, finalUpToDate.year().get());
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

    @Autowired(required = true)
    @Qualifier("edOrgExtractHelper")
    public void setEdOrgExtractHelper(EdOrgExtractHelper edOrgExtractHelper) {
        EntityDateHelper.edOrgExtractHelper = edOrgExtractHelper;
    }

}
