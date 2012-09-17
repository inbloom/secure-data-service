/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.security.CallingApplicationInfoProvider;
import org.slc.sli.api.security.context.traversal.graph.NodeAggregator;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;

/**
 *
 *
 */
@Component
public class EdOrgToChildEdOrgNodeFilter extends NodeAggregator {

    private static final String REFERENCE = "parentEducationAgencyReference";

    @Autowired
    private CallingApplicationInfoProvider clientInfo;

    @Autowired
    
    private PagingRepositoryDelegate<Entity> repo;

    @Override
    public List<String> addAssociatedIds(List<String> ids) {
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
        return new ArrayList<String>(toReturn);
    }

    /**
     * Finds all the child ed orgs immediately under a SEA.
     *
     * @param edOrgId
     *            - the mongo ID of the SEA
     * @return
     */
    public List<String> getChildEducationOrganizations(String edOrgId) {
        List<String> myEdOrgsIds = new ArrayList<String>();
        if (edOrgId != null) {
            NeutralQuery childrenQuery = new NeutralQuery();
            childrenQuery.addCriteria(new NeutralCriteria("parentEducationAgencyReference", "=", edOrgId));

            Iterable<Entity> myEdOrgs = repo.findAll(EntityNames.EDUCATION_ORGANIZATION, childrenQuery);

            for (Entity cur : myEdOrgs) {
                myEdOrgsIds.add(cur.getEntityId());
            }
        }
        return myEdOrgsIds;
    }
    
    public Set<String> fetchParents(Set<String> ids) {
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

    public Set<String> getBlacklist() {
        Set<String> blacklist = new HashSet<String>();
        String clientId = clientInfo.getClientId();

        if (null == clientId) {
            return blacklist; // Unit tests dont have a client ID
        }

        NeutralQuery nq = new NeutralQuery(new NeutralCriteria("client_id", NeutralCriteria.OPERATOR_EQUAL, clientId));
        Entity appEntity = repo.findOne("application", nq);

        if (null == appEntity) {
            return blacklist; // No application found with this client ID
        }

        String appId = appEntity.getEntityId();
        NeutralQuery nq2 = new NeutralQuery();
        Iterable<Entity> entities = repo.findAll("applicationAuthorization", nq2);

        for (Iterator<Entity> i = entities.iterator(); i.hasNext();) {
            Entity appAuth = i.next();
            List<String> appIdArray = (List<String>) appAuth.getBody().get("appIds");
            if (!appIdArray.contains(appId)) {
                NeutralQuery query = new NeutralQuery(new NeutralCriteria("_id",
                        NeutralCriteria.OPERATOR_EQUAL, appAuth.getBody().get("authId"), false));
                Entity edorgEntity = repo.findOne(EntityNames.EDUCATION_ORGANIZATION, query);
                if (edorgEntity != null) {
                    blacklist.add(edorgEntity.getEntityId());
                }
            }
        }

        debug("Blacklisted Edorgs = {}", blacklist.toString());
        return blacklist;
    }
}
