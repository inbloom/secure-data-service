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


package org.slc.sli.manager.impl;

import com.googlecode.ehcache.annotations.Cacheable;

import org.apache.commons.lang3.StringUtils;

import org.slc.sli.client.RESTClient;
import org.slc.sli.manager.PortalWSManager;


/**
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

    @Override
    @Cacheable(cacheName = "user.portal.header")
    public String getHeader(boolean isAdmin) {
        try {
            return restClient.getJsonRequest(portalHeaderUrl + "?isAdmin=" + isAdmin, true);
        } catch (Throwable t) {
            return StringUtils.EMPTY;
        }
    }

    @Override
    @Cacheable(cacheName = "user.portal.footer")
    public String getFooter(boolean isAdmin) {
        try {
            return restClient.getJsonRequest(portalFooterUrl + "?isAdmin=" + isAdmin, true);
        } catch (Throwable t) {
            return StringUtils.EMPTY;
        }
    }
}