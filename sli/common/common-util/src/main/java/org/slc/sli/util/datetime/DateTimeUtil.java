package org.slc.sli.util.datetime;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Convenience utilities for common Joda time methods.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */
public class DateTimeUtil {
    
    /**
     * Returns the current UTC time in ISO 8601 format.
     * 
     * @return
     */
    public static String getNowInUTC() {
        
        return new DateTime(DateTimeZone.UTC).toString();
        
    }
}
