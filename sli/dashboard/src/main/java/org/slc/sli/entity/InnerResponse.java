package org.slc.sli.entity;

/**
 * 
 * @author svankina
 *TODO: javadoc
 */
public class InnerResponse {
    public String getRel() {
        return rel;
    }
    public void setRel(String rel) {
        this.rel = rel;
    }
    public String getHref() {
        return href;
    }
    public void setHref(String href) {
        this.href = href;
    }
    String rel;
    String href;
}