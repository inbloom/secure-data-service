package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.traversal.graph.NodeFilter;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    }


    @Override
    public List<String> filterIds(List<String> toResolve) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        List<String> returnIds = new ArrayList<String>();

        if (entityName != null && referenceId != null && gracePeriod != null && endDateParamName != null) {
            //get the filter date
            String curDateWithGracePeriod = helper.getFilterDate(gracePeriod, calendar);
            String curDate = helper.getFilterDate(null, calendar);

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
                        if (isResolvable(endDateStr, curDateWithGracePeriod)) {
                            returnIds.add(refId);
                        }
                    } else {
                        String startDateStr = (String) entity.getBody().get(startDateParamName);
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
     * Compares two given dates
     *
     * @param filterDateString
     * @param formattedEndDateString
     * @return
     */
    protected boolean isResolvable(String filterDateString, String formattedEndDateString) {

        Date filterDate = null, endDate = null;
        boolean retValue = true;

        try {
            if (filterDateString != null && !filterDateString.equals("")) {
                filterDate = formatter.parse(filterDateString);
                endDate = formatter.parse(formattedEndDateString);

                if (filterDate.before(endDate)) {
                    retValue = false;
                }
            }

        } catch (ParseException e) {
            retValue = false;
        }
        return retValue;
    }

}
