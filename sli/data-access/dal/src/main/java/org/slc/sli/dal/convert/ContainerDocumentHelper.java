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

package org.slc.sli.dal.convert;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.domain.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jstokes
 */
public class ContainerDocumentHelper {

    public static NaturalKeyDescriptor extractNaturalKeyDescriptor(final Entity entity,
                                                                   final List<String> parentKeys) {
        final Map<String, String> naturalKeyMap = new HashMap<String, String>();
        for (final String key : parentKeys) {
            String value = (String) entity.getBody().get(key);
            naturalKeyMap.put(key, value);
        }
        return new NaturalKeyDescriptor(naturalKeyMap);
    }
}