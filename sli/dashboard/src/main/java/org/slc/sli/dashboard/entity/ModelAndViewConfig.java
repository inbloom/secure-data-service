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


package org.slc.sli.dashboard.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 * Component aggregate which contains all necessary data and config pieces relevant for component rendering
 * @author agrebneva
 *
 * @NotThreadSafe
 *
 */
public class ModelAndViewConfig {
    //
    private Map<String, GenericEntity> data;
    private Map<String, Config> config;
    private List<Config> layoutItems;
    private Collection<Config> widgetConfig;

    public ModelAndViewConfig() {
        this.data = new LinkedHashMap<String, GenericEntity>();
        this.config = new LinkedHashMap<String, Config>();
        this.layoutItems = new ArrayList<Config>();
        this.widgetConfig = new ArrayList<Config>();
    }

    public Map<String, GenericEntity> getData() {
        return data;
    }

    public void addData(String dataAliasId, GenericEntity data) {
        this.data.put(dataAliasId, data);
    }

    public Map<String, Config> getConfig() {
        return config;
    }

    public void addConfig(String componentId, Config config) {
        this.config.put(componentId, config);
    }

    public void addLayoutItem(Config layoutItem) {
        this.layoutItems.add(layoutItem);
    }

    public List<Config> getLayoutItems() {
        return layoutItems;
    }

    public boolean hasDataForAlias(String dataAliasId) {
        return this.data.containsKey(dataAliasId);
    }
    /**
     * Returning cached GenericEntity by cacheKey
     * @param dataAliasId
     * @return
     */
    public GenericEntity getDataForAlias(String dataAliasId) {
        return this.data.get(dataAliasId);
    }

    public void setWidgetConfig(Collection<Config> widgetConfigs) {
        this.widgetConfig = widgetConfigs;
    }

    public Collection<Config> getWidgetConfig() {
        return widgetConfig;
    }
}
