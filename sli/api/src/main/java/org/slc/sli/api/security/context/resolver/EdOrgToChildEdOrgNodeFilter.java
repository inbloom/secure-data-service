package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.CallingApplicationInfoProvider;
import org.slc.sli.api.security.context.traversal.graph.NodeFilter;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 *
 *
 */
@Component
public class EdOrgToChildEdOrgNodeFilter extends NodeFilter {

    private static final String REFERENCE = "parentEducationAgencyReference";

    @Autowired
    private CallingApplicationInfoProvider clientInfo;

    @Autowired
    private Repository<Entity> repo;

    @Override
    public List<String> filterIds(List<String> ids) {
        Set<String> parents = fetchParents(new HashSet<String>(ids));
        Set<String> blacklist = getBlacklist();
        Set<String> toReturn = new HashSet<String>(ids);
        Queue<String> toResolve = new LinkedList<String>(ids);

        while (!toResolve.isEmpty()) {
            NeutralQuery query = new NeutralQuery();
            query.addCriteria(new NeutralCriteria(REFERENCE, NeutralCriteria.CRITERIA_IN, toResolve));
            Iterable<Entity> ents = repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query);
            toResolve.clear();
            for (Iterator<Entity> i = ents.iterator(); i.hasNext();) {
                String childEdOrg = i.next().getEntityId();
                if (!blacklist.contains(childEdOrg)) {
                    toReturn.add(childEdOrg);
                    toResolve.add(childEdOrg);
                }
            }

        }
        toReturn.addAll(parents);
        return new ArrayList<String>(toReturn);
    }

    /**
     * Finds all the child ed orgs immediately under a SEA.
     * @param parentEdOrgStateId - the stateOrganizationId of the SEA
     * @return 
     */
    public List<String> getChildEducationOrganizations(String parentEdOrgStateId) {
        NeutralQuery stateQuery = new NeutralQuery();
        stateQuery.addCriteria(new NeutralCriteria("stateOrganizationId", "=", parentEdOrgStateId));
        
        Entity stateEdOrg = repo.findOne(EntityNames.EDUCATION_ORGANIZATION, stateQuery);
        
        NeutralQuery childrenQuery = new NeutralQuery();
        childrenQuery.addCriteria(new NeutralCriteria("parentEducationAgencyReference", "=", stateEdOrg.getEntityId()));
        Iterable<Entity> myEdOrgs = repo.findAll(EntityNames.EDUCATION_ORGANIZATION, childrenQuery);

        List<String> myEdOrgsIds = new ArrayList<String>();
        for (Entity cur : myEdOrgs) {
            String stateOrgId = (String) cur.getBody().get("stateOrganizationId");
            myEdOrgsIds.add(stateOrgId);
        }
        return myEdOrgsIds;
    }


    private Set<String> fetchParents(Set<String> ids) {
        Set<String> returned = new HashSet<String>(ids);
        String toResolve = "";
        for (String id : ids) {
            if (repo.exists(EntityNames.EDUCATION_ORGANIZATION, id)) {
                debug("We found a valid ED-ORG id {}", id);
                toResolve = id;
            }
        }
        while (toResolve.length() > 0) {
            Entity edOrg = repo.findById(EntityNames.EDUCATION_ORGANIZATION, toResolve);
            Map<String, Object> body = edOrg.getBody();
            if (body.containsKey(REFERENCE)) {
                toResolve = (String) body.get(REFERENCE);
            } else {
                toResolve = "";
            }
            debug("Adding a parent Ed-Org {}", (String) edOrg.getEntityId());
            returned.add(edOrg.getEntityId());
        }

        return returned;
    }

    private Set<String> getBlacklist() {
        Set<String> blacklist = new HashSet<String>();
        String clientId = clientInfo.getClientId();

        if (null == clientId) {
            return blacklist; //Unit tests dont have a client ID
        }

        NeutralQuery nq = new NeutralQuery(new NeutralCriteria("client_id", NeutralCriteria.OPERATOR_EQUAL, clientId));
        Entity appEntity = repo.findOne("application", nq);

        if (null == appEntity) {
            return blacklist; //No application found with this client ID
        }

        String appId = appEntity.getEntityId();

        Iterable<Entity> entities =  repo.findAll("applicationAuthorization");

        for (Iterator<Entity> i = entities.iterator(); i.hasNext();) {
            Entity appAuth = i.next();
            List<String> appIdArray = (List<String>) appAuth.getBody().get("appIds");
            if (!appIdArray.contains(appId)) {
                Entity edorgEntity = repo.findOne(EntityNames.EDUCATION_ORGANIZATION, new NeutralQuery(
                        new NeutralCriteria("stateOrganizationId", NeutralCriteria.OPERATOR_EQUAL, (String) appAuth.getBody().get("authId"))));
                if (edorgEntity != null) {
                    blacklist.add(edorgEntity.getEntityId());
                }
            }
        }

        debug("Blacklisted Edorgs = {}", blacklist.toString());
        return blacklist;
    }
}
