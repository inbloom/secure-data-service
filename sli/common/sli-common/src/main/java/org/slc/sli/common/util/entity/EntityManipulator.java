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

package org.slc.sli.common.util.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Handles manipulation of entities, which are represented as a Map from String to Object
 *
 * @author jstokes
 */
public final class EntityManipulator {

    // prevent instantiation
    private EntityManipulator() { }

    /**
     * Removes fields from a JSON-like map of String => Object. Supports dotted notation
     * for embedded map removal.
     * @param toRemoveFrom map from which to remove fields
     * @param toRemoveList list of fields to be removed
     */
    public static void removeFields(final Map<String, Object> toRemoveFrom, final List<String> toRemoveList) {
        if (toRemoveList == null || toRemoveList.isEmpty()) {
            return;
        }
        if (toRemoveFrom == null) {
            return;
        }

        for (final String toStrip : toRemoveList) {
            removeField(toRemoveFrom, toStrip);
        }
    }

    /**
     * Removes fields from a JSON-like map of String => Object. Supports dotted notation
     * for embedded map removal.
     * @param toRemoveFrom map from which to remove fields
     * @param toRemove field to be removed
     */
    public static void removeFields(final Map<String, Object> toRemoveFrom, final String toRemove) {
        removeFields(toRemoveFrom, Arrays.asList(toRemove));
    }

    private static void removeField(final Map<String, Object> toRemoveFrom, final String toRemove) {
        if (toRemove.contains(".")) {
            final int dotIndex = toRemove.indexOf(".");
            final String firstKey = toRemove.substring(0, dotIndex);
            final Object obj = toRemoveFrom.get(firstKey);
            removeField(obj, toRemove.substring(dotIndex + 1));
        } else {
            toRemoveFrom.remove(toRemove);
        }
    }

    private static void removeField(final List<Object> toRemoveFromList, final String toRemove) {
        for (final Object toRemoveFrom : toRemoveFromList) {
            removeField(toRemoveFrom, toRemove);
        }
    }

    @SuppressWarnings("unchecked")
    private static void removeField(final Object obj, final String toRemove) {
        if (obj instanceof Map) {
            removeField((Map<String, Object>) obj, toRemove);
        } else if (obj instanceof List) {
            removeField((List<Object>) obj, toRemove);
        }
    }
}
