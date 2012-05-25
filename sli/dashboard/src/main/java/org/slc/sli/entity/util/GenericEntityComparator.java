package org.slc.sli.entity.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Comparator for elements in GenericEntity by a specified Map<String,Integer> Priority or the
 * Object's Comparable interface.
 */
public class GenericEntityComparator implements Comparator<HashMap<String, Object>> {
    /** field name being used for comparisons */
    private String fieldName = "";
    /** priority list optionally used to effect comparisons */
    private Map<String, Integer> priorityList = null;

    /**
     * Compare by given field name of JSON. Use specified priority list for comparisons.
     *
     * @param fieldName
     *            field name of JSON for comparison
     * @param priorityList
     *            String: name of type, Integer priority (1 is the highest priority)
     */
    public GenericEntityComparator(String fieldName, Map<String, Integer> priorityList) {
        this.fieldName = fieldName;

        if (priorityList == null)
            this.priorityList = new HashMap<String, Integer>();
        else
            this.priorityList = priorityList;
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
     *
     */
    public GenericEntityComparator(String fieldName) {
        this.fieldName = fieldName;
        this.priorityList = null;
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
            if (o1Type != null && this.priorityList.containsKey(o1Type.toString())) {
                o1Priority = this.priorityList.get(o1Type.toString());
            }
            if (o2Type != null && this.priorityList.containsKey(o2Type.toString())) {
                o2Priority = this.priorityList.get(o2Type.toString());
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
                return ((Comparable) o1Type).compareTo((Comparable) o2Type);
            } catch (ClassCastException cce) {
                // does not implement Comparable, cannot be compared
                return 0;
            }
        }
    }
}
