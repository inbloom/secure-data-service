/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
package org.slc.sli.api.security.roles;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.service.Treatment;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Class to filter the authorized fields from the entity.
 *
 * @author: tke
 */
@Component
public class EntityRightsFilter {

    @Autowired
    RightAccessValidator rightAccessValidator;

    /**
     * given an entity, make the entity body to expose
     *
     * @param entity   the entity to be filtered
     * @param treamts  treatments to be applied to the entity
     * @param defn     EntityDefinition needed to create the entity
     * @return
     */
    public EntityBody makeEntityBody(Entity entity, List<Treatment> treamts, EntityDefinition defn) {
        EntityBody toReturn = createBody(entity, treamts, defn);

        if ((entity.getEmbeddedData() != null) && !entity.getEmbeddedData().isEmpty()) {
            for (Map.Entry<String, List<Entity>> enbDocList : entity.getEmbeddedData().entrySet()) {
                List<EntityBody> subDocbody = new ArrayList<EntityBody>();
                for (Entity subEntity : enbDocList.getValue()) {
                    subDocbody.add(createBody(subEntity, treamts, defn));
                }
                toReturn.put(enbDocList.getKey(), subDocbody);
            }
        }
        return toReturn;
    }

    private EntityBody createBody(Entity entity, List<Treatment> treatments, EntityDefinition defn) {
        EntityBody toReturn = new EntityBody(entity.getBody());

        for (Treatment treatment : treatments) {
            toReturn = treatment.toExposed(toReturn, defn, entity);
        }

        Collection<GrantedAuthority> auths = rightAccessValidator.getAuthorities(false, entity);

        filterFields(toReturn, auths, "", entity.getType());
        complexFilter(toReturn, auths, entity.getType());

        return toReturn;
    }

    private void complexFilter(EntityBody entityBody, Collection<GrantedAuthority> auths, String entityType) {

        if (!auths.contains(Right.READ_RESTRICTED) && entityType.equals(EntityNames.STAFF)) {
            final String work = "Work";
            final String telephoneNumberType = "telephoneNumberType";
            final String emailAddressType = "emailAddressType";
            final String telephone = "telephone";
            final String electronicMail = "electronicMail";

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> telephones = (List<Map<String, Object>>) entityBody.get(telephone);
            if (telephones != null) {

                for (Iterator<Map<String, Object>> it = telephones.iterator(); it.hasNext(); ) {
                    if (!work.equals(it.next().get(telephoneNumberType))) {
                        it.remove();
                    }
                }

            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> emails = (List<Map<String, Object>>) entityBody.get(electronicMail);
            if (emails != null) {

                for (Iterator<Map<String, Object>> it = emails.iterator(); it.hasNext(); ) {
                    if (!work.equals(it.next().get(emailAddressType))) {
                        it.remove();
                    }
                }

            }

        }
    }

    @SuppressWarnings("unchecked")
    private void filterFields(EntityBody entityBody, Collection<GrantedAuthority> auths, String prefix, String entityType) {

        if (!auths.contains(Right.FULL_ACCESS)) {

            List<String> toRemove = new LinkedList<String>();

            for (Iterator<Map.Entry<String, Object>> it = entityBody.entrySet().iterator(); it.hasNext();) {
                String fieldName = it.next().getKey();

                Set<Right> neededRights = rightAccessValidator.getNeededRights(prefix + fieldName, entityType);

                if (!neededRights.isEmpty() && !rightAccessValidator.intersection(auths, neededRights)) {
                    it.remove();
                }
            }

        }
    }
}
