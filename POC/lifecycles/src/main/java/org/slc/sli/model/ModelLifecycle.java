package org.slc.sli.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBList;


/**
 * Model lifecycle information
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 */
public class ModelLifecycle implements Serializable {
        
    // Constants
    private static final long serialVersionUID = -6352647930596227722L;
    
    
    // Attributes
    protected ModelLifecycleState state = ModelLifecycleState.WIP;
    protected String currentPart = "";
    protected String[] parts = {};
    protected String[] dependencies = {};
    protected boolean isCore = true;
    protected String installedOn = "";
    protected String resolvedOn = "";
    protected String activatedOn = "";
    
    
    // Constructors
    public ModelLifecycle() {  
    }
    
    public ModelLifecycle(String currentPart, String[] parts, String[] dependencies) {  
        if (currentPart != null) {
            this.currentPart = currentPart;
        }
        if (parts != null) {
            this.parts = parts;
        }
        if (dependencies != null) {
            this.dependencies = dependencies;
        }
    }
    
    public ModelLifecycle(Map<String, Object> map) {  
        if (map.get("state") != null) {
            this.state = ModelEntity.getEnumFromString(ModelLifecycleState.class, (String)map.get("state"));
        }
        if (map.get("currentPart") != null) {
            this.currentPart = (String)map.get("currentPart");
        }
        if (map.get("parts") != null) {
            this.parts = this.toArray((BasicDBList)map.get("parts"));
        }
        if (map.get("dependencies") != null) {
            this.dependencies = this.toArray((BasicDBList)map.get("dependencies"));
        }
        if (map.get("isCore") != null) {
            this.isCore = Boolean.parseBoolean(map.get("isCore").toString());
        }
        if (map.get("installedOn") != null) {
            this.installedOn = (String)map.get("installedOn");
        }
        if (map.get("resolvedOn") != null) {
            this.resolvedOn = (String)map.get("resolvedOn");
        }
        if (map.get("activatedOn") != null) {
            this.activatedOn = (String)map.get("activatedOn");
        }
    }
    
    public ModelLifecycle(ModelLifecycle existingModelLifecycle) {
        this.state = existingModelLifecycle.getState();
        this.currentPart = existingModelLifecycle.getCurrentPart();
        this.parts = Arrays.copyOf(existingModelLifecycle.getParts(), existingModelLifecycle.getParts().length);
        this.dependencies = Arrays.copyOf(existingModelLifecycle.getDependencies(), existingModelLifecycle.getDependencies().length);
        this.isCore = existingModelLifecycle.isCore();
        this.installedOn = existingModelLifecycle.getInstalledOn();
        this.resolvedOn = existingModelLifecycle.getResolvedOn();
        this.activatedOn = existingModelLifecycle.getActivatedOn();
    }
    
    
    // Methods
    public ModelLifecycleState getState() {
        return state;
    }
   
    public void setState(ModelLifecycleState state) {
        this.state = state;
    }
    
    public String getCurrentPart() {
        return currentPart;
    }
   
    public void setCurrentPart(String currentPart) {
        this.currentPart = currentPart;
    }
    
    public String[] getParts() {
        return parts;
    }

    public void setParts(String[] parts) {
        this.parts = parts;
    }
   
    public String[] getDependencies() {
        return dependencies;
    }

    public void setDependencies(String[] dependencies) {
        this.dependencies = dependencies;
    }
   
    public boolean isCore() {
        return isCore;
    }
   
    public void setCore(boolean setting) {
        this.isCore = setting;
    }
    
    public String getInstalledOn() {
        return installedOn;
    }
   
    public void setInstalledOn(String installedOn) {
        this.installedOn = installedOn;
    }
    
    public String getResolvedOn() {
        return resolvedOn;
    }
   
    public void setResolvedOn(String resolvedOn) {
        this.resolvedOn = resolvedOn;
    }
    
    public String getActivatedOn() {
        return activatedOn;
    }
   
    public void setActivatedOn(String activatedOn) {
        this.activatedOn = activatedOn;
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        
        if (this.state != null) {
            map.put("state", this.state.toString());
        }
        if (this.currentPart != null) {
            map.put("currentPart", this.currentPart);
        }
        if (this.parts != null) {
            map.put("parts", this.parts);
        }
        if (this.dependencies != null) {
            map.put("dependencies", this.dependencies);
        }
        if ((this.installedOn != null) && (this.installedOn.length() > 0)) {
            map.put("installedOn", this.installedOn);
        }
        if ((this.resolvedOn != null) && (this.resolvedOn.length() > 0)) {
            map.put("resolvedOn", this.resolvedOn);
        }
        if ((this.activatedOn != null) && (this.activatedOn.length() > 0)) {
            map.put("activatedOn", this.activatedOn);
        }
        
        return map;
    }
    
    private String[] toArray(BasicDBList dbList) {
        String[] array = new String[dbList.size()];
        
        int index = 0;
        for (Object item : dbList) {
            array[index] = item.toString();
            index++;
        }
        
        return array;
    }
}
