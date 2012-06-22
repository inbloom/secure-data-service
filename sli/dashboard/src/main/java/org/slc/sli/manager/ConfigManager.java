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


package org.slc.sli.manager;

import java.util.Collection;

import org.slc.sli.entity.Config;
import org.slc.sli.entity.ConfigMap;
import org.slc.sli.entity.EdOrgKey;


/**
 *
 * ConfigManager allows other classes, such as controllers, to access and persist view
 * configurations.
 * Given a user, it will obtain view configuration at each level of the user's hierarchy, and merge
 * them into one set for the user.
 *
 * @author dwu
 */
public interface ConfigManager {

    /**
     * Read the educational organization hierarchy and return proper config file
     *
     * @param token - user token
     * @param userEdOrg - user educational organization proxy
     * @param componentId - name of the profile
     * @return proper Config to be used for the dashbord
     */
    Config getComponentConfig(String token, EdOrgKey edOrgKey, String componentId);

    /**
     * Get all available widget configs relevant for the user
     *
     * @param token - user token
     * @param userEdOrg - user educational organization proxy
     * @return collection of widget conigs
     */
    public Collection<Config> getWidgetConfigs(String token, EdOrgKey userEdOrg);

    /**
     * Get custom config for ed org
     *
     * @param token - user token
     * @param userEdOrg - user educational organization proxy
     * @return
     */
    public ConfigMap getCustomConfig(String token, EdOrgKey userEdOrg);
    void putCustomConfig(String token, EdOrgKey edOrgKey, ConfigMap configMap);

}
