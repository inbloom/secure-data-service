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

package org.slc.sli.dal.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.tenantdb.TenantIdToDbName;
import org.slc.sli.domain.CalculatedData;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
/**
 * elasticsearch connector
 */
public class ElasticSearchRepository implements Repository<Entity> {

    static final Logger LOG = LoggerFactory.getLogger(ElasticSearchRepository.class);


    @Autowired
    private ElasticSearchQueryConverter converter;

    private String esUri;

    private RestTemplate searchTemplate;

    private String esUsername;

    private String esPassword;

    // transport client is used for a query builder. The actual connection is over http.
    private Client esClient;

    private Client getClient() {
        return esClient;
    }

    /**
     * called by init-method
     *
     * @throws Exception
     */
    public void init() throws Exception {
        esClient = new TransportClient();
    }

    /**
     * called by destroy-method
     *
     * @throws Exception
     */
    public void destroy() throws Exception {
        esClient.close();
    }

    @Override
    public Iterable<Entity> findAll(String collectionName, NeutralQuery neutralQuery) {
        SearchRequestBuilder srb = getClient().prepareSearch(
                TenantContext.getTenantId().toLowerCase()).setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        srb.setQuery(converter.getQuery(neutralQuery));
        // convert the response to search hits
        return EntityConverter.toEntityCol(sendRESTQuery(srb.toString()));
    }

    /**
     * Send REST query to elasticsearch server
     *
     * @param query
     * @return
     */
    private HttpEntity<String> sendRESTQuery(String query) {

        HttpMethod method = HttpMethod.POST;
        HttpHeaders headers = new HttpHeaders();

        // Basic Authentication when username and password are provided
        if (esUsername != null && esPassword != null) {
            headers.set("Authorization",
                    "Basic " + Base64.encodeBase64String((esUsername + ":" + esPassword).getBytes()));
        }
        HttpEntity<String> entity = new HttpEntity<String>(query, headers);

        // make the REST call
        try {
            return searchTemplate.exchange(esUri, method, entity, String.class, getIndex());
        } catch (RestClientException rce) {
            LOG.error("Error sending elastic search request!", rce);
            throw rce;

        }
    }

    private String getIndex() {
        return TenantIdToDbName.convertTenantIdToDbName(TenantContext.getTenantId()).toLowerCase();
    }

    public void setSearchUrl(String esUrl) {
        this.esUri = esUrl + "/{tenantId}/_search";
    }

    public void setSearchTemplate(RestTemplate searchTemplate) {
        this.searchTemplate = searchTemplate;
    }

    public void setSearchUsername(String esUsername) {
        this.esUsername = esUsername;
    }

    public void setSearchPassword(String esPassword) {
        this.esPassword = esPassword;
    }

    /**
     * Simple adapter for SearchHits to Entity
     *
     */
    static final class SearchHitEntity implements Entity {
        private Map<String, Object> body;
        private Map<String, Object> metaData;
        private String type;
        private String id;

        SearchHitEntity(String id, String type, Map<String, Object> body, Map<String, Object> metaData) {
            this.id = id;
            this.type = type;
            this.body = body;
            this.body.put("type", type);
            this.metaData = metaData;
        }

        @Override
        public String getType() {
            return this.type;
        }

        @Override
        public String getEntityId() {
            return this.id;
        }

        @Override
        public Map<String, Object> getBody() {
            return this.body;
        }

        @Override
        public Map<String, Object> getMetaData() {
            return metaData;
        }

        @Override
        public CalculatedData<Map<String, Integer>> getAggregates() {
            return null;
        }

        @Override
        public CalculatedData<String> getCalculatedValues() {
            return null;
        }

        @Override
        public String getStagedEntityId() {
            return null;
        }

        @Override
        public Map<String, List<Entity>> getEmbeddedData() {
            return null;
        }
    }

    // Unimplemented methods

    @Override
    public Entity create(String type, Map<String, Object> body) {
        throw new UnsupportedOperationException("ElasticSearchRepository.create not implemented");
    }

    @Override
    public Entity create(String type, Map<String, Object> body, String collectionName) {
        throw new UnsupportedOperationException("ElasticSearchRepository.create not implemented");
    }

    @Override
    public Entity create(String type, Map<String, Object> body, Map<String, Object> metaData, String collectionName) {
        throw new UnsupportedOperationException("ElasticSearchRepository.create not implemented");
    }

    @Override
    public Entity findById(String collectionName, String id) {
        throw new UnsupportedOperationException("ElasticSearchRepository.findById not implemented");
    }

    @Override
    public boolean exists(String collectionName, String id) {
        throw new UnsupportedOperationException("ElasticSearchRepository.exists not implemented");
    }

    @Override
    public Entity findOne(String collectionName, NeutralQuery neutralQuery) {
        throw new UnsupportedOperationException("ElasticSearchRepository.findOne not implemented");
    }

