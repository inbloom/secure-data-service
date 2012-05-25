package org.slc.sli.entity.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;

/**
 * Sorting class for Parents Contact information based on priority rules
 * 
 * @author tosako
 * 
 */
public class ParentsSorter implements Comparator<LinkedHashMap<String, Object>> {
    private static Map<String, Integer> primaryContactStatusPriority;
    private static Map<String, Integer> relationPriority;
    static {
        primaryContactStatusPriority = new HashMap<String, Integer>();
        primaryContactStatusPriority.put("true", 1);
        primaryContactStatusPriority.put(null, 2);
        primaryContactStatusPriority.put("false", 3);
        
        relationPriority = new HashMap<String, Integer>();
        relationPriority.put("Parent", 1);
        relationPriority.put("Mother", 2);
        relationPriority.put("Father", 3);
        relationPriority.put("Adoptive parents", 4);
        relationPriority.put("Court appointed guardian", 5);
        relationPriority.put("Mother, foster", 6);
        relationPriority.put("Father, foster", 7);
    }
    
    private static final int SORT_BY_NUMBERS = 0;
    private static final int SORT_BY_ALPHABETS = 1;
    
    private String targetField = "";
    private Map<String, Integer> priorities = null;
    private int sortBy = -1;
    
    public ParentsSorter(String targetField, Map<String, Integer> priorities) {
        this.targetField = targetField;
        this.priorities = priorities;
    }
    
    public ParentsSorter(String targetField, int sortBy) {
        this.targetField = targetField;
        this.sortBy = sortBy;
    }
    
    /**
     * Return sorted Parents contact information
     * 
     * @param genericEntities
     * @return
     */
    public static List<GenericEntity> sort(List<GenericEntity> genericEntities) {
        
        // Fifth, by “Relation”, in the following order: Parent; Mother; Father; Adoptive parents;
        // Court appointed guardian; Mother, foster; Father, foster; thereafter, alphabetically.
        ParentsSorter parentsContactSorter = new ParentsSorter(Constants.ATTR_RELATION, SORT_BY_ALPHABETS);
        Collections.sort(genericEntities, parentsContactSorter);
        
        parentsContactSorter = new ParentsSorter(Constants.ATTR_RELATION, relationPriority);
        Collections.sort(genericEntities, parentsContactSorter);
        
        // Second, by ContactPriority (lower numbers indicate higher priority)
        parentsContactSorter = new ParentsSorter(Constants.ATTR_CONTACT_PRIORITY, SORT_BY_NUMBERS);
        Collections.sort(genericEntities, parentsContactSorter);
        
        // First, by PrimaryContactStatus, in the order true, null, false.
        parentsContactSorter = new ParentsSorter(Constants.ATTR_PRIMARY_CONTACT_STATUS, primaryContactStatusPriority);
        Collections.sort(genericEntities, parentsContactSorter);
        
        return genericEntities;
    }
    
    @Override
    public int compare(LinkedHashMap<String, Object> o1, LinkedHashMap<String, Object> o2) {
        
        int compareResult = 0;
        // temporary assigning priority. Make it lowest possible.
        int o1Priority = Integer.MAX_VALUE;
        int o2Priority = Integer.MAX_VALUE;
        
        Object targetPriority1 = null;
        Object targetPriority2 = null;
        
        List<LinkedHashMap<String, Object>> o1StudentParentAssociation = null;
        // read studentParentAssocation element
        if (o1 != null)
            o1StudentParentAssociation = (List<LinkedHashMap<String, Object>>) o1
                    .get(Constants.ATTR_STUDENT_PARENT_ASSOCIATION);
        // if there is the element available, access targetField
        if (o1StudentParentAssociation != null && !o1StudentParentAssociation.isEmpty()) {
            LinkedHashMap<String, Object> studentParentAssociation = o1StudentParentAssociation.get(0);
            targetPriority1 = studentParentAssociation.get(this.targetField);
        }
        
        List<LinkedHashMap<String, Object>> o2StudentParentAssociation = null;
        // read studentParentAssocation element
        if (o2 != null)
            o2StudentParentAssociation = (List<LinkedHashMap<String, Object>>) o2
                    .get(Constants.ATTR_STUDENT_PARENT_ASSOCIATION);
        if (o2StudentParentAssociation != null && !o2StudentParentAssociation.isEmpty()) {
            LinkedHashMap<String, Object> studentParentAssociation = o2StudentParentAssociation.get(0);
            targetPriority2 = studentParentAssociation.get(this.targetField);
        }
        
        if (this.sortBy == SORT_BY_NUMBERS) {
            // google json treats integer as double. use Double parser then cast to int
            try {
                o1Priority = (int) Double.parseDouble(targetPriority1.toString());
            } catch (Exception e) {
                o1Priority = Integer.MAX_VALUE;
            }
            try {
                o2Priority = (int) Double.parseDouble(targetPriority2.toString());
            } catch (Exception e) {
                o2Priority = Integer.MAX_VALUE;
            }
            compareResult = o1Priority == o2Priority ? 0 : (o1Priority < o2Priority ? -1 : 1);
        } else if (this.sortBy == SORT_BY_ALPHABETS) {
            // sort by alphabetical order (not case sensitive)
            if (targetPriority1 == null)
                targetPriority1 = "";
            if (targetPriority2 == null)
                targetPriority2 = "";
            compareResult = targetPriority1.toString().compareToIgnoreCase(targetPriority2.toString());
        } else if (this.priorities != null) {
            // if custom defined priority is available, use the list
            Integer priority = this.priorities.get(targetPriority1 == null ? null : targetPriority1.toString());
            if (priority != null) {
                o1Priority = priority.intValue();
            }
            priority = this.priorities.get(targetPriority2 == null ? null : targetPriority2.toString());
            if (priority != null) {
                o2Priority = priority.intValue();
            }
            compareResult = o1Priority == o2Priority ? 0 : (o1Priority < o2Priority ? -1 : 1);
        }
        
        return compareResult;
    }
}
