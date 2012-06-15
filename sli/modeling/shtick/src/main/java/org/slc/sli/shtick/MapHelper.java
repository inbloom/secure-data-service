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

    private static List<Object> copyList(List toCopy) {
        final List<Object> copy = new ArrayList<Object>(toCopy.size());
        for (Object elem : toCopy) {
            copy.add(copyValue(elem));
        }
        return copy;
    }

    private static Object copyValue(Object elem) {
        if (elem instanceof Map) {
            return deepCopy((Map<String, Object>) elem);
        } else if (elem instanceof List) {
            return copyList((List) elem);
        } else { // We have a primitive
            return elem;
        }
    }
}
