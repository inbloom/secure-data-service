package org.slc.sli.manager;

import org.slc.sli.client.APIClient;

/**
 *
 * Superclass for manager classes.
 *
 * @author dwu
 *
 */
public abstract class Manager {

    protected APIClient apiClient;

    public APIClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(APIClient apiClient) {
        this.apiClient = apiClient;
    }

}
