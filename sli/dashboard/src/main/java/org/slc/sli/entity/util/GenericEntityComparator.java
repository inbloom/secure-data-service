package org.slc.sli.entity.util;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Comparator for elements in GenericEntity by a specified Map<String,Integer> Priority or the
 * Object's Comparable interface.
 */
public class GenericEntityComparator implements Comparator<HashMap<String, Object>> {
    /** field name being used for comparisons */
    private String fieldName = "";
    /** priority list optionally used to effect comparisons */
    private Map<?, Integer> priorityList = null;
    
    private Class<?> castingClass = String.class;
    
    /**
     * Compare by given field name of JSON. Use specified priority list for comparisons.
     * 
     * @param fieldName
     *            field name of JSON for comparison
     * @param priorityList
     *            String: name of type, Integer priority (1 is the highest priority)
     */
    public GenericEntityComparator(String fieldName, Map<?, Integer> priorityList) {
        this.fieldName = fieldName;
        
        if (priorityList == null)
            this.priorityList = Collections.emptyMap();
        else {
            this.priorityList = priorityList;
            
            // determine the class of key.
            if (!this.priorityList.isEmpty()) {
                Set<?> keys = this.priorityList.keySet();
                if (keys != null && !keys.isEmpty()) {
                    Iterator<?> i = keys.iterator();
                    while (i.hasNext()) {
                        Object o = i.next();
                        // if an object is null, find next available.
                        if (o == null)
                            continue;
                        //just need to find the class once.
                        this.castingClass = i.next().getClass();
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Compare by given field name of JSON. This method does not use a priorityList for comparisons
     * but rather leverages the Comparable interface being implemented by the specified field's
     * underlying object. Note that String, Integer, Float etc. implement Comparable and behave as
     * expected (e.g. Strings compare alphabetically, case sensitive). If the objects in the
     * specified field do not implement Comparable, using this constructor will result in no actual
     * comparisons being made - all objects will be treated as equal.
     * 
     * @param fieldName
     *            field name of JSON for comparison
     * @param targetClass
     *            class which implemented Comparable. JSON value will be re-instantiate with this
     *            class in order to compare two objects
     * 
     */
    public GenericEntityComparator(String fieldName, Class<? extends Comparable<?>> targetClass) {
        this.fieldName = fieldName;
        this.priorityList = null;
        this.castingClass = targetClass;
    }
    
    @Override
    public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
        
        // get the objects for the field being compared
        Object o1Type = o1.get(this.fieldName);
        Object o2Type = o2.get(this.fieldName);
        
        if (priorityList != null) {
            // compare using the defined priority list
            
            // Note that this does not behave "as expected"; i.e. if the underlying objects are
            // Strings, they will not be compared alphabetically. Rather, the ones with values
            // defined in the priority list will take precedence over others with no such values
            // (which are all equal as far as this Comparator is concerned)
            
            // temporary assigning priority. Make it lowest possible.
            int o1Priority = Integer.MAX_VALUE;
            int o2Priority = Integer.MAX_VALUE;
            
            // find true priority
            if (o1Type != null) {
                // if targetClass is other than String class,
                // then re-instantiate to an appropriate object.
                if (String.class.equals(this.castingClass)) {
                    // if o1Type is not String object, then get String
                    if (!o1Type.getClass().equals(this.castingClass)) {
                        o1Type = o1Type.toString();
                    }
                } else {
                    try {
                        Constructor<?> constructor = this.castingClass.getConstructor(String.class);
                        o1Type = constructor.newInstance(o1Type.toString());
                    } catch (Exception e) {
                        o1Priority = Integer.MAX_VALUE;
                    }
                }
                if (this.priorityList.containsKey(o1Type)) {
                    o1Priority = this.priorityList.get(o1Type);
                }
            }
            if (o2Type != null) {
                // if targetClass is other than String class,
                // then re-instantiate to an appropriate object.
                if (String.class.equals(this.castingClass)) {
                    // if o12ype is not String object, then get String
                    if (!o2Type.getClass().equals(this.castingClass)) {
                        o2Type = o2Type.toString();
                    }
                } else {
                    try {
                        Constructor<?> constructor = this.castingClass.getConstructor(String.class);
                        o2Type = constructor.newInstance(o2Type.toString());
                    } catch (Exception e) {
                        o2Priority = Integer.MAX_VALUE;
                    }
                }
                if (this.priorityList.containsKey(o2Type)) {
                    o2Priority = this.priorityList.get(o2Type);
                }
            }
            
            // if a field's value is present in the priority list, it has precedence
            // otherwise, all field values not in the priority list have equal precedence
            return o1Priority == o2Priority ? 0 : (o1Priority < o2Priority ? -1 : 1);
        } else {
            // compare using the underlying object's Comparable interface
            
            // This comparison behaves "as expected", e.g. smaller Integers come before bigger
            // Integers, Strings compare alphabetically (case sensitive) etc.
            
            // Note that, currently, if the underlying object does not implement Comparable, it
            // cannot be compared by this class unless you use the priorityList option
            try {
                Comparable o1TypeComparable = null;
                Comparable o2TypeComparable = null;
                
                // if castingClass is String, then just simply cast
                if (String.class.equals(this.castingClass)) {
                    o1TypeComparable = (String) o1Type;
                    o2TypeComparable = (String) o2Type;
                } else {
                    try {
                        // throwing exception is very expensive.
                        // so, check an object is null or not before calling toString method.
                        // if an object is null, then the object has the lowest priority
                        if (o1Type != null && o2Type == null)
                            return -1;
                        else if (o1Type == null && o2Type != null)
                            return 1;
                        else if (o1Type == null && o2Type == null)
                            return 0;
                        
                        // Let's assume that all Comparable implemented class takes one String
                        // object parameter in its constructor.
                        Constructor<?> constructor = this.castingClass.getConstructor(String.class);
                        o1TypeComparable = (Comparable) constructor.newInstance(o1Type.toString());
                        o2TypeComparable = (Comparable) constructor.newInstance(o2Type.toString());
                    } catch (Exception e) {
                        return 0;
                    }
                    
                }
                return o1TypeComparable.compareTo(o2TypeComparable);
            } catch (ClassCastException cce) {
                // does not implement Comparable, cannot be compared
                return 0;
            }
        }
    }
}
