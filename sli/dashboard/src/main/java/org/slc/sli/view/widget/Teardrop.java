package org.slc.sli.view.widget;

import java.util.HashMap;
import java.util.Map;

/**
 * Teardrop is a widget that displays a value along with associated color-coding and trending
 * information.
 * The colors supported include [darkgreen, lightgreen, yellow, orange, red, grey].
 * The trends supported include [notrend, flattrend, uptrend, downtrend].
 * 
 * @author rbloh
 */
public class Teardrop extends HashMap<String, String> {
    
    /**
     * Text value to be displayed with visual teardrop clues.
     */
    private static final String VALUE_KEY = "value";
    
    /**
     * Text previous value used to determine teardrop trending.
     */
    private static final String PREVIOUS_VALUE_KEY = "previousValue";
    
    /**
     * CSS style class name for this teardrop rendering.
     */
    private static final String STYLE_NAME_KEY = "styleName";
    
    /**
     * CSS style prefix for teardrop.
     */
    private static final String CSS_STYLE_PREFIX = "teardrop";
    
    /**
     * Colors.
     */
    private static final String DEFAULT_COLOR = "grey";
    
    /**
     * Trends.
     */
    private static final String DEFAULT_TREND = "notrend";
    
    /**
     * Grade color code mappings per US1146.
     */
    private static final Map<String, String> GRADE_COLOR_CODES = new HashMap<String, String>();
    static {
        GRADE_COLOR_CODES.put("A+", "darkgreen");
        GRADE_COLOR_CODES.put("A", "darkgreen");
        GRADE_COLOR_CODES.put("A-", "darkgreen");
        GRADE_COLOR_CODES.put("B+", "lightgreen");
        GRADE_COLOR_CODES.put("B", "lightgreen");
        GRADE_COLOR_CODES.put("B-", "lightgreen");
        GRADE_COLOR_CODES.put("C+", "yellow");
        GRADE_COLOR_CODES.put("C", "yellow");
        GRADE_COLOR_CODES.put("C-", "yellow");
        GRADE_COLOR_CODES.put("D+", "orange");
        GRADE_COLOR_CODES.put("D", "orange");
        GRADE_COLOR_CODES.put("D-", "orange");
        GRADE_COLOR_CODES.put("F+", "red");
        GRADE_COLOR_CODES.put("F", "red");
        GRADE_COLOR_CODES.put("F-", "red");
        
        GRADE_COLOR_CODES.put("1", "darkgreen");
        GRADE_COLOR_CODES.put("2", "lightgreen");
        GRADE_COLOR_CODES.put("3", "yellow");
        GRADE_COLOR_CODES.put("4", "orange");
        GRADE_COLOR_CODES.put("5", "red");
    }
    
    /**
     * Grade trend mappings.
     */
    private static final Map<String, Integer> GRADE_TREND_CODES = new HashMap<String, Integer>();
    static {
        GRADE_TREND_CODES.put("A+", 15);
        GRADE_TREND_CODES.put("A", 14);
        GRADE_TREND_CODES.put("A-", 13);
        GRADE_TREND_CODES.put("B+", 12);
        GRADE_TREND_CODES.put("B", 11);
        GRADE_TREND_CODES.put("B-", 10);
        GRADE_TREND_CODES.put("C+", 9);
        GRADE_TREND_CODES.put("C", 8);
        GRADE_TREND_CODES.put("C-", 7);
        GRADE_TREND_CODES.put("D+", 6);
        GRADE_TREND_CODES.put("D", 5);
        GRADE_TREND_CODES.put("D-", 4);
        GRADE_TREND_CODES.put("F+", 3);
        GRADE_TREND_CODES.put("F", 2);
        GRADE_TREND_CODES.put("F-", 1);
        
        GRADE_TREND_CODES.put("1", 5);
        GRADE_TREND_CODES.put("2", 4);
        GRADE_TREND_CODES.put("3", 3);
        GRADE_TREND_CODES.put("4", 2);
        GRADE_TREND_CODES.put("5", 1);
    }
    
    /**
     * CSS convention separator.
     */
    private static final String CSS_SEPARATOR = "-";
    
    /**
     * Constructors for Teardrop.
     * 
     */
    public Teardrop() {
        this(null, null);
    }
    
    public Teardrop(String value, String previousValue) {
        this.setValue(value);
        this.setPreviousValue(previousValue);
        
        this.init();
    }
    
    /**
     * Initialize the Teardrop to determine the appropriate color-coding and trending information to
     * convey via the determined CSS styling.
     * 
     */
    public void init() {
        
        String currentValue = this.getValue();
        String previousValue = this.getPreviousValue();
        
        // Lookup color-coding
        String colorCoding = DEFAULT_COLOR;
        if (currentValue != null) {
            colorCoding = GRADE_COLOR_CODES.get(currentValue);
        }
        
        // Determine trend
        String trend = DEFAULT_TREND;
        if ((currentValue != null) && (previousValue != null)) {
            Integer currentTrendCode = GRADE_TREND_CODES.get(currentValue);
            Integer previousTrendCode = GRADE_TREND_CODES.get(previousValue);
            if ((currentTrendCode != null) && (previousTrendCode != null)) {
                Integer trendCode = currentTrendCode - previousTrendCode;
                if (trendCode > 0) {
                    trend = "uptrend";
                } else if (trendCode < 0) {
                    trend = "downtrend";
                } else {
                    trend = "flattrend";
                }
            }
        }
        
        // Derive CSS style class name using convention
        String styleName = CSS_STYLE_PREFIX + CSS_SEPARATOR + colorCoding + CSS_SEPARATOR + trend;
        this.setStyleName(styleName);
    }
    
    /**
     * Return the teardrop current value.
     * 
     * @return The current value
     */
    public String getValue() {
        return this.get(VALUE_KEY);
    }
    
    /**
     * Set the teardrop current value.
     * 
     * @param value
     *            The current value to set.
     */
    public void setValue(String value) {
        if (value != null)
            this.put(VALUE_KEY, value);
    }
    
    /**
     * Return the teardrop previous value using to determine trending.
     * 
     * @return The previous value
     */
    public String getPreviousValue() {
        return this.get(PREVIOUS_VALUE_KEY);
    }
    
    /**
     * Set the teardrop previous value used to determine trending.
     * 
     * @param previousValue
     *            The current value to set.
     */
    public void setPreviousValue(String previousValue) {
        if (previousValue != null)
            this.put(PREVIOUS_VALUE_KEY, previousValue);
    }
    
    /**
     * Return the teardrop CSS style class name.
     * 
     * @return The CSS style class name.
     */
    public String getStyleName() {
        return this.get(STYLE_NAME_KEY);
    }
    
    /**
     * Set the teardrop CSS style class name.
     * 
     * @param styleName
     *            The CSS style class name to set.
     */
    public void setStyleName(String styleName) {
        if (styleName != null)
            this.put(STYLE_NAME_KEY, styleName);
    }
    
}
