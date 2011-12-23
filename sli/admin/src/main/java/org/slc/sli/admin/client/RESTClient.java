package org.slc.sli.admin.client;

import com.google.gson.JsonArray;

/**
 * 
 * @author pwolf
 *
 */
public interface RESTClient {
    
    public JsonArray getRoles(String token);

}
