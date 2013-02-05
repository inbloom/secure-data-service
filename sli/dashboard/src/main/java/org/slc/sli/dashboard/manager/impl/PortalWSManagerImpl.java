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

package org.slc.sli.dashboard.manager.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;

import org.slc.sli.dashboard.client.RESTClient;
import org.slc.sli.dashboard.manager.PortalWSManager;
import org.slc.sli.dashboard.util.Constants;

import com.google.gson.JsonSyntaxException;
import org.springframework.web.client.ResourceAccessException;

/**
 * Retrieves header and footer from Portal WS
 *
 * @author svankina
 *
 */
public class PortalWSManagerImpl implements PortalWSManager {
    private RESTClient restClient;
    private String portalHeaderUrl;
    private String portalFooterUrl;

    public void setPortalHeaderUrl(String portalHeaderUrl) {
        this.portalHeaderUrl = portalHeaderUrl;
    }

    public void setPortalFooterUrl(String portalFooterUrl) {
        this.portalFooterUrl = portalFooterUrl;
    }

    public void setRestClient(RESTClient restClient) {
        this.restClient = restClient;
    }
    
    private String get(String url, boolean isAdmin) {
        if (this.restClient == null) {
            return StringUtils.EMPTY;
        }
        
        try {
            return restClient.getJsonRequest(url + "?isAdmin=" + isAdmin, true);
        } catch (JsonSyntaxException ex) {
            return StringUtils.EMPTY;
        } catch (IllegalArgumentException iae) {
            return StringUtils.EMPTY;
        } catch (ResourceAccessException rae) {
            return StringUtils.EMPTY;
        }
    }

    @Override
    @Cacheable(value = Constants.CACHE_PORTAL_DATA, key = "'header' + #isAdmin")
    public String getHeader(boolean isAdmin) {
        return this.get(portalHeaderUrl, isAdmin);
    }

    @Override
    @Cacheable(value = Constants.CACHE_PORTAL_DATA, key = "'footer' + #isAdmin")
    public String getFooter(boolean isAdmin) {
        return this.get(portalFooterUrl, isAdmin);
    }
}
