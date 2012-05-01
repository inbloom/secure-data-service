package org.slc.sli.entity;

import java.util.LinkedHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * CustomConfig is the custom configuration which can be uploaded by an IT Administrator
 * to allow customized Dashboard pages for a designated Educational Organization.
 * 
 * @author rbloh
 */
public class CustomConfig extends LinkedHashMap<String, Config> {
    
    /**
     * Constructors for CustomConfig.
     * 
     */
    public CustomConfig() {
        super();
    }

    /**
     * Get the JSON representation of the CustomConfig.
     * 
     * @return The education organization's custom configuration JSON.
     * 
     */
    public String toJson() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }
    
}
