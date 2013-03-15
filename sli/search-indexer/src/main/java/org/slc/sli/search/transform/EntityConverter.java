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
package org.slc.sli.search.transform;

import java.util.List;
import java.util.Map;

import org.slc.sli.search.entity.IndexEntity.Action;

public interface EntityConverter {

    /**
     * Transform the given entity map to a list of maps to be indexed
     * 
     * @param index
     *            tenantDB hash
     * @param action
     * @param entityMap
     * @return List of Maps to be transformed by IndexEntityConverter
     * 
     */
    public List<Map<String, Object>> treatment(String index, Action action, Map<String, Object> entityMap);

    /**
     * Some actions may also need to changed depending on the situation
     * 
     * @param action
     * @return
     *         new action
     */
    public Action convertAction(Action action);

}
