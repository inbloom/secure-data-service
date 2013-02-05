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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.slc.sli.dashboard.web.util.NoBadChars;


/**
 * Collection of Config objects for custom config store
 * @author agrebneva
 *
 */
public class ConfigMap implements Serializable {
    private static final long serialVersionUID = 1L;
    @Valid
    @NoBadChars(depth = 1)
    private Map<String, Config> config;

    public Map<String, Config> getConfig() {
        return config;
    }

    public Config getComponentConfig(String componentId) {
        return config.get(componentId);
    }

    public void setConfig(Map<String, Config> config) {
        this.config = config;
    }

    public int size() {
        return config.size();
    }

    public boolean isEmpty() {
        return config == null || config.isEmpty();
    }

    public ConfigMap cloneWithNewConfig(Config newConfig) {
        ConfigMap newMap = new ConfigMap();
        if (this.getConfig() == null) {
            newMap.config = new HashMap<String, Config>();
        } else {
            newMap.config = new HashMap<String, Config>(this.getConfig());
        }
        newMap.config.put(newConfig.getId(), newConfig);
        return newMap;
    }
}
