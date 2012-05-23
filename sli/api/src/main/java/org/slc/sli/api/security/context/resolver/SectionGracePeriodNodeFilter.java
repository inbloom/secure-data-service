package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.traversal.graph.NodeFilter;
import org.slc.sli.client.constants.EntityNames;
import org.slc.sli.client.constants.v1.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Calendar;

/**
 * Filters the sections by a given date (grace period)
 *
 * @author srupasinghe
 *
 */
@Component
public class SectionGracePeriodNodeFilter extends NodeFilter {
    private static final String END_DATE = "endDate";
    private static final String ID = "_id";
    private static final String GREATER_THAN_EQUAL = ">=";

    @Value("${sli.security.gracePeriod}")
    private String gracePeriod;

    @Autowired
    private Repository<Entity> repo;

    @Autowired
    private AssociativeContextHelper helper;

    @Override
    public List<String> filterIds(List<String> toResolve) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        //get the filter date
        String endDate = helper.getFilterDate(gracePeriod, calendar);

        if (!toResolve.isEmpty() && !endDate.isEmpty()) {
            //get the section entities
            Iterable<Entity> sections = helper.getReferenceEntities(EntityNames.SECTION, ID, toResolve);
            //get the session Ids
            Set<String> sessionIds = getIds(sections, ParameterConstants.SESSION_ID);

            NeutralQuery query = new NeutralQuery();
            query.addCriteria(new NeutralCriteria(ID, NeutralCriteria.CRITERIA_IN,
                    new ArrayList<String>(sessionIds)));
            query.addCriteria(new NeutralCriteria(END_DATE, GREATER_THAN_EQUAL, endDate));

            //get the session entities
            Iterable<Entity> sessions = repo.findAll(EntityNames.SESSION, query);

            //filter out the relevant ids
            return new ArrayList<String>(getEntityIds(sections, ParameterConstants.SESSION_ID,
                    getIds(sessions, ID)));
        }

        return toResolve;
    }

    /**
     * Returns a list of ids for the id key given
     * @param entities List of entities
     * @param idKey The key to match
     * @return
     */
    protected Set<String> getIds(Iterable<Entity> entities, String idKey) {
        Set<String> ids = new HashSet<String>();

        if (entities == null) return ids;

        for (Entity entity : entities) {
            if (idKey.equals(ID)) {
                ids.add(entity.getEntityId());
            } else {
                if (entity.getBody().containsKey(idKey)) {
                    ids.add((String) entity.getBody().get(idKey));
                }
            }
        }

        return ids;
    }

    /**
     * Returns a list of entity ids that matches the id key and the given
     * list of keys
     * @param entities List of entities
     * @param idKey The key to match
     * @param keys The list of key ids to match
     * @return
     */
    protected Set<String> getEntityIds(Iterable<Entity> entities, String idKey, Set<String> keys) {
        Set<String> ids = new HashSet<String>();

        if (entities == null) return ids;

        for (Entity entity : entities) {
            String keyId = (String) entity.getBody().get(idKey);

            if (keys.contains(keyId)) {
                ids.add(entity.getEntityId());
            }
        }

        return ids;
    }

}
