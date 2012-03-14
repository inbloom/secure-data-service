package org.slc.sli.view.widget;

import java.util.Map;

import org.slc.sli.config.Field;
import org.slc.sli.view.AggregateResolver;

/**
 * Simple class to return the count of a field in a series of objects
 */
public class FieldCounter {

    private Map student;
    private Field field;
    private AggregateResolver resolver;
    private int[] boundaries;

    public FieldCounter(Field field, Map student, AggregateResolver resolver, int[] boundaries) {
        this.student = student;
        this.field = field;
        this.resolver = resolver;
        this.boundaries = boundaries;
    }

    public String getText() {
        if (resolver == null)
            return "";
        return "" + resolver.getCountForPath(field);
    }

    /**
     * Meant to return the color class to help style this color value.
     * @return string class to represent the value in this column
     */
    public int getColorIndex() {
        int level = 1;
        int count = resolver.getCountForPath(field);
        for (int i = 0; boundaries != null && i < boundaries.length; i++) {
            level++;
            if (count <= boundaries[i]) {
                break;
            }
        }
        level--;
        return level;
    }
}
