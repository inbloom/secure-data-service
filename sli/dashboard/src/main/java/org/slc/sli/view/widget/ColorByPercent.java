package org.slc.sli.view.widget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple class to yield a percent with a color
 */
public class ColorByPercent {

    private static Logger logger = LoggerFactory.getLogger(ColorByPerf.class);
    private boolean isInverted = false;
    private int total = 0;
    private int actual = 0;
    
    //text used for invalid numbers, such as when total == 0
    public static final String INVALID_NUMBER_DISPLAY_TEXT = "-";
    
    // 0-30 is critical, 30-70 is low, 70-90 is average, and 90-100 is high
    private static final int[] DEFAULT_BOUNDARIES = new int[] {30, 70, 90};
    private int[] boundaries = DEFAULT_BOUNDARIES;
    
    // mapping of performance level to color index
    private static int[][] perfToColor = {{0, 0, 0, 0, 0}, // 1 level
                                          {1, 5, 0, 0, 0}, // 2 levels
                                          {1, 2, 5, 0, 0}, // 3 levels
                                          {1, 2, 4, 5, 0}, // 4 levels
                                          {1, 2, 3, 4, 5}};  // 5 levels
    
    public ColorByPercent() {
    }

    public ColorByPercent(int total, int actual) {
        this.total = total;
        this.actual = actual;
    }

    public ColorByPercent(boolean inverted, int total, int actual) {
        isInverted = inverted;
        this.total = total;
        this.actual = actual;
    }

    public void setTotal(int i) {
        this.total = i;
    }

    public void setActual(int i) {
        if (i < 0) { i = 0; }
        this.actual = i;
    }

    public String getText() {
        Integer percent = calculatePercentage();
        if (percent == null) //divison by 0
            return INVALID_NUMBER_DISPLAY_TEXT;
        return Integer.toString(calculatePercentage());
    }
    
    private float calculateRatio() {
        //Sanity checks on our data.
        if (total < 0 || total == 0) {
            total = 0;
        }
        if (actual < 0) {
            actual = 0;
        }

        if (isInverted) {
            return 1f -  ((float) actual / (float) total);
        }
                
        return (float) actual / (float) total;
    }

    /**
     * 
     * @return either the value rounded to the nearest percent, or null if it's
     * not a valid percentage.
     */
    private Integer calculatePercentage() {
        float ratio = calculateRatio();
        if (Float.isInfinite(ratio) || Float.isNaN(ratio))
            return null;
        
        return Math.round(ratio * 100f);
    }

    public int getColorIndex() {
        Integer percentage = calculatePercentage();
        if (percentage == null || boundaries.length == 0) {
            return 0;
        }
        
        //Inversion is the case where we want to flip the colors
        //For example, for grades a 0% is bad, but for tardiness a 0% is good
        //so for the tardy case we'd invert the colors
        boolean invert = false;
        if (boundaries[0] > boundaries[boundaries.length - 1]) {
            invert = true;
            percentage = 100 - percentage;
        }
        
        int[] colorLevels = perfToColor[boundaries.length];
        int[] cutoffs = new int[boundaries.length + 2];
        
        cutoffs[0] = 0;
        cutoffs[cutoffs.length - 1] = 100;
        
        for (int i = 1; i < boundaries.length + 1; i++) {
            cutoffs[i] = boundaries[i - 1];
            
            if (invert) {
                cutoffs[i] = 100 - cutoffs[i];
            }
        }
        
        for (int i = 0; i < cutoffs.length - 1; i++) {
            
            //we place the >= on the lower number, so if a percentage is on a cutoff point
            //include it in the lower of the two levels.  We could change that if desired
            if (percentage >= cutoffs[i] && percentage < cutoffs[i + 1]) {
                return colorLevels[i];
            }
        }
        
        return 0;
    }

    public void setIsInverted(boolean inverted) {
        this.isInverted = inverted;
    }
    
    public void setBoundaries(int[] bounds) {
        this.boundaries = bounds;
    }
    

}
