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

package org.slc.sli.api.resources.v1;

import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.aggregation.CalculatedDataListingResource;

/**
 * The operations a CRUD endpoint should be able to perform (Create, Read, Update, Delete)
 *
 * @author kmyers
 *
 */
@Deprecated
public interface CrudEndpoint {

    /**
     * Reads all entities from a specific location or collection.
     *
     * @param resourceName
     *            where the entity should be located
     * @param headers
     *            HTTP header information (which includes request headers)
     * @param uriInfo
     *            URI information including path and query parameters
     * @return requested information or error status
     */
    public Response readAll(String resourceName, HttpHeaders headers, UriInfo uriInfo);

    /**
     * Reads one or more entities from a specific location or collection.
     *
     * @param resourceName
     *            where the entity should be located
     * @param key
     *            field to be queried against
     * @param value
     *            comma separated list of values to be found in the key
     * @param headers
     *            HTTP header information (which includes request headers)
     * @param uriInfo
     *            URI information including path and query parameters
     * @return requested information or error status
     */
    public Response read(String resourceName, String key, String value, HttpHeaders headers, UriInfo uriInfo);

    /**
     * Searches "resourceName" for entries where "key" equals "value", then for each result
     * uses "idkey" field's value to query "resolutionResourceName" against the ID field.
     *
     * @param resourceName
     *            where the entity should be located
     * @param key
     *            field to be queried against (when searching resources)
     * @param value
     *            comma separated list of expected values to be found for the key
     * @param idKey
     *            field in resource that contains the ID to be resolved
     * @param resolutionResourceName
     *            where to query for the entitity with the ID taken from the "idKey" field
     * @param headers
     *            HTTP header information (which includes request headers)
     * @param uriInfo
     *            URI information including path and query parameters
     * @return requested information or error status
     */
    public Response read(String resourceName, String key, String value, String idKey, String resolutionResourceName,
            HttpHeaders headers, UriInfo uriInfo);

    /**
     * Reads one or more entities from a specific location or collection.
     *
     * @param resourceName
     *            where the entity should be located
     * @param idList
     *            a single ID or a comma separated list of IDs
     * @param headers
     *            HTTP header information (which includes request headers)
     * @param uriInfo
     *            URI information including path and query parameters
     * @return requested information or error status
     */
    public Response read(String resourceName, String idList, HttpHeaders headers, UriInfo uriInfo);

    /**
     * Creates a new entity in a specific location or collection.
     *
     * @param resourceName
     *            where the entity should be located
     * @param newEntityBody
     *            new map of keys/values for entity
     * @param headers
     *            HTTP header information (which includes request headers)
     * @param uriInfo
     *            URI information including path and query parameters
     * @return resulting status from request
     */
    public Response create(String resourceName, EntityBody newEntityBody, HttpHeaders headers, UriInfo uriInfo);

    /**
     * Updates a given entity in a specific location or collection.
     *
     * @param resourceName
     *            where the entity should be located
     * @param id
     *            ID of object being updated
     * @param newEntityBody
     *            new map of keys/values for entity
     * @param headers
     *            HTTP header information (which includes request headers)
     * @param uriInfo
     *            URI information including path and query parameters
     * @return resulting status from request
     */
    public Response update(String resourceName, String id, EntityBody newEntityBody, HttpHeaders headers,
            UriInfo uriInfo);

    /**
     * Deletes a given entity from a specific location or collection.
     *
     * @param resourceName
     *            where the entity should be located
     * @param id
     *            ID of object being deleted
     * @param headers
     *            HTTP header information (which includes request headers)
     * @param uriInfo
     *            URI information including path and query parameters
     * @return resulting status from request
     */
    public Response delete(String resourceName, String id, HttpHeaders headers, UriInfo uriInfo);

    /**
     * Patches a given entity in a specific location or collection, which means that
     * less than the full entity body is passed in the request and only passed keys are
     * updated and the rest of the entity remains the same.
     *
     * @param resourceName
     *            where the entity should be located
     * @param id
     *            ID of object being patched
     * @param newEntityBody
     *            new map of keys/values for entity (partial set of key/values)
     * @param headers
     *            HTTP header information (which includes request headers)
     * @param uriInfo
     *            URI information including path and query parameters
     * @return resulting status from request
     */
    public Response patch(String resourceName, String id, EntityBody newEntityBody, HttpHeaders headers, UriInfo uriInfo);

    /**
     * Get derived values for the given entity.
     *
     * @param id
     *            the id of the entity
     * @return the aggregated and derived values
     */
    public CalculatedDataListingResource<String> getCalculatedValueListings(String id);

    /**
     * Get aggregates and derived values for the given entity
     *
     * @param id
     *            the id of the entity
     * @return the aggregated and derived values
     */
    public CalculatedDataListingResource<Map<String, Integer>> getAggregationListings(String id);
}
