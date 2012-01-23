package org.slc.sli.api.representation;

import java.util.HashMap;

/**
 * Wrapper around CollectionResponse to give structure to XML responses
 * 
 */
public class Associations extends HashMap<String, Object> {
    
    private static final long serialVersionUID = 3535181277131542612L;
    
    public Associations(CollectionResponse collectionResponse) {
        super();
        this.put("Association", collectionResponse);
    }
}
