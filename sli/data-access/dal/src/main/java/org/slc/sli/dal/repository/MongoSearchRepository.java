package org.slc.sli.dal.repository;


import com.google.common.collect.Iterators;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.tenantdb.TenantIdToDbName;
import org.slc.sli.domain.*;
import org.slc.sli.encryption.tool.Encryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

//import org.elasticsearch.action.search.SearchRequestBuilder;
//import org.elasticsearch.action.search.SearchType;
//import org.elasticsearch.client.Client;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.cache.CacheBuilder;
//import org.elasticsearch.common.cache.CacheLoader;
//import org.elasticsearch.common.cache.LoadingCache;
//import org.elasticsearch.common.collect.Iterators;

public class MongoSearchRepository implements Repository<Entity> {
    private static final Logger LOG = LoggerFactory.getLogger(MongoSearchRepository.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    //@Autowired
    // private ElasticSearchQueryConverter converter;

    //private String esUri;

    private RestTemplate searchTemplate;

    //private String esUsername;
    //private String esPassword;

    private Encryptor encryptor;

    private String dalKeyAlias;

    private String keyStorePass;

    private boolean enableEncryption;

    // transport client is used for a query builder. The actual connection is over http.
    //private Client esClient;

    //private Client getClient() {
    //    return esClient;
    //}

    public void init() throws Exception {
        LOG.info(">>>MongoSearchRepository.init()");
        // This is an ugly hack to prevent an exception to be written to
        // standard output by the elastic search package, when it cannot load the "snappy" library
        // TODO: remove this when elastic search removes snappy compression as a dependency.
        //PrintStream stdout = System.out;
        //System.setOut(new PrintStream(new NullOutputStream()));
        //try {
        //    esClient = new TransportClient();
        //}
        //finally {
        //    System.setOut(stdout);
        //}
        LOG.info("<<<MongoSearchRepository.init()");
    }

    public void destroy() throws Exception {
        LOG.info(">>>MongoSearchRepository.destroy()");
        //esClient.close();
        LOG.info("<<<MongoSearchRepository.destroy()");
    }

    @Override
    public Iterable<Entity> findAll(String collectionName, NeutralQuery neutralQuery) {
        LOG.info(">>>MongoSearchRepository.findAll()");
        LOG.info("   collectionName: " + collectionName);
        LOG.info("   neutralQuery: " + ToStringBuilder.reflectionToString(neutralQuery, ToStringStyle.MULTI_LINE_STYLE));
        //return ReadConverter.fromSearchJson(queryForSearch(getQuery(neutralQuery, false)));
        String qry = getQuery(neutralQuery, false);
        queryForSearch(qry);
        LOG.info("<<<MongoSearchRepository.findAll()");
        return null;
    }

    /**
     * Send REST query to elasticsearch server
     *
     * @param query
     * @return
     */
    private HttpEntity<String> query(String url, HttpMethod method, String query, Object[] params) {
        LOG.info(">>>MongoSearchRepository.query()");
        LOG.info("   url: " + url);
        LOG.info("   httpMethod: " + ToStringBuilder.reflectionToString(method, ToStringStyle.MULTI_LINE_STYLE));
        LOG.info("   query: " + query);
        LOG.info("   params: " + ToStringBuilder.reflectionToString(params, ToStringStyle.MULTI_LINE_STYLE));


        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json;charset=utf-8");

        // Basic Authentication when username and password are provided
//        if (esUsername != null && esPassword != null) {
//            try {
//                headers.set(
//                      "Authorization",
//                      "Basic "
//                            + Base64.encodeBase64String(((this.enableEncryption ? this.encryptor.decrypt(
//                            this.dalKeyAlias, this.keyStorePass, this.esUsername) : this.esUsername) + ":" + (this.enableEncryption ? this.encryptor
//                            .decrypt(this.dalKeyAlias, this.keyStorePass, this.esPassword)
//                            : this.esPassword)).getBytes())
//                );
//            } catch (Exception e) {
//                MongoSearchRepository.LOG.error("Error decrypting", e);
//            }
//        }

        long start = System.currentTimeMillis();
        HttpEntity<String> entity = new HttpEntity<String>(query, headers);

        // make the REST call
        try {
            return searchTemplate.exchange(url, method, entity, String.class, params);
        } finally {
            LOG.info("ES call {} finished in {} ms", entity, System.currentTimeMillis() - start);
            LOG.info("<<<MongoSearchRepository.query()");
        }
    }

    private String getIndex() {
        return TenantIdToDbName.convertTenantIdToDbName(TenantContext.getTenantId()).toLowerCase();
    }

    /**
     * Get ES Query
     *
     * @param neutralQuery
     * @return query topost as a body
     */
    private String getQuery(NeutralQuery neutralQuery, boolean noFields) {
        LOG.info(">>>MongoSearchRepository.getQuery()");
//        SearchRequestBuilder srb = getClient().prepareSearch(TenantContext.getTenantId().toLowerCase()).setSearchType(
//              SearchType.DFS_QUERY_THEN_FETCH);
//        srb.setQuery(converter.getQuery(neutralQuery));
//        if (noFields) {
//            srb.setNoFields();
//        } else {
//            srb.setFrom(neutralQuery.getOffset()).setSize(neutralQuery.getLimit());
//        }
//        return srb.toString();
        LOG.info("<<<MongoSearchRepository.getQuery()");
        return "<null>";
    }

    private HttpEntity<String> queryForSearch(String query) {
        LOG.info(">>>MongoSearchRepository.queryForSearch()");
        String esUri = "<esUri foo>";
        return query(esUri + "/{tenantId}/_search", HttpMethod.POST, query, new Object[]{getIndex()});
    }

    private HttpEntity<String> queryForMapping() {
        String esUri = "<esUri foo>";
        return query(esUri + "/{tenantId}/_mapping", HttpMethod.GET, null, new Object[]{getIndex()});
    }

    //public void setSearchUrl(String esUrl) {
    //    this.esUri = esUrl;
    //}

    public void setSearchTemplate(RestTemplate searchTemplate) {
        this.searchTemplate = searchTemplate;
    }

    //public void setSearchUsername(String esUsername) {
    //    this.esUsername = esUsername;
    //}

    //public void setSearchPassword(String esPassword) {
    //    this.esPassword = esPassword;
    //}

    public void setEncryptor(Encryptor encryptor) {
        this.encryptor = encryptor;
    }

    public void setDalKeyAlias(String dalKeyAlias) {
        this.dalKeyAlias = dalKeyAlias;
    }

    public void setKeyStorePass(String keyStorePass) {
        this.keyStorePass = keyStorePass;
    }

    public void setEnableEncryption(boolean enableEncryption) {
        this.enableEncryption = enableEncryption;
    }

    // Unimplemented methods
    @Override
    public Entity create(String type, Map<String, Object> body) {
        throw new UnsupportedOperationException("create not implemented");
    }

    @Override
    public Entity create(String type, Map<String, Object> body, String collectionName) {
        throw new UnsupportedOperationException("Ecreate not implemented");
    }

    @Override
    public Entity create(String type, Map<String, Object> body, Map<String, Object> metaData, String collectionName) {
        throw new UnsupportedOperationException("create not implemented");
    }

    @Override
    public Entity findById(String collectionName, String id) {
        throw new UnsupportedOperationException(".indById not implemented");
    }

    @Override
    public Entity findById(String collectionName, String id, boolean allFields) {
        throw new UnsupportedOperationException("findById not implemented");
    }

    @Override
    public boolean exists(String collectionName, String id) {
        throw new UnsupportedOperationException("exists not implemented");
    }

    @Override
    public Entity findOne(String collectionName, NeutralQuery neutralQuery) {
        throw new UnsupportedOperationException("findOne not implemented");
    }

    @Override
    public Entity findOne(String collectionName, NeutralQuery neutralQuery, boolean allFields) {
        throw new UnsupportedOperationException("findOne not implemented");
    }

    @Override
    public Iterable<String> findAllIds(String collectionName, NeutralQuery neutralQuery) {
        throw new UnsupportedOperationException("findAllIds not implemented");
    }

    @Override
    public long count(String collectionName, NeutralQuery neutralQuery) {
        return ReadConverter.fromCountJson(queryForSearch(getQuery(neutralQuery, true)));
    }

    @Override
    public boolean update(String collection, Entity object, boolean isSuperdoc) {
        throw new UnsupportedOperationException("update not implemented");
    }

    @Override
    public boolean delete(String collectionName, String id) {
        throw new UnsupportedOperationException("ElasticSearchRepository.delete not implemented");
    }

    @Override
    public CascadeResult safeDelete(String entityType, String id, boolean cascade, boolean dryrun, boolean forced, boolean logViolations,
                                    Integer maxObjects, AccessibilityCheck access) {
        throw new UnsupportedOperationException("safeDelete not implemented");
    }

    @Override
    public void deleteAll(String collectionName, NeutralQuery query) {
        throw new UnsupportedOperationException("deleteAll not implemented");
    }

    @Override
    public DBCollection getCollection(String collectionName) {
        throw new UnsupportedOperationException("getCollection not implemented");
    }

    @Override
    public List<DBCollection> getCollections(boolean includeSystemCollections) {
        throw new UnsupportedOperationException("getCollections not implemented");
    }

   @Override
    public boolean collectionExists(String collection) {
       LOG.info(">>>MongoSearchRepository.collectionExists()");
//        try {
//            return Boolean.TRUE == availableTypesCache.get(collection);
//        } catch (ExecutionException e) {
//            LOG.error("Unable to check for existing types", e);
//        }
        return false;
    }

    @Override
    public Iterable<Entity> findByQuery(String collectionName, Query query,
                                        int skip, int max) {
        throw new UnsupportedOperationException("findByQuery not implemented");
    }

    @Override
    public long count(String collectionName, Query query) {
        throw new UnsupportedOperationException("count not implemented");
    }

    @Override
    public void setWriteConcern(String writeConcern) {
        // override super implementation with empty implementation
    }

    @Override
    public void setReferenceCheck(String referenceCheck) {
        // override super implementation with empty implementation
    }

    @Override
    public boolean doUpdate(String collection, NeutralQuery query, Update update) {
        throw new UnsupportedOperationException("doUpdate not implemented");
    }

    @Override
    public boolean updateWithRetries(String collection, Entity object, int noOfRetries) {
        throw new UnsupportedOperationException("updateWithRetries not implemented");
    }

    @Override
    public List<Entity> insert(List<Entity> records, String collectionName) {
        throw new UnsupportedOperationException("insert not implemented");
    }

    @Override
    public Entity createWithRetries(String type, String id, Map<String, Object> body, Map<String, Object> metaData,
                                    String collectionName, int noOfRetries) {
        throw new UnsupportedOperationException("createWithRetries not implemented");
    }

    @Override
    public boolean patch(String type, String collectionName, String id, Map<String, Object> newValues) {
        throw new UnsupportedOperationException("patch not implemented");
    }

    @Override
    public Entity findAndUpdate(String collectionName, NeutralQuery neutralQuery, Update update) {
        throw new UnsupportedOperationException("findAndUpdate not implemented");
    }

    @Override
    public WriteResult updateMulti(NeutralQuery query, Map<String, Object> update, String entityReferenced) {
        throw new UnsupportedOperationException("updateMulti not implemented");
    }

    @Override
    public Iterator<Entity> findEach(String collectionName, NeutralQuery query) {
        throw new UnsupportedOperationException("getDBCursor not implemented");
    }

    @Override
    public Iterator<Entity> findEach(String collectionName, Query query) {
        throw new UnsupportedOperationException("getDBCursor not implemented");
    }

    public static class ReadConverter {

        private static JsonNode getHitsNode(HttpEntity<String> response) throws JsonProcessingException, IOException {
            return objectMapper.readTree(response.getBody()).get("hits");
        }

        /**
         * Converts elasticsearch http response to collection of entities
         *
         * @param response
         * @return
         */
        static Collection<Entity> fromSearchJson(HttpEntity<String> response) {
            LOG.info(">>>MongoSearchRepository.fromSearchJson()");
            LOG.info("   response: " + ToStringBuilder.reflectionToString(response, ToStringStyle.MULTI_LINE_STYLE));

            Collection<Entity> hits = new ArrayList<Entity>();
            try {
                JsonNode hitsNode = getHitsNode(response).get("hits");
                SearchHitEntity hit;

                // create a search hit entity object for each hit
                for (int i = 0; i < hitsNode.size(); i++) {
                    hit = fromSingleSearchJson(hitsNode.get(i));
                    if (hit != null) {
                        hits.add(hit);
                    }
                }
            } catch (JsonProcessingException e) {
                MongoSearchRepository.LOG.error("Error converting search response to entities", e);
            } catch (IOException e) {
                MongoSearchRepository.LOG.error("Error converting search response to entities", e);
            }
            LOG.info("<<<MongoSearchRepository.fromSearchJson()");
            return hits;
        }

        /**
         * Converts a json response to a search hit entity
         */
        static SearchHitEntity fromSingleSearchJson(JsonNode hitNode) {
            LOG.info(">>>MongoSearchRepository.fromSingleSearchJson()");
            LOG.info("   hitNode: " + ToStringBuilder.reflectionToString(hitNode, ToStringStyle.MULTI_LINE_STYLE));

            try {
                TypeReference<Map<String, Object>> tr = new TypeReference<Map<String, Object>>() {
                };
                // read the values from the json
                String id = hitNode.get("_id").getTextValue();
                String type = hitNode.get("_type").getTextValue();
                JsonNode bodyNode = hitNode.get("_source");
                Map<String, Object> body = objectMapper.readValue(bodyNode, tr);
                body.remove("context");
                Map<String, Object> metaData = (Map<String, Object>) body.get("_metaData");
                body.remove("_metaData");

                // create a return the search hit entity
                return new SearchHitEntity(id, type, body, metaData);

            } catch (JsonMappingException e) {
                MongoSearchRepository.LOG.error("Error converting search json response to search hit entity", e);
            } catch (IOException e) {
                MongoSearchRepository.LOG.error("Error converting search json response to search hit entity", e);
            }
            LOG.info("<<<MongoSearchRepository.fromSingleSearchJson()");
            return null;
        }

        static long fromCountJson(HttpEntity<String> response) {
            LOG.info(">>>MongoSearchRepository.fromCountJson()");
            LOG.info("   response: " + ToStringBuilder.reflectionToString(response, ToStringStyle.MULTI_LINE_STYLE));
            try {
                return getHitsNode(response).get("total").asLong();
            } catch (Exception t) {
                LOG.error("Unable to get count from search engine", t);
            }
            LOG.info("<<<MongoSearchRepository.fromCountJson()");
            return 0;
        }

        static Set<String> fromMappingJson(HttpEntity<String> response) {
            LOG.info(">>>MongoSearchRepository.fromMappingJson()");
            LOG.info("   response: " + ToStringBuilder.reflectionToString(response, ToStringStyle.MULTI_LINE_STYLE));
            try {
                Set<String> set = new HashSet<String>();
                Iterators.addAll(set, objectMapper.readTree(response.getBody()).getElements().next().getFieldNames());
                return set;
            } catch (Exception t) {
                LOG.error("Unable to get mappings from search engine", t);
            }
            LOG.info("<<<MongoSearchRepository.fromMappingJson()");
            return Collections.emptySet();
        }
    }

    /**
     * Simple adapter for SearchHits to Entity
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

        @Override
        public Map<String, List<Map<String, Object>>> getDenormalizedData() {
            return null;
        }

        @Override
        public Map<String, List<Entity>> getContainerData() {
            return null;
        }

        @Override
        public void hollowOut() {
            // override super implementation with empty implementation
        }
    }

    @Override
    public CascadeResult safeDelete(Entity entity, String id, boolean cascade, boolean dryrun, boolean forced, boolean logViolations,
                                    Integer maxObjects, AccessibilityCheck access) {
        // TODO Auto-generated method stub
        return null;
    }

}
