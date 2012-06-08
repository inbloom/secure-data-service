package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.traversal.graph.NodeFilter;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

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
    public List<Entity> filterEntities(List<Entity> toResolve,String referenceField) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        //get the filter date
        String endDate = helper.getFilterDate(gracePeriod, calendar);

        if (!toResolve.isEmpty() && !endDate.isEmpty()) {
            //get the section entities
            Iterable<Entity> sections = helper.getReferenceEntities(EntityNames.SECTION, ID, getReferencedIds(toResolve,referenceField));
            //get the session Ids
            Set<String> sessionIds = getIds(sections, ParameterConstants.SESSION_ID);

            NeutralQuery query = new NeutralQuery();
            query.addCriteria(new NeutralCriteria(ID, NeutralCriteria.CRITERIA_IN,
                    new ArrayList<String>(sessionIds)));
            query.addCriteria(new NeutralCriteria(END_DATE, GREATER_THAN_EQUAL, endDate));

            //get the session entities
            Iterable<Entity> sessions = repo.findAll(EntityNames.SESSION, query);

            //filter out the relevant ids
            return new ArrayList<Entity>(getEntityIds(toResolve, ParameterConstants.SESSION_ID,
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
    protected Set<Entity> getEntityIds(Iterable<Entity> entities, String idKey, Set<String> keys) {
        Set<Entity> filteredEntities = new HashSet<Entity>();

        if (entities == null) return filteredEntities;

        for (Entity entity : entities) {
            String keyId = entity.getEntityId();
            if(idKey != null && !idKey.isEmpty()){
                keyId = (String) entity.getBody().get(idKey);
            }
            if (keys.contains(keyId)) {
                filteredEntities.add(entity);
            }
        }

        return filteredEntities;
    }

    private List<String> getReferencedIds(List<Entity> toResolve,String referenceField){
        List<String> foundIds = new ArrayList<String>();

        if(referenceField != null && !referenceField.isEmpty()){
            for (Entity e : toResolve) {
                Map<String, Object> body = e.getBody();
                foundIds.add((String) body.get(referenceField));
            }
        }
        else{
            for (Entity e : toResolve) {
                foundIds.add(e.getEntityId());
            }
        }
        return foundIds;
    }

}
