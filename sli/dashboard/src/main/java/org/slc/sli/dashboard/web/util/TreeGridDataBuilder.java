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

    /**
     * Takes a list of GenericEntities, containing hierarchical data, and flattens the structure
     * for use by jqGrid tree grid. The subLevels param is a list of strings, specifying the attribute
     * names to progress down levels of the hierarchy.
     *
     * For example, if the data is a list of subjects, with nested "courses" and "sections" attributes,
     * you would pass in "courses" and "sections" in the subLevel list.
     *
     * @param entities
     * @param subLevels
     * @return
     */
    public static List<GenericEntity> build(List<GenericEntity> entities, List<String> subLevels) {

        List<GenericEntity> treeGrid = new ArrayList<GenericEntity>();

        // loop through top-level data
        for (GenericEntity entity : entities) {
            buildLevel(treeGrid, entity, null, subLevels, 0);
        }
        return treeGrid;
    }

    /**
     * Takes one node of the input hierarchical data structure, adds it to the treeGrid,
     * and then recurses down to the next node, indicated by the subLevels and level params.
     *
     * @param treeGrid
     * @param entity
     * @param parentId
     * @param subLevels
     * @param level
     */
    @SuppressWarnings("unchecked")
    private static void buildLevel(List<GenericEntity> treeGrid, GenericEntity entity, String parentId, List<String> subLevels, int level) {

        // add entity to tree grid
        entity.put(LEVEL, level);
        if (parentId == null) {
            entity.put(PARENT, "null");
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
