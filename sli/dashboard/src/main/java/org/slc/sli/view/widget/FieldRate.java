package org.slc.sli.view.widget;

import java.util.Map;

import org.slc.sli.config.Field;
import org.slc.sli.view.AggregateRatioResolver;

public class FieldRate extends ColorByPercent {

    private Map student;
    private Field field;
    private AggregateRatioResolver resolver;

    public FieldRate(Field field, Map student, AggregateRatioResolver resolver) {
        this.student = student;
        this.field = field;
        this.resolver = resolver;
        this.setActual(resolver.getSubCountForPath(field));
        this.setTotal(resolver.getTotalCountForPath(field));
    }
    
    
    
    
}
