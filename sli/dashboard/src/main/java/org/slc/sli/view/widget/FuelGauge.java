package org.slc.sli.view.widget;

import org.slc.sli.config.Field;
import org.slc.sli.entity.Assessment;
import org.slc.sli.entity.Student;
import org.slc.sli.view.AssessmentResolver;
import java.util.List;

/**
 * Logic used by a widget that displays assessment results color-coded by performance level and 
 * shows rectangles sized corresponding to scale score. 
 * 
 * @author syau
 */
public class FuelGauge {
    
    private Field field;
    private Student student;
    private AssessmentResolver assmts;
    private ColorByPerf colorByPerf;

    public FuelGauge(Field field, Student student, AssessmentResolver assmts) {
        this.field = field;
        this.student = student;
        this.assmts = assmts;
        this.colorByPerf = WidgetFactory.createColorByPerf(field, student, assmts);
    }
    
    /**
     * Get the color index for display
     */
    public int getColorIndex() { return colorByPerf.getColorIndex(); }
    /**
     * Returns the text to display
     */
    public String getText() { return colorByPerf.getText(); }
    
    /**
     * Returns the cut points 
     */
    public List<Integer> getCutpoints() { return assmts.getCutpoints(field, student); }

    /**
     * Returns the number of possible performance levels that are "real" 
     */
    public Integer getNumRealPerfLevels() { 
        Assessment assmt = assmts.resolveAssessment(field, student);
        if (assmt == null) { return null; }
        return assmts.getMetaData().findNumRealPerfLevelsForFamily(assmt.getAssessmentName());
    }
    /**
     * Returns the performance level 
     */
    public Integer getPerfLevel() { 
        Assessment assmt = assmts.resolveAssessment(field, student);
        if (assmt == null) { return null; }
        return assmt.getPerfLevel();
    }

    /**
     * Returns the score 
     */
    public Integer getScore() { 
        Assessment a = assmts.resolveAssessment(field, student);
        return a == null ? null : a.getScaleScore();
    }
}
