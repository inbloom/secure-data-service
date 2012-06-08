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
 *
 */
@Component
public class NodeDateFilter extends NodeFilter {

    @Autowired
    private AssociativeContextHelper helper;

    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    protected String getGracePeriod() {
        return gracePeriod;
    }

    protected String getFilterDateParam() {
        return filterDateParam;
    }

    private String gracePeriod;
    private String filterDateParam;

    public void setParameters(String gracePeriod, String filterDateParam) {
        this.gracePeriod = gracePeriod;
        this.filterDateParam = filterDateParam;
    }

    @Override
    public List<Entity> filterEntities(List<Entity> toResolve,String referenceField) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        List<Entity> returnEntityList = new ArrayList<Entity>();

        if (gracePeriod != null && filterDateParam != null) {
            //get the filter date
            String formattedEndDateString = helper.getFilterDate(gracePeriod, calendar);

            if (!toResolve.isEmpty()) {
                for (Entity entity : toResolve) {
                    String filterDateString = (String) entity.getBody().get(filterDateParam);
                    if (isResolvable(filterDateString, formattedEndDateString)) {
                        returnEntityList.add(entity);
                    }
                }
            }
        }

        return returnEntityList;
    }

    /**
     * Compares two given dates
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
