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


package org.slc.sli.api.client;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Generic entity returned by the SLI API ReSTful service. Each entity has a unique
 * identifier, a data collection, and a collection of resource links. This interface provides
 * the most stripped down interface required by all entities.
 *
 * Each entity returned by the SLI API contains a type string. This type identifies
 * the SLI data model type returned by the API. When creating or updating an entity, the
 * entity type must be provided. The API will reject entities that are not typed.
 *
 * Data is represented as a map of maps. Value types are defined by the SLI data model.
 * The API validates all incoming entities and rejects entities that are missing required
 * fields or contain invalid values.
 *
 * The API does entity validation against the SLI XML Schema. Attempts to create or update
 * an entity using data that does not conform to this schema are rejected by the API.
 *
 * Each Entity returned by the API contains one or more links to associated resources.
 * These resources are context-sensitive. These links are read-only and cannot be altered
 * by client applications.
 *
 * The fields returned by the API and the available resource links are context-sensitive based
 * on the role(s) assigned by an identity provider and associations between the user and
 * the resource, if any. Responses contain only the information available to the user
 * based on these roles and associations. Resources that are not associated with the
 * current user are not returned by the API.
 *
 * @author asaarela
 */
public interface Entity {

    /** Key to locate 'links' field of the Entity. */
    static final String LINKS_KEY = "links";

    /** Key to locate the Entity's id field */
    static final String ENTITY_ID_KEY = "id";

    /**
     * Get the data associated with this entity. If the entity has no data, returns
     * an empty map. The key into this map is the property name. The values of this
     * map can one of the following JSON types:
     *
     * <ul>
     * <li>List</li>
     * <li>Map</li>
     * <li>null</li>
     * <li>Boolean</li>
     * <li>Character</li>
     * <li>Long</li>
     * <li>Double</li>
     * <li>String</li>
     * </ul>
     *
     * @return Map of data.
     */
    Map<String, Object> getData();

    /**
     * Get the type name for this entity.
     *
     * @return EntityType for this entity
     *
     * @see org.slc.sli.api.client.constants.EntityNames for a list of available names.
     */
    String getEntityType();

    /**
     * Get the ID for the entity. Each entity in the system has a unique identifier
     * assigned to it.
     *
     * @return id String
     */
    String getId();

    /**
     * Get a list of links for this entity. If the entity has no links, returns an empty list.
     *
     * @return a List of links.
     */
    @Deprecated
    List<Link> getLinks();

    /**
     * Get a map of resource name to link.
     *
     * @return a Map of resource name to link
     */
    Map<String, URL> getLinkMap();
}