    @Override
    public Iterable<String> findAllIds(String collectionName, NeutralQuery neutralQuery) {
        throw new UnsupportedOperationException("ElasticSearchRepository.findAllIds not implemented");
    }

    @Override
    public long count(String collectionName, NeutralQuery neutralQuery) {
        return 0;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean update(String collection, Entity object) {
        throw new UnsupportedOperationException("ElasticSearchRepository.update not implemented");
    }

    @Override
    public boolean delete(String collectionName, String id) {
        throw new UnsupportedOperationException("ElasticSearchRepository.delete not implemented");
    }

    @Override
    public void deleteAll(String collectionName, NeutralQuery query) {
        throw new UnsupportedOperationException("ElasticSearchRepository.deleteAll not implemented");
    }

    @Override
    public DBCollection getCollection(String collectionName) {
        throw new UnsupportedOperationException("ElasticSearchRepository.getCollection not implemented");
    }

    @Override
    public List<DBCollection> getCollections(boolean includeSystemCollections) {
        throw new UnsupportedOperationException("ElasticSearchRepository.getCollections not implemented");
    }

    @Override
    public boolean collectionExists(String collection) {
        return false;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<Entity> findByQuery(String collectionName, org.springframework.data.mongodb.core.query.Query query, int skip, int max) {
        throw new UnsupportedOperationException("ElasticSearchRepository.findByQuery not implemented");
    }

    @Override
    public long count(String collectionName, org.springframework.data.mongodb.core.query.Query query) {
        throw new UnsupportedOperationException("ElasticSearchRepository.count not implemented");
    }

    @Override
    public void setWriteConcern(String writeConcern) {
    }

    @Override
    public void setReferenceCheck(String referenceCheck) {
    }

    @Override
    public boolean doUpdate(String collection, NeutralQuery query, Update update) {
        throw new UnsupportedOperationException("ElasticSearchRepository.doUpdate not implemented");
    }

    @Override
    public boolean updateWithRetries(String collection, Entity object, int noOfRetries) {
        throw new UnsupportedOperationException("ElasticSearchRepository.updateWithRetries not implemented");
    }

    @Override
    public List<Entity> insert(List<Entity> records, String collectionName) {
        throw new UnsupportedOperationException("ElasticSearchRepository.insert not implemented");
    }

    @Override
    public Entity createWithRetries(String type, String id, Map<String, Object> body, Map<String, Object> metaData,
            String collectionName, int noOfRetries) {
        throw new UnsupportedOperationException("ElasticSearchRepository.createWithRetries not implemented");
    }

    @Override
    public boolean patch(String type, String collectionName, String id, Map<String, Object> newValues) {
        throw new UnsupportedOperationException("ElasticSearchRepository.patch not implemented");
    }

    @Override
    public WriteResult updateMulti(NeutralQuery query, Map<String, Object> update, String entityReferenced) {
        throw new UnsupportedOperationException("ElasticSearchRepository.updateMulti not implemented");
    }

    public static class EntityConverter {
        /**
         * Converts elasticsearch http response to collection of entities
         *
         * @param response
         * @return
         */
        static Collection<Entity> toEntityCol(HttpEntity<String> response) {

            ObjectMapper mapper = new ObjectMapper();
            Collection<Entity> hits = new ArrayList<Entity>();
            JsonNode node = null;
            try {

                // get the hits from the response
                node = mapper.readTree(response.getBody());
                JsonNode hitsNode = node.get("hits").get("hits");

                SearchHitEntity hit;

                // create a search hit entity object for each hit
                for (int i = 0; i < hitsNode.size(); i++) {
                    hit = convertJsonToSearchHitEntity(hitsNode.get(i));
                    if (hit != null) {
                        hits.add(hit);
                    }
                }
            } catch (JsonProcessingException e) {
                ElasticSearchRepository.LOG.error("Error converting search response to entities", e);
            } catch (IOException e) {
                ElasticSearchRepository.LOG.error("Error converting search response to entities", e);
            }
            return hits;
        }

        /**
         * Converts a json response to a search hit entity
         */
        static SearchHitEntity convertJsonToSearchHitEntity(JsonNode hitNode) {
            try {
                TypeReference<Map<String, Object>> tr = new TypeReference<Map<String, Object>>() {
                };
                // read the values from the json
                ObjectMapper mapper = new ObjectMapper();
                String id = hitNode.get("_id").getTextValue();
                String type = hitNode.get("_type").getTextValue();
                JsonNode bodyNode = hitNode.get("_source");
                Map<String, Object> body = mapper.readValue(bodyNode, tr);
                body.remove("context");

                // create a return the search hit entity
                return new SearchHitEntity(id, type, body, null);

            } catch (JsonMappingException e) {
                ElasticSearchRepository.LOG.error("Error converting search json response to search hit entity", e);
            } catch (IOException e) {
                ElasticSearchRepository.LOG.error("Error converting search json response to search hit entity", e);
            }
            return null;
        }

    }
}
