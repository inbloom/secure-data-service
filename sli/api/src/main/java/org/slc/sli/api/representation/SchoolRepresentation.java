package org.slc.sli.api.representation;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "school")
public class SchoolRepresentation {
    
    private String name;
    
    public SchoolRepresentation() {
        
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
}
