/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.shtick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jstokes
 */
public final class MapHelper {
    public static Map<String, Object> deepCopy(final Map<String, Object> toCopy) {
        final Map<String, Object> copy = new HashMap<String, Object>(toCopy.size());
        for (Map.Entry<String, Object> entry : toCopy.entrySet()) {
            copy.put(entry.getKey(), copyValue(entry.getValue()));
        }
        return copy;
    }
    
    private static List<Object> copyList(final List<Object> toCopy) {
        final List<Object> copy = new ArrayList<Object>(toCopy.size());
        for (Object elem : toCopy) {
            copy.add(copyValue(elem));
        }
        return copy;
    }
    
    private static Object copyValue(final Object elem) {
        if (elem instanceof Map) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> map = (Map<String, Object>) elem;
            return deepCopy(map);
        } else if (elem instanceof List) {
            @SuppressWarnings("unchecked")
            final List<Object> list = (List<Object>) elem;
            return copyList(list);
        } else { // We have a primitive
            return elem;
        }
    }
}
