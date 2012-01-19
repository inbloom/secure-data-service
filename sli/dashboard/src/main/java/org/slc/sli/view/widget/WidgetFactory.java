package org.slc.sli.view.widget;

import org.slc.sli.config.Field;
import org.slc.sli.entity.Student;
import org.slc.sli.view.AssessmentResolver;

/**
 * Factory for creating widget helper objects. 
 * An instance of WidgetFactory is passed in the model map to the FreeMarker view templates.
 * 
 * @author dwu
 *
 */
public class WidgetFactory {
    
    public static ColorByPerf createColorByPerf(Field field, Student student, AssessmentResolver assmts) {
        return new ColorByPerf(field, student, assmts);
    }
    public static FuelGauge createFuelGauge(Field field, Student student, AssessmentResolver assmts) {
        return new FuelGauge(field, student, assmts);
    }
    
}
