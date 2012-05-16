package org.slc.sli.entity;

import java.io.Serializable;
import java.util.Map;

import javax.validation.Valid;

import org.slc.sli.web.util.NoBadChars;


/**
 * Collection of Config objects for custom config store
 * @author agrebneva
 *
 */
public class ConfigMap implements Serializable {
    private static final long serialVersionUID = 1L;
    @Valid
    @NoBadChars(depth = 1)
    private Map<String, Config> config;

    public Map<String, Config> getConfig() {
        return config;
    }

    public Config getComponentConfig(String componentId) {
        return config.get(componentId);
    }

    public void setConfig(Map<String, Config> config) {
        this.config = config;
    }

    public int size() {
        return config.size();
    }

    public boolean isEmpty() {
        return config == null || config.isEmpty();
    }
}
