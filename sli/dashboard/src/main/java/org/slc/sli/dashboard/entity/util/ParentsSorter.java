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


package org.slc.sli.dashboard.entity.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.util.Constants;

/**
 * Sorting class for Parents Contact information based on priority rules
 *
 * @author tosako
 *
 */
public final class ParentsSorter {
    private static Map<String, Integer> primaryContactStatusPriority;
    private static Map<String, Integer> relationPriority;
    static {
        primaryContactStatusPriority = new HashMap<String, Integer>();
        primaryContactStatusPriority.put("true", 1);
        primaryContactStatusPriority.put(null, 2);
        primaryContactStatusPriority.put("false", 3);

        relationPriority = new HashMap<String, Integer>();
        relationPriority.put("Parent", 1);
        relationPriority.put("Mother", 2);
        relationPriority.put("Father", 3);
        relationPriority.put("Adoptive parents", 4);
        relationPriority.put("Court appointed guardian", 5);
        relationPriority.put("Mother, foster", 6);
        relationPriority.put("Father, foster", 7);
    }

    private ParentsSorter() {
    }

    /**
     * Return sorted Parents contact information
     *
     * @param genericEntities
     * @return
     */
    public static List<GenericEntity> sort(List<GenericEntity> genericEntities) {

        // "relation", "contactPriority", and "primaryContactStatus" elements are nested element
        // which are in "studentParentAssociation" element.
        // GenericEntityComparator cannot access to nested elements. Therefore, before sorting, we
        // copy these elements and make GenericEntityComparator accessible.
        for (GenericEntity genericEntity : genericEntities) {
            List<Map<String, Object>> studentParentAssociations = (List<Map<String, Object>>) genericEntity
                    .get(Constants.ATTR_STUDENT_PARENT_ASSOCIATIONS);
            if (studentParentAssociations != null && !studentParentAssociations.isEmpty()) {
                Map<String, Object> studentParentAssociation = studentParentAssociations.get(0);
                genericEntity.put(Constants.ATTR_RELATION, studentParentAssociation.get(Constants.ATTR_RELATION));
                genericEntity.put(Constants.ATTR_CONTACT_PRIORITY,
                        studentParentAssociation.get(Constants.ATTR_CONTACT_PRIORITY));
                genericEntity.put(Constants.ATTR_PRIMARY_CONTACT_STATUS,
                        studentParentAssociation.get(Constants.ATTR_PRIMARY_CONTACT_STATUS));
            }
        }

        // Fifth, by “Relation”, in the following order: Parent; Mother; Father; Adoptive parents;
        // Court appointed guardian; Mother, foster; Father, foster; thereafter, alphabetically.
        GenericEntityComparator comparator = new GenericEntityComparator(Constants.ATTR_RELATION, String.class);
        Collections.sort(genericEntities, comparator);

        comparator = new GenericEntityComparator(Constants.ATTR_RELATION, relationPriority);
        Collections.sort(genericEntities, comparator);

        // Second, by ContactPriority (lower numbers indicate higher priority)
        comparator = new GenericEntityComparator(Constants.ATTR_CONTACT_PRIORITY, Double.class);
        Collections.sort(genericEntities, comparator);

        // First, by PrimaryContactStatus, in the order true, null, false.
        comparator = new GenericEntityComparator(Constants.ATTR_PRIMARY_CONTACT_STATUS, primaryContactStatusPriority);
        Collections.sort(genericEntities, comparator);

        // clean up the temporary element we copy for GenericEntityCompator
        for (GenericEntity genericEntity : genericEntities) {
            genericEntity.remove(Constants.ATTR_RELATION);
            genericEntity.remove(Constants.ATTR_CONTACT_PRIORITY);
            genericEntity.remove(Constants.ATTR_PRIMARY_CONTACT_STATUS);
        }

        return genericEntities;
    }
}
