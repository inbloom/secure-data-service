package org.slc.sli.view.widget;

import java.util.Map;

import org.slc.sli.config.Field;
import org.slc.sli.view.AssessmentResolver;
import org.slc.sli.view.AttendanceResolver;
import org.slc.sli.view.LozengeConfigResolver;
import org.slc.sli.view.StudentResolver;

/**
 * Factory for creating widget helper objects.
 * An instance of WidgetFactory is passed in the model map to the FreeMarker view templates.
 *
 * @author dwu
 *
 */
public class WidgetFactory {

    
    public static ColorByPerf createColorByPerf(Field field, Map student, AssessmentResolver assmts) {
        return new ColorByPerf(field, student, assmts);
    }
    
    public static FuelGauge createFuelGauge(Field field, Map student, AssessmentResolver assmts) {
        return new FuelGauge(field, student, assmts);
    }
    
    public static Lozenge createLozenge(Field field, Map student, StudentResolver students, LozengeConfigResolver lozenges) {
        return new Lozenge(field, student, students, lozenges);
    }
    
    public static FieldCounter createFieldCounter(Field field, Map student, AttendanceResolver resolver, int[] boundaries) {
        return new FieldCounter(field, student, resolver.getAttendanceCountResolverForStudent(student), boundaries);
    }
    
    public static ColorByPercent createFieldRate(Field field, Map student, AttendanceResolver resolver) {
        return new FieldRate(field, student, resolver.getTardinessResolverForStudent(student));
    }

}
