package org.slc.sli.api.security.context.resolver;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.traversal.graph.NodeFilter;
import org.slc.sli.domain.Entity;

/**
 * Filters the entity by a given date
 *
 * @author jcole
 */
@Component
public class NodeDateFilter extends NodeFilter {

    @Autowired
    private AssociativeContextHelper helper;

    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    protected String getEntityName() {
        return entityName;
    }

    protected String getReferenceId() {
        return referenceId;
    }

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

    private String entityName;
    private String referenceId;
    private String gracePeriod;
    private String endDateParamName;
    private String startDateParamName;

    public void setParameters(String entityName, String referenceId, String gracePeriod, String endDateParamName) {
        this.entityName = entityName;
        this.referenceId = referenceId;
        this.gracePeriod = gracePeriod;
        this.endDateParamName = endDateParamName;
        this.startDateParamName = "";
    }


    @Override
    public List<String> filterIds(List<String> toResolve) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        List<String> returnIds = new ArrayList<String>();

        if (entityName != null && referenceId != null && gracePeriod != null && endDateParamName != null) {
            //get the filter date
            String curDateWithGracePeriod = helper.getFilterDate(gracePeriod, calendar);

            if (!toResolve.isEmpty()) {
                //get the entities
                Iterable<Entity> referenceEntities = helper.getReferenceEntities(entityName,
                        referenceId, toResolve);

                for (Entity entity : referenceEntities) {
                    String endDateStr = (String) entity.getBody().get(endDateParamName);

                    String refId = (String) entity.getBody().get(referenceId);
                    if (returnIds.contains(refId)) {
                        continue; // refId already added to returnIds
                    }

                    if (startDateParamName.isEmpty()) {
                        if (isFirstDateBeforeSecondDate(endDateStr, curDateWithGracePeriod)) {
                            returnIds.add(refId);
                        }
                    } else {
                        String startDateStr = (String) entity.getBody().get(startDateParamName);
                        String curDate = getCurrentDate();
                        if (isDateInRange(curDate, startDateStr, endDateStr)) {
                            returnIds.add(refId);
                        }
                    }

                }
            }
        }

        return returnIds;
    }

    protected boolean isDateInRange(String date, String startDate, String endDate) {
        try {
            return isDateInRange(formatter.parse(date), formatter.parse(startDate), formatter.parse(endDate));
        } catch (ParseException e) {
            return false;
        }
    }

    protected boolean isDateInRange(Date date, Date startDate, Date endDate) {
        return date.compareTo(startDate) >= 0 && date.compareTo(endDate) <= 0;
    }

    /**
     * Compares two given dates.
     * Returns true when first date is before or second date.
     * Determines 'is date is before end date'.
     *
     * @param formattedFirstDateString
     * @param formattedSecondDateString
     * @return
     */
    protected boolean isFirstDateBeforeSecondDate(String formattedFirstDateString, String formattedSecondDateString) {

        Date date = null, endDate = null;
        boolean retValue = true;

        try {
            if (formattedFirstDateString != null && !formattedFirstDateString.equals("")) {
                date = formatter.parse(formattedFirstDateString);
                endDate = formatter.parse(formattedSecondDateString);

                if (date.before(endDate)) {
                    retValue = false;
                }
            }

        } catch (ParseException e) {
            retValue = false;
        }
        return retValue;
    }

    protected String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return helper.getFilterDate(null, calendar);
    }

}
