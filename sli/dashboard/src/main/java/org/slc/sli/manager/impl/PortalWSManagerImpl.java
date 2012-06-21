package org.slc.sli.manager.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;

import org.slc.sli.client.RESTClient;
import org.slc.sli.manager.PortalWSManager;
import org.slc.sli.util.Constants;




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

    @Override
    @Cacheable(value = Constants.CACHE_PORTAL_DATA, key = "'header' + #isAdmin")
    public String getHeader(boolean isAdmin) {
        try {
            return restClient.getJsonRequest(portalHeaderUrl + "?isAdmin=" + isAdmin, true);
        } catch (Throwable t) {
            return StringUtils.EMPTY;
        }
    }

    @Override
    @Cacheable(value = Constants.CACHE_PORTAL_DATA, key = "'footer' + #isAdmin")
    public String getFooter(boolean isAdmin) {
        try {
            return restClient.getJsonRequest(portalFooterUrl + "?isAdmin=" + isAdmin, true);
        } catch (Throwable t) {
            return StringUtils.EMPTY;
        }
    }
}