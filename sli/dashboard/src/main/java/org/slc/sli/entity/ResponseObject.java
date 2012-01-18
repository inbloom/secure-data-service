package org.slc.sli.entity;

public class ResponseObject {

    String id;
    InnerResponse link;
    
    public class InnerResponse{
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
    
    
    public String getId(){
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getHref(){
        return link.getHref();
    }
    
    public String getRel() {
        return link.getRel();
    }
    
}
