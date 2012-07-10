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

package org.slc.sli.dashboard.web.util;

import java.util.ArrayList;
import java.util.List;

import org.slc.sli.dashboard.entity.GenericEntity;

/**
 * Given hierarchical data, creates the flattened data structure needed by the jqGrid Tree Grid.
 *
 * @author dwu
 *
 */
public class TreeGridDataBuilder {

    private static final String LOADED = "loaded";
    private static final String EXPANDED = "expanded";
    private static final String IS_LEAF = "isLeaf";
    private static final String PARENT = "parent";
    private static final String LEVEL = "level";

    static public List<GenericEntity> build(List<GenericEntity> entities, List<String> subLevels) {

        List<GenericEntity> treeGrid = new ArrayList<GenericEntity>();

        // loop through top-level data
        for (GenericEntity entity : entities) {
            buildLevel(treeGrid, entity, null, subLevels, 0);
        }
        return treeGrid;
    }

    static private void buildLevel(List<GenericEntity> treeGrid, GenericEntity entity, String parentId, List<String> subLevels, int level) {

        // add entity to tree grid
        entity.put(LEVEL, level);
        if (parentId == null) {
            entity.put(PARENT, "");
        } else {
            entity.put(PARENT, parentId);
        }
        entity.put(IS_LEAF, true);
        entity.put(EXPANDED, false);
        entity.put(LOADED, true);
        treeGrid.add(entity);

        // recurse on sublevel
        if (level < subLevels.size()) {

            List<GenericEntity> subEntities = (List<GenericEntity>) entity.get(subLevels.get(level));
            if (subEntities != null && subEntities.size() > 0) {
                entity.put(IS_LEAF, false);
                for (GenericEntity subEntity : subEntities) {
                    buildLevel(treeGrid, subEntity, entity.getId(), subLevels, level + 1);
                }
                entity.remove(subLevels.get(level));
            }
        }
    }
}
