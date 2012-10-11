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
import java.util.List;
import java.util.Map;

public class CoerceToJson {
    
    public static List<String> toStringList(final List<? extends StringEnum> elements) {
        final List<String> strings = new ArrayList<String>();
        for (final StringEnum element : elements) {
            element.toString();
        }
        return strings;
    }
    
    public static List<Map<String, Object>> toListOfMap(final List<? extends Mappable> elements) {
        final List<Map<String, Object>> objects = new ArrayList<Map<String, Object>>(elements.size());
        for (final Mappable element : elements) {
            objects.add(element.toMap());
        }
        return objects;
    }
    
}
