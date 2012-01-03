package org.slc.sli.view.widget;

import org.slc.sli.config.Field;
import org.slc.sli.entity.Student;
import org.slc.sli.view.AssessmentResolver;

/**
 * Logic used by a widget that displays assessment results color-coded by performance level
 * 
 * @author dwu
 */
public class ColorByPerf {
    
    Field field;
    Student student;
    AssessmentResolver assmts;
    
    public ColorByPerf(Field field, Student student, AssessmentResolver assmts) {
        this.field = field;
        this.student = student;
        this.assmts = assmts;
    }
    
    /*
     * Get the color index for display
     */
    public int getColorIndex() {
        
        // get data point id for the perf level
        String dataPointId = field.getValue();
        String assmtName = dataPointId.substring(0, dataPointId.indexOf('.'));
        String perfDataPointId = assmtName + ".perfLevel";
        
        // get number of levels and assmt result level
        int numLevels = 4; // TODO: base on assmt meta data
        int level = 0;
        try {
            level = Integer.parseInt(assmts.get(perfDataPointId, student));
        } catch (Exception e) {
            System.out.println("Could not find performance level");
            return 0;
        }
        
        return getColorIndex(level, numLevels);
    }
    
    /*
     * Get the color index, given the performance level and total number of levels
     */
    public int getColorIndex(int level, int numLevels) {
        
        int colorIndex = 0;
        
        // range check
        if (level <= 0 || level > numLevels) {
            return 0;
        }
        
        if (numLevels == 5) {
            colorIndex = level;
        }
        
        if (numLevels == 4) {
            if (level <= 2) {
                colorIndex = level;
            } else {
                colorIndex = level + 1;
            }
        }
        
        if (numLevels == 3) {
            if (level <= 2) {
                colorIndex = level;
            } else {
                colorIndex = 5;
            }
        }
        
        if (numLevels == 2) {
            if (level == 1) {
                colorIndex = 1;
            } else {
                colorIndex = 5;
            }
        }
        
        return colorIndex;

    }
    
    /*
     * Returns the text to display
     */
    public String getText() {
        
        String dataPointId = field.getValue();
        return assmts.get(dataPointId, student);
    }
    
}
