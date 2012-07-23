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

package org.slc.sli.dashboard.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.dashboard.entity.Config;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.manager.ApiClientManager;
import org.slc.sli.dashboard.manager.ConfigManager;
import org.slc.sli.dashboard.manager.DashboardBuilderManager;

/**
 *
 *
 * @author dwu
 *
 */
public class DashboardBuilderManagerImpl extends ApiClientManager implements DashboardBuilderManager {

    @Autowired
    private ConfigManager configManager;

    @Override
    public GenericEntity getProfiles(String token, Object id,
            Config.Data config) {

        GenericEntity entity = new GenericEntity();

        // get all layout configs
        Collection<Config> layouts = configManager.getLayoutConfigs(token);

        // build profile list
        List<GenericEntity> profileList = new ArrayList<GenericEntity>();
        for (Config layout : layouts) {
            GenericEntity profile = new GenericEntity();
            profile.put("id", layout.getId());
            profile.put("name", layout.getName());
            profileList.add(profile);
        }

        entity.put("root", profileList);
        return entity;
    }

    @Override
    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }


}
