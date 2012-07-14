package org.slc.sli.shtick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jstokes
 */
public final class MapHelper {
    public static Map<String, Object> deepCopy(final Map<String, Object> toCopy) {
        final Map<String, Object> copy = new HashMap<String, Object>(toCopy.size());
        for (Map.Entry<String, Object> entry : toCopy.entrySet()) {
            copy.put(entry.getKey(), copyValue(entry.getValue()));
        }
        return copy;
    }
    
    private static List<Object> copyList(final List<Object> toCopy) {
        final List<Object> copy = new ArrayList<Object>(toCopy.size());
        for (Object elem : toCopy) {
            copy.add(copyValue(elem));
        }
        return copy;
    }
    
    private static Object copyValue(final Object elem) {
        if (elem instanceof Map) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> map = (Map<String, Object>) elem;
            return deepCopy(map);
        } else if (elem instanceof List) {
            @SuppressWarnings("unchecked")
            final List<Object> list = (List<Object>) elem;
            return copyList(list);
        } else { // We have a primitive
            return elem;
        }
    }
}
