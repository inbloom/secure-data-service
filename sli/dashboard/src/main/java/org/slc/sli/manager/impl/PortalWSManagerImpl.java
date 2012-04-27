package org.slc.sli.manager.impl;

import org.slc.sli.client.LiveAPIClient;
import org.slc.sli.manager.PortalWSManager;
import org.springframework.beans.factory.annotation.Autowired;

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
    public String getHeader(String token) {
        return apiClient.getHeader(token);
    }
    
    //@Cacheable(cacheName = "user.footer")
    public String getFooter(String token) {
        return apiClient.getFooter(token);
    }

    public void setApiClient(LiveAPIClient apiClient) {
        this.apiClient = apiClient;
    }
    
}
