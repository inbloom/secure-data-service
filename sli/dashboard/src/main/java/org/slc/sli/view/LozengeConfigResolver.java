package org.slc.sli.view;

import org.slc.sli.config.LozengeConfig;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * A utility class for views in SLI dashboard. As a wrapper around lozenge configuration data passed onto
 *  dashboard views. Contains useful tools to look up how a user's configuration specifies various
 *  lozenges are displayed.
 *
 * @author syau
 *
 */
public class LozengeConfigResolver {
    Map<String, LozengeConfig> lozengeConfigs;

    /**
     * Constructor
     */
    public LozengeConfigResolver(List<LozengeConfig> s) {
        lozengeConfigs = new HashMap<String, LozengeConfig>();
        for (LozengeConfig lc : s) {
            lozengeConfigs.put(lc.getName(), lc);
        }
    }

    public LozengeConfig get(String name) {
        return lozengeConfigs.get(name);
    }

}
