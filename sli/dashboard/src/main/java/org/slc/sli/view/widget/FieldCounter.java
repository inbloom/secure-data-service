package org.slc.sli.view.widget;

import org.slc.sli.config.Field;
import org.slc.sli.view.AggregateResolver;

import java.util.Map;

/**
 * Simple class to return the count of a field in a series of objects
 */
public class FieldCounter {

    private Map student;
    private Field field;
    private AggregateResolver resolver;

    public FieldCounter(Field field, Map student, AggregateResolver resolver) {
        this.student = student;
        this.field = field;
        this.resolver = resolver;
    }

    public String getText() {
        return "" + resolver.getCountForPath(field);
    }

    /**
     * Meant to return the color class to help style this color value.
     * @return string class to represent the value in this column
     */
    public String getColor() {
        return "black";
    }
}
