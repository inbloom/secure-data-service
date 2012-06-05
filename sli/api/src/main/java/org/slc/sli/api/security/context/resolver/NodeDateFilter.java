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

    protected String entityName;

    public String getEntityName() {
        return entityName;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public String getGracePeriod() {
        return gracePeriod;
    }

    public String getFilterDateParam() {
        return filterDateParam;
    }

    protected String referenceId;
    protected String gracePeriod;
    protected String filterDateParam;

    public void setParameters (String entityName, String referenceId, String gracePeriod, String filterDateParam) {
        this.entityName = entityName;
        this.referenceId = referenceId;
        this.gracePeriod = gracePeriod;
        this.filterDateParam = filterDateParam;
    }

    @Override
    public List<String> filterIds(List<String> toResolve) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        List<String> returnIds = new ArrayList<String>();

        if (entityName != null && referenceId != null && gracePeriod != null && filterDateParam != null) {
            //get the filter date
            String formattedEndDateString = helper.getFilterDate(gracePeriod, calendar);

            if (!toResolve.isEmpty()) {
                //get the entities
                Iterable<Entity> referenceEntities = helper.getReferenceEntities(entityName,
                        referenceId, toResolve);

                for (Entity entity : referenceEntities) {
                    String filterDateString = (String) entity.getBody().get(filterDateParam);
                    String refId= (String) entity.getBody().get(referenceId);
                    if (!returnIds.contains(refId) &&
                            isResolvable(filterDateString, formattedEndDateString)) {
                        returnIds.add(refId);
                    }
                }
            }
        }

        return returnIds;
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
