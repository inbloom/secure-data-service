package org.slc.sli.entity;

/**
 * 
 * @author svankina
 *TODO: javadoc
 */
public class ResponseObject {

    String id;
    InnerResponse[] links;
    
    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }   


    public InnerResponse[] getLinks() {
        return links;
    }


    public void setLinks(InnerResponse[] links) {
        this.links = links;
    }

 
}
