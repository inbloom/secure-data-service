package org.slc.sli.dal.lifecycle;

import java.io.Serializable;


/**
 * Model lifecycle states
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 */
public enum ModelLifecycleState implements Serializable {
    
    WIP("WIP"),
    INSTALLED("Installed"),
    RESOLVED("Resolved"),
    ACTIVE("Active"),
    DEACTIVATED("Deactivated"),
    INVALID("Invalid");
    
    private String value;
    
    private ModelLifecycleState(String value) {
        this.value = value;
    }
    
    public String value() {
        return this.value;
    }
    
}
