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


package org.slc.sli.dashboard.manager;

import org.slc.sli.dashboard.client.APIClient;
import org.slc.sli.dashboard.util.SecurityUtil;

/**
 *
 * Superclass for manager classes.
 *
 * @author dwu
 *
 */
public abstract class ApiClientManager implements Manager {

    private APIClient apiClient;

    public APIClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(APIClient apiClient) {
        this.apiClient = apiClient;
    }

    public static final String DUMMY_EDORG_NAME = "No Ed-Org";

    protected String getToken() {
        return SecurityUtil.getToken();
    }
}
