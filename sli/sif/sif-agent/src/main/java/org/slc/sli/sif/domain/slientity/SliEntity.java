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

package org.slc.sli.sif.domain.slientity;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.impl.GenericEntity;

/**
 * An GenericEntity in the SLI domain. Each SLI Entity can be converted to a
 * JSON Node ready for SLI operations.
 *
 * @author slee
 *
 */
public abstract class SliEntity {
    protected static final ObjectMapper MAPPER = new ObjectMapper();

    protected static final Logger LOG = LoggerFactory.getLogger(SliEntity.class);

    private String creatorRefId = null;
    private String otherSifRefId = null;
    private String zoneId = null;
    private Entity matchedEntity;

    /**
     * Constructor
     */
    public SliEntity() {
        // Empty Default constructor
    }

    /**
     * Get the SLI entity type name
     */
    public abstract String entityType();

    /**
     * Output body of this Entity
     */
    public GenericEntity createGenericEntity() {
        GenericEntity entity = new GenericEntity(entityType(), createBody());
        return entity;
    }

    /**
     * Output body of this Entity
     */
    public Map<String, Object> createBody() {
        Map<String, Object> body = null;
        try {
            body = MAPPER.
                    readValue(this.json(), new TypeReference<Map<String, Object>>() {
            });
            clearNullValueKeys(body);
        } catch (JsonParseException e) {
            LOG.error("Entity map conversion error: ", e);
        } catch (JsonMappingException e) {
            LOG.error("Entity map conversion error: ", e);
        } catch (IOException e) {
            LOG.error("Entity map conversion error: ", e);
        }

        return body;
    }

    /**
     * Output this Entity as a JSON Node
     */
    public JsonNode json() {
        return MAPPER.valueToTree(this);
    }

    /**
     * creatorRefId is a helper attribute which is excluded from json body.
     *
     * Return true if this entity can be created by more than one SIF data objects
     * and the entity is already created by one of them.
     * For example, EmployeePersonal and StaffPersonal both can create a StaffEntity.
     * If a StaffEntity was create by EmployeePersonal, StaffPersonal should check
     * that it is already created.
     *
     */
    @JsonIgnore
    public boolean isCreatedByOthers() {
        return this.creatorRefId != null && this.creatorRefId.length() > 0;
    }

    @JsonIgnore
    public String getCreatorRefId() {
        return this.creatorRefId;
    }

    @JsonIgnore
    public void setCreatorRefId(String creatorRefId) {
        this.creatorRefId = creatorRefId;
    }

    /**
     * zoneId is a helper attribute which is excluded from json body.
     *
     */
    @JsonIgnore
    public String getZoneId() {
        return this.zoneId;
    }

    @JsonIgnore
    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * otherSifRefId is a helper attribute which is excluded from json body.
     *
     * It is an SIF RefId of other SIF Data Object.
     * When set, it can be used together with zoneId to query the SLI guid of the matched entity.
     *
     */
    @JsonIgnore
    public String getOtherSifRefId() {
        return this.otherSifRefId;
    }

    @JsonIgnore
    public void setOtherSifRefId(String otherSifRefId) {
        this.otherSifRefId = otherSifRefId;
    }

    @JsonIgnore
    public boolean hasOtherSifRefId() {
        return this.otherSifRefId != null && this.otherSifRefId.length() > 0;
    }

    /**
     * Output this object as a JSON String
     */
    @Override
    public String toString() {
        return json().toString();
    }

    // ============ private helper

    // removes all keys from this map that has a null value. If some values are
    // maps,
    // do it recursively
    private static void clearNullValueKeys(Map<String, Object> m) {
        Set<String> keySet = m.keySet();
        Set<Object> keysToRemove = new HashSet<Object>();
        for (Object k : keySet) {
            if (isNullValue(m.get(k))) {
                keysToRemove.add(k);
            }
        }
        for (Object k : keysToRemove) {
            m.remove(k);
        }
    }

    private static void clearNullValueFromList(List<?> l) {
        ListIterator<?> it = l.listIterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (isNullValue(o)) {
                it.remove();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static boolean isNullValue(Object o) {
        if (o == null) {
            return true;
        }
        if (o instanceof Map) {
            clearNullValueKeys((Map<String, Object>) o);
            return ((Map<?, ?>) o).isEmpty();
        } else if (o instanceof List) {
            clearNullValueFromList((List<?>) o);
            return ((List<?>) o).isEmpty();
        }
        return false;
    }
}
