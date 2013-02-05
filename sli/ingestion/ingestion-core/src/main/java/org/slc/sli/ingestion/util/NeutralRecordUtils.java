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

package org.slc.sli.ingestion.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.util.StringUtils;

/**
 * A utility class to process a neutral record
 *
 * @author ablum
 *
 */
public final class NeutralRecordUtils {

    private NeutralRecordUtils() { }

    @SuppressWarnings("unchecked")
    public static <T> T decodeAndTrimXmlStrings(T obj) {
        if (Map.class.isInstance(obj)) {
            return (T) process((Map<?, ?>) obj);
        } else if (List.class.isInstance(obj)) {
            return (T) process((List<?>) obj);
        } else if (String.class.isInstance(obj)) {
            return (T) process((String) obj);
        } else {
            return obj;
        }
    }

    private static List<Object> process(List<?> value) {
        List<Object> newList = new ArrayList<Object>();

        boolean isEmpty = true;
        for (Object record : value) {
            Object theRecord = decodeAndTrimXmlStrings(record);

            if (theRecord != null) {
                isEmpty = false;
            }

            newList.add(theRecord);
        }

        if (isEmpty) {
            newList = new ArrayList<Object>();
        }

        return newList;
    }

    private static Map<Object, Object> process(Map<?, ?> value) {
        Map<Object, Object> newMap = new HashMap<Object, Object>();

        boolean isEmpty = true;
        for (Map.Entry<?, ?> item : value.entrySet()) {
            Object newValue = decodeAndTrimXmlStrings(item.getValue());

            if (newValue != null) {
                isEmpty = false;
            }

            newMap.put(item.getKey(), newValue);
        }

        if (isEmpty) {
            newMap = null;
        }

        return newMap;
    }

    private static String process(String value) {
        String cmp = value;

        if (!StringUtils.hasText(cmp)) {
            cmp = null;
        } else {
            cmp = StringEscapeUtils.unescapeXml(cmp.trim());
        }

        return cmp;
    }

    @SuppressWarnings("unchecked")
    public static String getByPath(String name, Map<String, Object> map) {
        // how many times have I written this code? Not enough, I say!
        String[] path = name.split("\\.");
        Map<String, Object> pathMap = map;
        for (int i = 0; i < path.length; i++) {
            Object obj = pathMap.get(path[i]);
            if (obj == null) {
                return null;
            } else if (i == path.length - 1 && obj instanceof String) {
                return (String) obj;
            } else if (obj instanceof Map) {
                pathMap = (Map<String, Object>) obj;
            } else {
                return null;
            }
        }
        return null;
    }
}
