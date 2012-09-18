package org.slc.sli.dal.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.slc.sli.dal.TenantContext;
import org.slc.sli.domain.CalculatedData;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * elasticsearch connector
 *
 */
public class ElasticSearchRepository extends SimpleEntityRepository {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleEntityRepository.class);

    private String esUri;

    private RestTemplate searchTemplate;

    private String esUsername;

    private String esPassword;

    // transport client is used for a query builder. The actual connection is over http.
    private Client esClient;

    @Autowired
    public ElasticSearchRepository(@Value("${sli.search.url}") String esUrl) {
        esClient = new TransportClient();
    }

    private Client getClient() {
        return esClient;
    }

    @Override
    public Iterable<Entity> findAll(String collectionName, NeutralQuery neutralQuery) {

        // send an elasticsearch REST query
        String query = Converter.getQuery(getClient(), neutralQuery, TenantContext.getTenantId()).toString();
        HttpEntity<String> response = sendRESTQuery(query);

        // convert the response to search hits
        return Converter.toEntityCol(response);
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

        //Basic Authentication when username and password are provided
        if (esUsername != null && esPassword != null) {
            headers.set("Authorization", "Basic " + Base64.encodeBase64String((esUsername + ":" + esPassword).getBytes()));
        }
        HttpEntity<String> entity = new HttpEntity<String>(query, headers);
        HttpEntity<String> response = null;

        // make the REST call
        try {
            response = searchTemplate.exchange(esUri, method, entity, String.class, TenantContext.getTenantId().toLowerCase());
        } catch (RestClientException rce) {
            LOG.error("Error sending elastic search request!", rce);
        }
        return response;
    }


    /**
     * Converter SLI to/from ES
     *
     * @author agrebneva
     *
     */
    public static class Converter {

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
                LOG.info("Hits returned from elasticsearch: " + hitsNode);

                // create a search hit entity object for each hit
                for (int i = 0; i < hitsNode.size(); i++) {
                    SearchHitEntity hit = convertJsonToSearchHitEntity(hitsNode.get(i));
                    if (hit != null) {
                        hits.add(hit);
                    }
                }
            } catch (Exception e) {
                LOG.error("Error converting search response to entities", e);
            }
            return hits;
        }

        /**
         * Converts a json response to a search hit entity
         */
        static SearchHitEntity convertJsonToSearchHitEntity(JsonNode hitNode) {

            try {

                // read the values from the json
                ObjectMapper mapper = new ObjectMapper();
                String id = hitNode.get("_id").getTextValue();
                String type = hitNode.get("_type").getTextValue();
                JsonNode bodyNode = hitNode.get("fields").get("body");
                Map<String, Object>body = mapper.readValue(bodyNode, new TypeReference<Map<String, Object>>() {});
                JsonNode metaDataNode = hitNode.get("fields").get("metaData");
                Map<String, Object>metaData = mapper.readValue(metaDataNode, new TypeReference<Map<String, Object>>() {});

                // create a return the search hit entity
                return new SearchHitEntity(id, type, body, metaData);

            } catch (Exception e) {
                LOG.error("Error converting search json response to search hit entity", e);
                return null;
            }
        }

        /**
         * Build elasticsearch query
         *
         * @param client
         * @param query
         * @return
         */
        static SearchRequestBuilder getQuery(Client client, NeutralQuery query, String tenantId) {

            SearchRequestBuilder srb = client.prepareSearch(tenantId.toLowerCase()).setSearchType(
                    SearchType.DFS_QUERY_THEN_FETCH);
            String queryString = null;
            BoolQueryBuilder bqb = QueryBuilders.boolQuery();
            BoolFilterBuilder bfb = FilterBuilders.boolFilter(), inFilter;

            // set query criteria
            for (NeutralCriteria s : query.getCriteria()) {
                if ("query".equals(s.getKey())) {
                    queryString = ((String) s.getValue()).trim();
                    queryString = "+" + StringUtils.join(queryString.split(" "), "* +") + "*";
                    bqb.must(new QueryStringQueryBuilder(queryString.toLowerCase()));
                } else {
                    bfb.must(FilterBuilders.termsFilter(s.getKey(), getTermTokens(s.getValue())));
                }
            }

            // set context metadata filter
            if (!query.getOrQueries().isEmpty()) {
                for (NeutralQuery q : query.getOrQueries()) {
                    if (q.getCriteria().size() == 1) {
                        NeutralCriteria s = q.getCriteria().get(0);
                        bfb.should(FilterBuilders.termsFilter(s.getKey(), getTermTokens(s.getValue())));
                    } else if (q.getCriteria().size() > 1) {
                        inFilter = FilterBuilders.boolFilter();
                        for (NeutralCriteria s : q.getCriteria()) {
                            inFilter.must(FilterBuilders.termsFilter(s.getKey(), getTermTokens(s.getValue())));
                        }
                        bfb.should(inFilter);
                    }
                }
            }
            srb.setQuery(bqb).setFilter(bfb).addFields("id", "body", "metaData").setFrom(query.getOffset())
                    .setSize(query.getLimit());
            return srb;
        }

        @SuppressWarnings("unchecked")
        private static String[] getTermTokens(Object value) {
            return (value instanceof List) ? ((List<String>) value).toArray(new String[0]) : ((String) value)
                    .split(",");
        }
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
    private static final class SearchHitEntity implements Entity {
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
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public CalculatedData<String> getCalculatedValues() {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
