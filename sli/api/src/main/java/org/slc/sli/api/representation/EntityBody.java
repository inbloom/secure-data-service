package org.slc.sli.api.representation;

import java.util.HashMap;
import java.util.Map;

/**
 * Contents of an entity body
 * 
 * @author nbrown
 * 
 */
public class EntityBody extends HashMap<String, Object> {
    
    private static final long serialVersionUID = -301785504415342449L;
    
    public EntityBody() {
        super();
    }
    
    public EntityBody(Map<? extends String, ? extends Object> m) {
        super(m);
    }
    
}
