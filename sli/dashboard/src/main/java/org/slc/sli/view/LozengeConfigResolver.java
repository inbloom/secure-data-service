package org.slc.sli.view;

import org.slc.sli.config.LozengeConfig;

import java.util.List;

/**
 * A utility class for views in SLI dashboard. As a wrapper around lozenge configuration data passed onto
 *  dashboard views. Contains useful tools to look up how a user's configuration specifies various 
 *  lozenges are displayed. 
 * 
 * @author syau
 *
 */
public class LozengeConfigResolver {
    List<LozengeConfig> lozengeConfigs;
    
    /**
     * Constructor
     */
    public LozengeConfigResolver(List<LozengeConfig> s) {
        lozengeConfigs = s;
    }
    
    public LozengeConfig get(String name) {
        for (LozengeConfig c : lozengeConfigs) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }
    
}
