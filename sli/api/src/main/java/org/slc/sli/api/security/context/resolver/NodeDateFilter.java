/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.api.security.context.resolver;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.traversal.graph.NodeFilter;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Filters the entity by a given date
 * 
 * @author jcole
 */
@Component
public class NodeDateFilter extends NodeFilter {
    
    @Autowired
    private AssociativeContextHelper helper;
    
    protected String getGracePeriod() {
        return gracePeriod;
    }
    
    protected String getEndDateParamName() {
        return endDateParamName;
    }
    
    public String getStartDateParamName() {
        return startDateParamName;
    }
    
    public void setStartDateParamName(String startDateParamName) {
        this.startDateParamName = startDateParamName;
    }
    
    private String gracePeriod;
    private String endDateParamName;
    private String startDateParamName;
    
    public void setParameters(String gracePeriod, String endDateParamName) {
        this.gracePeriod = gracePeriod;
        this.endDateParamName = endDateParamName;
        this.startDateParamName = "";
    }
    
    @Override
    public List<Entity> filterEntities(List<Entity> toResolve, String unusedReferenceField) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        List<Entity> returnEntityList = new ArrayList<Entity>();
        
        if (gracePeriod != null && endDateParamName != null) {
            // get the filter date
            String curDateWithGracePeriod = helper.getFilterDate(gracePeriod, calendar);
            
            if (!toResolve.isEmpty()) {
                for (Entity entity : toResolve) {
                    String endDateStr = (String) entity.getBody().get(endDateParamName);
                    
                    if (startDateParamName.isEmpty()) {
                        if (endDateStr == null || endDateStr.isEmpty()
                                || isFirstDateBeforeSecondDate(curDateWithGracePeriod, endDateStr)) {
                            returnEntityList.add(entity);
                        }
                    } else {
                        String startDateStr = (String) entity.getBody().get(startDateParamName);
                        String curDate = getCurrentDate();
                        if (isDateInRange(curDate, startDateStr, endDateStr)) {
                            returnEntityList.add(entity);
                        }
                    }
                    
                }
            }
        }
        
        return returnEntityList;
    }
    
    /**
     * Parses the dateTime string using the format 'yyyy-MM-dd'.
     * 
     * @param dateTime
     *            String to be parsed.
     * @return DateTime representation of the String.
     */
    protected DateTime getDateTime(String dateTime) throws IllegalArgumentException {
        return DateTime.parse(dateTime, new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").toFormatter());
    }
    
    /**
     * Converts the input strings to DateTime representations, in order to determine if the date in
     * question falls between the start date and end date (inclusive).
     * 
     * @param date
     *            String representation of the date to compare with start and end dates.
     * @param startDate
     *            String representation of the first value on interval (inclusive).
     * @param endDate
     *            String representation of the last value on interval (inclusive).
     * @return true if the DateTime representation of 'date' is greater than or equal to the
     *         DateTime
     *         representation of start date but less than or equal to the DateTime representation of
     *         end date,
     *         false otherwise.
     */
    protected boolean isDateInRange(String date, String startDate, String endDate) throws IllegalArgumentException {
        return isDateInRange(getDateTime(date), getDateTime(startDate), getDateTime(endDate));
    }
    
    /**
     * Determines if the date in question falls between the start date and end date.
     * 
     * @param date
     *            Date to compare with start and end dates.
     * @param startDate
     *            First value on interval (inclusive).
     * @param endDate
     *            Last value on interval (inclusive).
     * @return true if 'date' is greater than or equal to start date but less than or equal to end
     *         date, false otherwise.
     */
    protected boolean isDateInRange(DateTime date, DateTime startDate, DateTime endDate)
            throws IllegalArgumentException {
        return (date.getMillis() == startDate.getMillis()) || (date.getMillis() == endDate.getMillis())
                || date.isAfter(startDate) && date.isBefore(endDate);
    }
    
    /**
     * Compares two given dates.
     * 
     * @param formattedFirstDateString
     *            first date
     * @param formattedSecondDateString
     *            second date
     * @return true when first date is before the second date, false otherwise.
     */
    protected boolean isFirstDateBeforeSecondDate(String formattedFirstDateString, String formattedSecondDateString)
            throws IllegalArgumentException {
        DateTime date = getDateTime(formattedFirstDateString);
        DateTime endDate = getDateTime(formattedSecondDateString);
        return date.isBefore(endDate);
    }
    
    protected String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return helper.getFilterDate(null, calendar);
    }
}
