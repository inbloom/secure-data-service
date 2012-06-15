package org.slc.sli.manager;

import org.slc.sli.client.APIClient;
import org.slc.sli.util.SecurityUtil;

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
