package org.slc.sli.api.client;

import java.util.List;
import java.util.Map;

/**
 * Generic entity returned by the SLI API ReSTful service. Each entity has a unique
 * identifier, a data collection, and a collection of resource links. This interface provides
 * the most stripped down interface required by all entities.
 *
 * Each entity returned by the SLI API contains a type string. This type identifies
 * the SLI data model type returned by the SLI. When creating or updating an entity, the
 * entity type must be provided. The SLI will reject entities that are not typed.
 *
 * Data is represented as a map of maps. Value types are defined by the SLI data model.
 * The API validates all incoming entities and rejects entities that are missing required
 * fields or contain invalid values.
 *
 * Each Entity returned by the API contains one or more links to associated resources.
 *
 * The fields returned by the API and the available resource links are context-dependent based
 * on the role(s) assigned by an identity provider. Responses contain only the information
 * available to the user based on these roles.
 *
 * @author asaarela
 */
public interface Entity {

    /** Key to locate 'links' field of the Entity. */
    public static final String LINKS_KEY = "links";

    /** Key to locate the Entity's id field */
    public static final String ENTITY_ID_KEY = "id";

    /**
     * Get the data associated with this entity. If the entity has no data, returns
     * an empty map.
     *
     * @return Map of data.
     */
    Map<String, Object> getData();

    /**
     * Standard helper functions.
     */

    /**
     * Get the type for this entity.
     * @return EntityType for this entity
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
    List<Link> getLinks();

}
