package org.slc.sli.api.client;

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
 * Each Entity returned by the API contains one or more links to associated resources.
 * These resources are context-sensitive.
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

    /** Key to locate the Entity's body information */
    static final String ENTITY_BODY_KEY = "body";

    /** Key to locate the Entity's metadata */
    static final String ENTITY_METADATA_KEY = "metaData";

    /**
     * Get the data associated with this entity. If the entity has no data, returns
     * an empty map. The key into this map is the property name.  The values of this
     * map can one of the following JSON types:
     *
     * <ul>
     * <li>List</li>
     * <li>Map</li>
     * <li>null</li>
     * <li>Boolean<li>
     * <li>Character</li>
     * <li>Integer</li>
     * <li>Long<li>
     * <li>Float</li>
     * <li>Double</li>
     * <li>String</li>
     * </ul>
     *
     * @return Map of data.
     */
    Map<String, Object> getData();

    /**
     * Get the body associated with this entity. If the entity has no body, returns
     * an empty map. The key into this map is the property name.  The values of this
     * map can one of the following JSON types:
     *
     * <ul>
     * <li>List</li>
     * <li>Map</li>
     * <li>null</li>
     * <li>Boolean<li>
     * <li>Character</li>
     * <li>Integer</li>
     * <li>Long<li>
     * <li>Float</li>
     * <li>Double</li>
     * <li>String</li>
     * </ul>
     *
     * @return Map of data.
     */
    Map<String, Object> getBody();


    /**
     * Get the metadata associated with this entity. If the entity has no metadata, returns
     * an empty map. The key into this map is the metadata property name.  The values of this
     * map can one of the following JSON types:
     *
     * <ul>
     * <li>List</li>
     * <li>Map</li>
     * <li>null</li>
     * <li>Boolean<li>
     * <li>Character</li>
     * <li>Integer</li>
     * <li>Long<li>
     * <li>Float</li>
     * <li>Double</li>
     * <li>String</li>
     * </ul>
     *
     * @return Map of data.
     */
	Map<String, Object> getMetaData();


    /**
     * Get the type name for this entity.
     * @return EntityType for this entity
     *
     * @see org.slc.sli.common.constants.EntityNames for a list of available names.
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
