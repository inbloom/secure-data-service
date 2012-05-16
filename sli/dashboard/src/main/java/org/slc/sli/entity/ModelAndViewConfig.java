package org.slc.sli.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 * Component aggregate which contains all necessary data and config pieces relevant for component rendering
 * @author agrebneva
 *
 * @NotThreadSafe
 *
 */
public class ModelAndViewConfig {
    //
    private Map<String, GenericEntity> data;
    private Map<String, Config> config;
    private List<Config> layoutItems;
    private Collection<Config> widgetConfig;

    public ModelAndViewConfig() {
        this.data = new LinkedHashMap<String, GenericEntity>();
        this.config = new LinkedHashMap<String, Config>();
        this.layoutItems = new ArrayList<Config>();
        this.widgetConfig = new ArrayList<Config>();
    }

    public Map<String, GenericEntity> getData() {
        return data;
    }

    public void addData(String dataAliasId, GenericEntity data) {
        this.data.put(dataAliasId, data);
    }

    public Map<String, Config> getConfig() {
        return config;
    }

    public void addConfig(String componentId, Config config) {
        this.config.put(componentId, config);
    }

    public void addLayoutItem(Config layoutItem) {
        this.layoutItems.add(layoutItem);
    }

    public List<Config> getLayoutItems() {
        return layoutItems;
    }

    public boolean hasDataForAlias(String dataAliasId) {
        return this.data.containsKey(dataAliasId);
    }

    public void setWidgetConfig(Collection<Config> widgetConfigs) {
        this.widgetConfig = widgetConfigs;
    }

    public Collection<Config> getWidgetConfig() {
        return widgetConfig;
    }
}
