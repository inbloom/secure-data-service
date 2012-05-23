package org.slc.sli.entity.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.entity.GenericEntity;

/**
 * Sorting class for Parents Contact information based on priority rules
 * 
 * @author tosako
 * 
 */
public class ParentsContactSorter {
    private static Map<String, Integer> primaryContactStatusPriority;
    static {
        primaryContactStatusPriority = new HashMap<String, Integer>();
        primaryContactStatusPriority.put("ture", 1);
        primaryContactStatusPriority.put(null, 2);
        primaryContactStatusPriority.put("false", 3);
    }
    
    /**
     * Return sorted Parents contact information
     * @param genericEntities
     * @return
     */
    public static List<GenericEntity> sort(List<GenericEntity> genericEntities) {
        
        //Second, by ContactPriority (lower numbers indicate higher priority)
        GenericSorter genericSorter = new GenericSorter("contactPriority", null);
        Collections.sort(genericEntities, genericSorter);
        
        //First, by PrimaryContactStatus, in the order true, null, false.
        genericSorter = new GenericSorter("primaryContactStatus", primaryContactStatusPriority);
        Collections.sort(genericEntities, genericSorter);
        return genericEntities;
    }
}
