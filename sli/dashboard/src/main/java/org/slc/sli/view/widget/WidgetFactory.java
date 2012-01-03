package org.slc.sli.view.widget;

import org.slc.sli.config.Field;
import org.slc.sli.entity.Student;
import org.slc.sli.view.AssessmentResolver;

/**
 * 
 * @author dwu
 *
 */
public class WidgetFactory {
    
    public ColorByPerfLevel createColorByPerfLevel(Field field, Student student, AssessmentResolver assmts) {
        return new ColorByPerfLevel(field, student, assmts);
    }
    
}
