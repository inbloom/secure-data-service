package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.traversal.graph.NodeFilter;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private String entityName;
    private String referenceId;
    private String endDateString;
    private String filterDateParam;

    public void setParameters (String entityName, String referenceId, String endDateString, String filterDateParam) {
        this.entityName = entityName;
        this.referenceId = referenceId;
        this.endDateString = endDateString;
        this.filterDateParam = filterDateParam;
    }

    @Override
    public List<String> filterIds(List<String> toResolve) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        List<String> returnIds = new ArrayList<String>();

        if (entityName != null && referenceId != null && endDateString != null && filterDateParam != null) {
            //get the filter date
            String formattedEndDateString = helper.getFilterDate(endDateString, calendar);

            if (!toResolve.isEmpty()) {
                //get the entities
                Iterable<Entity> referenceEntities = helper.getReferenceEntities(entityName,
                        referenceId, toResolve);

                for (Entity entity : referenceEntities) {
                    String filterDateString = (String) entity.getBody().get(filterDateParam);

                    if (isResolvable(filterDateString, formattedEndDateString)) {
                        returnIds.add((String) entity.getBody().get(referenceId));
                    }
                }
            }
        }

        return returnIds;
    }

    /**
     * Compares two given dates
     * @param filterDate
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
