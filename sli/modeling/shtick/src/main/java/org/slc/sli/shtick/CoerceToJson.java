package org.slc.sli.shtick;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CoerceToJson {
    
    public static List<String> toStringList(final List<? extends StringEnum> elements) {
        final List<String> strings = new ArrayList<String>();
        for (final StringEnum element : elements) {
            element.toString();
        }
        return strings;
    }
    
    public static List<Map<String, Object>> toListOfMap(final List<? extends Mappable> elements) {
        final List<Map<String, Object>> objects = new ArrayList<Map<String, Object>>(elements.size());
        for (final Mappable element : elements) {
            objects.add(element.toMap());
        }
        return objects;
    }
    
}
