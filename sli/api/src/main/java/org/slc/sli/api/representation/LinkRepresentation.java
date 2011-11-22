package org.slc.sli.api.representation;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * An AtomPub style link.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */
@XmlRootElement(name = "link")
public class LinkRepresentation {
    
    /** The relation */
    private String rel;
    
    /** The URI */
    
    private String href;
    
    public LinkRepresentation() {
    }
    
    public LinkRepresentation(String rel, String href) {
        this.rel = rel;
        this.href = href;
    }
    
    /**
     * @return the rel
     */
    public String getRel() {
        return rel;
    }
    
    /**
     * @param rel
     *            the rel to set
     */
    public void setRel(String rel) {
        this.rel = rel;
    }
    
    /**
     * @return the href
     */
    public String getHref() {
        return href;
    }
    
    /**
     * @param href
     *            the href to set
     */
    public void setHref(String href) {
        this.href = href;
    }
    
}
