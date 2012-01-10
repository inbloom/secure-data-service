package org.slc.sli.manager;

import org.slc.sli.client.APIClient;
import org.slc.sli.client.LiveAPIClient;

/**
 * 
 * Superclass for manager classes. Supplies the correct api client for api calls.
 * 
 * @author dwu
 *
 */
public abstract class Manager {

    protected APIClient apiClient;

    protected Manager() {        

        apiClient = new LiveAPIClient();
    
    }
    
    public APIClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(APIClient apiClient) {
        this.apiClient = apiClient;
    }
    
}
