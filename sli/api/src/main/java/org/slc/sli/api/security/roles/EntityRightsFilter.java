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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.Treatment;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.enums.Right;

/**
 * Class to filter the authorized fields from the entity.
 *
 * @author: tke
 */
@Component
public class EntityRightsFilter {
    @Autowired
    private RightAccessValidator rightAccessValidator;

    /**
     * given an entity, make the entity body to be exposed
     *
     * @param entity
     *            the entity to be filtered
     * @param treamts
     *            treatments to be applied to the entity
     * @param defn
     *            EntityDefinition needed to create the entity
     * @return the result entity body
     */
    public EntityBody makeEntityBody(Entity entity, List<Treatment> treamts, EntityDefinition defn, boolean isSelf) {

        Collection<GrantedAuthority> nonSelfAuths = rightAccessValidator.getContextualAuthorities(false, entity);
        Collection<GrantedAuthority> selfAuths = rightAccessValidator.getContextualAuthorities(isSelf, entity);

        return makeEntityBody(entity, treamts, defn, nonSelfAuths, selfAuths);
    }

    /**
     * Given an entity, make the entity body to be exposed.
     *
     * @param entity
     *            the entity to be filtered
     * @param treamts
     *            treatments to be applied to the entity
     * @param defn
     *            EntityDefinition needed to create the entity
     * @param nonSelfAuths
     *            list of granted authorities without self rights
     * @param selfAuths
     *            list of granted authorities with self rights if necessary.
     * @return the result entity body
     */
    public EntityBody makeEntityBody(Entity entity, List<Treatment> treamts, EntityDefinition defn,
            Collection<GrantedAuthority> nonSelfAuths, Collection<GrantedAuthority> selfAuths) {

        EntityBody toReturn = exposeTreatments(entity, treamts, defn);

        String type = entity.getType() != null ? entity.getType() : defn.getType();
        toReturn = filterBody(toReturn, type, nonSelfAuths, selfAuths);

        if ((entity.getEmbeddedData() != null) && !entity.getEmbeddedData().isEmpty()) {
            for (Map.Entry<String, List<Entity>> enbDocList : entity.getEmbeddedData().entrySet()) {
                List<EntityBody> subDocbody = new ArrayList<EntityBody>();
                for (Entity subEntity : enbDocList.getValue()) {
                    EntityBody sdBody = exposeTreatments(subEntity, treamts, defn);
                    subDocbody.add(filterBody(sdBody, type, nonSelfAuths, selfAuths));
                }
                toReturn.put(enbDocList.getKey(), subDocbody);
            }
        }
        return toReturn;
    }

    /**
     * Expose the provided list of treatments to the entity.
     *
     * @param entity
     *            entity to be treated
     * @param treatments
     *            list of treatments to expose
     * @param defn
     *            entity definition
     * @return treated entity body.
     */
    public EntityBody exposeTreatments(Entity entity, List<Treatment> treatments, EntityDefinition defn) {
        EntityBody toReturn = new EntityBody(entity.getBody());

        for (Treatment treatment : treatments) {
            toReturn = treatment.toExposed(toReturn, defn, entity);
        }

        return toReturn;
    }

    protected EntityBody filterBody(EntityBody entityBody, String entityType,
            Collection<GrantedAuthority> nonSelfAuths, Collection<GrantedAuthority> selfAuths) {

        filterFields(entityBody, selfAuths, "", entityType);

        complexFilter(entityBody, nonSelfAuths, entityType);

        return entityBody;
    }

    protected void complexFilter(EntityBody entityBody, Collection<GrantedAuthority> auths, String entityType) {

        if (!auths.contains(Right.READ_RESTRICTED) && (entityType.equals(EntityNames.STAFF) || entityType.equals(EntityNames.TEACHER))) {
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
    protected void filterFields(EntityBody entityBody, Collection<GrantedAuthority> auths, String prefix,
            String entityType) {

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

    public void setRightAccessValidator(RightAccessValidator rightAccessValidator) {
        this.rightAccessValidator = rightAccessValidator;
    }

}
