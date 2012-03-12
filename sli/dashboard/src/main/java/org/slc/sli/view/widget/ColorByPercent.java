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
        if (percentage > 90) {
            return "high";
        }
        if (percentage < 90 && percentage > 70) {
            return "average";
        }
        if (percentage < 70 && percentage > 30) {
            return "low";
        }
        return "critical";
    }

    public void setIsInverted(boolean inverted) {
        this.isInverted = inverted;
    }
}
