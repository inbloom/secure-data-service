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

import org.slc.sli.domain.Entity;

/**
 * @author ablum tke
 */
public class EntityDateHelper {

    /**
     * Check if the input entity should be extracted.
     *
     * @param entity - Entity from which to retrieve the date
     * @param upToDate - Up to date
     *
     * @return - true if the entity should be extracted, false otherwise.
     */
    public static boolean shouldExtract(Entity entity, DateTime upToDate) {
        ExtractVerifier extractVerifier = ExtractVerifierFactory.retrieveExtractVerifier(entity.getType());
        return extractVerifier.shouldExtract(entity, upToDate);
    }

    public static String retrieveDate(Entity entity) {
        return (String) entity.getBody().get(EntityDates.ENTITY_DATE_FIELDS.get(entity.getType()));
    }

}
