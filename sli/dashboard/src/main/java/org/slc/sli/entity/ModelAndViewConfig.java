package org.slc.sli.entity;

import java.util.ArrayList;
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
    private Map<String, Config> componentViewConfigMap;
    private List<Config> layoutItems;
    
    public ModelAndViewConfig() {
        this.data = new LinkedHashMap<String, GenericEntity>();
        this.componentViewConfigMap = new LinkedHashMap<String, Config>();
        this.layoutItems = new ArrayList<Config>();
    }
    
    public Map<String, GenericEntity> getData() {
        return data;
    }
    
    public void addData(String dataAliasId, GenericEntity data) {
        this.data.put(dataAliasId, data);
    }

    public Map<String, Config> getComponentViewConfigMap() {
        return componentViewConfigMap;
    }
    
    public void addComponentViewConfigMap(String componentId, Config componentViewConfigMap) {
        this.componentViewConfigMap.put(componentId, componentViewConfigMap);
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
}
