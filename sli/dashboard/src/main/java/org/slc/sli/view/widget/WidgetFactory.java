package org.slc.sli.view.widget;

import org.slc.sli.config.Field;
import org.slc.sli.entity.Student;
import org.slc.sli.view.AssessmentResolver;
import org.slc.sli.view.StudentResolver;
import org.slc.sli.view.LozengeConfigResolver;

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

    public static Lozenge createLozenge(Field field, Student student, StudentResolver students, LozengeConfigResolver lozenges) {
        return new Lozenge(field, student, students, lozenges);
    }

}
