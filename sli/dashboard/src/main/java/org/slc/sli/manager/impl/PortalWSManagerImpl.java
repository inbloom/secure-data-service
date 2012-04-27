package org.slc.sli.manager.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.client.LiveAPIClient;
import org.slc.sli.manager.PortalWSManager;

//import com.googlecode.ehcache.annotations.Cacheable;

/**
 *
 * @author svankina
 *
 */
public class PortalWSManagerImpl implements PortalWSManager {

    @Autowired
    LiveAPIClient apiClient;


    //@Cacheable(cacheName = "user.header")
    @Override
    public String getHeader(String token) {
        try {
            return apiClient.getHeader(token);
        } catch (Throwable t) {
            return StringUtils.EMPTY;
        }
    }

    //@Cacheable(cacheName = "user.footer")
    @Override
    public String getFooter(String token) {
        try {
            return apiClient.getFooter(token);
        } catch (Throwable t) {
            return StringUtils.EMPTY;
        }
    }

    public void setApiClient(LiveAPIClient apiClient) {
        this.apiClient = apiClient;
    }

}
