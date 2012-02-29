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
        if (i == 0) { i = 1; }
        this.total = i;
    }

    public void setActual(int i) {
        if (i < 0) { i = 0; }
        this.actual = i;
    }

    public String getText() {
        return "" + calculatePercentage();
    }

    private int calculatePercentage() {
        //Sanity checks on our data.
        if (total < 0 || total == 0) {
            total = 0;
        }
        if (actual < 0) {
            actual = 0;
        }

        if (isInverted) {
            return 100 - (int) (((float) actual / (float) total) * 100);
        }
        return (int) (((float) actual / (float) total) * 100);
    }

    public String getColor() {
        int percentage = calculatePercentage();
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
