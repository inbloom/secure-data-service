package org.slc.sli.api.security.context.validator;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractContextValidator implements IContextValidator {

    @Value("${sli.security.gracePeriod}")
    private String gracePeriod;
    
    private DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
    
    protected String getFilterDate() {
        DateTime now = DateTime.now();
        int numDays = Integer.parseInt(gracePeriod);
        now = now.minusDays(numDays);
        return now.toString(fmt);
    }
    
}
