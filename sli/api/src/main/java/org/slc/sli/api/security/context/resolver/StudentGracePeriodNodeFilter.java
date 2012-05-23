package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.traversal.graph.NodeFilter;
import org.slc.sli.client.constants.EntityNames;
import org.slc.sli.client.constants.v1.ParameterConstants;
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
 * Filters the students using student-school-associations
 * by a given date (grace period)
 *
 * @author srupasinghe
 *
 */
@Component
public class StudentGracePeriodNodeFilter extends NodeFilter {
    private static final String EXIT_WITHDRAW_DATE = "exitWithdrawDate";

    @Value("${sli.security.gracePeriod}")
    private String gracePeriod;

    @Autowired
    private AssociativeContextHelper helper;

    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


    @Override
    public List<String> filterIds(List<String> toResolve) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        //get the filter date
        String filterDate = helper.getFilterDate(gracePeriod, calendar);
        List<String> returnIds = new ArrayList<String>();

        if (!toResolve.isEmpty()) {
            //get the studentschoolassociation entities
            Iterable<Entity> studentSchoolAssociations = helper.getReferenceEntities(EntityNames.STUDENT_SCHOOL_ASSOCIATION,
                    ParameterConstants.STUDENT_ID, toResolve);

            for (Entity entity : studentSchoolAssociations) {
                String exitWithdrawDate = (String) entity.getBody().get(EXIT_WITHDRAW_DATE);

                if (isResolvable(exitWithdrawDate, filterDate)) {
                    returnIds.add((String) entity.getBody().get(ParameterConstants.STUDENT_ID));
                }
            }
        }

        return returnIds;
    }

    /**
     * Compares two given dates
     * @param exitWithdrawDate
     * @param filterDate
     * @return
     */
    protected boolean isResolvable(String exitWithdrawDate, String filterDate) {
        Date exitDate = null, endDate = null;
        boolean retValue = true;

        try {
            if (exitWithdrawDate != null && !exitWithdrawDate.equals("")) {
                exitDate = formatter.parse(exitWithdrawDate);
                endDate = formatter.parse(filterDate);

                if (exitDate.before(endDate)) {
                    retValue = false;
                }
            }

        } catch (ParseException e) {
            retValue = false;
        }

        return retValue;
    }

}
