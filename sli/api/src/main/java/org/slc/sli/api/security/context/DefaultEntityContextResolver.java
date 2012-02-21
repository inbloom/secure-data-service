package org.slc.sli.api.security.context;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;

/**
 * Default context traversing implementation that allows access to everything
 */
@Component
public class DefaultEntityContextResolver implements EntityContextResolver {
    /**
     * List that always says 'YES' I have it
     */
    public static final List<String> SUPER_LIST = new AbstractList<String>() {
        
        @Override
        public boolean contains(Object obj) {
            return true;
        }
        
        @Override
        public boolean containsAll(Collection<?> c) {
            return true;
        }
        
        @Override
        public String get(int index) {
            return "";
        }
        
        @Override
        public int size() {
            return -1;
        }
        
    };
    
    @Override
    public List<String> findAccessible(Entity principal) {
        return SUPER_LIST;
    }
    
    @Override
    public String getSourceType() {
        return null;
    }
    
    @Override
    public String getTargetType() {
        return null;
    }
    
}
