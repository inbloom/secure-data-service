package org.slc.sli.view.widget;

import java.util.Map;

import org.slc.sli.config.Field;
import org.slc.sli.view.AggregateRatioResolver;

/**
 * 
 * @author pwolf
 *
 */
public class FieldRate extends ColorByPercent {

    private Map student;
    private Field field;
    private AggregateRatioResolver resolver;

    public FieldRate(Field field, Map student, AggregateRatioResolver resolver, int[] boundaries) {
        this.student = student;
        this.field = field;
        this.resolver = resolver;
        this.setActual(resolver.getCountForPath(field));
        this.setTotal(resolver.getSize(field));
        this.setBoundaries(boundaries);
    }
    
    
    
    
}
