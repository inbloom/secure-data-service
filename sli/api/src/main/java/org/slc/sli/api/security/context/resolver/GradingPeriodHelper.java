package org.slc.sli.api.security.context.resolver;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.api.service.query.UriInfoToApiQueryConverter;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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

    UriInfoToApiQueryConverter queryConverter = new UriInfoToApiQueryConverter();

    public List<String> getCalendarDatesForGradingPeriods(String queryParameters, String ...gradingPeriodIds) {
        List<String> allGPcalendarDates = new LinkedList<String>();
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(gradingPeriodIds)));
        Iterable<Entity> gradingPeriods = repo.findAll("gradingPeriod", query);
        for(Entity gradingPeriod: gradingPeriods) {
            List<String> gpCalendarDates = (List<String>)gradingPeriod.getBody().get("calendarDateReference");
            if (gpCalendarDates != null) {
                allGPcalendarDates.addAll(gpCalendarDates);
            }
        }
        ApiQuery apiQuery = new ApiQuery();
        queryConverter.convert(apiQuery, queryParameters);
        apiQuery.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, allGPcalendarDates));
        Iterable<String> matchingCalendarDates = repo.findAllIds("calendarDate", apiQuery);
        List<String> selectedCDs = Lists.newArrayList(matchingCalendarDates);
        return selectedCDs;
    }
}
