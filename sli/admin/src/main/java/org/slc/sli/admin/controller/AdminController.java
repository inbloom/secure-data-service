package org.slc.sli.admin.controller;

import org.slc.sli.admin.client.RESTClient;

/**
 * Base class for any controller needing access to the {@link RESTClient}
 * 
 * @author pwolf
 */
public abstract class AdminController {
    
    protected RESTClient restClient = null;
    
    public void setClient(RESTClient client) {
        this.restClient = client;
    }
    
    public RESTClient geClient() {
        return this.restClient;
    }

}
