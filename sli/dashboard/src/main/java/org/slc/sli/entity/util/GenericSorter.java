package org.slc.sli.entity.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


/**
 * sorting for elements in GenericEntity by given Map<String,Integer> Priority.
 */
public class GenericSorter implements Comparator<Map<String, Object>> {
    private String fieldName = "";
    private Map<String, Integer> priorityList = null;

    /**
     * sort by given field name of JSON.
     *
     * @param fieldName
     *            field name of JSON for sorting
     * @param priorityList
     *            Stirng: name of type, Integer priority (1 is the heigest priority)
     */
    public GenericSorter(String fieldName, Map<String, Integer> priorityList) {
        this.fieldName = fieldName;

        if (priorityList == null) {
            this.priorityList = new HashMap<String, Integer>();
        } else {
            this.priorityList = priorityList;
        }
    }

    @Override
    public int compare(Map<String, Object> o1, Map<String, Object> o2) {

        // temporary assigning priority. Make it lowest possible.
        int o1Priority = Integer.MAX_VALUE;
        int o2Priority = Integer.MAX_VALUE;

        Object o1Type = o1.get(this.fieldName);
        Object o2Type = o2.get(this.fieldName);

        // find true priority
        if (o1Type != null && this.priorityList.containsKey(o1Type.toString())) {
                o1Priority = this.priorityList.get(o1Type.toString());
        }
        if (o2Type != null && this.priorityList.containsKey(o2Type.toString())) {
                o2Priority = this.priorityList.get(o2Type.toString());
        }

        return o1Priority == o2Priority ? 0 : (o1Priority < o2Priority ? -1 : 1);
    }
}
