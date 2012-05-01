package org.slc.sli.entity;

import java.util.LinkedHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CustomConfig is the custom configuration which can be uploaded by an IT Administrator
 * to allow customized Dashboard pages for a designated Educational Organization.
 * 
 * @author rbloh
 */
public class CustomConfig extends LinkedHashMap<String, Config> {
    
    private static final Logger LOG = LoggerFactory.getLogger(CustomConfig.class);
    
    /**
     * Utility method to formalize CustomConfig JSON input from administrators.
     * 
     * @param The
     *            raw JSON to formalize.
     * @return The formal JSON.
     */
    public static String formalizeJson(String json) {
        String formalizedJson = json;
        try {
            Gson gson = new GsonBuilder().create();
            CustomConfig customConfig = gson.fromJson(json, CustomConfig.class);
            if (customConfig != null) {
                formalizedJson = gson.toJson(customConfig);
            }
        } catch (Exception exception) {
            LOG.warn("JSON Parse Error: {}", exception.getMessage());
        }
        return formalizedJson;
    }

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
