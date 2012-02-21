package org.slc.sli.validation;

import java.util.HashMap;
import java.util.Map;

import org.slc.sli.domain.Entity;


/**
 * Various utility functions for test
 * 
 * @author nbrown
 *
 */
public class ValidationTestUtils {

    public static Entity makeDummyEntity(final String type, final String id) {
        return new Entity() {
            
            @Override
            public String getType() {
                return type;
            }
            
            @Override
            public Map<String, Object> getMetaData() {
                return new HashMap<String, Object>();
            }
            
            @Override
            public String getEntityId() {
                return id;
            }
            
            @Override
            public Map<String, Object> getBody() {
                return new HashMap<String, Object>();
            }
        };
    }
    
}
