package org.slc.sli.api.security.context.resolver;

import com.google.common.collect.Iterables;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ldalgado
 * Date: 7/9/13
 * Time: 5:43 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class GradingPeriodHelper {
    @Autowired
    protected PagingRepositoryDelegate<Entity> repo;

    public Set<String> getCalendarDatesForGradingPeriods(String ...gradingPeriodIds) {
        Set<String> calendarDates = new HashSet<String>();
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(gradingPeriodIds)));
        Iterable<Entity> gradingPeriods = repo.findAll("gradingPeriod", query);
        for(Entity gradingPeriod: gradingPeriods) {
            List<String> gpCalendarDates = (List<String>)gradingPeriod.getBody().get("calendarDateReference");
            if (gpCalendarDates != null) {
                calendarDates.addAll(gpCalendarDates);
            }
        }
        return calendarDates;
    }
}
