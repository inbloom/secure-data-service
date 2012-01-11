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
    
    private Field field;
    private Student student;
    private AssessmentResolver assmts;
    
    private static final int MAX_LEVELS = 5;
    
    // mapping of performance level to color index
    private static int[][] perfToColor = {{0, 0, 0, 0, 0}, // 1 level
                                          {1, 5, 0, 0, 0}, // 2 levels
                                          {1, 2, 5, 0, 0}, // 3 levels
                                          {1, 2, 4, 5, 0}, // 4 levels
                                          {1, 2, 3, 4, 5}};  // 5 levels
                                         
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
        
        // create temporary Field to get perfLevel - TODO: there should be a better way to do this
        Field perfField = new Field();
        perfField.setType(field.getType());
        perfField.setValue(perfDataPointId);
        perfField.setTimeSlot(field.getTimeSlot());
        
        // get number of levels and assmt result level
        Integer numLevels = assmts.getMetaData().findNumRealPerfLevelsForFamily(assmtName);
        if (numLevels == null) { numLevels = 0; }  
        int level = 0;
        try {
            level = Integer.parseInt(assmts.get(perfField, student));
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
        
        // range check
        if (level <= 0 || level > numLevels || numLevels > MAX_LEVELS) {
            return 0;
        }
        
        return perfToColor[numLevels - 1][level - 1];
    }
    
    /*
     * Returns the text to display
     */
    public String getText() {
        
        return assmts.get(field, student);
    }
    
}
