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

    private static String[] colors = new String[]{"critical", "low", "average", "high"};
    
    // 0-30 is critical, 30-70 is low, 70-90 is average, and 90-100 is high
    private static final int[] DEFAULT_BOUNDARIES = new int[] {30, 70, 90};
    private int[] boundaries = DEFAULT_BOUNDARIES;
    
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

    public String getColor() {
        Integer percentage = calculatePercentage();
        if (percentage == null) {
            return "none";
        }
        
        //Inverted is for the case where higher values are bad
        //for example, when calculating a tardiness, 100% tardy rate is very bad, 
        //but for grades, 100% is good
        boolean inverted = boundaries[0] > boundaries[2];
        
        if (!inverted) {
            if (percentage > boundaries[2]) {
                return colors[3];   //high
            } else if (percentage < boundaries[2] && percentage > boundaries[1]) {
                return colors[2];   //average
            } else if (percentage < boundaries[1] && percentage > boundaries[0]) {
                return colors[1];   //low
            } else {
                return colors[0];   //critical
            }
        }
        
        //inverted case
        if (percentage > boundaries[0]) {
            return colors[0];   //critical
        } else if (percentage < boundaries[0] && percentage > boundaries[1]) {
            return colors[1];   //low
        } else if (percentage < boundaries[1] && percentage > boundaries[2]) {
            return colors[2];   //average
        } else { // percentage < boundaries[2]
            return colors[3];   //high
        }

    }

    public void setIsInverted(boolean inverted) {
        this.isInverted = inverted;
    }
    
    public void setBoundaries(int[] bounds) {
        this.boundaries = bounds;
    }
    

}
