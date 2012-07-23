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


package org.slc.sli.dashboard.manager;

import org.slc.sli.dashboard.entity.Config;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.manager.Manager.EntityMapping;
import org.slc.sli.dashboard.manager.Manager.EntityMappingManager;

/**
 * This Manager facilitates the Dashboard Builder feature, which allows users
 * to configure Dashboard layouts and panels.
 *
 * @author dwu
 *
 */
@EntityMappingManager
public interface DashboardBuilderManager {

    @EntityMapping("profiles")
    public abstract GenericEntity getProfiles(String token, Object id,
            Config.Data config);

    public void setConfigManager(ConfigManager configManager);
}

